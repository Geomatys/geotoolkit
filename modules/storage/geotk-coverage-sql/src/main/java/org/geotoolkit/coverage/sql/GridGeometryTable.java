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

import java.sql.Array;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.apache.sis.internal.referencing.j2d.AffineTransform2D;


/**
 * Connection to a table of grid geometries.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Antoine Hnawia (IRD)
 */
final class GridGeometryTable extends CachedTable<Integer,GridGeometryEntry> {
    /**
     * Name of this table in the database.
     */
    static final String TABLE = "GridGeometries";

    /**
     * Tables of additional axes, created when first needed.
     */
    private AdditionalAxisTable axisTable;

    /**
     * Creates a new {@code GridGeometryTable}.
     */
    GridGeometryTable(final Transaction transaction) {
        super(Target.GRID_GEOMETRY, transaction);
    }

    /**
     * Returns the SQL {@code SELECT} statement.
     */
    @Override
    String select() {
        return "SELECT \"width\", \"height\", \"scaleX\", \"shearY\", \"shearX\", \"scaleY\", \"translateX\", \"translateY\","
                + " \"srid\", \"additionalAxes\""
                + " FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"identifier\" = ?";
    }

    /**
     * Creates a grid geometry from the current row in the specified result set.
     *
     * @param  results     the result set to read.
     * @param  identifier  the identifier of the grid geometry to create.
     * @return the entry for current row in the specified result set.
     * @throws SQLException if an error occurred while reading the database.
     */
    @Override
    @SuppressWarnings("fallthrough")
    GridGeometryEntry createEntry(final ResultSet results, final Integer identifier) throws SQLException, CatalogException {
        final long width  = results.getLong(1);
        final long height = results.getLong(2);
        final AffineTransform2D gridToCRS = new AffineTransform2D(
                results.getDouble(3),
                results.getDouble(4),
                results.getDouble(5),
                results.getDouble(6),
                results.getDouble(7),
                results.getDouble(8));

        final int srid = results.getInt(9);
        final Array refs = results.getArray(10);
        AdditionalAxisTable.Entry[] axes = null;
        String extraDimName = null;
        if (refs != null) {
            final Object data = refs.getArray();
            final int length = java.lang.reflect.Array.getLength(data);
            if (length != 0) {
                if (axisTable == null) {
                    axisTable = new AdditionalAxisTable(transaction);
                }
                axes = new AdditionalAxisTable.Entry[length];
                for (int i=0; i<axes.length; i++) {
                    final String id = (String) java.lang.reflect.Array.get(data, i);
                    extraDimName = (i == 0) ? id : extraDimName + " + " + id;
                    axes[i] = axisTable.getEntry(id);
                }
            }
            refs.free();
        }
        try {
            final CoordinateReferenceSystem crs;
            crs = transaction.database.authorityFactory.createCoordinateReferenceSystem(String.valueOf(srid));
            return new GridGeometryEntry(width, height, gridToCRS, crs, axes, extraDimName, transaction.database);
        } catch (FactoryException | TransformException exception) {
            throw new IllegalRecordException(exception, results, 9, identifier);
        }
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
     * Returns the identifier for the specified grid geometry. If a suitable entry already exists,
     * its identifier is returned. Otherwise a new entry is created and its identifier is returned.
     *
     * @param  gridToCRS         the transform from grid coordinates to "real world" coordinates.
     * @param  horizontalSRID    the "real world" horizontal coordinate reference system.
     * @return the identifier of a matching entry.
     * @throws SQLException if the operation failed.
     */
    int findOrCreate(final long width, final long height, final Matrix gridToCRS,
                     final int srid, final Array axes) throws SQLException, IllegalUpdateException
    {
        boolean insert = false;
        do {
            final PreparedStatement statement;
            if (!insert) {
                statement = prepareStatement("SELECT \"identifier\" FROM " + SCHEMA + ".\"" + TABLE + '"'
                        + " WHERE \"width\" = ? AND \"height\" = ? AND \"scaleX\" = ? AND \"shearY\" = ? AND \"shearX\" = ?"
                        + " AND \"scaleY\" = ? AND \"translateX\" = ? AND \"translateY\" = ? AND \"srid\" = ?"
                        + " AND \"additionalAxes\" IS NOT DISTINCT FROM ?");
                /*
                 * Use "IS NOT DISTINCT FROM" instead of "=" for the field where '?' may be NULL.
                 * This is needed because expression like "A = NULL" always evaluate to 'false'.
                 */
            } else {
                statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\"("
                        + "\"width\", \"height\", \"scaleX\", \"shearY\", \"shearX\", \"scaleY\", "
                        + "\"translateX\", \"translateY\", \"srid\", \"additionalAxes\")"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?)", "identifier");
            }
            final int trc = gridToCRS.getNumCol() - 1;
            statement.setLong  (1, width);
            statement.setLong  (2, height);
            statement.setDouble(3, gridToCRS.getElement(0, 0));       // scaleX
            statement.setDouble(4, gridToCRS.getElement(1, 0));       // shearY
            statement.setDouble(5, gridToCRS.getElement(0, 1));       // shearX
            statement.setDouble(6, gridToCRS.getElement(1, 1));       // scaleY
            statement.setDouble(7, gridToCRS.getElement(0, trc));     // translateX
            statement.setDouble(8, gridToCRS.getElement(1, trc));     // translateY
            statement.setInt   (9, srid);
            if (axes != null) {
                statement.setArray(10, axes);
            } else {
                statement.setNull(10, Types.ARRAY);
            }
            if (insert) {
                if (statement.executeUpdate() == 0) {
                    continue;                                           // Should never happen, but we are paranoiac.
                }
            }
            try (ResultSet results = insert ? statement.getGeneratedKeys() : statement.executeQuery()) {
                while (results.next()) {
                    final int identifier = results.getInt(1);
                    if (!results.wasNull()) return identifier;          // Should never be null, but we are paranoiac.
                }
            }
        } while ((insert = !insert) == true);
        throw new IllegalUpdateException("Can not add the grid geometry.");    // TODO: provide better error message.
    }

    /**
     * Closes the statements used by this table.
     */
    @Override
    public void close() throws SQLException {
        super.close();
        final AdditionalAxisTable t = axisTable;
        if (t != null) {
            axisTable = null;
            t.close();
        }
    }
}
