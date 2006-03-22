/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License");  you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is Protege-2000.
 *
 * The Initial Developer of the Original Code is Stanford University. Portions
 * created by Stanford University are Copyright (C) 2005.  All Rights Reserved.
 *
 * Protege was developed by Stanford Medical Informatics
 * (http://www.smi.stanford.edu) at the Stanford University School of Medicine
 * with support from the National Library of Medicine, the National Science
 * Foundation, and the Defense Advanced Research Projects Agency.  Current
 * information about Protege can be obtained at http://protege.stanford.edu.
 *
 */

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
			} else {
				value = time1.compareTo(time2);
			}
		}
		
		return value;
	}
}
