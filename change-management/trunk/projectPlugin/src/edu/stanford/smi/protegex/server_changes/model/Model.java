package edu.stanford.smi.protegex.server_changes.model;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;

public class Model {


    //Change level
    public static final String CHANGE_LEVEL_INFO = "info";
    public static final String CHANGE_LEVEL_DISP_TRANS = "disp_transaction";
    public static final String CHANGE_LEVEL_TRANS_INFO = "transaction_info";
    public static final String CHANGE_LEVEL_TRANS = "transaction";
    public static final String CHANGE_LEVEL_DEBUG = "debug";
    public static final String CHANGE_LEVEL_ROOT = "ROOT";
    
    public static final String CHANGETYPE_CHANGE = "Change";
 
    // Names of subclasses of Class_Change
    public static final String CHANGETYPE_ANNOTATION_MODIFIED = "Annotation_Modified";
    public static final String CHANGETYPE_ANNOTATION_REMOVED = "Annotation_Removed";
    public static final String CHANGETYPE_ANNOTATION_ADDED = "Annotation_Added";
    public static final String CHANGETYPE_DOCUMENTATION_REMOVED = "Documentation_Removed";
    public static final String CHANGETYPE_DOCUMENTATION_ADDED = "Documentation_Added";  
    public static final String CHANGETYPE_DOMAIN_PROP_ADDED = "DomainProperty_Added"; 
    public static final String CHANGETYPE_DOMAIN_PROP_REMOVED = "DomainProperty_Removed";
    public static final String CHANGETYPE_DISJOINT_CLASS_ADDED = "DisjointClass_Added";
    public static final String CHANGETYPE_SUBCLASS_ADDED = "Subclass_Added";  
    public static final String CHANGETYPE_SUBCLASS_REMOVED = "Subclass_Removed";
    public static final String CHANGETYPE_SUPERCLASS_ADDED = "Superclass_Added";
    public static final String CHANGETYPE_TEMPLATESLOT_REMOVED = "TemplateSlot_Removed";
    public static final String CHANGETYPE_TEMPLATESLOT_ADDED = "TemplateSlot_Added";
    public static final String CHANGETYPE_SUPERCLASS_REMOVED = "Superclass_Removed";

    // Names of subclasses of Instance_Change
    public static final String CHANGETYPE_DIRECTTYPE_ADDED = "DirectType_Added";
    public static final String CHANGETYPE_DIRECTTYPE_REMOVED = "DirectType_Removed";
    public static final String CHANGETYPE_INSTANCE_ADDED = "Instance_Added";
    public static final String CHANGETYPE_INSTANCE_REMOVED = "Instance_Removed";
    public static final String CHANGETYPE_SLOT_VALUE = "Slot_Value";
    
    // Names of subclasses of KB_Change
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
    public static final String SLOT_NAME_BODY = "body";
    public static final String SLOT_NAME_CHANGES = "Changes";
    public static final String SLOT_NAME_CREATED = "created";
    public static final String SLOT_NAME_CONTEXT = "context";
    public static final String SLOT_NAME_TITLE = "title";
    public static final String SLOT_NAME_TYPE = "type";
    public static final String SLOT_NAME_MODIFIED = "modified";
    public static final String SLOT_NAME_OLDNAME = "oldName";
    public static final String SLOT_NAME_NEWNAME = "newName";
    public static final String SLOT_PART_OF_TRANSACTION = "part_of_Transaction";
    
    // Class Name
    public static final String CLS_NAME_CHANGE = "Change";
    public static final String CLS_NAME_ANNOTATE = "Annotation";
     
    private KnowledgeBase changesKB;
   
    public Model(KnowledgeBase changesKB) {
        this.changesKB = changesKB;
    }
    
    /* --------------------- gete ontology classes  --------------------- */
    
    private Cls changed_class;  
    public Cls getChangeClass() {
        if (changed_class == null) {
            changed_class = changesKB.getCls(CHANGETYPE_CHANGE);
        }
        return changed_class;
    }
    
    private Cls class_created_class;  
    public Cls getClassCreatedClass() {
        if (class_created_class == null) {
            class_created_class = changesKB.getCls(CHANGETYPE_CLASS_CREATED);
        }
        return class_created_class;
    }
    
    private Cls class_deleted_class;
    public Cls getClassDeletedClass() {
        if (class_deleted_class == null) {
            class_deleted_class = changesKB.getCls(CHANGETYPE_CLASS_DELETED);
        }
        return class_deleted_class;
    }
    
    private Cls slot_created_class;
    public Cls getSlotCreatedClass() {
        if (slot_created_class == null) {
            slot_created_class = changesKB.getCls(CHANGETYPE_SLOT_CREATED);
        }
        return slot_created_class;
    }
    
    private Cls slot_deleted_class;
    public Cls getSlotDeletedClass() {
        if (slot_deleted_class == null) {
            slot_deleted_class = changesKB.getCls(CHANGETYPE_SLOT_DELETED);
        }
        return slot_deleted_class;
    }
    
    private Cls property_created_class;
    public Cls getPropertyCreatedClass() {
        if (property_created_class == null) {
            property_created_class = changesKB.getCls(CHANGETYPE_PROPERTY_CREATED);
        }
        return property_created_class;
    }

    private Cls property_deleted_class    ;
    public Cls getPropertyDeletedClass() {
        if (property_deleted_class     == null) {
            property_deleted_class     = changesKB.getCls(CHANGETYPE_PROPERTY_DELETED);
        }
        return property_deleted_class    ;
    }

    private Cls instance_added_class;
    public Cls getInstanceAddedClass() {
        if (instance_added_class == null) {
            instance_added_class = changesKB.getCls(CHANGETYPE_INSTANCE_ADDED);
        }
        return instance_added_class;
    }

    private Cls instance_removed_class;
    public Cls getInstanceRemovedClass() {
        if (instance_removed_class == null) {
            instance_removed_class = changesKB.getCls(CHANGETYPE_INSTANCE_REMOVED);
        }
        return instance_removed_class;
    }
    
    private Cls name_changed_class;
    public Cls getNameChangedClass() {
        if (name_changed_class == null) {
            name_changed_class  = changesKB.getCls(CHANGETYPE_NAME_CHANGED);
        }
        return name_changed_class;
    }
    
    /* -------------------------- get Slot methods ----------------------------- */
    
    private Slot sub_changes_slot;
    public Slot getSubChangesSlot() {
        if (sub_changes_slot == null) {
            sub_changes_slot = changesKB.getSlot(SLOT_NAME_CHANGES);
        }
        return sub_changes_slot;
    }
    
    private Slot part_of_transaction_slot;
    public Slot getPartOfTransactionSlot() {
        if (part_of_transaction_slot == null) {
            part_of_transaction_slot = changesKB.getSlot(SLOT_PART_OF_TRANSACTION);
        }
        return part_of_transaction_slot;
    }

    /* --------------------- get slot value methods --------------------- */
    public static String getAction(Instance aInst) {
        KnowledgeBase cKb = aInst.getKnowledgeBase();
    	return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_ACTION));
    }

    public static String getApplyTo(Instance aInst) {
        KnowledgeBase cKb = aInst.getKnowledgeBase();
    	return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_APPLYTO));
    }

    public static String getAuthor(Instance aInst) {
        KnowledgeBase cKb = aInst.getKnowledgeBase();
    	return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_AUTHOR));
    }

    public static String getTitle(Instance aInst) {
        KnowledgeBase cKb = aInst.getKnowledgeBase();
    	return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_TITLE));
    }

    public static Collection getChanges(Instance aInst) {
        KnowledgeBase cKb = aInst.getKnowledgeBase();
    	return aInst.getOwnSlotValues(cKb.getSlot(SLOT_NAME_CHANGES));
    }

    public static Collection getAssocAnnotations(Instance aInst) {
        KnowledgeBase cKb = aInst.getKnowledgeBase();
    	return aInst.getOwnSlotValues(cKb.getSlot(SLOT_NAME_ASSOC_ANNOTATIONS));
    }

    public static String getContext(Instance aInst) {
        KnowledgeBase cKb = aInst.getKnowledgeBase();
    	return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_CONTEXT));
    }

    public static String getBody(Instance aInst) {
        KnowledgeBase cKb = aInst.getKnowledgeBase();
    	return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_BODY));
    }

    public static String getType(Instance aInst) {
        KnowledgeBase cKb = aInst.getKnowledgeBase();
    	return (String) aInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_TYPE));
    }

    public static Collection getChangeInsts(KnowledgeBase cKb) {
    	Cls chgs = cKb.getCls(CLS_NAME_CHANGE);
    	return cKb.getInstances(chgs);
    }

    public static Collection getAnnotationInsts(KnowledgeBase cKb) {
    	Cls annotations = cKb.getCls(CLS_NAME_ANNOTATE);
    	return cKb.getInstances(annotations);
    }

    public static Collection getAnnotationChanges(Instance annotateInst) {
        KnowledgeBase cKb = annotateInst.getKnowledgeBase();
    	return annotateInst.getOwnSlotValues(cKb.getSlot(SLOT_NAME_ANNOTATES));
    }

    public static Collection getTransChanges(Instance transInst) {
        KnowledgeBase cKb = transInst.getKnowledgeBase();
    	return transInst.getOwnSlotValues(cKb.getSlot(SLOT_NAME_CHANGES));
    }

    public static String getNameChangedNewName(Instance nameChangedInst) {
        KnowledgeBase cKb = nameChangedInst.getKnowledgeBase();
    	return (String) nameChangedInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_NEWNAME));
    }

    public static String getNameChangedOldName(Instance nameChangedInst) {
        KnowledgeBase cKb = nameChangedInst.getKnowledgeBase();
    	return (String) nameChangedInst.getOwnSlotValue(cKb.getSlot(SLOT_NAME_OLDNAME));
    }

    public static void setInstApplyTo(Instance aInst, String newVal) {
        KnowledgeBase cKb = aInst.getKnowledgeBase();
    	aInst.setOwnSlotValue(cKb.getSlot(SLOT_NAME_APPLYTO), newVal);
    }

    public static void setInstContext(Instance aInst, String newVal) {
        KnowledgeBase cKb = aInst.getKnowledgeBase();
    	aInst.setOwnSlotValue(cKb.getSlot(SLOT_NAME_CONTEXT), newVal);
    }
    
    public static void logChange(String msg, Logger log, Level level, Instance aInst) {
        if (!log.isLoggable(level)) {
            return;
        }
        Cls cls = aInst.getDirectType();
        logChange(msg, log, level, aInst, cls);
    }
    

        
    public static void logChange(String msg, Logger log, Level level, Instance aInst, Cls cls) {
        if (!log.isLoggable(level)) {
            return;
        }
        log.log(level, msg);
        log.log(level, "\tAction = " + getAction(aInst));
        log.log(level, "\tApplyTo = " + getApplyTo(aInst));
        log.log(level, "\tAuthor = " + getAuthor(aInst));
        log.log(level, "\tContext = " + getContext(aInst));
        log.log(level, "\tCreated = " + Timestamp.getTimestamp(aInst));
        log.log(level, "\tType = " + getType(aInst));
        log.log(level, "\tDirect type = " + cls);
        log.log(level, "\tFrame ID = " + aInst.getFrameID().getLocalPart());
    }


    
    

}
