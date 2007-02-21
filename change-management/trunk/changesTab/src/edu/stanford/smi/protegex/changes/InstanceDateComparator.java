package edu.stanford.smi.protegex.changes;

import java.util.Comparator;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;

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
			
			String time1 = ChangeCreateUtil.getCreated(changeKb, inst1);
			String time2 = ChangeCreateUtil.getCreated(changeKb, inst2);
					
			if (time2 == null) {
				value = 0;
			} else if (time1 == null) {
				value = 1;
			} else {
				value = time1.compareTo(time2);
			}
		}
		
		return value;
	}
}
