package edu.stanford.bmir.protegex.notification;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public interface PropertyConstants {
    public static final String ENABLE_ALL_NOTIFICATION = "enable.all.notification";
    public static final String ENABLE_IMMEDIATE_NOTIFICATION = "enable.immediate.notification";

    public static final String DAILY_NOTIFICATION_THREAD_STARTUP_DELAY_PROP = "daily.notification.startup.delay";
    public static final String HOURLY_NOTIFICATION_THREAD_STARTUP_DELAY_PROP = "hourly.notification.startup.delay";
    public static final String IMMEDIATE_NOTIFICATION_THREAD_STARTUP_DELAY_PROP = "immediate.notification.startup.delay";
    public static final String IMMEDIATE_NOTIFICATION_THREAD_INTERVAL_PROP = "immediate.notification.interval.delay";


    public static final String EMAIL_PASSWORD_PROP = "notification.email.password";
    public static final String EMAIL_USERNAME_PROP = "notification.email.account";
    public static final String EMAIL_SSL_FACTORY_PROP = "notification.email.ssl.factory";
    public static final String EMAIL_SMTP_PORT_PROP = "notification.email.smtp.port";
    public static final String EMAIL_SMTP_HOST_NAME_PROP = "notification.email.smtp.host.name";

    public static final String EMAIL_RETRY_DELAY_PROP = "email.notification.retry.delay";
    public static final int EMAIL_RETRY_DELAY_DEFAULT = 2 * 1000 * 60;

    public static final String APPLICATION_NAME_PROP = "application.name";
    public static final String APPLICATION_NAME_DEFAULT = "WebProtege";

    public static final String APPLICATION_URL_PROP = "application.url";
    public static final String APPLICATION_URL_DEFAULT = "localhost";
}
