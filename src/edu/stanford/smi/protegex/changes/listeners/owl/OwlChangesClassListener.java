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
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
 
public class OwlChangesClassListener extends ClassAdapter{

	public void instanceAdded(RDFSClass arg0, RDFResource arg1) {
		String instName = arg1.getBrowserText();
		StringBuffer context = new StringBuffer();
		context.append("Instance Added: ");
		context.append(instName);
		context.append(" (instance of: " );
		context.append(arg0.getBrowserText() );
		context.append(")");
		
		// Update frames map
		String frameId = arg0.getFrameID().toString();
		if (!Util.frameExists(frameId)) {
			Util.updateMap(frameId, instName);	
		}
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_INSTANCE_ADDED,
												instName, 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
	}

	public void instanceRemoved(RDFSClass arg0, RDFResource arg1) {
		String instName = Util.getName(arg1.getFrameID().toString());
		StringBuffer context = new StringBuffer();
		context.append("Instance Removed: ");
		context.append(instName);
		context.append(" (instance of: ");
		context.append(arg0.getBrowserText());
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_INSTANCE_REMOVED,
												instName, 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
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
		String frameId = arg1.getFrameID().toString();
		if (!Util.frameExists(frameId)) {
			Util.updateMap(frameId, clsName);	
		}
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_SUBCLASS_ADDED,
												clsName, 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
	}

	public void subclassRemoved(RDFSClass arg0, RDFSClass arg1) {
		String clsName = Util.getName(arg1.getFrameID().toString());
		StringBuffer context = new StringBuffer();
		context.append("Subclass Removed: ");
		context.append(clsName);
		context.append(" (removed from: ");
		context.append(arg0.getBrowserText());
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_SUBCLASS_REMOVED,
												clsName, 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
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
		String frameId = arg1.getFrameID().toString();
		if (!Util.frameExists(frameId)) {
			Util.updateMap(frameId, clsName);	
		}
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_SUPERCLASS_ADDED,
												clsName, 
												context.toString(),
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
	}

	public void superclassRemoved(RDFSClass arg0, RDFSClass arg1) {
		String clsName = Util.getName(arg1.getFrameID().toString());
		StringBuffer context = new StringBuffer();
		context.append("Superclass Removed: ");
		context.append(clsName);
		context.append(" (removed from: ");
		context.append(arg0.getBrowserText());
		context.append(")");
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_SUPERCLASS_REMOVED,
												clsName, 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		ChangesTab.createChange(changeInst);
	}
}
