package edu.stanford.bmir.protegex.chao.annotation.api;

import java.io.Serializable;
import java.util.Collection;

/**
 * Generated by Protege (http://protege.stanford.edu).
 * Source Class: AnnotatableThing
 *
 * @version generated on Mon Aug 18 21:11:09 GMT-08:00 2008
 */
public interface AnnotatableThing extends Serializable {

    // Slot associatedAnnotations

    Collection<Annotation> getAssociatedAnnotations();

    boolean hasAssociatedAnnotations();

    void addAssociatedAnnotations(Annotation newAssociatedAnnotations);

    void removeAssociatedAnnotations(Annotation oldAssociatedAnnotations);

    void setAssociatedAnnotations(Collection<? extends Annotation> newAssociatedAnnotations);

    void delete();

    boolean canAs(Class<?> javaInterface);

	<X> X as(Class<? extends X> javaInterface);
}