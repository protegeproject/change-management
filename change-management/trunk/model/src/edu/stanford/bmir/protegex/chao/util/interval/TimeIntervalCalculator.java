package edu.stanford.bmir.protegex.chao.util.interval;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.socket.RmiSocketFactory;
import edu.stanford.smi.protege.server.socket.SSLFactory;

public class TimeIntervalCalculator implements RemoteTimeIntervalCalculator {
    private static ProjectAdapter projectListener = new ProjectAdapter() {
        public void projectClosed(ProjectEvent event) {
            Project changesProject = (Project) event.getSource();
            KnowledgeBase changesKb = changesProject.getKnowledgeBase();
            sortedChangesMap.remove(changesKb);
            changesProject.removeProjectListener(projectListener);
        }
    };
    
    private static Map<KnowledgeBase, TreeMap<Time, Change>> sortedChangesMap = new HashMap<KnowledgeBase, TreeMap<Time, Change>>();
    private KnowledgeBase changesKb;
    
    public TimeIntervalCalculator(KnowledgeBase changesKb) {
        this.changesKb = changesKb;
        if (sortedChangesMap.get(changesKb)  == null) {
            changesKb.getProject().addProjectListener(projectListener);
            sortedChangesMap.put(changesKb, (new GetTopLevelChangesTreeMapJob(changesKb)).execute());
        }
    }
   
    public Collection<Change> getTopLevelChangesBefore(Date d) {
        TreeMap<Time, Change> changeMap = sortedChangesMap.get(changesKb);
        return Collections.unmodifiableCollection(changeMap.headMap(new Time(d)).values());
    }
    
    public Collection<Change> getTopLevelChangesAfter(Date d) {
        TreeMap<Time, Change> changeMap = sortedChangesMap.get(changesKb);
        return Collections.unmodifiableCollection(changeMap.tailMap(new Time(d)).values());
    }

    public Collection<Change> getTopLevelChanges(Date start, Date end) {
        TreeMap<Time, Change> changeMap = sortedChangesMap.get(changesKb);
        List<Change> changes = new ArrayList<Change>();
        Time endTime = new Time(end);
        for (Change change : changeMap.tailMap(new Time(start)).values()) {
            if (change.getTimestamp().getDateParsed().compareTo(end) >= 0) {
                break;
            }
            changes.add(change);
        }
        return changes;
    }
    
    public void dispose() {
        Project changesProject = changesKb.getProject();
        changesProject.removeProjectListener(projectListener);
        sortedChangesMap.remove(changesKb);
    }
}
