package edu.stanford.smi.protegex.server_changes.listeners.owl;

import java.util.ArrayList;

import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

public class OwlChangesFrameListener implements FrameListener {
    private OWLModel om;
    private KnowledgeBase changesKb;
    
    public OwlChangesFrameListener(OWLModel kb) {
        om = kb;
        changesKb = ChangesProject.getChangesKB(kb);
    }	
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
        
    	
        
        else if (f instanceof Cls) {
           
          	Cls c = (Cls)f;
         	String cName = c.getBrowserText();
            Slot s = event.getSlot();
            ArrayList oldValue = (ArrayList)event.getArgument2();
 		    String sName = s.getName();
 		
 		    StringBuffer context = new StringBuffer();
 		    String newSlotValue = CollectionUtilities.toString(c.getOwnSlotValues(event.getSlot()));
 		    String newSlotValue1 = CollectionUtilities.toString(c.getOwnSlotValues(event.getSlot()));
 		    
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
// 		   			ChangesTab.createTransactionChange(om, ChangesTab.TRANS_SIGNAL_TRANS_BEGIN);
// 		   			ChangesTab.setInRemoveAnnotation(true);
// 		   		    } 
 		        	  
 		        	  Instance changeInst = ServerChangesUtil.createChange(om,
 								changesKb,
 								ServerChangesUtil.CHANGETYPE_ANNOTATION_REMOVED, 
 								cName, 
 								context.toString(), 
 								ServerChangesUtil.CHANGE_LEVEL_INFO);

 		        		ChangesProject.createChange(om,changesKb, changeInst);
 		        	}//Annotation deleted
 		        	else{
 		        		boolean isAdd = true;
 		        		if(!(oldValue == null) && !oldValue.isEmpty()) {
 		        		    String oldSlotValue = oldValue.toString();        
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
 	 		        	  
 	 		        	Instance changeInst = ServerChangesUtil.createChange(om,
 	 								changesKb,
 	 								ServerChangesUtil.CHANGETYPE_ANNOTATION_ADDED, 
 	 								cName, 
 	 								context.toString(), 
 	 								ServerChangesUtil.CHANGE_LEVEL_INFO);

 	 		      	ChangesProject.createChange(om,changesKb, changeInst);
 	 		            
// 	 		      	    if (ChangesTab.getIsInTransaction() && ChangesTab.getInRemoveAnnotation()) {
// 	 					ChangesTab.createTransactionChange(om, ChangesTab.TRANS_SIGNAL_TRANS_END);
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
            	
              Instance changeInst = ServerChangesUtil.createChange(om,
						changesKb,
						ServerChangesUtil.CHANGETYPE_DISJOINT_CLASS_ADDED, 
						cName, 
						context.toString(), 
						ServerChangesUtil.CHANGE_LEVEL_INFO);

            
            	
          	ChangesProject.createChange(om,changesKb, changeInst);
		   
		    } // Handles disjoints
		
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
    	    if(!ownSName.equals("rdf:type")){
    	    context.append("Slot: ");
    	    context.append(ownSName);
    	    context.append(" for Instance: ");
    	    context.append(iName);
    	    context.append(" set to: ");
    	    context.append(newSlotValue);
            Instance changeInst = ServerChangesUtil.createChange(om,
    						changesKb,
    						ServerChangesUtil.CHANGETYPE_SLOT_VALUE, 
    						iName, 
    						context.toString(), 
    						ServerChangesUtil.CHANGE_LEVEL_INFO);
        	ChangesProject.createChange(om,changesKb, changeInst);
         
    	    } 
    	}

    }
	
	

	

}
