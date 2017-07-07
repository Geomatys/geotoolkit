package org.geotoolkit.processing.science.drift;

import org.geotoolkit.process.ProcessException;

public class UnavailableDataException extends ProcessException {
    public UnavailableDataException(final String message, final org.geotoolkit.process.Process process, final Throwable cause) {
        super(message, process, cause);
    }
}
