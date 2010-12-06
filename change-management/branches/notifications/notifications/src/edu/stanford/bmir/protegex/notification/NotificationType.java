package edu.stanford.bmir.protegex.notification;

/**
 * @author Jack Elliott <jacke@stanford.edu>
 */
public enum NotificationType {
    ONTOLOGY("ontology.notification.interval"),COMMENT("comment.notification.interval");
    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static NotificationType fromString(String str){
        if (ONTOLOGY.getValue().equals(str)){
            return ONTOLOGY;
        }
        if (COMMENT.getValue().equals(str)){
            return COMMENT;
        }
        return null;
    }
}
