package edu.stanford.bmir.protegex.chao.util.interval;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.stanford.bmir.protegex.chao.annotation.api.Annotation;
import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.server.framestore.ServerFrameStore;
import edu.stanford.smi.protegex.changes.ChangeProjectUtil;
import edu.stanford.smi.protegex.server_changes.model.AbstractChangeListener;

public class TimeIntervalCalculator {
    private static ProjectAdapter projectListener = new CleanupListener();
    private static Map<KnowledgeBase, TimeIntervalCalculator> instanceMap = new HashMap<KnowledgeBase, TimeIntervalCalculator>();

    private KnowledgeBase changesKb;
    private AbstractChangeListener changeListener;
    private TreeMap<SimpleTime, Change> sortedChangesMap = new TreeMap<SimpleTime, Change>();
    
    private TimeIntervalCalculator(KnowledgeBase changesKb) {
        this.changesKb = changesKb;
        
        sortedChangesMap = new GetTopLevelChangesTreeMapJob(changesKb).execute();

        changesKb.getProject().addProjectListener(projectListener); 

        changeListener = new UpdateChangesListener();
        changesKb.addFrameListener(changeListener);
        if (changesKb.getProject().isMultiUserServer()) {
            ServerFrameStore.requestEventDispatch(changesKb);
        }
    }
    
    public static TimeIntervalCalculator get(KnowledgeBase changesKb) {
        TimeIntervalCalculator t = instanceMap.get(changesKb);
        if (t == null) {
            t = new TimeIntervalCalculator(changesKb);
            instanceMap.put(changesKb, t);
        }
        return t;
    }
   
    public Collection<Change> getTopLevelChangesBefore(Date d) {
        return Collections.unmodifiableCollection(sortedChangesMap.headMap(new SimpleTime(d)).values());
    }
    
    public Collection<Change> getTopLevelChangesAfter(Date d) {
        return Collections.unmodifiableCollection(sortedChangesMap.tailMap(new SimpleTime(d)).values());
    }

    public Collection<Change> getTopLevelChanges(Date start, Date end) {
        List<Change> changes = new ArrayList<Change>();
        for (Change change : sortedChangesMap.tailMap(new SimpleTime(start)).values()) {
            if (change.getTimestamp().getDateParsed().compareTo(end) >= 0) {
                break;
            }
            changes.add(change);
        }
        return changes;
    }
    
    public void dispose() {
        changesKb.getProject().removeProjectListener(projectListener);
        changesKb.removeFrameListener(changeListener);
        
        instanceMap.remove(changesKb);
    }
    
    private static class CleanupListener extends ProjectAdapter {
        
        public void projectClosed(ProjectEvent event) {
            Project changesProject = (Project) event.getSource();
            KnowledgeBase changesKb = changesProject.getKnowledgeBase();
            TimeIntervalCalculator  t = instanceMap.get(changesKb);
            if (t != null) {
                t.dispose();
            }
        }
    }
    
    private class UpdateChangesListener extends AbstractChangeListener {
        
        public UpdateChangesListener() {
            super(changesKb);
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
            if (slot.equals(change.getPartOfCompositeChange()) &&
                    change.hasPartOfCompositeChange()) {
                sortedChangesMap.remove(change.getTimestamp());
            }
            else if (slot.equals(change.getPartOfCompositeChange())) {
                sortedChangesMap.put(new SimpleTime(change.getTimestamp()), change);
            }
        }
        
        @Override
        public void addAnnotation(Annotation annotation) {

        }
    }
}
