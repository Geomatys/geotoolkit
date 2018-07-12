/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2018, Geomatys
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

import java.awt.Image;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.storage.coverage.AbstractCoverageResource;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class CoverageSQLResource extends AbstractCoverageResource {

    private final CoverageSQLStore store;

    CoverageSQLResource(final CoverageSQLStore store, GenericName name) {
        super(store, name);
        this.store = store;
    }

    @Override
    public int getImageIndex() {
        return 0;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        return store.db.createGridCoverageReader(getIdentifier().tip().toString());
    }

    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        return store.db.createGridCoverageWriter(getIdentifier().tip().toString());
    }

    @Override
    public Image getLegend() throws DataStoreException {
        return null;
    }

}
