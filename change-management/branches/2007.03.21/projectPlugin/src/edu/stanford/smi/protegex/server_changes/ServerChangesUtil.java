package edu.stanford.smi.protegex.server_changes;



import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;

import edu.stanford.smi.protegex.server_changes.model.generated.*;

import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeSlot;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Composite_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Created_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Deleted_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Name_Changed;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Property;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;


public class ServerChangesUtil {
    private static final Logger log = Log.getLogger(ServerChangesUtil.class);
    
    private ChangeModel model;
	
	private ServerChangesUtil(ChangeModel model) {
	    this.model = model;
    }

    
    public static Change createChangeStd(ChangesDb changes_db,
                                         ChangeCls type, 
                                         Frame applyTo,
                                         String context) {
        Ontology_Component frame = changes_db.getOntologyComponent(applyTo, true);
        Change change = changes_db.createChange(type);
        changes_db.finalizeChange(change, frame, context);
        return change;
    }
    

    public static Created_Change createCreatedChange(ChangesDb changes_db,
                                                     ChangeCls type,
                                                     Frame applyTo,
                                                     String name) {
        String context;
//      TT: Do we want to make the difference between a slot and a property here?
        switch (type) {
        case Class_Created:
            context = "Class";
            break;
        case Property_Created:
            context = "Property";
            break;
        case Individual_Created:
            context = "Instance";
            break;
        default:
            throw new IllegalArgumentException("Change type " + type + " is not a create frame type");
        }
        
        context = context + " Created: " + name;
        
        Ontology_Component oc = changes_db.getOntologyComponent(applyTo, true);
        oc.setCurrentName(name);
        
        Created_Change change = (Created_Change) changes_db.createChange(type);
        change.setCreationName(name);
        changes_db.finalizeChange(change, oc, context);
        return change;
    }
    
    public static Deleted_Change createDeletedChange(ChangesDb changes_db,
                                                     ChangeCls type,
                                                     Frame frame,
                                                     String name) {
        String context;
//      TT: Do we want to make the difference between a slot and a property here?
        switch (type) {
        case Class_Deleted:
            context = "Class";
            break;
        case Property_Deleted:
            context = "Property";
            break;
        case Individual_Deleted:
            context = "Instance";
            break;
        default:
            throw new IllegalArgumentException("Change type " + type + " is not a create frame type");
        }
        context = context + " Deleted: " + name;

        Ontology_Component applyTo = changes_db.getOntologyComponent(frame, true);
        applyTo.setCurrentName(null);
    
        Deleted_Change change = (Deleted_Change) changes_db.createChange(type);
        change.setDeletionName(name);
        changes_db.finalizeChange(change, applyTo, context);
        changes_db.updateDeletedFrameIdToNameMap(frame.getFrameID(), change);
        return change;
    }
    
    public static Name_Changed createNameChange(ChangesDb changes_db,
                                                Frame applyTo,
                                                String oldName,
                                                String newName) {

        StringBuffer context = new StringBuffer();
        context.append("Name change from '");
        context.append(oldName);
        context.append("' to '");
        context.append(newName);
        context.append("'");
        
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        applyToOc.setCurrentName(newName);
        
        Name_Changed change = (Name_Changed) changes_db.createChange(ChangeCls.Name_Changed);
        change.setOldName(oldName);
        change.setNewName(newName);
        changes_db.finalizeChange(change, applyToOc, context.toString());
        return change;
    }
    
    

    public static Composite_Change createTransactionChange(ChangesDb changes_db,
                                                           Frame applyTo,
                                                           String context,
                                                           List<Change> changes) {
        Ontology_Component oc = changes_db.getOntologyComponent(applyTo, true);
        return createTransactionChange(changes_db, oc, context, changes);
    }
                                                  
    public static Composite_Change createTransactionChange(ChangesDb changes_db,
                                                           Ontology_Component applyTo,
                                                           String context,
                                                           List<Change> changes) {
        Composite_Change transaction = (Composite_Change) changes_db.createChange(ChangeCls.Composite_Change);
        transaction.setSubChanges(changes);
        changes_db.finalizeChange(transaction, applyTo, context);
        return transaction;
    }
    
    public static Change createChangeWithSlot(ChangesDb changes_db,
                                              ChangeCls type,
                                              Frame applyTo,
                                              String context,
                                              Slot slot) {
        Ontology_Component oc = changes_db.getOntologyComponent(applyTo, true);
        Change change = (Change) changes_db.createChange(type);
        ChangeModel model = changes_db.getModel();
        
        Ontology_Property s = (Ontology_Property) changes_db.getOntologyComponent(slot, true);
        change.setDirectOwnSlotValue(model.getSlot(ChangeSlot.associatedProperty), s);
        
        changes_db.finalizeChange(change, oc, context);
        return change;
    }
    
    public static Change createChangeWithProperty(ChangesDb changes_db,
                                                  ChangeCls type,
                                                  Frame applyTo,
                                                  String context,
                                                  RDFProperty property) {
        Ontology_Component oc = changes_db.getOntologyComponent(applyTo, true);
        Change change = (Change) changes_db.createChange(type);
        ChangeModel model = changes_db.getModel();
        
        Ontology_Property p = (Ontology_Property) changes_db.getOntologyComponent(property, true);
        change.setDirectOwnSlotValue(model.getSlot(ChangeSlot.associatedProperty), p);
        
        changes_db.finalizeChange(change, oc, context);
        return change;
    }
                                                  
	
	public Annotation createAnnotation(ChangeCls annotType,Collection annotatables) {
        Annotation a = (Annotation) model.createInstance(annotType);
        a.setAnnotates(annotatables);
        a.setCreated(Timestamp.getTimestamp(model));
        ChangeModel.logAnnotatableThing("Creating change for annotation", log, Level.FINE, a);
            // we need to determine what will trigger the listener
		return a;
	}
	
	public Annotation updateAnnotation(KnowledgeBase kb, Annotation a) {
	    a.setModified(Timestamp.getTimestamp(model));
        a.setAuthor(ChangesProject.getUserName(kb));
        if (a.getBody() == null) {
            a.setBody("");
        }
		ChangeModel.logAnnotatableThing("Updated Annotation", log, Level.FINE, a);
		return a;	
	}
	
    public static DomainProperty_Added createDomainPropertyAddedChange(ChangesDb changes_db, Frame applyTo, String value, Frame prop){

        
    	String desc = "Added domain property: "+value+" to: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        Ontology_Property p = (Ontology_Property) changes_db.getOntologyComponent(prop, true);
        //applyToOc.setCurrentName(value);

        DomainProperty_Added change = (DomainProperty_Added) changes_db.createChange(ChangeCls.DomainProperty_Added);
        change.setAssociatedProperty(p);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    public static DomainProperty_Removed createDomainPropertyRemovedChange(ChangesDb changes_db, Frame applyTo, String value, Frame prop){

        
    	String desc = "Removed domain property: "+value+" from: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        Ontology_Property p = (Ontology_Property) changes_db.getOntologyComponent(prop, true);
        //applyToOc.setCurrentName(value);

        DomainProperty_Removed change = (DomainProperty_Removed) changes_db.createChange(ChangeCls.DomainProperty_Removed);
        change.setAssociatedProperty(p);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
 /*   public static TemplateSlotValue_Added createTemplateSlotValueAddedChange(ChangesDb changes_db, Frame applyTo, String slot, String facet, String value){

        
    	String desc = "Added template slot value: "+value+" to: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        //applyToOc.setCurrentName(value);

        TemplateSlotValue_Added change = (TemplateSlotValue_Added) changes_db.createChange(ChangeCls.TemplateSlotValue_Added);
        change.setValue(value);
        change.setSlot(slot);
        change.setFacet(facet);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    
    public static TemplateSlotValue_Modified createTemplateSlotValueModifiedChange(ChangesDb changes_db, Frame applyTo, String slot, String facet, String oldVal, String newVal){

        
    	String desc = "Modified template slot value from: "+oldVal+" to: "+newVal;
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        //applyToOc.setCurrentName(newVal);

        TemplateSlotValue_Modified change = (TemplateSlotValue_Modified) changes_db.createChange(ChangeCls.TemplateSlotValue_Modified);
        change.setOldValue(oldVal);
        change.setNewValue(newVal);
        change.setFacet(facet);
        change.setSlot(slot);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    public static TemplateSlotValue_Removed createTemplateSlotValueRemovedChange(ChangesDb changes_db, Frame applyTo, String slot, String facet, String value){

        
    	String desc = "Removed template slot value: "+value+" from: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        //applyToOc.setCurrentName(value);

        TemplateSlotValue_Removed change = (TemplateSlotValue_Removed) changes_db.createChange(ChangeCls.TemplateSlotValue_Removed);
        change.setValue(value);
        change.setSlot(slot);
        change.setFacet(facet);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    public static OwnSlot_Added createOwnSlotAddedChange(ChangesDb changes_db, Frame applyTo, String slot, String facet, String value){

        
    	String desc = "Added own slot: "+value+" to: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        //applyToOc.setCurrentName(value);

        OwnSlot_Added change = (OwnSlot_Added) changes_db.createChange(ChangeCls.OwnSlot_Added);
        change.setValue(value);
        change.setSlot(slot);
        change.setFacet(facet);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    public static OwnSlot_Removed createOwnSlotRemovedChange(ChangesDb changes_db, Frame applyTo, String slot, String facet, String value){

        
    	String desc = "Removed own slot: "+value+" from: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        //applyToOc.setCurrentName(value);

        OwnSlot_Removed change = (OwnSlot_Removed) changes_db.createChange(ChangeCls.OwnSlot_Removed);
        change.setValue(value);
        change.setSlot(slot);
        change.setFacet(facet);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    public static OwnSlotValue_Added createOwnSlotValueAddedChange(ChangesDb changes_db, Frame applyTo, String slot, String facet, String value){

        
    	String desc = "Added own slot value: "+value+" to: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        //applyToOc.setCurrentName(value);

        OwnSlotValue_Added change = (OwnSlotValue_Added) changes_db.createChange(ChangeCls.OwnSlotValue_Added);
        change.setValue(value);
        change.setSlot(slot);
        change.setFacet(facet);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    
    public static OwnSlotValue_Modified createOwnSlotValueModifiedChange(ChangesDb changes_db, Frame applyTo, String slot, String facet, String oldVal, String newVal){

        
    	String desc = "Modified own slot value from: "+oldVal+" to: "+newVal;
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        //applyToOc.setCurrentName(newVal);

        OwnSlotValue_Modified change = (OwnSlotValue_Modified) changes_db.createChange(ChangeCls.OwnSlotValue_Modified);
        change.setOldValue(oldVal);
        change.setNewValue(newVal);
        change.setFacet(facet);
        change.setSlot(slot);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    public static OwnSlotValue_Removed createOwnSlotValueRemovedChange(ChangesDb changes_db, Frame applyTo, String slot, String facet, String value){

        
    	String desc = "Removed own slot value: "+value+" from: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        //applyToOc.setCurrentName(value);

        OwnSlotValue_Removed change = (OwnSlotValue_Removed) changes_db.createChange(ChangeCls.OwnSlotValue_Removed);
        change.setValue(value);
        change.setSlot(slot);
        change.setFacet(facet);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    */
    public static Facet_Added createFacetAddedChange(ChangesDb changes_db, Frame applyTo, String value){

        
    	String desc = "Added facet: "+value+" to: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        //applyToOc.setCurrentName(value);

        Facet_Added change = (Facet_Added) changes_db.createChange(ChangeCls.Facet_Added);
        change.setValue(value);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    public static Facet_Removed createFacetRemovedChange(ChangesDb changes_db, Frame applyTo, String value){

        
    	String desc = "Removed facet: "+value+" from: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        //applyToOc.setCurrentName(value);

        Facet_Removed change = (Facet_Removed) changes_db.createChange(ChangeCls.Facet_Removed);
        change.setValue(value);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    public static FacetValue_Added createFacetValueAddedChange(ChangesDb changes_db, Frame applyTo, Facet facet, String value){

        
    	String desc = "Added facet value: "+value+" to: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        Ontology_Facet f = (Ontology_Facet) changes_db.getOntologyComponent(facet, true);
        //applyToOc.setCurrentName(value);

        FacetValue_Added change = (FacetValue_Added) changes_db.createChange(ChangeCls.FacetValue_Added);
        change.setValue(value);
        change.setAssociatedFacet(f);

       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    
    public static FacetValue_Modified createFacetValueModifiedChange(ChangesDb changes_db, Frame applyTo, Facet facet, String oldVal, String newVal){

        
    	String desc = "Modified facet value from: "+oldVal+" to: "+newVal;
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        Ontology_Facet f = (Ontology_Facet) changes_db.getOntologyComponent(facet, true);
        
        //applyToOc.setCurrentName(newVal);

        FacetValue_Modified change = (FacetValue_Modified) changes_db.createChange(ChangeCls.FacetValue_Modified);
        change.setOldValue(oldVal);
        change.setNewValue(newVal);
        change.setAssociatedFacet(f);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    public static FacetValue_Removed createFacetValueRemovedChange(ChangesDb changes_db, Frame applyTo, Facet facet, String value){

        
    	String desc = "Removed facet value: "+value+" from: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        Ontology_Facet f = (Ontology_Facet) changes_db.getOntologyComponent(facet, true);
        //applyToOc.setCurrentName(value);

        FacetValue_Removed change = (FacetValue_Removed) changes_db.createChange(ChangeCls.FacetValue_Removed);
        change.setValue(value);
        change.setAssociatedFacet(f);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    public static Restriction_Added createRestrictionAddedChange(ChangesDb changes_db, Frame applyTo, String value, Frame restr){

        
    	String desc = "Added restriction: "+value+" to: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        Ontology_Class c = (Ontology_Class) changes_db.getOntologyComponent(restr, true);
        //applyToOc.setCurrentName(value);

        Restriction_Added change = (Restriction_Added) changes_db.createChange(ChangeCls.Restriction_Added);
        change.setAssociatedRestriction(c);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    
    public static Restriction_Modified createRestrictionModifiedChange(ChangesDb changes_db, Frame applyTo, String oldVal, String newVal, Frame restr){

        
    	String desc = "Modified restriction from: "+oldVal+" to: "+newVal;
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        Ontology_Class c = (Ontology_Class) changes_db.getOntologyComponent(restr, true);
        //applyToOc.setCurrentName(value);

        Restriction_Modified change = (Restriction_Modified) changes_db.createChange(ChangeCls.Restriction_Modified);
        change.setOldValue(oldVal);
        change.setNewValue(newVal);
        change.setAssociatedRestriction(c);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    
    public static Restriction_Removed createRestrictionRemovedChange(ChangesDb changes_db, Frame applyTo, String value, Frame restr){

        
    	String desc = "Removed restriction: "+value+" from: "+applyTo.getName();
		
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        Ontology_Class c = (Ontology_Class) changes_db.getOntologyComponent(restr, true);
        //applyToOc.setCurrentName(value);

        Restriction_Removed change = (Restriction_Removed) changes_db.createChange(ChangeCls.Restriction_Removed);
        change.setAssociatedRestriction(c);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    public static Property_Created createPropertyAddedChange(ChangesDb changes_db, Frame applyTo, String value){

        
    	String desc = "Added property: "+value+" to: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        applyToOc.setCurrentName(value);

        Property_Created change = (Property_Created) changes_db.createChange(ChangeCls.Property_Created);

       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
    
    public static Property_Deleted createPropertyDeletedChange(ChangesDb changes_db, Frame applyTo, String value){

        
    	String desc = "Removed property: "+value+" from: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        applyToOc.setCurrentName(null);

        Property_Deleted change = (Property_Deleted) changes_db.createChange(ChangeCls.Property_Deleted);

        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
    
   public static PropertyValue_Added createPropertyValueAddedChange(ChangesDb changes_db, Frame applyTo, String value, Slot prop){

        
    	String desc = "Added property value: "+value+" to: "+applyTo.getName();
        Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
        Ontology_Property p = (Ontology_Property) changes_db.getOntologyComponent(prop, true);
        //applyToOc.setCurrentName(value);

        PropertyValue_Added change = (PropertyValue_Added) changes_db.createChange(ChangeCls.PropertyValue_Added);
        change.setValue(value);
        change.setAssociatedProperty(p);
       
        changes_db.finalizeChange(change, applyToOc, desc);
        return change;
    }
   
   
   public static PropertyValue_Removed createPropertyValueDeletedChange(ChangesDb changes_db, Frame applyTo, String value, Slot prop){

       
   	   String desc = "Removed property value: "+value+" from: "+applyTo.getName();
       Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
       Ontology_Property p = (Ontology_Property) changes_db.getOntologyComponent(prop, true);
       //applyToOc.setCurrentName(value);

       PropertyValue_Removed change = (PropertyValue_Removed) changes_db.createChange(ChangeCls.PropertyValue_Removed);
       change.setValue(value);
       change.setAssociatedProperty(p);
      
       changes_db.finalizeChange(change, applyToOc, desc);
       return change;
   }
   
   
   
   public static PropertyValue_Modified createPropertyValueModifiedChange(ChangesDb changes_db, Frame applyTo, String oldVal, String newVal, Slot prop){

       
   	   String desc = "Modified property value from: "+oldVal+" to: "+newVal;
       Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
       Ontology_Property p = (Ontology_Property) changes_db.getOntologyComponent(prop, true);
       //applyToOc.setCurrentName(value);

       PropertyValue_Modified change = (PropertyValue_Modified) changes_db.createChange(ChangeCls.PropertyValue_Modified);
       change.setOldValue(oldVal);
       change.setNewValue(newVal);
       change.setAssociatedProperty(p);
      
       changes_db.finalizeChange(change, applyToOc, desc);
       return change;
   }
    
    
   
   public static SufficientCondition_Added createSufficientConditionAddedChange(ChangesDb changes_db, Frame applyTo, String property, String restriction, String value){

       
	   String desc = "Added sufficient condition: "+value+" to: "+applyTo.getName();
       Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
       //applyToOc.setCurrentName(value);

       SufficientCondition_Added change = (SufficientCondition_Added) changes_db.createChange(ChangeCls.SufficientCondition_Added);
       change.setValue(value);
       change.setProperty(property);
       change.setRestriction(restriction);
      
       changes_db.finalizeChange(change, applyToOc, desc);
       return change;
   }
  
  
  public static SufficientCondition_Removed createSufficientConditionDeletedChange(ChangesDb changes_db, Frame applyTo, String property, String restriction, String value){

      
	  String desc = "Removed sufficient condition: "+value+" from: "+applyTo.getName();
      Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
      //applyToOc.setCurrentName(value);

      SufficientCondition_Removed change = (SufficientCondition_Removed) changes_db.createChange(ChangeCls.SufficientCondition_Removed);
      change.setValue(value);
      change.setProperty(property);
      change.setRestriction(restriction);
     
      changes_db.finalizeChange(change, applyToOc, desc);
      return change;
  }
  
  
  
  public static SufficientCondition_Modified createSufficientConditionModifiedChange(ChangesDb changes_db, Frame applyTo, String property, String restriction, String oldVal, String newVal){

      
	  String desc = "Modified sufficient condition from: "+oldVal+" to: "+newVal;
      Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
      //applyToOc.setCurrentName(value);

      SufficientCondition_Modified change = (SufficientCondition_Modified) changes_db.createChange(ChangeCls.SufficientCondition_Modified);
      change.setOldValue(oldVal);
      change.setNewValue(newVal);
      change.setProperty(property);
      change.setRestriction(restriction);
     
      changes_db.finalizeChange(change, applyToOc, desc);
      return change;
  }
   
    
  public static Superclass_Added createNecessaryConditionAddedChange(ChangesDb changes_db, Frame applyTo,String value){

      
	  String desc = "Added superclass: "+value+" to: "+applyTo.getName();
      Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
      //applyToOc.setCurrentName(value);

      Superclass_Added change = (Superclass_Added) changes_db.createChange(ChangeCls.Superclass_Added);

     
      changes_db.finalizeChange(change, applyToOc, desc);
      return change;
  }
 
 
 
 public static Superclass_Removed createNecessaryConditionRemovedChange(ChangesDb changes_db, Frame applyTo,String value){

      
	  String desc = "Removed superclass: "+value+" from: "+applyTo.getName();
      Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
      //applyToOc.setCurrentName(value);

      Superclass_Removed change = (Superclass_Removed) changes_db.createChange(ChangeCls.Superclass_Removed);

     
      changes_db.finalizeChange(change, applyToOc, desc);
      return change;
  }
 
 
 public static Superclass_Modified createNecessaryConditionModifiedChange(ChangesDb changes_db, Frame applyTo,String oldVal, String newVal){

     
	 String desc = "Modified superclass from: "+oldVal+" to: "+newVal;
     Ontology_Component applyToOc = changes_db.getOntologyComponent(applyTo, true);
     //applyToOc.setCurrentName(value);

     Superclass_Modified change = (Superclass_Modified) changes_db.createChange(ChangeCls.Superclass_Modified);

    
     changes_db.finalizeChange(change, applyToOc, desc);
     return change;
 }
 
 
   
    

    
    
 }
