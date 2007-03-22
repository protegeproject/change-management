
// Created on Wed Mar 21 17:52:27 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.util.*;
import java.beans.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;


/** 
 */
public class FacetValue_Added extends Property_Change {

	public FacetValue_Added(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}

	public void setValue(String value) {
		ModelUtilities.setOwnSlotValue(this, "value", value);	}
	public String getValue() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "value"));
	}
// __Code above is automatically generated. Do not change
}
