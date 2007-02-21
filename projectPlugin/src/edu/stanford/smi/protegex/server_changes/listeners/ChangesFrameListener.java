package edu.stanford.smi.protegex.server_changes.listeners;

import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.ChangesProject;

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
        	Slot s = (Slot)f;
         	String sName = s.getName();
            Slot ownS = event.getSlot();
 		    String ownSName = ownS.getName();
 		    String newSlotValue = CollectionUtilities.toString(s.getOwnSlotValues(event.getSlot()));
 		
		    StringBuffer context = new StringBuffer();
		    if(ownSName.equals(":SLOT-NUMERIC-MAXIMUM")){
		    	   context.append("Maximum value for: ");
	               context.append(sName);
	               context.append(" set to: ");
	               context.append(newSlotValue);
	               Instance changeInst = ServerChangesUtil.createChange(
	       						ChangesProject.getChangesKB(),
	       						ServerChangesUtil.CHANGETYPE_MAXIMUM_VALUE, 
	       						sName, 
	       						context.toString(), 
	       						ServerChangesUtil.CHANGE_LEVEL_INFO);

	           	ChangesProject.createChange(changeInst);
		    }
            if(ownSName.equals(":SLOT-NUMERIC-MINIMUM")){
         	   context.append("Minimum value for: ");
               context.append(sName);
               context.append(" set to: ");
               context.append(newSlotValue);
               Instance changeInst =ServerChangesUtil.createChange(
            		        ChangesProject.getChangesKB(),
            		        ServerChangesUtil.CHANGETYPE_MINIMUM_VALUE, 
       						sName, 
       						context.toString(), 
       						ServerChangesUtil.CHANGE_LEVEL_INFO);

           	ChangesProject.createChange(changeInst);
		    	
		    }
		    

        	if(ownSName.equals(":SLOT-MINIMUM-CARDINALITY")){
        		if(!newSlotValue.equals("")){
        			//should have atleast - value
        	      context.append("Minimum cardinality for: ");
               	  context.append(sName);
               	  context.append(" set to: ");
               	  context.append(newSlotValue);
               	  Instance changeInst =ServerChangesUtil.createChange(
       						ChangesProject.getChangesKB(),
       						ServerChangesUtil.CHANGETYPE_MINIMUM_CARDINALITY, 
       						sName, 
       						context.toString(), 
       						ServerChangesUtil.CHANGE_LEVEL_INFO);

              	ChangesProject.createChange(changeInst);
        			
        		}
        		
        	}
            if(ownSName.equals(":SLOT-MAXIMUM-CARDINALITY")){
            	if(newSlotValue.equals("")){
            		//slot can take multiple values
          	      
               	  context.append(sName);
               	  context.append(" can take multiple values");
               	  
               	  Instance changeInst = ServerChangesUtil.createChange(
       						ChangesProject.getChangesKB(),
       						ServerChangesUtil.CHANGETYPE_MAXIMUM_CARDINALITY, 
       						sName, 
       						context.toString(), 
       						ServerChangesUtil.CHANGE_LEVEL_INFO);

              	ChangesProject.createChange(changeInst);
            	}
            	else{
            		//maximum values set to -
          	      context.append("Maximum cardinality for: ");
               	  context.append(sName);
               	  context.append(" set to: ");
               	  context.append(newSlotValue);
               	  Instance changeInst =ServerChangesUtil.createChange(
       						ChangesProject.getChangesKB(),
       						ServerChangesUtil.CHANGETYPE_MAXIMUM_CARDINALITY, 
       						sName, 
       						context.toString(), 
       						ServerChangesUtil.CHANGE_LEVEL_INFO);
              	ChangesProject.createChange(changeInst);
            	}
        		
        	}
        }
        
        else if (f instanceof Cls) {
           
          	Cls c = (Cls)f;
         	String cName = c.getBrowserText();
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
            	
            	Instance changeInst = ServerChangesUtil.createChange(
						ChangesProject.getChangesKB(),
						ServerChangesUtil.CHANGETYPE_DOCUMENTATION_REMOVED, 
						cName, 
						context.toString(), 
						ServerChangesUtil.CHANGE_LEVEL_INFO);

            	ChangesProject.createChange(changeInst);
		   
            	
              }
              else
              {
            	// ADDED DOCUMENTATION
            	  context.append("Added documentation: ");
            	  context.append(newSlotValue);
            	  context.append(" to: ");
            	  context.append(cName);
            	  Instance changeInst = ServerChangesUtil.createChange(
    						ChangesProject.getChangesKB(),
    						ServerChangesUtil.CHANGETYPE_DOCUMENTATION_ADDED, 
    						cName, 
    						context.toString(), 
    						ServerChangesUtil.CHANGE_LEVEL_INFO);
            		ChangesProject.createChange(changeInst);
            
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
        
        else if (f instanceof Instance){
    		Instance i = (Instance)f;
         	String iName = i.getBrowserText();
            Slot ownS = event.getSlot();
 		    String ownSName = ownS.getName();
 		    String newSlotValue = CollectionUtilities.toString(i.getOwnSlotValues(event.getSlot()));
 		    ArrayList oldValue = (ArrayList)event.getArgument2();
		    String oldSlotValue = oldValue.toString();
		    
		    StringBuffer context = new StringBuffer();
		    context.append("Slot: ");
		    context.append(ownSName);
		    context.append(" for Instance: ");
		    context.append(iName);
		    context.append(" set to: ");
		    context.append(newSlotValue);
            Instance changeInst = ServerChangesUtil.createChange(
    						ChangesProject.getChangesKB(),
    						ServerChangesUtil.CHANGETYPE_SLOT_VALUE, 
    						iName, 
    						context.toString(), 
    						ServerChangesUtil.CHANGE_LEVEL_INFO);

        	ChangesProject.createChange(changeInst);
		    
    	}

    }

	
	

}
