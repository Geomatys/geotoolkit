package org.geotoolkit.openeo.dto;

/**
 * Check message DTO for OpenEO API.
 * This class is used to represent the result of a validation check,
 * indicating whether the check passed and providing an associated message.
 *
 * @author Quentin BIALOTA (Geomatys)
 */
public class CheckMessage {

    public CheckMessage(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }

    private boolean isValid;

    private String message;

    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }
}
