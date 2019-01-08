package org.geotoolkit.coverage.processing;


/**
 * Throws if an operation name given to {@link GridCoverageProcessor} is not a know operation.
 *
 * @author Martin Desruisseaux (IRD)
 */
public class OperationNotFoundException extends IllegalArgumentException {
    /**
     * Creates an exception with no message.
     */
    public OperationNotFoundException() {
        super();
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param  message  the detail message. The detail message is saved for
     *         later retrieval by the {@link #getMessage()} method.
     */
    public OperationNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates an exception with the specified message.
     *
     * @param  message  the detail message. The detail message is saved for
     *         later retrieval by the {@link #getMessage()} method.
     * @param  cause  the cause, or {@code null}.
     */
    public OperationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
