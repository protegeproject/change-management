package edu.stanford.smi.protegex.changes.listeners.owl;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.changes.ChangeCreateUtil;
import edu.stanford.smi.protegex.changes.ChangesTab;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.PropertyAdapter;

public class OwlChangesPropertyListener extends PropertyAdapter{

	public void subpropertyAdded(RDFProperty arg0, RDFProperty arg1) {
		StringBuffer context = new StringBuffer();
		context.append("Subproperty Added: ");
		context.append(arg0.getBrowserText());
		context.append(" (added to: ");
		context.append(arg1.getBrowserText());
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_SUBPROPERTY_ADDED,
												arg0.getBrowserText(), 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
	}

	public void subpropertyRemoved(RDFProperty arg0, RDFProperty arg1) {
		StringBuffer context = new StringBuffer();
		context.append("Subproperty Removed: ");
		context.append(arg0.getBrowserText());
		context.append(" (removed from: ");
		context.append(arg1.getBrowserText());
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_SUBPROPERTY_REMOVED,
												arg0.getBrowserText(), 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
	}

	public void superpropertyAdded(RDFProperty arg0, RDFProperty arg1) {
		StringBuffer context = new StringBuffer();
		context.append("Superproperty Added: ");
		context.append(arg0.getBrowserText());
		context.append(" (added to: ");
		context.append(arg1.getBrowserText());
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_SUPERPROPERTY_ADDED,
												arg0.getBrowserText(), 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
	}

	public void superpropertyRemoved(RDFProperty arg0, RDFProperty arg1) {
		StringBuffer context = new StringBuffer();
		context.append("Superproperty Removed: ");
		context.append(arg0.getBrowserText());
		context.append(" (removed from: " );
		context.append(arg1.getBrowserText());
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_SUPERPROPERTY_REMOVED,
												arg0.getBrowserText(), 
												context.toString(),
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
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
				
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_DOMAIN_PROP_ADDED,
												clsName, 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
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
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_DOMAIN_PROP_REMOVED,
												clsName, 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
	}

}
