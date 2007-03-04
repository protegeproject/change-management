package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.smi.protege.event.InstanceEvent;
import edu.stanford.smi.protege.event.InstanceListener;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;

public class ChangesInstanceListener implements InstanceListener{
    private KnowledgeBase kb;
    private ChangesDb changes_db;
    private KnowledgeBase changesKb;

    public ChangesInstanceListener(KnowledgeBase kb) {
        this.kb = kb;
        changes_db = ChangesProject.getChangesDb(kb);
        changesKb = ChangesProject.getChangesKB(kb);
    }
    /* (non-Javadoc)
     * @see edu.stanford.smi.protege.event.InstanceListener#directTypeAdded(edu.stanford.smi.protege.event.InstanceEvent)
     */
    public void directTypeAdded(InstanceEvent event) {

        String directType = event.getInstance().getDirectType().getBrowserText();
        String directTypeName = event.getInstance().getDirectType().getName();
        String instName = event.getInstance().getBrowserText();

        StringBuffer context = new StringBuffer();
        context.append("Direct Type Added: ");
        context.append(directType);
        context.append(" (added to: ");
        context.append(instName);
        context.append(")");

        Ontology_Component applyTo = changes_db.getOntologyComponent(instName, true);

        Change change = changes_db.createChange(ChangeCls.DirectType_Added);
        changes_db.finalizeChange(change, applyTo, context.toString(), ChangeModel.CHANGE_LEVEL_INFO);
    }

    /* (non-Javadoc)
     * @see edu.stanford.smi.protege.event.InstanceListener#directTypeRemoved(edu.stanford.smi.protege.event.InstanceEvent)
     */
    public void directTypeRemoved(InstanceEvent event) {
        String directTypeName = event.getInstance().getDirectType().getName();
        String directTypeText = event.getInstance().getDirectType().getBrowserText();
        String instName = changes_db.getPossiblyDeletedBrowserText(event.getInstance());
        String instText = event.getInstance().getBrowserText();

        StringBuffer context = new StringBuffer();
        context.append("Direct Type Removed: ");
        context.append(directTypeText);
        context.append(" (removed from: ");
        context.append(instText);
        context.append(")");

        Ontology_Component applyTo = changes_db.getOntologyComponent(instName, true);

        Change change = changes_db.createChange(ChangeCls.DirectType_Removed);
        changes_db.finalizeChange(change, applyTo, context.toString(), ChangeModel.CHANGE_LEVEL_INFO);
    }
}
