package edu.stanford.smi.protegex.changes.listeners;

import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.changes.ChangeCreateUtil;
import edu.stanford.smi.protegex.changes.ChangesTab;
import java.util.*;


public class ChangesFrameListener implements FrameListener {
	
	public void browserTextChanged(FrameEvent event) {
		
	}

	public void deleted(FrameEvent event) {
		
	}

	public void nameChanged(FrameEvent event) {
		
	}

	public void visibilityChanged(FrameEvent event) {
		
	}

	public void ownFacetAdded(FrameEvent event) {
		
	}

	public void ownFacetRemoved(FrameEvent event) {
		
	}

	public void ownFacetValueChanged(FrameEvent event) {
		
	}

	public void ownSlotAdded(FrameEvent event) {
		
	}

	public void ownSlotRemoved(FrameEvent event) {
		
	}

	public void ownSlotValueChanged(FrameEvent event) {
    	
    	Frame f = event.getFrame();
        if (f instanceof Slot) {
            
        }
        
        if (f instanceof Cls) {
           
          	Cls c = (Cls)f;
         	String cName = c.getName();
            Slot s = event.getSlot();
 		    String sName = s.getName();
 		    StringBuffer context = new StringBuffer();
 	
 		 
 		    
 		    if(sName.equals(":DOCUMENTATION"))
 		    {
              String newSlotValue = CollectionUtilities.toString(c.getOwnSlotValues(event.getSlot()));
              if(newSlotValue.equals(""))
              {
            	//REMOVED DOCUMENTATION
            	context.append("Removed documentation from ");
            	context.append(cName);
            	
            	Instance changeInst = ChangeCreateUtil.createChange(
						ChangesTab.getChangesKB(),
						ChangeCreateUtil.CHANGETYPE_DOCUMENTATION_REMOVED, 
						cName, 
						context.toString(), 
						ChangeCreateUtil.CHANGE_LEVEL_INFO);

               ChangesTab.createChange(changeInst);
		   
            	
              }
              else
              {
            	// ADDED DOCUMENTATION
            	  context.append("Added documentation: ");
            	  context.append(newSlotValue);
            	  context.append(" to: ");
            	  context.append(cName);
            	  Instance changeInst = ChangeCreateUtil.createChange(
    						ChangesTab.getChangesKB(),
    						ChangeCreateUtil.CHANGETYPE_DOCUMENTATION_ADDED, 
    						cName, 
    						context.toString(), 
    						ChangeCreateUtil.CHANGE_LEVEL_INFO);

                  ChangesTab.createChange(changeInst);
            
              }
		      /*ArrayList oldValue = (ArrayList)event.getArgument2();
		      String oldSlotValue = oldValue.toString();
		      if( !oldSlotValue.equals("[]"))
		      {
	            String oldSlotValueMod = oldSlotValue.substring(1,oldSlotValue.length()-1);
		      } // old value of the documentation
*/		      

		   
		    } // Handles documentation slot
		
        }

    }

	
	

}
