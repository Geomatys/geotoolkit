/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.process;

import org.opengis.metadata.Identifier;


/**
 * Thrown when a {@linkplain Process process} failed.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
public class ProcessException extends Exception {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -9009593453460083634L;

    /**
     * The process identifier, or {@code null}. We retain only the identifier rather than
     * a full {@link Process} object because the process may not be serializable, or may
     * be associated with large amount of data that we don't want to serialize.
     */
    private final Identifier processId;

    /**
     * Creates a new exception with the specified detail message.
     *
     * @param message The details message, or {@code null}.
     * @param process The process that failed, or {@code null}.
     */
    public ProcessException(final String message, final Process process) {
        this(message, process, null);
    }

    /**
     * Creates a new exception with the specified detail message and cause.
     *
     * @param message The details message, or {@code null}.
     * @param process The process that failed, or {@code null}.
     * @param cause   The cause, or {@code null}.
     */
    public ProcessException(final String message, final Process process, final Throwable cause) {
        super(message, cause);
        if (process != null) {
            final ProcessDescriptor descriptor = process.getDescriptor();
            if (descriptor != null) {
                processId = descriptor.getIdentifier();
                return;
            }
        }
        processId = null;
    }

    /**
     * Returns the identifier of the process that failed, or {@code null} if this information
     * is not available.
     *
     * @return The identifier of the process that failed, or {@code null}.
     */
    public Identifier getProcessIdentifier() {
        return processId;
    }
}
