package edu.stanford.bmir.protegex.notification;

import edu.stanford.bmir.protegex.chao.ChAOKbManager;
import edu.stanford.bmir.protegex.chao.annotation.api.AnnotatableThing;
import edu.stanford.bmir.protegex.chao.annotation.api.Annotation;
import edu.stanford.bmir.protegex.chao.annotation.api.AnnotationFactory;
import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.bmir.protegex.notification.cache.AnnotationCache;
import edu.stanford.bmir.protegex.notification.cache.WatchedEntitiesCache;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.server.metaproject.MetaProject;
import edu.stanford.smi.protege.server.metaproject.User;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public class GetCommentChangesDelegate extends BranchWatchAwareDelegate {
    private final Date fromDate;
    private final Date toDate;
    private final AnnotationCache annotationCache;
    private final WatchedEntitiesCache watchedEntitiesCache;
    private final Slot authorSlot;

    public GetCommentChangesDelegate(Date fromDate, Date toDate, final AnnotationCache annotationCache, final WatchedEntitiesCache watchedEntitiesCache, final Slot authorSlot) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.annotationCache = annotationCache;
        this.watchedEntitiesCache = watchedEntitiesCache;
        this.authorSlot = authorSlot;
    }

    public void notifyAllUsers(Project project, MetaProject metaProject, NotificationInterval interval, Map<User, Set<ChangeData>> userNamesToChangesMap) {
        final KnowledgeBase knowledgeBase = project.getKnowledgeBase();
        if (knowledgeBase == null) {
            return;
        }

        final Map<String, List<edu.stanford.bmir.protegex.chao.ontologycomp.api.User>> watchedBranchNodeToUserMap = watchedEntitiesCache.getWatchedBranches();
        final Collection<Instance> annotationInstances = annotationCache.getAnnotations(fromDate, toDate);

        for (Instance annotationInstance : annotationInstances) {
            final Ontology_Component rootNode = annotationCache.getRootNode(annotationInstance);

            final String author = (String) annotationInstance.getDirectOwnSlotValue(authorSlot);

            String currentRootName = rootNode.getCurrentName();
            final String className = currentRootName == null ? "" : currentRootName;

            final Collection<edu.stanford.bmir.protegex.chao.ontologycomp.api.User> users = rootNode.getWatchedBy();
            for (edu.stanford.bmir.protegex.chao.ontologycomp.api.User user : users) {
                final User metaProjectUser = metaProject.getUser(user.getName());
                if (metaProjectUser != null) {
                    Set<ChangeData> changeData = userNamesToChangesMap.get(metaProjectUser);
                    if (changeData == null) {
                        changeData = new HashSet<ChangeData>();
                    }

                    changeData.add(new ChangeData(author, annotationInstance.getBrowserText(),
                            annotationCache.getChangeDate(annotationInstance), project.getProjectName(), rootNode.getComponentType(), className, NotificationType.COMMENT));

                    userNamesToChangesMap.put(metaProjectUser, changeData);
                }
            }

            final ChangeData dataWithProject = new ChangeData(author, annotationInstance.getBrowserText(),
                    annotationCache.getChangeDate(annotationInstance), project.getProjectName(), rootNode.getComponentType(), className, NotificationType.COMMENT);

            final Annotation annotation = AnnotationFactory.getGenericAnnotation(annotationInstance);
            final Collection<AnnotatableThing> annotatedThings = annotation.getAnnotates();
            if (annotatedThings != null) {
                for (AnnotatableThing annotatedThing : annotatedThings) {
                    if (annotatedThing.canAs(Ontology_Component.class)) {
                        Ontology_Component oc = annotatedThing.as(Ontology_Component.class);
                        // if the name is null, then we're a delete, which we don't handle.
                        String currentName = oc.getCurrentName();
                        if (currentName != null) {
                            Frame frame = knowledgeBase.getFrame(currentName);
                            addIfIsBranchWatch(userNamesToChangesMap, watchedBranchNodeToUserMap, frame, dataWithProject, metaProject);
                        }
                    }
                }
            }
        }
    }
}
