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
package org.geotoolkit.image.io;

import java.util.logging.Logger;
import java.util.logging.LogRecord;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.event.IIOWriteWarningListener;

import org.geotoolkit.util.Localized;
import org.geotoolkit.util.logging.Logging;


/**
 * Interface for objects that may produce warnings for recoverable failures. The warnings
 * are encapsulated in {@link LogRecord} objects and given to the {@link #warningOccurred
 * warningOccurred} method. Then there is a choice:
 * <p>
 * <ul>
 *   <li>If there is at least one {@link IIOReadWarningListener} or {@link IIOWriteWarningListener}
 *       reachable directly or indirectly, then the logging message is given to those listeners.</li>
 *   <li>Otherwise the record is logged using the {@link #LOGGER} declared in this interface.</li>
 * </ul>
 * <p>
 * Warnings are localized in a {@linkplain java.util.Locale locale} that can be specified
 * to the {@link javax.imageio.ImageReader} or {@link javax.imageio.ImageWriter} plugin.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @see ImageReader#warningOccurred(LogRecord)
 * @see ImageWriter#warningOccurred(LogRecord)
 * @see org.geotoolkit.image.io.metadata.SpatialMetadata#warningOccurred(LogRecord)
 * @see org.geotoolkit.image.io.metadata.MetadataAccessor#warningOccurred(LogRecord)
 *
 * @since 3.08
 * @module
 */
public interface WarningProducer extends Localized {
    /**
     * The logger to use as a fallback when no warning listener can be reach.
     */
    Logger LOGGER = Logging.getLogger(WarningProducer.class);

    /**
     * Invoked when a warning occured. This method is typically invoked by the class implementing
     * this {@code WarningProducer} interface. However it can also be invoked by other classes that
     * just forward the warnings to this class. This method is public for that raison.
     *
     * @param  record The warning that occured.
     * @return {@code true} if the message has been sent to at least one warning listener,
     *         or {@code false} if it has been sent to the logging system as a fallback.
     */
    boolean warningOccurred(LogRecord record);
}
