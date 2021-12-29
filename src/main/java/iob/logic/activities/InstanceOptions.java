package iob.logic.activities;

public interface InstanceOptions {
    interface Types {
        String USER = "USER";
        String USER_LOGIN = "LOGIN";
    }

    interface Attributes {
        String INCOMING_LIKES = "incoming_likes";
        String OUTGOING_LIKES = "outgoing_likes";
        String LOCATION = "location";
        String OTHER_USER = "other_user";
    }
}