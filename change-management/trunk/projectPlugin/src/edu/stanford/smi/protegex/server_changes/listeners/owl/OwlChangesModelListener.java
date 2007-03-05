package edu.stanford.smi.protegex.server_changes.listeners.owl;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
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
        ServerChangesUtil.createCreatedChange(changes_db, ChangeCls.Class_Created, clsName, true);
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
        
        ServerChangesUtil.createCreatedChange(changes_db, ChangeCls.Property_Created, propName, true);
    }

    public void propertyDeleted(RDFProperty arg0) {
        // This can't be done here because of the missing name field.  Look in the kb listener.
    }

    public void resourceNameChanged(RDFResource arg0, String arg1) {
        String oldName = arg1;
        String newName = arg0.getName();

        ServerChangesUtil.createNameChange(changes_db, oldName, newName);
    }
}
