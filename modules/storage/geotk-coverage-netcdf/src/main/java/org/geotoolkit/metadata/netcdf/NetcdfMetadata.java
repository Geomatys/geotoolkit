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
package org.geotoolkit.metadata.netcdf;

import java.util.Locale;
import java.util.logging.LogRecord;
import org.geotoolkit.image.io.WarningProducer;
import org.apache.sis.storage.netcdf.AttributeNames;


/**
 * Mapping from/to NetCDF metadata to ISO 19115-2 metadata.
 * See {@link AttributeNames} for the list of NetCDF attributes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public abstract class NetcdfMetadata extends AttributeNames implements WarningProducer {
    /**
     * Where to send the warnings, or {@code null} if none.
     */
    final WarningProducer owner;

    /**
     * Creates a new metadata reader or writer for the given source or destination.
     *
     * @param owner Typically the {@link org.geotoolkit.image.io.SpatialImageReader} or
     *              {@link org.geotoolkit.image.io.SpatialImageWriter} object using this
     *              transcoder, or {@code null}.
     */
    protected NetcdfMetadata(final WarningProducer owner) {
        this.owner = owner;
    }

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
