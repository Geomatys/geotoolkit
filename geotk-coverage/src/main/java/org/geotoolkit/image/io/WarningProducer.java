/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import org.apache.sis.util.Localized;


/**
 * Interface for objects that may produce warnings for recoverable failures. The warnings
 * are encapsulated in {@link LogRecord} objects and given to the {@link #warningOccurred
 * warningOccurred} method. The (sometime indirect) result of a call to {@code warningOccurred}
 * is one of the following choices:
 *
 * <ol>
 *   <li><p>If there is at least one listener ({@link IIOReadWarningListener} or
 *     {@link IIOWriteWarningListener}) is reachable directly or indirectly, then
 *     the logging message is given to those listeners.</p></li>
 *
 *   <li><p>Otherwise the record is logged using the {@link #LOGGER} declared in this interface.</p></li>
 * </ol>
 *
 * {@section Localization}
 * Warnings are localized for a {@linkplain java.util.Locale locale} typically inferred from
 * the image reader or writer. The locale can be specified by a call to the {@code setLocale}
 * method defined in the {@link javax.imageio.ImageReader} or {@link javax.imageio.ImageWriter}
 * class.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public interface WarningProducer extends Localized {
    /**
     * The logger to use as a fallback when no warning listener can be reach.
     */
    Logger LOGGER = Logger.getLogger("org.geotoolkit.image.io");

    /**
     * Invoked when a warning occurred. This method is typically invoked by the class implementing
     * this {@code WarningProducer} interface.
     *
     * @param  record The warning that occurred.
     * @return {@code true} if the message has been sent to at least one warning listener,
     *         or {@code false} if it has been sent to the logging system as a fallback.
     */
    boolean warningOccurred(LogRecord record);
}
