
// Created on Sat Mar 03 09:41:37 PST 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Collection;
import edu.stanford.smi.protege.model.DefaultSimpleInstance;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.ModelUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;


/** 
 */
public class Ontology_Component extends AnnotatableThing {

	public Ontology_Component(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}

	public void setChanges(Collection changes) {
		ModelUtilities.setOwnSlotValues(this, "changes", changes);	}
	public Collection getChanges(){
		return  ModelUtilities.getOwnSlotValues(this, "changes");
	}

	public void setCurrentName(String currentName) {
		ModelUtilities.setOwnSlotValue(this, "currentName", currentName);	}
	public String getCurrentName() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "currentName"));
	}
// __Code above is automatically generated. Do not change
}
