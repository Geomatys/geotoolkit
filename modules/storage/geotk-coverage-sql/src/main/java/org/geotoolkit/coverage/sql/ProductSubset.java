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

import java.util.List;
import org.opengis.util.GenericName;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.WarningListeners;
import org.apache.sis.internal.storage.AbstractGridResource;


final class ProductSubset extends AbstractGridResource {
    private final ProductEntry product;

    private final List<GridCoverageEntry> entries;

    /**
     * @todo Need a better way than using a representative coverage.
     */
    private final GridCoverageEntry representative;

    ProductSubset(final ProductEntry product, final List<GridCoverageEntry> entries) {
        super((WarningListeners<DataStore>) null);
        this.product = product;
        this.entries = entries;
        representative = entries.get(0);
    }

    @Override
    public GenericName getIdentifier() {
        return product.createIdentifier("subset");      // TODO: need a unique name.
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return representative.getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return representative.getSampleDimensions();
    }

    @Override
    public GridCoverage read(final GridGeometry areaOfInterest, final int... bands) throws DataStoreException {
        // TODO: select the entry to use.
        if (ProductCoverage.HACK) {
            return entries.get(0).coverage();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
