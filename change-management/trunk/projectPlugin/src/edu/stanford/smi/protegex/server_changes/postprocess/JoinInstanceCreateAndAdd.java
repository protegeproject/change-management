package edu.stanford.smi.protegex.server_changes.postprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.server.RemoteSession;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Composite_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Individual_Added;
import edu.stanford.smi.protegex.server_changes.model.generated.Individual_Created;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;

public class JoinInstanceCreateAndAdd implements PostProcessor {
    
    private ChangesDb changes_db;
    
    private boolean owl;
    
    private Map<RemoteSession, Individual_Created> lastCreateBySession = new HashMap<RemoteSession, Individual_Created>();

    public void addChange(Change change) {
        if (owl) return;
        
        RemoteSession session = changes_db.getCurrentSession();
        if (change instanceof Individual_Created) {
            lastCreateBySession.put(session, (Individual_Created) change);
            return;
        }
        if (change instanceof Individual_Added) {
            Individual_Created create_op = lastCreateBySession.remove(session);
            Ontology_Component created = (Ontology_Component) change.getApplyTo();
            if (create_op == null || !created.equals(change.getApplyTo())) return;
            
            List<Instance> subChanges = new ArrayList<Instance>();
            subChanges.add(create_op);
            subChanges.add(change);
            
            Composite_Change transaction = (Composite_Change) changes_db.createChange(ChangeCls.Composite_Change);
            transaction.setSubChanges(subChanges);
            changes_db.finalizeChange(transaction, (Ontology_Component) create_op.getApplyTo(), 
                                      "Created Instance " + created.getCurrentName());
        }
    }

    public void initialize(ChangesDb changes_db) {
        this.changes_db = changes_db;
        owl = changes_db.isOwl();
    }

}
