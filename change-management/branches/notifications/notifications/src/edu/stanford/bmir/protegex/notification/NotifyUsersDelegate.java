package edu.stanford.bmir.protegex.notification;

import edu.stanford.bmir.protegex.notification.email.EmailUtil;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Transaction;
import edu.stanford.smi.protege.server.metaproject.MetaProject;
import edu.stanford.smi.protege.server.metaproject.User;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public class NotifyUsersDelegate implements NotificationDelegate {

    private static final Logger logger = Logger.getLogger(NotifyUsersDelegate.class.getName());
    private final MessageFormat ontologyChangeMessage = new MessageFormat("{1,date} {1,time}: {2} made the change: \n\t{0}\n");
    private final MessageFormat noteChangeMessage = new MessageFormat("{1,date} {1,time}: {2} added a new comment: \n\t{0}\n");
    private final MessageFormat linkMessage = new MessageFormat("\tDirect link: http://{0}?ontology={1}&tab={2}&id={3}\n\n");
    private NotificationDelegate delegate;

    public NotifyUsersDelegate(NotificationDelegate delegate) {
        this.delegate = delegate;
    }

    public void notifyAllUsers(Project project, MetaProject metaProject, NotificationInterval interval, Map<User, Set<ChangeData>> userNamesToChangesMap) {
        delegate.notifyAllUsers(project, metaProject, interval, userNamesToChangesMap);

        for (Map.Entry<User, Set<ChangeData>> userToChanges : userNamesToChangesMap.entrySet()) {
            Set<ChangeData> changes = userToChanges.getValue();
            if (changes.isEmpty()) {
                continue;
            }
            final User user = userToChanges.getKey();
            if (user == null) {
                continue;
            }
            
            List<ChangeData> sortedChanges = new ArrayList<ChangeData>(changes);
            Collections.sort(sortedChanges, new Comparator<ChangeData>() {
                public int compare(ChangeData o1, ChangeData o2) {
                    return new Long(o1.getTimestamp().getTime() - o2.getTimestamp().getTime()).intValue();
                }
            });

            if (!changes.isEmpty()) {
                final String userEmail = user.getEmail();
                if (userEmail != null && !userEmail.trim().equals("")) {
                    sendNotification(userEmail, sortedChanges);
                }
            }
        }
    }

    private void sendNotification(final String userEmail, final Collection<ChangeData> changes) {

        StringBuffer stringBuffer = new StringBuffer();
        final String applicationName = ApplicationProperties.getString(PropertyConstants.APPLICATION_NAME_PROP, PropertyConstants.APPLICATION_NAME_DEFAULT);
        final String applicationURL = ApplicationProperties.getString(PropertyConstants.APPLICATION_URL_PROP, PropertyConstants.APPLICATION_URL_DEFAULT);

        for (ChangeData change : changes) {
            Object[] messageParams = new Object[]{Transaction.removeApplyTo(change.getDescription()), change.getTimestamp(), change.getAuthor()};
            if (NotificationType.COMMENT.equals(change.getType())) {
                noteChangeMessage.format(messageParams, stringBuffer, new FieldPosition(0));
            } else {
                ontologyChangeMessage.format(messageParams, stringBuffer, new FieldPosition(0));
            }
            try {
                String tabName = getTabName(change);
                linkMessage.format(new Object[]{
                        applicationURL,
                        URLEncoder.encode(change.getProject(), "UTF-8"),
                        tabName,
                        change.getName() == null ? "" : URLEncoder.encode(change.getName(), "UTF-8")
                }, stringBuffer, new FieldPosition(0));
            } catch (UnsupportedEncodingException e) {
                Log.getLogger().log(Level.SEVERE, "Error formatting to URLEncoding projectName = " + change.getProject() + ", tab = " + change.getValueType() + ", id = " + change.getName(), e);
            }
        }
        stringBuffer.append("\n-----\n* To change the frequency of your notifications, or to stop receiving them altogether, please edit your profile by going to http://");
        stringBuffer.append(applicationURL);
        stringBuffer.append(" and clicking Options -> Edit Profile.");
        final String subject = MessageFormat.format("{1} change report generated on {0,date} {0,time}", new Date(), applicationName);
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "emailing user '" + userEmail + "' with subject '" + subject + "' message '" + stringBuffer.toString() + "'");
        }
        EmailUtil.sendEmail(userEmail, subject, stringBuffer.toString(), ApplicationProperties.getApplicationOrSystemProperty(PropertyConstants.EMAIL_USERNAME_PROP, ""));
    }

    private String getTabName(ChangeData  change) throws UnsupportedEncodingException {
        String tabName = "ClassesTab";
        if ("Individual".equals(change.getValueType())) {
            tabName = "IndividualsTab";
        } else if (NotificationType.COMMENT.equals(change.getType())) {
            tabName = "NotesTab";
        }
        return tabName;
    }
}
