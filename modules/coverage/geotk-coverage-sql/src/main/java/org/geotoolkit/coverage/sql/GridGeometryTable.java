/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2007-2012, Geomatys
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
import java.sql.SQLNonTransientException;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.logging.Level;

import org.opengis.util.FactoryException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransformFactory;

import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.util.collection.WeakHashSet;
import org.geotoolkit.internal.sql.table.Column;
import org.geotoolkit.internal.sql.table.Database;
import org.geotoolkit.internal.sql.table.QueryType;
import org.geotoolkit.internal.sql.table.LocalCache;
import org.geotoolkit.internal.sql.table.SingletonTable;
import org.geotoolkit.internal.sql.table.IllegalRecordException;
import org.geotoolkit.internal.sql.table.SpatialDatabase;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.factory.AbstractAuthorityFactory;
import org.geotoolkit.referencing.factory.IdentifiedObjectFinder;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.resources.Errors;

import static java.lang.reflect.Array.getLength;
import static java.lang.reflect.Array.getDouble;


/**
 * Connection to a table of grid geometries.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Antoine Hnawia (IRD)
 * @version 3.15
 *
 * @since 3.10 (derived from Seagis)
 * @module
 */
final class GridGeometryTable extends SingletonTable<GridGeometryEntry> {
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
     * Creates a new instance having the same configuration than the given table.
     * This is a copy constructor used for obtaining a new instance to be used
     * concurrently with the original instance.
     *
     * @param table The table to use as a template.
     */
    private GridGeometryTable(final GridGeometryTable table) {
        super(table);
        gridCRS = table.gridCRS;
    }

    /**
     * Returns a copy of this table. This is a copy constructor used for obtaining
     * a new instance to be used concurrently with the original instance.
     */
    @Override
    protected GridGeometryTable clone() {
        return new GridGeometryTable(this);
    }

    /**
     * Returns a CRS identifier for the specified CRS. The given CRS should appears in the PostGIS
     * {@code "spatial_ref_sys"} table. The returned value is a primary key in the same table.
     *
     * @param  crs The CRS to search.
     * @return The identifier for the given CRS, or 0 if none.
     * @throws FactoryException if an error occurred while searching for the CRS.
     */
    public int getSRID(final CoordinateReferenceSystem crs) throws FactoryException {
        final SpatialDatabase database = (SpatialDatabase) getDatabase();
        final AbstractAuthorityFactory factory = (AbstractAuthorityFactory) database.getCRSAuthorityFactory();
        final IdentifiedObjectFinder finder = factory.getIdentifiedObjectFinder(CoordinateReferenceSystem.class);
        final ReferenceIdentifier srid = IdentifiedObjects.getIdentifier(finder.find(crs), Citations.POSTGIS);
        if (srid == null) {
            return 0;
        }
        final String code = srid.getCode();
        try {
            return Integer.parseInt(code);
        } catch (NumberFormatException e) {
            throw new FactoryException(Errors.format(Errors.Keys.UNPARSABLE_NUMBER_1, code), e);
        }
    }

    /**
     * Creates a grid geometry from the current row in the specified result set.
     *
     * @param  results The result set to read.
     * @param  identifier The identifier of the grid geometry to create.
     * @return The entry for current row in the specified result set.
     * @throws SQLException if an error occurred while reading the database.
     */
    @Override
    @SuppressWarnings("fallthrough")
    protected GridGeometryEntry createEntry(final LocalCache lc, final ResultSet results, final Comparable<?> identifier)
            throws SQLException
    {
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
                srsEntry.createSpatioTemporalCRS(database);
            } catch (FactoryException exception) {
                gridCRS.remove(srsEntry);
                final Column column;
                switch (srsEntry.uninitialized()) {
                    case 1:  column = query.horizontalSRID; break;
                    case 2:  column = query.verticalSRID;   break;
                    default: column = query.identifier;     break;
                }
                throw new IllegalRecordException(exception, this, results, indexOf(column), identifier);
            }
        }
        final double[] altitudes = asDoubleArray(verticalOrdinates);
        final MathTransformFactory mtFactory = database.getMathTransformFactory();
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
            throw new IllegalRecordException(errors().getString(Errors.Keys.EMPTY_ENVELOPE_2D), this, results,
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
        ArgumentChecks.ensureNonNull("size",      size);
        ArgumentChecks.ensureNonNull("gridToCRS", gridToCRS);
        Integer id = null;
        final GridGeometryQuery query = (GridGeometryQuery) super.query;
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            final LocalCache.Stmt ce = getStatement(lc, QueryType.LIST);
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
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    final int nextID   = results.getInt(idIndex);
                    final int nextSRID = results.getInt(vsIndex);
                    /*
                     * We check vertical SRID in Java code rather than in the SQL statement because it is
                     * uneasy to write a statement that works for both non-null and null values (the former
                     * requires "? IS NULL" since the "? = NULL" statement doesn't work with PostgreSQL 8.2.
                     */
                    if (results.wasNull() != (verticalOrdinates == null)) {
                        // Inconsistent fields. We will ignore this entry.
                        continue; //
                    }
                    if (verticalOrdinates != null && nextSRID != verticalSRID) {
                        // Not the expected SRID. Search for an other entry.
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
                            // Could happen if there is insufficient conditions in the WHERE clause.
                            log("find", errors().getLogRecord(Level.WARNING, Errors.Keys.DUPLICATED_RECORD_1, id));
                            continue;
                        }
                    }
                    id = nextID;
                    foundStrictlyEquals = isStrictlyEquals;
                }
            }
            release(lc, ce);
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
        ArgumentChecks.ensureStrictlyPositive("horizontalSRID", horizontalSRID);
        Integer id;
        final LocalCache lc = getLocalCache();
        synchronized (lc) {
            boolean success = false;
            transactionBegin(lc);
            try {
                id = find(size, gridToCRS, horizontalSRID, verticalOrdinates, verticalSRID);
                if (id == null) {
                    /*
                     * No match found. Add a new record in the database.
                     */
                    final GridGeometryQuery query = (GridGeometryQuery) super.query;
                    final LocalCache.Stmt ce = getStatement(lc, QueryType.INSERT);
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
                            numbers[i] = Double.valueOf(verticalOrdinates[i]);
                        }
                        final Array array = statement.getConnection().createArrayOf("float8", numbers);
                        statement.setArray(voIndex, array);
                    }
                    success = updateSingleton(statement);
                    /*
                     * Get the identifier of the entry that we just generated.
                     */
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        while (keys.next()) {
                            id = keys.getInt(query.identifier.name);
                            if (!keys.wasNull()) break;
                            id = null; // Should never reach this point, but I'm paranoiac.
                        }
                    }
                    release(lc, ce);
                }
            } finally {
                transactionEnd(lc, success);
            }
        }
        if (id == null) {
            // Should never occur, but I'm paranoiac.
            throw new SQLNonTransientException();
        }
        return id;
    }
}
