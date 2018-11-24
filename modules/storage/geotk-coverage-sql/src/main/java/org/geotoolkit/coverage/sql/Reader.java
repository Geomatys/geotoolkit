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

import java.util.List;
import java.util.Collections;
import java.sql.SQLException;

import org.opengis.util.GenericName;
import org.opengis.geometry.Envelope;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.Coverage;
import org.opengis.referencing.operation.TransformException;

import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.Resource;

import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.storage.coverage.CoverageResource;
import org.geotoolkit.storage.coverage.CoverageUtilities;


final class Reader extends GridCoverageReader {
    private final DatabaseStore.Raster entry;

    Reader(final DatabaseStore.Raster entry) {
        this.entry = entry;
    }

    @Override
    public List<GenericName> getCoverageNames() throws CoverageStoreException {
        return Collections.singletonList(entry.getIdentifier());
    }

    @Override
    public GeneralGridGeometry getGridGeometry(int index) throws CoverageStoreException {
        try (Transaction transaction = entry.transaction()) {
            return CoverageUtilities.toGeotk(entry.product(transaction).getGridGeometry(transaction));
        } catch (SQLException | TransformException e) {
            throw new CatalogException(e);
        }
    }

    @Override
    public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException {
        return null;
    }

    @Override
    public GridCoverage read(final int index, final GridCoverageReadParam param) throws CoverageStoreException {
        Envelope envelope = null;
        if (param != null) {
            envelope = param.getEnvelope();
        }
        if (envelope == null) {
            throw new CoverageStoreException("Must specify an envelope.");
        }
        final List<GridCoverageReference> coverages;
        try (Transaction transaction = entry.transaction()) {
            final Product product = entry.product(transaction);
            coverages = product.getCoverageReferences(transaction, envelope);
        } catch (SQLException e) {
            throw new CatalogException(e);
        }
        for (final GridCoverageReference c : coverages) {
            try (DataStore store = IO.store(c.getFormat(), c.getPath())) {
                final CoverageResource r = resource(store);
                if (r != null) {
                    CoverageReader reader = r.acquireReader();
                    final Coverage coverage = reader.read(c.imageIndex, param);
                    reader.dispose();
                    if (coverage instanceof GridCoverage) {
                        return (GridCoverage) coverage;
                    }
                }
            } catch (CoverageStoreException e) {
                throw e;
            } catch (DataStoreException e) {
                throw new CatalogException(e);
            }
        }
        return null;
    }

    private static CoverageResource resource(final Resource resource) throws DataStoreException {
        if (resource instanceof Aggregate) {
            for (final Resource child : ((Aggregate) resource).components()) {
                CoverageResource r = resource(child);
                if (r != null) return r;
            }
        } else if (resource instanceof CoverageResource) {
            return (CoverageResource) resource;
        }
        return null;
    }

    @Override
    public void dispose() throws CoverageStoreException {
        super.dispose();
    }
}
