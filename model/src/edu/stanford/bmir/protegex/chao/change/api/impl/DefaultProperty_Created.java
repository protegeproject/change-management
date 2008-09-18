package edu.stanford.bmir.protegex.chao.change.api.impl;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.bmir.protegex.chao.annotation.api.Annotation;
import edu.stanford.bmir.protegex.chao.annotation.api.impl.DefaultAnnotation;
import edu.stanford.bmir.protegex.chao.change.api.Composite_Change;
import edu.stanford.bmir.protegex.chao.change.api.Property_Created;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Timestamp;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.impl.DefaultOntology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.impl.DefaultTimestamp;
import edu.stanford.smi.protege.code.generator.wrapping.AbstractWrappedInstance;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;

/**
 * Generated by Protege (http://protege.stanford.edu).
 * Source Class: Property_Created
 *
 * @version generated on Mon Aug 18 21:13:43 GMT-08:00 2008
 */
public class DefaultProperty_Created extends AbstractWrappedInstance
         implements Property_Created {

    public DefaultProperty_Created(Instance instance) {
        super(instance);
    }


    public DefaultProperty_Created() {
    }

    // Slot action

    public String getAction() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getActionSlot());
    }


    public Slot getActionSlot() {
        final String name = "action";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasAction() {
        return hasSlotValues(getActionSlot());
    }


    public void setAction(String newAction) {
        setSlotValue(getActionSlot(), newAction);
    }

    // Slot applyTo

    public Ontology_Component getApplyTo() {
        Object object = getWrappedProtegeInstance().getOwnSlotValue(getApplyToSlot());
        Cls cls = getKnowledgeBase().getCls("Ontology_Component");
        if (object instanceof Instance && ((Instance)object).hasType(cls)) {
            return new DefaultOntology_Component((Instance)object);
        }
        return null;
    }


    public Slot getApplyToSlot() {
        final String name = "applyTo";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasApplyTo() {
        return hasSlotValues(getApplyToSlot());
    }


    public void setApplyTo(Ontology_Component newApplyTo) {
        setSlotValue(getApplyToSlot(), newApplyTo);
    }

    // Slot associatedAnnotations

    public Collection<Annotation> getAssociatedAnnotations() {
        Collection protegeValues = getWrappedProtegeInstance().getOwnSlotValues(getAssociatedAnnotationsSlot());
        Collection<Annotation> values = new ArrayList<Annotation>();
        Cls cls = getKnowledgeBase().getCls("Annotation");
        for (Object object : protegeValues) {
            if (object instanceof Instance && ((Instance)object).hasType(cls)) {
                values.add(new DefaultAnnotation((Instance)object));
            }
        }
        return values;
    }


    public Slot getAssociatedAnnotationsSlot() {
        final String name = "associatedAnnotations";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasAssociatedAnnotations() {
        return hasSlotValues(getAssociatedAnnotationsSlot());
    }


    public void addAssociatedAnnotations(Annotation newAssociatedAnnotations) {
        addSlotValue(getAssociatedAnnotationsSlot(), newAssociatedAnnotations);
    }


    public void removeAssociatedAnnotations(Annotation oldAssociatedAnnotations) {
        removeSlotValue(getAssociatedAnnotationsSlot(), oldAssociatedAnnotations);
    }


    public void setAssociatedAnnotations(Collection<? extends Annotation> newAssociatedAnnotations) {
        setSlotValues(getAssociatedAnnotationsSlot(), newAssociatedAnnotations);
    }

    // Slot author

    public String getAuthor() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getAuthorSlot());
    }


    public Slot getAuthorSlot() {
        final String name = "author";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasAuthor() {
        return hasSlotValues(getAuthorSlot());
    }


    public void setAuthor(String newAuthor) {
        setSlotValue(getAuthorSlot(), newAuthor);
    }

    // Slot context

    public String getContext() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getContextSlot());
    }


    public Slot getContextSlot() {
        final String name = "context";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasContext() {
        return hasSlotValues(getContextSlot());
    }


    public void setContext(String newContext) {
        setSlotValue(getContextSlot(), newContext);
    }

    // Slot creationName

    public String getCreationName() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getCreationNameSlot());
    }


    public Slot getCreationNameSlot() {
        final String name = "creationName";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasCreationName() {
        return hasSlotValues(getCreationNameSlot());
    }


    public void setCreationName(String newCreationName) {
        setSlotValue(getCreationNameSlot(), newCreationName);
    }

    // Slot partOfCompositeChange

    public Composite_Change getPartOfCompositeChange() {
        Object object = getWrappedProtegeInstance().getOwnSlotValue(getPartOfCompositeChangeSlot());
        Cls cls = getKnowledgeBase().getCls("Composite_Change");
        if (object instanceof Instance && ((Instance)object).hasType(cls)) {
            return new DefaultComposite_Change((Instance)object);
        }
        return null;
    }


    public Slot getPartOfCompositeChangeSlot() {
        final String name = "partOfCompositeChange";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasPartOfCompositeChange() {
        return hasSlotValues(getPartOfCompositeChangeSlot());
    }


    public void setPartOfCompositeChange(Composite_Change newPartOfCompositeChange) {
        setSlotValue(getPartOfCompositeChangeSlot(), newPartOfCompositeChange);
    }

    // Slot timestamp

    public Timestamp getTimestamp() {
        Object object = getWrappedProtegeInstance().getOwnSlotValue(getTimestampSlot());
        Cls cls = getKnowledgeBase().getCls("Timestamp");
        if (object instanceof Instance && ((Instance)object).hasType(cls)) {
            return new DefaultTimestamp((Instance)object);
        }
        return null;
    }


    public Slot getTimestampSlot() {
        final String name = "timestamp";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasTimestamp() {
        return hasSlotValues(getTimestampSlot());
    }


    public void setTimestamp(Timestamp newTimestamp) {
        setSlotValue(getTimestampSlot(), newTimestamp);
    }
}
