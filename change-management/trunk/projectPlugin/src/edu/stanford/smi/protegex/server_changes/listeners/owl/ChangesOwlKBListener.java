package edu.stanford.smi.protegex.server_changes.listeners.owl;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.event.KnowledgeBaseListener;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Class_Deleted;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;
import edu.stanford.smi.protegex.server_changes.model.generated.Property_Deleted;


public class ChangesOwlKBListener implements KnowledgeBaseListener {
    private static final Logger log = Log.getLogger(ChangesOwlKBListener.class);
    private OWLModel om;
    private KnowledgeBase changesKb;
    private ChangesDb changes_db;
    
    public ChangesOwlKBListener(OWLModel om) {
        this.om = om;
        changes_db = ChangesProject.getChangesDb(om);
        changesKb = changes_db.getChangesKb();
    }

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void clsCreated(KnowledgeBaseEvent event) {
        String clsName = event.getFrame().getName();
        ServerChangesUtil.createCreatedChange(changes_db, ChangeCls.Class_Created, clsName, true);
	}
    
    /* (non-Javadoc)
     * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
     */
    public void clsDeleted(KnowledgeBaseEvent event) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("In deleted class listener");
        }
        String deletedClsName = event.getOldName();
        FrameID frameId = event.getCls().getFrameID();
        ServerChangesUtil.createDeletedChange(changes_db, ChangeCls.Class_Deleted, frameId, deletedClsName);
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

        ServerChangesUtil.createNameChange(changes_db, oldName, newName);

	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#instanceCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void instanceCreated(KnowledgeBaseEvent event) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("In created instance listener");
        }
        Frame frame = event.getFrame();
        String name = frame.getName();
        ServerChangesUtil.createCreatedChange(changes_db, ChangeCls.Instance_Created, name, false);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#instanceDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void instanceDeleted(KnowledgeBaseEvent event) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("In deleted instance listener");
        }
        String name = event.getOldName();
        FrameID frameId = event.getFrame().getFrameID();
        ServerChangesUtil.createDeletedChange(changes_db, ChangeCls.Instance_Deleted, frameId, name);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#slotCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void slotCreated(KnowledgeBaseEvent event) {
        Frame prop = event.getFrame();
        String propName = prop.getName();
        ChangeCls change = ChangeCls.Slot_Created;
        if (prop instanceof RDFProperty) {
            change = ChangeCls.Property_Created;
        }
        ServerChangesUtil.createCreatedChange(changes_db, change, propName, true);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#slotDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void slotDeleted(KnowledgeBaseEvent event) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("In deleted slot listener");
        }
        String propName = event.getOldName();
        FrameID frameId = event.getSlot().getFrameID();
        if (event.getSlot() instanceof RDFProperty) {
            ServerChangesUtil.createDeletedChange(changes_db,
                                                  ChangeCls.Property_Deleted,
                                                  frameId, 
                                                  propName);
        }
	}
    

}
