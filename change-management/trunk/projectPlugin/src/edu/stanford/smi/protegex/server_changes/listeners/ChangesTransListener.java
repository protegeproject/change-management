package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.smi.protege.event.TransactionEvent;
import edu.stanford.smi.protege.event.TransactionListener;
import edu.stanford.smi.protegex.server_changes.ChangesProject;

public class ChangesTransListener implements TransactionListener {
	
	public void transactionBegin(TransactionEvent arg0) {

		ChangesProject.createTransactionChange(ChangesProject.TRANS_SIGNAL_TRANS_BEGIN);
	}

	public void transactionEnded(TransactionEvent arg0) {

		ChangesProject.createTransactionChange(ChangesProject.TRANS_SIGNAL_TRANS_END);
	}

}
