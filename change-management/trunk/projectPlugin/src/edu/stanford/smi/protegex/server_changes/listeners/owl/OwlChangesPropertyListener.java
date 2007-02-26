package edu.stanford.smi.protegex.server_changes.listeners.owl;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.PropertyAdapter;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.Model;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

public class OwlChangesPropertyListener extends PropertyAdapter{
    private OWLModel om;
    private KnowledgeBase changesKb;
    private ChangesDb changesDb;

    public OwlChangesPropertyListener(OWLModel om) {
        this.om = om;
        changesKb = ChangesProject.getChangesKB(om);
        changesDb = ChangesProject.getChangesDb(om);
    }


	public void subpropertyAdded(RDFProperty arg0, RDFProperty arg1) {
		StringBuffer context = new StringBuffer();
		context.append("Subproperty Added: ");
		context.append(arg0.getBrowserText());
		context.append(" (added to: ");
		context.append(arg1.getBrowserText());
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(om,
		                                                     changesKb,
		                                                     Model.CHANGETYPE_SUBPROPERTY_ADDED,
		                                                     arg0.getBrowserText(), 
		                                                     context.toString(), 
		                                                     Model.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om, changesKb, changeInst);
	}

	public void subpropertyRemoved(RDFProperty arg0, RDFProperty arg1) {
        String browserText0 = changesDb.getPossiblyDeletedBrowserText(arg0);
        
		StringBuffer context = new StringBuffer();
		context.append("Subproperty Removed: ");
		context.append(browserText0);
		context.append(" (removed from: ");
		context.append(arg1.getBrowserText());
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(om,
		                                                     changesKb,
		                                                     Model.CHANGETYPE_SUBPROPERTY_REMOVED,
		                                                     browserText0, 
		                                                     context.toString(), 
		                                                     Model.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

	public void superpropertyAdded(RDFProperty arg0, RDFProperty arg1) {
		StringBuffer context = new StringBuffer();
		context.append("Superproperty Added: ");
		context.append(arg0.getBrowserText());
		context.append(" (added to: ");
		context.append(arg1.getBrowserText());
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(
                                                                     om,
												changesKb,
												Model.CHANGETYPE_SUPERPROPERTY_ADDED,
												arg0.getBrowserText(), 
												context.toString(), 
												Model.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

	public void superpropertyRemoved(RDFProperty arg0, RDFProperty arg1) {
        String browserText0 = changesDb.getPossiblyDeletedBrowserText(arg0);
        String browserText1 = changesDb.getPossiblyDeletedBrowserText(arg1);
		StringBuffer context = new StringBuffer();
		context.append("Superproperty Removed: ");
		context.append(browserText0);
		context.append(" (removed from: " );
		context.append(browserText1);
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(om,
		                                                     changesKb,
		                                                     Model.CHANGETYPE_SUPERPROPERTY_REMOVED,
		                                                     browserText0, 
		                                                     context.toString(),
		                                                     Model.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

	public void unionDomainClassAdded(RDFProperty arg0, RDFSClass arg1) {
		String propName = arg0.getBrowserText();
		String clsName = arg1.getBrowserText();
		
		StringBuffer context = new StringBuffer();
		context.append("Domain Property Added: ");
		context.append(propName);
		context.append("(added to: ");
		context.append(clsName);
		context.append(")");
				
		Instance changeInst = ServerChangesUtil.createChange(om,
												changesKb,
												Model.CHANGETYPE_DOMAIN_PROP_ADDED,
												clsName, 
												context.toString(), 
												Model.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

	public void unionDomainClassRemoved(RDFProperty arg0, RDFSClass arg1) {
		String propName = changesDb.getPossiblyDeletedBrowserText(arg0);
		String clsName = changesDb.getPossiblyDeletedBrowserText(arg1);
		
		StringBuffer context = new StringBuffer();
		context.append("Domain Property Removed: ");
		context.append(propName);
		context.append("(removed from: ");
		context.append(clsName);
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(om,
												changesKb,
												Model.CHANGETYPE_DOMAIN_PROP_REMOVED,
												clsName, 
												context.toString(), 
												Model.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

}
