package org.geotoolkit.processing.science.drift;

import org.geotoolkit.process.Process;

public class UnavailableDataException extends CanNotDownloadException {
    public UnavailableDataException(final String message, final Process process, final Throwable cause) {
        super(message, process, cause);
    }
}
