
// Created on Wed Mar 21 17:52:27 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.util.*;
import java.beans.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;


/** 
 */
public class Name_Changed extends Change {

	public Name_Changed(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}

	public void setNewName(String newName) {
		ModelUtilities.setOwnSlotValue(this, "newName", newName);	}
	public String getNewName() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "newName"));
	}

	public void setOldName(String oldName) {
		ModelUtilities.setOwnSlotValue(this, "oldName", oldName);	}
	public String getOldName() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "oldName"));
	}
// __Code above is automatically generated. Do not change
}
