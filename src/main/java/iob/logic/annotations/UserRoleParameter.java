package iob.logic.annotations;

import org.apache.commons.text.WordUtils;

public enum UserRoleParameter {
    ADMIN,
    MANAGER,
    PLAYER;

    @Override
    public String toString() {
        // Capitalize the name to make it more human-readable.
        return WordUtils.capitalizeFully(this.name());
    }
}