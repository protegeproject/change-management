package edu.stanford.smi.protegex.server_changes.postprocess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.change.api.Composite_Change;
import edu.stanford.bmir.protegex.chao.change.api.Created_Change;
import edu.stanford.bmir.protegex.chao.change.api.Individual_Added;
import edu.stanford.bmir.protegex.chao.change.api.Individual_Created;
import edu.stanford.bmir.protegex.chao.change.api.Name_Changed;
import edu.stanford.bmir.protegex.chao.change.api.Subclass_Added;
import edu.stanford.bmir.protegex.chao.change.api.TemplateSlot_Added;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Property;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.impl.DefaultTimestamp;
import edu.stanford.smi.protege.code.generator.wrapping.AbstractWrappedInstance;
import edu.stanford.smi.protege.server.RemoteSession;
import edu.stanford.smi.protegex.server_changes.PostProcessorManager;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

public class JoinCreateAndNameChange implements PostProcessor {
    private PostProcessorManager postProcessorManager;
    /*
     * When a component has just been created this map determines the change that caused the change.
     * This is used in the slightly tricky code that creates a transaction around sequential create +
     * name change operations.
     */
    private Map<RemoteSession, Created_Change> lastCreateBySession = new HashMap<RemoteSession, Created_Change>();
    private Set<RemoteSession> instanceAddedSeen = new HashSet<RemoteSession>();
    private Set<RemoteSession> subclassAddedSeen = new HashSet<RemoteSession>();
    private Set<RemoteSession> templateClassAddedSeen = new HashSet<RemoteSession>();

    public void initialize(PostProcessorManager postProcessorManager) {
        this.postProcessorManager = postProcessorManager;
    }

    /*
     * This is a little tricky.  I  am trying to combine a create operation
     * followed by a name change into a single transaction.  There are several cases.
     * The simple case is the sequence
     *      create operation
     *      name change.
     * In this case we create a transaction (**after the fact**) combining the create operation and
     * the name change.  But in the sequence
     *      create operation
     *      not name change (or name change of different object)
     * The transaction does not happen in this case.  A similar case is
     *      create operation
     *      begin transaction
     *        xxx
     *      end transaction
     * In this case the attempt to create the transaction also must be aborted. Finally there
     * is the owl case:
     *      begin transaction
     *         create change
     *         xxx
     *      end  transaction
     *      name change
     * Here we - again after the fact - create the transaction
     *      begin transaction
     *        begin transaction
     *          create change
     *          xxx
     *        end transaction
     *        name change
     *      end transaction
     * I think that the following code works.  Note that lastCreateBySession is only set by
     * ChangesDb.startChangeTransaction.  This gives the routine creating a change event a
     * chance to not try to create a session.
     */
    public void addChange(Change aChange) {
        RemoteSession session = postProcessorManager.getCurrentSession();
        if (aChange instanceof Created_Change
                && !(aChange instanceof Individual_Created)
                && aChange.getApplyTo() != null) {
        	removeLastCreate(session);
            lastCreateBySession.put(session, (Created_Change) aChange);
            return;
        }
        if (!postProcessorManager.getTransactionState().inTransaction()) {
            Created_Change previous_change = lastCreateBySession.get(session);
            if (previous_change == null) {
				return;
			}
            Ontology_Component created = previous_change.getApplyTo();

            if (aChange instanceof TemplateSlot_Added &&
                    !((TemplateSlot_Added) aChange).getAssociatedProperty().equals(created)) {
               removeLastCreate(session);
               return;
            }
            else if (!(aChange instanceof TemplateSlot_Added)) {
            	//TT: don't know if this is correct, but it threw a NullPointer before. Tim will fix.
            	if (aChange.getApplyTo() == null) {
                    removeLastCreate(session);
                    return;
            	} else {
            		if (!aChange.getApplyTo().equals(created)) {
            		    removeLastCreate(session);
                        return;
            		}
            	}
            } else if (aChange instanceof Name_Changed) {
                addNameChange(previous_change, (Name_Changed) aChange);
                removeLastCreate(session);
                return;
            }
            else if (!postProcessorManager.isOwl() && aChange instanceof Subclass_Added) {
                if (!subclassAddedSeen.contains(session)) {
                    subclassAddedSeen.add(session);
                    combineInTransaction(previous_change, aChange);
                    return;
                }
            }
            else if (!postProcessorManager.isOwl() && aChange instanceof Individual_Added) {
                if (!instanceAddedSeen.contains(session)) {
                    instanceAddedSeen.add(session);
                    combineInTransaction(previous_change, aChange);
                    return;
                }
            }
            else if (!postProcessorManager.isOwl() && aChange instanceof TemplateSlot_Added &&
                     created instanceof Ontology_Property) {
                if (!templateClassAddedSeen.contains(session)) {
                    templateClassAddedSeen.add(session);
                    combineInTransaction(previous_change, aChange);
                    return;
                }
            }
            else if (aChange instanceof Composite_Change) {
                Composite_Change transaction = (Composite_Change) aChange;
                Composite_Change create_transaction = getTopTransactionContaining(previous_change);
                if (create_transaction != null && transaction.equals(create_transaction)) {
                    return;
                }
            }
            removeLastCreate(session);
        }
    }

    private Composite_Change combineInTransaction(Created_Change change1, Change change2) {
    	//fishy
        Composite_Change transaction = getTopTransactionContaining(change1);
        if (transaction == null) {
            String new_name = change1.getCreationName();
            Ontology_Component created = change1.getApplyTo();
            List<Change> changes = new ArrayList<Change>();
            changes.add(change1);
            changes.add(change2);
            transaction = ServerChangesUtil.createTransactionChange(postProcessorManager,
                                                                    created,
                                                                    "Created " +
                                                                    created.getComponentType() + " "
                                                                    + new_name,
                                                                    changes);
            return transaction;
        }
        else {
            Collection subChanges = new ArrayList(transaction.getSubChanges());
            subChanges.add(change2);
            transaction.setSubChanges(subChanges);
            //fishy
            ((AbstractWrappedInstance)transaction.getTimestamp()).getWrappedProtegeInstance().delete();
            transaction.setTimestamp(DefaultTimestamp.getTimestamp(postProcessorManager.getChangesKb()));
            return transaction;
        }
    }

    private Composite_Change addNameChange(Created_Change change1, Name_Changed change2) {
    	//fishy
        Ontology_Component created = change1.getApplyTo();
        String new_name = change2.getNewName();
        List<Change> sub_changes = new ArrayList<Change>();
        Composite_Change top_created_transaction = getTopTransactionContaining(change1);
        Change first_change = top_created_transaction == null ? change1 : top_created_transaction;
        sub_changes.add(first_change);
        sub_changes.add(change2);

        Composite_Change transaction = ServerChangesUtil.createTransactionChange(postProcessorManager,
                                                                                 created,
                                                                                 "Created " +
                                                                                 created.getComponentType() + " " +
                                                                                 new_name,
                                                                                 sub_changes);
        return transaction;
    }

    private Composite_Change getTopTransactionContaining(Change change) {
        Composite_Change transaction = change.getPartOfCompositeChange();
        if (transaction == null && change instanceof Composite_Change) {
            return (Composite_Change) change;
        }
        else if (transaction == null) {
            return null;
        }
        else {
            return getTopTransactionContaining(transaction);
        }
    }

    private void removeLastCreate(RemoteSession session) {
        lastCreateBySession.remove(session);
        subclassAddedSeen.remove(session);
        instanceAddedSeen.remove(session);
        templateClassAddedSeen.remove(session);
    }



}
