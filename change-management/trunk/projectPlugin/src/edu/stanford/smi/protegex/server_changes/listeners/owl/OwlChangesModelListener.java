package edu.stanford.smi.protegex.server_changes.listeners.owl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.util.Util;

public class OwlChangesModelListener extends ModelAdapter{
    private OWLModel om;
    private KnowledgeBase changesKb;
    
    
    public OwlChangesModelListener(OWLModel om) {
        this.om = om;
        changesKb = ChangesProject.getChangesKB(om);
    }

	public void classCreated(RDFSClass arg0) {
		String clsName = arg0.getBrowserText();
		String context = "Created Class: " + clsName;
        
        ChangesDb changesDb = ChangesProject.getChangesDb(om);
				
		Instance changeInst = ServerChangesUtil.createChange(om,
												changesKb,
												ServerChangesUtil.CHANGETYPE_CLASS_CREATED,
												clsName, 
												context, 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

	
	

	public void classDeleted(RDFSClass arg0) {
//		
//		String frameID = arg0.getFrameID().toString();
//
//		String clsName = Util.getName(frameID);
//		
//		
//		String context = "Class deleted: " + clsName;
//		
//		Instance changeInst = ChangeCreateUtil.createChange(
//												ChangesTab.getChangesKB(),
//												ChangeCreateUtil.CHANGETYPE_CLASS_DELETED,
//												clsName, 
//				               				    context, 
//												ChangeCreateUtil.CHANGE_LEVEL_INFO);
//		ChangesTab.createChange(changeInst);
	}

	public void individualCreated(RDFResource arg0) {
	}

	public void individualDeleted(RDFResource arg0) {
	}

	public void propertyCreated(RDFProperty arg0) {
		String propName = arg0.getBrowserText();
		String context = "Property Created: " + propName;

        ChangesDb changesDb = ChangesProject.getChangesDb(om);

		Instance changeInst = ServerChangesUtil.createChange(om,
												changesKb,
												ServerChangesUtil.CHANGETYPE_PROPERTY_CREATED,
												propName, 
												context, 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	}

	public void propertyDeleted(RDFProperty arg0) {
        ChangesDb changesDb = ChangesProject.getChangesDb(om);
		String propName = changesDb.getName(arg0);
		String context = "Property Deleted: " + propName;
		
		Instance changeInst = ServerChangesUtil.createChange(om,
												changesKb,
												ServerChangesUtil.CHANGETYPE_PROPERTY_DELETED,
												propName, 
												context, 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(om,changesKb, changeInst);
	
	}

	public void resourceNameChanged(RDFResource arg0, String arg1) {
		String oldName = arg1;
		String newName = arg0.getBrowserText();

		StringBuffer context = new StringBuffer();
		context.append("Name change from '");
		context.append(oldName);
		context.append("' to '");
		context.append(newName);
		context.append("'");
		
        ChangesDb changesDb = ChangesProject.getChangesDb(om);
		
		Instance changeInst = ServerChangesUtil.createNameChange(om,
												changesKb,
												ServerChangesUtil.CHANGETYPE_NAME_CHANGED,
												newName, 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO, 
												oldName, 
												newName);
		ChangesProject.createChange(om, changesKb, changeInst);
		
	}
}
