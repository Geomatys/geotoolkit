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

import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.internal.storage.MetadataBuilder;
import org.apache.sis.internal.util.CollectionsExt;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreReferencingException;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.resources.Errors;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedReferenceSystemException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;
import org.opengis.util.NameSpace;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * A coverage product, which may be an aggregate of other products.
 * This class provides the public methods of {@link Aggregate} and {@link GridCoverageResource}
 * but without implementing those interfaces, since the set of interfaces to implement depends
 * on whether this entry is for a product or a group of products (or both - this is allowed).
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 */
final class ProductEntry extends Entry {
    /**
     * The database that produced this entry.
     */
    private final Database database;

    /**
     * The parent of this product, or {@code null} if none.
     */
    private final String parent;

    /**
     * The name of this product.
     */
    private final String name;

    /**
     * Same value than {@link #name} but provided as a name space.
     */
    private final NameSpace namespace;

    /**
     * The spatiotemporal grid geometry of the datacube to give to users, or {@code null} if none.
     * This information may be {@code null} or only approximate. If {@code null}, then we have no
     * datacube for this product but we may have an aggregate of sub-products.
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
     * A representative format for this product, or {@code null} if none.
     * If the product uses different formats, then this is an arbitrary format in that list.
     */
    private final FormatEntry format;

    /**
     * Identifier to an entry in {@code metadata.Metadata} table, or {@code null} if none.
     *
     * @todo not yet used.
     */
    private final String metadata;

    /**
     * The sub-products (maybe empty), or {@code null} if not yet determined. Shall be an unmodifiable list.
     */
    private List<ProductEntry> components;

    /**
     * {@code true} if this product has been deleted.
     */
    private volatile boolean isDeleted;

    /**
     * Creates a new product.
     *
     * @param parent              the parent of this product, or {@code null} if none.
     * @param name                the product name.
     * @param exportedGrid        the spatial component of the grid geometry. May be {@code null} or only approximate.
     * @param temporalResolution  typical time interval between images, or {@code null} if unknown.
     * @param metadata            optional entry in {@code metadata.Metadata} table, or {@code null}.
     */
    ProductEntry(final Database database, final String parent, final String name, final GridGeometry exportedGrid,
            final Duration temporalResolution, final FormatEntry format, final String metadata)
    {
        this.database           = database;
        this.parent             = parent;
        this.name               = name;
        this.exportedGrid       = exportedGrid;
        this.temporalResolution = temporalResolution;
        this.format             = format;
        this.metadata           = metadata;
        this.namespace          = database.nameFactory.createNameSpace(database.nameFactory.parseGenericName(null, name), null);
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
     *
     * @see GridCoverageResource#getIdentifier()
     */
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
     * If this product has a parent, adds itself to the component of that parent.
     * Otherwise adds itself to the given map.
     *
     * This method is not synchronized. All {@code ProductEntry} initialized with this method
     * shall be created in the same thread.
     *
     * @param  products  a map of products by names. Will be filled by this method.
     * @return whether this method has been able to proceed or not.
     */
    final boolean dispatch(final Map<String,ProductEntry> products) throws CatalogException {
        if (parent == null) {
            if (products.put(name, this) != null) {
                throw new CatalogException("Duplicated product: " + name);
            }
            return true;
        } else {
            final ProductEntry p = products.get(parent);
            if (p == null) {
                return false;
            }
            List<ProductEntry> addTo = p.components;
            if (addTo == null) {
                p.components = addTo = new ArrayList<>();
            }
            return addTo.add(this);
        }
    }

    /**
     * Invoked after the caller finished to create {@link #dispatch(Map)} for all products.
     * This method is not synchronized. All {@code ProductEntry} initialized with this method
     * shall be created in the same thread.
     */
    final void finish() {
        components = (components != null) ? CollectionsExt.unmodifiableOrCopy(components) : Collections.emptyList();
    }

    /**
     * Fetches now the list of components if not already available.
     * This method should be invoked when the caller is going to invoke {@link #components()} soon.
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    final synchronized List<ProductEntry> components(final ProductTable table) throws SQLException, DataStoreException {
        if (components == null) {
            components = table.list(name);
        }
        return components;
    }

    /**
     * Returns the sub-products. This method fetches them when first needed, if not already known.
     * Deferred listing of components may happen if the product was fetched by name instead than
     * by listing all products.
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public synchronized List<ProductEntry> components() throws DataStoreException {
        ensureValid();
        if (components == null) {
            try (Transaction transaction = database.transaction();
                 ProductTable table = new ProductTable(transaction))
            {
                components = table.list(name);
            } catch (SQLException e) {
                throw new CatalogException(e);
            }
        }
        return components;
    }

    /**
     * Returns whether this product can be considered as a grid resource.
     */
    final boolean isGrid() {
        return exportedGrid != null;
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
    final void createMetadata(final MetadataBuilder metadata) throws DataStoreException {
        ensureValid();
        metadata.addIdentifier(null, name, MetadataBuilder.Scope.RESOURCE);
        metadata.addSpatialRepresentation(null, exportedGrid, true);
        if (exportedGrid != null) try {
            metadata.addExtent(exportedGrid.getEnvelope());
        } catch (TransformException e) {
            throw new DataStoreReferencingException(e);

        }
//      metadata.addTemporalResolution(temporalResolution);                 // TODO
        if (format != null) {
            for (final SampleDimension band : format.sampleDimensions) {
                metadata.addNewBand(band);
            }
        }
    }

    /**
     * Returns the grid geometry envelope, or {@code null} if unknown.
     * This method is invoked indirectly by {@link #createMetadata(MetadataBuilder)}.
     */
    public Envelope getEnvelope() throws CatalogException {
        ensureValid();
        return (exportedGrid != null) ? exportedGrid.getEnvelope() : null;
    }

    public GridGeometry getGridGeometry() throws CatalogException {
        ensureValid();
        if (exportedGrid != null) {
            return exportedGrid;
        } else {
            throw new CatalogException("No grid geometry for \"" + name + "\" product.");
        }
    }

    /**
     * Returns sample dimensions "global" to this product. Those sample dimensions may be slightly different than the
     * sample dimensions returned {@link GridCoverage#getSampleDimensions()} if {@link FormatEntry#approximate} is true.
     */
    public List<SampleDimension> getSampleDimensions() throws CatalogException {
        ensureValid();
        if (format != null) {
            return format.sampleDimensions;
        } else {
            throw new CatalogException("No sample dimension for \"" + name + "\" product.");
        }
    }

    final boolean isImplementedBySIS() {
        return (format != null) && format.isImplementedBySIS();
    }

    public GridCoverage read(GridGeometry areaOfInterest, int... bands) throws DataStoreException {
        // SIS API (GridCoverageResource#read) specifies that a null area should fallback to entire domain.
        if (areaOfInterest == null) {
            areaOfInterest = getGridGeometry();
        }
        Envelope envelope = areaOfInterest.getEnvelope();
        return subset(envelope, getGridGeometry().getResolution(true)).read(areaOfInterest, bands);
    }

    /**
     * Returns a reference to every coverages available in this product which intersect the given envelope.
     * If the given envelope is {@code null}, then this method returns the references to every coverages
     * available for this product regardless of their envelope.
     *
     * @param  areaOfInterest  the envelope for filtering the coverages, or {@code null} for no filtering.
     * @param  resolution      resolution in unit of AOI, or {@code null} for no sub-sampling.
     * @return the set of coverages of this product which intersect the given envelope, or {@code null} if none.
     * @throws DataStoreException if an error occurred while querying the database.
     */
    private ProductSubset subset(final Envelope areaOfInterest, final double[] resolution) throws DataStoreException {
        ensureValid();
        try (Transaction transaction = database.transaction();
             GridCoverageTable table = new GridCoverageTable(transaction))
        {
            final List<GridCoverageEntry> entries = table.find(name, areaOfInterest);
            if (!entries.isEmpty()) {
                return new ProductSubset(this, areaOfInterest, resolution, entries);
            }
        } catch (DataStoreException exception) {
            throw exception;
        } catch (TransformException exception) {
            throw new MismatchedReferenceSystemException(Errors.getResources(database.locale)
                    .getString(Errors.Keys.IllegalCoordinateReferenceSystem, exception));
        } catch (Exception exception) {
            throw new CatalogException(exception);
        }
        throw new DisjointCoverageDomainException("No data in \"" + name + "\" product for the given area of interest.");
    }

    /**
     * Removes this product from the given database.
     */
    final void remove() throws DataStoreException {
        try (Transaction transaction = database.transaction()) {
            transaction.writeStart();
            try (ProductTable table = new ProductTable(transaction)) {
                removeCached(table);
                table.delete(name);
            }
            transaction.writeEnd();
        } catch (SQLException e) {
            throw new CatalogException(e);
        }
    }

    /**
     * Removed recursively all cached values.
     */
    private void removeCached(final ProductTable table) throws SQLException, DataStoreException {
        table.removeCached(name);
        isDeleted = true;
        for (final ProductEntry c : components(table)) {
            c.removeCached(table);
        }
    }

    /**
     * Throws an exception if this product has been deleted.
     */
    private void ensureValid() throws CatalogException {
        if (isDeleted) {
            throw new CatalogException("Product \"" + name + "\" has been deleted.");
        }
    }
}
