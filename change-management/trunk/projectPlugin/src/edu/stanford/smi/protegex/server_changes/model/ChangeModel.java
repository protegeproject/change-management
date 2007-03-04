package edu.stanford.smi.protegex.server_changes.model;

import java.util.Collection;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Class_Created;
import edu.stanford.smi.protegex.server_changes.model.generated.Class_Deleted;
import edu.stanford.smi.protegex.server_changes.model.generated.Name_Changed;
import edu.stanford.smi.protegex.server_changes.model.generated.Property_Created;
import edu.stanford.smi.protegex.server_changes.model.generated.Property_Deleted;
import edu.stanford.smi.protegex.server_changes.model.generated.Slot_Created;
import edu.stanford.smi.protegex.server_changes.model.generated.Slot_Deleted;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;

public class ChangeModel {
    public final static String CHANGE_TYPE_ROOT = "Root";
    
    public final static String CHANGE_LEVEL_INFO = "info";
    public static final String CHANGE_LEVEL_TRANS_INFO = "transaction_info";
    public static final String CHANGE_LEVEL_TRANS = "transaction";
    
    
    private KnowledgeBase changes_kb;
    private EnumMap<ChangeCls, Cls> clsMap = new EnumMap<ChangeCls, Cls>(ChangeCls.class);
    private EnumMap<ChangeSlot, Slot> slotMap = new EnumMap<ChangeSlot, Slot>(ChangeSlot.class);
    
    
    public ChangeModel(KnowledgeBase changes_kb) {
        this.changes_kb = changes_kb;
    }
    
    // not recommended style but it is very convenient (pretty?) for the enum name to be the name.
    // alternatively I could use a constructor...
    public enum ChangeCls {
        Annotation,
        Change,
        
        Class_Change,
        Class_Created,
        Class_Deleted,
        Subclass_Added,
        Subclass_Removed,
        Superclass_Added,
        Superclass_Removed,
        
        Instance_Change,
        
        Property_Change,
        
        Slot_Change,
        
        Composite_Change,
        
        Ontology_Component,
        
        Timestamp;
    }
    
    public enum ChangeSlot {
        action,
        applyTo,
        annotates,
        associatedAnnotations,
        author,
        body,
        changes,
        date,
        sequence,
        timestamp
    }
    
    public KnowledgeBase getChangeKb() {
        return changes_kb;
    }
    
    public Cls getCls(ChangeCls c) {
        Cls cls = clsMap.get(c);
        if (cls == null) {
            cls = changes_kb.getCls(c.name());
            clsMap.put(c, cls);
        }
        return cls;
    }
    
    
    public Slot getSlot(ChangeSlot s) {
        Slot slot = slotMap.get(s);
        if (slot == null) {
            slot = changes_kb.getSlot(s.name());
            slotMap.put(s, slot);
        }
        return slot;
    }
    
    public Collection<Instance> getInstances(ChangeCls cls) {
        return getCls(cls).getInstances();
    }
    
    public Object getDirectValue(Instance i, ChangeSlot s) {
        return i.getDirectOwnSlotValue(getSlot(s));
    }
    
    public Collection getDirectValues(Instance i, ChangeSlot s) {
        return i.getDirectOwnSlotValues(getSlot(s));
    }
    
    public Instance createInstance(ChangeCls cls) {
        return getCls(cls).createDirectInstance(null);
    }
    
    /*
     * utility for debug sessions...
     */
    public static void logAnnotatableThing(Instance i) {
        logAnnotatableThing("debug:", Log.getLogger(), Level.CONFIG, i);
    }


    public static void logAnnotatableThing(String msg, Logger log, Level level, Instance aInst, Cls cls) {
        if (!log.isLoggable(level)) {
            return;
        }
        log.log(level, msg);
        if (aInst instanceof Change) {
            Change change = (Change) aInst;
            log.log(level, "\tAction = " + change.getAction());
            log.log(level, "\tApplyTo = " + change.getApplyTo());
            log.log(level, "\tAuthor = " + change.getAuthor());
            log.log(level, "\tContext = " + change.getContext());
            log.log(level, "\tCreated = " + ((Timestamp) change.getTimestamp()).getDate());
            log.log(level, "\tType = " + change.getType());
        }
        log.log(level, "\tDirect type = " + cls);
        log.log(level, "\tFrame ID = " + aInst.getFrameID().getLocalPart());
    }


    public static void logAnnotatableThing(String msg, Logger log, Level level, Instance aInst) {
        if (!log.isLoggable(level)) {
            return;
        }
        Cls cls = aInst.getDirectType();
        ChangeModel.logAnnotatableThing(msg, log, level, aInst, cls);
    }
}
