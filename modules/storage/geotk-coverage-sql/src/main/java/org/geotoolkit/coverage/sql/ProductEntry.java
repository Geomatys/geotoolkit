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
import java.time.Duration;
import java.sql.SQLException;

import org.opengis.util.NameSpace;
import org.opengis.util.GenericName;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.operation.TransformException;

import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.internal.storage.MetadataBuilder;
import org.apache.sis.util.logging.WarningListeners;

import org.geotoolkit.resources.Errors;


/**
 * A coverage product.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class ProductEntry extends AbstractGridResource {
    /**
     * The name of this product.
     */
    private final String name;

    /**
     * Same value than {@link #name} but provided as a name space.
     */
    private final NameSpace namespace;

    /**
     * The spatiotemporal grid geometry. This information may be only approximate.
     */
    private final GridGeometry exportedGrid;

    /**
     * Typical time interval between images, or {@code null} if unknown.
     * For example a product of weekly <cite>Sea Surface Temperature</cite> (SST) coverages
     * may set this field to 7, while a product of monthly SST coverage may set this field
     * to 30. The value is only approximate.
     */
    private final Duration temporalResolution;

    /**
     * A representative format for this product. If the product uses different formats,
     * then this is an arbitrary format in that list.
     */
    private final FormatEntry format;

    /**
     * Identifier to an entry in {@code metadata.Metadata} table, or {@code null} if none.
     *
     * @todo not yet used.
     */
    private final String metadata;

    /**
     * The database that produced this entry.
     */
    private final Database database;

    /**
     * Creates a new product.
     *
     * @param name                the product name.
     * @param exportedGrid        the spatial component of the grid geometry. This information may be only approximate.
     * @param temporalResolution  typical time interval between images, or {@code null} if unknown.
     * @param metadata            optional entry in {@code metadata.Metadata} table, or {@code null}.
     */
    ProductEntry(final Database database, final String name, final GridGeometry exportedGrid, final Duration temporalResolution,
            final FormatEntry format, final String metadata)
    {
        super((WarningListeners<DataStore>) null);
        this.name               = name;
        this.exportedGrid       = exportedGrid;
        this.temporalResolution = temporalResolution;
        this.format             = format;
        this.metadata           = metadata;
        this.database           = database;
        this.namespace          = database.nameFactory.createNameSpace(database.nameFactory.createLocalName(null, name), null);
    }

    /**
     * Returns a string representation of this product for debugging purpose.
     *
     * @return string representation (may change in any future version).
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns a unique identifier for this resource.
     */
    @Override
    public GenericName getIdentifier() {
        return namespace.name();
    }

    /**
     * Creates an identifier for a subset of this resource.
     */
    final GenericName createIdentifier(final String subset) {
        return database.nameFactory.createLocalName(namespace, subset);
    }

    /**
     * Returns the resources bundle for error messages.
     */
    private Errors errors() {
        return Errors.getResources(database.locale);
    }

    /**
     * Creates a new transaction with the database.
     */
    private Transaction transaction() throws SQLException {
        return database.transaction();
    }

    /**
     * Creates the metadata when first needed. Metadata contains:
     *
     * <ul>
     *   <li>A time range encompassing all coverages in this product.</li>
     *   <li>The geographic bounding box. If the CRS used by the database is not geographic
     *       (for example if it is a projected CRS), then this method will transform the product
     *       envelope from the product CRS to a geographic CRS.
     * </ul>
     *
     * This method also initializes {@link #gridGeometry} as a side-effect.
     */
    @Override
    protected void createMetadata(final MetadataBuilder metadata) throws DataStoreException {
        try {
            metadata.addIdentifier(null, name, MetadataBuilder.Scope.RESOURCE);
            metadata.addExtent(exportedGrid.getEnvelope());
            // TODO
//          metadata.addResolution(exportedGrid);
//          metadata.addTemporalResolution(temporalResolution);
        } catch (TransformException e) {
            throw new DataStoreException(e);
        }
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return exportedGrid;
    }

    @Override
    public List<SampleDimension> getSampleDimensions() {
        return format.sampleDimensions;
    }

    @Override
    public GridCoverage read(GridGeometry areaOfInterest, int... bands) throws DataStoreException {
        return subset(exportedGrid.getEnvelope()).read(areaOfInterest, bands);
    }

    /**
     * Returns a reference to every coverages available in this product which intersect the given envelope.
     * If the given envelope is {@code null}, then this method returns the references to every coverages
     * available for this product regardless of their envelope.
     *
     * @param  areaOfInterest  the envelope for filtering the coverages, or {@code null} for no filtering.
     * @return the set of coverages of this product which intersect the given envelope, or {@code null} if none.
     * @throws CatalogException if an error occurred while querying the database.
     */
    final ProductSubset subset(final Envelope areaOfInterest) throws CatalogException {
        final List<GridCoverageEntry> entries;
        try (Transaction transaction = transaction(); GridCoverageTable table = new GridCoverageTable(transaction)) {
            entries = table.find(name, areaOfInterest);
        } catch (SQLException exception) {
            throw new CatalogException(exception);
        } catch (TransformException exception) {
            throw new MismatchedReferenceSystemException(errors()
                    .getString(Errors.Keys.IllegalCoordinateReferenceSystem, exception));
        }
        if (entries.isEmpty()) {
            return null;
        }
        return new ProductSubset(this, areaOfInterest, entries);
    }
}
