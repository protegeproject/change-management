
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
public abstract class Annotation extends AnnotatableThing {

	public Annotation() {
	}


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
