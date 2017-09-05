package org.geotoolkit.process;

/**
 * Thrown when a {@linkplain Process process} is dismissed.
 *
 * @author Mehdi Sidhoum (Geomatys).
 * @version 5.0.0
 *
 * @module
 */
public class DismissProcessException extends ProcessException {

    /**
     * Creates a new exception with the specified detail message.
     *
     * @param message The details message, or {@code null}.
     * @param process The process that failed, or {@code null}.
     */
    public DismissProcessException(String message, Process process) {
        super(message, process);
    }

    /**
     * Creates a new exception with the specified detail message and cause.
     *
     * @param message The details message, or {@code null}.
     * @param process The process that failed, or {@code null}.
     * @param cause   The cause, or {@code null}.
     */
    public DismissProcessException(String message, Process process, Throwable cause) {
        super(message, process, cause);
    }


}
