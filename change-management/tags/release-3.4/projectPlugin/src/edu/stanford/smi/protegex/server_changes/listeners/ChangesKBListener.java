package edu.stanford.smi.protegex.server_changes.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.smi.protege.event.KnowledgeBaseAdapter;
import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.server_changes.PostProcessorManager;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

public class ChangesKBListener extends KnowledgeBaseAdapter {
    private final static Logger log = Log.getLogger(ChangesKBListener.class);

    private PostProcessorManager changes_db;
    private ChangeFactory factory;

    public ChangesKBListener(KnowledgeBase kb) {
        changes_db = ChangesProject.getPostProcessorManager(kb);
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
        Cls createdCls = event.getCls();
        ServerChangesUtil.createCreatedChange(changes_db, factory.createClass_Created(null), createdCls, createdCls.getName());
    }

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	@Override
	public void clsDeleted(KnowledgeBaseEvent event) {
            if (log.isLoggable(Level.FINE)) {
                log.fine("In class deleted listener");
            }
            if (event.isReplacementEvent()) {
                return;
            }
            String oldName = event.getOldName();
            Frame frame = event.getCls();
            ServerChangesUtil.createDeletedChange(changes_db, factory.createClass_Deleted(null), frame, oldName);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#frameNameChanged(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	@Override
	public void frameNameChanged(KnowledgeBaseEvent event) {
	    String oldName = event.getOldName();
        Frame frame = event.getFrame();
	    String newName = event.getNewFrame().getName();

        ServerChangesUtil.createNameChange(changes_db, frame, oldName, newName);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#instanceCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	@Override
	public void instanceCreated(KnowledgeBaseEvent event) {
	    if (event.isReplacementEvent()) {
	        return;
	    }
	    if (log.isLoggable(Level.FINE)) {
	        log.fine("In created instance listener");
	    }
	    Frame frame = event.getFrame();
	    ServerChangesUtil.createCreatedChange(changes_db, factory.createIndividual_Created(null), frame, frame.getName());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#instanceDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	@Override
	public void instanceDeleted(KnowledgeBaseEvent event) {
	    if (event.isReplacementEvent()) {
	        return;
	    }
        if (log.isLoggable(Level.FINE)) {
            log.fine("In deleted instance listener");
        }
        Frame frame = event.getFrame();
        String name = event.getOldName();
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
		Slot createdSlot = event.getSlot();
		ServerChangesUtil.createCreatedChange(changes_db, factory.createProperty_Created(null), createdSlot, createdSlot.getName());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#slotDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	@Override
	public void slotDeleted(KnowledgeBaseEvent event) {
	    if (event.isReplacementEvent()) {
	        return;
	    }
        String oldName = event.getOldName();
        Frame frame = event.getSlot();
        ServerChangesUtil.createDeletedChange(changes_db, factory.createProperty_Deleted(null), frame, oldName);
	}
}
