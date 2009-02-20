package edu.stanford.smi.protegex.server_changes.listeners.owl;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.smi.protege.event.KnowledgeBaseAdapter;
import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.PostProcessorManager;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;


public class ChangesOwlKBListener extends KnowledgeBaseAdapter {
    private static final Logger log = Log.getLogger(ChangesOwlKBListener.class);

    private PostProcessorManager changes_db;
    private ChangeFactory factory;

    public ChangesOwlKBListener(OWLModel om) {
        changes_db = ChangesProject.getPostProcessorManager(om);
        factory = new ChangeFactory(changes_db.getChangesKb());
    }

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	@Override
	public void clsCreated(KnowledgeBaseEvent event) {
	    if (event.isReplacementEvent()) {
	        return;
	    }
        Cls cls = event.getCls();
        ServerChangesUtil.createCreatedChange(changes_db, factory.createClass_Created(null), cls, cls.getName());
	}

    /* (non-Javadoc)
     * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
     */
    @Override
	public void clsDeleted(KnowledgeBaseEvent event) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("In deleted class listener");
        }
        if (event.isReplacementEvent()) {
            return;
        }
        String deletedClsName = event.getOldName();
        Frame deletedFrame = event.getCls();
        ServerChangesUtil.createDeletedChange(changes_db, factory.createClass_Deleted(null), deletedFrame, deletedClsName);
    }


	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#frameNameChanged(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	@Override
	public void frameNameChanged(KnowledgeBaseEvent event) {
        String oldName = event.getOldName();

        ServerChangesUtil.createNameChange(changes_db, event.getFrame(), oldName, event.getNewFrame().getName());

	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#instanceCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	@Override
	public void instanceCreated(KnowledgeBaseEvent event) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("In created instance listener");
        }
        if (event.isReplacementEvent()) {
            return;
        }
        Frame frame = event.getFrame();
        ServerChangesUtil.createCreatedChange(changes_db, factory.createIndividual_Created(null), frame, frame.getName());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#instanceDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	@Override
	public void instanceDeleted(KnowledgeBaseEvent event) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("In deleted instance listener");
        }
        if (event.isReplacementEvent()) {
            return;
        }
        String name = event.getOldName();
        Frame frame = event.getFrame();
        ServerChangesUtil.createDeletedChange(changes_db, factory.createIndividual_Deleted(null), frame, name);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#slotCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	@Override
	public void slotCreated(KnowledgeBaseEvent event) {
        if (event.isReplacementEvent()) {
            return;
        }
        Frame prop = event.getFrame();
        String propName = prop.getName();
        ServerChangesUtil.createCreatedChange(changes_db, factory.createProperty_Created(null), prop, prop.getName());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#slotDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	@Override
	public void slotDeleted(KnowledgeBaseEvent event) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("In deleted slot listener");
        }
        if (event.isReplacementEvent()) {
            return;
        }
        String propName = event.getOldName();
        Frame frame = event.getSlot();
        if (event.getSlot() instanceof RDFProperty) {
            ServerChangesUtil.createDeletedChange(changes_db,
                                                  factory.createProperty_Deleted(null),
                                                  frame,
                                                  propName);
        }
	}


}
