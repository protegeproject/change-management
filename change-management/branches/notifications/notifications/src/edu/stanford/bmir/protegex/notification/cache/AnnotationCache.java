package edu.stanford.bmir.protegex.notification.cache;

import edu.stanford.bmir.protegex.chao.annotation.api.Annotation;
import edu.stanford.bmir.protegex.chao.annotation.api.AnnotationFactory;
import edu.stanford.bmir.protegex.chao.annotation.api.impl.DefaultAnnotation;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.OntologyComponentFactory;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.KnowledgeBaseAdapter;
import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
//FIXME: no need for the annotationToRootNode
//project -> Project (ServerProject)
//Instance -> Annotation
public class AnnotationCache extends FrameAdapter {
    private static final Map<Project, AnnotationCache> caches = new HashMap<Project, AnnotationCache>();
    private final Map<Instance, Date> annotationToDate = new HashMap<Instance, Date>();
    private final Map<Instance, Ontology_Component> annotationToRootNode = new HashMap<Instance, Ontology_Component>();

    private AnnotationCache(final Project chaoProject) {
        final KnowledgeBase chaoKb = chaoProject.getKnowledgeBase();
        if (chaoKb != null) {
            final AnnotationFactory annotationFactory = new AnnotationFactory(chaoKb);
            chaoKb.addKnowledgeBaseListener(new KnowledgeBaseAdapter() {
                @Override
                public void instanceDeleted(KnowledgeBaseEvent event) {
                    Cls annotationCls = annotationFactory.getAnnotationClass();
                    Instance inst = (Instance) event.getFrame();
                    if (!inst.hasType(annotationCls)) {
                        return;
                    }
                    removeAnnotation( inst);
                }
            });
            chaoKb.addFrameListener(new FrameAdapter(){
                @Override
                public void ownSlotValueChanged(FrameEvent event) {
                    if (!event.getSlot().equals(annotationFactory.getAnnotatesSlot())){
                        return;
                    }
                    Cls annotationCls = annotationFactory.getAnnotationClass();

                    Instance inst = (Instance) event.getFrame();
                    if (!inst.hasType(annotationCls)) {
                        return;
                    }
                    addAnnotation(inst);
                }
            });
        }
    }

    public static AnnotationCache getCache(Project project){
        return caches.get(project);
    }

    synchronized void removeAnnotation(final Instance inst) {
        annotationToDate.remove(inst);
        annotationToRootNode.remove(inst);
    }

    synchronized void removeAnnotations(final Collection<Instance> instances) {
        for (Instance instance : instances) {
            removeAnnotation(instance);
        }
    }

    synchronized void addAnnotation(final Instance inst) {
        annotationToDate.put(inst, new Date());
        Annotation annotation = new DefaultAnnotation(inst);
        final Collection<Ontology_Component> components = ServerChangesUtil.getAnnotatedOntologyComponents(annotation);
        for (Ontology_Component component : components) {
            annotationToRootNode.put(inst, component);
        }
    }

    public synchronized Ontology_Component getRootNode(Instance instance) {
        return annotationToRootNode.get(instance);
    }

    public synchronized Date getChangeDate( Instance instance) {
        return annotationToDate.get(instance);
    }

    public synchronized Collection<Instance> getAnnotations(Date from, Date to) {
        Collection<Instance> results = new ArrayList<Instance>();
        for (Map.Entry<Instance, Date> instanceDateEntry : annotationToDate.entrySet()) {
            if (instanceDateEntry.getValue().after(from) && instanceDateEntry.getValue().before(to)) {
                results.add(instanceDateEntry.getKey());
            }
        }
        return results;
    }

    public synchronized void purge(Date from, Date to) {
        Collection<Instance> results = getAnnotations(from, to);
        removeAnnotations(results);
    }

    public static void initialize(Project chaoProject) {
        final AnnotationCache cache = new AnnotationCache(chaoProject);
        caches.put(chaoProject, cache);
    }
}
