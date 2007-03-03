package edu.stanford.smi.protegex.server_changes.listeners.owl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

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
import edu.stanford.smi.protegex.server_changes.model.Model;

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

	@SuppressWarnings("unchecked")
    public void ownSlotValueChanged(FrameEvent event) {

	    Frame f = event.getFrame();
	    if (f instanceof Slot) {
	        // not handled yet...
	    }
	    else if (f instanceof Cls) {

	        Cls c = (Cls)f;
            Slot slot = event.getSlot();
	        String cText = c.getBrowserText();
	        String cName = c.getName();
	        Slot s = event.getSlot();
	        ArrayList oldSlotValues = (ArrayList) event.getOldValues();
	        String sName = s.getName();

	        StringBuffer context = new StringBuffer();
            
	        if(s instanceof DefaultOWLDatatypeProperty){
	            DefaultOWLDatatypeProperty sProp = (DefaultOWLDatatypeProperty)s;
	            boolean isAnnotation = sProp.isAnnotationProperty();

	            if(isAnnotation){
                    Collection newSlotValues = c.getOwnSlotValues(slot);
                    if ((newSlotValues == null && oldSlotValues  == null) || newSlotValues.equals(oldSlotValues)) {
                        return;
                    }
                    if (newSlotValues == null || newSlotValues.isEmpty()){
	                    context.append("Annotation Removed: ");
	                    context.append(sName);
	                    context.append(" from class: ");
	                    context.append(cText);

	                    Instance changeInst = ServerChangesUtil.createChange(om,
	                                                                         changesKb,
	                                                                         Model.CHANGETYPE_ANNOTATION_REMOVED, 
	                                                                         cName, 
	                                                                         context.toString(), 
	                                                                         Model.CHANGE_LEVEL_INFO);

	                    ChangesProject.postProcessChange(om,changesKb, changeInst);
	                }//Annotation deleted
	                else if (oldSlotValues == null || oldSlotValues.isEmpty()) {


	                        context.append("Annotation Added: ");
	                        context.append(sName);
	                        context.append(": ");

	                        context.append("'");
	                        context.append(CollectionUtilities.toString(newSlotValues));
	                        context.append("'");
	                        context.append(" to class: ");
	                        context.append(cName);

	                        Instance changeInst = ServerChangesUtil.createChange(om,
	                                                                             changesKb,
	                                                                             Model.CHANGETYPE_ANNOTATION_ADDED, 
	                                                                             cName, 
	                                                                             context.toString(), 
	                                                                             Model.CHANGE_LEVEL_INFO);

	                        ChangesProject.postProcessChange(om,changesKb, changeInst);

	                }
                    else {
                        context.append("Annotation Modified: ");
                        context.append("annotation ");
                        context.append(sName);
                        context.append(" for class: ");
                        context.append(cName);
                        
                        Instance changeInst = ServerChangesUtil.createChange(om,
                                                                             changesKb,
                                                                             Model.CHANGETYPE_ANNOTATION_MODIFIED, 
                                                                             cName, 
                                                                             context.toString(), 
                                                                             Model.CHANGE_LEVEL_INFO);

                        ChangesProject.postProcessChange(om, changesKb, changeInst);
                    }
	            }// handles annotations
	        }


	        if(sName.equals("owl:disjointWith")) { 
                Collection newSlotValues = c.getOwnSlotValues(slot);
                Collection deleted = new HashSet(oldSlotValues);
                deleted.removeAll(newSlotValues);
                Collection added = new HashSet(newSlotValues);
                added.removeAll(oldSlotValues);
                if (added.isEmpty() && deleted.isEmpty()) {
                    return;
                }
                if (!added.isEmpty()) {
                    context.append("Added disjoint class(es): ");
                    context.append(CollectionUtilities.toString(added));
                }
                if (!deleted.isEmpty()) {
                    context.append("removed disjoint class(es): ");
                    context.append(CollectionUtilities.toString(deleted));
                }
	            context.append(" to: ");
	            context.append(cName);

	            Instance changeInst = ServerChangesUtil.createChange(om,
	                                                                 changesKb,
	                                                                 Model.CHANGETYPE_DISJOINT_CLASS_ADDED, 
	                                                                 cName, 
	                                                                 context.toString(), 
	                                                                 Model.CHANGE_LEVEL_INFO);



	            ChangesProject.postProcessChange(om,changesKb, changeInst);

	        } // Handles disjoints

	    }
        
        else if (f instanceof Instance){
    		Instance i = (Instance)f;
         	String iName = i.getName();
                String iText = i.getBrowserText();
                Slot ownS = event.getSlot();
                String ownSName = ownS.getName();
    		String newSlotValue = CollectionUtilities.toString(i.getOwnSlotValues(event.getSlot()));
                ArrayList oldValue = (ArrayList)event.getArgument2();
                String oldSlotValue = oldValue.toString();
    	    
    	    StringBuffer context = new StringBuffer();
    	    if(!ownSName.equals("rdf:type")) {
                context.append("Slot: ");
                context.append(ownSName);
                context.append(" for Instance: ");
                context.append(iText);
                context.append(" set to: ");
                context.append(newSlotValue);
                Instance changeInst = ServerChangesUtil.createChange(om,
                                                                     changesKb,
                                                                     Model.CHANGETYPE_SLOT_VALUE, 
                                                                     iName, 
                                                                     context.toString(), 
                                                                     Model.CHANGE_LEVEL_INFO);
                ChangesProject.postProcessChange(om,changesKb, changeInst);
         
    	    } 
    	}

    }
	
	

	

}
