package edu.stanford.smi.protegex.server_changes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import com.ibm.icu.util.StringTokenizer;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.server_changes.*;


public class TransactionUtility {
    private KnowledgeBase currentKB;
	private KnowledgeBase cKb;
	
	private Cls transChange;
	private Slot changes;
	private Slot author;
	private Slot context;
	private Slot created;
	private Slot action;
	private Slot type;
	private Slot applyTo;
	
	
	public TransactionUtility(KnowledgeBase currentKB, KnowledgeBase cKb) {
        this.currentKB = currentKB;
		this.cKb = cKb;
		transChange = cKb.getCls("TransChange");
		changes = cKb.getSlot("Changes");
		author = cKb.getSlot("author");
		context = cKb.getSlot("context");
		created = cKb.getSlot("created");
		action = cKb.getSlot("action");
		type = cKb.getSlot("type");
		applyTo = cKb.getSlot("applyTo");
	}
	
	public Stack convertTransactions(Stack trans) {
		Stack aTrans = new Stack();
		Object elem;
		
		while (!(elem = trans.pop()).equals(ChangesProject.TRANS_SIGNAL_START)) {
			aTrans.push(elem);
		}
		
		Collections.reverse(aTrans);
		trans.push(aTrans);
		return trans;
	}
	
	// Display transation
	public void displayTransactions(Stack trans) {
		
		for (Iterator iter = trans.iterator(); iter.hasNext();) {
			Object element = (Object) iter.next();
			
			if (element instanceof Instance) {
				Instance aElem = (Instance) element;
				
			} else if (element instanceof Stack) {
				displayTransactions((Stack) element);
			}
		}
	}
	
	public Instance findAggAction(Stack transStack, boolean isOwl) {
		
		ArrayList results = new ArrayList();
		Stack actionStack = (Stack) transStack.pop();
		ArrayList actions = new ArrayList(actionStack);
		ArrayList flatActions = filterActions(actionStack);
		
		Instance transInst = null;
		
		if (isOwl) {
			// Check if we have a restriction
			if (isRestriction(flatActions)) {
				transInst = findRestrictionInstance(flatActions);
			} else {
				transInst = generateTransInstance(flatActions);
			}
		
		// Not an OWL project
		} else {
			transInst = generateTransInstance(flatActions);
		}
		
		return transInst;
	}
	
	private Instance generateTransInstance(Collection transActions) {
		
		Instance tInst = null;
		
		if (!transActions.isEmpty()) {
			// find first info action
			boolean firstInfoInst = false;
			boolean firstDeleted = false;
			boolean firstAnnotate = false;
			Instance firstInst = null;
			
			for (Iterator iter = transActions.iterator(); iter.hasNext();) {
				Instance aInst = (Instance) iter.next();
				if ( ServerChangesUtil.getType(cKb, aInst).equals(ServerChangesUtil.CHANGE_LEVEL_INFO) && !firstInfoInst) {
					firstInst = aInst;
					firstInfoInst = true;
				}
				
				if (aInst.getDirectType().getName().equals(ServerChangesUtil.CHANGETYPE_CLASS_DELETED) && !firstDeleted) {
					firstInst = aInst;
					firstDeleted = true;
				}
				
//				if (aInst.getDirectType().getName().equals(ChangeCreateUtil.CHANGETYPE_ANNOTATION_ADDED) && !firstAnnotate) {
//					firstInst = aInst;
//					firstAnnotate = true;
//				}
			}
			
		
			
			tInst = ServerChangesUtil.createTransChange(cKb, transactionLevel(transActions), firstInst);
		} 
		
		return tInst;
	}
	
	private Instance findRestrictionInstance(ArrayList actions) {
		
		String contextVal = null;
		String ctxt = null;
		String applyToVal = null;
		
		String remApplyToVal = null;
		String remCtxt = null;
		String addCtxt = null;
		
		HashMap equalityMap = new HashMap();

		boolean foundAppliesTo = false;
		boolean foundRemAppliesTo = false;
		
		boolean containsRemoves = false;
		boolean containsAdds = false;
		 
		for (Iterator iter = actions.iterator(); iter.hasNext();) {
			Object element = (Object) iter.next();
			
			if (element instanceof Instance) {
				Instance aInst = (Instance) element;
				String actionStr = (String) aInst.getOwnSlotValue(action);
				
				// Checking here for equivalence (necessary vs. necessary and sufficient)
				if (actionStr.equals(ServerChangesUtil.CHANGETYPE_SUPERCLASS_ADDED) || 
						actionStr.equals(ServerChangesUtil.CHANGETYPE_SUBCLASS_ADDED)){
					containsAdds = true;
					String possCtxt = (String) aInst.getOwnSlotValue(context);
					if (!isAnon(possCtxt)) {
						
						// Find applies to field
						if (actionStr.equals(ServerChangesUtil.CHANGETYPE_SUBCLASS_ADDED) && !foundAppliesTo) {
							
							applyToVal = (String) aInst.getOwnSlotValue(applyTo);
							addCtxt = (String) decomposeContext(possCtxt, "(added to:").get(1);
							
							foundAppliesTo = true;
						}
						
						ArrayList names = decomposeContext(possCtxt, "(added to:");
						String fwd = actionStr+","+names.get(0)+","+names.get(1);
						if (equalityMap.containsKey(fwd)) {
							equalityMap.remove(fwd);
						} else {
							String rev = actionStr+","+names.get(1)+","+names.get(0);
							equalityMap.put(rev, null);
						}
					}
				} else if (actionStr.equals(ServerChangesUtil.CHANGETYPE_SUPERCLASS_REMOVED) || 
							actionStr.equals(ServerChangesUtil.CHANGETYPE_SUBCLASS_REMOVED)) {
					containsRemoves = true;
					String possCtxt = (String) aInst.getOwnSlotValue(context);
					if (!isAnon(possCtxt)) {
						if (actionStr.equals(ServerChangesUtil.CHANGETYPE_SUBCLASS_REMOVED) && !foundRemAppliesTo) {
							remApplyToVal = (String) aInst.getOwnSlotValue(applyTo);
							remCtxt = (String) decomposeContext(possCtxt, "(removed from:").get(1);
							foundRemAppliesTo = true;
						}	
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
		
		Instance cInst = cKb.createInstance(null, new ArrayList());
		cInst.setOwnSlotValues(changes, transactionLevel(actions));
		
		cInst.setOwnSlotValue(author, ChangesProject.getUserName(currentKB));
		cInst.setOwnSlotValue(action, act);
		cInst.setOwnSlotValue(context, contextVal);
		cInst.setOwnSlotValue(created, ChangesProject.getTimeStamp());
		cInst.setOwnSlotValue(applyTo, applyToVal);
		
		cInst.setOwnSlotValue(type,ServerChangesUtil.CHANGE_LEVEL_TRANS);
        
        cKb.setDirectType(cInst, transChange);
		return cInst;
	}
	
	// For restrictions
	private String contextCleaning(String context) {
		if (context.contains("rdf:List")) {
			context = context.replaceAll("rdf:List", "").trim();
			context = context.substring(1, context.length()-1);
			
			StringTokenizer tokens = new StringTokenizer(context, ",");
			while(tokens.hasMoreTokens()) {
				context = tokens.nextToken().trim();
			}
			
		}
		return context;
	}
	
	// Decompose context into its constituent parts for "Action: name (added to: actUpon)"
	private ArrayList decomposeContext(String context, String actUponStr) {
		ArrayList result = new ArrayList();
		
		String action = context.substring(context.indexOf(":")+1, context.length()).trim();
		String name = action.substring(0, action.lastIndexOf(actUponStr)-1);
		String actUpon = action.substring(action.indexOf(":")+1, action.length()-1).trim();
		
		result.add(name);
		result.add(actUpon);
		
		return result;
	}
	
	private boolean isRestriction(ArrayList actions) {
		
		boolean isRest = false;
		
		for (Iterator iter = actions.iterator(); iter.hasNext();) {
			Instance aInst = (Instance) iter.next();
			String ctxt = (String) aInst.getOwnSlotValue(context);
			
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
	private Collection transactionLevel(Collection trans) {
		for (Iterator iter = trans.iterator(); iter.hasNext();) {
			Instance element = (Instance) iter.next();
			
			String typeVal = (String) element.getOwnSlotValue(type);
			String transTyp = "transaction_" + typeVal;
			element.setOwnSlotValue(type, transTyp);
		}
		
		return trans;
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
