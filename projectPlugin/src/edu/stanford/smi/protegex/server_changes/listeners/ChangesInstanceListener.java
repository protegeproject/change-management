package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.smi.protege.event.InstanceEvent;
import edu.stanford.smi.protege.event.InstanceListener;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.Model;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

public class ChangesInstanceListener implements InstanceListener{
    private KnowledgeBase kb;
    private KnowledgeBase changesKb;
    
    public ChangesInstanceListener(KnowledgeBase kb) {
        this.kb = kb;
        changesKb = ChangesProject.getChangesKB(kb);
    }
	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.InstanceListener#directTypeAdded(edu.stanford.smi.protege.event.InstanceEvent)
	 */
	public void directTypeAdded(InstanceEvent event) {
		
		String directType = event.getInstance().getDirectType().getBrowserText();
		String instName = event.getInstance().getBrowserText();
		
		StringBuffer context = new StringBuffer();
		context.append("Direct Type Added: ");
		context.append(directType);
		context.append(" (added to: ");
		context.append(instName);
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(kb,
												changesKb,
												Model.CHANGETYPE_DIRECTTYPE_ADDED, 
												directType, 
												context.toString(), 
												Model.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(kb, changesKb, changeInst);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.InstanceListener#directTypeRemoved(edu.stanford.smi.protege.event.InstanceEvent)
	 */
	public void directTypeRemoved(InstanceEvent event) {
		String directType = event.getInstance().getDirectType().getBrowserText();
		String instName = event.getInstance().getBrowserText();
		
		StringBuffer context = new StringBuffer();
		context.append("Direct Type Removed: ");
		context.append(directType);
		context.append(" (removed from: ");
		context.append(instName);
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(kb,
												changesKb,
												Model.CHANGETYPE_DIRECTTYPE_REMOVED,
												directType, 
												context.toString(), 
												Model.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(kb, changesKb, changeInst);
	}
}
