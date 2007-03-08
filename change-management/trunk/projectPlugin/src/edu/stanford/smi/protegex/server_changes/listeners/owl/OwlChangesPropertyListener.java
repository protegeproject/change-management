package edu.stanford.smi.protegex.server_changes.listeners.owl;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.PropertyAdapter;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;

public class OwlChangesPropertyListener extends PropertyAdapter{
    private OWLModel om;
    private KnowledgeBase changesKb;
    private ChangesDb changes_db;

    public OwlChangesPropertyListener(OWLModel om) {
        this.om = om;
        changesKb = ChangesProject.getChangesKB(om);
        changes_db = ChangesProject.getChangesDb(om);
    }


	public void subpropertyAdded(RDFProperty arg0, RDFProperty arg1) {
		StringBuffer context = new StringBuffer();
		context.append("Subproperty Added: ");
		context.append(arg0.getBrowserText());
		context.append(" (added to: ");
		context.append(arg1.getBrowserText());
		context.append(")");
        
        ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Subproperty_Added, arg0, context.toString());
	}

	public void subpropertyRemoved(RDFProperty arg0, RDFProperty arg1) {
            String browserText0 = changes_db.getPossiblyDeletedBrowserText(arg0);
        
            StringBuffer context = new StringBuffer();
            context.append("Subproperty Removed: ");
            context.append(browserText0);
            context.append(" (removed from: ");
            context.append(arg1.getBrowserText());
            context.append(")");
            
            ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Subproperty_Removed, arg0, context.toString());

	}

	public void superpropertyAdded(RDFProperty arg0, RDFProperty arg1) {
		StringBuffer context = new StringBuffer();
		context.append("Superproperty Added: ");
		context.append(arg0.getBrowserText());
		context.append(" (added to: ");
		context.append(arg1.getBrowserText());
		context.append(")");
        
        ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Superproperty_Added, arg0, context.toString());
	}

	public void superpropertyRemoved(RDFProperty arg0, RDFProperty arg1) {
        String browserText0 = changes_db.getPossiblyDeletedBrowserText(arg0);
        String browserText1 = changes_db.getPossiblyDeletedBrowserText(arg1);
		StringBuffer context = new StringBuffer();
		context.append("Superproperty Removed: ");
		context.append(browserText0);
		context.append(" (removed from: " );
		context.append(browserText1);
		context.append(")");
		
        ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Superproperty_Added, arg0, context.toString());

	}

	public void unionDomainClassAdded(RDFProperty arg0, RDFSClass arg1) {
		String propText = arg0.getBrowserText();
		String propName = arg0.getName();
		String clsName = arg1.getName();
		String clsText = arg1.getBrowserText();
		
		StringBuffer context = new StringBuffer();
		context.append("Domain Property Added: ");
		context.append(propText);
		context.append("(added to: ");
		context.append(clsText);
		context.append(")");
        
        ServerChangesUtil.createChangeStd(changes_db, ChangeCls.DomainProperty_Added, arg0, context.toString());

	}

	public void unionDomainClassRemoved(RDFProperty arg0, RDFSClass arg1) {
		String propText = changes_db.getPossiblyDeletedBrowserText(arg0);
		String clsText = changes_db.getPossiblyDeletedBrowserText(arg1);
		
		StringBuffer context = new StringBuffer();
		context.append("Domain Property Removed: ");
		context.append(propText);
		context.append("(removed from: ");
		context.append(clsText);
		context.append(")");
        
        ServerChangesUtil.createChangeStd(changes_db, ChangeCls.DomainProperty_Removed, arg0, context.toString());

	}

}
