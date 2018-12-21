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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.storage.coverage.AbstractCoverageResource;
import org.geotoolkit.storage.coverage.GeoReferencedGridCoverageReader;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;


/**
 * Interoperability with legacy API.
 */
class ProductResource extends AbstractCoverageResource implements GridCoverageResource {
    final ProductEntry product;

    ProductResource(final DataStore store, final ProductEntry product)  {
        super(store, product.getIdentifier());
        this.product = product;
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
    public GridCoverageReader acquireReader() throws CatalogException {
        return new Reader();
    }

    @Override
    public GridCoverageWriter acquireWriter() throws CatalogException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Image getLegend() throws DataStoreException {
        return null;
    }

    private final class Reader extends GeoReferencedGridCoverageReader {
        Reader() {
            super(ProductResource.this);
        }

        @Override
        public List<GenericName> getCoverageNames() throws CoverageStoreException {
            return Collections.singletonList(ProductResource.this.getIdentifier());
        }

        @Override
        public GeneralGridGeometry getGridGeometry(int index) throws CoverageStoreException {
            return CoverageUtilities.toGeotk(product.getGridGeometry(), true);
        }

        @Override
        public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException {
            return CoverageUtilities.toGeotk(product.getSampleDimensions());
        }

        @Override
        protected GridCoverage readGridSlice(int[] areaLower, int[] areaUpper, int[] subsampling, GridCoverageReadParam param)
                throws CoverageStoreException, TransformException, CancellationException {
            try {
                final GeneralGridGeometry gg = getGridGeometry(0);
                final GeneralGridGeometry subg = GeoReferencedGridCoverageReader.getGridGeometry(gg, areaLower, areaUpper, subsampling);
                return CoverageUtilities.toGeotk(product.read(CoverageUtilities.toSIS(subg), null));
            } catch (DataStoreException ex) {
                throw new CoverageStoreException(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return product.getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return product.getSampleDimensions();
    }

    @Override
    public org.apache.sis.coverage.grid.GridCoverage read(GridGeometry areaOfInterest, int... bands) throws DataStoreException {
        return product.read(areaOfInterest, bands);
    }
}
