package edu.stanford.smi.protegex.changes;

import java.util.Collection;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

public class ChangeCreateUtil {

	private ChangeCreateUtil() {}
	
	public static String getActionDisplay(KnowledgeBase cKb, Instance aInst) {
		String actionStr = (String) aInst.getOwnSlotValue(cKb.getSlot(ServerChangesUtil.SLOT_NAME_ACTION));
		return actionStr.replace('_', ' ');
	}
	
	public static String getAction(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(ServerChangesUtil.SLOT_NAME_ACTION));
	}
	
	public static String getApplyTo(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(ServerChangesUtil.SLOT_NAME_APPLYTO));
	}
	
	public static String getAuthor(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(ServerChangesUtil.SLOT_NAME_AUTHOR));
	}
	
	public static String getCreated(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(ServerChangesUtil.SLOT_NAME_CREATED));
	}
	
	public static String getTitle(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(ServerChangesUtil.SLOT_NAME_TITLE));
	}
	
	public static Collection getChanges(KnowledgeBase cKb, Instance aInst) {
		return aInst.getOwnSlotValues(cKb.getSlot(ServerChangesUtil.SLOT_NAME_CHANGES));
	}
	
	public static Collection getAssocAnnotations(KnowledgeBase cKb, Instance aInst) {
		return aInst.getOwnSlotValues(cKb.getSlot(ServerChangesUtil.SLOT_NAME_ASSOC_ANNOTATIONS));
	}
	
	public static String getContext(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(ServerChangesUtil.SLOT_NAME_CONTEXT));
	}
	
	public static String getBody(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(ServerChangesUtil.SLOT_NAME_BODY));
	}
	
	public static String getType(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(ServerChangesUtil.SLOT_NAME_TYPE));
	}
	
	public static void setInstApplyTo(KnowledgeBase cKb, Instance aInst, String newVal) {
		aInst.setOwnSlotValue(cKb.getSlot(ServerChangesUtil.SLOT_NAME_APPLYTO), newVal);
	}
	
	public static void setInstContext(KnowledgeBase cKb, Instance aInst, String newVal) {
		aInst.setOwnSlotValue(cKb.getSlot(ServerChangesUtil.SLOT_NAME_CONTEXT), newVal);
	}

	public static Collection getChangeInsts(KnowledgeBase cKb) {
		Cls chgs = cKb.getCls(ServerChangesUtil.CLS_NAME_CHANGE);
		return cKb.getInstances(chgs);
	}
	
	public static Collection getAnnotationInsts(KnowledgeBase cKb) {
		Cls annotations = cKb.getCls(ServerChangesUtil.CLS_NAME_ANNOTATE);
		return cKb.getInstances(annotations);
	}
	
	public static Collection getAnnotationChanges(KnowledgeBase cKb, Instance annotateInst) {
		return annotateInst.getOwnSlotValues(cKb.getSlot(ServerChangesUtil.SLOT_NAME_ANNOTATES));
	}

	public static Collection getTransChanges(KnowledgeBase cKb, Instance transInst) {
		return transInst.getOwnSlotValues(cKb.getSlot(ServerChangesUtil.SLOT_NAME_CHANGES));
	}
	
	public static String getNameChangedNewName(KnowledgeBase cKb, Instance nameChangedInst) {
		return (String) nameChangedInst.getOwnSlotValue(cKb.getSlot(ServerChangesUtil.SLOT_NAME_NEWNAME));
	}
	
	public static String getNameChangedOldName(KnowledgeBase cKb, Instance nameChangedInst) {
		return (String) nameChangedInst.getOwnSlotValue(cKb.getSlot(ServerChangesUtil.SLOT_NAME_OLDNAME));
	}
	
	
	
	public static Instance createAnnotation(KnowledgeBase cKb, String annotType,Collection changeInsts) {
		Cls annotate = cKb.getCls(annotType);
		Slot annotates = cKb.getSlot(ServerChangesUtil.SLOT_NAME_ANNOTATES);
		//Slot title = cKb.getSlot(ServerChangesUtil.SLOT_NAME_TITLE);
		
		Instance annotateInst = cKb.createInstance(null, annotate);
		annotateInst.setOwnSlotValues(annotates, changeInsts);
		//annotateInst.setOwnSlotValue(title, annotateInst.getName());
		
		return annotateInst;
	}
	
	public static Instance updateAnnotation(KnowledgeBase cKb, Instance annotateInst) {
		
		Slot created = cKb.getSlot(ServerChangesUtil.SLOT_NAME_CREATED);
		Slot author = cKb.getSlot(ServerChangesUtil.SLOT_NAME_AUTHOR);
		Slot modified = cKb.getSlot(ServerChangesUtil.SLOT_NAME_MODIFIED);
		Slot body = cKb.getSlot(ServerChangesUtil.SLOT_NAME_BODY);
		
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
