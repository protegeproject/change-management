package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.SystemFrames;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;


public class ChangesFrameListener extends FrameAdapter {    
	private ChangesDb changes_db;


	public ChangesFrameListener(KnowledgeBase kb) {
		changes_db = ChangesProject.getChangesDb(kb);    
	}

	public void ownSlotValueChanged(FrameEvent event) {

		Frame frame = event.getFrame();
		String frameName = frame.getBrowserText();
		KnowledgeBase kb = frame.getKnowledgeBase();
		SystemFrames systemFrames = kb.getSystemFrames();

		Slot ownSlot = event.getSlot();  
		String newSlotValue = CollectionUtilities.toString(frame.getOwnSlotValues(event.getSlot()));

		StringBuffer context = new StringBuffer();

		if(ownSlot.equals(systemFrames.getDocumentationSlot())) {

			if(newSlotValue.equals("")) {				
				context.append("Removed documentation from ");
				context.append(frameName);
				ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Documentation_Removed, frame, context.toString());
			}
			else {		
				context.append("Added documentation: ");
				context.append(newSlotValue);
				context.append(" to: ");
				context.append(frameName);

				ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Documentation_Added, frame, context.toString());
			}

		} else if (ownSlot.equals(systemFrames.getMaximumValueSlot())) {

			context.append("Maximum value for: ");
			context.append(frameName);
			context.append(" set to: ");
			context.append(newSlotValue);

			ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Maximum_Value, frame, context.toString());

		} else if (ownSlot.equals(systemFrames.getMinimumValueSlot())){
			
			context.append("Minimum value for: ");
			context.append(frameName);
			context.append(" set to: ");
			context.append(newSlotValue);

			ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Minimum_Value, frame, context.toString());

		} else if (ownSlot.equals(systemFrames.getMinimumCardinalitySlot())){
			
			if(!newSlotValue.equals("")){
				context.append("Minimum cardinality for: ");
				context.append(frameName);
				context.append(" set to: ");
				context.append(newSlotValue);

				ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Minimum_Cardinality, frame, context.toString());
			}
		} else if (ownSlot.equals(systemFrames.getMaximumCardinalitySlot())){
			
			if (newSlotValue.equals("")){
				context.append(frameName);
				context.append(" can take multiple values");

				ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Maximum_Cardinality, frame, context.toString());
			}
			else {
				context.append("Maximum cardinality for: ");
				context.append(frameName);
				context.append(" set to: ");
				context.append(newSlotValue);

				ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Maximum_Cardinality, frame, context.toString());
			}
		} else if (ownSlot.isSystem()) {
			//TT: do nothing, it is handled somewhere else (hpefully!)
		} else {
			
			context.append("Set value for ");
			context.append(ownSlot.getBrowserText());
			context.append(" for ");
			context.append(frameName);
			context.append(" to: ");
			context.append(newSlotValue);

			ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Property_Value, frame, context.toString());
		}
	}

}
