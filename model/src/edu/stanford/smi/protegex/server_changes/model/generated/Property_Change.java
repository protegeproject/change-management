
// Created on Wed Mar 21 17:52:27 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.util.*;
import java.beans.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;


/** 
 */
public abstract class Property_Change extends Change {

	public Property_Change(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}

	public void setAssociatedProperty(Instance associatedProperty) {
		ModelUtilities.setOwnSlotValue(this, "associatedProperty", associatedProperty);	}
	public Instance getAssociatedProperty() {
		return ((Instance) ModelUtilities.getOwnSlotValue(this, "associatedProperty"));
	}
// __Code above is automatically generated. Do not change
}
