/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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

import java.util.Collections;
import java.io.IOException;
import ucar.nc2.NetcdfFileWriteable;

import org.opengis.metadata.Metadata;
import org.opengis.metadata.lineage.Lineage;
import org.opengis.metadata.quality.DataQuality;
import org.opengis.util.InternationalString;

import org.geotoolkit.util.Version;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.image.io.WarningProducer;


/**
 * Mapping from ISO 19115-2 metadata to NetCDF metadata.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
public class NetcdfMetadataWriter extends NetcdfMetadata {
    /**
     * The NetCDF file where to write ISO metadata.
     * This file is set at construction time.
     * <p>
     * This {@code NetcdfMetadataReader} class does <strong>not</strong> close this file.
     * Closing this file after usage is the user responsibility.
     */
    protected final NetcdfFileWriteable file;

    /**
     * Creates a new <cite>ISO to NetCDF</cite> mapper for the given file.
     *
     * @param file  The NetCDF file where to write metadata.
     * @param owner Typically the {@link org.geotoolkit.image.io.SpatialImageWriter} instance
     *              using this encoder, or {@code null}.
     */
    public NetcdfMetadataWriter(final NetcdfFileWriteable file, final WarningProducer owner) {
        super(owner);
        ArgumentChecks.ensureNonNull("file", file);
        this.file  = file;
    }

    /**
     * Returns the given collection if non-null, or an empty set otherwise.
     */
    private static <E> Iterable<E> nonNull(Iterable<E> collection) {
        if (collection == null) {
            collection = Collections.emptySet();
        }
        return collection;
    }

    /**
     * Returns a string representation of the given text if non-null and non-empty,
     * or {@code null} otherwise.
     */
    private String toString(final InternationalString text) {
        if (text != null) {
            String s = text.toString(getLocale());
            if (s != null && !((s = s.trim()).isEmpty())) {
                return s;
            }
        }
        return null;
    }

    /**
     * Writes NetCDF attribute values for the given metadata object.
     *
     * @param  metadata The metadata object to write, or {@code null}.
     * @throws IOException If an error occurred while writing the attribute values.
     */
    public void write(final Metadata metadata) throws IOException {
        final StringBuilder history = new StringBuilder(80);
        if (metadata != null) {
            for (final DataQuality quality : nonNull(metadata.getDataQualityInfo())) {
                final Lineage lineage = quality.getLineage();
                if (lineage != null) {
                    if (history.length() != 0) {
                        history.append('\n');
                    }
                    final String s = toString(lineage.getStatement());
                    if (s != null) {
                        history.append(s);
                    }
                }
            }
        }
        if (history.length() == 0) {
            history.append("Created by Geotoolkit.org version ").append(Version.GEOTOOLKIT);
        }
        file.addGlobalAttribute(HISTORY, history.toString());
    }
}
