package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.smi.protege.event.InstanceEvent;
import edu.stanford.smi.protege.event.InstanceListener;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.ChangesProject;

public class ChangesInstanceListener implements InstanceListener{

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
		
		Instance changeInst = ServerChangesUtil.createChange(
												ChangesProject.getChangesKB(),
												ServerChangesUtil.CHANGETYPE_DIRECTTYPE_ADDED, 
												directType, 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(changeInst);
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
		
		Instance changeInst = ServerChangesUtil.createChange(
												ChangesProject.getChangesKB(),
												ServerChangesUtil.CHANGETYPE_DIRECTTYPE_REMOVED,
												directType, 
												context.toString(), 
												ServerChangesUtil.CHANGE_LEVEL_INFO);
		ChangesProject.createChange(changeInst);
	}
}
