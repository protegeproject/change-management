package edu.stanford.smi.protegex.server_changes;



import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Class_Created;
import edu.stanford.smi.protegex.server_changes.model.generated.Class_Deleted;
import edu.stanford.smi.protegex.server_changes.model.generated.Created_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Deleted_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Name_Changed;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;

public class ServerChangesUtil {
    private static final Logger log = Log.getLogger(ServerChangesUtil.class);
    
    private ChangeModel model;
	
	private ServerChangesUtil(ChangeModel model) {
	    this.model = model;
    }
    
    public static Change createChangeStd(ChangesDb changes_db,
                                         ChangeCls type, 
                                         String applyTo,
                                         String context) {
        Ontology_Component frame = changes_db.getOntologyComponent(applyTo, true);
        Change change = changes_db.createChange(type);
        changes_db.finalizeChange(change, frame, context);
        return change;
    }
    

    public static Created_Change createCreatedChange(ChangesDb changes_db,
                                                     ChangeCls type,
                                                     String name,
                                                     boolean createTransaction) {
        
        String context;
        switch (type) {
        case Class_Created:
            context = "Class";
            break;
        case Slot_Created:
            context = "Slot";
            break;
        case Property_Created:
            context = "Property";
            break;
        case Instance_Created:
            context = "Instance";
            break;
        default:
            throw new IllegalArgumentException("Change type " + type + " is not a create frame type");
        }
        context = context + " Created: " + name;

        if (createTransaction) {
            // Create artifical transaction for create class
            changes_db.getTransactionState().beginTransaction(context);
            changes_db.setInCreateClass(true);
        }
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(name, true);
        applyTo.setCurrentName(name);
        
        Class_Created change = (Class_Created) changes_db.createChange(type);
        change.setCreationName(name);
        changes_db.finalizeChange(change, applyTo, context);
        return change;
    }
    
    public static Deleted_Change createDeletedChange(ChangesDb changes_db,
                                                     ChangeCls type,
                                                     String name) {
        String context;
        switch (type) {
        case Class_Deleted:
            context = "Class";
            break;
        case Slot_Deleted:
            context = "Slot";
            break;
        case Property_Deleted:
            context = "Property";
            break;
        case Instance_Deleted:
            context = "Instance";
            break;
        default:
            throw new IllegalArgumentException("Change type " + type + " is not a create frame type");
        }
        context = context + " Deleted: " + name;

        Ontology_Component applyTo = changes_db.getOntologyComponent(name, true);
        applyTo.setCurrentName(null);
    
        Class_Deleted change = (Class_Deleted) changes_db.createChange(type);
        change.setDeletionName(name);
        changes_db.finalizeChange(change, applyTo, context);
        return change;
    }
    
    public static Name_Changed createNameChange(ChangesDb changes_db,
                                         String oldName,
                                         String newName) {

        StringBuffer context = new StringBuffer();
        context.append("Name change from '");
        context.append(oldName);
        context.append("' to '");
        context.append(newName);
        context.append("'");
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(oldName, true);
        applyTo.setCurrentName(newName);
        
        Name_Changed change = (Name_Changed) changes_db.createChange(ChangeCls.Name_Changed);
        change.setOldName(oldName);
        change.setNewName(newName);
        changes_db.finalizeChange(change, applyTo, context.toString());
        return change;
    }
                                                  
    
	
	public Annotation createAnnotation(String annotType,Collection annotatables) {
        Annotation a = (Annotation) model.createInstance(ChangeCls.Annotation);
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
