package edu.stanford.smi.protegex.server_changes;



import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeSlot;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Composite_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Created_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Deleted_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Name_Changed;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Property;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;

public class ServerChangesUtil {
    private static final Logger log = Log.getLogger(ServerChangesUtil.class);
    
    private ChangeModel model;
	
	private ServerChangesUtil(ChangeModel model) {
	    this.model = model;
    }

    
    public static Change createChangeStd(ChangesDb changes_db,
                                         ChangeCls type, 
                                         Frame applyTo,
                                         String context) {
        Ontology_Component frame = changes_db.getOntologyComponent(applyTo, true);
        Change change = changes_db.createChange(type);
        changes_db.finalizeChange(change, frame, context);
        return change;
    }
    

    public static Created_Change createCreatedChange(ChangesDb changes_db,
                                                     ChangeCls type,
                                                     Frame applyTo,
                                                     String name) {
        String context;
//      TT: Do we want to make the difference between a slot and a property here?
        switch (type) {
        case Class_Created:
            context = "Class";
            break;
        case Property_Created:
            context = "Property";
            break;
        case Individual_Created:
            context = "Instance";
            break;
        default:
            throw new IllegalArgumentException("Change type " + type + " is not a create frame type");
        }
        
        context = context + " Created: " + name;
        
        Ontology_Component oc = changes_db.getOntologyComponent(applyTo, true);
        oc.setCurrentName(name);
        
        Created_Change change = (Created_Change) changes_db.createChange(type);
        change.setCreationName(name);
        changes_db.finalizeChange(change, oc, context);
        return change;
    }
    
    public static Deleted_Change createDeletedChange(ChangesDb changes_db,
                                                     ChangeCls type,
                                                     Frame frame,
                                                     String name) {
        String context;
//      TT: Do we want to make the difference between a slot and a property here?
        switch (type) {
        case Class_Deleted:
            context = "Class";
            break;
        case Property_Deleted:
            context = "Property";
            break;
        case Individual_Deleted:
            context = "Instance";
            break;
        default:
            throw new IllegalArgumentException("Change type " + type + " is not a create frame type");
        }
        context = context + " Deleted: " + name;

        Ontology_Component applyTo = changes_db.getOntologyComponent(frame, true);
        applyTo.setCurrentName(null);
    
        Deleted_Change change = (Deleted_Change) changes_db.createChange(type);
        change.setDeletionName(name);
        changes_db.finalizeChange(change, applyTo, context);
        changes_db.updateDeletedFrameIdToNameMap(frame.getFrameID(), change);
        return change;
    }
    
    public static Name_Changed createNameChange(ChangesDb changes_db,
                                                Frame applyTo,
                                                String oldName,
                                                String newName) {

        StringBuffer context = new StringBuffer();
        context.append("Name change from '");
        context.append(oldName);
        context.append("' to '");
        context.append(newName);
        context.append("'");
        
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        applyToOc.setCurrentName(newName);
        
        Name_Changed change = (Name_Changed) changes_db.createChange(ChangeCls.Name_Changed);
        change.setOldName(oldName);
        change.setNewName(newName);
        changes_db.finalizeChange(change, applyToOc, context.toString());
        return change;
    }
    
    public static Composite_Change createTransactionChange(ChangesDb changes_db,
                                                           Frame applyTo,
                                                           String context,
                                                           List<Change> changes) {
        Ontology_Component oc = changes_db.getOntologyComponent(applyTo, true);
        return createTransactionChange(changes_db, oc, context, changes);
    }
                                                  
    public static Composite_Change createTransactionChange(ChangesDb changes_db,
                                                           Ontology_Component applyTo,
                                                           String context,
                                                           List<Change> changes) {
        Composite_Change transaction = (Composite_Change) changes_db.createChange(ChangeCls.Composite_Change);
        transaction.setSubChanges(changes);
        changes_db.finalizeChange(transaction, applyTo, context);
        return transaction;
    }
    
    public static Change createChangeWithSlot(ChangesDb changes_db,
                                              ChangeCls type,
                                              Frame applyTo,
                                              String context,
                                              Slot slot) {
        Ontology_Component oc = changes_db.getOntologyComponent(applyTo, true);
        Change change = (Change) changes_db.createChange(type);
        ChangeModel model = changes_db.getModel();
        
        Ontology_Property s = (Ontology_Property) changes_db.getOntologyComponent(slot, true);
        change.setDirectOwnSlotValue(model.getSlot(ChangeSlot.associatedProperty), s);
        
        changes_db.finalizeChange(change, oc, context);
        return change;
    }
    
    public static Change createChangeWithProperty(ChangesDb changes_db,
                                                  ChangeCls type,
                                                  Frame applyTo,
                                                  String context,
                                                  RDFProperty property) {
        Ontology_Component oc = changes_db.getOntologyComponent(applyTo, true);
        Change change = (Change) changes_db.createChange(type);
        ChangeModel model = changes_db.getModel();
        
        Ontology_Property p = (Ontology_Property) changes_db.getOntologyComponent(property, true);
        change.setDirectOwnSlotValue(model.getSlot(ChangeSlot.associatedProperty), p);
        
        changes_db.finalizeChange(change, oc, context);
        return change;
    }
                                                  
	
	public Annotation createAnnotation(ChangeCls annotType,Collection annotatables) {
        Annotation a = (Annotation) model.createInstance(annotType);
        a.setAnnotates(annotatables);
        a.setCreated(Timestamp.getTimestamp(model));
        ChangeModel.logAnnotatableThing("Creating change for annotation", log, Level.FINE, a);
            // we need to determine what will trigger the listener
		return a;
	}
	
	public Annotation updateAnnotation(KnowledgeBase kb, Annotation a) {
	    a.setModified(Timestamp.getTimestamp(model));
        a.setAuthor(ChangesProject.getUserName(kb));
        if (a.getBody() == null) {
            a.setBody("");
        }
		ChangeModel.logAnnotatableThing("Updated Annotation", log, Level.FINE, a);
		return a;	
	}
 }
