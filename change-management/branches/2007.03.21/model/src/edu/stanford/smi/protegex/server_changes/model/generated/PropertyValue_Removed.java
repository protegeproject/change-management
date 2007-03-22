
// Created on Thu Mar 22 12:38:19 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.util.*;
import java.beans.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;


/** 
 */
public class PropertyValue_Removed extends PropertyValue_Change {

	public PropertyValue_Removed(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}

	public void setValue(String value) {
		ModelUtilities.setOwnSlotValue(this, "value", value);	}
	public String getValue() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "value"));
	}
// __Code above is automatically generated. Do not change
}
