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
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;

public class ServerChangesUtil {
    private static final Logger log = Log.getLogger(ServerChangesUtil.class);
    
    private ChangeModel model;
	
	private ServerChangesUtil(ChangeModel model) {
	    this.model = model;
    }
    

	
	public Annotation createAnnotation(String annotType,Collection annotatables) {
        Annotation a = (Annotation) model.createInstance(ChangeCls.Annotation);
        a.setAnnotates(annotatables);
        a.setCreated(new Timestamp(model));
        ChangeModel.logAnnotatableThing("Creating change for annotation", log, Level.FINE, a);
            // we need to determine what will trigger the listener
		return a;
	}
	
	public Annotation updateAnnotation(KnowledgeBase kb, Annotation a) {
	    a.setModified(new Timestamp(model));
        a.setAuthor(ChangesProject.getUserName(kb));
        if (a.getBody() == null) {
            a.setBody("");
        }
		ChangeModel.logAnnotatableThing("Updated Annotation", log, Level.FINE, a);
		return a;	
	}
    
    public static Instance createTransChange(KnowledgeBase cKb, Collection transChanges, Instance repInst) {
        Slot applyTo = cKb.getSlot(Model.SLOT_NAME_APPLYTO);
        Slot context = cKb.getSlot(Model.SLOT_NAME_CONTEXT);
        return createTransChange(cKb, transChanges, repInst, 
                                 (String) repInst.getOwnSlotValue(applyTo),
                                 (String) repInst.getOwnSlotValue(context));
    }
    
    public static Instance createTransChange(KnowledgeBase cKb, 
                                             Collection transChanges, 
                                             Instance repInst,
                                             String altApplyTo,
                                             String altContext) {
    	Cls transChange = cKb.getCls(Model.CHANGETYPE_TRANS_CHANGE);
		Instance tInst = cKb.createInstance(null, new ArrayList());
		
		Slot applyTo = cKb.getSlot(Model.SLOT_NAME_APPLYTO);
		Slot author = cKb.getSlot(Model.SLOT_NAME_AUTHOR);
		Slot action = cKb.getSlot(Model.SLOT_NAME_ACTION);
        Slot context = cKb.getSlot(Model.SLOT_NAME_CONTEXT);
		Slot type = cKb.getSlot(Model.SLOT_NAME_TYPE);
		Slot changes = cKb.getSlot(Model.SLOT_NAME_CHANGES);
		
		tInst.setOwnSlotValue(author, repInst.getOwnSlotValue(author));
		tInst.setOwnSlotValue(action, repInst.getOwnSlotValue(action));
		tInst.setOwnSlotValue(context, altContext);
        Timestamp.getTimestamp().setTimestamp(tInst);
		tInst.setOwnSlotValue(applyTo, altApplyTo);
		tInst.setOwnSlotValue(type, Model.CHANGE_LEVEL_DISP_TRANS);
		tInst.setOwnSlotValues(changes, transChanges);
        ChangeModel.logAnnotatableThing("Creating Transaction Change", log, Level.FINE, tInst, transChange);
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
			changeInst.setOwnSlotValue(type, typ);	
		}
		else{
		    changeInst.setOwnSlotValue(action, change.getName());
		    changeInst.setOwnSlotValue(applyTo, apply);
		    changeInst.setOwnSlotValue(author, ChangesProject.getUserName(currentKB));
		    changeInst.setOwnSlotValue(context, desc);
            Timestamp.getTimestamp().setTimestamp(changeInst);
		    changeInst.setOwnSlotValue(type, typ);
		}
        ChangeModel.logAnnotatableThing("Creating change", log, Level.FINE, changeInst, change);
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
		ChangeModel.logAnnotatableThing("Modified name change change", log, Level.FINE, changeInst);
		return changeInst;
	}
    
    public static Collection<Instance> removeRoots(Collection<Instance> changes) {
        Collection<Instance> roots = new ArrayList<Instance>();
        for (Instance change : changes) {
            if (Model.getType(change).equals(Model.CHANGE_LEVEL_ROOT)) {
                roots.add(change);
            }
        }
        changes.removeAll(roots);
        return changes;
    }
 }
