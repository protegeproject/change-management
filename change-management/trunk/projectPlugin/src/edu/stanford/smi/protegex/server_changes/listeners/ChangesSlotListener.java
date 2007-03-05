package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.smi.protege.event.SlotEvent;
import edu.stanford.smi.protege.event.SlotListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.TransactionState;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;


public class ChangesSlotListener implements SlotListener{
    private KnowledgeBase kb;
    private ChangesDb changes_db;
    private KnowledgeBase changesKb;
    
    public ChangesSlotListener(KnowledgeBase kb) {
        this.kb = kb;
        changes_db = ChangesProject.getChangesDb(kb);
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
            
            ServerChangesUtil.createChangeStd(changes_db, ChangeCls.TemplateSlot_Added, theCls.getName(), context.toString());
			// Create artificial transaction for create slot
            TransactionState tstate = changes_db.getTransactionState();
			if (changes_db.isInCreateSlot() && tstate.inTransaction()) {
                tstate.commitTransaction();
                changes_db.setInCreateSlot(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#templateSlotClsRemoved(edu.stanford.smi.protege.event.SlotEvent)
	 */
	public void templateSlotClsRemoved(SlotEvent event) {
		if (event.getArgument() instanceof Cls) {
			Cls theCls = event.getCls();
            String name = changes_db.getPossiblyDeletedFrameName(theCls);
			
			StringBuffer context = new StringBuffer();
			context.append("Removed template slot: ");
			context.append(event.getSlot().getName());
			context.append(" from: ");
			context.append(theCls.getBrowserText());
			
            ServerChangesUtil.createChangeStd(changes_db, ChangeCls.TemplateSlot_Removed, name, context.toString());
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSubslotAdded(edu.stanford.smi.protege.event.SlotEvent)
	 */
	public void directSubslotAdded(SlotEvent event) {
		if (event.getArgument() instanceof Slot) {
			Slot eventSlot = (Slot) event.getArgument();
			String context = "Direct Subslot Added: " + eventSlot.getBrowserText();
            
            ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Subslot_Added, eventSlot.getName(), context);
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSubslotRemoved(edu.stanford.smi.protege.event.SlotEvent)
	 */
	public void directSubslotRemoved(SlotEvent event) {
		if (event.getArgument() instanceof Slot) {
			Slot eventSlot = (Slot) event.getArgument();
            String name = changes_db.getPossiblyDeletedFrameName(eventSlot);
			String context = "Direct Subslot Removed: " + eventSlot.getBrowserText();
            
            ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Subslot_Removed, name, context);
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
            
            ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Superslot_Added, eventSlot.getName(), context);
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSuperslotRemoved(edu.stanford.smi.protege.event.SlotEvent)
	 */
	public void directSuperslotRemoved(SlotEvent event) {
		if (event.getArgument() instanceof Slot) {
			Slot eventSlot = (Slot) event.getArgument();
            String name = changes_db.getPossiblyDeletedBrowserText(eventSlot);
			String context = "Direct Superslot Removed: " + eventSlot.getBrowserText();
            
            ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Superslot_Removed, name, context);
		}
	}
}
