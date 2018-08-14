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
import java.sql.SQLException;
import java.nio.file.Path;
import org.apache.sis.coverage.grid.GridGeometry;

import org.opengis.util.FactoryException;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.operation.TransformException;

import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;

import org.geotoolkit.util.DateRange;
import org.geotoolkit.resources.Errors;


/**
 * A coverage product.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 */
final class Product {
    /**
     * The name of this product.
     */
    final String name;

    /**
     * Typical resolution in metres, or {@link Double#NaN} if unknown.
     * The value is only approximate.
     */
    private final double spatialResolution;

    /**
     * Typical time interval (in days) between images, or {@link Double#NaN} if unknown.
     * For example a product of weekly <cite>Sea Surface Temperature</cite> (SST) coverages
     * may set this field to 7, while a product of monthly SST coverage may set this field
     * to 30. The value is only approximate.
     */
    private final double temporalResolution;

    /**
     * Identifier to an entry in {@code metadata.Metadata} table, or {@code null} if none.
     *
     * @todo not yet used.
     */
    private final String metadata;

    /**
     * The domain for this product, or {@code null} if not yet computed.
     * Will be computed only when first needed.
     *
     * @see #getDomain()
     */
    private DomainOfProductTable.Entry domain;

    /**
     * The geographic bounding box. Will be computed when first needed.
     */
    private GeographicBoundingBox boundingBox;

    /**
     * The database that produced this entry.
     */
    private final Database database;

    /**
     * Creates a new product.
     *
     * @param name                the product name.
     * @param spatialResolution   typical resolution in metres, or {@link Double#NaN} if unknown.
     * @param temporalResolution  typical time interval (in days) between images, or {@link Double#NaN} if unknown.
     * @param metadata            optional entry in {@code metadata.Metadata} table, or {@code null}.
     */
    Product(final Database database, final String name, final double spatialResolution, final double temporalResolution, final String metadata) {
        this.name               = name;
        this.spatialResolution  = spatialResolution;
        this.temporalResolution = temporalResolution;
        this.metadata           = metadata;
        this.database           = database;
    }

    /**
     * Returns the resources bundle for error messages.
     */
    private Errors errors() {
        return Errors.getResources(database.locale);
    }

    /**
     * Returns the domain of this product, or {@code null} if none.
     *
     * @throws SQLException if an error occurred while fetching the domain.
     */
    private DomainOfProductTable.Entry getDomain() throws SQLException {
        assert Thread.holdsLock(this);
        if (domain == null) {
            try (Transaction t=database.transaction(); DomainOfProductTable d=new DomainOfProductTable(t)) {
                domain = d.query(name);
            }
        }
        return domain;
    }

    /**
     * Returns a time range encompassing all coverages in this product, or {@code null} if none.
     *
     * @return the time range encompassing all coverages, or {@code null}.
     * @throws CatalogException if an error occurred while fetching the time range.
     */
    public synchronized DateRange getTimeRange() throws CatalogException {
        try {
            return getDomain().timeRange;
        } catch (SQLException e) {
            throw new CatalogException(e);
        }
    }

    /**
     * Returns the geographic bounding box, or {@code null} if unknown. If the CRS used by
     * the database is not geographic (for example if it is a projected CRS), then this method
     * will transform the product envelope from the product CRS to a geographic CRS.
     *
     * @return the product geographic bounding box, or {@code null} if none.
     * @throws CatalogException if an error occurred while querying the database
     *         or while projecting the product envelope.
     */
    public synchronized GeographicBoundingBox getGeographicBoundingBox() throws CatalogException {
        if (boundingBox == null) {
            final DefaultGeographicBoundingBox bbox = new DefaultGeographicBoundingBox();
            try {
                bbox.setBounds(getDomain().bbox);
            } catch (SQLException | TransformException e) {
                throw new CatalogException(e);
            }
            bbox.transition(DefaultGeographicBoundingBox.State.FINAL);
            boundingBox = bbox;
        }
        return boundingBox;
    }

    public synchronized GeneralGridGeometry getGridGeometry(final Transaction transaction) throws CoverageStoreException {
        final List<GridGeometryEntry> geometries;
        try (final GridCoverageTable table = new GridCoverageTable(transaction)) {
            geometries = table.getGridGeometries(name);
        } catch (SQLException e) {
            throw new CatalogException(e);
        }
        if (geometries.isEmpty()) {
            return null;
        }
        if (domain == null) {
            try (DomainOfProductTable table=new DomainOfProductTable(transaction)) {
                domain = table.query(name);
            } catch (SQLException e) {
                throw new CatalogException(e);
            }
        }
        int index = 0;      // TODO: select the "best" geometry.
        final DateRange dates = domain.timeRange;
        return geometries.get(index).getGridGeometry((dates != null) ? dates.getMinValue() : null,
                                                     (dates != null) ? dates.getMaxValue() : null);
    }

    /**
     * Returns a reference to every coverages available in this product which intersect the given envelope.
     * If the given envelope is {@code null}, then this method returns the references to every coverages
     * available for this product regardless of their envelope.
     *
     * @param  areaOfInterest  the envelope for filtering the coverages, or {@code null} for no filtering.
     * @return the set of coverages of this product which intersect the given envelope.
     * @throws CatalogException if an error occurred while querying the database.
     */
    List<GridCoverageReference> getCoverageReferences(final Transaction transaction, final Envelope areaOfInterest) throws CatalogException {
        final List<GridCoverageReference> entries;
        try (final GridCoverageTable table = new GridCoverageTable(transaction)) {
            entries = table.find(name, areaOfInterest);
        } catch (SQLException exception) {
            throw new CatalogException(exception);
        } catch (TransformException exception) {
            throw new MismatchedReferenceSystemException(errors()
                    .getString(Errors.Keys.IllegalCoordinateReferenceSystem, exception));
        }
        return entries;
    }

    /**
     * Adds new coverage references in the database.
     */
    public void addCoverageReference(final Transaction transaction, final NewRaster... rasters) throws CatalogException {
        try (final GridCoverageTable table = new GridCoverageTable(transaction)) {
            for (final NewRaster r : rasters) {
                table.add(name, name, r.path, r.geometry, r.imageIndex);
            }
        } catch (SQLException | FactoryException | TransformException exception) {
            throw new CatalogException(exception);
        }
    }

    static final class NewRaster {
        Path path;
        GridGeometry geometry;
        int imageIndex;
    }
}
