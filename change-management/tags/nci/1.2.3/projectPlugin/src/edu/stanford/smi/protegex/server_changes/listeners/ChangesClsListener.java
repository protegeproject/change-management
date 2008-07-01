package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.smi.protege.event.ClsAdapter;
import edu.stanford.smi.protege.event.ClsEvent;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;

public class ChangesClsListener extends ClsAdapter{    
    private ChangesDb changes_db;    
    
    public ChangesClsListener(KnowledgeBase kb) {       
        changes_db = ChangesProject.getChangesDb(kb);        
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
        
        ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Individual_Added, addedInst, context.toString());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directInstanceRemoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directInstanceRemoved(ClsEvent event) {
		Instance removedInst = event.getInstance();
		Cls clsOfInst = event.getCls();
		
		StringBuffer context = new StringBuffer();
		context.append("Removed Instance: ");
		context.append(removedInst.getBrowserText());
		context.append(" (instance of ");
		context.append(clsOfInst.getBrowserText());
		context.append(")");
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(removedInst, true);
        
        Change change = changes_db.createChange(ChangeCls.Individual_Removed);
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
        
        ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Subclass_Added, subClass, context.toString());
	}


	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directSubclassRemoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directSubclassRemoved(ClsEvent event) {
		Cls subClass = event.getSubclass();
		Cls superClass = event.getCls();
		
		StringBuffer context = new StringBuffer();
		context.append("Removed subclass: ");
		context.append(subClass.getBrowserText());
		context.append(" (subclass of ");
		context.append(superClass.getBrowserText());
		context.append(")");
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(subClass, true);
        
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
        
        ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Superclass_Added, subClass, context.toString());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.ClsListener#directSuperclassRemoved(edu.stanford.smi.protege.event.ClsEvent)
	 */
	public void directSuperclassRemoved(ClsEvent event) {
		Cls subClass = event.getSubclass();
		Cls superClass = event.getCls();
	
		StringBuffer context = new StringBuffer();
		context.append("Removed superclass: ");
		context.append(subClass.getBrowserText());
		context.append(" (subclass of ");
		context.append(superClass.getBrowserText());
		context.append(")");
        
        ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Superclass_Removed, subClass, context.toString());
	}

}
