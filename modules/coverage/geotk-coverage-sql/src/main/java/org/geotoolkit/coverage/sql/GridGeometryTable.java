/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2010, Geomatys
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

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Dimension;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransformFactory;

import org.geotoolkit.util.collection.WeakHashSet;
import org.geotoolkit.internal.sql.table.Column;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.SingletonTable;
import org.geotoolkit.internal.sql.table.CatalogException;
import org.geotoolkit.internal.sql.table.IllegalRecordException;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.geotoolkit.referencing.factory.IdentifiedObjectFinder;
import org.geotoolkit.referencing.factory.wkt.AuthorityFactoryProvider;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.resources.Errors;

import static java.lang.reflect.Array.getLength;
import static java.lang.reflect.Array.getDouble;


/**
 * Connection to a table of grid geometries.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Antoine Hnawia (IRD)
 * @version 3.10
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class GridGeometryTable extends SingletonTable<GridGeometryEntry> {
    /**
     * The authority factory connected to the PostGIS {@code "spatial_ref_sys"} table.
     * Will be created when first needed.
     */
    private transient CRSAuthorityFactory crsFactory;

    /**
     * A set of CRS descriptions created up to date. Cached because we
     * will typically have many grid geometries using the same set of CRS.
     */
    private final WeakHashSet<SpatialRefSysEntry> gridCRS;

    /**
     * Constructs a new {@code GridGeometryTable}.
     *
     * @param connection The connection to the database.
     */
    public GridGeometryTable(final Database database) {
        this(new GridGeometryQuery(database));
    }

    /**
     * Constructs a new {@code GridGeometryTable} from the specified query.
     */
    private GridGeometryTable(final GridGeometryQuery query) {
        super(query, query.byIdentifier);
        gridCRS = WeakHashSet.newInstance(SpatialRefSysEntry.class);
    }

    /**
     * Returns the CRS authority factory backed by the PostGIS {@code "spatial_ref_sys"} table.
     * Because each {@link Database} maintain only one instance of {@code GridGeometryTable}, a
     * single {@link CRSAuthorityFactory} will be shared for all access to the same database.
     *
     * @throws FactoryException If the factory can not be created.
     */
    private synchronized CRSAuthorityFactory getAuthorityFactory() throws FactoryException {
        if (crsFactory == null) {
            final Database db = getDatabase();
            crsFactory = new AuthorityFactoryProvider(db.hints).createFromPostGIS(db.getDataSource(true));
        }
        return crsFactory;
    }

    /**
     * Returns a CRS identifier for the specified CRS. The given CRS should appears in the PostGIS
     * {@code "spatial_ref_sys"} table. The returned value is a primary key in the same table.
     *
     * @param  crs The CRS to search.
     * @return The identifier for the given CRS, or 0 if none.
     * @throws FactoryException if an error occured while searching for the CRS.
     */
    public int getSRID(final CoordinateReferenceSystem crs) throws FactoryException {
        final AbstractAuthorityFactory factory = (AbstractAuthorityFactory) getAuthorityFactory();
        final IdentifiedObjectFinder finder = factory.getIdentifiedObjectFinder(CoordinateReferenceSystem.class);
        final ReferenceIdentifier srid = AbstractIdentifiedObject.getIdentifier(finder.find(crs), Citations.POSTGIS);
        if (srid == null) {
            return 0;
        }
        final String code = srid.getCode();
        try {
            return Integer.parseInt(code);
        } catch (NumberFormatException e) {
            throw new FactoryException(Errors.format(Errors.Keys.UNPARSABLE_NUMBER_$1, code), e);
        }
    }

    /**
     * Creates a grid geometry from the current row in the specified result set.
     *
     * @param  results The result set to read.
     * @return The entry for current row in the specified result set.
     * @throws CatalogException if an inconsistent record is found in the database.
     * @throws SQLException if an error occured while reading the database.
     */
    @Override
    @SuppressWarnings("fallthrough")
    protected GridGeometryEntry createEntry(final ResultSet results) throws CatalogException, SQLException {
        final GridGeometryQuery query  = (GridGeometryQuery) super.query;
        final String identifier        = results.getString(indexOf(query.identifier));
        final int    width             = results.getInt   (indexOf(query.width));
        final int    height            = results.getInt   (indexOf(query.height));
        final double scaleX            = results.getDouble(indexOf(query.scaleX));
        final double shearY            = results.getDouble(indexOf(query.shearY));
        final double shearX            = results.getDouble(indexOf(query.shearX));
        final double scaleY            = results.getDouble(indexOf(query.scaleY));
        final double translateX        = results.getDouble(indexOf(query.translateX));
        final double translateY        = results.getDouble(indexOf(query.translateY));
        final int    horizontalSRID    = results.getInt   (indexOf(query.horizontalSRID));
        final int    verticalSRID      = results.getInt   (indexOf(query.verticalSRID));
        final Array  verticalOrdinates = results.getArray (indexOf(query.verticalOrdinates));
        /*
         * Creates the SpatialRefSysEntry object, looking for an existing one in the cache first.
         * If a new object has been created, it will be completed after insertion in the cache.
         */
        SpatialRefSysEntry srsEntry = new SpatialRefSysEntry(horizontalSRID, verticalSRID, SQLCoverageReader.TEMPORAL_CRS);
        synchronized (gridCRS) {
            final SpatialRefSysEntry candidate = gridCRS.unique(srsEntry);
            if (candidate != srsEntry) {
                srsEntry = candidate;
            } else try {
                srsEntry.createSpatioTemporalCRS(getAuthorityFactory());
            } catch (FactoryException exception) {
                gridCRS.remove(srsEntry);
                final Column column;
                switch (srsEntry.uninitialized()) {
                    case 1:  column = query.horizontalSRID; break;
                    case 2:  column = query.verticalSRID;   break;
                    default: column = query.identifier;;    break;
                }
                throw new IllegalRecordException(exception, this, results, indexOf(column), identifier);
            }
        }
        final double[] altitudes = asDoubleArray(verticalOrdinates);
        final MathTransformFactory mtFactory = getDatabase().getMathTransformFactory();
        final AffineTransform2D at = new AffineTransform2D(scaleX, shearY, shearX, scaleY, translateX, translateY);
        final Dimension size = new Dimension(width, height);
        final GridGeometryEntry entry;
        try {
            entry = new GridGeometryEntry(identifier, size, srsEntry, at, altitudes, mtFactory);
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) { // We want to catch only the checked exceptions here.
            throw new IllegalRecordException(exception, this, results, indexOf(query.identifier), identifier);
        }
        if (entry.isEmpty()) {
            throw new IllegalRecordException("The geodetic envelope is empty.", this, results, // TODO: localize
                    indexOf(width == 0 ? query.width : height == 0 ? query.height : query.identifier), identifier);
        }
        return entry;
    }

    /**
     * Returns the specified SQL array as an array of type {@code double[]}, or {@code null}
     * if the SQL array is null. The array is {@linkplain Array#free freeded} by this method.
     */
    private static double[] asDoubleArray(final Array verticalOrdinates) throws SQLException {
        final double[] altitudes;
        if (verticalOrdinates != null) {
            final Object data = verticalOrdinates.getArray();
            final int length = getLength(data);
            altitudes = new double[length];
            final Number[] asNumbers = (data instanceof Number[]) ? (Number[]) data : null;
            for (int i=0; i<length; i++) {
                final double z;
                if (asNumbers != null) {
                    z = asNumbers[i].doubleValue();
                } else {
                    z = getDouble(data, i);
                }
                altitudes[i] = z;
            }
            verticalOrdinates.free();
        } else {
            altitudes = null;
        }
        return altitudes;
    }

    /**
     * For every values in the specified map, replaces the collection of {@link GridGeometryEntry}
     * identifiers by a set of altitudes. On input, the values are usually {@code List<String>}.
     * On output, all values will be {@code SortedSet<Number>}.
     *
     * @param  centroids The date-extents map.
     * @return The same reference than {@code centroids}, but casted as a date-altitudes map.
     */
    SortedMap<Date,SortedSet<Number>> identifiersToAltitudes(final SortedMap<Date,List<String>> centroids)
            throws CatalogException, SQLException
    {
        final Map<Number,Number> numbers = new HashMap<Number,Number>(); // For sharing instances.
        final Map<SortedSet<Number>, SortedSet<Number>> pool = new HashMap<SortedSet<Number>, SortedSet<Number>>();
        final Map<List<String>,SortedSet<Number>> altitudesMap = new HashMap<List<String>,SortedSet<Number>>();
        for (final Map.Entry<Date,List<String>> entry : centroids.entrySet()) {
            final List<String> extents = entry.getValue();
            SortedSet<Number> altitudes = altitudesMap.get(extents);
            if (altitudes == null) {
                altitudes = new TreeSet<Number>();
                for (final String extent : extents) {
                    final double[] ordinates = getEntry(extent).getVerticalOrdinates();
                    if (ordinates != null) {
                        for (int i=0; i<ordinates.length; i++) {
                            final Number z = ordinates[i];
                            Number shared = numbers.get(z);
                            if (shared == null) {
                                shared = z;
                                numbers.put(shared, shared);
                            }
                            altitudes.add(shared);
                        }
                    }
                }
                /*
                 * Replaces the altitudes set by shared instances, in order to reduce memory usage.
                 * It is quite common to have many dates (if not all) associated with identical set
                 * of altitudes values.
                 */
                altitudes = Collections.unmodifiableSortedSet(altitudes);
                final SortedSet<Number> existing = pool.get(altitudes);
                if (existing != null) {
                    altitudes = existing;
                } else {
                    pool.put(altitudes, altitudes);
                }
                altitudesMap.put(extents, altitudes);
            }
            setValueUnsafe(entry, altitudes);
        }
        return unsafe(centroids);
    }

    /**
     * Unsafe setting on a map entry. Used because we are changing the map type in-place.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private static void setValueUnsafe(final Map.Entry entry, final SortedSet<Number> altitudes) {
        entry.setValue(altitudes);
    }

    /**
     * Unsafe cast of a map. Used because we changed the map type in-place.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private static SortedMap<Date,SortedSet<Number>> unsafe(final SortedMap centroids) {
        return centroids;
    }
}
