
// Created on Mon Mar 19 14:22:16 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import edu.stanford.smi.protege.model.DefaultSimpleInstance;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.ModelUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.server_changes.model.ChangeDateComparator;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;


/** 
 */
public abstract class Change extends AnnotatableThing {

	public Change() {
	}


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
