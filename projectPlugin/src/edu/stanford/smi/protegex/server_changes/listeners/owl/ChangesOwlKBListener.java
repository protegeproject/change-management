package edu.stanford.smi.protegex.server_changes.listeners.owl;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.event.KnowledgeBaseListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.Model;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;


public class ChangesOwlKBListener implements KnowledgeBaseListener {
    private static final Logger log = Log.getLogger(ChangesOwlKBListener.class);
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
	}
    
    /* (non-Javadoc)
     * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
     */
    public void clsDeleted(KnowledgeBaseEvent event) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("In deleted class listener");
        }
        updateDeletedFrameMap(event);
        String deletedClsName = event.getOldName();
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
        if (log.isLoggable(Level.FINE)) {
            log.fine("In deleted instance listener");
        }
	    updateDeletedFrameMap(event);
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
        if (log.isLoggable(Level.FINE)) {
            log.fine("In deleted slot listener");
        }
	    updateDeletedFrameMap(event);
        String propName = event.getOldName();
        if (event.getSlot() instanceof RDFProperty) {
            String context = "Property Deleted: " + propName;

            Instance changeInst = ServerChangesUtil.createChange(om,
                                                                 changesKb,
                                                                 Model.CHANGETYPE_PROPERTY_DELETED,
                                                                 propName, 
                                                                 context, 
                                                                 Model.CHANGE_LEVEL_INFO);
            ChangesProject.createChange(om,changesKb, changeInst);
        }
	}
    
    private void updateDeletedFrameMap(KnowledgeBaseEvent event) {
        Frame f = event.getFrame();
        String name = event.getOldName();
        if (log.isLoggable(Level.FINE)) {
            log.fine("Deleting frame with frame id " + f.getFrameID() + " and name = " + name);
        }
        ChangesDb changesDb = ChangesProject.getChangesDb(om);
        changesDb.updateMap(f.getFrameID(), name);
    }

}
