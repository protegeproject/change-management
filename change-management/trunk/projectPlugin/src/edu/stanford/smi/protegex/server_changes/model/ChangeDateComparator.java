package edu.stanford.smi.protegex.server_changes.model;

import java.util.Comparator;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;

public class ChangeDateComparator implements Comparator<Instance> {
    
    public ChangeDateComparator(KnowledgeBase changes_kb) {
        ;
    }

    public int compare(Instance o1, Instance o2) {
        Change c1 = (Change) o1;
        Change c2 = (Change) o2;
        return ((Timestamp) c1.getTimestamp()).compareTimestamp((Timestamp) c2.getTimestamp());
    }
}
