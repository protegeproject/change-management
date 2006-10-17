package edu.stanford.smi.protegex.changes.listeners.owl;

import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.changes.ChangeCreateUtil;
import edu.stanford.smi.protegex.changes.ChangesTab;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import java.util.*;

public class OwlChangesFrameListener implements FrameListener {
	
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
            ArrayList oldValue = (ArrayList)event.getArgument2();
		    String oldSlotValue = oldValue.toString();
 		    String sName = s.getName();
 		
 		    StringBuffer context = new StringBuffer();
 		    String newSlotValue = CollectionUtilities.toString(c.getOwnSlotValues(event.getSlot()));
 		
 		    
 		    if(s instanceof DefaultOWLDatatypeProperty){
 		        DefaultOWLDatatypeProperty sProp = (DefaultOWLDatatypeProperty)s;
 		        boolean isAnnotation = sProp.isAnnotationProperty();
 		        
 		        if(isAnnotation){
 		        	if(newSlotValue == null || newSlotValue.equals("")){
 		        	  context.append("Annotation Removed: ");
 		        	  context.append(sName);
 		        	  context.append(" from class: ");
 		        	  context.append(cName);
 		        	  
// 		     		if (!ChangesTab.getIsInTransaction()) {
// 		   			ChangesTab.createTransactionChange(ChangesTab.TRANS_SIGNAL_TRANS_BEGIN);
// 		   			ChangesTab.setInRemoveAnnotation(true);
// 		   		    } 
 		        	  
 		        	  Instance changeInst = ChangeCreateUtil.createChange(
 								ChangesTab.getChangesKB(),
 								ChangeCreateUtil.CHANGETYPE_ANNOTATION_REMOVED, 
 								cName, 
 								context.toString(), 
 								ChangeCreateUtil.CHANGE_LEVEL_INFO);

 		               ChangesTab.createChange(changeInst);
 		        	}//Annotation deleted
 		        	else{
 		        		boolean isAdd = true;
 		        		if( !oldSlotValue.equals("[]"))
 		 		        {
 		 	              String oldSlotValueMod = oldSlotValue.substring(1,oldSlotValue.length()-1);
 		 	              String slotCompare = oldSlotValueMod.concat(", ");
 		 	              //System.out.println("OLD: "+oldSlotValueMod+"Length: "+oldSlotValueMod.length());
 		 	              //System.out.println("SLOT COMPARE: "+slotCompare+"Length: "+slotCompare.length());
 		 	              //System.out.println("NEW: "+newSlotValue+"Length: "+newSlotValue.length());
 		 	              if(newSlotValue.equals(slotCompare)|| newSlotValue.concat(", ").equals(oldSlotValueMod))
 		 	            	  isAdd = false;
 		 		        } // if the old & new values of the slot are same - don't count it as an annotation added change.
 		        		
 		        		if(isAdd){
 		        	
 		        		context.append("Annotation Added: ");
 	 		        	context.append(sName);
 	 		        	context.append(": ");
 	 		        
 	 		        	context.append("'");
 	 		        	context.append(newSlotValue);
 	 		        	context.append("'");
 	 		        	context.append(" to class: ");
 	 		        	context.append(cName);
 	 		        	  
 	 		        	Instance changeInst = ChangeCreateUtil.createChange(
 	 								ChangesTab.getChangesKB(),
 	 								ChangeCreateUtil.CHANGETYPE_ANNOTATION_ADDED, 
 	 								cName, 
 	 								context.toString(), 
 	 								ChangeCreateUtil.CHANGE_LEVEL_INFO);

 	 		            ChangesTab.createChange(changeInst);
 	 		            
// 	 		      	    if (ChangesTab.getIsInTransaction() && ChangesTab.getInRemoveAnnotation()) {
// 	 					ChangesTab.createTransactionChange(ChangesTab.TRANS_SIGNAL_TRANS_END);
// 	 					ChangesTab.setInRemoveAnnotation(false);
// 	 				    }
		        		}
 		        	}
 		        }// handles annotations
 		    }
 		    
 		    
 		    if(sName.equals("owl:disjointWith"))
 		    { 
              
            
              context.append("Added disjoint class(es): ");
              context.append(newSlotValue);
              context.append(" to: ");
              context.append(cName);
            	
              Instance changeInst = ChangeCreateUtil.createChange(
						ChangesTab.getChangesKB(),
						ChangeCreateUtil.CHANGETYPE_DISJOINT_CLASS_ADDED, 
						cName, 
						context.toString(), 
						ChangeCreateUtil.CHANGE_LEVEL_INFO);

               ChangesTab.createChange(changeInst);
		   
            	
           
		   
		    } // Handles disjoints
		
        }

    }

	

}
