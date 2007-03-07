package edu.stanford.smi.protegex.server_changes.model;

import java.util.Comparator;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;

public class AnnotationCreationComparator implements Comparator<Instance> {

    public int compare(Instance arg0, Instance arg1) {
        Annotation a0 = (Annotation) arg0;
        Annotation a1 = (Annotation) arg1;
        return ((Timestamp) a0.getCreated()).compareTimestamp((Timestamp) a1.getCreated());
    }

}
