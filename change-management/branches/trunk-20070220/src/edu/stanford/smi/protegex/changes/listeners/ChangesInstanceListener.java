package edu.stanford.smi.protegex.changes.listeners;

import edu.stanford.smi.protege.event.InstanceEvent;
import edu.stanford.smi.protege.event.InstanceListener;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.changes.ChangeCreateUtil;
import edu.stanford.smi.protegex.changes.ChangesTab;

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
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_DIRECTTYPE_ADDED, 
												directType, 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		
		ChangesTab.createChange(changeInst);
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
		
		Instance changeInst = ChangeCreateUtil.createChange(
												ChangesTab.getChangesKB(),
												ChangeCreateUtil.CHANGETYPE_DIRECTTYPE_REMOVED,
												directType, 
												context.toString(), 
												ChangeCreateUtil.CHANGE_LEVEL_INFO);
		
		ChangesTab.createChange(changeInst);
	}
}
