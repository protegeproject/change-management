package edu.stanford.bmir.protegex.chao.util.interval;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import edu.stanford.bmir.protegex.chao.annotation.api.Annotation;
import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.server.framestore.ServerFrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.changes.ChangeProjectUtil;
import edu.stanford.smi.protegex.server_changes.model.AbstractChangeListener;

public class TimeIntervalCalculator {
    private static final ConcurrentMap<KnowledgeBase, Future<TimeIntervalCalculator>> kb2TimeIntervalCalculatorMap = new ConcurrentHashMap<KnowledgeBase, Future<TimeIntervalCalculator>>();

    private ProjectAdapter projectListener = new CleanupListener();

    private AbstractChangeListener changeListener;
    private KnowledgeBase changesKb;

    private TreeMap<SimpleTime, Change> sortedChangesMap = new TreeMap<SimpleTime, Change>();

    private TimeIntervalCalculator(KnowledgeBase changesKb) {
        this.changesKb = changesKb;

        long t0= System.currentTimeMillis();
        Log.getLogger().info("Started caching of top level changes at: " + new Date());

        sortedChangesMap = new GetTopLevelChangesTreeMapJob(changesKb).execute();

        Log.getLogger().info("Finished caching top level changes in " + (System.currentTimeMillis() - t0)/1000 + " seconds.");

        changesKb.getProject().addProjectListener(projectListener);

        changeListener = new UpdateChangesListener();
        changesKb.addFrameListener(changeListener);
        if (changesKb.getProject().isMultiUserServer()) {
            ServerFrameStore.requestEventDispatch(changesKb);
        }
    }

    /**
     * Gets the time interval calculator for a changesKb.  
     * 
     * This routine is potentially very expensive.  The caller cannot be holding
     * the changesKb lock.
     * 
     * @param changesKb
     * @return
     */
    public static TimeIntervalCalculator get(final KnowledgeBase changesKb) {
        return get(changesKb, true);
    }

    private static TimeIntervalCalculator get(final KnowledgeBase changesKb, final boolean createTimeIntervalCalculatorIfNotAlreadyCalculated) {
        Future<TimeIntervalCalculator> future = kb2TimeIntervalCalculatorMap.get(changesKb);
        if (future == null) {
            final Callable<TimeIntervalCalculator> callable = new Callable<TimeIntervalCalculator>() {
                public TimeIntervalCalculator call() throws ExecutionException, InterruptedException {
                    return new TimeIntervalCalculator(changesKb);
                }
            };
            final FutureTask<TimeIntervalCalculator> futureTask = new FutureTask<TimeIntervalCalculator>(callable);
            future = kb2TimeIntervalCalculatorMap.putIfAbsent(changesKb, futureTask);
            if (future == null && createTimeIntervalCalculatorIfNotAlreadyCalculated) {
                future = futureTask;
                futureTask.run();
            }
            if (future == null && !createTimeIntervalCalculatorIfNotAlreadyCalculated){
                return null;
            }
        }
        try {
            return future.get();
        } catch (Exception e) {
            kb2TimeIntervalCalculatorMap.remove(changesKb, future);
            throw new RuntimeException(e);
        }
    }

    public Collection<Change> getTopLevelChanges() {
        synchronized (changesKb) {
            return new ArrayList<Change>(sortedChangesMap.values());
        }
    }

    public Collection<Change> getTopLevelChangesAfter(Date d) {
        synchronized (changesKb) {
            return new ArrayList<Change>(sortedChangesMap.headMap(new SimpleTime(d)).values());
        }
    }

    public Collection<Change> getTopLevelChangesBefore(Date d) {
        synchronized (changesKb) {
            return new ArrayList<Change>(sortedChangesMap.tailMap(new SimpleTime(d)).values());
        }
    }

    public Collection<Change> getTopLevelChanges(Date start, Date end) {
        synchronized (changesKb) {
            return new ArrayList<Change>(sortedChangesMap.subMap(new SimpleTime(end), new SimpleTime(start)).values());
        }
    }

    public void dispose() {
        synchronized (TimeIntervalCalculator.class) {
            kb2TimeIntervalCalculatorMap.remove(changesKb);
        }
        changesKb.getProject().removeProjectListener(projectListener);
        changesKb.removeFrameListener(changeListener);
    }

    private static class CleanupListener extends ProjectAdapter {

        @Override
        public void projectClosed(ProjectEvent event) {
            Project changesProject = (Project) event.getSource();
            KnowledgeBase changesKb = changesProject.getKnowledgeBase();
            TimeIntervalCalculator t = get(changesKb, false);
            if (t != null) {
                t.dispose();
            }
        }
    }

    private class UpdateChangesListener extends AbstractChangeListener {
        private Slot partOfCompositeChangeSlot;

        public UpdateChangesListener() {
            super(changesKb);
            partOfCompositeChangeSlot = new ChangeFactory(changesKb).getPartOfCompositeChangeSlot();
        }

        @Override
        public void addChange(Change change) {
            if (!ChangeProjectUtil.isRoot(change) && change.getPartOfCompositeChange() == null
                    && change.getTimestamp().hasDate()) {
                sortedChangesMap.put(new SimpleTime(change.getTimestamp()), change);
            }
        }

        @Override
        public void modifyChange(Change change, Slot slot, List oldValues) {
            if (slot.equals(partOfCompositeChangeSlot) &&
                    change.hasPartOfCompositeChange()) {
                sortedChangesMap.remove(new SimpleTime(change.getTimestamp()));
            }
            else if (slot.equals(partOfCompositeChangeSlot)) {
                sortedChangesMap.put(new SimpleTime(change.getTimestamp()), change);
            }
        }

        @Override
        public void addAnnotation(Annotation annotation) {

        }
    }
}
