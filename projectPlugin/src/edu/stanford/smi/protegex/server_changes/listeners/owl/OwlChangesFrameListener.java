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
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;

public class OwlChangesFrameListener implements FrameListener {
    private OWLModel om;
    private ChangesDb changes_db;
    private KnowledgeBase changesKb;

    public OwlChangesFrameListener(OWLModel kb) {
        om = kb;
        changes_db = ChangesProject.getChangesDb(kb);
        changesKb = changes_db.getChangesKb();
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
        Slot slot = event.getSlot();
        if (f instanceof Slot) {
            // not handled yet...
        }
        else if (f instanceof Cls) {

            Cls c = (Cls)f;
 
            if (slot instanceof RDFProperty && ((RDFProperty) slot).isAnnotationProperty()) {
                handleAnnotation(c, (RDFProperty) slot, event);
            }
            else if (slot.getName().equals("owl:disjointWith")) { 
                handleOwlDisjoint(c, slot, event);
            } // Handles disjoints

        }

        else if (f instanceof Instance){
            if (!slot.getName().equals("rdf:type")) {
                handleInstanceSlotValueChange((Instance) f, slot, event);
            }
        }

    }
    

    private void handleAnnotation(Cls c, RDFProperty prop, FrameEvent event) {
        String cText = c.getBrowserText();
        String cName = c.getName();
        Slot s = event.getSlot();
        ArrayList oldSlotValues = (ArrayList) event.getOldValues();
        String sName = s.getName();

        StringBuffer context = new StringBuffer();
        Collection newSlotValues = c.getOwnSlotValues(prop);
        if ((newSlotValues == null && oldSlotValues  == null) || newSlotValues.equals(oldSlotValues)) {
            return;
        }
        
        Ontology_Component applyTo = changes_db.getOntologyComponent(c, true);
        Ontology_Component ontologyAnnotation = changes_db.getOntologyComponent(s, true);
        
        if (newSlotValues == null || newSlotValues.isEmpty()){
            context.append("Annotation Removed: ");
            context.append(sName);
            context.append(" from class: ");
            context.append(cText);

            Annotation_Change change = (Annotation_Change) changes_db.createChange(ChangeCls.Annotation_Removed);
            change.setAssociatedProperty(ontologyAnnotation);
            changes_db.finalizeChange(change, applyTo, context.toString());
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

            Annotation_Change change = (Annotation_Change) changes_db.createChange(ChangeCls.Annotation_Added);
            change.setAssociatedProperty(ontologyAnnotation);
            changes_db.finalizeChange(change, applyTo, context.toString());
        }
        else {
            context.append("Annotation Modified: ");
            context.append("annotation ");
            context.append(sName);
            context.append(" for class: ");
            context.append(cName);
            context.append(" set to: ");
            context.append(newSlotValues);

            Annotation_Change change = (Annotation_Change) changes_db.createChange(ChangeCls.Annotation_Modified);
            change.setAssociatedProperty(ontologyAnnotation);
            changes_db.finalizeChange(change, applyTo, context.toString());
        }
    }
    
    private void handleOwlDisjoint(Cls c, Slot slot, FrameEvent event) {
        String cText = c.getBrowserText();
        String cName = c.getName();
        Slot s = event.getSlot();
        ArrayList oldSlotValues = (ArrayList) event.getOldValues();
        String sName = s.getName();
        StringBuffer context = new StringBuffer();
        
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
        
        ServerChangesUtil.createChangeStd(changes_db, ChangeCls.DisjointClass_Added, c, context.toString());
    }
    
    private void handleInstanceSlotValueChange(Instance i, Slot ownS, FrameEvent event) {
        String iName = i.getName();
        String iText = i.getBrowserText();
        String ownSName = ownS.getName();
        String newSlotValue = CollectionUtilities.toString(i.getOwnSlotValues(event.getSlot()));
        ArrayList oldValue = (ArrayList)event.getArgument2();

        StringBuffer context = new StringBuffer();
        context.append("Slot: ");
        context.append(ownSName);
        context.append(" for Instance: ");
        context.append(iText);
        context.append(" set to: ");
        context.append(newSlotValue);

        ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Property_Value, i, context.toString());
    }





}
