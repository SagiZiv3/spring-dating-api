package iob.logic.exceptions.instance;

import lombok.Getter;

@Getter
public class InstanceNotFoundException extends RuntimeException {
    private final String domain, id;

    public InstanceNotFoundException(String domain, String id) {
        this.domain = domain;
        this.id = id;
    }
}