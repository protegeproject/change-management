package edu.stanford.smi.protegex.server_changes.listeners.owl;

import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.event.KnowledgeBaseListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.Model;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;


public class ChangesOwlKBListener implements KnowledgeBaseListener {
    private OWLModel om;
    private KnowledgeBase changesKb;
    
    public ChangesOwlKBListener(OWLModel om) {
        this.om = om;
        changesKb = ChangesProject.getChangesKB(om);
    }

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void clsCreated(KnowledgeBaseEvent event) {
		
//		Cls createdCls = event.getCls();
//		String clsName = createdCls.getName();
//		String context = "Created Class: " + clsName;
//		
//		// Create artifical transaction for create class
//		if (!ChangesTab.getIsInTransaction()) {
//			ChangesTab.createTransactionChange(om, ChangesTab.TRANS_SIGNAL_TRANS_BEGIN);
//			ChangesTab.setInCreateClass(true);
//		} 
//		
//		Instance changeInst = ChangeCreateUtil.createChange(
//												ChangesTab.getChangesKB(),
//												ChangeCreateUtil.CHANGETYPE_CLASS_CREATED, 
//												clsName, 
//												context, 
//												ChangeCreateUtil.CHANGE_LEVEL_INFO);
//		
//		ChangesTab.createChange(changeInst);
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
            Cls deletedCls = event.getCls();
            deletedClsName = oldName;
            ChangesDb changesDb = ChangesProject.getChangesDb(om);
            changesDb.updateMap(deletedCls.getFrameID(), deletedClsName);
        }
        
        String context = "Deleted Class: " + deletedClsName;
        Instance changeInst = ServerChangesUtil.createChange(om,
                                                changesKb,
                                                Model.CHANGETYPE_CLASS_DELETED,
                                                deletedClsName, 
                                                context, 
                                                Model.CHANGE_LEVEL_INFO);
        ChangesProject.createChange(om, changesKb, changeInst);
    
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

	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#slotDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void slotDeleted(KnowledgeBaseEvent event) {

	}

}
