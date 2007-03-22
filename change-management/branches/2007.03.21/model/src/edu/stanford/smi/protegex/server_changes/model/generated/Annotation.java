
// Created on Thu Mar 22 12:38:19 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.util.*;
import java.beans.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;


/** 
 */
public abstract class Annotation extends AnnotatableThing {

	public Annotation(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}

	public void setAnnotates(Collection annotates) {
		ModelUtilities.setOwnSlotValues(this, "annotates", annotates);	}
	public Collection getAnnotates(){
		return  ModelUtilities.getOwnSlotValues(this, "annotates");
	}

	public void setAuthor(String author) {
		ModelUtilities.setOwnSlotValue(this, "author", author);	}
	public String getAuthor() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "author"));
	}

	public void setBody(String body) {
		ModelUtilities.setOwnSlotValue(this, "body", body);	}
	public String getBody() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "body"));
	}

	public void setContext(String context) {
		ModelUtilities.setOwnSlotValue(this, "context", context);	}
	public String getContext() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "context"));
	}

	public void setCreated(Instance created) {
		ModelUtilities.setOwnSlotValue(this, "created", created);	}
	public Instance getCreated() {
		return ((Instance) ModelUtilities.getOwnSlotValue(this, "created"));
	}

	public void setModified(Instance modified) {
		ModelUtilities.setOwnSlotValue(this, "modified", modified);	}
	public Instance getModified() {
		return ((Instance) ModelUtilities.getOwnSlotValue(this, "modified"));
	}

	public void setRelated(String related) {
		ModelUtilities.setOwnSlotValue(this, "related", related);	}
	public String getRelated() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "related"));
	}
// __Code above is automatically generated. Do not change
}
