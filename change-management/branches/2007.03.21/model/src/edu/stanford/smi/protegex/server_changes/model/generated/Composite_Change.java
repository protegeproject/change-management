
// Created on Thu Mar 22 12:38:19 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.util.*;
import java.beans.*;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.*;


/** 
 */
public class Composite_Change extends Change {

	public Composite_Change(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}

	public void setSubChanges(Collection subChanges) {
		ModelUtilities.setOwnSlotValues(this, "subChanges", subChanges);	}
	public Collection getSubChanges(){
		return  ModelUtilities.getOwnSlotValues(this, "subChanges");
	}
// __Code above is automatically generated. Do not change
	
	public List<Change> getAllSubChanges() {
		return getAllSubChanges(this);
	}


	private List<Change> getAllSubChanges(Change change) {
		ArrayList<Change> allSubChanges = new ArrayList<Change>();
		
		allSubChanges.add(change);
		
		if (!(change instanceof Composite_Change)) {
			return allSubChanges;
		}
		
		for (Iterator iter = ((Composite_Change)change).getSubChanges().iterator(); iter.hasNext();) {
			Change subChange = (Change) iter.next();
			allSubChanges.addAll(getAllSubChanges(subChange));
		}
		
		return allSubChanges;
	}
	
}
