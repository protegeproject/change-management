package edu.stanford.smi.protegex.server_changes.listeners.owl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.util.Util;
 
public class OwlChangesClassListener extends ClassAdapter{
    private OWLModel om;
    private KnowledgeBase changesKb;
    
    public OwlChangesClassListener(OWLModel om) {
        this.om = om;
        changesKb = ChangesProject.getChangesKB(om);
    }

	public void instanceAdded(RDFSClass arg0, RDFResource arg1) {
		String instName = arg1.getBrowserText();
		StringBuffer context = new StringBuffer();
		context.append("Instance Added: ");
		context.append(instName);
		context.append(" (instance of: " );
		context.append(arg0.getBrowserText() );
		context.append(")");
		
		// Update frames map
        ChangesDb changesDb = ChangesProject.getChangesDb(om);
		Instance changeInst = ServerChangesUtil.createChange(om,
												changesKb,
												ServerChangesUtil.CHANGETYPE_INSTANCE_ADDED,
												instName, 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

	public void instanceRemoved(RDFSClass arg0, RDFResource arg1) {
        ChangesDb changesDb = ChangesProject.getChangesDb(om);
		String instName = changesDb.getName(arg1);
		StringBuffer context = new StringBuffer();
		context.append("Instance Removed: ");
		context.append(instName);
		context.append(" (instance of: ");
		context.append(arg0.getBrowserText());
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(om,
												changesKb,
												ServerChangesUtil.CHANGETYPE_INSTANCE_REMOVED,
												instName, 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

	public void addedToUnionDomainOf(RDFSClass arg0, RDFProperty arg1) {
	}

	public void removedFromUnionDomainOf(RDFSClass arg0, RDFProperty arg1) {
	}

	public void subclassAdded(RDFSClass arg0, RDFSClass arg1) {
		String clsName = arg1.getBrowserText();
		StringBuffer context = new StringBuffer();
		context.append("Subclass Added: ");
		context.append(clsName);
		context.append(" (added to: ");
		context.append(arg0.getBrowserText());
		context.append(")");
		
		// Update frames map
        ChangesDb changesDb = ChangesProject.getChangesDb(om);
		
		Instance changeInst = ServerChangesUtil.createChange(om,
												changesKb,
												ServerChangesUtil.CHANGETYPE_SUBCLASS_ADDED,
												clsName, 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

	public void subclassRemoved(RDFSClass arg0, RDFSClass arg1) {
        ChangesDb changesDb = ChangesProject.getChangesDb(om);
		String clsName = changesDb.getName(arg1);
		StringBuffer context = new StringBuffer();
		context.append("Subclass Removed: ");
		context.append(clsName);
		context.append(" (removed from: ");
		context.append(arg0.getBrowserText());
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(om,
												changesKb,
												ServerChangesUtil.CHANGETYPE_SUBCLASS_REMOVED,
												clsName, 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

	public void superclassAdded(RDFSClass arg0, RDFSClass arg1) {
		String clsName = arg1.getBrowserText();
		StringBuffer context = new StringBuffer();
		context.append("Superclass Added: ");
		context.append(clsName);
		context.append(" (added to: ");
		context.append(arg0.getBrowserText());
		context.append(")");
		
		// Update frames map
        ChangesDb changesDb = ChangesProject.getChangesDb(om);
		
		Instance changeInst = ServerChangesUtil.createChange(om,
												changesKb,
												ServerChangesUtil.CHANGETYPE_SUPERCLASS_ADDED,
												clsName, 
												context.toString(),
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

	public void superclassRemoved(RDFSClass arg0, RDFSClass arg1) {
		String a = arg1.getName();
		String b = arg1.getBrowserText();
        ChangesDb changesDb = ChangesProject.getChangesDb(om);
		String clsName = changesDb.getName(arg1);
		StringBuffer context = new StringBuffer();
		context.append("Superclass Removed: ");
		context.append(clsName);
		context.append(" (removed from: ");
		context.append(arg0.getBrowserText());
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(om,
												changesKb,
												ServerChangesUtil.CHANGETYPE_SUPERCLASS_REMOVED,
												clsName, 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}
}
