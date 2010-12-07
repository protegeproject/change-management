package edu.stanford.bmir.protegex.notification;

import edu.stanford.bmir.protegex.chao.ChAOKbManager;
import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.bmir.protegex.notification.EmailUsersDelegate;
import edu.stanford.bmir.protegex.notification.cache.AnnotationCache;
import edu.stanford.bmir.protegex.notification.cache.WatchedEntitiesCache;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.server.Server;
import edu.stanford.smi.protege.server.metaproject.MetaProject;
import edu.stanford.smi.protege.server.metaproject.ProjectInstance;
import edu.stanford.smi.protege.server.metaproject.User;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.URIUtilities;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationTimerTask extends TimerTask {
    private static final Logger logger = Logger.getLogger(NotificationTimerTask.class.getName());
    public static final String PROPERTY_PREFIX = "scheduler.last.run.time.";
    private final NotificationInterval interval;
    private final boolean isRescheduled;
    private final Project project;

    NotificationTimerTask(Project project, NotificationInterval interval) {
        this(project, interval, false);
    }

    NotificationTimerTask(Project project, NotificationInterval interval, boolean rescheduled) {
        this.interval = interval;
        isRescheduled = rescheduled;
        this.project = project;
    }

    /**
     * The run method.
     * <p/>
     * Note that this method will only run when scheduled; there is nothing to do with scheduling in this method.
     */
    @Override
    public void run() {
        // Set the time we poll until to one minute ago. This is to correct a rare issue to do with the change processor not processing updates.
        final Date now = new Date(System.currentTimeMillis() - 60000);

        long t0 = System.currentTimeMillis();

        try {
            if (!ApplicationProperties.getBooleanProperty(PropertyConstants.ENABLE_ALL_NOTIFICATION, true)) {
                return;
            }
            if (NotificationThreadManager.getNotificationSuspensionObserver(project).hasSuspendedNotification(interval) && !isRescheduled) {
                return;
            }


            final Map<User, Set<ChangeData>> userNamesToChanges = new HashMap<User, Set<ChangeData>>();

            final String intervalPropertyName = PROPERTY_PREFIX + interval.getValue();
            final MetaProject metaProject = Server.getInstance().getMetaProjectNew();

            if (metaProject == null){
                logger.fine("Could not find meta project.");
                return;
            }

            try {
                Date lastRunTime = null;
                
                final ProjectInstance instance = getProjectInstance(project, metaProject);
                if (instance == null){
                    logger.fine("Could not find corresponding metaproject entry for project " + project.getProjectName());
                    return ;
                }
                final String threadIntervalPropertyValue = instance.getPropertyValue(intervalPropertyName);
                if (threadIntervalPropertyValue != null) {
                    lastRunTime = new Date(Long.parseLong(threadIntervalPropertyValue));
                }

                // first run only - initialize lastRunTime
                if (lastRunTime == null) {
                    // if we do not have a last run date, then the last run date was 24 hours ago
                    lastRunTime = new Date(new Date().getTime() - (1000 * 60 * 24));

                }

                final KnowledgeBase chaoKb = ChAOKbManager.getChAOKb(project.getKnowledgeBase());
                if (chaoKb == null) {
                    return;
                }
                final Project chaoProject = chaoKb.getProject();
                final AnnotationCache annotationCache = AnnotationCache.getCache(chaoProject);
                final WatchedEntitiesCache watchedEntitiesCache = WatchedEntitiesCache.getCache(chaoProject);
                final Slot authorSlot = new ChangeFactory(chaoKb).getAuthorSlot();

                final NotificationDelegate delegate = new PurgeCacheDelegate(
                        new UpdateProjectWithLastRuntimeDelegate(
                                new EmailUsersDelegate(
                                        new RemoveUnwantedChangesDelegate(
                                                new GetOntologyChangesDelegate(
                                                        new GetCommentChangesDelegate(lastRunTime, now, annotationCache, watchedEntitiesCache, authorSlot), lastRunTime, now, watchedEntitiesCache))), now), now, annotationCache);
                try {
                    delegate.notifyAllUsers(project, metaProject, interval, userNamesToChanges);
                } catch (SuspendNotificationException e) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE, "Suspended: " + e.getMessage());
                    }
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Caught error in email notification thread, continuing.", e);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Caught in notification thread", e);
        }

        long notificationRuntime = System.currentTimeMillis() - t0;
        if (notificationRuntime > 10 * 1000) {
            logger.info("Finished email notification with interval=" + interval + " isRescheduled=" + isRescheduled + " took " +
                    notificationRuntime / 1000 + " seconds.");
        }

        NotificationThreadManager.getNotificationSuspensionObserver(project).unSuspendNotification(interval);
    }


    public static ProjectInstance getProjectInstance(Project project, MetaProject metaProject){
        URI projectURI = project.getProjectURI();
        final Set<ProjectInstance> projects = metaProject.getProjects();
        for (ProjectInstance projectInstance : projects) {
            if (URIUtilities.createURI(projectInstance.getLocation()).equals(projectURI)){
                return projectInstance;
            }
        }
        return null;
    }


}