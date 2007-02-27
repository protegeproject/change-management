package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.smi.protege.event.SlotEvent;
import edu.stanford.smi.protege.event.SlotListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.Model;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;


public class ChangesSlotListener implements SlotListener{
    private KnowledgeBase kb;
    private KnowledgeBase changesKb;
    
    public ChangesSlotListener(KnowledgeBase kb) {
        this.kb = kb;
        changesKb = ChangesProject.getChangesKB(kb);
    }
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
			context.append(theCls.getBrowserText());
			
			Instance changeInst = ServerChangesUtil.createChange(kb,
													changesKb,
													Model.CHANGETYPE_TEMPLATESLOT_ADDED,
													theCls.getName(), 
													context.toString(), 
													Model.CHANGE_LEVEL_INFO);
		
			ChangesProject.createChange(kb, changesKb, changeInst);
			// Create artificial transaction for create slot
			if (ChangesProject.getInCreateSlot(kb) && ChangesProject.getIsInTransaction(kb)) {
				ChangesProject.createTransactionChange(kb, ChangesProject.TRANS_SIGNAL_TRANS_END);
				ChangesProject.setInCreateSlot(kb, false);
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
			context.append(theCls.getBrowserText());
			
			Instance changeInst = ServerChangesUtil.createChange(kb,
													changesKb,
													Model.CHANGETYPE_TEMPLATESLOT_REMOVED,
													theCls.getName(), 
													context.toString(),
													Model.CHANGE_LEVEL_INFO);
			ChangesProject.createChange(kb, changesKb, changeInst);
	
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSubslotAdded(edu.stanford.smi.protege.event.SlotEvent)
	 */
	public void directSubslotAdded(SlotEvent event) {
		if (event.getArgument() instanceof Slot) {
			Slot eventSlot = (Slot) event.getArgument();
			String context = "Direct Subslot Added: " + eventSlot.getBrowserText();
			
			Instance changeInst = ServerChangesUtil.createChange(kb,
                                                                             changesKb,
                                                                             Model.CHANGETYPE_SUBSLOT_ADDED,
                                                                             eventSlot.getName(), 
                                                                             context, 
                                                                             Model.CHANGE_LEVEL_INFO);
			ChangesProject.createChange(kb, changesKb, changeInst);
			
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSubslotRemoved(edu.stanford.smi.protege.event.SlotEvent)
	 */
	public void directSubslotRemoved(SlotEvent event) {
		if (event.getArgument() instanceof Slot) {
			Slot eventSlot = (Slot) event.getArgument();
			String context = "Direct Subslot Removed: " + eventSlot.getBrowserText();
			
			Instance changeInst = ServerChangesUtil.createChange(kb,
													changesKb,
													Model.CHANGETYPE_SUBSLOT_REMOVED,
													eventSlot.getName(), 
													context, 
													Model.CHANGE_LEVEL_INFO);
			ChangesProject.createChange(kb, changesKb, changeInst);
		
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
			
			Instance changeInst = ServerChangesUtil.createChange(kb,
                                                                             changesKb,
                                                                             Model.CHANGETYPE_SUPERSLOT_ADDED,
                                                                             eventSlot.getName(), 
                                                                             context, 
                                                                             Model.CHANGE_LEVEL_INFO);
			ChangesProject.createChange(kb, changesKb, changeInst);
			
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSuperslotRemoved(edu.stanford.smi.protege.event.SlotEvent)
	 */
	public void directSuperslotRemoved(SlotEvent event) {
		if (event.getArgument() instanceof Slot) {
			Slot eventSlot = (Slot) event.getArgument();
			String context = "Direct Superslot Removed: " + eventSlot.getBrowserText();
			
			Instance changeInst = ServerChangesUtil.createChange(kb,
                                                                             changesKb,
                                                                             Model.CHANGETYPE_SUPERSLOT_REMOVED,
                                                                             eventSlot.getName(), 
                                                                             context, 
                                                                             Model.CHANGE_LEVEL_INFO);
			
			ChangesProject.createChange(kb, changesKb, changeInst);
		}
	}
}
