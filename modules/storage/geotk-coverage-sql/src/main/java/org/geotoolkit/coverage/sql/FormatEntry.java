/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2018, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.List;
import java.nio.file.Path;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.coverage.SampleDimension;


/**
 * Information about a raster format.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class FormatEntry extends Entry {
    /**
     * The data store provider to use for opening files, or {@code null} if unknown.
     */
    private final DataStoreProvider provider;

    /**
     * The sample dimensions for coverages encoded with this format, or {@code null} if undefined.
     * If non-null, then the list is guaranteed to be non-empty and the list size is equals to the
     * expected number of bands.
     *
     * <p>Empty lists are not allowed because our Raster I/O framework interprets that as "no bands",
     * as opposed to "unknown bands" (which is what we mean in the particular case of our database
     * schema).</p>
     *
     * <p>Each {@code SampleDimension} specifies how to convert pixel values to geophysics values.
     * For example coverages read from PNG files will typically store their data as integer values
     * (non-geophysics), while coverages read from ASCII files will often store their pixel values
     * as real numbers (geophysics values).</p>
     */
    final List<SampleDimension> sampleDimensions;

    /**
     * Reference to an entry in the {@code metadata.Format} table, or {@code null}.
     */
    private final String metadata;

    /**
     * Creates a new entry for this format.
     *
     * @param driver    the format name (i.e. the plugin to use).
     * @param bands     sample dimensions for coverages encoded with this format, or {@code null}.
     *                  The bands given to this constructor shall <strong>not</strong> be geophysics.
     * @param metadata  reference to an entry in the {@code metadata.Format} table, or {@code null}.
     */
    FormatEntry(String driver, final List<SampleDimension> bands, final String metadata) {
        driver = driver.trim();
        sampleDimensions = bands;
        this.metadata    = metadata;
        for (DataStoreProvider provider : DataStores.providers()) {
            if (driver.equalsIgnoreCase(provider.getShortName())) {
                this.provider = provider;
                return;
            }
        }
        provider = null;
    }

    /**
     * Opens the resource at the given path using the data store provider referenced in this format entry.
     *
     * @param  path  path to the file to open. Shall be resolved (i.e. should include the {@linkplain Database#root root directory}).
     */
    final DataStore open(final Path path) throws DataStoreException {
        if (provider != null) {
            return provider.open(new StorageConnector(path));
        } else {
            return DataStores.open(path);
        }
    }

    /**
     * Returns {@code true} if the provider is an Apache SIS implementation.
     * This is used as a temporary workaround while we are migrating from Geotk data store API to Apache SIS data store API.
     */
    final boolean isImplementedBySIS() {
        return (provider != null) && provider.getClass().getName().startsWith("org.apache.sis.");
    }
}
