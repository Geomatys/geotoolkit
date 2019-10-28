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
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.storage.AbstractResource;
import org.opengis.geometry.Envelope;


/**
 * Interoperability with legacy API.
 */
class ProductResource extends AbstractResource implements GridCoverageResource, StoreResource {

    protected final DataStore store;
    final ProductEntry product;

    ProductResource(final DataStore store, final ProductEntry product)  {
        super(product.getIdentifier());
        this.store = store;
        this.product = product;
    }

    @Override
    public DataStore getOriginator() {
        return store;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.ofNullable(product.getEnvelope());
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
