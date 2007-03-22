
// Created on Thu Mar 22 12:38:19 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.util.*;
import java.beans.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;


/** 
 */
public abstract class Created_Change extends Change {

	public Created_Change(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}

	public void setCreationName(String creationName) {
		ModelUtilities.setOwnSlotValue(this, "creationName", creationName);	}
	public String getCreationName() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "creationName"));
	}
// __Code above is automatically generated. Do not change
}
