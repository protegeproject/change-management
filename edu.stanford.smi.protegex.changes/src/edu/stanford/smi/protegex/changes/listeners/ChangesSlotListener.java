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

import edu.stanford.smi.protege.event.SlotEvent;
import edu.stanford.smi.protege.event.SlotListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.changes.ChangeCreateUtil;
import edu.stanford.smi.protegex.changes.ChangesTab;

public class ChangesSlotListener implements SlotListener{

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#templateSlotClsAdded(edu.stanford.smi.protege.event.SlotEvent)
	 */
	public void templateSlotClsAdded(SlotEvent event) {
		if (event.getArgument() instanceof Cls) {
			Cls theCls = event.getCls();
		
			StringBuffer context = new StringBuffer();
			context.append("Added template slot: ");
			context.append(event.getSlot().getName());
			context.append(" to: ");
			context.append(theCls.getName());
			
			Instance changeInst = ChangeCreateUtil.createChange(
													ChangesTab.getChangesKB(),
													ChangeCreateUtil.CHANGETYPE_TEMPLATESLOT_ADDED,
													theCls.getName(), 
													context.toString(), 
													ChangeCreateUtil.CHANGE_LEVEL_INFO);
			
			ChangesTab.createChange(changeInst);
			
			// Create artificial transaction for create slot
			if (ChangesTab.getInCreateSlot() && ChangesTab.getIsInTransaction()) {
				ChangesTab.createTransactionChange(ChangesTab.TRANS_SIGNAL_TRANS_END);
				ChangesTab.setInCreateSlot(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#templateSlotClsRemoved(edu.stanford.smi.protege.event.SlotEvent)
	 */
	public void templateSlotClsRemoved(SlotEvent event) {
		if (event.getArgument() instanceof Cls) {
			Cls theCls = event.getCls();
			
			StringBuffer context = new StringBuffer();
			context.append("Removed template slot: ");
			context.append(event.getSlot().getName());
			context.append(" from: ");
			context.append(theCls.getName());
			
			Instance changeInst = ChangeCreateUtil.createChange(
													ChangesTab.getChangesKB(),
													ChangeCreateUtil.CHANGETYPE_TEMPLATESLOT_REMOVED,
													theCls.getName(), 
													context.toString(),
													ChangeCreateUtil.CHANGE_LEVEL_INFO);
			
			ChangesTab.createChange(changeInst);
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSubslotAdded(edu.stanford.smi.protege.event.SlotEvent)
	 */
	public void directSubslotAdded(SlotEvent event) {
		if (event.getArgument() instanceof Slot) {
			Slot eventSlot = (Slot) event.getArgument();
			String context = "Direct Subslot Added: " + eventSlot.getBrowserText();
			
			Instance changeInst = ChangeCreateUtil.createChange(
													ChangesTab.getChangesKB(),
													ChangeCreateUtil.CHANGETYPE_SUBSLOT_ADDED,
													eventSlot.getBrowserText(), 
													context, 
													ChangeCreateUtil.CHANGE_LEVEL_INFO);
			
			ChangesTab.createChange(changeInst);
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSubslotRemoved(edu.stanford.smi.protege.event.SlotEvent)
	 */
	public void directSubslotRemoved(SlotEvent event) {
		if (event.getArgument() instanceof Slot) {
			Slot eventSlot = (Slot) event.getArgument();
			String context = "Direct Subslot Removed: " + eventSlot.getBrowserText();
			
			Instance changeInst = ChangeCreateUtil.createChange(
													ChangesTab.getChangesKB(),
													ChangeCreateUtil.CHANGETYPE_SUBSLOT_REMOVED,
													eventSlot.getBrowserText(), 
													context, 
													ChangeCreateUtil.CHANGE_LEVEL_INFO);
			
			ChangesTab.createChange(changeInst);
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSubslotMoved(edu.stanford.smi.protege.event.SlotEvent)
	 */
	public void directSubslotMoved(SlotEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSuperslotAdded(edu.stanford.smi.protege.event.SlotEvent)
	 */
	public void directSuperslotAdded(SlotEvent event) {
		if (event.getArgument() instanceof Slot) {
			Slot eventSlot = (Slot) event.getArgument();
			String context = "Direct Superslot Added: " + eventSlot.getBrowserText();
			
			Instance changeInst = ChangeCreateUtil.createChange(
													ChangesTab.getChangesKB(),
													ChangeCreateUtil.CHANGETYPE_SUPERSLOT_ADDED,
													eventSlot.getBrowserText(), 
													context, 
													ChangeCreateUtil.CHANGE_LEVEL_INFO);
			
			ChangesTab.createChange(changeInst);
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSuperslotRemoved(edu.stanford.smi.protege.event.SlotEvent)
	 */
	public void directSuperslotRemoved(SlotEvent event) {
		if (event.getArgument() instanceof Slot) {
			Slot eventSlot = (Slot) event.getArgument();
			String context = "Direct Superslot Removed: " + eventSlot.getBrowserText();
			
			Instance changeInst = ChangeCreateUtil.createChange(
													ChangesTab.getChangesKB(),
													ChangeCreateUtil.CHANGETYPE_SUPERSLOT_REMOVED,
													eventSlot.getBrowserText(), 
													context, 
													ChangeCreateUtil.CHANGE_LEVEL_INFO);
			
			ChangesTab.createChange(changeInst);
		}
	}
}
