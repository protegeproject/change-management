package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.smi.protege.event.TransactionEvent;
import edu.stanford.smi.protege.event.TransactionListener;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.server_changes.PostProcessorManager;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.TransactionState;

public class ChangesTransListener implements TransactionListener {
    private PostProcessorManager changes_db;
    
    public ChangesTransListener(KnowledgeBase currentKB) {
        changes_db = ChangesProject.getChangesDb(currentKB);
    }
	
	public void transactionBegin(TransactionEvent arg0) {
	    TransactionState tstate = changes_db.getTransactionState();
        tstate.beginTransaction(arg0.getBeginString());
	}

    /* TODO TR - fix this to distinguish a rollback and a commit - requires changing event generation */
	public void transactionEnded(TransactionEvent arg0) {
	    TransactionState tstate = changes_db.getTransactionState();
	    tstate.commitTransaction();
	}

}
