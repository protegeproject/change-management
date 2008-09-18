package edu.stanford.bmir.protegex.chao.ontologycomp.api;

import java.util.Collection;

import edu.stanford.bmir.protegex.chao.annotation.api.AnnotatableThing;
import edu.stanford.bmir.protegex.chao.annotation.api.Annotation;
import edu.stanford.bmir.protegex.chao.change.api.Change;

/**
 * Generated by Protege (http://protege.stanford.edu).
 * Source Class: Ontology_Component
 *
 * @version generated on Mon Aug 18 21:08:59 GMT-08:00 2008
 */
public interface Ontology_Component extends AnnotatableThing {

    // Slot associatedAnnotations

    Collection<Annotation> getAssociatedAnnotations();

    boolean hasAssociatedAnnotations();

    void addAssociatedAnnotations(Annotation newAssociatedAnnotations);

    void removeAssociatedAnnotations(Annotation oldAssociatedAnnotations);

    void setAssociatedAnnotations(Collection<? extends Annotation> newAssociatedAnnotations);


    // Slot changes

    Collection<Change> getChanges();

    boolean hasChanges();

    void addChanges(Change newChanges);

    void removeChanges(Change oldChanges);

    void setChanges(Collection<? extends Change> newChanges);


    // Slot currentName

    String getCurrentName();

    boolean hasCurrentName();

    void setCurrentName(String newCurrentName);

    void delete();

    // __Code above is automatically generated. Do not change

    String getComponentType();

    String getInitialName();

    Status getStatus();

    boolean isAnonymous();
}


