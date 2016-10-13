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
import javax.measure.UnitConverter;
import javax.measure.IncommensurableException;
import javax.measure.Unit;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.util.InternationalString;
import org.apache.sis.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.measure.Units;


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
     * Default axis info for longitudes.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#EAST East}
     * and units are {@linkplain Units#DEGREE decimal degrees}.
     *
     * The abbreviation is "&lambda;" (lambda).
     * <p>
     * This axis is usually part of a {@link #LONGITUDE}, {@link #LATITUDE}, {@link #ALTITUDE} set.
     */
    public static final DefaultCoordinateSystemAxis LONGITUDE = create(
            Vocabulary.Keys.Longitude, "\u03BB", AxisDirection.EAST, Units.DEGREE);

    /**
     * Default axis info for latitudes.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#NORTH North}
     * and units are {@linkplain Units#DEGREE decimal degrees}.
     *
     * The abbreviation is "&phi;" (phi).
     * <p>
     * This axis is usually part of a {@link #LONGITUDE}, {@link #LATITUDE}, {@link #ALTITUDE} set.
     */
    public static final DefaultCoordinateSystemAxis LATITUDE = create(
            Vocabulary.Keys.Latitude, "\u03C6", AxisDirection.NORTH, Units.DEGREE);

    /**
     * The default axis for height values above the ellipsoid in a
     * {@linkplain org.opengis.referencing.crs.GeographicCRS geographic CRS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#UP up}
     * and units are {@linkplain Units#METRE metres}.
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
            Vocabulary.Keys.EllipsoidalHeight, "h", AxisDirection.UP, Units.METRE);

    /**
     * Default axis info for <var>x</var> values in a
     * {@linkplain org.opengis.referencing.cs.CartesianCS Cartesian CS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#EAST East}
     * and units are {@linkplain Units#METRE metres}.
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
            -1, "x", AxisDirection.EAST, Units.METRE);

    /**
     * Default axis info for <var>y</var> values in a
     * {@linkplain org.opengis.referencing.cs.CartesianCS Cartesian CS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#NORTH North}
     * and units are {@linkplain Units#METRE metres}.
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
            -1, "y", AxisDirection.NORTH, Units.METRE);

    /**
     * Default axis info for <var>z</var> values in a
     * {@linkplain org.opengis.referencing.cs.CartesianCS Cartesian CS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#UP up}
     * and units are {@linkplain Units#METRE metres}.
     *
     * The abbreviation is lower case "<var>z</var>".
     * <p>
     * This axis is usually part of a {@link #X}, {@link #Y}, {@link #Z} set.
     */
    public static final DefaultCoordinateSystemAxis Z = create(
            -1, "z", AxisDirection.UP, Units.METRE);

    /**
     * Default axis info for Easting values in a
     * {@linkplain org.opengis.referencing.crs.ProjectedCRS projected CRS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#EAST East}
     * and units are {@linkplain Units#METRE metres}.
     *
     * The ISO 19111 name is "<cite>easting</cite>" and the abbreviation is upper case
     * "<var>E</var>".
     * <p>
     * This axis is usually part of a {@link #EASTING}, {@link #NORTHING} set.
     */
    public static final DefaultCoordinateSystemAxis EASTING = create(
            Vocabulary.Keys.Easting, "E", AxisDirection.EAST, Units.METRE);

    /**
     * Default axis info for Northing values in a
     * {@linkplain org.opengis.referencing.crs.ProjectedCRS projected CRS}.
     *
     * Increasing ordinates values go {@linkplain AxisDirection#NORTH North}
     * and units are {@linkplain Units#METRE metres}.
     *
     * The ISO 19111 name is "<cite>northing</cite>" and the abbreviation is upper case
     * "<var>N</var>".
     * <p>
     * This axis is usually part of a {@link #EASTING}, {@link #NORTHING} set.
     */
    public static final DefaultCoordinateSystemAxis NORTHING = create(
            Vocabulary.Keys.Northing, "N", AxisDirection.NORTH, Units.METRE);

    /**
     * A default axis for column indices in a {@linkplain org.opengis.coverage.grid.GridCoverage
     * grid coverage}. Increasing values go toward {@linkplain AxisDirection#COLUMN_POSITIVE
     * positive column number}.
     *
     * The abbreviation is lower case "<var>i</var>".
     */
    public static final DefaultCoordinateSystemAxis COLUMN = create(
            Vocabulary.Keys.Column, "i", AxisDirection.COLUMN_POSITIVE, Units.ONE);

    /**
     * A default axis for row indices in a {@linkplain org.opengis.coverage.grid.GridCoverage grid
     * coverage}. Increasing values go toward {@linkplain AxisDirection#ROW_POSITIVE positive row
     * number}.
     *
     * The abbreviation is lower case "<var>j</var>".
     */
    public static final DefaultCoordinateSystemAxis ROW = create(
            Vocabulary.Keys.Row, "j", AxisDirection.ROW_POSITIVE, Units.ONE);

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
        return axis;
    }

    /**
     * Returns a new axis with the same properties than current axis except for the units.
     *
     * @param  newUnit The unit for the new axis.
     * @return An axis using the specified unit.
     * @throws IncommensurableException If the specified unit is incompatible with the expected one.
     */
    static CoordinateSystemAxis usingUnit(final CoordinateSystemAxis a, final Unit<?> newUnit)
            throws IncommensurableException
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
}
