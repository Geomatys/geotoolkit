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
import org.opengis.referencing.IdentifiedObject;
import javax.measure.converter.UnitConverter;
import javax.measure.converter.ConversionException;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.util.InternationalString;
import org.apache.sis.referencing.cs.DefaultCoordinateSystemAxis;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.resources.Vocabulary;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Predefined axes.
 * <strong>Warning:</strong> this is a temporary class which may disappear in future Geotk version,
 * after we migrated functionality to Apache SIS.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @module
 */
public final class Axes {
    private Axes() {
    }

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

    private static final Map<DefaultCoordinateSystemAxis,DefaultCoordinateSystemAxis> OPPOSITES = new HashMap<>();

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
    public static final DefaultCoordinateSystemAxis GEODETIC_LONGITUDE = create(
            Vocabulary.Keys.GeodeticLongitude, "\u03BB", AxisDirection.EAST, NonSI.DEGREE_ANGLE);

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
    public static final DefaultCoordinateSystemAxis GEODETIC_LATITUDE = create(
            Vocabulary.Keys.GeodeticLatitude, "\u03C6", AxisDirection.NORTH, NonSI.DEGREE_ANGLE);

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
    public static final DefaultCoordinateSystemAxis LONGITUDE = create(
            Vocabulary.Keys.Longitude, "\u03BB", AxisDirection.EAST, NonSI.DEGREE_ANGLE);

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
    public static final DefaultCoordinateSystemAxis LATITUDE = create(
            Vocabulary.Keys.Latitude, "\u03C6", AxisDirection.NORTH, NonSI.DEGREE_ANGLE);

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
    public static final DefaultCoordinateSystemAxis ELLIPSOIDAL_HEIGHT = create(
            Vocabulary.Keys.EllipsoidalHeight, "h", AxisDirection.UP, SI.METRE);

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
    public static final DefaultCoordinateSystemAxis GRAVITY_RELATED_HEIGHT = create(
            Vocabulary.Keys.GravityRelatedHeight, "H", AxisDirection.UP, SI.METRE);

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
    public static final DefaultCoordinateSystemAxis ALTITUDE = create(
            Vocabulary.Keys.Altitude, "h", AxisDirection.UP, SI.METRE);

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
    public static final DefaultCoordinateSystemAxis DEPTH = create(
            Vocabulary.Keys.Depth, "d", AxisDirection.DOWN, SI.METRE);
    static {
        OPPOSITES.put(ALTITUDE, DEPTH);
        OPPOSITES.put(DEPTH, ALTITUDE);
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
    public static final DefaultCoordinateSystemAxis GEOCENTRIC_RADIUS = create(
            Vocabulary.Keys.GeocentricRadius, "r", AxisDirection.UP, SI.METRE);

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
    public static final DefaultCoordinateSystemAxis SPHERICAL_LONGITUDE = create(
            Vocabulary.Keys.SphericalLongitude, "\u03A9", AxisDirection.EAST, NonSI.DEGREE_ANGLE);

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
    public static final DefaultCoordinateSystemAxis SPHERICAL_LATITUDE = create(
            Vocabulary.Keys.SphericalLatitude, "\u03B8", AxisDirection.NORTH, NonSI.DEGREE_ANGLE);

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
    public static final DefaultCoordinateSystemAxis X = create(
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
    public static final DefaultCoordinateSystemAxis Y = create(
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
    public static final DefaultCoordinateSystemAxis Z = create(
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
    public static final DefaultCoordinateSystemAxis GEOCENTRIC_X = create(
            Vocabulary.Keys.GeocentricX, "X", AxisDirection.GEOCENTRIC_X, SI.METRE);

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
    public static final DefaultCoordinateSystemAxis GEOCENTRIC_Y = create(
            Vocabulary.Keys.GeocentricY, "Y", AxisDirection.GEOCENTRIC_Y, SI.METRE);

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
    public static final DefaultCoordinateSystemAxis GEOCENTRIC_Z = create(
            Vocabulary.Keys.GeocentricZ, "Z", AxisDirection.GEOCENTRIC_Z, SI.METRE);

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
    public static final DefaultCoordinateSystemAxis EASTING = create(
            Vocabulary.Keys.Easting, "E", AxisDirection.EAST, SI.METRE);

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
    public static final DefaultCoordinateSystemAxis WESTING = create(
            Vocabulary.Keys.Westing, "W", AxisDirection.WEST, SI.METRE);
    static {
        OPPOSITES.put(EASTING, WESTING);
        OPPOSITES.put(WESTING, EASTING);
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
    public static final DefaultCoordinateSystemAxis NORTHING = create(
            Vocabulary.Keys.Northing, "N", AxisDirection.NORTH, SI.METRE);

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
    public static final DefaultCoordinateSystemAxis SOUTHING = create(
            Vocabulary.Keys.Southing, "S", AxisDirection.SOUTH, SI.METRE);
    static {
        OPPOSITES.put(NORTHING, SOUTHING);
        OPPOSITES.put(SOUTHING, NORTHING);
    }

    /**
     * A default axis for time values in a {@linkplain org.opengis.referencing.cs.TimeCS time CS}.
     *
     * Increasing time go toward {@linkplain AxisDirection#FUTURE future}
     * and units are {@linkplain NonSI#DAY days}.
     *
     * The abbreviation is lower case "<var>t</var>".
     */
    public static final DefaultCoordinateSystemAxis TIME = create(
            Vocabulary.Keys.Time, "t", AxisDirection.FUTURE, NonSI.DAY);

    /**
     * A default axis for column indices in a {@linkplain org.opengis.coverage.grid.GridCoverage
     * grid coverage}. Increasing values go toward {@linkplain AxisDirection#COLUMN_POSITIVE
     * positive column number}.
     *
     * The abbreviation is lower case "<var>i</var>".
     */
    public static final DefaultCoordinateSystemAxis COLUMN = create(
            Vocabulary.Keys.Column, "i", AxisDirection.COLUMN_POSITIVE, Unit.ONE);

    /**
     * A default axis for row indices in a {@linkplain org.opengis.coverage.grid.GridCoverage grid
     * coverage}. Increasing values go toward {@linkplain AxisDirection#ROW_POSITIVE positive row
     * number}.
     *
     * The abbreviation is lower case "<var>j</var>".
     */
    public static final DefaultCoordinateSystemAxis ROW = create(
            Vocabulary.Keys.Row, "j", AxisDirection.ROW_POSITIVE, Unit.ONE);

    /**
     * A default axis for <var>x</var> values in a display device. Increasing values go toward
     * {@linkplain AxisDirection#DISPLAY_RIGHT display right}.
     *
     * The abbreviation is lower case "<var>x</var>".
     *
     * @since 2.2
     */
    public static final DefaultCoordinateSystemAxis DISPLAY_X = create(
            -1, "x", AxisDirection.DISPLAY_RIGHT, Unit.ONE);

    /**
     * A default axis for <var>y</var> values in a display device. Increasing values go toward
     * {@linkplain AxisDirection#DISPLAY_DOWN display down}.
     *
     * The abbreviation is lower case "<var>y</var>".
     *
     * @since 2.2
     */
    public static final DefaultCoordinateSystemAxis DISPLAY_Y = create(
            -1, "y", AxisDirection.DISPLAY_DOWN, Unit.ONE);

    /**
     * Undefined or unknown axis. Axis direction is {@link AxisDirection#OTHER OTHER}
     * and the unit is dimensionless. This constant is sometime used as a placeholder
     * for axes that were not properly defined.
     *
     * @since 3.00
     */
    public static final DefaultCoordinateSystemAxis UNDEFINED = create(
            Vocabulary.Keys.Undefined, "?", AxisDirection.OTHER, Unit.ONE);

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
    private static DefaultCoordinateSystemAxis create(final int           name,
                                                      final String        abbreviation,
                                                      final AxisDirection direction,
                                                      final Unit<?>       unit)
    {
        final Map<String,Object> properties = new HashMap<>(4);
        if (name >= 0) {
            final InternationalString n = Vocabulary.formatInternational(name);
            properties.put(IdentifiedObject.NAME_KEY, n.toString(null));
            properties.put(IdentifiedObject.ALIAS_KEY, n);
        } else {
            properties.put(IdentifiedObject.NAME_KEY, abbreviation);
        }
        final DefaultCoordinateSystemAxis axis = new DefaultCoordinateSystemAxis(properties, abbreviation, direction, unit);
        PREDEFINED[PREDEFINED_COUNT++] = axis;
        return axis;
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
     * Returns an axis with the opposite direction of this one, or {@code null} if unknown.
     * This method is not public because only a few predefined constants have this information.
     */
    static DefaultCoordinateSystemAxis getOpposite(final CoordinateSystemAxis axis) {
        return OPPOSITES.get(axis);
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
     * Returns a new axis with the same properties than current axis except for the units.
     *
     * @param  newUnit The unit for the new axis.
     * @return An axis using the specified unit.
     * @throws ConversionException If the specified unit is incompatible with the expected one.
     */
    static CoordinateSystemAxis usingUnit(final CoordinateSystemAxis a, final Unit<?> newUnit)
            throws ConversionException
    {
        final Unit<?> unit = a.getUnit();
        if (unit.equals(newUnit)) {
            return a;
        }
        final UnitConverter converter = unit.getConverterToAny(newUnit);
        final Map<String,Object> properties = new HashMap<>(org.geotoolkit.referencing.IdentifiedObjects.getProperties(a, null));
        properties.put(DefaultCoordinateSystemAxis.MINIMUM_VALUE_KEY, converter.convert(a.getMinimumValue()));
        properties.put(DefaultCoordinateSystemAxis.MAXIMUM_VALUE_KEY, converter.convert(a.getMaximumValue()));
        properties.put(DefaultCoordinateSystemAxis.RANGE_MEANING_KEY, a.getRangeMeaning()); // TODO: should be provided by getProperties
        return new DefaultCoordinateSystemAxis(properties, a.getAbbreviation(), a.getDirection(), newUnit);
    }

    /**
     * Equivalent to {@code DefaultCoordinateSystemAxis.equals(that, false, false)}.
     */
    static boolean equalsMetadata(final CoordinateSystemAxis a1, final CoordinateSystemAxis a2) {
        final String n2 = a2.getName().getCode();
        if (!IdentifiedObjects.isHeuristicMatchForName(a1, n2)) {
            final String n1 = a1.getName().getCode();
            if (!IdentifiedObjects.isHeuristicMatchForName(a2, n1)) {
                return false;
            }
        }
        return a1.getDirection().equals(a2.getDirection());
    }
}
