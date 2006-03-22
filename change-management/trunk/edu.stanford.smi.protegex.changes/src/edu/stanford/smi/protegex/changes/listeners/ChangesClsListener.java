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

import edu.stanford.smi.protege.event.ClsEvent;
import edu.stanford.smi.protege.event.ClsListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.changes.ChangeCreateUtil;
import edu.stanford.smi.protegex.changes.ChangesTab;

public class ChangesClsListener implements ClsListener{

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directInstanceAdded(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directInstanceAdded(ClsEvent event) {
		Instance addedInst = event.getInstance();
		Cls clsOfInst = event.getCls();
		
		StringBuffer context = new StringBuffer();
		context.append("Added Instance: ");
		context.append(addedInst.getName());
		context.append(" (instance of ");
		context.append(clsOfInst.getName());
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_INSTANCE_ADDED, 
												clsOfInst.getName(), 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		
		ChangesTab.createChange(changeInst);
		
		// Create artificial transaction for create slot
		if (ChangesTab.getInCreateSlot() && ChangesTab.getIsInTransaction()) {
			ChangesTab.createTransactionChange(ChangesTab.TRANS_SIGNAL_TRANS_END);
			ChangesTab.setInCreateSlot(false);
		}
		
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directInstanceRemoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directInstanceRemoved(ClsEvent event) {
		Instance removedInst = event.getInstance();
		Cls clsOfInst = event.getCls();
		
		StringBuffer context = new StringBuffer();
		context.append("Removed Instance: ");
		context.append(removedInst.getName());
		context.append(" (instance of ");
		context.append(clsOfInst.getName());
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_INSTANCE_REMOVED, 
												clsOfInst.getName(), 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
		
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directSubclassAdded(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directSubclassAdded(ClsEvent event) {
		Cls subClass = event.getSubclass();
		Cls superClass = event.getCls();
		
		StringBuffer context = new StringBuffer();
		context.append("Added subclass: ");
		context.append(subClass.getName());
		context.append(" (subclass of ");
		context.append(superClass.getName());
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_SUBCLASS_ADDED, 
												subClass.getName(), 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		
		ChangesTab.createChange(changeInst);
		
		// Create artificial transaction for create class
		if (ChangesTab.getIsInTransaction() && ChangesTab.getInCreateClass()) {
			ChangesTab.createTransactionChange(ChangesTab.TRANS_SIGNAL_TRANS_END);
			ChangesTab.setInCreateClass(false);
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directSubclassMoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directSubclassMoved(ClsEvent event) {
		// Method is not used/called
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directSubclassRemoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directSubclassRemoved(ClsEvent event) {
		Cls subClass = event.getSubclass();
		Cls superClass = event.getCls();
		
		StringBuffer context = new StringBuffer();
		context.append("Removed subclass: ");
		context.append(subClass.getName());
		context.append(" (subclass of ");
		context.append(superClass.getName());
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_SUBCLASS_REMOVED, 
												subClass.getName(), 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		
		ChangesTab.createChange(changeInst);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directSuperclassAdded(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directSuperclassAdded(ClsEvent event) {
		Cls subClass = event.getSubclass();
		Cls superClass = event.getCls();
	
		StringBuffer context = new StringBuffer();
		context.append("Added superclass: ");
		context.append(subClass.getName());
		context.append(" (subclass of ");
		context.append( superClass.getName());
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_SUPERCLASS_ADDED,
												subClass.getName(), 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		
		ChangesTab.createChange(changeInst);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directSuperclassRemoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directSuperclassRemoved(ClsEvent event) {
		Cls subClass = event.getSubclass();
		Cls superClass = event.getCls();
	
		StringBuffer context = new StringBuffer();
		context.append("Removed superclass: ");
		context.append(subClass.getName());
		context.append(" (subclass of ");
		context.append(superClass.getName());
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_SUPERCLASS_REMOVED, 
												subClass.getName(), 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		
		ChangesTab.createChange(changeInst);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#templateFacetAdded(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void templateFacetAdded(ClsEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#templateFacetRemoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void templateFacetRemoved(ClsEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#templateFacetValueChanged(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void templateFacetValueChanged(ClsEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#templateSlotAdded(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void templateSlotAdded(ClsEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#templateSlotRemoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void templateSlotRemoved(ClsEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#templateSlotValueChanged(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void templateSlotValueChanged(ClsEvent event) {
	}
}
