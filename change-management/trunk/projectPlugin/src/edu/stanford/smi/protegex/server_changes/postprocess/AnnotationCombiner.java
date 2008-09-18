package edu.stanford.smi.protegex.server_changes.postprocess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.bmir.protegex.chao.change.api.Annotation_Change;
import edu.stanford.bmir.protegex.chao.change.api.Annotation_Removed;
import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.bmir.protegex.chao.change.api.Composite_Change;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.smi.protege.server.RemoteSession;
import edu.stanford.smi.protegex.server_changes.PostProcessorManager;

public class AnnotationCombiner implements PostProcessor {
    private PostProcessorManager changes_db;
    private Map<RemoteSession, List<Annotation_Change>> lastAnnotationsBySession
                                    = new HashMap<RemoteSession, List<Annotation_Change>>();

    public void initialize(PostProcessorManager changes_db) {
        this.changes_db = changes_db;
    }

    /*
     * Combine Annotations.
     */
    public void addChange(Change aChange) {
        RemoteSession session = changes_db.getCurrentSession();
        List<Annotation_Change> previous_annotations = lastAnnotationsBySession.get(session);

        if (aChange instanceof Annotation_Change) {
            Annotation_Change annotation = (Annotation_Change) aChange;

            if (previous_annotations == null) {
                previous_annotations = new ArrayList<Annotation_Change>();
                previous_annotations.add(annotation);
                lastAnnotationsBySession.put(session, previous_annotations);
                return;
            }

            Ontology_Component applyTo = annotation.getApplyTo();
            Ontology_Component property = annotation.getAssociatedProperty();

            Annotation_Change earlier_annotation = previous_annotations.get(0);
            if (!applyTo.equals(earlier_annotation.getApplyTo()) ||
                    !property.equals(earlier_annotation.getAssociatedProperty())) {
                combineAnnotations(previous_annotations);
                List<Annotation_Change> new_annotations = new ArrayList<Annotation_Change>();
                new_annotations.add(annotation);
                lastAnnotationsBySession.put(session, new_annotations);
                return;
            }
            else {
                previous_annotations.add(annotation);
                return;
            }
        }
        else if (previous_annotations != null) {
            combineAnnotations(previous_annotations);
        }

    }

    private void combineAnnotations(List<Annotation_Change> annotations) {
        lastAnnotationsBySession.remove(changes_db.getCurrentSession());
        if (annotations.size() <= 1) {
			return;
		}
        Ontology_Component applyTo = annotations.get(0).getApplyTo();
        Composite_Change transaction = new ChangeFactory(changes_db.getChangesKb()).createComposite_Change(null);
        transaction.setSubChanges(annotations);
        changes_db.finalizeChange(transaction, applyTo, getContextForAnnotations(annotations));
    }

    private String getContextForAnnotations(Collection<Annotation_Change> annotations) {
        String context = null;
        for (Annotation_Change annotation : annotations) {
            context = annotation.getContext();
            if (!(annotation instanceof Annotation_Removed)) {
                break;
            }
        }
        return context;
    }

}
