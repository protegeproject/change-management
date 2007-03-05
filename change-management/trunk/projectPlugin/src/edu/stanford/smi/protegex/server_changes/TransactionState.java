package edu.stanford.smi.protegex.server_changes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Composite_Change;
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
        changeStack.pop();  // this isn't right! but I am not calling it yet...
        transactionNameStack.pop();
    }
    
    
	public void createTransactionChange(List<Change> changes, String name) {
        if (changes.size() == 0) return;
        
        String action = ChangeCls.Composite_Change.toString();
        String context = name;
        Ontology_Component applyTo = (Ontology_Component) changes.get(0).getApplyTo();
        
        Representative r = guessRepresentative(changes);
        if (r != null) {
            if (r.getAction() != null) action = r.getAction();
            if (r.getContext() != null) context = r.getContext();
            if (r.getApplyTo() != null) applyTo = r.getApplyTo();
        }
        Composite_Change transaction = (Composite_Change) changesDb.createChange(ChangeCls.Composite_Change);
        transaction.setSubChanges(changes);
        transaction.setAction(action);
        changesDb.finalizeChange(transaction, applyTo, context);
        
    }

	
	private Representative guessRepresentative(List<Change> actions) {
        boolean isOwl = changesDb.isOwl();
		if (isOwl) {
			// Check if we have a restriction
			if (isRestriction(actions)) {
				return findRestrictionInstance(actions);
			} else {
				return generateTransInstance(actions);
			}
		
		// Not an OWL project
		} else {
			return generateTransInstance(actions);
		}
	}
	
	private Representative generateTransInstance(Collection<Change> transActions) {
	    boolean firstInfoInst = false;
	    boolean firstDeleted = false;
	    Change firstInst = null;

	    for (Change change : transActions) {
	        if (!(change instanceof Composite_Change) && !firstInfoInst) {
	            firstInst = change;
	            firstInfoInst = true;
	        }

	        if (change.getDirectType().getName().equals(ChangeCls.Class_Deleted.toString()) && !firstDeleted) {
	            firstInst = change;
	            firstDeleted = true;
	        }
	    }
        return new Representative(firstInst.getAction(), 
                                  firstInst.getContext(), 
                                  (Ontology_Component) firstInst.getApplyTo());
	}
	
	private Representative findRestrictionInstance(List<Change> actions) {
        String contextVal;
		String ctxt = null;
		Ontology_Component applyToVal = null;
		
		Ontology_Component remApplyToVal = null;
		String remCtxt = null;
		String addCtxt = null;
		
		HashMap<String, String> equalityMap = new HashMap<String, String>();

		boolean foundAppliesTo = false;
		boolean foundRemAppliesTo = false;
		
		boolean containsRemoves = false;
		boolean containsAdds = false;
		 
		for (Change change : actions) {

		    String actionStr = change.getAction();

		    // Checking here for equivalence (necessary vs. necessary and sufficient)
		    if (actionStr.equals(ChangeCls.Superclass_Added.toString()) || 
		            actionStr.equals(ChangeCls.Subclass_Added.toString())) {
		        containsAdds = true;
		        String possCtxt = change.getContext();
		        if (!isAnon(possCtxt)) {

		            // Find applies to field
		            if (actionStr.equals(ChangeCls.Subclass_Added.toString()) && !foundAppliesTo) {

		                applyToVal = (Ontology_Component) change.getApplyTo();
		                addCtxt = (String) decomposeContext(possCtxt, "(added to:").get(1);
		                foundAppliesTo = true;
		            }

		            List<String> names = decomposeContext(possCtxt, "(added to:");
		            String fwd = actionStr+","+names.get(0)+","+names.get(1);
		            if (equalityMap.containsKey(fwd)) {
		                equalityMap.remove(fwd);
		            } else {
		                String rev = actionStr+","+names.get(1)+","+names.get(0);
		                equalityMap.put(rev, null);
		            }
		        }
		    } else if (actionStr.equals(ChangeCls.Superclass_Added.toString()) || 
		            actionStr.equals(ChangeCls.Subclass_Added.toString())) {
		        containsRemoves = true;
		        String possCtxt = change.getContext();
		        if (!isAnon(possCtxt)) {
		            if (actionStr.equals(ChangeCls.Subclass_Removed.toString()) && !foundRemAppliesTo) {
		                remApplyToVal = (Ontology_Component) change.getApplyTo();
		                remCtxt = (String) decomposeContext(possCtxt, "(removed from:").get(1);
		                foundRemAppliesTo = true;
		            }	
		        }
		    }
		}
		
		String act = "";
		if (containsAdds && containsRemoves) {
			act = "Restriction Modified";
			ctxt = addCtxt;
		} else if (containsAdds && !containsRemoves) {
			ctxt = addCtxt;
			act = "Restriction Created";
			if (equalityMap.isEmpty()) {
				act = "Restriction Created (defined)";
			}
		} else if (containsRemoves && ! containsAdds) {
			act = "Restriction Removed";
			applyToVal = remApplyToVal;
			ctxt = remCtxt;
		}
		contextVal = act + ": " + ctxt;
        
        return new Representative(act, ctxt, applyToVal);
    }
	
	// Decompose context into its constituent parts for "Action: name (added to: actUpon)"
	private List<String> decomposeContext(String context, String actUponStr) {
		ArrayList<String> result = new ArrayList<String>();
		
		String action = context.substring(context.indexOf(":")+1, context.length()).trim();
		String name = action.substring(0, action.lastIndexOf(actUponStr)-1);
		String actUpon = action.substring(action.indexOf(":")+1, action.length()-1).trim();
		
		result.add(name);
		result.add(actUpon);
		
		return result;
	}
	
	private boolean isRestriction(List<Change> actions) {
		
		boolean isRest = false;
		
		for (Change aInst : actions) {
			String ctxt = aInst.getContext();
			
			if (isAnon(ctxt)) {
				isRest = true;
				return isRest;
			}
		}
		
		return isRest;
	}
	
	private boolean isAnon(String context) {
		boolean isAnon = false;
		
		if (context.contains("?") || context.contains("empty") || context.contains("ANONYMOUS")) {
			isAnon = true;
		}
		
		return isAnon;
	}
	
	
    /*
     * This class provides values that the user wants to see.  If we use a poor heuristic for finding these
     * values then it is hard for the user to understand what the change tab is showing.
     */
    public class Representative {
        private String action;
        private String context;
        private Ontology_Component applyTo;
        
        public String getAction() {
            return action;
        }
        
        public String getContext() {
            return context;
        }
        
        public Ontology_Component getApplyTo() {
            return applyTo;
        }
        
        public Representative(String action, String context, Ontology_Component applyTo) {
            this.action = action;
            this.context = context;
            this.applyTo = applyTo;
        }
    }
}
