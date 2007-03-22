
// Created on Wed Mar 21 17:52:27 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.util.*;
import java.beans.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;


/** 
 */
public class SufficientCondition_Modified extends Class_Change {

	public SufficientCondition_Modified(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}

	public void setAssociatedRestriction(Instance associatedRestriction) {
		ModelUtilities.setOwnSlotValue(this, "associatedRestriction", associatedRestriction);	}
	public Instance getAssociatedRestriction() {
		return ((Instance) ModelUtilities.getOwnSlotValue(this, "associatedRestriction"));
	}

	public void setNewValue(String newValue) {
		ModelUtilities.setOwnSlotValue(this, "newValue", newValue);	}
	public String getNewValue() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "newValue"));
	}

	public void setOldValue(String oldValue) {
		ModelUtilities.setOwnSlotValue(this, "oldValue", oldValue);	}
	public String getOldValue() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "oldValue"));
	}
// __Code above is automatically generated. Do not change
}
