package edu.stanford.smi.protegex.server_changes.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.event.KnowledgeBaseListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.Model;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

public class ChangesKBListener implements KnowledgeBaseListener {
    private final static Logger log = Log.getLogger(ChangesKBListener.class);
    private KnowledgeBase kb;
    private KnowledgeBase changesKb;
    
    public ChangesKBListener(KnowledgeBase kb) {
        this.kb = kb;
        changesKb = ChangesProject.getChangesKB(kb);
    }
	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
    public void clsCreated(KnowledgeBaseEvent event) {
        Cls createdCls = event.getCls();
        String clsName = createdCls.getName();
        String context = "Created Class: " + clsName;

        // Create artifical transaction for create class
        if (!ChangesProject.getIsInTransaction(kb)) {
            ChangesProject.createTransactionChange(kb, ChangesProject.TRANS_SIGNAL_TRANS_BEGIN);
            ChangesProject.setInCreateClass(kb, true);
        } 

        Instance changeInst = ServerChangesUtil.createChange(kb,
                                                             changesKb,
                                                             Model.CHANGETYPE_CLASS_CREATED, 
                                                             clsName, 
                                                             context, 
                                                             Model.CHANGE_LEVEL_INFO);

        ChangesProject.createChange(kb, changesKb, changeInst);
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
            Instance changeInst = ServerChangesUtil.createChange(kb,
                                                                 changesKb,
                                                                 Model.CHANGETYPE_CLASS_DELETED,
                                                                 deletedClsName, 
                                                                 context, 
                                                                 Model.CHANGE_LEVEL_INFO);
            ChangesProject.createChange(kb, changesKb, changeInst);
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
            
	    // Update all instances in the change ontology with the oldName to newName
	    ServerChangesUtil.updateChangeDbAfterNameChange(changesKb, oldName,newName);


	    //Don't create a name change if it is a rename right after a creation
	    if (changesDb.createChangeNameContains(oldName)&& needsNameChange(oldName)) {


	        //Instance cChange = (Instance) ChangesProject.createChangeName.get(oldName);
	        //ServerChangesUtil.setInstApplyTo(changesKb, cChange, newName);
	        //updateChangeInstances(cChange, oldName, newName);
	        changesDb.removeChangeName(oldName);

	    }
	    else
	    {		
	        StringBuffer context = new StringBuffer();
	        context.append("Name change from '");
	        context.append(oldName);
	        context.append("' to '");
	        context.append(newName);
	        context.append("'");


	        Instance changeInst = ServerChangesUtil.createNameChange(kb,
	                                                                 changesKb,
	                                                                 Model.CHANGETYPE_NAME_CHANGED,
	                                                                 newName, 
	                                                                 context.toString(), 
	                                                                 Model.CHANGE_LEVEL_INFO, 
	                                                                 oldName, 
	                                                                 newName);

	        ChangesProject.createChange(kb, changesKb, changeInst);
	    }
	}
	
	private boolean needsNameChange(String name) {
	    boolean needsNameChange = false;

	    if (name != null) {
	        int index = name.lastIndexOf('_');
	        String possibleIntegerString = name.substring(index + 1);
	        try {
	            Integer.parseInt(possibleIntegerString);
	            needsNameChange = true;
	        } catch (Exception e) {
	        }
	    }
	    return needsNameChange;
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
		if (!ChangesProject.getIsInTransaction(kb)) {
			ChangesProject.createTransactionChange(kb, ChangesProject.TRANS_SIGNAL_TRANS_BEGIN);
			ChangesProject.setInCreateSlot(kb, true);
		}
		
		String context = "Created Slot: " + slotName;
		Instance changeInst = ServerChangesUtil.createChange(kb,
												changesKb,
												Model.CHANGETYPE_SLOT_CREATED,
												slotName, 
												context, 
												Model.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(kb, changesKb, changeInst);
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
		Instance changeInst = ServerChangesUtil.createChange(kb,
												changesKb,
												Model.CHANGETYPE_SLOT_DELETED,
												deletedSlotName, 
												context, 
												Model.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(kb, changesKb, changeInst);
	
	}
}
