package edu.stanford.smi.protegex.server_changes;



import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;

public class ServerChangesUtil {
    
    //Change level
    public static final String CHANGE_LEVEL_INFO = "info";
	public static final String CHANGE_LEVEL_DEBUG = "debug";
	public static final String CHANGE_LEVEL_TRANS = "transaction";
	public static final String CHANGE_LEVEL_TRANS_INFO = "transaction_info";
	public static final String CHANGE_LEVEL_DISP_TRANS = "disp_transaction";
	
	//Class change
	public static final String CHANGETYPE_DOMAIN_PROP_ADDED = "DomainProperty_Added";
	public static final String CHANGETYPE_DOMAIN_PROP_REMOVED = "DomainProperty_Removed";
	public static final String CHANGETYPE_SUBCLASS_ADDED = "Subclass_Added";
	public static final String CHANGETYPE_SUBCLASS_REMOVED = "Subclass_Removed";
	public static final String CHANGETYPE_SUPERCLASS_ADDED = "Superclass_Added";
	public static final String CHANGETYPE_SUPERCLASS_REMOVED = "Superclass_Removed";
	public static final String CHANGETYPE_TEMPLATESLOT_ADDED = "TemplateSlot_Added";
	public static final String CHANGETYPE_TEMPLATESLOT_REMOVED = "TemplateSlot_Removed";
	public static final String CHANGETYPE_DOCUMENTATION_ADDED = "Documentation_Added";
	public static final String CHANGETYPE_DOCUMENTATION_REMOVED = "Documentation_Removed";
	public static final String CHANGETYPE_ANNOTATION_ADDED = "Annotation_Added";
	public static final String CHANGETYPE_ANNOTATION_REMOVED = "Annotation_Removed";
	public static final String CHANGETYPE_ANNOTATION_MODIFIED = "Annotation_Modified";
	public static final String CHANGETYPE_DISJOINT_CLASS_ADDED = "DisjointClass_Added";
	//Instance change
	public static final String CHANGETYPE_DIRECTTYPE_ADDED = "DirectType_Added";
	public static final String CHANGETYPE_DIRECTTYPE_REMOVED = "DirectType_Removed";
	public static final String CHANGETYPE_INSTANCE_ADDED = "Instance_Added";
	public static final String CHANGETYPE_INSTANCE_REMOVED = "Instance_Removed";
	public static final String CHANGETYPE_SLOT_VALUE = "Slot_Value";
	
	// KB change
	public static final String CHANGETYPE_CLASS_CREATED = "Class_Created";
	public static final String CHANGETYPE_CLASS_DELETED = "Class_Deleted";
	public static final String CHANGETYPE_NAME_CHANGED = "Name_Changed";
	
	// Propety change
	public static final String CHANGETYPE_PROPERTY_CREATED = "Property_Created";
	public static final String CHANGETYPE_PROPERTY_DELETED = "Property_Deleted";
	public static final String CHANGETYPE_SUBPROPERTY_ADDED = "Subproperty_Added";
	public static final String CHANGETYPE_SUBPROPERTY_REMOVED = "Subproperty_Removed";
	public static final String CHANGETYPE_SUPERPROPERTY_ADDED = "Superproperty_Added";
	public static final String CHANGETYPE_SUPERPROPERTY_REMOVED = "Superproperty_Removed";
	
	// Slot change
	public static final String CHANGETYPE_SLOT_CREATED = "Slot_Created";
	public static final String CHANGETYPE_SLOT_DELETED = "Slot_Deleted";
	public static final String CHANGETYPE_SUBSLOT_ADDED = "Subslot_Added";
	public static final String CHANGETYPE_SUBSLOT_REMOVED = "Subslot_Removed";
	public static final String CHANGETYPE_SUPERSLOT_ADDED = "Superslot_Added";
	public static final String CHANGETYPE_SUPERSLOT_REMOVED = "Superslot_Removed";
	public static final String CHANGETYPE_MAXIMUM_CARDINALITY = "Maximum_Cardinality";
	public static final String CHANGETYPE_MINIMUM_CARDINALITY = "Minimum_Cardinality";
	public static final String CHANGETYPE_MINIMUM_VALUE = "Minimum_Value";
	public static final String CHANGETYPE_MAXIMUM_VALUE = "Maximum_Value";
	
	// Trans change
	public static final String CHANGETYPE_TRANS_CHANGE = "TransChange";
	
	// Slot Names
	public static final String SLOT_NAME_ACTION = "action";
	public static final String SLOT_NAME_APPLYTO = "applyTo";
	public static final String SLOT_NAME_ASSOC_ANNOTATIONS = "assoc_annotations";
	public static final String SLOT_NAME_AUTHOR = "author";
	public static final String SLOT_NAME_ANNOTATES = "annotates";
    public static final String SLOT_NAME_IS_IN_TRANSACTION = "isInTransaction";
	public static final String SLOT_NAME_BODY = "body";
	public static final String SLOT_NAME_CHANGES = "Changes";
	public static final String SLOT_NAME_CREATED = "created";
	public static final String SLOT_NAME_CONTEXT = "context";
	public static final String SLOT_NAME_TITLE = "title";
	public static final String SLOT_NAME_TYPE = "type";
	public static final String SLOT_NAME_MODIFIED = "modified";
	public static final String SLOT_NAME_OLDNAME = "oldName";
	public static final String SLOT_NAME_NEWNAME = "newName";
	
	// Class Name
	public static final String CLS_NAME_CHANGE = "Change";
	public static final String CLS_NAME_ANNOTATE = "Annotation";

	
	private ServerChangesUtil() {}
	
	public static String getActionDisplay(KnowledgeBase cKb, Instance aInst) {
		String actionStr = (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_ACTION));
		return actionStr.replace('_', ' ');
	}
	
	public static String getAction(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_ACTION));
	}
	
	public static String getApplyTo(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_APPLYTO));
	}
	
	public static String getAuthor(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_AUTHOR));
	}
	
	public static String getCreated(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_CREATED));
	}
	
	public static String getTitle(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_TITLE));
	}
	
	public static Collection getChanges(KnowledgeBase cKb, Instance aInst) {
		return aInst.getOwnSlotValues(cKb.getSlot(SLOT_NAME_CHANGES));
	}
	
	public static Collection getAssocAnnotations(KnowledgeBase cKb, Instance aInst) {
		return aInst.getOwnSlotValues(cKb.getSlot(SLOT_NAME_ASSOC_ANNOTATIONS));
	}
	
	public static String getContext(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_CONTEXT));
	}
	
	public static String getBody(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_BODY));
	}
	
	public static String getType(KnowledgeBase cKb, Instance aInst) {
		return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_TYPE));
	}
	
	public static Instance createClassCreatedChange(KnowledgeBase cKb, String apply) {
		Cls createCls = cKb.getCls("Class_Created");
		
		Instance changeInst = cKb.createInstance(null, new ArrayList());
		Slot action = cKb.getSlot("action");
		Slot applyTo = cKb.getSlot("applyTo");
		Slot author = cKb.getSlot("author");
		Slot context = cKb.getSlot("context");
		Slot created = cKb.getSlot("created");
		Slot type = cKb.getSlot("type");
		
		String desc = "Created Class: " + apply;
		
		changeInst.setOwnSlotValue(action, createCls.getName());
		changeInst.setOwnSlotValue(applyTo, apply);
		changeInst.setOwnSlotValue(author, "");
		changeInst.setOwnSlotValue(context, desc);
		changeInst.setOwnSlotValue(created, "");
		changeInst.setOwnSlotValue(type, "info");
                cKb.setDirectType(changeInst, createCls);
		
		return changeInst;
		
	}
	
	public static Instance createTemplateSlotAddedChange(KnowledgeBase cKb, String apply, String slot) {
		Cls tempSlotAddCls = cKb.getCls("TemplateSlot_Added");
		
		Instance changeInst = cKb.createInstance(null, new ArrayList());
		Slot action = cKb.getSlot("action");
		Slot applyTo = cKb.getSlot("applyTo");
		Slot author = cKb.getSlot("author");
		Slot context = cKb.getSlot("context");
		Slot created = cKb.getSlot("created");
		Slot type = cKb.getSlot("type");
		
		String desc = "Added template slot: "+slot+" to: "+apply;
		
		changeInst.setOwnSlotValue(action, tempSlotAddCls.getName());
		changeInst.setOwnSlotValue(applyTo, apply);
		changeInst.setOwnSlotValue(author, "");
		changeInst.setOwnSlotValue(context, desc);
		changeInst.setOwnSlotValue(created, "");
		changeInst.setOwnSlotValue(type, "info");
		cKb.setDirectType(changeInst, tempSlotAddCls);
		return changeInst;
		
	}
	
	
	public static Instance createRestrictionAddedChange(KnowledgeBase cKb, String apply, String slot) {
		Cls restrAddCls = cKb.getCls("TransChange");
		
		Instance changeInst = cKb.createInstance(null, new ArrayList());
		Slot action = cKb.getSlot("action");
		Slot applyTo = cKb.getSlot("applyTo");
		Slot author = cKb.getSlot("author");
		Slot context = cKb.getSlot("context");
		Slot created = cKb.getSlot("created");
		Slot type = cKb.getSlot("type");
		
		String desc = "Restriction Created: "+slot;
		
		changeInst.setOwnSlotValue(action, "Restriction Created");
		changeInst.setOwnSlotValue(applyTo, apply);
		changeInst.setOwnSlotValue(author, "");
		changeInst.setOwnSlotValue(context, desc);
		changeInst.setOwnSlotValue(created, "");
		changeInst.setOwnSlotValue(type, "transaction");
		cKb.setDirectType(changeInst, restrAddCls);
		return changeInst;
		
	}
	
	
	
	public static Instance createRestrictionRemovedChange(KnowledgeBase cKb, String apply, String slot) {
		Cls restrRemCls = cKb.getCls("TransChange");
		
		Instance changeInst = cKb.createInstance(null, new ArrayList());
		Slot action = cKb.getSlot("action");
		Slot applyTo = cKb.getSlot("applyTo");
		Slot author = cKb.getSlot("author");
		Slot context = cKb.getSlot("context");
		Slot created = cKb.getSlot("created");
		Slot type = cKb.getSlot("type");
		
		String desc = "Restriction Removed: "+slot;
		
		changeInst.setOwnSlotValue(action, "Restriction Removed");
		changeInst.setOwnSlotValue(applyTo, apply);
		changeInst.setOwnSlotValue(author, "");
		changeInst.setOwnSlotValue(context, desc);
		changeInst.setOwnSlotValue(created, "");
		changeInst.setOwnSlotValue(type, "transaction");

		cKb.setDirectType(changeInst, restrRemCls);

		return changeInst;
		
	}
	
	
	public static Instance createTemplateSlotRemovedChange(KnowledgeBase cKb, String apply, String slot) {
		Cls tempSlotRemCls = cKb.getCls("TemplateSlot_Removed");
		
		Instance changeInst = cKb.createInstance(null, new ArrayList());
		Slot action = cKb.getSlot("action");
		Slot applyTo = cKb.getSlot("applyTo");
		Slot author = cKb.getSlot("author");
		Slot context = cKb.getSlot("context");
		Slot created = cKb.getSlot("created");
		Slot type = cKb.getSlot("type");
		
		String desc = "Removed template slot: "+slot+" from: "+apply;
		
		changeInst.setOwnSlotValue(action, tempSlotRemCls.getName());
		changeInst.setOwnSlotValue(applyTo, apply);
		changeInst.setOwnSlotValue(author, "");
		changeInst.setOwnSlotValue(context, desc);
		changeInst.setOwnSlotValue(created, "");
		changeInst.setOwnSlotValue(type, "info");

                cKb.setDirectType(changeInst, tempSlotRemCls);
		
		return changeInst;
		
	}
	
	public static Instance createClassDeletedChange(KnowledgeBase cKb, String apply) {
		Cls deleteCls = cKb.getCls("Class_Deleted");
	
		Instance changeInst = cKb.createInstance(null, new ArrayList());
		Slot action = cKb.getSlot("action");
		Slot applyTo = cKb.getSlot("applyTo");
		Slot author = cKb.getSlot("author");
		Slot context = cKb.getSlot("context");
		Slot created = cKb.getSlot("created");
		Slot type = cKb.getSlot("type");

		String desc = "Deleted Class: " + apply;
		
		changeInst.setOwnSlotValue(action, deleteCls.getName());
		changeInst.setOwnSlotValue(applyTo, apply);
		changeInst.setOwnSlotValue(author, "");
		changeInst.setOwnSlotValue(context, desc);
		changeInst.setOwnSlotValue(created, "");
		changeInst.setOwnSlotValue(type, "info");
		
                cKb.setDirectType(changeInst, deleteCls);

		return changeInst;
		
	}
	
	public static Instance createClsRenameChange(KnowledgeBase cKb, String oldName, String newName) {
		Cls nameChangeCls = cKb.getCls("Name_Changed");
		
		Instance changeInst = cKb.createInstance(null, new ArrayList());
		Slot action = cKb.getSlot("action");
		Slot applyTo = cKb.getSlot("applyTo");
		Slot author = cKb.getSlot("author");
		Slot context = cKb.getSlot("context");
		Slot created = cKb.getSlot("created");
		Slot type = cKb.getSlot("type");

		String desc = "Name change from '" + oldName +"' to '" + newName + "'";
		
		changeInst.setOwnSlotValue(action, nameChangeCls.getName());
		changeInst.setOwnSlotValue(applyTo, newName);
		changeInst.setOwnSlotValue(author, "");
		changeInst.setOwnSlotValue(context, desc);
		changeInst.setOwnSlotValue(created, "");
		changeInst.setOwnSlotValue(type, "info");
		
                cKb.setDirectType(changeInst, nameChangeCls);

		return changeInst;
	}
	
	public static Collection getChangeInsts(KnowledgeBase cKb) {
		Cls chgs = cKb.getCls(CLS_NAME_CHANGE);
		return cKb.getInstances(chgs);
	}
	
	public static Collection getAnnotationInsts(KnowledgeBase cKb) {
		Cls annotations = cKb.getCls(CLS_NAME_ANNOTATE);
		return cKb.getInstances(annotations);
	}
	
	public static Collection getAnnotationChanges(KnowledgeBase cKb, Instance annotateInst) {
		return annotateInst.getOwnSlotValues(cKb.getSlot(SLOT_NAME_ANNOTATES));
	}

	public static Collection getTransChanges(KnowledgeBase cKb, Instance transInst) {
		return transInst.getOwnSlotValues(cKb.getSlot(SLOT_NAME_CHANGES));
	}
	
	public static String getNameChangedNewName(KnowledgeBase cKb, Instance nameChangedInst) {
		return (String) nameChangedInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_NEWNAME));
	}
	
	public static String getNameChangedOldName(KnowledgeBase cKb, Instance nameChangedInst) {
		return (String) nameChangedInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_OLDNAME));
	}
	
	public static void setInstApplyTo(KnowledgeBase cKb, Instance aInst, String newVal) {
		aInst.setOwnSlotValue(cKb.getSlot(SLOT_NAME_APPLYTO), newVal);
	}
	
	public static void setInstContext(KnowledgeBase cKb, Instance aInst, String newVal) {
		aInst.setOwnSlotValue(cKb.getSlot(SLOT_NAME_CONTEXT), newVal);
	}
	
	public static Instance createAnnotation(KnowledgeBase cKb, String annotType,Collection changeInsts) {
		Cls annotate = cKb.getCls(annotType);
		Slot annotates = cKb.getSlot(SLOT_NAME_ANNOTATES);
		//Slot title = cKb.getSlot(SLOT_NAME_TITLE);
		
		Instance annotateInst = cKb.createInstance(null, new ArrayList());
		annotateInst.setOwnSlotValues(annotates, changeInsts);
		//annotateInst.setOwnSlotValue(title, annotateInst.getName());
		cKb.setDirectType(annotateInst, annotate);

		return annotateInst;
	}
	
	public static Instance updateAnnotation(KnowledgeBase kb,
                                            KnowledgeBase cKb, Instance annotateInst) {
		
		Slot created = cKb.getSlot(SLOT_NAME_CREATED);
		Slot author = cKb.getSlot(SLOT_NAME_AUTHOR);
		Slot modified = cKb.getSlot(SLOT_NAME_MODIFIED);
		Slot body = cKb.getSlot(SLOT_NAME_BODY);
		
		annotateInst.setOwnSlotValue(created, ChangesProject.getTimeStamp());
		annotateInst.setOwnSlotValue(modified, ChangesProject.getTimeStamp());
		annotateInst.setOwnSlotValue(author, ChangesProject.getUserName(kb));
		
		// If no comments are added, add empty string as comment
		Object bdy = annotateInst.getOwnSlotValue(body);
		if (bdy == null) {
			annotateInst.setOwnSlotValue(body, "");
		}
		
		return annotateInst;	
	}
	
	public static Instance createTransChange(KnowledgeBase cKb, Collection transChanges, Instance repInst) {
		Cls transChange = cKb.getCls(CHANGETYPE_TRANS_CHANGE);
		Instance tInst = cKb.createInstance(null, new ArrayList());
		
		Slot applyTo = cKb.getSlot(SLOT_NAME_APPLYTO);
		Slot author = cKb.getSlot(SLOT_NAME_AUTHOR);
		Slot action = cKb.getSlot(SLOT_NAME_ACTION);
		Slot context = cKb.getSlot(SLOT_NAME_CONTEXT);
		Slot created = cKb.getSlot(SLOT_NAME_CREATED);
		Slot type = cKb.getSlot(SLOT_NAME_TYPE);
		Slot changes = cKb.getSlot(SLOT_NAME_CHANGES);
        Slot inTransactionSlot = cKb.getSlot(SLOT_NAME_IS_IN_TRANSACTION);
		
		tInst.setOwnSlotValue(author, repInst.getOwnSlotValue(author));
		tInst.setOwnSlotValue(action, repInst.getOwnSlotValue(action));
		tInst.setOwnSlotValue(context, repInst.getOwnSlotValue(context));
		tInst.setOwnSlotValue(created, repInst.getOwnSlotValue(created));
		tInst.setOwnSlotValue(applyTo, repInst.getOwnSlotValue(applyTo));
		tInst.setOwnSlotValue(type, CHANGE_LEVEL_DISP_TRANS);
		tInst.setOwnSlotValues(changes, transChanges);
        tInst.setOwnSlotValue(inTransactionSlot, Boolean.FALSE);

		cKb.setDirectType(tInst, transChange);
		
		return tInst;
	}
	
	public static Instance createChange(KnowledgeBase currentKB,
                                        KnowledgeBase changeKB, 
                                        String changeClsName, String apply, String desc, String typ) {
		
		Cls change = changeKB.getCls(changeClsName);
		Slot action = changeKB.getSlot(SLOT_NAME_ACTION);
		Slot applyTo = changeKB.getSlot(SLOT_NAME_APPLYTO);
		Slot author = changeKB.getSlot(SLOT_NAME_AUTHOR);
		Slot context = changeKB.getSlot(SLOT_NAME_CONTEXT);
		Slot created = changeKB.getSlot(SLOT_NAME_CREATED);
        Slot inTransaction = changeKB.getSlot(SLOT_NAME_IS_IN_TRANSACTION);
		Slot type = changeKB.getSlot(SLOT_NAME_TYPE);
		
		Instance changeInst = changeKB.createInstance(null, new ArrayList());
		
		if(apply.equals("ROOT")){
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
		changeInst.setOwnSlotValue(inTransaction, ChangesProject.getIsInTransaction(currentKB));
		}
        
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
		Slot oldN = changeKB.getSlot("oldName");
		Slot newN = changeKB.getSlot("newName");
		Instance changeInst = createChange(currentKB, changeKB, changeClsName, apply, desc, typ);
		changeInst.setOwnSlotValue(oldN, oldName);
		changeInst.setOwnSlotValue(newN, newName);
		
		return changeInst;
	}
}
