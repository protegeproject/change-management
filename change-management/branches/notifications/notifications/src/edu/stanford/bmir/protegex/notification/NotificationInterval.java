package edu.stanford.bmir.protegex.notification;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public enum NotificationInterval {
    HOURLY("Hourly", 60 * 60),DAILY("Daily",  60 * 60 * 24),IMMEDIATELY("Immediately", 0), NEVER("Never", -1);
    private final String value;
    private final int intervalAsSeconds;

    NotificationInterval(String value, int intervalAsSeconds) {
        this.value = value;
        this.intervalAsSeconds = intervalAsSeconds;
    }

    public String getValue() {
        return value;
    }

    public static NotificationInterval fromString(String str){
        if (HOURLY.getValue().equals(str)){
            return HOURLY;
        }
        if (DAILY.getValue().equals(str)){
            return DAILY;
        }
        if (IMMEDIATELY.getValue().equals(str)){
            return IMMEDIATELY;
        }
        if (NEVER.getValue().equals(str)){
            return NEVER;
        }
        return null;
    }

    public int getIntervalInSeconds() {
        return intervalAsSeconds;
    }


}
