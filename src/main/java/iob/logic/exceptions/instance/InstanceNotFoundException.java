package iob.logic.exceptions.instance;

import lombok.Getter;

@Getter
public class InstanceNotFoundException extends RuntimeException {

    public InstanceNotFoundException(String domain, String id) {
        super(String.format("Instance with id '%s' doesn't exist in domain '%s'", id, domain));
    }

    public InstanceNotFoundException() {
        super("Couldn't find requested instance.");
    }
}