package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.smi.protege.event.ClsEvent;
import edu.stanford.smi.protege.event.ClsListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.TransactionState;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;

public class ChangesClsListener implements ClsListener{
    private KnowledgeBase kb;
    private ChangesDb changes_db;
    private KnowledgeBase changesKb;
    
    public ChangesClsListener(KnowledgeBase kb) {
        this.kb = kb;
        changes_db = ChangesProject.getChangesDb(kb);
        changesKb = changes_db.getChangesKb();
    }

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directInstanceAdded(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directInstanceAdded(ClsEvent event) {
		Instance addedInst = event.getInstance();
		Cls clsOfInst = event.getCls();
		
		StringBuffer context = new StringBuffer();
		context.append("Added Instance: ");
		context.append(addedInst.getBrowserText());
		context.append(" (instance of ");
		context.append(clsOfInst.getBrowserText());
		context.append(")");
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(addedInst.getName(), true);
        
        Change change = changes_db.createChange(ChangeCls.Instance_Added);
        changes_db.finalizeChange(change, applyTo, context.toString());

		// Create artificial transaction for create slot
        TransactionState tstate = changes_db.getTransactionState();
		if (changes_db.isInCreateClass() && tstate.inTransaction()) {
            tstate.commitTransaction();
            changes_db.setInCreateClass(false);
		}
		
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directInstanceRemoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directInstanceRemoved(ClsEvent event) {
		Instance removedInst = event.getInstance();
		Cls clsOfInst = event.getCls();
		String name = changes_db.getPossiblyDeletedFrameName(removedInst);
		
		StringBuffer context = new StringBuffer();
		context.append("Removed Instance: ");
		context.append(removedInst.getBrowserText());
		context.append(" (instance of ");
		context.append(clsOfInst.getBrowserText());
		context.append(")");
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(name, true);
        
        Change change = changes_db.createChange(ChangeCls.Instance_Removed);
        changes_db.finalizeChange(change, applyTo, context.toString());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directSubclassAdded(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directSubclassAdded(ClsEvent event) {
		Cls subClass = event.getSubclass();
		Cls superClass = event.getCls();
		
		StringBuffer context = new StringBuffer();
		context.append("Added subclass: ");
		context.append(subClass.getBrowserText());
		context.append(" (subclass of ");
		context.append(superClass.getBrowserText());
		context.append(")");
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(subClass.getName(), true);
        
        Change change = changes_db.createChange(ChangeCls.Subclass_Added);
        changes_db.finalizeChange(change, applyTo, context.toString());
		
        TransactionState tstate = changes_db.getTransactionState();
		// Create artificial transaction for create class
		if (tstate.inTransaction() && changes_db.isInCreateClass()) {
            tstate.commitTransaction();
            changes_db.setInCreateClass(false);
		}
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directSubclassMoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directSubclassMoved(ClsEvent event) {
		// Method is not used/called
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directSubclassRemoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directSubclassRemoved(ClsEvent event) {
		Cls subClass = event.getSubclass();
		Cls superClass = event.getCls();
        String name = changes_db.getPossiblyDeletedFrameName(subClass);
		
		StringBuffer context = new StringBuffer();
		context.append("Removed subclass: ");
		context.append(subClass.getBrowserText());
		context.append(" (subclass of ");
		context.append(superClass.getBrowserText());
		context.append(")");
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(name, true);
        
        Change change = changes_db.createChange(ChangeCls.Subclass_Removed);
        changes_db.finalizeChange(change, applyTo, context.toString());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directSuperclassAdded(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directSuperclassAdded(ClsEvent event) {
		Cls subClass = event.getSubclass();
		Cls superClass = event.getCls();
	
		StringBuffer context = new StringBuffer();
		context.append("Added superclass: ");
		context.append(subClass.getBrowserText());
		context.append(" (subclass of ");
		context.append( superClass.getBrowserText());
		context.append(")");
		
        Ontology_Component applyTo = changes_db.getOntologyComponent(subClass.getName(), true);
        
        Change change = changes_db.createChange(ChangeCls.Superclass_Added);
        changes_db.finalizeChange(change, applyTo, context.toString());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directSuperclassRemoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directSuperclassRemoved(ClsEvent event) {
		Cls subClass = event.getSubclass();
		Cls superClass = event.getCls();
        String name = changes_db.getPossiblyDeletedFrameName(subClass);
	
		StringBuffer context = new StringBuffer();
		context.append("Removed superclass: ");
		context.append(subClass.getBrowserText());
		context.append(" (subclass of ");
		context.append(superClass.getBrowserText());
		context.append(")");
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(name, true);
        
        Change change = changes_db.createChange(ChangeCls.Superclass_Removed);
        changes_db.finalizeChange(change, applyTo, context.toString());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#templateFacetAdded(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void templateFacetAdded(ClsEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#templateFacetRemoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void templateFacetRemoved(ClsEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#templateFacetValueChanged(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void templateFacetValueChanged(ClsEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#templateSlotAdded(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void templateSlotAdded(ClsEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#templateSlotRemoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void templateSlotRemoved(ClsEvent event) {
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#templateSlotValueChanged(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void templateSlotValueChanged(ClsEvent event) {
	}
}
