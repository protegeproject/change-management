package edu.stanford.bmir.protegex.chao.util;

import java.util.Comparator;

import edu.stanford.bmir.protegex.chao.annotation.api.Annotation;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Timestamp;
import edu.stanford.smi.protege.model.KnowledgeBase;

public class NoteDateComparator implements Comparator<Annotation> {

    public NoteDateComparator(KnowledgeBase changes_kb) {
        ;
    }

    public int compare(Annotation o1, Annotation o2) {
    	if (o1 == null) {
    		return -1;
    	}

    	if (o2 == null) {
    		return 1;
    	}

    	Annotation c1 = o1;
    	Annotation c2 = o2;

        //fishy
        Timestamp t1 = c1.getCreated();
        Timestamp t2 = c2.getCreated();

        if (t1 == null) {
        	return -1;
        }

        return t1.compareTimestamp(t2);
    }
}