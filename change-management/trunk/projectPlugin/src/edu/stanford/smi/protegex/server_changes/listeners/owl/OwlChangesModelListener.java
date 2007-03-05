package edu.stanford.smi.protegex.server_changes.listeners.owl;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Class_Created;
import edu.stanford.smi.protegex.server_changes.model.generated.Name_Changed;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;
import edu.stanford.smi.protegex.server_changes.model.generated.Property_Created;

public class OwlChangesModelListener extends ModelAdapter{
    private OWLModel om;
    private ChangesDb changes_db;
    private KnowledgeBase changesKb;


    public OwlChangesModelListener(OWLModel om) {
        this.om = om;
        changes_db = ChangesProject.getChangesDb(om);
        changesKb = changes_db.getChangesKb();
    }

    public void classCreated(RDFSClass arg0) {
        String clsName = arg0.getName();
        String clsText = arg0.getBrowserText();
        String context = "Created Class: " + clsText;
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(clsName, true);
        applyTo.setCurrentName(clsName);
        
        Class_Created change = (Class_Created) changes_db.createChange(ChangeCls.Class_Created);
        change.setCreationName(clsName);
        changes_db.finalizeChange(change, applyTo, context.toString(), ChangeModel.CHANGE_LEVEL_INFO);
    }




    public void classDeleted(RDFSClass arg0) {
        // This can't be done here because of the missing name field.  Look in the kb listener.
    }

    public void individualCreated(RDFResource arg0) {
    }

    public void individualDeleted(RDFResource arg0) {
    }

    public void propertyCreated(RDFProperty arg0) {
        String propName = arg0.getName();
        String propText = arg0.getBrowserText();
        String context = "Property Created: " + propText;
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(propName, true);
        
        Property_Created change = (Property_Created) changes_db.createChange(ChangeCls.Property_Created);
        change.setCreationName(propName);
        changes_db.finalizeChange(change, applyTo, context.toString(), ChangeModel.CHANGE_LEVEL_INFO);
    }

    public void propertyDeleted(RDFProperty arg0) {
        // This can't be done here because of the missing name field.  Look in the kb listener.
    }

    public void resourceNameChanged(RDFResource arg0, String arg1) {
        String oldName = arg1;
        String newName = arg0.getName();

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
        changes_db.finalizeChange(change, applyTo, context.toString(), ChangeModel.CHANGE_LEVEL_INFO);
    }
}
