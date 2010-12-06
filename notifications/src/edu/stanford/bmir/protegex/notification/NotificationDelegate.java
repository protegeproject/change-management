package edu.stanford.bmir.protegex.notification;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.metaproject.MetaProject;
import edu.stanford.smi.protege.server.metaproject.User;

import java.util.Map;
import java.util.Set;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public interface NotificationDelegate {

    void notifyAllUsers(Project project, MetaProject metaProject, NotificationInterval interval, Map<User, Set<ChangeData>> userNamesToChangesMap);
}
