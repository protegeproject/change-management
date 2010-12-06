package edu.stanford.bmir.protegex.notification;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.metaproject.MetaProject;
import edu.stanford.smi.protege.server.metaproject.ProjectInstance;
import edu.stanford.smi.protege.server.metaproject.User;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public class UpdateProjectWithLastRuntimeDelegate implements NotificationDelegate {
    private NotificationDelegate delegate;
    private Date now;

    public UpdateProjectWithLastRuntimeDelegate(NotificationDelegate delegate, Date now) {
        this.delegate = delegate;
        this.now = now;
    }

    public void notifyAllUsers(final Project project, final MetaProject metaProject, final NotificationInterval interval, Map<User, Set<ChangeData>> userNamesToChangesMap) {

        delegate.notifyAllUsers(project, metaProject, interval, userNamesToChangesMap);
        
        final String intervalPropertyName = NotificationTimerTask.PROPERTY_PREFIX + interval.getValue();
        final ProjectInstance instance = NotificationTimerTask.getProjectInstance(project, metaProject);
        String threadIntervalPropertyValue = instance.getPropertyValue(intervalPropertyName);
        while (threadIntervalPropertyValue != null) {
            instance.removePropertyValue(intervalPropertyName, threadIntervalPropertyValue);
            threadIntervalPropertyValue = instance.getPropertyValue(intervalPropertyName);
        }
        instance.addPropertyValue(intervalPropertyName, Long.toString(now.getTime()));
    }
}
