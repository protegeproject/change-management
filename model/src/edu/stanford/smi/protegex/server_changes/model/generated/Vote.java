
// Created on Wed Mar 21 17:52:27 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.util.*;
import java.beans.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;


/** 
 */
public abstract class Vote extends Annotation {

	public Vote(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}

	public void setVoteValue(String voteValue) {
		ModelUtilities.setOwnSlotValue(this, "voteValue", voteValue);	}
	public String getVoteValue() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "voteValue"));
	}
// __Code above is automatically generated. Do not change
}
