package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.smi.protege.event.InstanceEvent;
import edu.stanford.smi.protege.event.InstanceListener;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.model.Model;

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
                String directTypeName = event.getInstance().getDirectType().getName();
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
												directTypeName, 
												context.toString(), 
												Model.CHANGE_LEVEL_INFO);
		ChangesProject.postProcessChange(kb, changesKb, changeInst);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.smi.protege.event.InstanceListener#directTypeRemoved(edu.stanford.smi.protege.event.InstanceEvent)
	 */
	public void directTypeRemoved(InstanceEvent event) {
		String directTypeName = event.getInstance().getDirectType().getName();
		String directTypeText = event.getInstance().getDirectType().getBrowserText();
		String instName = event.getInstance().getName();
                String instText = event.getInstance().getBrowserText();
		
		StringBuffer context = new StringBuffer();
		context.append("Direct Type Removed: ");
		context.append(directTypeText);
		context.append(" (removed from: ");
		context.append(instText);
		context.append(")");
		
		Instance changeInst = ServerChangesUtil.createChange(kb,
                                                                     changesKb,
                                                                     Model.CHANGETYPE_DIRECTTYPE_REMOVED,
                                                                     directTypeName, 
                                                                     context.toString(), 
                                                                     Model.CHANGE_LEVEL_INFO);
		ChangesProject.postProcessChange(kb, changesKb, changeInst);
	}
}
