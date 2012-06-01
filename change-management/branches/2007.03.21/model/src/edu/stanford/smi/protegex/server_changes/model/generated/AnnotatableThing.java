
// Created on Thu Mar 22 12:38:19 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.util.*;
import java.beans.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;


/** 
 */
public abstract class AnnotatableThing extends DefaultSimpleInstance {

	public AnnotatableThing(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}

	public void setAssociatedAnnotations(Collection associatedAnnotations) {
		ModelUtilities.setOwnSlotValues(this, "associatedAnnotations", associatedAnnotations);	}
	public Collection getAssociatedAnnotations(){
		return  ModelUtilities.getOwnSlotValues(this, "associatedAnnotations");
	}
// __Code above is automatically generated. Do not change
	
	
}