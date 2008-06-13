package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.smi.protege.event.InstanceEvent;
import edu.stanford.smi.protege.event.InstanceListener;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;

public class ChangesInstanceListener implements InstanceListener{    
    private ChangesDb changes_db;    

    public ChangesInstanceListener(KnowledgeBase kb) {
        changes_db = ChangesProject.getChangesDb(kb);
    }
    /* (non-Javadoc)
     * @see edu.stanford.smi.protege.event.InstanceListener#directTypeAdded(edu.stanford.smi.protege.event.InstanceEvent)
     */
    public void directTypeAdded(InstanceEvent event) {

        String directType = event.getInstance().getDirectType().getBrowserText();
        
        Instance inst = event.getInstance();
        String instName = inst.getBrowserText();

        StringBuffer context = new StringBuffer();
        context.append("Direct Type Added: ");
        context.append(directType);
        context.append(" (added to: ");
        context.append(instName);
        context.append(")");
        
        ServerChangesUtil.createChangeStd(changes_db, ChangeCls.DirectType_Added, inst, context.toString());
    }

    /* (non-Javadoc)
     * @see edu.stanford.smi.protege.event.InstanceListener#directTypeRemoved(edu.stanford.smi.protege.event.InstanceEvent)
     */
    public void directTypeRemoved(InstanceEvent event) {        
        String directTypeText = event.getInstance().getDirectType().getBrowserText();        
        String instText = event.getInstance().getBrowserText();

        StringBuffer context = new StringBuffer();
        context.append("Direct Type Removed: ");
        context.append(directTypeText);
        context.append(" (removed from: ");
        context.append(instText);
        context.append(")");
        
        ServerChangesUtil.createChangeStd(changes_db, ChangeCls.DirectType_Removed, event.getInstance(), context.toString());
    }
}
