package edu.stanford.smi.protegex.server_changes.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;

public class ChangeModel {

    
    
    private KnowledgeBase changes_kb;
    private EnumMap<ChangeCls, Cls> clsMap = new EnumMap<ChangeCls, Cls>(ChangeCls.class);
    private EnumMap<AnnotationCls, Cls> annotationClsMap = new EnumMap<AnnotationCls, Cls>(AnnotationCls.class);
    private EnumMap<ChangeSlot, Slot> slotMap = new EnumMap<ChangeSlot, Slot>(ChangeSlot.class);
    
    
    public ChangeModel(KnowledgeBase changes_kb) {
        this.changes_kb = changes_kb;
    }
    
    // not recommended style but it is very convenient (pretty?) for the enum name to be the name.
    // alternatively I could use a constructor...
    public enum ChangeCls {        
        Change,
        
        Class_Change,
        Annotation_Added,
        Annotation_Modified,
        Annotation_Removed,
        Class_Created,
        Class_Deleted,
        DisjointClass_Added,
        Documentation_Added,
        Documentation_Removed,
        DomainProperty_Added,
        DomainProperty_Removed,
        Subclass_Added,
        Subclass_Removed,
        Superclass_Added,
        Superclass_Removed,
        TemplateSlot_Added,
        TemplateSlot_Removed,
        
        Individual_Change,
        Individual_Created,
        Individual_Deleted,
        DirectType_Added,
        DirectType_Removed,
        Individual_Added,
        Individual_Removed,
        Property_Value,
        
        Property_Change,
        Property_Created,
        Property_Deleted,
        Subproperty_Added,
        Subproperty_Removed,
        Superproperty_Added,
        Superproperty_Removed,
        Maximum_Cardinality,
        Maximum_Value,
        Minimum_Cardinality,
        Minimum_Value,
                
        Name_Changed,
        
        Composite_Change,
        
        Ontology_Component,
        Ontology_Class,        
        Ontology_Property,        
        Ontology_Individual,
        
        Timestamp;
    }
    
    
    public enum AnnotationCls {
    	Annotation,
    	
    	Advice,
    	Comment,
    	Example,
    	Explanation,
    	
    	Proposal,
    	SimpleProposal,
    	VotingProposal,
    	AgreeDisagreeVoteProposal,
    	FiveStarsVoteProposal,
    	
    	Question,
    	SeeAlso,	
    	    	
    	Vote,
    	AgreeDisagreeVote,
    	FiveStarsVote;    	
    }
    
    
    public enum ChangeSlot {
        action,
        applyTo,
        annotates,
        associatedAnnotations,
        associatedProperty,
        author,
        body,
        changes,
        context,
        date,
        partOfCompositeChange,
        sequence,
        subChanges,
        timestamp,
        currentName,
        subject,
        voteValue,
    }
    
    public KnowledgeBase getChangeKb() {
        return changes_kb;
    }
    
    /*
     * Hopefully the definition of root will change or - better - go away.
     * These two methods need to be synchronized with ChangesDb.createRootChange
     * while we are figuring this out.
     */
    
    public static boolean isRoot(Change change) {
        return change.getApplyTo() == null;
    }
    
    public static Collection<Change> removeRoots(Collection<Change> changes) {
        Collection<Instance> roots = new ArrayList<Instance>();
        for (Instance change : changes) {
          if (isRoot((Change) change)) {
                roots.add(change);
            }
        }
        changes.removeAll(roots);
        return changes;
    }
    
    @SuppressWarnings("unchecked")
    public List<Change> getSortedChanges() { 
        ProtegeJob job = new GetSortedChangesJob(changes_kb);
        return (List<Change>) job.execute();
    }
    
    @SuppressWarnings("unchecked")
    public List<Change> getSortedTopLevelChanges() {
        ProtegeJob job = new GetSortedTopLevelChangesJob(changes_kb);
        return (List<Change>) job.execute();
    }
    
    public Cls getCls(ChangeCls c) {
        Cls cls = clsMap.get(c);
        if (cls == null) {
            cls = changes_kb.getCls(c.name());
            clsMap.put(c, cls);
        }
        return cls;
    }
    
    public Cls getCls(AnnotationCls c) {
        Cls cls = annotationClsMap.get(c);
        if (cls == null) {
            cls = changes_kb.getCls(c.name());
            annotationClsMap.put(c, cls);
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
    
    public Collection<Instance> getInstances(AnnotationCls cls) {
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
    
    public Instance createInstance(AnnotationCls cls) {
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
            if (change.getTimestamp() != null) {
                log.log(level, "\tCreated = " + ((Timestamp) change.getTimestamp()).getDate());
            }
            else {
                log.log(level, "\tNo timestamp");
            }
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
