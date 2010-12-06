package edu.stanford.bmir.protegex.notification;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.metaproject.MetaProject;
import edu.stanford.smi.protege.server.metaproject.User;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public class RemoveUnwantedChangesDelegate implements NotificationDelegate {
    private final NotificationDelegate delegate;

    public RemoveUnwantedChangesDelegate(NotificationDelegate delegate) {
        this.delegate = delegate;
    }

    public void notifyAllUsers(Project project, MetaProject metaProject, NotificationInterval interval, Map<User, Set<ChangeData>> userNamesToChangesMap) {
        delegate.notifyAllUsers(project, metaProject, interval, userNamesToChangesMap);
        for (Map.Entry<User, Set<ChangeData>> userNameToChanges : userNamesToChangesMap.entrySet()) {
            final User user = userNameToChanges.getKey();
            if (user == null) {
                continue;
            }
            Set changes = userNameToChanges.getValue();
            NotificationInterval frequencyOntology = NotificationInterval.fromString(user.getPropertyValue(NotificationType.ONTOLOGY.getValue()));
            NotificationInterval frequencyComment = NotificationInterval.fromString(user.getPropertyValue(NotificationType.COMMENT.getValue()));
            //if user has not set their frequency, then we default to immediate notification.
            if (frequencyOntology == null) {
                frequencyOntology = NotificationInterval.IMMEDIATELY;
            }
            changes = removeUnwantedChanges(changes, frequencyOntology, NotificationType.ONTOLOGY, interval);

            //if user has not set their frequency, then we default to immediate notification.
            if (frequencyComment == null) {
                frequencyComment = NotificationInterval.IMMEDIATELY;
            }
            changes = removeUnwantedChanges(changes, frequencyComment, NotificationType.COMMENT, interval);
        }

    }


    private Set<ChangeData> removeUnwantedChanges(final Set<ChangeData> changes, final NotificationInterval frequencyOfInterval, final NotificationType notificationType, NotificationInterval currentNotificationInterval) {
        Set<ChangeData> tempChanges = new HashSet<ChangeData>(changes);
        if (!frequencyOfInterval.equals(currentNotificationInterval)) {
            for (ChangeData change : changes) {
                if (change.getType().equals(notificationType)) {
                    tempChanges.remove(change);
                }
            }
        }
        return tempChanges;
    }
}
