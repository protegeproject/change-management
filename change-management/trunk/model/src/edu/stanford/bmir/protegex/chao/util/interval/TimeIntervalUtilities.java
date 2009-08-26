package edu.stanford.bmir.protegex.chao.util.interval;

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

public class TimeIntervalUtilities {
    private static ProjectAdapter projectListener = new ProjectAdapter() {
        public void projectClosed(ProjectEvent event) {
            Project changesProject = (Project) event.getSource();
            KnowledgeBase changesKb = changesProject.getKnowledgeBase();
            sortedChangesMap.remove(changesKb);
            changesProject.removeProjectListener(projectListener);
        }
    };
    
    private static Map<KnowledgeBase, TreeMap<Time, Change>> sortedChangesMap = new HashMap<KnowledgeBase, TreeMap<Time, Change>>();
    
    public static void initialize(KnowledgeBase changesKb) {
        if (sortedChangesMap.get(changesKb)  == null) {
            changesKb.getProject().addProjectListener(projectListener);
            sortedChangesMap.put(changesKb, (new GetTopLevelChangesTreeMapJob(changesKb)).execute());
        }
    }
   
    public static Collection<Change> getTopLevelChangesBefore(KnowledgeBase changesKb, Date d) {
        initialize(changesKb);
        TreeMap<Time, Change> changeMap = sortedChangesMap.get(changesKb);
        return Collections.unmodifiableCollection(changeMap.headMap(new Time(d)).values());
    }
    
    public static Collection<Change> getTopLevelChangesAfter(KnowledgeBase changesKb, Date d) {
        initialize(changesKb);
        TreeMap<Time, Change> changeMap = sortedChangesMap.get(changesKb);
        return Collections.unmodifiableCollection(changeMap.tailMap(new Time(d)).values());
    }

    public static Collection<Change> getTopLevelChanges(KnowledgeBase changesKb, Date start, Date end) {
        initialize(changesKb);
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
}
