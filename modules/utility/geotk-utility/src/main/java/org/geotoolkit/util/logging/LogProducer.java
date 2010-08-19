/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.util.logging;

import java.util.logging.Level;


/**
 * Interface for objects that produce logs of special interest. While every classes in the Geotk
 * library may emit log records, a few of them can emit logs considered more useful than other
 * logs for monitoring an application.
 * <p>
 * <b>Example:</b> The {@link org.geotoolkit.image.io.mosaic.MosaicImageReader} class can log the
 * set of tiles used for a particular read operations. This information is normally logged at the
 * {@link Level#FINE} (which means that it is typically disabled) because it is costly to create
 * and not of interest when the application works normally. If those logs are needed, the usual
 * approach is to use the {@link java.util.logging} API or edit the {@code logging.properties}
 * file. This interface provides an alternative, allowing to raise the level to {@link Level#INFO},
 * which is quite convenient on occasions.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
public interface LogProducer {
    /**
     * Returns the current logging level.
     *
     * @return The current logging level.
     */
    Level getLogLevel();

    /**
     * Sets the logging level to the given value.
     * A {@code null} value restore the default level.
     *
     * @param level The new logging level, or {@code null} for the default.
     */
    void setLogLevel(Level level);
}
