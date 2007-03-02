package edu.stanford.smi.protegex.server_changes.model;

import java.util.Collection;
import java.util.EnumMap;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;

public class ChangeModel {
    private KnowledgeBase changes_kb;
    private EnumMap<ChangeCls, Cls> clsMap = new EnumMap<ChangeCls, Cls>(ChangeCls.class);
    private EnumMap<SlotCls, Slot> slotMap = new EnumMap<SlotCls, Slot>(SlotCls.class);
    
    // not recommended style but it is very convenient (pretty?) for the enum name to be the name.
    // alternatively I could use a constructor...
    public enum ChangeCls {
        Annotation,
        Change,
        Class_Change,
        Instance_Change,
        KB_Change,
        Class_Created,
        Class_Deleted,
        Property_Change,
        Slot_Change,
        CompositeChange,
        Timestamp
    }
    
    public enum SlotCls {
        action,
        applyTo,
        annotates,
        associatedAnnotations,
        author,
        body,
        changes
    }
    
    public Cls getCls(ChangeCls c) {
        Cls cls = clsMap.get(c);
        if (cls == null) {
            cls = changes_kb.getCls(c.name());
            clsMap.put(c, cls);
        }
        return cls;
    }
    
    
    public Slot getSlot(SlotCls s) {
        Slot slot = slotMap.get(s);
        if (slot == null) {
            slot = changes_kb.getSlot(s.name());
            slotMap.put(s, slot);
        }
        return slot;
    }
    
    public Object getDirectValue(Instance i, SlotCls s) {
        return i.getDirectOwnSlotValue(getSlot(s));
    }
    
    public Collection getDirectValues(Instance i, SlotCls s) {
        return i.getDirectOwnSlotValues(getSlot(s));
    }
    

}
