package edu.stanford.smi.protegex.server_changes.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.event.KnowledgeBaseListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.TransactionState;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Class_Created;
import edu.stanford.smi.protegex.server_changes.model.generated.Class_Deleted;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;

public class ChangesKBListener implements KnowledgeBaseListener {
    private final static Logger log = Log.getLogger(ChangesKBListener.class);
    private KnowledgeBase kb;
    private KnowledgeBase changesKb;
    private ChangesDb changes_db;
    
    public ChangesKBListener(KnowledgeBase kb) {
        this.kb = kb;
        changesKb = ChangesProject.getChangesKB(kb);
        changes_db = ChangesProject.getChangesDb(kb);   
    }
	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
    public void clsCreated(KnowledgeBaseEvent event) {
        Cls createdCls = event.getCls();
        String clsName = createdCls.getName();
        String context = "Created Class: " + clsName;

        // Create artifical transaction for create class
        changes_db.getTransactionState().beginTransaction(context);
        changes_db.setInCreateClass(true);
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(clsName, true);
        applyTo.setCurrentName(clsName);
        
        Class_Created change = (Class_Created) changes_db.createChange(ChangeCls.Class_Created);
        change.setCreationName(clsName);
        changes_db.finalizeChange(change, applyTo, context.toString(), ChangeModel.CHANGE_LEVEL_INFO);
    }

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void clsDeleted(KnowledgeBaseEvent event) {
            if (log.isLoggable(Level.FINE)) {
                log.fine("In class deleted listener");
            }
            String oldName = event.getOldName();
            String deletedClsName = "";
            if (event.getArgument2() instanceof Cls) {
                Cls deletedCls = (Cls) event.getArgument2();
                deletedClsName = deletedCls.getName();
            } else {
                deletedClsName = oldName;
            }
		
            String context = "Deleted Class: " + deletedClsName;
            
            Ontology_Component applyTo = changes_db.getOntologyComponent(deletedClsName, true);
            
            Class_Deleted change = (Class_Deleted) changes_db.createChange(ChangeCls.Class_Deleted);
            change.setDeletionName(deletedClsName);
            changes_db.finalizeChange(change, applyTo, context.toString(), ChangeModel.CHANGE_LEVEL_INFO);
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
	    ChangesDb changesDb = ChangesProject.getChangesDb(kb);

	    StringBuffer context = new StringBuffer();
	    context.append("Name change from '");
	    context.append(oldName);
	    context.append("' to '");
	    context.append(newName);
	    context.append("'");
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(oldName, true);
        applyTo.setCurrentName(newName);
        
        Change change = changes_db.createChange(ChangeCls.Name_Changed);
        changes_db.finalizeChange(change, applyTo, context.toString(), ChangeModel.CHANGE_LEVEL_INFO);
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

        
        String context = "Created Slot: " + slotName;
        
		// Create artifical transaction for create slot
        TransactionState tstate = changes_db.getTransactionState();
        tstate.beginTransaction(context);
        changes_db.setInCreateSlot(true);
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(slotName, true);
        applyTo.setCurrentName(slotName);
        
        Change change = changes_db.createChange(ChangeCls.Slot_Created);
        changes_db.finalizeChange(change, applyTo, context.toString(), ChangeModel.CHANGE_LEVEL_INFO);
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
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(deletedSlotName, true);
        applyTo.setCurrentName(null);
        
        Change change = changes_db.createChange(ChangeCls.Slote_Deleted);
        changes_db.finalizeChange(change, applyTo, context.toString(), ChangeModel.CHANGE_LEVEL_INFO);
	}
}
