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
import org.opengis.geometry.Envelope;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.WarningListeners;
import org.apache.sis.internal.storage.AbstractGridResource;


final class ProductSubset extends AbstractGridResource {
    /**
     * The product for which this object is a subset.
     */
    private final ProductEntry product;

    /**
     * Area of data requested by user.
     */
    private final Envelope areaOfInterest;

    /**
     * Desired resolution in units of AOI, or {@code null} for no sub-sampling.
     */
    private final double[] resolution;

    /**
     * List of raster files intersection the {@link #areaOfInterest}.
     */
    private final List<GridCoverageEntry> entries;

    /**
     * An arbitrary element from {@link #entries} list.
     *
     * @todo Need a better way than using a representative coverage.
     */
    private final GridCoverageEntry representative;

    /**
     * Creates a new subset for the given product.
     */
    ProductSubset(final ProductEntry product, final Envelope areaOfInterest, final double[] resolution,
            final List<GridCoverageEntry> entries)
    {
        super((WarningListeners<DataStore>) null);
        this.product        = product;
        this.areaOfInterest = areaOfInterest;
        this.resolution     = resolution;
        this.entries        = entries;
        if (Entry.HACK) {
            representative = entries.get(entries.size() / 2);
        } else {
            throw new UnsupportedOperationException();
        }
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
    public GridCoverage read(final GridGeometry targetGeometry, final int... bands) throws DataStoreException {
        return representative.coverage(targetGeometry, bands);
    }
}
