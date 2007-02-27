package edu.stanford.smi.protegex.server_changes.util;

import java.util.Comparator;
import java.util.Date;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.server_changes.Model;

public class InstanceDateComparator implements Comparator{

	private KnowledgeBase changeKb;
	
	public InstanceDateComparator(KnowledgeBase cKb) {
		changeKb = cKb;
	}
	
	public int compare(Object obj1, Object obj2) {
	    if (obj1 instanceof Instance && obj2 instanceof Instance) {
	        Instance inst1 = (Instance) obj1;
	        Instance inst2 = (Instance) obj2;

	        boolean isRoot1 = (Model.getType(inst1).equals(Model.CHANGE_LEVEL_ROOT));
	        boolean isRoot2 = (Model.getType(inst2).equals(Model.CHANGE_LEVEL_ROOT));

	        if (isRoot1 && isRoot2) return 0;
	        else if (isRoot1) return -1;
	        else if (isRoot2) return +1;

	        String time1 = Model.getCreated(inst1);
	        String time2 = Model.getCreated(inst2);

	        if (time1 == null && time2 == null) {
	            return 0;
	        } 
	        else if (time1 == null) {
	            return -1;
	        } 
	        else if (time2 == null) {
	            return +1;
	        }
	        Date d1 = Model.parseDate(time1);
	        Date d2 = Model.parseDate(time2);
	        return d1.compareTo(d2);
	    }
        else {
            return 0;
        }
	}
}
