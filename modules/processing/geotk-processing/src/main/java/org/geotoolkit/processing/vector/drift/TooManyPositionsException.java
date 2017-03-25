package org.geotoolkit.processing.vector.drift;

import org.geotoolkit.process.ProcessException;


/**
 * Thrown when {@link DriftProbability} can not complete because there is too many positions to track.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public class TooManyPositionsException extends ProcessException {
    public TooManyPositionsException(final String message, final org.geotoolkit.process.Process process) {
        super(message, process);
    }
}
