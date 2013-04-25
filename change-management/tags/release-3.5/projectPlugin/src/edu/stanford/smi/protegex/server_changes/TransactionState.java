package edu.stanford.smi.protegex.server_changes;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Transaction;


public class TransactionState {
	private PostProcessorManager changesDb;
	
	// Synchronization: these two variables are protected by TransactionState.this.
	//                  there is no threat of deadlock because the lock is only held for
	//                  the duration of java library calls.
	private Stack<List<Change>> changeStack = new Stack<List<Change>>();
	private Stack<String> transactionNameStack = new Stack<String>();


	public TransactionState(PostProcessorManager changesDb) {
		this.changesDb = changesDb;
	}

	public synchronized int getTransactionDepth() {
		return changeStack.size();
	}

	public synchronized boolean inTransaction() {
		return changeStack.size() != 0;
	}

	public synchronized void addToTransaction(Change change) {
		changeStack.peek().add(change);
	}

	public synchronized void beginTransaction(String name) {
		changeStack.push(new ArrayList<Change>());
		transactionNameStack.push(name);
	}

	public void commitTransaction() {
	    synchronized (changesDb.getChangesKb()) {
	        List<Change> changes;
	        String name;
	        synchronized (this) {
	            changes = changeStack.pop();
	            name = transactionNameStack.pop();
	        }
	        createTransactionChange(changes, name);
	    }
	}

	public synchronized void rollbackTransaction() {
		changeStack.pop();
		transactionNameStack.pop();
	}


	public void createTransactionChange(List<Change> changes, String context) {
	    synchronized (this) { // I don't believe synchronization is necessary but this is easier to explain
	        if (changes.size() == 0) {
	            return;
	        }
	    }

		Ontology_Component applyTo = getApplyToFromContext(context);

		if (applyTo == null) {
			Representative r;
			try {
				r = guessRepresentative(changes, context);
			} catch (Exception e) {
				e.printStackTrace();
				r = null;
			}
			if (r != null) {
				//if (r.getAction() != null) action = r.getAction();
				if (r.getApplyTo() != null && applyTo == null) {
					applyTo = r.getApplyTo();
				}
			}
		}
		ServerChangesUtil.createTransactionChange(changesDb, applyTo, context, changes);
	}

	public Ontology_Component getApplyToFromContext(String context) {
		if (context == null) {
			return null;
		}
		String frame_name = Transaction.getApplyTo(context);
		if (frame_name == null) {
			return null;
		}
		Frame frame = changesDb.getKb().getFrame(frame_name);
		if (frame == null) {
			return null;
		}
		return changesDb.getOntologyComponent(frame, true);
	}


	private Representative guessRepresentative(List<Change> actions, String name) {
		boolean owl = changesDb.isOwl();
		Ontology_Component first = null;
		Ontology_Component named = null;
		for (Change change : actions) {
			if (first == null) {
				first = change.getApplyTo();
				if (!owl) {
					break;
				}
			}
			if (owl && named == null) {
				Ontology_Component current = change.getApplyTo();
				if (current.isAnonymous()) {
					named = current;
					break;
				}
			}
		}
		Representative r = new Representative(name, named != null ? named : first);
		return r;
	}


	/*
	 * This class provides values that the user wants to see.  If we use a poor heuristic for finding these
	 * values then it is hard for the user to understand what the change tab is showing.
	 */
	class Representative {
		private String action;
		private Ontology_Component applyTo;

		public String getAction() {
			return action;
		}

		public Ontology_Component getApplyTo() {
			return applyTo;
		}

		public Representative(String action, Ontology_Component applyTo) {
			this.action = action;
			this.applyTo = applyTo;
		}
	}
}
