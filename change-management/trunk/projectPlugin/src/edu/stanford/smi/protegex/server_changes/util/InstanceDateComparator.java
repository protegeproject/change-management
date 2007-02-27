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
		
		int value = 0;
				
		if (obj1 instanceof Instance && obj2 instanceof Instance) {
			Instance inst1 = (Instance) obj1;
			Instance inst2 = (Instance) obj2;
			
			String time1 = Model.getCreated(inst1);
			String time2 = Model.getCreated(inst2);
					
			if (time2 == null) {
				value = 0;
			} else if (time1 == null) {
				value = 1;
			} else {
				Date d1 = Model.parseDate(time1);
                Date d2 = Model.parseDate(time2);
                d1.compareTo(d2);
			}
		}
		
		return value;
	}
}
