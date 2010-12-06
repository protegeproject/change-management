package edu.stanford.bmir.protegex.notification;

import edu.stanford.bmir.protegex.notification.cache.AnnotationCache;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.metaproject.MetaProject;
import edu.stanford.smi.protege.server.metaproject.User;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public class PurgeCacheDelegate implements NotificationDelegate {
    private final NotificationDelegate delegate;
    private final Date toDate;
    private final AnnotationCache annotationCache;

    public PurgeCacheDelegate(NotificationDelegate delegate, Date toDate, AnnotationCache annotationCache) {
        this.delegate = delegate;
        this.toDate = toDate;
        this.annotationCache = annotationCache;
    }

    public void notifyAllUsers(Project project, MetaProject metaProject, NotificationInterval interval, Map<User, Set<ChangeData>> userNamesToChangesMap) {
        delegate.notifyAllUsers(project, metaProject, interval, userNamesToChangesMap);
        if (interval.equals(NotificationInterval.DAILY)) {
            purgeCache(project, toDate);
        }
    }


    /**
     * clean up cache by making sure that we use only the earliest time of the immediate/hourly/daily runs, preventing the last thread (daily) from purging elements the other threads have yet to use
     *
     * @param now The time to purge the cache for.
     */
    private void purgeCache(Project project, Date now) {
        Long earliestCommentTime = now.getTime();
        String hourlyProperty = NotificationInterval.HOURLY.getValue();
        String immediatelyProperty = NotificationInterval.IMMEDIATELY.getValue();
        String hourlyValue = System.getProperty(hourlyProperty);
        String immediatelyValue = System.getProperty(immediatelyProperty);

        if (hourlyValue != null) {
            if (earliestCommentTime > Long.parseLong(hourlyValue)) {
                earliestCommentTime = Long.parseLong(hourlyValue);
            }
        }
        if (immediatelyValue != null) {
            if (earliestCommentTime > Long.parseLong(immediatelyValue)) {
                earliestCommentTime = Long.parseLong(immediatelyValue);
            }
        }
        annotationCache.purge(new Date(0), new Date(earliestCommentTime));
    }
}
