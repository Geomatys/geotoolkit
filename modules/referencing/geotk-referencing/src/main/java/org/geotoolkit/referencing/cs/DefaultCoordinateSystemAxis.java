/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.cs;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.measure.converter.UnitConverter;
import javax.measure.converter.ConversionException;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.xml.bind.annotation.XmlTransient;

import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.cs.RangeMeaning;
import org.opengis.util.InternationalString;

import org.apache.sis.measure.Angle;
import org.apache.sis.io.wkt.Formatter;
import org.apache.sis.referencing.cs.CoordinateSystems;
import org.apache.sis.referencing.AbstractIdentifiedObject;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.resources.Vocabulary;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static java.util.Collections.singletonMap;


/**
 * Definition of a coordinate system axis. This is used to label axes, and indicate the orientation.
 * See {@linkplain org.opengis.referencing.cs#AxisNames axis name constraints}.
 * <p>
 * In some case, the axis name is constrained by ISO 19111 depending on the
 * {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system}
 * type. These constraints are identified in the javadoc by "<cite>ISO 19111 name is...</cite>"
 * sentences. This constraint works in two directions; for example the names
 * "<cite>geodetic latitude</cite>" and "<cite>geodetic longitude</cite>" shall be used to
 * designate the coordinate axis names associated with a
 * {@linkplain org.opengis.referencing.crs.GeographicCRS geographic coordinate reference system}.
 * Conversely, these names shall not be used in any other context.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @see AbstractCS
 * @see Unit
 *
 * @since 2.0
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
@XmlTransient
public class DefaultCoordinateSystemAxis extends org.apache.sis.referencing.cs.DefaultCoordinateSystemAxis {
    /**
     * Number of directions from "North", "North-North-East", "North-East", etc.
     * This is verified by {@code DefaultCoordinateSystemAxisTest.testCompass}.
     */
    static final int COMPASS_DIRECTION_COUNT = 16;

    /**
     * Number of items in {@link #PREDEFINED}. Should be considered
     * as a constant after the class initialization is completed.
     */
    private static int PREDEFINED_COUNT = 0;

    /**
     * The list of predefined constants declared in this class,
     * in declaration order. This order matter.
     */
    private static final DefaultCoordinateSystemAxis[] PREDEFINED = new DefaultCoordinateSystemAxis[27];

    /**
     * Default axis info for geodetic longitudes in a
     * {@linkplain org.opengis.referencing.crs.GeographicCRS geographic CRS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#EAST East}
     * and units are {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
     *
     * The ISO 19111 name is "<cite>geodetic longitude</cite>" and the abbreviation is "&lambda;"
     * (lambda).
     * <p>
     * This axis is usually part of a {@link #GEODETIC_LONGITUDE}, {@link #GEODETIC_LATITUDE},
     * {@link #ELLIPSOIDAL_HEIGHT} set.
     *
     * @see #LONGITUDE
     * @see #SPHERICAL_LONGITUDE
     * @see #GEODETIC_LATITUDE
     */
    public static final DefaultCoordinateSystemAxis GEODETIC_LONGITUDE = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.GEODETIC_LONGITUDE, "\u03BB", AxisDirection.EAST, NonSI.DEGREE_ANGLE);

    /**
     * Default axis info for geodetic latitudes in a
     * {@linkplain org.opengis.referencing.crs.GeographicCRS geographic CRS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#NORTH North}
     * and units are {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
     *
     * The ISO 19111 name is "<cite>geodetic latitude</cite>" and the abbreviation is "&phi;" (phi).
     * <p>
     * This axis is usually part of a {@link #GEODETIC_LONGITUDE}, {@link #GEODETIC_LATITUDE},
     * {@link #ELLIPSOIDAL_HEIGHT} set.
     *
     * @see #LATITUDE
     * @see #SPHERICAL_LATITUDE
     * @see #GEODETIC_LONGITUDE
     */
    public static final DefaultCoordinateSystemAxis GEODETIC_LATITUDE = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.GEODETIC_LATITUDE, "\u03C6", AxisDirection.NORTH, NonSI.DEGREE_ANGLE);

    /**
     * Default axis info for longitudes.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#EAST East}
     * and units are {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
     *
     * The abbreviation is "&lambda;" (lambda).
     * <p>
     * This axis is usually part of a {@link #LONGITUDE}, {@link #LATITUDE}, {@link #ALTITUDE} set.
     *
     * @see #GEODETIC_LONGITUDE
     * @see #SPHERICAL_LONGITUDE
     * @see #LATITUDE
     */
    public static final DefaultCoordinateSystemAxis LONGITUDE = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.LONGITUDE, "\u03BB", AxisDirection.EAST, NonSI.DEGREE_ANGLE);

    /**
     * Default axis info for latitudes.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#NORTH North}
     * and units are {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
     *
     * The abbreviation is "&phi;" (phi).
     * <p>
     * This axis is usually part of a {@link #LONGITUDE}, {@link #LATITUDE}, {@link #ALTITUDE} set.
     *
     * @see #GEODETIC_LATITUDE
     * @see #SPHERICAL_LATITUDE
     * @see #LONGITUDE
     */
    public static final DefaultCoordinateSystemAxis LATITUDE = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.LATITUDE, "\u03C6", AxisDirection.NORTH, NonSI.DEGREE_ANGLE);

    /**
     * The default axis for height values above the ellipsoid in a
     * {@linkplain org.opengis.referencing.crs.GeographicCRS geographic CRS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#UP up}
     * and units are {@linkplain SI#METRE metres}.
     *
     * The ISO 19111 name is "<cite>ellipsoidal heigt</cite>" and the abbreviation is lower case
     * "<var>h</var>".
     * <p>
     * This axis is usually part of a {@link #GEODETIC_LONGITUDE}, {@link #GEODETIC_LATITUDE},
     * {@link #ELLIPSOIDAL_HEIGHT} set.
     *
     * @see #ALTITUDE
     * @see #GEOCENTRIC_RADIUS
     * @see #GRAVITY_RELATED_HEIGHT
     * @see #DEPTH
     */
    public static final DefaultCoordinateSystemAxis ELLIPSOIDAL_HEIGHT = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.ELLIPSOIDAL_HEIGHT, "h", AxisDirection.UP, SI.METRE);

    /**
     * The default axis for height values measured from gravity.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#UP up}
     * and units are {@linkplain SI#METRE metres}.
     *
     * The ISO 19111 name is "<cite>gravity-related height</cite>" and the abbreviation is lower
     * case "<var>H</var>".
     *
     * @see #ALTITUDE
     * @see #ELLIPSOIDAL_HEIGHT
     * @see #GEOCENTRIC_RADIUS
     * @see #DEPTH
     */
    public static final DefaultCoordinateSystemAxis GRAVITY_RELATED_HEIGHT = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.GRAVITY_RELATED_HEIGHT, "H", AxisDirection.UP, SI.METRE);

    /**
     * The default axis for altitude values.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#UP up}
     * and units are {@linkplain SI#METRE metres}.
     *
     * The abbreviation is lower case "<var>h</var>".
     * <p>
     * This axis is usually part of a {@link #LONGITUDE}, {@link #LATITUDE}, {@link #ALTITUDE} set.
     *
     * @see #ELLIPSOIDAL_HEIGHT
     * @see #GEOCENTRIC_RADIUS
     * @see #GRAVITY_RELATED_HEIGHT
     * @see #DEPTH
     */
    public static final DefaultCoordinateSystemAxis ALTITUDE = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.ALTITUDE, "h", AxisDirection.UP, SI.METRE);

    /**
     * The default axis for depth.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#DOWN down}
     * and units are {@linkplain SI#METRE metres}.
     *
     * The ISO 19111 name is "<cite>depth</cite>".
     *
     * @see #ALTITUDE
     * @see #ELLIPSOIDAL_HEIGHT
     * @see #GEOCENTRIC_RADIUS
     * @see #GRAVITY_RELATED_HEIGHT
     */
    public static final DefaultCoordinateSystemAxis DEPTH = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.DEPTH, "d", AxisDirection.DOWN, SI.METRE);
    static {
        ALTITUDE.opposite = DEPTH;
        DEPTH.opposite = ALTITUDE;
    }

    /**
     * Default axis info for radius in a
     * {@linkplain org.opengis.referencing.crs.GeocentricCRS geocentric CRS} using
     * {@linkplain org.opengis.referencing.cs.SphericalCS spherical CS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#UP up}
     * and units are {@linkplain SI#METRE metres}.
     *
     * The ISO 19111 name is "<cite>geocentric radius</cite>" and the abbreviation is lower case
     * "<var>r</var>".
     * <p>
     * This axis is usually part of a {@link #SPHERICAL_LONGITUDE}, {@link #SPHERICAL_LATITUDE},
     * {@link #GEOCENTRIC_RADIUS} set.
     *
     * @see #ALTITUDE
     * @see #ELLIPSOIDAL_HEIGHT
     * @see #GRAVITY_RELATED_HEIGHT
     * @see #DEPTH
     */
    public static final DefaultCoordinateSystemAxis GEOCENTRIC_RADIUS = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.GEOCENTRIC_RADIUS, "r", AxisDirection.UP, SI.METRE);

    /**
     * Default axis info for longitudes in a
     * {@linkplain org.opengis.referencing.crs.GeocentricCRS geocentric CRS} using
     * {@linkplain org.opengis.referencing.crs.SphericalCS spherical CS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#EAST East}
     * and units are {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
     *
     * The ISO 19111 name is "<cite>spherical longitude</cite>" and the abbreviation is "&Omega;"
     * (omega).
     * <p>
     * This axis is usually part of a {@link #SPHERICAL_LONGITUDE}, {@link #SPHERICAL_LATITUDE},
     * {@link #GEOCENTRIC_RADIUS} set.
     *
     * @see #LONGITUDE
     * @see #GEODETIC_LONGITUDE
     * @see #SPHERICAL_LATITUDE
     */
    public static final DefaultCoordinateSystemAxis SPHERICAL_LONGITUDE = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.SPHERICAL_LONGITUDE, "\u03A9", AxisDirection.EAST, NonSI.DEGREE_ANGLE);

    /**
     * Default axis info for latitudes in a
     * {@linkplain org.opengis.referencing.crs.GeocentricCRS geocentric CRS} using
     * {@linkplain org.opengis.referencing.cs.SphericalCS spherical CS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#NORTH North}
     * and units are {@linkplain NonSI#DEGREE_ANGLE decimal degrees}.
     *
     * The ISO 19111 name is "<cite>spherical latitude</cite>" and the abbreviation is "&Theta;"
     * (theta).
     * <p>
     * This axis is usually part of a {@link #SPHERICAL_LONGITUDE}, {@link #SPHERICAL_LATITUDE},
     * {@link #GEOCENTRIC_RADIUS} set.
     *
     * @see #LATITUDE
     * @see #GEODETIC_LATITUDE
     * @see #SPHERICAL_LONGITUDE
     */
    public static final DefaultCoordinateSystemAxis SPHERICAL_LATITUDE = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.SPHERICAL_LATITUDE, "\u03B8", AxisDirection.NORTH, NonSI.DEGREE_ANGLE);

    /**
     * Default axis info for <var>x</var> values in a
     * {@linkplain org.opengis.referencing.cs.CartesianCS Cartesian CS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#EAST East}
     * and units are {@linkplain SI#METRE metres}.
     *
     * The abbreviation is lower case "<var>x</var>".
     * <p>
     * This axis is usually part of a {@link #X}, {@link #Y}, {@link #Z} set.
     *
     * @see #EASTING
     * @see #WESTING
     * @see #GEOCENTRIC_X
     * @see #DISPLAY_X
     * @see #COLUMN
     */
    public static final DefaultCoordinateSystemAxis X = new DefaultCoordinateSystemAxis(
            -1, "x", AxisDirection.EAST, SI.METRE);

    /**
     * Default axis info for <var>y</var> values in a
     * {@linkplain org.opengis.referencing.cs.CartesianCS Cartesian CS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#NORTH North}
     * and units are {@linkplain SI#METRE metres}.
     *
     * The abbreviation is lower case "<var>y</var>".
     * <p>
     * This axis is usually part of a {@link #X}, {@link #Y}, {@link #Z} set.
     *
     * @see #NORTHING
     * @see #SOUTHING
     * @see #GEOCENTRIC_Y
     * @see #DISPLAY_Y
     * @see #ROW
     */
    public static final DefaultCoordinateSystemAxis Y = new DefaultCoordinateSystemAxis(
            -1, "y", AxisDirection.NORTH, SI.METRE);

    /**
     * Default axis info for <var>z</var> values in a
     * {@linkplain org.opengis.referencing.cs.CartesianCS Cartesian CS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#UP up}
     * and units are {@linkplain SI#METRE metres}.
     *
     * The abbreviation is lower case "<var>z</var>".
     * <p>
     * This axis is usually part of a {@link #X}, {@link #Y}, {@link #Z} set.
     */
    public static final DefaultCoordinateSystemAxis Z = new DefaultCoordinateSystemAxis(
            -1, "z", AxisDirection.UP, SI.METRE);

    /**
     * Default axis info for <var>x</var> values in a
     * {@linkplain org.opengis.referencing.crs.GeocentricCRS geocentric CRS} using
     * {@linkplain org.opengis.referencing.cs.CartesianCS Cartesian CS}.
     *
     * Increasing ordinates values goes typically toward prime meridian, but the actual axis
     * direction is {@link AxisDirection#GEOCENTRIC_X GEOCENTRIC_X}. In legacy OGC 01-009
     * specification (still in use for WKT format), it was {@link AxisDirection#OTHER OTHER}).
     * <p>
     * The units are {@linkplain SI#METRE metres}.
     * The ISO 19111 name is "<cite>geocentric X</cite>" and the abbreviation is upper case
     * "<var>X</var>".
     * <p>
     * This axis is usually part of a {@link #GEOCENTRIC_X}, {@link #GEOCENTRIC_Y},
     * {@link #GEOCENTRIC_Z} set.
     */
    public static final DefaultCoordinateSystemAxis GEOCENTRIC_X = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.GEOCENTRIC_X, "X", AxisDirection.GEOCENTRIC_X, SI.METRE);

    /**
     * Default axis info for <var>y</var> values in a
     * {@linkplain org.opengis.referencing.crs.GeocentricCRS geocentric CRS} using
     * {@linkplain org.opengis.referencing.cs.CartesianCS Cartesian CS}.
     *
     * Increasing ordinates values goes typically toward East, but the actual axis direction
     * is {@link AxisDirection#GEOCENTRIC_Y GEOCENTRIC_Y}. In legacy OGC 01-009 specification
     * (still in use for WKT format), it was {@link AxisDirection#EAST EAST}).
     * <p>
     * The units are {@linkplain SI#METRE metres}.
     * The ISO 19111 name is "<cite>geocentric Y</cite>" and the abbreviation is upper case
     * "<var>Y</var>".
     * <p>
     * This axis is usually part of a {@link #GEOCENTRIC_X}, {@link #GEOCENTRIC_Y},
     * {@link #GEOCENTRIC_Z} set.
     */
    public static final DefaultCoordinateSystemAxis GEOCENTRIC_Y = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.GEOCENTRIC_Y, "Y", AxisDirection.GEOCENTRIC_Y, SI.METRE);

    /**
     * Default axis info for <var>z</var> values in a
     * {@linkplain org.opengis.referencing.crs.GeocentricCRS geocentric CRS} using
     * {@linkplain org.opengis.referencing.cs.CartesianCS Cartesian CS}.
     *
     * Increasing ordinates values goes typically toward North, but the actual axis direction
     * is {@link AxisDirection#GEOCENTRIC_Z GEOCENTRIC_Z}. In legacy OGC 01-009 specification
     * (still in use for WKT format), it was {@link AxisDirection#NORTH NORTH}).
     * <p>
     * The units are {@linkplain SI#METRE metres}.
     * The ISO 19111 name is "<cite>geocentric Z</cite>" and the abbreviation is upper case
     * "<var>Z</var>".
     * <p>
     * This axis is usually part of a {@link #GEOCENTRIC_X}, {@link #GEOCENTRIC_Y},
     * {@link #GEOCENTRIC_Z} set.
     */
    public static final DefaultCoordinateSystemAxis GEOCENTRIC_Z = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.GEOCENTRIC_Z, "Z", AxisDirection.GEOCENTRIC_Z, SI.METRE);

    /**
     * Default axis info for Easting values in a
     * {@linkplain org.opengis.referencing.crs.ProjectedCRS projected CRS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#EAST East}
     * and units are {@linkplain SI#METRE metres}.
     *
     * The ISO 19111 name is "<cite>easting</cite>" and the abbreviation is upper case
     * "<var>E</var>".
     * <p>
     * This axis is usually part of a {@link #EASTING}, {@link #NORTHING} set.
     *
     * @see #X
     * @see #EASTING
     * @see #WESTING
     */
    public static final DefaultCoordinateSystemAxis EASTING = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.EASTING, "E", AxisDirection.EAST, SI.METRE);

    /**
     * Default axis info for Westing values in a
     * {@linkplain org.opengis.referencing.crs.ProjectedCRS projected CRS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#WEST West}
     * and units are {@linkplain SI#METRE metres}.
     *
     * The ISO 19111 name is "<cite>westing</cite>" and the abbreviation is upper case
     * "<var>W</var>".
     *
     * @see #X
     * @see #EASTING
     * @see #WESTING
     */
    public static final DefaultCoordinateSystemAxis WESTING = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.WESTING, "W", AxisDirection.WEST, SI.METRE);
    static {
        EASTING.opposite = WESTING;
        WESTING.opposite = EASTING;
    }

    /**
     * Default axis info for Northing values in a
     * {@linkplain org.opengis.referencing.crs.ProjectedCRS projected CRS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#NORTH North}
     * and units are {@linkplain SI#METRE metres}.
     *
     * The ISO 19111 name is "<cite>northing</cite>" and the abbreviation is upper case
     * "<var>N</var>".
     * <p>
     * This axis is usually part of a {@link #EASTING}, {@link #NORTHING} set.
     *
     * @see #Y
     * @see #NORTHING
     * @see #SOUTHING
     */
    public static final DefaultCoordinateSystemAxis NORTHING = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.NORTHING, "N", AxisDirection.NORTH, SI.METRE);

    /**
     * Default axis info for Southing values in a
     * {@linkplain org.opengis.referencing.crs.ProjectedCRS projected CRS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#SOUTH South}
     * and units are {@linkplain SI#METRE metres}.
     *
     * The ISO 19111 name is "<cite>southing</cite>" and the abbreviation is upper case
     * "<var>S</var>".
     *
     * @see #Y
     * @see #NORTHING
     * @see #SOUTHING
     */
    public static final DefaultCoordinateSystemAxis SOUTHING = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.SOUTHING, "S", AxisDirection.SOUTH, SI.METRE);
    static {
        NORTHING.opposite = SOUTHING;
        SOUTHING.opposite = NORTHING;
    }

    /**
     * A default axis for time values in a {@linkplain org.opengis.referencing.cs.TimeCS time CS}.
     *
     * Increasing time go toward {@linkplain AxisDirection#FUTURE future}
     * and units are {@linkplain NonSI#DAY days}.
     *
     * The abbreviation is lower case "<var>t</var>".
     */
    public static final DefaultCoordinateSystemAxis TIME = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.TIME, "t", AxisDirection.FUTURE, NonSI.DAY);

    /**
     * A default axis for column indices in a {@linkplain org.opengis.coverage.grid.GridCoverage
     * grid coverage}. Increasing values go toward {@linkplain AxisDirection#COLUMN_POSITIVE
     * positive column number}.
     *
     * The abbreviation is lower case "<var>i</var>".
     */
    public static final DefaultCoordinateSystemAxis COLUMN = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.COLUMN, "i", AxisDirection.COLUMN_POSITIVE, Unit.ONE);

    /**
     * A default axis for row indices in a {@linkplain org.opengis.coverage.grid.GridCoverage grid
     * coverage}. Increasing values go toward {@linkplain AxisDirection#ROW_POSITIVE positive row
     * number}.
     *
     * The abbreviation is lower case "<var>j</var>".
     */
    public static final DefaultCoordinateSystemAxis ROW = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.ROW, "j", AxisDirection.ROW_POSITIVE, Unit.ONE);

    /**
     * A default axis for <var>x</var> values in a display device. Increasing values go toward
     * {@linkplain AxisDirection#DISPLAY_RIGHT display right}.
     *
     * The abbreviation is lower case "<var>x</var>".
     *
     * @since 2.2
     */
    public static final DefaultCoordinateSystemAxis DISPLAY_X = new DefaultCoordinateSystemAxis(
            -1, "x", AxisDirection.DISPLAY_RIGHT, Unit.ONE);

    /**
     * A default axis for <var>y</var> values in a display device. Increasing values go toward
     * {@linkplain AxisDirection#DISPLAY_DOWN display down}.
     *
     * The abbreviation is lower case "<var>y</var>".
     *
     * @since 2.2
     */
    public static final DefaultCoordinateSystemAxis DISPLAY_Y = new DefaultCoordinateSystemAxis(
            -1, "y", AxisDirection.DISPLAY_DOWN, Unit.ONE);

    /**
     * Undefined or unknown axis. Axis direction is {@link AxisDirection#OTHER OTHER}
     * and the unit is dimensionless. This constant is sometime used as a placeholder
     * for axes that were not properly defined.
     *
     * @since 3.00
     */
    public static final DefaultCoordinateSystemAxis UNDEFINED = new DefaultCoordinateSystemAxis(
            Vocabulary.Keys.UNDEFINED, "?", AxisDirection.OTHER, Unit.ONE);

    /**
     * The axis with opposite direction, or {@code null} if unknown.
     * Not serialized because only used for the predefined constants.
     */
    private transient DefaultCoordinateSystemAxis opposite;

    /**
     * Constructs a new coordinate system axis with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param axis The coordinate system axis to copy.
     *
     * @since 2.2
     */
    public DefaultCoordinateSystemAxis(final CoordinateSystemAxis axis) {
        super(axis);
    }

    /**
     * Constructs an axis from a set of properties. The properties map is given unchanged to the
     * {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map) super-class constructor}.
     *
     * @param properties   Set of properties. Should contains at least {@code "name"}.
     * @param abbreviation The {@linkplain #getAbbreviation abbreviation} used for this
     *                     coordinate system axes.
     * @param direction    The {@linkplain #getDirection direction} of this coordinate system axis.
     * @param unit         The {@linkplain #getUnit unit of measure} used for this coordinate
     *                     system axis.
     * @param minimum      The minimum value normally allowed for this axis.
     * @param maximum      The maximum value normally allowed for this axis.
     * @param rangeMeaning The meaning of axis value range specified by the minimum and
     *                     maximum values.
     *
     * @since 2.3
     */
    public DefaultCoordinateSystemAxis(final Map<String,?> properties,
                                       final String        abbreviation,
                                       final AxisDirection direction,
                                       final Unit<?>       unit,
                                       final double        minimum,
                                       final double        maximum,
                                       final RangeMeaning  rangeMeaning)
    {
        super(complete(properties, minimum, maximum, rangeMeaning), abbreviation, direction, unit);
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static Map<String,?> complete(Map<String,?> properties,
            final double minimum, final double maximum, final RangeMeaning reangeMeaning)
    {
        final Map<String,Object> copy = new HashMap<>(properties);
        copy.put(RANGE_MEANING_KEY, reangeMeaning);
        copy.put(MINIMUM_VALUE_KEY, minimum);
        copy.put(MAXIMUM_VALUE_KEY, maximum);
        return copy;
    }

    /**
     * Constructs an unbounded axis from a set of properties. The properties map is given
     * unchanged to the {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map)
     * super-class constructor}. The {@linkplain #getMinimumValue minimum} and
     * {@linkplain #getMaximumValue maximum} values are inferred from the axis unit and
     * direction.
     *
     * @param properties   Set of properties. Should contains at least {@code "name"}.
     * @param abbreviation The {@linkplain #getAbbreviation abbreviation} used for this
     *                     coordinate system axes.
     * @param direction    The {@linkplain #getDirection direction} of this coordinate system axis.
     * @param unit         The {@linkplain #getUnit unit of measure} used for this coordinate
     *                     system axis.
     */
    public DefaultCoordinateSystemAxis(final Map<String,?> properties,
                                       final String        abbreviation,
                                       final AxisDirection direction,
                                       final Unit<?>       unit)
    {
        super(properties, abbreviation, direction, unit);
    }

    /**
     * Constructs an axis with the same {@linkplain #getName name} as the abbreviation.
     *
     * @param abbreviation The {@linkplain #getAbbreviation abbreviation} used for this
     *                     coordinate system axes.
     * @param direction    The {@linkplain #getDirection direction} of this coordinate system axis.
     * @param unit         The {@linkplain #getUnit unit of measure} used for this coordinate
     *                     system axis.
     */
    public DefaultCoordinateSystemAxis(final String        abbreviation,
                                       final AxisDirection direction,
                                       final Unit<?>       unit)
    {
        this(singletonMap(NAME_KEY, abbreviation), abbreviation, direction, unit);
    }

    /**
     * Constructs an axis with the given name and abbreviation. Special case:
     *
     * <ul>
     *   <li><p>If the given name is an {@linkplain InternationalString international string},
     *   then the {@linkplain #getName name of this identified object} is set to the unlocalized
     *   version of the {@code name} argument, as given by
     *   <code>name.{@linkplain InternationalString#toString(java.util.Locale) toString}(null)</code>.
     *   The same {@code name} argument is also stored as an {@linkplain #getAlias alias},
     *   which allows fetching localized versions of the name.</p></li>
     *
     *   <li><p>Otherwise, <code>name.toString()</code> is used as the primary name and
     *   no alias is defined.</p></li>
     * </ul>
     *
     * @param name         The name of this axis. Also stored as an alias for localization purpose.
     * @param abbreviation The {@linkplain #getAbbreviation abbreviation} used for this
     *                     coordinate system axis.
     * @param direction    The {@linkplain #getDirection direction} of this coordinate system axis.
     * @param unit         The {@linkplain #getUnit unit of measure} used for this coordinate
     *                     system axis.
     */
    public DefaultCoordinateSystemAxis(final CharSequence  name,
                                       final String        abbreviation,
                                       final AxisDirection direction,
                                       final Unit<?>       unit)
    {
        this(toMap(name), abbreviation, direction, unit);
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static Map<String,Object> toMap(final CharSequence name) {
        final Map<String,Object> properties = new HashMap<>(4);
        if (name != null) {
            if (name instanceof InternationalString) {
                // The "null" locale argument is required for getting the unlocalized version.
                properties.put(NAME_KEY, ((InternationalString) name).toString(null));
                properties.put(ALIAS_KEY, name);
            } else {
                properties.put(NAME_KEY, name.toString());
            }
        }
        return properties;
    }

    /**
     * Constructs an axis with a name and an abbreviation as a resource bundle key.
     * To be used for construction of pre-defined constants only.
     *
     * @param name         The resource bundle key for the name, or {@code -1} if none.
     * @param abbreviation The {@linkplain #getAbbreviation abbreviation} used for this
     *                     coordinate system axes.
     * @param direction    The {@linkplain #getDirection direction} of this coordinate system axis.
     * @param unit         The {@linkplain #getUnit unit of measure} used for this coordinate
     *                     system axis.
     */
    private DefaultCoordinateSystemAxis(final int           name,
                                        final String        abbreviation,
                                        final AxisDirection direction,
                                        final Unit<?>       unit)
    {
        this(name >= 0 ? Vocabulary.formatInternational(name) : abbreviation, abbreviation, direction, unit);
        PREDEFINED[PREDEFINED_COUNT++] = this;
    }

    /**
     * Returns a Geotk axis implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultCoordinateSystemAxis castOrCopy(final CoordinateSystemAxis object) {
        return (object == null) || (object instanceof DefaultCoordinateSystemAxis)
                ? (DefaultCoordinateSystemAxis) object : new DefaultCoordinateSystemAxis(object);
    }

    /**
     * Returns one of the predefined axis for the given name and direction, or {@code null} if
     * none. This method searches only in predefined constants like {@link #GEODETIC_LATITUDE},
     * not in any custom axis instantiated by a public constructor. The name of those constants
     * match ISO 19111 names or some names commonly found in <cite>Well Known Text</cite> (WKT)
     * formats.
     * <p>
     * This method first checks if the specified name matches the {@linkplain #getAbbreviation
     * abbreviation} of a predefined axis. The comparison is case-sensitive (for example the
     * {@link #GEOCENTRIC_X} abbreviation is uppercase {@code "X"}, while the abbreviation for
     * the generic {@link #X} axis is lowercase {@code "x"}).
     * <p>
     * If the specified name doesn't match any abbreviation, then this method compares the name
     * against predefined axis {@linkplain #getName name} in a case-insensitive manner. Examples
     * of valid names are "<cite>Geodetic latitude</cite>" and "<cite>Northing</cite>".
     * <p>
     * The direction argument is optional and can be used in order to resolve ambiguity like
     * {@link #X} and {@link #DISPLAY_X} axis. If this argument is {@code null}, then the first
     * axis with a matching name or abbreviation will be returned.
     *
     * @param  name The axis name or abbreviation.
     * @param  direction An optional direction, or {@code null}.
     * @return One of the constants declared in this class, or {@code null}.
     *
     * @since 2.4
     */
    public static DefaultCoordinateSystemAxis getPredefined(String name, AxisDirection direction) {
        ensureNonNull("name", name);
        name = name.trim();
        DefaultCoordinateSystemAxis found = null;
        for (int i=0; i<PREDEFINED_COUNT; i++) {
            final DefaultCoordinateSystemAxis candidate = PREDEFINED[i];
            if (direction != null && !direction.equals(candidate.getDirection())) {
                continue;
            }
            // Reminder: case matter for abbreviation, so 'equalsIgnoreCase' is not allowed.
            if (candidate.getAbbreviation().equals(name)) {
                return candidate;
            }
            if (found == null && candidate.isHeuristicMatchForName(name)) {
                /*
                 * We need to perform a special check for Geodetic longitude and latitude.
                 * Because of the ALIAS map, the "Geodetic latitude" and "Latitude" names
                 * are considered equivalent, while they are two distinct predefined axis
                 * constants in Geotk. Because Geodetic longitude & latitude constants
                 * are declared first, they have precedence.  So we prevent the selection
                 * of GEODETIC_LATITUDE if the user is likely to ask for LATITUDE.
                 */
                if (candidate == GEODETIC_LONGITUDE || candidate == GEODETIC_LATITUDE) {
                    if (!name.toLowerCase().startsWith("geodetic")) {
                        continue;
                    }
                }
                found = candidate;
            }
        }
        if (found == null && name.length() == 1) {
            /*
             * No "official" matching found. Special case for the legacy OGC 01-009 specification,
             * which were using (OTHER,EAST,NORTH) axis direction for GeocentricCRS. This is still
             * in use in WKT parsing, so we need to translate that to the new ISO 19111 directions.
             */
            switch (name.charAt(0)) {
                case 'X': if (AxisDirection.OTHER.equals(direction)) return GEOCENTRIC_X; break;
                case 'Y': if (AxisDirection.EAST .equals(direction)) return GEOCENTRIC_Y; break;
                case 'Z': if (AxisDirection.NORTH.equals(direction)) return GEOCENTRIC_Z; break;
            }
        }
        return found;
    }

    /**
     * Returns a predefined axis similar to the specified one except for units.
     * Returns {@code null} if no predefined axis match.
     */
    static DefaultCoordinateSystemAxis getPredefined(final CoordinateSystemAxis axis) {
        return getPredefined(axis.getName().getCode(), axis.getDirection());
    }

    /**
     * Returns the list of all predefined constants.
     * Currently used for testing purpose only.
     */
    static DefaultCoordinateSystemAxis[] values() {
        return PREDEFINED.clone();
    }

    /**
     * Returns an axis direction constants from its name.
     *
     * @param  direction The direction name (e.g. "north", "east", etc.).
     * @return The axis direction for the given name.
     * @throws NoSuchElementException if the given name is not a know axis direction.
     *
     * @deprecated Moved to Apache SIS {@link CoordinateSystems#parseAxisDirection(String)}.
     */
    @Deprecated
    public static AxisDirection getDirection(String direction) throws NoSuchElementException {
        try {
            return CoordinateSystems.parseAxisDirection(direction);
        } catch (IllegalArgumentException e) {
            throw new NoSuchElementException(e.getMessage());
        }
    }

    /**
     * Returns an axis with the opposite direction of this one, or {@code null} if unknown.
     * This method is not public because only a few predefined constants have this information.
     */
    final DefaultCoordinateSystemAxis getOpposite() {
        return opposite;
    }

    /**
     * Returns {@code true} if the specified direction is a compass direction.
     * Compass directions include "<cite>North</cite>", "<cite>North-North-East</cite>",
     * "<cite>North-East</cite>", <i>etc.</i>
     *
     * @param direction The axis direction to test.
     * @return {@code true} if the given direction is a compass direction.
     *
     * @since 2.4
     */
    public static boolean isCompassDirection(final AxisDirection direction) {
        ensureNonNull("direction", direction);
        final int n = direction.ordinal() - AxisDirection.NORTH.ordinal();
        return n >= 0 && n < COMPASS_DIRECTION_COUNT;
    }

    /**
     * Returns the arithmetic (counterclockwise) angle from the first direction to the second
     * direction, in decimal <strong>degrees</strong>. This method returns a value between
     * -180° and +180°, or {@link Double#NaN NaN} if no angle can be computed.
     * <p>
     * A positive angle denotes a right-handed system, while a negative angle denotes
     * a left-handed system. Example:
     * <p>
     * <ul>
     *   <li>The angle from {@linkplain AxisDirection#EAST EAST} to
     *       {@linkplain AxisDirection#NORTH NORTH} is 90°</li>
     *   <li>The angle from {@linkplain AxisDirection#SOUTH SOUTH} to
     *       {@linkplain AxisDirection#WEST WEST} is -90°</li>
     *   <li>The angle from "<cite>North along 90 deg East</cite>" to
     *       "<cite>North along 0 deg</cite>" is 90°.</li>
     * </ul>
     *
     * @param  source The source axis direction.
     * @param  target The target axis direction.
     * @return The arithmetic angle (in degrees) of the rotation to apply on a line pointing toward
     *         the source direction in order to make it point toward the target direction, or
     *         {@link Double#NaN} if this value can't be computed.
     *
     * @since 2.4
     *
     * @deprecated Moved to Apache SIS as {@link CoordinateSystems#angle(AxisDirection, AxisDirection)}
     */
    @Deprecated
    public static double getAngle(final AxisDirection source, final AxisDirection target) {
        final Angle angle = CoordinateSystems.angle(source, target);
        return (angle != null) ? angle.degrees() : Double.NaN;
    }

    /**
     * Tests for angle on compass only (do not tests angle between direction along meridians).
     * Returns {@link Integer#MIN_VALUE} if the angle can't be computed.
     */
    static int getCompassAngle(final AxisDirection source, final AxisDirection target) {
        final int base = AxisDirection.NORTH.ordinal();
        final int src  = source.ordinal() - base;
        if (src >= 0 && src < COMPASS_DIRECTION_COUNT) {
            int tgt = target.ordinal() - base;
            if (tgt >= 0 && tgt < COMPASS_DIRECTION_COUNT) {
                tgt = src - tgt;
                if (tgt < -COMPASS_DIRECTION_COUNT/2) {
                    tgt += COMPASS_DIRECTION_COUNT;
                } else if (tgt > COMPASS_DIRECTION_COUNT/2) {
                    tgt -= COMPASS_DIRECTION_COUNT;
                }
                return tgt;
            }
        }
        return Integer.MIN_VALUE;
    }

    /**
     * Returns {@code true} if the specified directions are perpendicular.
     *
     * @param first The first axis direction to test.
     * @param second The second axis direction to test.
     * @return {@code true} if the given axis direction are perpendicular.
     *
     * @since 2.4
     */
    public static boolean perpendicular(final AxisDirection first, final AxisDirection second) {
        return Math.abs(Math.abs(getAngle(first, second)) - 90) <= 1E-10;
    }

    /**
     * Returns a new axis with the same properties than current axis except for the units.
     *
     * @param  newUnit The unit for the new axis.
     * @return An axis using the specified unit.
     * @throws ConversionException If the specified unit is incompatible with the expected one.
     */
    final DefaultCoordinateSystemAxis usingUnit(final Unit<?> newUnit) throws ConversionException {
        final Unit<?> unit = getUnit();
        if (unit.equals(newUnit)) {
            return this;
        }
        final UnitConverter converter = unit.getConverterToAny(newUnit);
        return new DefaultCoordinateSystemAxis(IdentifiedObjects.getProperties(this),
                    getAbbreviation(), getDirection(), newUnit,
                    converter.convert(getMinimumValue()), converter.convert(getMaximumValue()), getRangeMeaning());
    }

    /**
     * Equivalent to {@code super.equals(that, false, false)}.
     */
    final boolean equalsMetadata(final DefaultCoordinateSystemAxis that) {
        final String thatName = that.getName().getCode();
        if (!isHeuristicMatchForName(thatName)) {
            final String thisName = getName().getCode();
            if (!IdentifiedObjects.isHeuristicMatchForName(that, thisName)) {
                return false;
            }
        }
        return getDirection().equals(that.getDirection());
    }

    /**
     * Formats the inner part of a <cite>Well Known Text</cite> (WKT) element.
     *
     * @param  formatter The formatter to use.
     * @return The WKT element name, which is {@code "AXIS"}.
     */
    @Override
    public String formatTo(final Formatter formatter) {
        return super.formatTo(formatter);
    }
}
