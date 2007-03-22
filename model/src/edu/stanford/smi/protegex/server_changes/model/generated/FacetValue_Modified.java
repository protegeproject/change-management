
// Created on Thu Mar 22 12:38:19 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.util.*;
import java.beans.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;


/** 
 */
public class FacetValue_Modified extends Property_Change {

	public FacetValue_Modified(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}

	public void setAssociatedFacet(Instance associatedFacet) {
		ModelUtilities.setOwnSlotValue(this, "associatedFacet", associatedFacet);	}
	public Instance getAssociatedFacet() {
		return ((Instance) ModelUtilities.getOwnSlotValue(this, "associatedFacet"));
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
