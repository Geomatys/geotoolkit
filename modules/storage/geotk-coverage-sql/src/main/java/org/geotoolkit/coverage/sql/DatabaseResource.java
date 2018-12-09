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
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.storage.coverage.AbstractCoverageResource;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;


final class DatabaseResource extends AbstractCoverageResource {
    private Product product;

    DatabaseResource(final DatabaseStore store, final String product) {
        super(store, new NamedIdentifier(null, product));
    }

    @Override
    protected DefaultMetadata createMetadata() throws DataStoreException {
        try (Transaction tr = transaction()) {
            return product(tr).createMetadata(tr);
        } catch (SQLException | TransformException e) {
            throw new CatalogException(e);
        }
    }

    @Override
    public int getImageIndex() {
        return 0;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    final Transaction transaction() throws SQLException {
        return ((DatabaseStore) store).database.transaction();
    }

    final synchronized Product product(final Transaction transaction) throws SQLException, CatalogException {
        if (product == null) {
            try (ProductTable table = new ProductTable(transaction)) {
                product = table.getEntry(identifier.getCode());
            }
        }
        return product;
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

    private final class Reader extends GridCoverageReader {
        Reader() {
        }

        @Override
        public List<GenericName> getCoverageNames() throws CoverageStoreException {
            return Collections.singletonList(DatabaseResource.this.getIdentifier());
        }

        @Override
        public GeneralGridGeometry getGridGeometry(int index) throws CoverageStoreException {
            try (Transaction transaction = transaction()) {
                return CoverageUtilities.toGeotk(product(transaction).getGridGeometry(transaction));
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
            final List<GridCoverageStack> coverages;
            try (Transaction transaction = transaction()) {
                final Product product = product(transaction);
                coverages = product.getCoverageReferences(transaction, envelope);
            } catch (SQLException e) {
                throw new CatalogException(e);
            }
            for (final GridCoverageStack c : coverages) {
                try {
                    c.read(envelope);   // TODO
                } catch (CoverageStoreException e) {
                    throw e;
                } catch (DataStoreException | TransformException e) {
                    throw new CatalogException(e);
                }
            }
            return null;
        }
    }
}
