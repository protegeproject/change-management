package edu.stanford.bmir.protegex.notification;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.ApplicationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public class NotificationThreadManager {
    private final Map<NotificationInterval, Boolean> hasRescheduledTaskPending = new HashMap<NotificationInterval, Boolean>();
    private final Project project;
    private static final Logger logger = Logger.getLogger(NotificationThreadManager.class.getName());
    private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory(){
            public Thread newThread(Runnable r) {
                final Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("Email Notification Thread");
                return thread;
            }
        });

    private static final Map<Project, NotificationThreadManager> observers = new HashMap<Project, NotificationThreadManager>();

    public NotificationThreadManager(Project project) {
        this.project = project;
    }

    public static NotificationThreadManager getNotificationSuspensionObserver(Project project){
        if (observers.get(project) == null){
            observers.put(project, new NotificationThreadManager(project));
        }
        return observers.get(project);
    }


    public void suspendNotification(NotificationInterval interval){
        hasRescheduledTaskPending.put(interval, Boolean.TRUE);
        final int retryDelay = ApplicationProperties.getIntegerProperty(PropertyConstants.EMAIL_RETRY_DELAY_PROP, PropertyConstants.EMAIL_RETRY_DELAY_DEFAULT);
        service.schedule(new NotificationTimerTask(project, interval, true), retryDelay, TimeUnit.SECONDS);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Rescheduling " + interval + "  thread to run in " + retryDelay + " seconds.");
            }
    }

    public void unSuspendNotification(NotificationInterval interval){
        hasRescheduledTaskPending.put(interval, Boolean.FALSE);
    }

    public boolean hasSuspendedNotification(NotificationInterval interval){
        return hasRescheduledTaskPending.get(interval) == null ? false : hasRescheduledTaskPending.get(interval);
    }

    public void scheduleAtFixedRate(NotificationTimerTask timerTask, int delay, int period){
        service.scheduleAtFixedRate(timerTask, delay, period, TimeUnit.SECONDS);
    }
}
