package edu.stanford.bmir.protegex.notification.email;

import edu.stanford.bmir.protegex.notification.PropertyConstants;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Properties;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public class EmailUtil {

    public static void sendEmail(String recipient, String subject, String message, String from) {
        String smtpHostName = ApplicationProperties.getApplicationOrSystemProperty(PropertyConstants.EMAIL_SMTP_HOST_NAME_PROP, "");
        if (smtpHostName.length() == 0) {
            Log.getLogger().warning("Failed to send email message to " + recipient + ". Email not configured on the server.");
            return;
        }

        if (recipient == null) {
            Log.getLogger().warning("Cannot send email with subject: " + subject + " Email address is null");
            return;
        }

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHostName);
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "false");
        props.put("mail.smtp.port", ApplicationProperties.getApplicationOrSystemProperty(PropertyConstants.EMAIL_SMTP_PORT_PROP,""));
        props.put("mail.smtp.socketFactory.port", ApplicationProperties.getApplicationOrSystemProperty(PropertyConstants.EMAIL_SMTP_PORT_PROP,""));
        props.put("mail.smtp.socketFactory.class", ApplicationProperties.getApplicationOrSystemProperty(PropertyConstants.EMAIL_SSL_FACTORY_PROP,"javax.net.ssl.SSLSocketFactory"));
        props.put("mail.smtp.socketFactory.fallback", "false");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ApplicationProperties.getApplicationOrSystemProperty(PropertyConstants.EMAIL_USERNAME_PROP, ""), ApplicationProperties.getApplicationOrSystemProperty(PropertyConstants.EMAIL_PASSWORD_PROP, ""));
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            InternetAddress addressFrom = new InternetAddress(from);
            msg.setFrom(addressFrom);

            InternetAddress[] addressTo = new InternetAddress[1];
            addressTo[0] = new InternetAddress(recipient);

            msg.setRecipients(Message.RecipientType.TO, addressTo);

            msg.setSubject(subject);
            msg.setText(message, "UTF-8");
            Transport.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException("There was an error sending email to " + " " + recipient + ". Message: " + e.getMessage() , e);
        }
    }
}
