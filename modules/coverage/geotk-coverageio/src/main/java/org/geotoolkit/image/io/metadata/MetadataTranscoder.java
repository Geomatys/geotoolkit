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
package org.geotoolkit.image.io.metadata;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.LogRecord;
import javax.imageio.ImageTranscoder;

import org.geotoolkit.image.io.WarningProducer;


/**
 * Base class for objects providing transcoding capabilities from/to a metadata object.
 * The metadata object is typically the ISO 19115 {@link org.opengis.metadata.Metadata},
 * but not necessarily.
 *
 * @param <T> The type of metadata object produced by this transcoder.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @see javax.imageio.ImageTranscoder
 *
 * @since 3.20
 * @module
 */
public abstract class MetadataTranscoder<T> implements WarningProducer {
    /**
     * Where to send the warnings, or {@code null} if none.
     */
    private final WarningProducer owner;

    /**
     * Creates a new transcoder for the given source or destination.
     *
     * @param owner Typically the {@link org.geotoolkit.image.io.SpatialImageReader} or
     *              {@link org.geotoolkit.image.io.SpatialImageWriter} object using this
     *              transcoder, or {@code null}.
     */
    protected MetadataTranscoder(final WarningProducer owner) {
        this.owner = owner;
    }

    /**
     * Creates an ISO {@code Metadata} object from the information known to this transcoder.
     *
     * @return The ISO metadata object.
     * @throws IOException If an I/O operation was necessary but failed.
     */
    public abstract T readMetadata() throws IOException;

    /**
     * Invoked when a warning occurred. The default implementation delegates to the object
     * given at construction time if any, or logs to the {@link #LOGGER} otherwise.
     */
    @Override
    public boolean warningOccurred(final LogRecord record) {
        if (owner != null) {
            return owner.warningOccurred(record);
        }
        LOGGER.log(record);
        return false;
    }

    /**
     * Returns the locale to use for formatting {@linkplain #warningOccurred(LogRecord) warnings}.
     * The default implementation delegates to the object given at construction time, or returns
     * {@code null} if none.
     * <p>
     * Note that overriding this method does not guaranteed that warnings will be produced with
     * the new locale. The actually used locale depends on which class produced the warning
     * message.
     */
    @Override
    public Locale getLocale() {
        return (owner != null) ? owner.getLocale() : null;
    }
}
