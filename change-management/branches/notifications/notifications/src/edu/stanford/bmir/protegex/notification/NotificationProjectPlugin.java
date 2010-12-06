package edu.stanford.bmir.protegex.notification;

import edu.stanford.bmir.protegex.chao.ChAOKbManager;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.OntologyComponentFactory;
import edu.stanford.bmir.protegex.notification.cache.AnnotationCache;
import edu.stanford.bmir.protegex.notification.cache.WatchedEntitiesCache;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.ProjectPluginAdapter;
import edu.stanford.smi.protege.util.ApplicationProperties;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public class NotificationProjectPlugin extends ProjectPluginAdapter {

    public void afterCreate(Project p) {

        initialize(p);
    }

    public void afterLoad(Project p) {
        initialize(p);
    }

    private void initialize(Project p) {
        //TODO: find out from TT if we are supporting cases other than multi-user clients
        if (p.isMultiUserClient()) {
            return;
        }

        if (ApplicationProperties.getBooleanProperty(PropertyConstants.ENABLE_IMMEDIATE_NOTIFICATION, true)) {
            NotificationThreadManager.getNotificationSuspensionObserver(p).scheduleAtFixedRate(new NotificationTimerTask(p, NotificationInterval.IMMEDIATELY),
                    ApplicationProperties.getIntegerProperty(PropertyConstants.IMMEDIATE_NOTIFICATION_THREAD_STARTUP_DELAY_PROP, 180),     // delay
                    ApplicationProperties.getIntegerProperty(PropertyConstants.IMMEDIATE_NOTIFICATION_THREAD_INTERVAL_PROP, 120) //period
            );
        }
        NotificationThreadManager.getNotificationSuspensionObserver(p).scheduleAtFixedRate(new NotificationTimerTask(p, NotificationInterval.HOURLY),
                ApplicationProperties.getIntegerProperty(PropertyConstants.HOURLY_NOTIFICATION_THREAD_STARTUP_DELAY_PROP, 600),     // delay
                NotificationInterval.HOURLY.getIntervalInSeconds()  //period
        );
        NotificationThreadManager.getNotificationSuspensionObserver(p).scheduleAtFixedRate(new NotificationTimerTask(p, NotificationInterval.DAILY),
                ApplicationProperties.getIntegerProperty(PropertyConstants.DAILY_NOTIFICATION_THREAD_STARTUP_DELAY_PROP, 1200),     // delay
                NotificationInterval.DAILY.getIntervalInSeconds() //period
        );

        if (ChAOKbManager.isValidChAOKb(p.getKnowledgeBase())) {
            OntologyComponentFactory factory = new OntologyComponentFactory(p.getKnowledgeBase());
            WatchedEntitiesCache.initialize(p, factory);
            AnnotationCache.initialize(p);
        }

    }
}
