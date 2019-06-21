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

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.time.Instant;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.format.ParserException;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.ParametricCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CSFactory;
import org.opengis.referencing.cs.TimeCS;
import org.opengis.referencing.cs.VerticalCS;
import org.opengis.referencing.cs.ParametricCS;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.VerticalDatumType;
import org.opengis.referencing.datum.ParametricDatum;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.referencing.datum.VerticalDatum;
import org.opengis.referencing.operation.MathTransform1D;

import org.apache.sis.measure.Units;
import org.apache.sis.util.iso.Types;
import org.apache.sis.internal.metadata.VerticalDatumTypes;
import org.apache.sis.internal.referencing.AxisDirections;
import org.apache.sis.internal.referencing.ReferencingFactoryContainer;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.cs.DefaultTimeCS;
import org.apache.sis.referencing.cs.DefaultParametricCS;
import org.apache.sis.referencing.cs.DefaultCoordinateSystemAxis;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.referencing.crs.DefaultParametricCRS;
import org.apache.sis.referencing.datum.DefaultTemporalDatum;
import org.apache.sis.referencing.datum.DefaultParametricDatum;

// Use static imports for avoiding confusion with SQL Array.
import static java.lang.reflect.Array.getDouble;
import static java.lang.reflect.Array.getLength;


/**
 * Connection to a table of additional axes.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
final class AdditionalAxisTable extends CachedTable<String,AdditionalAxisEntry> {
    /**
     * Name of this table in the database.
     */
    static final String TABLE = "AdditionalAxes";

    /**
     * Maximum number of additional axes for the same name. Current algorithm is very inefficient
     * for a large number of name collisions, so we are better to keep this limit small.
     */
    private static final int MAX_AXES = 100;

    /**
     * Whether to enable the replacement of some vertical datum.
     * This is a temporary hack while waiting for a better support of vertical transformations.
     */
    private static final boolean REPLACE_DATUM = false;

    /**
     * Name of temporal datum for forecasts.
     */
    private static final String FORECAST_DATUM = "Forecast";

    /**
     * Name of temporal datum for runtime, interpreted as parametric.
     */
    static final String RUNTIME_DATUM = "Runtime";

    /**
     * Datum name for runtime relative to the start time of a {@link GridCoverageEntry}.
     */
    static final String RELATIVE_RUNTIME_DATUM = "Runtime relative to data time";

    /**
     * Datum name for time relative to the start time of a {@link GridCoverageEntry}.
     */
    static final String RELATIVE_TIME_DATUM = "Days since datafile start time";

    /**
     * Contains the epoch of runtime axis. Since runtime are currently represented by {@link ParametricCRS},
     * the origin is not specified. We fix it to the beginning of Julian days for now.
     *
     * @todo a future version should use a second {@link TemporalCRS} instead.
     */
    private static final CommonCRS.Temporal RUNTIME_EPOCH = CommonCRS.Temporal.JULIAN;

    /**
     * Coordinate reference system used to store temporal coordinates relative to datafile start time.
     * The coordinates are in days, with day zero being the start time declared in a {@link GridCoverageEntry}.
     * Axis unit and direction shall be the same than {@link GridGeometryEntry#TEMPORAL_CRS}.
     */
    private static final TemporalCRS RELATIVE_TIME;
    static {
        TimeCS cs = GridGeometryEntry.TEMPORAL_CRS.getCoordinateSystem();
        final CoordinateSystemAxis axis = new DefaultCoordinateSystemAxis(
                properties("Relative time"), "Δt", AxisDirection.FUTURE, cs.getAxis(0).getUnit());
        cs = new DefaultTimeCS(properties(cs.getName()), axis);
        final DefaultTemporalDatum datum = new DefaultTemporalDatum(properties(RELATIVE_TIME_DATUM), new Date(0));
        RELATIVE_TIME = new DefaultTemporalCRS(properties(datum.getName()), datum, cs);
    }

    /**
     * Coordinate reference system used to store temporal coordinates relative to datafile start time.
     * The coordinates are in days, with day zero being the start time declared in a {@link GridCoverageEntry}.
     * Axis unit and direction shall be the same than {@link #RELATIVE_TIME}.
     */
    private static final ParametricCRS RELATIVE_RUNTIME;
    static {
        CoordinateSystem cs = RELATIVE_TIME.getCoordinateSystem();
        CoordinateSystemAxis axis = cs.getAxis(0);
        axis = new DefaultCoordinateSystemAxis(properties(axis.getName()), axis.getAbbreviation(), AxisDirection.PAST, axis.getUnit());
        final DefaultParametricDatum datum = new DefaultParametricDatum(properties(RELATIVE_RUNTIME_DATUM));
        final Map<String,?> name = properties(datum.getName());
        RELATIVE_RUNTIME = new DefaultParametricCRS(name, datum, new DefaultParametricCS(properties(cs.getName()), axis));
    }

    /**
     * Creates an additional axes table.
     */
    AdditionalAxisTable(final Transaction transaction) {
        super(Target.AXES, transaction);
    }

    /**
     * Returns {@code true} if the given axis is a temporal axis whose time coordinates are instants
     * between the start time and end time of a {@link GridCoverageEntry}.
     */
    static boolean isTemporalAxis(final AdditionalAxisEntry axis) {
        return axis.crs == RELATIVE_TIME;
    }

    static AxisDirection getDirection(final CoordinateReferenceSystem crs) {
        final CoordinateSystem cs = crs.getCoordinateSystem();
        return cs.getDimension() == 1 ? cs.getAxis(0).getDirection() : null;
    }

    static boolean isTemporalAxis(final SingleCRS crs, final String expectedDatum) {
        return AxisDirections.isTemporal(getDirection(crs))
                && expectedDatum.equalsIgnoreCase(crs.getDatum().getName().getCode());
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
    AdditionalAxisEntry createEntry(final ResultSet results, final String identifier) throws SQLException, IllegalRecordException {
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
        Exception error;
        try {
            crs = crs(datum, Types.forCodeName(AxisDirection.class, direction, false), Units.valueOf(units), transaction.database);
            error = null;
        } catch (ParserException | FactoryException e) {
            crs = null;
            error = e;
        }
        if (crs == null) {
            throw new IllegalRecordException("Unsupported CRS definition in \"" + identifier + "\" entry.", error);
        }
        return new AdditionalAxisEntry(crs, values);
    }

    /**
     * Creates a coordinate reference system with datum of the given name.
     *
     * @param  name       name of the datum, also used as the coordinate reference system name if a new CRS needs to be created.
     * @param  direction  the axis direction.
     * @param  units      the axis units.
     * @param  factories  group of factories for creating geodetic objects, if needed.
     */
    private static SingleCRS crs(final String name, final AxisDirection direction, final Unit<?> units,
            final ReferencingFactoryContainer factories) throws FactoryException
    {
        /*
         * Vertical dimension (most common case). First, check if a CommonCRS.Vertical constant fits
         * by comparing the datum type, axis units and axis direction. If CommonCRS does not suit,
         * create a new datum.
         */
        if (AxisDirections.isVertical(direction)) {
            VerticalDatumType type = VerticalDatumTypes.guess(name, null, null);
            if (type == null) {
                if (Units.isPressure(units)) {
                    type = VerticalDatumType.BAROMETRIC;
                } else if (Units.isLinear(units)) {
                    if (AxisDirection.UP.equals(direction)) {
                        type = VerticalDatumType.GEOIDAL;
                    } else {
                        type = VerticalDatumType.DEPTH;
                    }
                } else {
                    return null;                // Can not build CRS with unknown vertical datum type.
                }
            }
            CommonCRS.Vertical candidate = null;
            if (VerticalDatumType.GEOIDAL.equals(type) || VerticalDatumType.DEPTH.equals(type)) {
                if (AxisDirection.UP.equals(direction)) {
                    candidate = CommonCRS.Vertical.MEAN_SEA_LEVEL;
                } else if (AxisDirection.DOWN.equals(direction)) {
                    candidate = CommonCRS.Vertical.DEPTH;
                }
            } else if (VerticalDatumTypes.ELLIPSOIDAL.equals(type)) {
                candidate = CommonCRS.Vertical.ELLIPSOIDAL;
            } else if (VerticalDatumType.BAROMETRIC.equals(type)) {
                candidate = CommonCRS.Vertical.BAROMETRIC;
            }
            final VerticalDatum datum;
            final String abbreviation;
            final Object axisName;
            if (candidate != null) {
                final VerticalCRS crs = candidate.crs();
                final CoordinateSystemAxis axis = crs.getCoordinateSystem().getAxis(0);
                if (axis.getDirection().equals(direction) && axis.getUnit().equals(units)) {
                    return crs;
                }
                /*
                 * If an item from the CommonCRS.Vertical enumeration corresponds to the given axis units
                 * and direction, it has been returned above.  Otherwise a new CRS will be created below,
                 * reusing some components from the CommonCRS.Vertical if possible.
                 */
                abbreviation = axis.getAbbreviation();
                axisName = axis.getName();
                datum = crs.getDatum();
            } else {
                abbreviation = "h";
                axisName = "Vertical";                            // This fallback should be rarely used.
                datum = factories.getDatumFactory().createVerticalDatum(properties(name), type);
            }
            final CSFactory csFactory = factories.getCSFactory();
            final CoordinateSystemAxis axis = csFactory.createCoordinateSystemAxis(properties(axisName), abbreviation, direction, units);
            final VerticalCS cs = csFactory.createVerticalCS(properties(axis.getName()), axis);
            return factories.getCRSFactory().createVerticalCRS(properties(name), datum, cs);
        }
        /*
         * Temporal CRS, excluding forecast time since they would appear as a second time axis. If an item
         * from the CommonCRS.Temporal enumeration corresponds to the given axis units and direction, that
         * item CRS will be returned.  Otherwise we will create a new CRS, using the best enumeration item
         * as a template if possible.
         */
        final boolean isTemporal = AxisDirections.isTemporal(direction);
        if (isTemporal && !(Entry.HACK && FORECAST_DATUM.equalsIgnoreCase(name))
                       && !(Entry.HACK && RUNTIME_DATUM.equalsIgnoreCase(name)))
        {
            if (RELATIVE_TIME_DATUM.equalsIgnoreCase(name)) {
                return RELATIVE_TIME;
            }
            if (RELATIVE_RUNTIME_DATUM.equalsIgnoreCase(name)) {
                return RELATIVE_RUNTIME;
            }
            TimeCS cs = null;
            TemporalDatum datum = null;
            for (final CommonCRS.Temporal candidate : CommonCRS.Temporal.values()) {
                if (IdentifiedObjects.isHeuristicMatchForName(candidate.datum(), name)) {
                    final TemporalCRS crs = candidate.crs();
                    final CoordinateSystemAxis axis = crs.getCoordinateSystem().getAxis(0);
                    if (axis.getDirection().equals(direction) && axis.getUnit().equals(units)) {
                        return crs;
                    }
                    cs = crs.getCoordinateSystem();     // To be used as a pattern for creating a new CS below.
                    datum = crs.getDatum();
                }
            }
            if (datum == null) {
                datum = factories.getDatumFactory().createTemporalDatum(properties(name), new Date(0));
                // TODO: actually we should throw an exception instead (we don't know time origin).
            }
            final CSFactory csFactory = factories.getCSFactory();
            if (cs == null) {
                cs = CommonCRS.Temporal.JULIAN.crs().getCoordinateSystem();
                // TODO: actually we should throw an exception instead (we don't know time unit).
            }
            CoordinateSystemAxis axis = cs.getAxis(0);
            axis = csFactory.createCoordinateSystemAxis(properties(axis.getName()), axis.getAbbreviation(), direction, units);
            cs = csFactory.createTimeCS(properties(cs.getName()), axis);
            return factories.getCRSFactory().createTemporalCRS(properties(name), datum, cs);
        }
        /*
         * Temporal CRS for forecast time, or any other CRS. We handle everything which is not vertical
         * or temporal as parametric. There is no enumeration of pre-defined values for those CRS.
         */
        final ParametricDatum datum = factories.getDatumFactory().createParametricDatum(properties(name));
        final CSFactory csFactory = factories.getCSFactory();
        final String axisName, abbreviation;
        if (isTemporal) {
            axisName = RUNTIME_DATUM;
            abbreviation = "rt";
        } else {
            axisName = "Parametric";
            abbreviation = "p";
        }
        final CoordinateSystemAxis axis = csFactory.createCoordinateSystemAxis(properties(axisName), abbreviation, direction, units);
        final ParametricCS cs = csFactory.createParametricCS(properties(axis.getName()), axis);
        return factories.getCRSFactory().createParametricCRS(properties(datum.getName()), datum, cs);
    }

    /**
     * Returns a singleton map with the given object as its {@code "name"} property.
     * This helper method is used for geodetic object construction.
     */
    static Map<String,?> properties(final Object name) {
        return Collections.singletonMap(SingleCRS.NAME_KEY, name);
    }

    /**
     * Returns the unit of a CRS presumed to have only one axis (this is not verified).
     */
    static Unit<?> getUnit(final SingleCRS crs) {
        return crs.getCoordinateSystem().getAxis(0).getUnit();
    }

    /**
     * Returns an identifier for an additional axis having the given data, or inserts a new entry in the database
     * if no suitable identifier is found.
     *
     * @param  suggestedID  suggested identifier if a new entry must be inserted.
     * @param  lowerValue   the first grid coordinate values of the grid dimension to add.
     * @param  numValues    number of values in the grid dimension to add.
     * @param  gridToCRS    conversion from grid coordinates to "real world" coordinates, mapping cell corners.
     * @param  crs          coordinate reference system after conversion from grid coordinates.
     * @param  startTime    start time of the grid coverage for which to create relative time values, or {@code null}.
     * @return actual name of the additional axis.
     */
    final String findOrInsert(String suggestedID, final long lowerValue, final int numValues,
            final MathTransform1D gridToCRS, SingleCRS crs, final Instant startTime) throws Exception
    {
        final UnitConverter toRelativeTime;
        if (startTime != null && AxisDirections.isTemporal(getDirection(crs))) {
            final Unit<?> unit = getUnit(crs);
            final TemporalCRS timeCRS;
            if (crs instanceof TemporalCRS) {
                timeCRS = (TemporalCRS) crs;
                crs = RELATIVE_TIME;
            } else {
                timeCRS = RUNTIME_EPOCH.crs();
                crs = RELATIVE_RUNTIME;
            }
            int sign = AxisDirection.PAST.equals(getDirection(timeCRS)) ? -1 : +1;
            double offset = ((DefaultTemporalCRS) timeCRS).toValue(startTime);          // In unit of temporal CRS.
            offset = getUnit(timeCRS).getConverterToAny(unit).convert(offset);          // In unit of specified CRS.
            UnitConverter step1 = Units.converter(sign, -sign * offset);
            UnitConverter step2 = unit.getConverterToAny(getUnit(crs));                 // To unit of relative CRS.
            toRelativeTime = step2.concatenate(step1);
        } else {
            toRelativeTime = null;
        }
        final Double[] values = new Double[Math.incrementExact(numValues)];
        for (int j=0; j<values.length; j++) {
            double value = gridToCRS.transform(lowerValue + j);
            if (toRelativeTime != null) {
                value = toRelativeTime.convert(value);
            }
            values[j] = value;
        }
        final Array bounds = getConnection().createArrayOf("FLOAT8", values);
        String datum = crs.getDatum().getName().getCode();
        if (REPLACE_DATUM && datum.equalsIgnoreCase("Unknown datum presumably based upon Mean Sea Level")) {
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
                        + " VALUES (?,CAST(? AS metadata.\"AxisDirection\"),?,?,?) ON CONFLICT (\"name\") DO NOTHING");
            }
            statement.setString(1, datum);
            statement.setString(2, direction);
            statement.setString(3, units);
            statement.setArray (4, bounds);
            if (insert) {
                /*
                 * Attempt to insert a new record may cause a name collision.
                 * Following algorithm is inefficient, but should be okay if
                 * there is few additional axes for the same product.
                 */
                StringBuilder buffer = null;
                for (int n=2; ; n++) {
                    statement.setString(5, suggestedID);
                    if (statement.executeUpdate() != 0) {
                        return suggestedID;
                    }
                    if (n >= MAX_AXES) {
                        throw new CatalogException("Axes already exist for all names up to \"" + suggestedID + "\".");
                    }
                    if (buffer == null) {
                        buffer = new StringBuilder(suggestedID).append('-');
                    }
                    final int s = buffer.length();
                    suggestedID = buffer.append(n).toString();
                    buffer.setLength(s);
                }
            } else try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    final String name = results.getString(1);
                    if (!results.wasNull()) return name;                // Should never be null, but we are paranoiac.
                }
            }
        } while ((insert = !insert) == true);
        throw new CatalogException();                                   // Should never reach this point.
    }
}
