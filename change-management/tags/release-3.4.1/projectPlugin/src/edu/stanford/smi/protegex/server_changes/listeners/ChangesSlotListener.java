package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.smi.protege.event.SlotAdapter;
import edu.stanford.smi.protege.event.SlotEvent;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.server_changes.PostProcessorManager;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;


public class ChangesSlotListener extends SlotAdapter{
    private PostProcessorManager changes_db;
    private ChangeFactory factory;

    public ChangesSlotListener(KnowledgeBase kb) {
        changes_db = ChangesProject.getPostProcessorManager(kb);
        factory = new ChangeFactory(changes_db.getChangesKb());
    }

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#templateSlotClsAdded(edu.stanford.smi.protege.event.SlotEvent)
	 */
	@Override
	public void templateSlotClsAdded(SlotEvent event) {
		if (event.getArgument() instanceof Cls) {
			Cls theCls = event.getCls();
            Slot theSlot = event.getSlot();

			StringBuffer context = new StringBuffer();
			context.append("Added template slot: ");
			context.append(theSlot.getBrowserText());
			context.append(" to: ");
			context.append(theCls.getBrowserText());

            ServerChangesUtil.createChangeWithSlot(changes_db, factory.createTemplateSlot_Added(null), theCls, context.toString(), theSlot);
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#templateSlotClsRemoved(edu.stanford.smi.protege.event.SlotEvent)
	 */
	@Override
	public void templateSlotClsRemoved(SlotEvent event) {
		if (event.getArgument() instanceof Cls) {
			Cls theCls = event.getCls();
            Slot theSlot = event.getSlot();
            //String name = changes_db.getPossiblyDeletedBrowserText(theCls);
            String name = theCls.getName();
			StringBuffer context = new StringBuffer();
			context.append("Removed template slot: ");
			context.append(theSlot.getBrowserText());
			context.append(" from: ");
			context.append(name);

            ServerChangesUtil.createChangeWithSlot(changes_db, factory.createTemplateSlot_Removed(null), theCls, context.toString(), theSlot);
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSubslotAdded(edu.stanford.smi.protege.event.SlotEvent)
	 */
	@Override
	public void directSubslotAdded(SlotEvent event) {
		if (event.getArgument() instanceof Slot) {
			Slot eventSlot = (Slot) event.getArgument();
			String context = "Direct Subslot Added: " + eventSlot.getBrowserText();

            ServerChangesUtil.createChangeStd(changes_db, factory.createSubproperty_Added(null), eventSlot, context);
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSubslotRemoved(edu.stanford.smi.protege.event.SlotEvent)
	 */
	@Override
	public void directSubslotRemoved(SlotEvent event) {
		if (event.getArgument() instanceof Slot) {
			Slot eventSlot = (Slot) event.getArgument();
            //String name = changes_db.getPossiblyDeletedBrowserText(eventSlot);
			String name = event.getSubslot().getName(); //TT -maybe null pointer
			String context = "Direct Subslot Removed: " + name;

            ServerChangesUtil.createChangeStd(changes_db, factory.createSubproperty_Removed(null), eventSlot, context);
		}
	}


	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSuperslotAdded(edu.stanford.smi.protege.event.SlotEvent)
	 */
	@Override
	public void directSuperslotAdded(SlotEvent event) {
		if (event.getArgument() instanceof Slot) {
			Slot eventSlot = (Slot) event.getArgument();
			String context = "Direct Superslot Added: " + eventSlot.getBrowserText();

            ServerChangesUtil.createChangeStd(changes_db, factory.createSuperproperty_Added(null), eventSlot, context);
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.SlotListener#directSuperslotRemoved(edu.stanford.smi.protege.event.SlotEvent)
	 */
	@Override
	public void directSuperslotRemoved(SlotEvent event) {
		if (event.getArgument() instanceof Slot) {
			Slot eventSlot = (Slot) event.getArgument();
			String context = "Direct Superslot Removed: " + eventSlot.getBrowserText();

            ServerChangesUtil.createChangeStd(changes_db, factory.createSuperproperty_Removed(null), eventSlot, context);
		}
	}
}
