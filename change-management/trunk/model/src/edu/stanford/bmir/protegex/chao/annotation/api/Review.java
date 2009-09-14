package edu.stanford.bmir.protegex.chao.annotation.api;

import java.util.Collection;

import edu.stanford.bmir.protegex.chao.ontologycomp.api.Timestamp;

/**
 * Generated by Protege (http://protege.stanford.edu). Source Class: Comment
 * 
 * @version generated on Mon Aug 18 21:11:09 GMT-08:00 2008
 */
public interface Review extends Annotation {

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

    // Slot context

    String getContext();

    boolean hasContext();

    void setContext(String newContext);

    // Slot created

    Timestamp getCreated();

    boolean hasCreated();

    void setCreated(Timestamp newCreated);

    // Slot modified

    Timestamp getModified();

    boolean hasModified();

    void setModified(Timestamp newModified);

    // Slot related

    String getRelated();

    boolean hasRelated();

    void setRelated(String newRelated);

    void delete();
}
