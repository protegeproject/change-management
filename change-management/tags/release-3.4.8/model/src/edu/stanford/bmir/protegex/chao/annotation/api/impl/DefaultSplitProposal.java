package edu.stanford.bmir.protegex.chao.annotation.api.impl;

import java.util.ArrayList;
import java.util.Collection;

import edu.stanford.bmir.protegex.chao.annotation.api.AnnotatableThing;
import edu.stanford.bmir.protegex.chao.annotation.api.Annotation;
import edu.stanford.bmir.protegex.chao.annotation.api.LinguisticEntity;
import edu.stanford.bmir.protegex.chao.annotation.api.SplitProposal;
import edu.stanford.bmir.protegex.chao.annotation.api.Status;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Timestamp;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.impl.DefaultTimestamp;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;

/**
 * Generated by Protege (http://protege.stanford.edu).
 * Source Class: SplitProposal
 *
 * @version generated on Wed Dec 09 14:44:45 PST 2009
 */
public class DefaultSplitProposal extends DefaultProposal
         implements SplitProposal {

    private static final long serialVersionUID = -7279857704896211237L;


    public DefaultSplitProposal(Instance instance) {
        super(instance);
    }


    public DefaultSplitProposal() {
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

    // Slot entityId

    public String getEntityId() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getEntityIdSlot());
    }


    public Slot getEntityIdSlot() {
        final String name = "entityId";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasEntityId() {
        return hasSlotValues(getEntityIdSlot());
    }


    public void setEntityId(String newEntityId) {
        setSlotValue(getEntityIdSlot(), newEntityId);
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

    // Slot preferredName

    public LinguisticEntity getPreferredName() {
        Object object = getWrappedProtegeInstance().getOwnSlotValue(getPreferredNameSlot());
        Cls cls = getKnowledgeBase().getCls("LinguisticEntity");
        if (object instanceof Instance && ((Instance)object).hasType(cls)) {
            return new DefaultLinguisticEntity((Instance)object);
        }
        return null;
    }


    public Slot getPreferredNameSlot() {
        final String name = "preferredName";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasPreferredName() {
        return hasSlotValues(getPreferredNameSlot());
    }


    public void setPreferredName(LinguisticEntity newPreferredName) {
        setSlotValue(getPreferredNameSlot(), newPreferredName);
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
