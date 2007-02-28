package edu.stanford.smi.protegex.changes;

import java.util.Collection;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.server_changes.model.Model;

public class ChangeCreateUtil {

	private ChangeCreateUtil() {}
    
    public static String getActionDisplay(KnowledgeBase cKb, Instance aInst) {
        String actionStr = (String) aInst.getOwnSlotValue(cKb.getSlot(Model.SLOT_NAME_ACTION));
        return actionStr.replace('_', ' ');
    }
    

	
	
	public static Instance createAnnotation(KnowledgeBase cKb, String annotType,Collection changeInsts) {
		Cls annotate = cKb.getCls(annotType);
		Slot annotates = cKb.getSlot(Model.SLOT_NAME_ANNOTATES);
		//Slot title = cKb.getSlot(Model.SLOT_NAME_TITLE);
		
		Instance annotateInst = cKb.createInstance(null, annotate);
		annotateInst.setOwnSlotValues(annotates, changeInsts);
		//annotateInst.setOwnSlotValue(title, annotateInst.getName());
		
		return annotateInst;
	}
	
	public static Instance updateAnnotation(KnowledgeBase cKb, Instance annotateInst) {
		
		Slot created = cKb.getSlot(Model.SLOT_NAME_CREATED);
		Slot author = cKb.getSlot(Model.SLOT_NAME_AUTHOR);
		Slot modified = cKb.getSlot(Model.SLOT_NAME_MODIFIED);
		Slot body = cKb.getSlot(Model.SLOT_NAME_BODY);
		
		annotateInst.setOwnSlotValue(created, ChangesTab.getTimeStamp());
		annotateInst.setOwnSlotValue(modified, ChangesTab.getTimeStamp());
		annotateInst.setOwnSlotValue(author, cKb.getUserName());
		
		// If no comments are added, add empty string as comment
		Object bdy = annotateInst.getOwnSlotValue(body);
		if (bdy == null) {
			annotateInst.setOwnSlotValue(body, "");
		}
		
		return annotateInst;	
	}
	
	
}
