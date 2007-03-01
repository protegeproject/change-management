package edu.stanford.smi.protegex.server_changes.model;

import java.util.Comparator;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;

public class InstanceDateComparator implements Comparator<Instance> {
    
    public InstanceDateComparator(KnowledgeBase changes_kb) {
        ;
    }

    public int compare(Instance o1, Instance o2) {
        return Timestamp.getTimestamp(o1).compareTo(Timestamp.getTimestamp(o2));
    }
    

}
