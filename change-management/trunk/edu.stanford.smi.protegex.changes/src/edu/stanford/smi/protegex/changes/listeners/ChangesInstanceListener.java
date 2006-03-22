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

package edu.stanford.smi.protegex.changes.listeners;

import edu.stanford.smi.protege.event.InstanceEvent;
import edu.stanford.smi.protege.event.InstanceListener;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.changes.ChangeCreateUtil;
import edu.stanford.smi.protegex.changes.ChangesTab;

public class ChangesInstanceListener implements InstanceListener{

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.InstanceListener#directTypeAdded(edu.stanford.smi.protege.event.InstanceEvent)
	 */
	public void directTypeAdded(InstanceEvent event) {
		
		String directType = event.getInstance().getDirectType().getBrowserText();
		String instName = event.getInstance().getBrowserText();
		
		StringBuffer context = new StringBuffer();
		context.append("Direct Type Added: ");
		context.append(directType);
		context.append(" (added to: ");
		context.append(instName);
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_DIRECTTYPE_ADDED, 
												directType, 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		
		ChangesTab.createChange(changeInst);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.InstanceListener#directTypeRemoved(edu.stanford.smi.protege.event.InstanceEvent)
	 */
	public void directTypeRemoved(InstanceEvent event) {
		String directType = event.getInstance().getDirectType().getBrowserText();
		String instName = event.getInstance().getBrowserText();
		
		StringBuffer context = new StringBuffer();
		context.append("Direct Type Removed: ");
		context.append(directType);
		context.append(" (removed from: ");
		context.append(instName);
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_DIRECTTYPE_REMOVED,
												directType, 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		
		ChangesTab.createChange(changeInst);
	}
}
