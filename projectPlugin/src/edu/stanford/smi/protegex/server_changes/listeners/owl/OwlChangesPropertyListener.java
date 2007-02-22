package edu.stanford.smi.protegex.server_changes.listeners.owl;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.PropertyAdapter;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

public class OwlChangesPropertyListener extends PropertyAdapter{
    private OWLModel om;
    private KnowledgeBase changesKb;

    public OwlChangesPropertyListener(OWLModel om) {
        this.om = om;
        changesKb = ChangesProject.getChangesKB(om);
    }


	public void subpropertyAdded(RDFProperty arg0, RDFProperty arg1) {
		StringBuffer context = new StringBuffer();
		context.append("Subproperty Added: ");
		context.append(arg0.getBrowserText());
		context.append(" (added to: ");
		context.append(arg1.getBrowserText());
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(
                                                                     om,
       
												changesKb,
												ServerChangesUtil.CHANGETYPE_SUBPROPERTY_ADDED,
												arg0.getBrowserText(), 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om, changesKb, changeInst);
	}

	public void subpropertyRemoved(RDFProperty arg0, RDFProperty arg1) {
		StringBuffer context = new StringBuffer();
		context.append("Subproperty Removed: ");
		context.append(arg0.getBrowserText());
		context.append(" (removed from: ");
		context.append(arg1.getBrowserText());
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(
                                                                     om,
												changesKb,
												ServerChangesUtil.CHANGETYPE_SUBPROPERTY_REMOVED,
												arg0.getBrowserText(), 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
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
												ServerChangesUtil.CHANGETYPE_SUPERPROPERTY_ADDED,
												arg0.getBrowserText(), 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

	public void superpropertyRemoved(RDFProperty arg0, RDFProperty arg1) {
		StringBuffer context = new StringBuffer();
		context.append("Superproperty Removed: ");
		context.append(arg0.getBrowserText());
		context.append(" (removed from: " );
		context.append(arg1.getBrowserText());
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(
                                                                     om,

												changesKb,
												ServerChangesUtil.CHANGETYPE_SUPERPROPERTY_REMOVED,
												arg0.getBrowserText(), 
												context.toString(),
												ServerChangesUtil.CHANGE_LEVEL_INFO);
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
												ServerChangesUtil.CHANGETYPE_DOMAIN_PROP_ADDED,
												clsName, 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

	public void unionDomainClassRemoved(RDFProperty arg0, RDFSClass arg1) {
		String propName = arg0.getBrowserText();
		String clsName = arg1.getBrowserText();
		
		StringBuffer context = new StringBuffer();
		context.append("Domain Property Removed: ");
		context.append(propName);
		context.append("(removed from: ");
		context.append(clsName);
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(om,
												changesKb,
												ServerChangesUtil.CHANGETYPE_DOMAIN_PROP_REMOVED,
												clsName, 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

}
