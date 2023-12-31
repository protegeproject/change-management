package edu.stanford.bmir.protegex.chao.annotation.api.impl;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.bmir.protegex.chao.annotation.api.AnnotatableThing;
import edu.stanford.bmir.protegex.chao.annotation.api.Annotation;
import edu.stanford.bmir.protegex.chao.annotation.api.RetireProposal;
import edu.stanford.bmir.protegex.chao.annotation.api.Status;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Timestamp;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.impl.DefaultOntology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.impl.DefaultTimestamp;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;

/**
 * Generated by Protege (http://protege.stanford.edu).
 * Source Class: RetireProposal
 *
 * @version generated on Wed Dec 09 14:44:45 PST 2009
 */
public class DefaultRetireProposal extends DefaultProposal
         implements RetireProposal {

    private static final long serialVersionUID = -5104674713137912769L;


    public DefaultRetireProposal(Instance instance) {
        super(instance);
    }


    public DefaultRetireProposal() {
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

    // Slot contactInformation

    public String getContactInformation() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getContactInformationSlot());
    }


    public Slot getContactInformationSlot() {
        final String name = "contactInformation";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasContactInformation() {
        return hasSlotValues(getContactInformationSlot());
    }


    public void setContactInformation(String newContactInformation) {
        setSlotValue(getContactInformationSlot(), newContactInformation);
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

    // Slot hasStatus

    public Status getHasStatus() {
        Object object = getWrappedProtegeInstance().getOwnSlotValue(getHasStatusSlot());
        Cls cls = getKnowledgeBase().getCls("Status");
        if (object instanceof Instance && ((Instance)object).hasType(cls)) {
            return new DefaultStatus((Instance)object);
        }
        return null;
    }


    public Slot getHasStatusSlot() {
        final String name = "hasStatus";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasHasStatus() {
        return hasSlotValues(getHasStatusSlot());
    }


    public void setHasStatus(Status newHasStatus) {
        setSlotValue(getHasStatusSlot(), newHasStatus);
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

    // Slot reasonForChange

    public String getReasonForChange() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getReasonForChangeSlot());
    }


    public Slot getReasonForChangeSlot() {
        final String name = "reasonForChange";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasReasonForChange() {
        return hasSlotValues(getReasonForChangeSlot());
    }


    public void setReasonForChange(String newReasonForChange) {
        setSlotValue(getReasonForChangeSlot(), newReasonForChange);
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

    // Slot retiredEntity

    public Ontology_Component getRetiredEntity() {
        Object object = getWrappedProtegeInstance().getOwnSlotValue(getRetiredEntitySlot());
        Cls cls = getKnowledgeBase().getCls("Ontology_Component");
        if (object instanceof Instance && ((Instance)object).hasType(cls)) {
            return new DefaultOntology_Component((Instance)object);
        }
        return null;
    }


    public Slot getRetiredEntitySlot() {
        final String name = "retiredEntity";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasRetiredEntity() {
        return hasSlotValues(getRetiredEntitySlot());
    }


    public void setRetiredEntity(Ontology_Component newRetiredEntity) {
        setSlotValue(getRetiredEntitySlot(), newRetiredEntity);
    }

    // Slot subject

    public String getSubject() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getSubjectSlot());
    }


    public Slot getSubjectSlot() {
        final String name = "subject";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasSubject() {
        return hasSlotValues(getSubjectSlot());
    }


    public void setSubject(String newSubject) {
        setSlotValue(getSubjectSlot(), newSubject);
    }
}
