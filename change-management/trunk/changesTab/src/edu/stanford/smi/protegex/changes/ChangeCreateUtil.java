package edu.stanford.smi.protegex.changes;

import java.util.Collection;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeSlot;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;

public class ChangeCreateUtil {
    private ChangeModel model;
    private KnowledgeBase cKb;

	public ChangeCreateUtil(ChangeModel model) {
	    this.model = model;
        cKb = model.getChangeKb();
    }
    
    public static String getActionDisplay(Change aInst) {
        String actionStr = aInst.getAction();
        
        //This should not be the case
        if (actionStr == null) {
        	return "No Action";
        }
        
        return actionStr.replace('_', ' ');
    }
    
@Deprecated 
/**
 * I don't think that this method will work right - see the AbstractChangeListener & ChangeDb.createAnnotation
 */
	public Annotation createAnnotation(String annotType,Collection changeInsts) {
		Cls annotate = cKb.getCls(annotType);
		Slot annotates = model.getSlot(ChangeSlot.annotates);
		
		Annotation annotateInst = (Annotation) cKb.createInstance(null, annotate);
		annotateInst.setOwnSlotValues(annotates, changeInsts);
		//annotateInst.setOwnSlotValue(title, annotateInst.getName());
		
		return annotateInst;
	}
	
@Deprecated 
/**
 * I don't think that this method will work right - see the AbstractChangeListener & ChangeDb.createAnnotation
 */
	public Annotation updateAnnotation(Annotation annotateInst) {
        Timestamp now = Timestamp.getTimestamp(model);
        annotateInst.setCreated(now);
        annotateInst.setModified(now);
        annotateInst.setAuthor(cKb.getUserName());
		
		// If no comments are added, add empty string as comment
		if (annotateInst.getBody() == null) {
            annotateInst.setBody("");
		}
		
		return annotateInst;	
	}
	
	
}
