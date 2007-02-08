package edu.stanford.smi.protegex.server_changes.listeners.owl;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.server_changes.*;
import edu.stanford.smi.protegex.server_changes.util.Util;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;

public class OwlChangesModelListener extends ModelAdapter{

	public void classCreated(RDFSClass arg0) {
		String clsName = arg0.getBrowserText();
		String context = "Created Class: " + clsName;
        
		String frameId = arg0.getFrameID().toString();
		if (!Util.frameExists(frameId)) {
			Util.updateMap(frameId, clsName);	
		}
				
		Instance changeInst = ServerChangesUtil.createChange(
												ChangesProject.getChangesKB(),
												ServerChangesUtil.CHANGETYPE_CLASS_CREATED,
												clsName, 
												context, 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(changeInst);
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

		String frameId = arg0.getFrameID().toString();
		if (!Util.frameExists(frameId)) {
			Util.updateMap(frameId, propName);
		}

		Instance changeInst = ServerChangesUtil.createChange(
												ChangesProject.getChangesKB(),
												ServerChangesUtil.CHANGETYPE_PROPERTY_CREATED,
												propName, 
												context, 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(changeInst);
	}

	public void propertyDeleted(RDFProperty arg0) {
		String propName = Util.getName(arg0.getFrameID().toString());
		String context = "Property Deleted: " + propName;
		
		Instance changeInst = ServerChangesUtil.createChange(
												ChangesProject.getChangesKB(),
												ServerChangesUtil.CHANGETYPE_PROPERTY_DELETED,
												propName, 
												context, 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(changeInst);
	
	}

	public void resourceNameChanged(RDFResource arg0, String arg1) {
		String oldName = arg1;
		String newName = arg0.getBrowserText();
		String frameId = arg0.getFrameID().toString();

		StringBuffer context = new StringBuffer();
		context.append("Name change from '");
		context.append(oldName);
		context.append("' to '");
		context.append(newName);
		context.append("'");
		
		Util.updateMap(frameId, newName);
		
		Instance changeInst = ServerChangesUtil.createNameChange(
												ChangesProject.getChangesKB(),
												ServerChangesUtil.CHANGETYPE_NAME_CHANGED,
												newName, 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO, 
												oldName, 
												newName);
		ChangesProject.createChange(changeInst);
		
	}
}
