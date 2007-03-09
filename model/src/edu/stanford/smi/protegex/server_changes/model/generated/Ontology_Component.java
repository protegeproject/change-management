
// Created on Wed Mar 07 13:03:39 PST 2007
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
public class Ontology_Component extends AnnotatableThing {

	public Ontology_Component() {
	}


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
    
    public enum Status {
        CREATED, DELETED, CREATED_AND_DELETED, UNCHANGED, CHANGED
    }
    
    private final static String ANONYMOUS_NAME_PREFIX = "@";
    
    @SuppressWarnings("unchecked")
    public Status getStatus() {
        List<Instance> changes = getSortedChanges();
        if (changes == null || changes.isEmpty()) {
            return Status.UNCHANGED;
        }
        else {
            boolean created = false;
            boolean deleted = false;
            // Can't assume that the deleted change is last 
            //     - there might be a composite change following
            for (Instance change : changes) {
                if (change instanceof Created_Change) {
                    created = true;
                }
                if (change instanceof Deleted_Change) {
                    deleted = true;
                    break;
                }
            }
            if (created && deleted) {
                return Status.CREATED_AND_DELETED;
            }
            else if (created) {
                return Status.CREATED;
            }
            else if (deleted) {
                return Status.DELETED;
            }
            else {
                return Status.CHANGED;
            }
        }
        
    }
    
    public String toString() {
        switch (getStatus()) {
        case CHANGED:
            return "Modified Object: " + getCurrentName();
        case CREATED:
            return "New Object: " + getCurrentName();
        case DELETED:
            return "Deleted Object: " + getInitialName();
        case CREATED_AND_DELETED:
            return "Created and Deleted Object";
        case UNCHANGED:
            return "Unchanged Object: " + getCurrentName();
        default:
            throw new RuntimeException("Developer missed a case");
        }
    }
    
    public String getInitialName() {
        List<Instance> changes = getSortedChanges();
        if (changes.get(0) instanceof Created_Change) {
            return null;
        }
        Collections.reverse(changes);
        String name = getCurrentName();
        for (Instance i : changes) {
            if (i instanceof Deleted_Change) {
                Deleted_Change change = (Deleted_Change) i;
                name = change.getDeletionName();
            }
            else if (i instanceof Name_Changed) {
                Name_Changed change = (Name_Changed) i;
                name = change.getOldName();
            }
            else if (i instanceof Created_Change) {
                Created_Change change = (Created_Change) i;
                return null;
            }
        }
        return name;
    }
    
    public List<Instance> getSortedChanges() {
        List<Instance> changes = new ArrayList<Instance>(getChanges());
        Collections.sort(changes, new ChangeDateComparator(getKnowledgeBase()));
        return changes;
    }
    
    public List<Instance> getSortedTopLevelChanges(){
    	List<Instance> topLevelChanges = new ArrayList<Instance>();
    	List<Instance> changes = new ArrayList<Instance>(getSortedChanges());
    	
    	for (Instance i : changes) {
			Change change = (Change) i;
			
			Instance compositeChange = change.getPartOfCompositeChange();
			
			if (compositeChange == null) {
				topLevelChanges.add(change);
			}
		}
    	
    	return topLevelChanges;
    }
    
    public String mostRecentName() {
    	String name = getCurrentName();
    	if (name != null) { return name; }
    	List<Instance> changes = getSortedChanges();
    	Collections.reverse(changes);
    	for (Instance i : changes) {
    		if (i instanceof Deleted_Change) {
    			Deleted_Change change = (Deleted_Change) i;
    			name = change.getDeletionName();
    			if (!name.contains("<<missing frame name")) return name;  // TODO -- TR -- FIX ME
    		}
    	}
    	return null;
    }
    
    
    public boolean isAnonymous() { 
    	String name = mostRecentName();
    	if (name != null) {
    		return name.startsWith(ANONYMOUS_NAME_PREFIX);
    	}
		Log.getLogger().warning("Could not determine anonymous status of " + this);
		return true;
    }
}
