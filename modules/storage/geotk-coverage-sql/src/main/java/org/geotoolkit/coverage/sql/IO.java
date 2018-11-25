/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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

import java.nio.file.Path;
import org.opengis.coverage.Coverage;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.storage.coverage.CoverageResource;


/**
 * Temporary utility classes performing the I/O operations.
 * To be deleted in a future version.
 */
final class IO {
    private IO() {
    }

    static DataStore store(final String format, final Path path) throws DataStoreException {
        for (DataStoreProvider provider : DataStores.providers()) {
            if (format.equalsIgnoreCase(provider.getShortName())) {
                return provider.open(new StorageConnector(path));
            }
        }
        return DataStores.open(path);
    }

    static GridCoverage2D read(final String format, final Path path) throws DataStoreException {
        try (DataStore store = store(format, path)) {
            if (store instanceof CoverageResource) {
                CoverageReader reader = ((CoverageResource) store).acquireReader();
                final Coverage coverage = reader.read(0, null);
                reader.dispose();
                if (coverage instanceof GridCoverage2D) {
                    return (GridCoverage2D) coverage;
                }
            }
        }
        return null;
    }
}
