
// Created on Wed Mar 21 17:52:27 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.util.*;
import java.beans.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;


/** 
 */
public abstract class Change extends AnnotatableThing {

	public Change(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}

	public void setAssociatedAnnotations(Collection associatedAnnotations) {
		ModelUtilities.setOwnSlotValues(this, "associatedAnnotations", associatedAnnotations);	}
	public Collection getAssociatedAnnotations(){
		return  ModelUtilities.getOwnSlotValues(this, "associatedAnnotations");
	}

	public void setAction(String action) {
		ModelUtilities.setOwnSlotValue(this, "action", action);	}
	public String getAction() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "action"));
	}

	public void setApplyTo(Instance applyTo) {
		ModelUtilities.setOwnSlotValue(this, "applyTo", applyTo);	}
	public Instance getApplyTo() {
		return ((Instance) ModelUtilities.getOwnSlotValue(this, "applyTo"));
	}

	public void setAuthor(String author) {
		ModelUtilities.setOwnSlotValue(this, "author", author);	}
	public String getAuthor() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "author"));
	}

	public void setContext(String context) {
		ModelUtilities.setOwnSlotValue(this, "context", context);	}
	public String getContext() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "context"));
	}

	public void setPartOfCompositeChange(Instance partOfCompositeChange) {
		ModelUtilities.setOwnSlotValue(this, "partOfCompositeChange", partOfCompositeChange);	}
	public Instance getPartOfCompositeChange() {
		return ((Instance) ModelUtilities.getOwnSlotValue(this, "partOfCompositeChange"));
	}

	public void setTimestamp(Instance timestamp) {
		ModelUtilities.setOwnSlotValue(this, "timestamp", timestamp);	}
	public Instance getTimestamp() {
		return ((Instance) ModelUtilities.getOwnSlotValue(this, "timestamp"));
	}
// __Code above is automatically generated. Do not change
}
