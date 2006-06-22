package edu.stanford.smi.protegex.changes.listeners.owl;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.changes.ChangeCreateUtil;
import edu.stanford.smi.protegex.changes.ChangesTab;
import edu.stanford.smi.protegex.changes.owl.Util;
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
				
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_CLASS_CREATED,
												clsName, 
												context, 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
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

		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_PROPERTY_CREATED,
												propName, 
												context, 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
	}

	public void propertyDeleted(RDFProperty arg0) {
		String propName = Util.getName(arg0.getFrameID().toString());
		String context = "Property Deleted: " + propName;
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_PROPERTY_DELETED,
												propName, 
												context, 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
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
		
		Instance changeInst = ChangeCreateUtil.createNameChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_NAME_CHANGED,
												newName, 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO, 
												oldName, 
												newName);
		ChangesTab.createChange(changeInst);
	}
}
