package edu.stanford.bmir.protegex.notification;

import edu.stanford.bmir.protegex.chao.ChAOKbManager;
import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.User;
import edu.stanford.bmir.protegex.notification.cache.WatchedEntitiesCache;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.metaproject.MetaProject;
import edu.stanford.smi.protegex.server_changes.RetrieveChangesProtegeJob;

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
public class GetOntologyChangesDelegate extends BranchWatchAwareDelegate {
    private static final Logger logger = Logger.getLogger(GetOntologyChangesDelegate.class.getName());
    private NotificationDelegate delegate;
    private Date fromDate;
    private Date toDate;
    private WatchedEntitiesCache watchedEntitiesCache;

    public GetOntologyChangesDelegate(NotificationDelegate delegate, Date fromDate, Date toDate, WatchedEntitiesCache watchedEntitiesCache) {
        this.delegate = delegate;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.watchedEntitiesCache = watchedEntitiesCache;
    }

    public void notifyAllUsers(Project project, MetaProject metaProject, NotificationInterval interval, Map<edu.stanford.smi.protege.server.metaproject.User, Set<ChangeData>> userNamesToChangesMap) {
        delegate.notifyAllUsers(project, metaProject, interval, userNamesToChangesMap);
        final KnowledgeBase knowledgeBase = project.getKnowledgeBase();
        if (knowledgeBase == null){
            logger.info("could not find knowledge base for project "+ project.getProjectName());
            return;
        }

        final Map<String, List<User>> watchedBranchNodeToUserMap = watchedEntitiesCache.getWatchedBranches();
        final Collection<Change> changes = (Collection<Change>) new RetrieveChangesProtegeJob(project.getKnowledgeBase(), fromDate, toDate).execute();

        // if we are in a transaction, then reschedule
        if (changes == null) {
            NotificationThreadManager.getNotificationSuspensionObserver(project).suspendNotification(interval);
            throw new SuspendNotificationException("Suspending exception because we detected a transaction when retrieving ontology changes.");
        }

        for (Change change : changes) {
            final Ontology_Component ontologyComponent = change.getApplyTo();
            final Set<edu.stanford.bmir.protegex.chao.ontologycomp.api.User> users = watchedEntitiesCache.getEntityWatches(ontologyComponent);
            final ChangeData dataWithProject = new ChangeData(change.getAuthor(), change.getContext(), change.getTimestamp().getDateParsed(),
                    project.getProjectName(), ontologyComponent.getComponentType(), ontologyComponent.getCurrentName(), NotificationType.ONTOLOGY);

            for (edu.stanford.bmir.protegex.chao.ontologycomp.api.User user : users) {
                final edu.stanford.smi.protege.server.metaproject.User metaProjectUser = metaProject.getUser(user.getName());
                Set<ChangeData> changeData = userNamesToChangesMap.get(metaProjectUser);
                if (changeData == null) {
                    changeData = new HashSet<ChangeData>();
                }
                if (!changeData.contains(dataWithProject)) {
                    changeData.add(dataWithProject);
                }
                userNamesToChangesMap.put(metaProjectUser, changeData);
            }

            // if the name is null, then we're a delete, which we don't handle.
            final String currentName = ontologyComponent.getCurrentName();
            if (currentName != null) {
                Frame frame = knowledgeBase.getFrame(currentName);

                addIfIsBranchWatch(userNamesToChangesMap, watchedBranchNodeToUserMap, frame, dataWithProject, metaProject);
            }
        }
    }

}
