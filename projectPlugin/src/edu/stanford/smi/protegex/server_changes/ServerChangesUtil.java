package edu.stanford.smi.protegex.server_changes;



import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;

public class ServerChangesUtil {
    private static final Logger log = Log.getLogger(ServerChangesUtil.class);
	
	private ServerChangesUtil() {}
	
	public static String getActionDisplay(KnowledgeBase cKb, Instance aInst) {
		String actionStr = (String) aInst.getOwnSlotValue(cKb.getSlot(Model.SLOT_NAME_ACTION));
		return actionStr.replace('_', ' ');
	}
	
	public static Instance createClassCreatedChange(KnowledgeBase cKb, String apply) {
		Cls createCls = cKb.getCls(Model.CHANGETYPE_CLASS_CREATED);
		
		Instance changeInst = cKb.createInstance(null, new ArrayList());
		Slot action = cKb.getSlot(Model.SLOT_NAME_ACTION);
		Slot applyTo = cKb.getSlot(Model.SLOT_NAME_APPLYTO);
		Slot author = cKb.getSlot(Model.SLOT_NAME_AUTHOR);
		Slot context = cKb.getSlot(Model.SLOT_NAME_CONTEXT);
		Slot created = cKb.getSlot(Model.SLOT_NAME_CREATED);
		Slot type = cKb.getSlot(Model.SLOT_NAME_TYPE);
		
		String desc = "Created Class: " + apply;
		
		changeInst.setOwnSlotValue(action, createCls.getName());
		changeInst.setOwnSlotValue(applyTo, apply);
		changeInst.setOwnSlotValue(author, "");
		changeInst.setOwnSlotValue(context, desc);
		changeInst.setOwnSlotValue(created, "");
		changeInst.setOwnSlotValue(type, Model.CHANGE_LEVEL_INFO);
		Model.logChange("Creating new change instance for new class", log, Level.FINE, changeInst, createCls);
		cKb.setDirectType(changeInst, createCls);
		
		return changeInst;
		
	}
	
	public static Instance createTemplateSlotAddedChange(KnowledgeBase cKb, String apply, String slot) {
		Cls tempSlotAddCls = cKb.getCls(Model.CHANGETYPE_TEMPLATESLOT_ADDED);
		
		Instance changeInst = cKb.createInstance(null, new ArrayList());
		Slot action = cKb.getSlot(Model.SLOT_NAME_ACTION);
		Slot applyTo = cKb.getSlot(Model.SLOT_NAME_APPLYTO);
		Slot author = cKb.getSlot(Model.SLOT_NAME_AUTHOR);
		Slot context = cKb.getSlot(Model.SLOT_NAME_CONTEXT);
		Slot created = cKb.getSlot(Model.SLOT_NAME_CREATED);
		Slot type = cKb.getSlot(Model.SLOT_NAME_TYPE);
		
		String desc = "Added template slot: "+slot+" to: "+apply;
		
		changeInst.setOwnSlotValue(action, tempSlotAddCls.getName());
		changeInst.setOwnSlotValue(applyTo, apply);
		changeInst.setOwnSlotValue(author, "");
		changeInst.setOwnSlotValue(context, desc);
		changeInst.setOwnSlotValue(created, "");
		changeInst.setOwnSlotValue(type, Model.CHANGE_LEVEL_INFO);
        Model.logChange("Creating change instance for added template slot", log, Level.FINE, changeInst, tempSlotAddCls);
		cKb.setDirectType(changeInst, tempSlotAddCls);
		return changeInst;
		
	}
	
	
	public static Instance createRestrictionAddedChange(KnowledgeBase cKb, String apply, String slot) {
		Cls restrAddCls = cKb.getCls(Model.CHANGETYPE_TRANS_CHANGE);
		
		Instance changeInst = cKb.createInstance(null, new ArrayList());
		Slot action = cKb.getSlot(Model.SLOT_NAME_ACTION);
		Slot applyTo = cKb.getSlot(Model.SLOT_NAME_APPLYTO);
		Slot author = cKb.getSlot(Model.SLOT_NAME_AUTHOR);
		Slot context = cKb.getSlot(Model.SLOT_NAME_CONTEXT);
		Slot created = cKb.getSlot(Model.SLOT_NAME_CREATED);
		Slot type = cKb.getSlot(Model.SLOT_NAME_TYPE);
		
		String desc = "Restriction Created: "+slot;
		
		changeInst.setOwnSlotValue(action, "Restriction Created");
		changeInst.setOwnSlotValue(applyTo, apply);
		changeInst.setOwnSlotValue(author, "");
		changeInst.setOwnSlotValue(context, desc);
		changeInst.setOwnSlotValue(created, "");
		changeInst.setOwnSlotValue(type, "transaction");
        Model.logChange("Creating new change for added restriction", log, Level.FINE, changeInst, restrAddCls);
		cKb.setDirectType(changeInst, restrAddCls);
		return changeInst;
		
	}
	
	
	
	public static Instance createRestrictionRemovedChange(KnowledgeBase cKb, String apply, String slot) {
		Cls restrRemCls = cKb.getCls(Model.CHANGETYPE_TRANS_CHANGE);
		
		Instance changeInst = cKb.createInstance(null, new ArrayList());
		Slot action = cKb.getSlot(Model.SLOT_NAME_ACTION);
		Slot applyTo = cKb.getSlot(Model.SLOT_NAME_APPLYTO);
		Slot author = cKb.getSlot(Model.SLOT_NAME_AUTHOR);
		Slot context = cKb.getSlot(Model.SLOT_NAME_CONTEXT);
		Slot created = cKb.getSlot(Model.SLOT_NAME_CREATED);
		Slot type = cKb.getSlot(Model.SLOT_NAME_TYPE);
		
		String desc = "Restriction Removed: "+slot;
		
		changeInst.setOwnSlotValue(action, "Restriction Removed");
		changeInst.setOwnSlotValue(applyTo, apply);
		changeInst.setOwnSlotValue(author, "");
		changeInst.setOwnSlotValue(context, desc);
		changeInst.setOwnSlotValue(created, "");
		changeInst.setOwnSlotValue(type, "transaction");
		Model.logChange("Creating new change for removed restriction", log, Level.FINE, changeInst, restrRemCls);
		cKb.setDirectType(changeInst, restrRemCls);

		return changeInst;
		
	}
	
	
	public static Instance createTemplateSlotRemovedChange(KnowledgeBase cKb, String apply, String slot) {
		Cls tempSlotRemCls = cKb.getCls(Model.CHANGETYPE_TEMPLATESLOT_REMOVED);
		
		Instance changeInst = cKb.createInstance(null, new ArrayList());
		Slot action = cKb.getSlot(Model.SLOT_NAME_ACTION);
		Slot applyTo = cKb.getSlot(Model.SLOT_NAME_APPLYTO);
		Slot author = cKb.getSlot(Model.SLOT_NAME_AUTHOR);
		Slot context = cKb.getSlot(Model.SLOT_NAME_CONTEXT);
		Slot created = cKb.getSlot(Model.SLOT_NAME_CREATED);
		Slot type = cKb.getSlot(Model.SLOT_NAME_TYPE);
		
		String desc = "Removed template slot: "+slot+" from: "+apply;
		
		changeInst.setOwnSlotValue(action, tempSlotRemCls.getName());
		changeInst.setOwnSlotValue(applyTo, apply);
		changeInst.setOwnSlotValue(author, "");
		changeInst.setOwnSlotValue(context, desc);
		changeInst.setOwnSlotValue(created, "");
		changeInst.setOwnSlotValue(type, Model.CHANGE_LEVEL_INFO);
		Model.logChange("Creating change for removed template slot", log, Level.FINE, changeInst, tempSlotRemCls);
		cKb.setDirectType(changeInst, tempSlotRemCls);
		
		return changeInst;
		
	}
	
	public static Instance createClassDeletedChange(KnowledgeBase cKb, String apply) {
		Cls deleteCls = cKb.getCls(Model.CHANGETYPE_CLASS_DELETED);
	
		Instance changeInst = cKb.createInstance(null, new ArrayList());
		Slot action = cKb.getSlot(Model.SLOT_NAME_ACTION);
		Slot applyTo = cKb.getSlot(Model.SLOT_NAME_APPLYTO);
		Slot author = cKb.getSlot(Model.SLOT_NAME_AUTHOR);
		Slot context = cKb.getSlot(Model.SLOT_NAME_CONTEXT);
		Slot created = cKb.getSlot(Model.SLOT_NAME_CREATED);
		Slot type = cKb.getSlot(Model.SLOT_NAME_TYPE);

		String desc = "Deleted Class: " + apply;
		
		changeInst.setOwnSlotValue(action, deleteCls.getName());
		changeInst.setOwnSlotValue(applyTo, apply);
		changeInst.setOwnSlotValue(author, "");
		changeInst.setOwnSlotValue(context, desc);
		changeInst.setOwnSlotValue(created, "");
		changeInst.setOwnSlotValue(type, Model.CHANGE_LEVEL_INFO);
		Model.logChange("Creating new change for deleted class", log, Level.FINE, changeInst, deleteCls);
		cKb.setDirectType(changeInst, deleteCls);

		return changeInst;
		
	}
	
	public static Instance createClsRenameChange(KnowledgeBase cKb, String oldName, String newName) {
		Cls nameChangeCls = cKb.getCls(Model.CHANGETYPE_NAME_CHANGED);
		
		Instance changeInst = cKb.createInstance(null, new ArrayList());
		Slot action = cKb.getSlot(Model.SLOT_NAME_ACTION);
		Slot applyTo = cKb.getSlot(Model.SLOT_NAME_APPLYTO);
		Slot author = cKb.getSlot(Model.SLOT_NAME_AUTHOR);
		Slot context = cKb.getSlot(Model.SLOT_NAME_CONTEXT);
		Slot created = cKb.getSlot(Model.SLOT_NAME_CREATED);
		Slot type = cKb.getSlot(Model.SLOT_NAME_TYPE);

		String desc = "Name change from '" + oldName +"' to '" + newName + "'";
		
		changeInst.setOwnSlotValue(action, nameChangeCls.getName());
		changeInst.setOwnSlotValue(applyTo, newName);
		changeInst.setOwnSlotValue(author, "");
		changeInst.setOwnSlotValue(context, desc);
		changeInst.setOwnSlotValue(created, "");
		changeInst.setOwnSlotValue(type, Model.CHANGE_LEVEL_INFO);
		Model.logChange("Creating new change for renamed class", log, Level.FINE, changeInst, nameChangeCls);
		cKb.setDirectType(changeInst, nameChangeCls);

		return changeInst;
	}
	
	public static Instance createAnnotation(KnowledgeBase cKb, String annotType,Collection changeInsts) {
		Cls annotate = cKb.getCls(annotType);
		Slot annotates = cKb.getSlot(Model.SLOT_NAME_ANNOTATES);
		//Slot title = cKb.getSlot(Model.SLOT_NAME_TITLE);
		
		Instance annotateInst = cKb.createInstance(null, new ArrayList());
		annotateInst.setOwnSlotValues(annotates, changeInsts);
		//annotateInst.setOwnSlotValue(title, annotateInst.getName());
        Model.logChange("Creating change for annotation", log, Level.FINE, annotateInst, annotate);
		cKb.setDirectType(annotateInst, annotate);

		return annotateInst;
	}
	
	public static Instance updateAnnotation(KnowledgeBase kb,
                                            KnowledgeBase cKb, Instance annotateInst) {
		
		Slot created = cKb.getSlot(Model.SLOT_NAME_CREATED);
		Slot author = cKb.getSlot(Model.SLOT_NAME_AUTHOR);
		Slot modified = cKb.getSlot(Model.SLOT_NAME_MODIFIED);
		Slot body = cKb.getSlot(Model.SLOT_NAME_BODY);
		
		annotateInst.setOwnSlotValue(created, ChangesProject.getTimeStamp());
		annotateInst.setOwnSlotValue(modified, ChangesProject.getTimeStamp());
		annotateInst.setOwnSlotValue(author, ChangesProject.getUserName(kb));
		
		// If no comments are added, add empty string as comment
		Object bdy = annotateInst.getOwnSlotValue(body);
		if (bdy == null) {
			annotateInst.setOwnSlotValue(body, "");
		}
		Model.logChange("Updated Annotation", log, Level.FINE, annotateInst);
		return annotateInst;	
	}
	
	public static Instance createTransChange(KnowledgeBase cKb, Collection transChanges, Instance repInst) {
		Cls transChange = cKb.getCls(Model.CHANGETYPE_TRANS_CHANGE);
		Instance tInst = cKb.createInstance(null, new ArrayList());
		
		Slot applyTo = cKb.getSlot(Model.SLOT_NAME_APPLYTO);
		Slot author = cKb.getSlot(Model.SLOT_NAME_AUTHOR);
		Slot action = cKb.getSlot(Model.SLOT_NAME_ACTION);
		Slot context = cKb.getSlot(Model.SLOT_NAME_CONTEXT);
		Slot created = cKb.getSlot(Model.SLOT_NAME_CREATED);
		Slot type = cKb.getSlot(Model.SLOT_NAME_TYPE);
		Slot changes = cKb.getSlot(Model.SLOT_NAME_CHANGES);
		
		tInst.setOwnSlotValue(author, repInst.getOwnSlotValue(author));
		tInst.setOwnSlotValue(action, repInst.getOwnSlotValue(action));
		tInst.setOwnSlotValue(context, repInst.getOwnSlotValue(context));
		tInst.setOwnSlotValue(created, repInst.getOwnSlotValue(created));
		tInst.setOwnSlotValue(applyTo, repInst.getOwnSlotValue(applyTo));
		tInst.setOwnSlotValue(type, Model.CHANGE_LEVEL_DISP_TRANS);
		tInst.setOwnSlotValues(changes, transChanges);
        Model.logChange("Creating Transaction Change", log, Level.FINE, tInst, transChange);
		cKb.setDirectType(tInst, transChange);
		
		return tInst;
	}
	
	public static Instance createChange(KnowledgeBase currentKB,
                                        KnowledgeBase changeKB, 
                                        String changeClsName, String apply, String desc, String typ) {
		
		Cls change = changeKB.getCls(changeClsName);
		Slot action = changeKB.getSlot(Model.SLOT_NAME_ACTION);
		Slot applyTo = changeKB.getSlot(Model.SLOT_NAME_APPLYTO);
		Slot author = changeKB.getSlot(Model.SLOT_NAME_AUTHOR);
		Slot context = changeKB.getSlot(Model.SLOT_NAME_CONTEXT);
		Slot created = changeKB.getSlot(Model.SLOT_NAME_CREATED);
		Slot type = changeKB.getSlot(Model.SLOT_NAME_TYPE);
		
		Instance changeInst = changeKB.createInstance(null, new ArrayList());
		
		if(apply.equals(Model.CHANGE_LEVEL_ROOT)){
			changeInst.setOwnSlotValue(action, "Type of change");
			changeInst.setOwnSlotValue(applyTo, apply);
			changeInst.setOwnSlotValue(author, "Person who made the change");
			changeInst.setOwnSlotValue(context, "Details of the action");
			changeInst.setOwnSlotValue(created, "Date and time the change was made");
			changeInst.setOwnSlotValue(type, typ);	
		}
		else{
		    changeInst.setOwnSlotValue(action, change.getName());
		    changeInst.setOwnSlotValue(applyTo, apply);
		    changeInst.setOwnSlotValue(author, ChangesProject.getUserName(currentKB));
		    changeInst.setOwnSlotValue(context, desc);
		    changeInst.setOwnSlotValue(created, ChangesProject.getTimeStamp());
		    changeInst.setOwnSlotValue(type, typ);
		}
        Model.logChange("Creating change", log, Level.FINE, changeInst, change);
        changeKB.setDirectType(changeInst, change);
		return changeInst;
	}
	
	public static Instance createNameChange(KnowledgeBase currentKB, 
                                            KnowledgeBase changeKB, 
                                            String changeClsName, 
                                            String apply, 
                                            String desc, 
                                            String typ, 
                                            String oldName, 
                                            String newName) {
		Slot oldN = changeKB.getSlot(Model.SLOT_NAME_OLDNAME);
		Slot newN = changeKB.getSlot(Model.SLOT_NAME_NEWNAME);
		Instance changeInst = createChange(currentKB, changeKB, changeClsName, apply, desc, typ);
		changeInst.setOwnSlotValue(oldN, oldName);
		changeInst.setOwnSlotValue(newN, newName);
		Model.logChange("Modified name change change", log, Level.FINE, changeInst);
		return changeInst;
	}
}
