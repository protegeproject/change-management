package edu.stanford.smi.protegex.server_changes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Transaction;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Composite_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Deleted_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;


public class TransactionState {
    private ChangesDb changesDb;
    private Stack<List<Change>> changeStack = new Stack<List<Change>>();
    private Stack<String> transactionNameStack = new Stack<String>();

    
    public TransactionState(ChangesDb changesDb) {
        this.changesDb = changesDb;
    }
    
    public int getTransactionDepth() {
        return changeStack.size();
    }
    
    public boolean inTransaction() {
        return changeStack.size() != 0;
    }
    
    public void addToTransaction(Change change) {
        changeStack.peek().add(change);
    }
    
    public void beginTransaction(String name) {
        changeStack.push(new ArrayList<Change>());
        transactionNameStack.push(name);
    }
    
    public void commitTransaction() {
        List<Change> changes = changeStack.pop();
        String name = transactionNameStack.pop();
        createTransactionChange(changes, name);
    }
    
    public void rollbackTransaction() {
        changeStack.pop();
        transactionNameStack.pop();
    }
    
    
	public void createTransactionChange(List<Change> changes, String context) {
        if (changes.size() == 0) return;
        
        String action = ChangeCls.Composite_Change.toString();
        
        Ontology_Component applyTo = getApplyToFromContext(context);
        
        if (applyTo == null) {
        	Representative r;
        	try {
        		r = guessRepresentative(changes, context);
        	} catch (Exception e) {
        		r = null;
        	}
        	if (r != null) {
        		//if (r.getAction() != null) action = r.getAction();
        		if (r.getApplyTo() != null && applyTo == null) applyTo = r.getApplyTo();
        	}
        }
        Composite_Change transaction = (Composite_Change) changesDb.createChange(ChangeCls.Composite_Change);
        transaction.setSubChanges(changes);
        transaction.setAction(action);
        changesDb.finalizeChange(transaction, applyTo, context);
        
    }
    
    public Ontology_Component getApplyToFromContext(String context) {
        if (context == null) return null;
        String frame_name = Transaction.getApplyTo(context);
        if (frame_name == null) return null;
        Frame frame = changesDb.getKb().getFrame(frame_name);
        if (frame == null) return null;
        return changesDb.getOntologyComponent(frame, true);
    }

	
	private Representative guessRepresentative(List<Change> actions, String name) {
		boolean owl = changesDb.isOwl();
		Ontology_Component first = null;
		Ontology_Component named = null;
		for (Change change : actions) {
			if (first == null) {
				first = (Ontology_Component) change.getApplyTo();
				if (!owl) break;
			}
			if (owl && named == null) {
				Ontology_Component current = (Ontology_Component) change.getApplyTo();
				if (!current.isAnonymous()) {
					named = current;
					break;
				}
			}
		}
		Representative r = new Representative(name, named != null ? named : first);
		return r;
	}
	/*
	{
        boolean isOwl = changesDb.isOwl();
        Representative r = null;
		if (isOwl) {
            if ((r = guessFirstNonAnonymousAction(actions, name)) != null) return r;
            if ((r = guessFirstOrDelete(actions, name)) != null) return r;
        }
        else {
            if ((r = guessFirstOrDelete(actions, name)) != null) return r;
        }
        return null;
	}
    */
    
    private Representative guessFirstNonAnonymousAction(List<Change> actions, String name) {
        for (Change change : actions) {
            if (!((Ontology_Component) change.getApplyTo()).isAnonymous()) {
                return new Representative(change.getAction(), 
                                          (Ontology_Component) change.getApplyTo());
            }
        }
        return null;
    }
   
    
	private Representative guessFirstOrDelete(Collection<Change> transActions, String name) {
	    boolean firstInfoInst = false;
	    boolean firstDeleted = false;
	    Change firstInst = null;

	    for (Change change : transActions) {
	        if (!(change instanceof Composite_Change) && !firstInfoInst) {
	            firstInst = change;
	            firstInfoInst = true;
	        }

	        if (change instanceof Deleted_Change && !firstDeleted) {
	            firstInst = change;
	            firstDeleted = true;
	        }
	    }
        return new Representative(firstInst.getAction(), 
                                  (Ontology_Component) firstInst.getApplyTo());
	}
    
    private Frame findNamedClass(RDFResource resource) {
        return resource;
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
