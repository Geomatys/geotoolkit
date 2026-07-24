/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcapi.dto.connectedsystems.dynamic;

/**
 * Status code of a {@link Command}, as reported by {@link CommandStatus#getStatusCode()}.
 *
 * @see <a href="https://docs.ogc.org/is/23-002/23-002.html">OGC API - Connected Systems - Part 2</a>
 */
public enum CommandStatusCode {

    /**
     * The command has not yet been processed.
     */
    PENDING,

    /**
     * The command has been accepted for processing.
     */
    ACCEPTED,

    /**
     * The command has been rejected; final status.
     */
    REJECTED,

    /**
     * The command has been scheduled for future execution.
     */
    SCHEDULED,

    /**
     * The command parameters were updated.
     */
    UPDATED,

    /**
     * The command has been canceled; final status.
     */
    CANCELED,

    /**
     * The command is currently executing.
     */
    EXECUTING,

    /**
     * The command has failed; final status.
     */
    FAILED,

    /**
     * The command has completed successfully; final status.
     */
    COMPLETED

}
