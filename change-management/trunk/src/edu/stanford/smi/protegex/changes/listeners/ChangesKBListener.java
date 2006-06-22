package edu.stanford.smi.protegex.changes.listeners;

import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.event.KnowledgeBaseListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.changes.ChangeCreateUtil;
import edu.stanford.smi.protegex.changes.ChangesTab;

public class ChangesKBListener implements KnowledgeBaseListener {

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void clsCreated(KnowledgeBaseEvent event) {
		
		Cls createdCls = event.getCls();
		String clsName = createdCls.getName();
		String context = "Created Class: " + clsName;
		
		// Create artifical transaction for create class
		if (!ChangesTab.getIsInTransaction()) {
			ChangesTab.createTransactionChange(ChangesTab.TRANS_SIGNAL_TRANS_BEGIN);
			ChangesTab.setInCreateClass(true);
		} 
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_CLASS_CREATED, 
												clsName, 
												context, 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		
		ChangesTab.createChange(changeInst);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void clsDeleted(KnowledgeBaseEvent event) {
		
		String oldName = event.getOldName();
		String deletedClsName = "";
		if (event.getArgument2() instanceof Cls) {
			Cls deletedCls = (Cls) event.getArgument2();
			deletedClsName = deletedCls.getName();
		} else {
			deletedClsName = oldName;
		}
		
		String context = "Deleted Class: " + deletedClsName;
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_CLASS_DELETED,
												deletedClsName, 
												context, 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		
		ChangesTab.createChange(changeInst);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#defaultClsMetaClsChanged(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void defaultClsMetaClsChanged(KnowledgeBaseEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#defaultFacetMetaClsChanged(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void defaultFacetMetaClsChanged(KnowledgeBaseEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#defaultSlotMetaClsChanged(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void defaultSlotMetaClsChanged(KnowledgeBaseEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#facetCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void facetCreated(KnowledgeBaseEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#facetDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void facetDeleted(KnowledgeBaseEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#frameNameChanged(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void frameNameChanged(KnowledgeBaseEvent event) {
		String oldName = event.getOldName();
		String newName = event.getFrame().getName();
		
		StringBuffer context = new StringBuffer();
		context.append("Name change from '");
		context.append(oldName);
		context.append("' to '");
		context.append(newName);
		context.append("'");
		
		Instance changeInst = ChangeCreateUtil.createNameChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_NAME_CHANGED,
												newName, 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO, 
												oldName, 
												newName);
	
		ChangesTab.createChange(changeInst);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#instanceCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void instanceCreated(KnowledgeBaseEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#instanceDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void instanceDeleted(KnowledgeBaseEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#slotCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void slotCreated(KnowledgeBaseEvent event) {
		Slot createdSlot = event.getSlot();
		String slotName = createdSlot.getName();

		// Create artifical transaction for create slot
		if (!ChangesTab.getIsInTransaction()) {
			ChangesTab.createTransactionChange(ChangesTab.TRANS_SIGNAL_TRANS_BEGIN);
			ChangesTab.setInCreateSlot(true);
		}
		
		String context = "Created Slot: " + slotName;
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_SLOT_CREATED,
												slotName, 
												context, 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		
		ChangesTab.createChange(changeInst);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#slotDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void slotDeleted(KnowledgeBaseEvent event) {
		
		String deletedSlotName = "";
		String oldName = event.getOldName();
		if (event.getArgument2() instanceof Slot) {
			Slot deletedSlot = (Slot)event.getArgument2();
			deletedSlotName = deletedSlot.getName();
		} else {
			deletedSlotName = oldName;
		}
		String context = "Deleted Slot: " + deletedSlotName;
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_SLOT_DELETED,
												deletedSlotName, 
												context, 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		
		ChangesTab.createChange(changeInst);
	}
}
