package edu.stanford.smi.protegex.server_changes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.ibm.icu.util.StringTokenizer;

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
    
    public void beginTransaction(String name) {
        changeStack.push(new Stack<Change>());
        transactionNameStack.push(name);
    }
    
    public void commitTransaction() {
        List<Change> changes = changeStack.pop();
        String name = transactionNameStack.pop();
        Collections.reverse(changes);
        findAggAction(changes, name);
    }
    
    public void rollbackTransaction() {
        changeStack.pop();
    }
	

	
	private void findAggAction(List<Change> actions, String name) {
        boolean isOwl = changesDb.isOwl();
		if (isOwl) {
			// Check if we have a restriction
			if (isRestriction(actions)) {
				findRestrictionInstance(actions, name);
			} else {
				generateTransInstance(actions, name);
			}
		
		// Not an OWL project
		} else {
			generateTransInstance(actions, name);
		}
	}
	
	private void generateTransInstance(Collection<Change> transActions, String name) {
		if (!transActions.isEmpty()) {
			// find first info action
			boolean firstInfoInst = false;
			boolean firstDeleted = false;
			Change firstInst = null;
			
			for (Change change : transActions) {
				if (change.getType().equals(ChangeModel.CHANGE_LEVEL_INFO) && !firstInfoInst) {
					firstInst = change;
					firstInfoInst = true;
				}
				
				if (change.getDirectType().getName().equals(ChangeCls.Class_Deleted.toString()) && !firstDeleted) {
					firstInst = change;
					firstDeleted = true;
				}
            }
            transactionLevel(transActions);
            Composite_Change transaction = (Composite_Change) changesDb.createChange(ChangeCls.Composite_Change);
            transaction.setSubChanges(transActions);
            transaction.setAction(firstInst.getAction());
            changesDb.finalizeChange(transaction, (Ontology_Component) firstInst.getApplyTo(), 
                                     name, ChangeModel.CHANGE_LEVEL_TRANS);
		}
	}
	
	private Composite_Change findRestrictionInstance(List<Change> actions, String name) {
		
		String contextVal = null;
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
        
        transactionLevel(actions);
        Composite_Change transaction = (Composite_Change) changesDb.createChange(ChangeCls.Composite_Change);
        transaction.setAction(act);
        transaction.setSubChanges(actions);

        changesDb.finalizeChange(transaction, 
                                 applyToVal, 
                                 name, 
                                 ChangeModel.CHANGE_LEVEL_TRANS);
		return transaction;
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
	
	// make sure we convert the level to transaction_level
	private void transactionLevel(Collection<Change> trans) {
		for (Change change : trans) {
            change.setType(ChangeModel.CHANGE_LEVEL_TRANS_INFO);
		}
	}
	
	private ArrayList filterActions(Stack actions) {
		ArrayList results = new ArrayList();
		
		for (Iterator iter = actions.iterator(); iter.hasNext();) {
			Object element = (Object) iter.next();
			
			if(element instanceof Instance) {
				Instance aInst = (Instance) element;
				results.add(aInst);
			} else if (element instanceof Stack) {
				results.addAll(filterActions((Stack)element));
			}
		}
		
		return results;
	}
}
