package edu.stanford.bmir.protegex.notification;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public class SuspendNotificationException extends RuntimeException{
    public SuspendNotificationException() {
    }

    public SuspendNotificationException(String message) {
        super(message);
    }
}
