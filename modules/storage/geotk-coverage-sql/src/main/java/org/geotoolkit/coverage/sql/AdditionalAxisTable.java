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

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javax.measure.Unit;
import javax.measure.format.ParserException;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.VerticalDatumType;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.metadata.spatial.DimensionNameType;

import org.apache.sis.measure.Units;
import org.apache.sis.util.iso.Types;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.internal.metadata.VerticalDatumTypes;
import org.apache.sis.internal.metadata.AxisDirections;

// Use static imports for avoiding confusion with SQL Array.
import static java.lang.reflect.Array.getDouble;
import static java.lang.reflect.Array.getLength;


/**
 * Connection to a table of additional axes.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
final class AdditionalAxisTable extends CachedTable<String, AdditionalAxisTable.Entry> {
    /**
     * Information about an additional axes (vertical or other).
     */
    static final class Entry {
        /**
         * The coordinate reference system for this entry.
         */
        final SingleCRS crs;

        /**
         * The transform from grid coordinates to the {@linkplain #crs}.
         */
        final MathTransform1D gridToCRS;

        /**
         * Minimum and maximum values in standard units and direction. For elevation, this is metres toward up.
         * For time axis, this is seconds toward future. For pressure, this is Pascal toward up.
         */
        final double standardMin, standardMax;

        /**
         * Number of values along this axis.
         */
        final int count;

        /**
         * Creates a new entry for an additional axis.
         *
         * @param values  limits of all layers. The array length is the number of layers + 1.
         *                The first and last values are the raster bounds along the axis.
         *                Other values are interstice between layers.
         */
        private Entry(final SingleCRS crs, final double[] values) {
            this.crs  = crs;
            gridToCRS = MathTransforms.interpolate(null, values);       // Integer indices map lower bounds.
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            for (int i=0; i<values.length; i++) {
                final double z = values[i];
                if (z < min) min = z;
                if (z > max) max = z;
            }
            // Transform the (min, max) in "standard" units of the database.
            final CoordinateSystemAxis axis = crs.getCoordinateSystem().getAxis(0);
            double scale = Units.toStandardUnit(axis.getUnit());
            if (AxisDirections.isOpposite(axis.getDirection())) {
                final double t = max;
                max = min;
                min = t;
                scale = -scale;
            }
            min *= scale;
            max *= scale;
            standardMin = min;
            standardMax = max;
            count = values.length - 1;
        }

        /**
         * Returns a standardized identifier for this axis, or {@code null} if none.
         */
        final DimensionNameType type() {
            if (crs instanceof VerticalCRS) {
                return DimensionNameType.VERTICAL;
            } else if (crs instanceof TemporalCRS) {
                return DimensionNameType.TIME;
            } else {
                return null;
            }
        }
    }

    /**
     * Name of this table in the database.
     */
    private static final String TABLE = "AdditionalAxes";

    /**
     * Creates an additional axes table.
     */
    AdditionalAxisTable(final Transaction transaction) {
        super(Target.AXES, transaction);
    }

    /**
     * Returns the SQL {@code SELECT} statement.
     */
    @Override
    String select() {
        return "SELECT \"datum\", \"direction\", \"units\", \"bounds\""
                + " FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE \"name\" = ?";
    }

    /**
     * Creates an axis from the current row in the specified result set.
     *
     * @param  results     the result set to read.
     * @param  identifier  the identifier of the axis to create.
     * @return the entry for current row in the specified result set.
     * @throws SQLException if an error occurred while reading the database.
     */
    @Override
    Entry createEntry(final ResultSet results, final String identifier) throws SQLException, IllegalRecordException {
        final String   datum     = results.getString(1);
        final String   direction = results.getString(2);
        final String   units     = results.getString(3);
        final Array    bounds    = results.getArray(4);
        final Object   data      = bounds.getArray();
        final int      length    = getLength(data);
        final double[] values    = new double[length];
        if (data instanceof Number[]) {
            final Number[] array = (Number[]) data;
            for (int i=0; i<length; i++) {
                values[i] = array[i].doubleValue();
            }
        } else {
            for (int i=0; i<length; i++) {
                values[i] = getDouble(data, i);
            }
        }
        bounds.free();
        if (values.length < 2) {
            throw new IllegalRecordException("Insufficient number of values in \"" + identifier + "\" entry.");
        }
        SingleCRS crs;
        RuntimeException error;
        try {
            crs = crs(datum, Types.forCodeName(AxisDirection.class, direction, false), Units.valueOf(units));
            error = null;
        } catch (ParserException e) {
            crs = null;
            error = e;
        }
        if (crs == null) {
            throw new IllegalRecordException("Unsupported CRS definition in \"" + identifier + "\" entry.", error);
        }
        return new Entry(crs, values);
    }

    /**
     * Creates a datum of the given name.
     *
     * @todo Support more types.
     */
    private static SingleCRS crs(final String datum, final AxisDirection direction, final Unit<?> units) {
        if (AxisDirections.isVertical(direction)) {
            CommonCRS.Vertical code = null;
            final VerticalDatumType type = VerticalDatumTypes.guess(datum, null, null);
            if (VerticalDatumType.GEOIDAL.equals(type) || VerticalDatumType.DEPTH.equals(type)) {
                if (AxisDirection.UP.equals(direction)) {
                    code = CommonCRS.Vertical.MEAN_SEA_LEVEL;
                } else if (AxisDirection.DOWN.equals(direction)) {
                    code = CommonCRS.Vertical.DEPTH;
                }
            } else if (VerticalDatumTypes.ELLIPSOIDAL.equals(type)) {
                code = CommonCRS.Vertical.ELLIPSOIDAL;
            } else if (VerticalDatumType.BAROMETRIC.equals(type)) {
                code = CommonCRS.Vertical.BAROMETRIC;
            }
            if (code != null) {
                final VerticalCRS crs = code.crs();
                final CoordinateSystemAxis axis = crs.getCoordinateSystem().getAxis(0);
                if (direction.equals(axis.getDirection()) && axis.getUnit().equals(units)) {
                    return crs;
                }
            }
        }
        return null;
    }

    /**
     * Returns an identifier for an additional axis having the given data, or inserts a new entry in the database
     * if no suitable identifier is found.
     *
     * @param  crs          coordinate reference system.
     * @param  values       values on the coordinate axis. Must map lower corner.
     * @param  suggestedID  suggested identifier if a new entry must be inserted.
     */
    final String findOrInsert(final SingleCRS crs, final double[] values, final String suggestedID)
            throws SQLException, IllegalUpdateException
    {
        final Double[] wrappers = new Double[values.length];
        for (int i=0; i<values.length; i++) wrappers[i] = values[i];
        final Array bounds = getConnection().createArrayOf("FLOAT8", wrappers);
        String datum = crs.getDatum().getName().getCode();
        if (datum.equalsIgnoreCase("Unknown datum presumably based upon Mean Sea Level")) {
            datum = "Mean Sea Level";
        }
        final CoordinateSystemAxis axis = crs.getCoordinateSystem().getAxis(0);
        final String direction = Types.getCodeName(axis.getDirection());
        final String units     = axis.getUnit().toString();
        boolean insert = false;
        do {
            final PreparedStatement statement;
            if (!insert) {
                statement = prepareStatement("SELECT \"name\" FROM " + SCHEMA + ".\"" + TABLE + "\" WHERE "
                        + "\"datum\"=? AND \"direction\"=CAST(? AS metadata.\"AxisDirection\") AND \"units\"=? AND \"bounds\"=?");
            } else {
                statement = prepareStatement("INSERT INTO " + SCHEMA + ".\"" + TABLE + "\"("
                        + "\"datum\", \"direction\", \"units\", \"bounds\", \"name\")"
                        + " VALUES (?,CAST(? AS metadata.\"AxisDirection\"),?,?,?)");
            }
            statement.setString(1, datum);
            statement.setString(2, direction);
            statement.setString(3, units);
            statement.setArray (4, bounds);
            if (insert) {
                statement.setString(5, suggestedID);
                if (statement.executeUpdate() != 0) {
                    return suggestedID;
                }
            } else try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    final String name = results.getString(1);
                    if (!results.wasNull()) return name;                // Should never be null, but we are paranoiac.
                }
            }
        } while ((insert = !insert) == true);
        throw new IllegalUpdateException("Can not add the series.");    // TODO: provide better error message.
    }
}