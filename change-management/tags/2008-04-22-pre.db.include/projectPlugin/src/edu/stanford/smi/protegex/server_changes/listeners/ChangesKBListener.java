package edu.stanford.smi.protegex.server_changes.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.event.KnowledgeBaseAdapter;
import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;

public class ChangesKBListener extends KnowledgeBaseAdapter {
    private final static Logger log = Log.getLogger(ChangesKBListener.class);
      
    private ChangesDb changes_db;
    
    public ChangesKBListener(KnowledgeBase kb) {    
        changes_db = ChangesProject.getChangesDb(kb);   
    }
	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
    public void clsCreated(KnowledgeBaseEvent event) {
        Cls createdCls = event.getCls();
        ServerChangesUtil.createCreatedChange(changes_db, ChangeCls.Class_Created, createdCls, createdCls.getName());
    }

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#clsDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void clsDeleted(KnowledgeBaseEvent event) {
            if (log.isLoggable(Level.FINE)) {
                log.fine("In class deleted listener");
            }
            String oldName = event.getOldName();
            Frame frame = event.getCls();
            ServerChangesUtil.createDeletedChange(changes_db, ChangeCls.Class_Deleted, frame, oldName);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#frameNameChanged(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void frameNameChanged(KnowledgeBaseEvent event) {
	    String oldName = event.getOldName();
        Frame frame = event.getFrame();
	    String newName = frame.getName();

        ServerChangesUtil.createNameChange(changes_db, frame, oldName, newName);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#instanceCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void instanceCreated(KnowledgeBaseEvent event) {
	    if (log.isLoggable(Level.FINE)) {
	        log.fine("In created instance listener");
	    }
	    Frame frame = event.getFrame();
	    ServerChangesUtil.createCreatedChange(changes_db, ChangeCls.Individual_Created, frame, frame.getName());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#instanceDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void instanceDeleted(KnowledgeBaseEvent event) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("In deleted instance listener");
        }
        Frame frame = event.getFrame();
        String name = event.getOldName();
        ServerChangesUtil.createDeletedChange(changes_db, ChangeCls.Individual_Deleted, frame, name);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#slotCreated(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void slotCreated(KnowledgeBaseEvent event) {
		Slot createdSlot = event.getSlot();
		ServerChangesUtil.createCreatedChange(changes_db, ChangeCls.Property_Created, createdSlot, createdSlot.getName());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.KnowledgeBaseListener#slotDeleted(edu.stanford.smi.protege.event.KnowledgeBaseEvent)
	 */
	public void slotDeleted(KnowledgeBaseEvent event) {
        String oldName = event.getOldName();
        Frame frame = event.getSlot();
        ServerChangesUtil.createDeletedChange(changes_db, ChangeCls.Property_Deleted, frame, oldName);
	}
}
