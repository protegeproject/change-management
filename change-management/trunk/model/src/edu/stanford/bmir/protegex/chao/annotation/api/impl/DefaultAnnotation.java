package edu.stanford.bmir.protegex.chao.annotation.api.impl;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.bmir.protegex.chao.annotation.api.AnnotatableThing;
import edu.stanford.bmir.protegex.chao.annotation.api.Annotation;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Timestamp;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.impl.DefaultTimestamp;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;

/**
 * Generated by Protege (http://protege.stanford.edu).
 * Source Class: Annotation
 *
 * @version generated on Mon Aug 18 21:11:09 GMT-08:00 2008
 */
public class DefaultAnnotation extends DefaultAnnotatableThing
         implements Annotation {

    public DefaultAnnotation(Instance instance) {
        super(instance);
    }


    public DefaultAnnotation() {
    }

    // Slot annotates

    public Collection<AnnotatableThing> getAnnotates() {
        Collection protegeValues = getWrappedProtegeInstance().getOwnSlotValues(getAnnotatesSlot());
        Collection<AnnotatableThing> values = new ArrayList<AnnotatableThing>();
        Cls cls = getKnowledgeBase().getCls("AnnotatableThing");
        for (Object object : protegeValues) {
            if (object instanceof Instance && ((Instance)object).hasType(cls)) {
                values.add(new DefaultAnnotatableThing((Instance)object));
            }
        }
        return values;
    }


    public Slot getAnnotatesSlot() {
        final String name = "annotates";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasAnnotates() {
        return hasSlotValues(getAnnotatesSlot());
    }


    public void addAnnotates(AnnotatableThing newAnnotates) {
        addSlotValue(getAnnotatesSlot(), newAnnotates);
    }


    public void removeAnnotates(AnnotatableThing oldAnnotates) {
        removeSlotValue(getAnnotatesSlot(), oldAnnotates);
    }


    public void setAnnotates(Collection<? extends AnnotatableThing> newAnnotates) {
        setSlotValues(getAnnotatesSlot(), newAnnotates);
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

    // Slot body

    public String getBody() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getBodySlot());
    }


    public Slot getBodySlot() {
        final String name = "body";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasBody() {
        return hasSlotValues(getBodySlot());
    }


    public void setBody(String newBody) {
        setSlotValue(getBodySlot(), newBody);
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

    // Slot created

    public Timestamp getCreated() {
        Object object = getWrappedProtegeInstance().getOwnSlotValue(getCreatedSlot());
        Cls cls = getKnowledgeBase().getCls("Timestamp");
        if (object instanceof Instance && ((Instance)object).hasType(cls)) {
            return new DefaultTimestamp((Instance)object);
        }
        return null;
    }


    public Slot getCreatedSlot() {
        final String name = "created";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasCreated() {
        return hasSlotValues(getCreatedSlot());
    }


    public void setCreated(Timestamp newCreated) {
        setSlotValue(getCreatedSlot(), newCreated);
    }

    // Slot modified

    public Timestamp getModified() {
        Object object = getWrappedProtegeInstance().getOwnSlotValue(getModifiedSlot());
        Cls cls = getKnowledgeBase().getCls("Timestamp");
        if (object instanceof Instance && ((Instance)object).hasType(cls)) {
            return new DefaultTimestamp((Instance)object);
        }
        return null;
    }


    public Slot getModifiedSlot() {
        final String name = "modified";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasModified() {
        return hasSlotValues(getModifiedSlot());
    }


    public void setModified(Timestamp newModified) {
        setSlotValue(getModifiedSlot(), newModified);
    }

    // Slot related

    public String getRelated() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getRelatedSlot());
    }


    public Slot getRelatedSlot() {
        final String name = "related";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasRelated() {
        return hasSlotValues(getRelatedSlot());
    }


    public void setRelated(String newRelated) {
        setSlotValue(getRelatedSlot(), newRelated);
    }
}
