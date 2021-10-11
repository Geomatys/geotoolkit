/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.metadata;

import org.apache.sis.util.logging.Logging;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public abstract class AbstractMetadataWriter implements MetadataWriter {

    /**
     * A debugging logger.
     */
    protected static final Logger LOGGER = Logging.getLogger("org.constellation.metadata.io");

    public static final int INSERTED = 0;

    public static final int REPLACED = 1;

    /**
     * The default level for logging non essential informations (ToSee => finer)
     */
    protected Level logLevel = Level.INFO;

    /**
     * Build a new metadata writer.
     *
     * @throws org.geotoolkit.metadata.MetadataIoException
     */
    public AbstractMetadataWriter() throws MetadataIoException {
    }

    /**
     * @param logLevel the LogLevel to set
     */
    @Override
    public void setLogLevel(final Level logLevel) {
        this.logLevel = logLevel;
    }
}
