
// Created on Thu Mar 22 12:38:19 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.util.*;
import java.beans.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;


/** 
 */
public class Restriction_Added extends Class_Change {

	public Restriction_Added(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}

	public void setAssociatedRestriction(Instance associatedRestriction) {
		ModelUtilities.setOwnSlotValue(this, "associatedRestriction", associatedRestriction);	}
	public Instance getAssociatedRestriction() {
		return ((Instance) ModelUtilities.getOwnSlotValue(this, "associatedRestriction"));
	}
// __Code above is automatically generated. Do not change
}
