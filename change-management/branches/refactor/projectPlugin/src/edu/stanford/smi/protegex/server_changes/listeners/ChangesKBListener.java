package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.event.KnowledgeBaseListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

public class ChangesKBListener implements KnowledgeBaseListener {

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void clsCreated(KnowledgeBaseEvent event) {
		
		Cls createdCls = event.getCls();
		String clsName = createdCls.getBrowserText();
		String context = "Created Class: " + clsName;
		
		// Create artifical transaction for create class
		if (!ChangesProject.getIsInTransaction()) {
			ChangesProject.createTransactionChange(ChangesProject.TRANS_SIGNAL_TRANS_BEGIN);
			ChangesProject.setInCreateClass(true);
		} 
		
		Instance changeInst = ServerChangesUtil.createChange(
												ChangesProject.getChangesKB(),
												ServerChangesUtil.CHANGETYPE_CLASS_CREATED, 
												clsName, 
												context, 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		
		ChangesProject.createChange(changeInst);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void clsDeleted(KnowledgeBaseEvent event) {
		
		String oldName = event.getOldName();
		String deletedClsName = "";
		if (event.getArgument2() instanceof Cls) {
			Cls deletedCls = (Cls) event.getArgument2();
			deletedClsName = deletedCls.getBrowserText();
		} else {
			deletedClsName = oldName;
		}
		
		String context = "Deleted Class: " + deletedClsName;
		Instance changeInst = ServerChangesUtil.createChange(
												ChangesProject.getChangesKB(),
												ServerChangesUtil.CHANGETYPE_CLASS_DELETED,
												deletedClsName, 
												context, 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(changeInst);
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
		
		Instance changeInst = ServerChangesUtil.createNameChange(
												ChangesProject.getChangesKB(),
												ServerChangesUtil.CHANGETYPE_NAME_CHANGED,
												newName, 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO, 
												oldName, 
												newName);
	
		ChangesProject.createChange(changeInst);
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
		if (!ChangesProject.getIsInTransaction()) {
			ChangesProject.createTransactionChange(ChangesProject.TRANS_SIGNAL_TRANS_BEGIN);
			ChangesProject.setInCreateSlot(true);
		}
		
		String context = "Created Slot: " + slotName;
		Instance changeInst = ServerChangesUtil.createChange(
												ChangesProject.getChangesKB(),
												ServerChangesUtil.CHANGETYPE_SLOT_CREATED,
												slotName, 
												context, 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(changeInst);
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
		Instance changeInst = ServerChangesUtil.createChange(
												ChangesProject.getChangesKB(),
												ServerChangesUtil.CHANGETYPE_SLOT_DELETED,
												deletedSlotName, 
												context, 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(changeInst);
	
	}
}
