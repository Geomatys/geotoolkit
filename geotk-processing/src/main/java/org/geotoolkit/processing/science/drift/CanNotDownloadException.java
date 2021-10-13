package org.geotoolkit.processing.science.drift;

import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessException;

public class CanNotDownloadException extends ProcessException {
    public CanNotDownloadException(final String message, final Process process, final Throwable cause) {
        super(message, process, cause);
    }
}
