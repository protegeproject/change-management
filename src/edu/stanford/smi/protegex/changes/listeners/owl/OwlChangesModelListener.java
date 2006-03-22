/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License");  you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is Protege-2000.
 *
 * The Initial Developer of the Original Code is Stanford University. Portions
 * created by Stanford University are Copyright (C) 2005.  All Rights Reserved.
 *
 * Protege was developed by Stanford Medical Informatics
 * (http://www.smi.stanford.edu) at the Stanford University School of Medicine
 * with support from the National Library of Medicine, the National Science
 * Foundation, and the Defense Advanced Research Projects Agency.  Current
 * information about Protege can be obtained at http://protege.stanford.edu.
 *
 */

package edu.stanford.smi.protegex.changes.listeners.owl;

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
		String context = "Class created: " + clsName;

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
		String frameID = arg0.getFrameID().toString();
		String clsName = Util.getName(frameID);
		String context = "Class deleted: " + clsName;
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_CLASS_DELETED,
												clsName, 
												context, 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
	}

	public void individualCreated(RDFResource arg0) {
	}

	public void individualDeleted(RDFResource arg0) {
	}

	public void propertyCreated(RDFProperty arg0) {
		String propName = arg0.getBrowserText();
		String context = "Property created: " + propName;

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
		String context = "Property deleted: " + propName;
		
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
