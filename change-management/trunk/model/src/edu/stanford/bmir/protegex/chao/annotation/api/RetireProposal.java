package edu.stanford.bmir.protegex.chao.annotation.api;

import java.util.Collection;

import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Timestamp;

/**
 * Generated by Protege (http://protege.stanford.edu).
 * Source Class: RetireProposal
 *
 * @version generated on Wed Dec 09 14:44:45 PST 2009
 */
public interface RetireProposal extends Proposal {

    // Slot annotates

    Collection<AnnotatableThing> getAnnotates();

    boolean hasAnnotates();

    void addAnnotates(AnnotatableThing newAnnotates);

    void removeAnnotates(AnnotatableThing oldAnnotates);

    void setAnnotates(Collection<? extends AnnotatableThing> newAnnotates);


    // Slot associatedAnnotations

    Collection<Annotation> getAssociatedAnnotations();

    boolean hasAssociatedAnnotations();

    void addAssociatedAnnotations(Annotation newAssociatedAnnotations);

    void removeAssociatedAnnotations(Annotation oldAssociatedAnnotations);

    void setAssociatedAnnotations(Collection<? extends Annotation> newAssociatedAnnotations);


    // Slot author

    String getAuthor();

    boolean hasAuthor();

    void setAuthor(String newAuthor);


    // Slot body

    String getBody();

    boolean hasBody();

    void setBody(String newBody);


    // Slot contactInformation

    String getContactInformation();

    boolean hasContactInformation();

    void setContactInformation(String newContactInformation);


    // Slot context

    String getContext();

    boolean hasContext();

    void setContext(String newContext);


    // Slot created

    Timestamp getCreated();

    boolean hasCreated();

    void setCreated(Timestamp newCreated);


    // Slot hasStatus

    Status getHasStatus();

    boolean hasHasStatus();

    void setHasStatus(Status newHasStatus);


    // Slot modified

    Timestamp getModified();

    boolean hasModified();

    void setModified(Timestamp newModified);


    // Slot reasonForChange

    String getReasonForChange();

    boolean hasReasonForChange();

    void setReasonForChange(String newReasonForChange);


    // Slot related

    String getRelated();

    boolean hasRelated();

    void setRelated(String newRelated);


    // Slot retiredEntity

    Ontology_Component getRetiredEntity();

    boolean hasRetiredEntity();

    void setRetiredEntity(Ontology_Component newRetiredEntity);


    // Slot subject

    String getSubject();

    boolean hasSubject();

    void setSubject(String newSubject);

    void delete();
}
