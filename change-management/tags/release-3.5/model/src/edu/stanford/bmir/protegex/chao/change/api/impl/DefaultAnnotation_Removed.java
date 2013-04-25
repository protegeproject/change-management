package edu.stanford.bmir.protegex.chao.change.api.impl;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.bmir.protegex.chao.annotation.api.Annotation;
import edu.stanford.bmir.protegex.chao.annotation.api.impl.DefaultAnnotation;
import edu.stanford.bmir.protegex.chao.change.api.Annotation_Removed;
import edu.stanford.bmir.protegex.chao.change.api.Composite_Change;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Property;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Timestamp;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.impl.DefaultOntology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.impl.DefaultOntology_Property;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.impl.DefaultTimestamp;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;

/**
 * Generated by Protege (http://protege.stanford.edu).
 * Source Class: Annotation_Removed
 *
 * @version generated on Mon Aug 18 21:13:43 GMT-08:00 2008
 */
public class DefaultAnnotation_Removed extends DefaultAnnotation_Change
         implements Annotation_Removed {

    private static final long serialVersionUID = -3577968176744079797L;


    public DefaultAnnotation_Removed(Instance instance) {
        super(instance);
    }


    public DefaultAnnotation_Removed() {
    }

    // Slot action

    @Override
	public String getAction() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getActionSlot());
    }


    @Override
	public Slot getActionSlot() {
        final String name = "action";
        return getKnowledgeBase().getSlot(name);
    }


    @Override
	public boolean hasAction() {
        return hasSlotValues(getActionSlot());
    }


    @Override
	public void setAction(String newAction) {
        setSlotValue(getActionSlot(), newAction);
    }

    // Slot applyTo

    @Override
	public Ontology_Component getApplyTo() {
        Object object = getWrappedProtegeInstance().getOwnSlotValue(getApplyToSlot());
        Cls cls = getKnowledgeBase().getCls("Ontology_Component");
        if (object instanceof Instance && ((Instance)object).hasType(cls)) {
            return new DefaultOntology_Component((Instance)object);
        }
        return null;
    }


    @Override
	public Slot getApplyToSlot() {
        final String name = "applyTo";
        return getKnowledgeBase().getSlot(name);
    }


    @Override
	public boolean hasApplyTo() {
        return hasSlotValues(getApplyToSlot());
    }


    @Override
	public void setApplyTo(Ontology_Component newApplyTo) {
        setSlotValue(getApplyToSlot(), newApplyTo);
    }

    // Slot associatedAnnotations

    @Override
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


    @Override
	public Slot getAssociatedAnnotationsSlot() {
        final String name = "associatedAnnotations";
        return getKnowledgeBase().getSlot(name);
    }


    @Override
	public boolean hasAssociatedAnnotations() {
        return hasSlotValues(getAssociatedAnnotationsSlot());
    }


    @Override
	public void addAssociatedAnnotations(Annotation newAssociatedAnnotations) {
        addSlotValue(getAssociatedAnnotationsSlot(), newAssociatedAnnotations);
    }


    @Override
	public void removeAssociatedAnnotations(Annotation oldAssociatedAnnotations) {
        removeSlotValue(getAssociatedAnnotationsSlot(), oldAssociatedAnnotations);
    }


    @Override
	public void setAssociatedAnnotations(Collection<? extends Annotation> newAssociatedAnnotations) {
        setSlotValues(getAssociatedAnnotationsSlot(), newAssociatedAnnotations);
    }

    // Slot associatedProperty

    @Override
	public Ontology_Property getAssociatedProperty() {
        Object object = getWrappedProtegeInstance().getOwnSlotValue(getAssociatedPropertySlot());
        Cls cls = getKnowledgeBase().getCls("Ontology_Property");
        if (object instanceof Instance && ((Instance)object).hasType(cls)) {
            return new DefaultOntology_Property((Instance)object);
        }
        return null;
    }


    @Override
	public Slot getAssociatedPropertySlot() {
        final String name = "associatedProperty";
        return getKnowledgeBase().getSlot(name);
    }


    @Override
	public boolean hasAssociatedProperty() {
        return hasSlotValues(getAssociatedPropertySlot());
    }


    @Override
	public void setAssociatedProperty(Ontology_Property newAssociatedProperty) {
        setSlotValue(getAssociatedPropertySlot(), newAssociatedProperty);
    }

    // Slot author

    @Override
	public String getAuthor() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getAuthorSlot());
    }


    @Override
	public Slot getAuthorSlot() {
        final String name = "author";
        return getKnowledgeBase().getSlot(name);
    }


    @Override
	public boolean hasAuthor() {
        return hasSlotValues(getAuthorSlot());
    }


    @Override
	public void setAuthor(String newAuthor) {
        setSlotValue(getAuthorSlot(), newAuthor);
    }

    // Slot context

    @Override
	public String getContext() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getContextSlot());
    }


    @Override
	public Slot getContextSlot() {
        final String name = "context";
        return getKnowledgeBase().getSlot(name);
    }


    @Override
	public boolean hasContext() {
        return hasSlotValues(getContextSlot());
    }


    @Override
	public void setContext(String newContext) {
        setSlotValue(getContextSlot(), newContext);
    }

    // Slot partOfCompositeChange

    @Override
	public Composite_Change getPartOfCompositeChange() {
        Object object = getWrappedProtegeInstance().getOwnSlotValue(getPartOfCompositeChangeSlot());
        Cls cls = getKnowledgeBase().getCls("Composite_Change");
        if (object instanceof Instance && ((Instance)object).hasType(cls)) {
            return new DefaultComposite_Change((Instance)object);
        }
        return null;
    }


    @Override
	public Slot getPartOfCompositeChangeSlot() {
        final String name = "partOfCompositeChange";
        return getKnowledgeBase().getSlot(name);
    }


    @Override
	public boolean hasPartOfCompositeChange() {
        return hasSlotValues(getPartOfCompositeChangeSlot());
    }


    @Override
	public void setPartOfCompositeChange(Composite_Change newPartOfCompositeChange) {
        setSlotValue(getPartOfCompositeChangeSlot(), newPartOfCompositeChange);
    }

    // Slot timestamp

    @Override
	public Timestamp getTimestamp() {
        Object object = getWrappedProtegeInstance().getOwnSlotValue(getTimestampSlot());
        Cls cls = getKnowledgeBase().getCls("Timestamp");
        if (object instanceof Instance && ((Instance)object).hasType(cls)) {
            return new DefaultTimestamp((Instance)object);
        }
        return null;
    }


    @Override
	public Slot getTimestampSlot() {
        final String name = "timestamp";
        return getKnowledgeBase().getSlot(name);
    }


    @Override
	public boolean hasTimestamp() {
        return hasSlotValues(getTimestampSlot());
    }


    @Override
	public void setTimestamp(Timestamp newTimestamp) {
        setSlotValue(getTimestampSlot(), newTimestamp);
    }
}
