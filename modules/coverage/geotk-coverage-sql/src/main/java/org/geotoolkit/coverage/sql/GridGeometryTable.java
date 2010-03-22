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
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransformFactory;

import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.util.collection.WeakHashSet;
import org.geotoolkit.internal.sql.table.Column;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.QueryType;
import org.geotoolkit.internal.sql.table.LocalCache;
import org.geotoolkit.internal.sql.table.SingletonTable;
import org.geotoolkit.internal.sql.table.IllegalRecordException;
import org.geotoolkit.internal.sql.table.SpatialDatabase;
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
@ThreadSafe(concurrent = true)
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
     * @param  identifier The identifier of the grid geometry to create.
     * @return The entry for current row in the specified result set.
     * @throws SQLException if an error occured while reading the database.
     */
    @Override
    @SuppressWarnings("fallthrough")
    protected GridGeometryEntry createEntry(final ResultSet results, final Comparable<?> identifier) throws SQLException {
        final GridGeometryQuery query  = (GridGeometryQuery) super.query;
        final SpatialDatabase database = (SpatialDatabase) getDatabase();
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
        SpatialRefSysEntry srsEntry = new SpatialRefSysEntry(horizontalSRID, verticalSRID, database.temporalCRS);
        synchronized (gridCRS) {
            final SpatialRefSysEntry candidate = gridCRS.unique(srsEntry);
            if (candidate != srsEntry) {
                srsEntry = candidate;
            } else try {
                srsEntry.createSpatioTemporalCRS(database, getAuthorityFactory());
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
            throw new IllegalRecordException(errors().getString(Errors.Keys.EMPTY_ENVELOPE), this, results,
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
// TODO: Uncomment when the JDBC driver will support this method.
// In order to test if the JDBC driver support this method, just
// uncomment the line below and try running GridGeometryTableTest.
//          verticalOrdinates.free();
        } else {
            altitudes = null;
        }
        return altitudes;
    }

    /**
     * Returns {@code true} if the specified arrays are equal when comparing the values
     * at {@code float} precision. This method is a workaround for the cases where some
     * original array was stored with {@code double} precision while the other array has
     * been casted to {@code float} precision. The precision lost causes the comparison
     * to fail when comparing the array at full {@code double} precision. For example
     * {@code (double) 0.1f} is not equals to {@code 0.1}.
     */
    private static boolean equalsAsFloat(final double[] a1, final double[] a2) {
        if (a1 == null || a2 == null || a1.length != a2.length) {
            return false;
        }
        for (int i=0; i<a1.length; i++) {
            if (Float.floatToIntBits((float) a1[i]) != Float.floatToIntBits((float) a2[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * For every values in the specified map, replaces the collection of {@link GridGeometryEntry}
     * identifiers by a set of altitudes. On input, the values are usually {@code List<Integer>}.
     * On output, all values will be {@code SortedSet<Number>}.
     * <p>
     * This method is for internal usage by {@link GridCoverageTable#getAvailableCentroids()} only.
     *
     * @param  centroids The date-extents map.
     * @return The same reference than {@code centroids}, but casted as a date-altitudes map.
     */
    NavigableMap<Date,SortedSet<Number>> identifiersToAltitudes(final NavigableMap<Date,List<Comparable<?>>> centroids) throws SQLException {
        /*
         * For sharing instances of java.lang.Double created by this method. Useful since
         * GridCoverageEntry instances at different dates usually still declare the same
         * set of altitudes.
         */
        final Map<Number,Number> sharedNumbers = new HashMap<Number,Number>();
        /*
         * For sharing instances of SortedSet<Number>. This serves the same purpose than
         * 'sharedNumbers', but applied to the whole Set instead than individual numbers.
         */
        final Map<SortedSet<Number>, SortedSet<Number>> sharedSet =
                new HashMap<SortedSet<Number>, SortedSet<Number>>();
        /*
         * The results of conversions from List<Comparable<?>> to SortedSet<Number> found
         * in previous execution of the loop. This is cached because we often have the same
         * list of geographic extents at different dates, and we want to avoid querying the
         * database many time for the same thing.
         */
        final Map<List<Comparable<?>>, SortedSet<Number>> altitudesMap =
                new HashMap<List<Comparable<?>>, SortedSet<Number>>();
        /*
         * Now perform in-place the replacements of List<Comparable<?>> by SortedSet<Number>.
         * The replacements are performed directly in entry values.
         */
        for (final Map.Entry<Date,List<Comparable<?>>> entry : centroids.entrySet()) {
            final List<Comparable<?>> extents = entry.getValue();
            SortedSet<Number> altitudes = altitudesMap.get(extents);
            if (altitudes == null) {
                altitudes = new TreeSet<Number>();
                for (final Comparable<?> extent : extents) {
                    final double[] ordinates = getEntry(extent).getVerticalOrdinates();
                    if (ordinates != null) {
                        for (int i=0; i<ordinates.length; i++) {
                            final Number z = ordinates[i];
                            Number shared = sharedNumbers.get(z);
                            if (shared == null) {
                                shared = z;
                                sharedNumbers.put(shared, shared);
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
                final SortedSet<Number> existing = sharedSet.get(altitudes);
                if (existing != null) {
                    altitudes = existing;
                } else {
                    sharedSet.put(altitudes, altitudes);
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
    private static NavigableMap<Date,SortedSet<Number>> unsafe(final NavigableMap centroids) {
        return centroids;
    }

    /**
     * Returns the identifier for the specified grid geometry.
     *
     * @param  size              The image width and height in pixels.
     * @param  gridToCRS         The transform from grid coordinates to "real world" coordinates.
     * @param  horizontalSRID    The "real world" horizontal coordinate reference system.
     * @param  verticalOrdinates The vertical coordinates, or {@code null}.
     * @param  verticalSRID      The "real world" vertical coordinate reference system.
     *                           Ignored if {@code verticalOrdinates} is {@code null}.
     * @return The identifier of a matching entry, or {@code null} if none was found.
     * @throws SQLException If the operation failed.
     */
    Integer find(final Dimension size,
                 final AffineTransform  gridToCRS, final int horizontalSRID,
                 final double[] verticalOrdinates, final int verticalSRID)
            throws SQLException
    {
        ensureNonNull("size",      size);
        ensureNonNull("gridToCRS", gridToCRS);
        Integer id = null;
        final GridGeometryQuery query = (GridGeometryQuery) super.query;
        synchronized (getLock()) {
            final LocalCache.Stmt ce = getStatement(QueryType.LIST);
            final PreparedStatement statement = ce.statement;
            statement.setInt   (indexOf(query.byWidth),          size.width );
            statement.setInt   (indexOf(query.byHeight),         size.height);
            statement.setDouble(indexOf(query.byScaleX),         gridToCRS.getScaleX());
            statement.setDouble(indexOf(query.byShearY),         gridToCRS.getShearY());
            statement.setDouble(indexOf(query.byShearX),         gridToCRS.getShearX());
            statement.setDouble(indexOf(query.byScaleY),         gridToCRS.getScaleY());
            statement.setDouble(indexOf(query.byTranslateX),     gridToCRS.getTranslateX());
            statement.setDouble(indexOf(query.byTranslateY),     gridToCRS.getTranslateY());
            statement.setInt   (indexOf(query.byHorizontalSRID), horizontalSRID);

            boolean foundStrictlyEquals = false;
            final int idIndex = indexOf(query.identifier);
            int vsIndex = indexOf(query.verticalSRID);
            int voIndex = indexOf(query.verticalOrdinates);
            final ResultSet results = statement.executeQuery();
            while (results.next()) {
                final int nextID   = results.getInt(idIndex);
                final int nextSRID = results.getInt(vsIndex);
                /*
                 * We check vertical SRID in Java code rather than in the SQL statement because it is
                 * uneasy to write a statement that works for both non-null and null values (the former
                 * requires "? IS NULL" since the "? = NULL" statement doesn't work with PostgreSQL 8.2.
                 */
                if (results.wasNull() != (verticalOrdinates == null) ||
                    (verticalOrdinates != null && nextSRID != verticalSRID))
                {
                    continue;
                }
                /*
                 * We compare the arrays in this Java code rather than in the SQL statement (in the
                 * WHERE clause) in order to make sure that we are insensitive to the array type
                 * (since we convert to double[] in all cases), and because we need to relax the
                 * tolerance threshold in some cases.
                 */
                final double[] altitudes = asDoubleArray(results.getArray(voIndex));
                final boolean isStrictlyEquals;
                if (Arrays.equals(altitudes, verticalOrdinates)) {
                    isStrictlyEquals = true;
                } else if (equalsAsFloat(altitudes, verticalOrdinates)) {
                    isStrictlyEquals = false;
                } else {
                    continue;
                }
                /*
                 * If there is more than one record with different ID, then there is a choice:
                 *   1) If the new record is more accurate than the previous one, keep the new one.
                 *   2) Otherwise we keep the previous record. A warning will be logged if and only
                 *      if the two records are strictly equals.
                 */
                if (id != null && id.intValue() != nextID) {
                    if (!isStrictlyEquals) {
                        continue;
                    }
                    if (foundStrictlyEquals) {
                        // Could happen if there is insuffisient conditions in the WHERE clause.
                        log("find", errors().getLogRecord(Level.WARNING, Errors.Keys.DUPLICATED_RECORD_$1, id));
                        continue;
                    }
                }
                id = nextID;
                foundStrictlyEquals = isStrictlyEquals;
            }
            results.close();
            ce.release();
        }
        return id;
    }

    /**
     * Returns the identifier for the specified grid geometry. If a suitable entry already
     * exists, its identifier is returned. Otherwise a new entry is created and its identifier
     * is returned.
     *
     * @param  size              The image width and height in pixels.
     * @param  gridToCRS         The transform from grid coordinates to "real world" coordinates.
     * @param  horizontalSRID    The "real world" horizontal coordinate reference system.
     * @param  verticalOrdinates The vertical coordinates, or {@code null}.
     * @param  verticalSRID      The "real world" vertical coordinate reference system.
     *                           Ignored if {@code verticalOrdinates} is {@code null}.
     * @return The identifier of a matching entry.
     * @throws SQLException If the operation failed.
     */
    int findOrCreate(final Dimension size,
                     final AffineTransform  gridToCRS, final int horizontalSRID,
                     final double[] verticalOrdinates, final int verticalSRID)
            throws SQLException
    {
        synchronized (getLock()) {
            boolean success = false;
            transactionBegin();
            try {
                Integer id = find(size, gridToCRS, horizontalSRID, verticalOrdinates, verticalSRID);
                if (id == null) {
                    /*
                     * No match found. Add a new record in the database.
                     */
                    final GridGeometryQuery query = (GridGeometryQuery) super.query;
                    final LocalCache.Stmt ce = getStatement(QueryType.INSERT);
                    final PreparedStatement statement = ce.statement;
                    statement.setInt   (indexOf(query.width),          size.width );
                    statement.setInt   (indexOf(query.height),         size.height);
                    statement.setDouble(indexOf(query.scaleX),         gridToCRS.getScaleX());
                    statement.setDouble(indexOf(query.scaleY),         gridToCRS.getScaleY());
                    statement.setDouble(indexOf(query.translateX),     gridToCRS.getTranslateX());
                    statement.setDouble(indexOf(query.translateY),     gridToCRS.getTranslateY());
                    statement.setDouble(indexOf(query.shearX),         gridToCRS.getShearX());
                    statement.setDouble(indexOf(query.shearY),         gridToCRS.getShearY());
                    statement.setInt   (indexOf(query.horizontalSRID), horizontalSRID);
                    final int vsIndex = indexOf(query.verticalSRID);
                    final int voIndex = indexOf(query.verticalOrdinates);
                    if (verticalOrdinates == null || verticalOrdinates.length == 0) {
                        statement.setNull(vsIndex, Types.INTEGER);
                        statement.setNull(voIndex, Types.ARRAY);
                    } else {
                        statement.setInt(vsIndex, verticalSRID);
                        final Double[] numbers = new Double[verticalOrdinates.length];
                        for (int i=0; i<numbers.length; i++) {
                            numbers[i] = verticalOrdinates[i];
                        }
                        final Array array = statement.getConnection().createArrayOf("float8", numbers);
                        statement.setArray(voIndex, array);
                    }
                    success = updateSingleton(statement);
                    /*
                     * Get the identifier of the entry that we just generated.
                     */
                    final ResultSet keys = statement.getGeneratedKeys();
                    if (keys.next()) {
                        id = keys.getInt(query.identifier.name);
                    }
                    keys.close();
                    ce.release();
                }
                return id;
            } finally {
                transactionEnd(success);
            }
        }
    }
}
