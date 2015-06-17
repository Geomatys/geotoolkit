/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.referencing.cs;

import java.util.HashMap;
import java.util.Map;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.measure.converter.UnitConverter;
import javax.measure.converter.ConversionException;

import org.opengis.referencing.cs.*;
import org.opengis.util.InternationalString;
import org.opengis.geometry.MismatchedDimensionException;

import org.geotoolkit.lang.Static;
import org.geotoolkit.measure.Measure;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;

import org.apache.sis.measure.Units;
import org.apache.sis.math.MathFunctions;
import org.apache.sis.referencing.cs.DefaultCartesianCS;
import org.apache.sis.referencing.cs.DefaultSphericalCS;
import org.apache.sis.referencing.cs.DefaultEllipsoidalCS;

import static org.opengis.referencing.IdentifiedObject.ALIAS_KEY;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;


/**
 * Predefined coordinate systems.
 * <strong>Warning:</strong> this is a temporary class which may disappear in future Geotk version,
 * after we migrated functionality to Apache SIS.
 *
 * @author Martin Desruisseaux (IRD)
 * @module
 */
public final class PredefinedCS extends Static {
    /**
     * A two-dimensional Cartesian CS with
     * <var>{@linkplain Axes#EASTING Easting}</var>,
     * <var>{@linkplain Axes#NORTHING Northing}</var>
     * axis in metres.
     */
    public static final DefaultCartesianCS PROJECTED = new DefaultCartesianCS(
                    name(Vocabulary.Keys.Projected),
                    Axes.EASTING,
                    Axes.NORTHING);

    /**
     * A three-dimensional Cartesian CS with geocentric
     * <var>{@linkplain Axes#GEOCENTRIC_X x}</var>,
     * <var>{@linkplain Axes#GEOCENTRIC_Y y}</var>,
     * <var>{@linkplain Axes#GEOCENTRIC_Z z}</var>
     * axis in metres.
     */
    public static final DefaultCartesianCS GEOCENTRIC = new DefaultCartesianCS(
                    name(Vocabulary.Keys.Geocentric),
                    Axes.GEOCENTRIC_X,
                    Axes.GEOCENTRIC_Y,
                    Axes.GEOCENTRIC_Z);

    /**
     * A two-dimensional Cartesian CS with
     * <var>{@linkplain Axes#X x}</var>,
     * <var>{@linkplain Axes#Y y}</var>
     * axis in metres.
     */
    public static final DefaultCartesianCS CARTESIAN_2D = new DefaultCartesianCS(
                    name(Vocabulary.Keys.Cartesian2d),
                    Axes.X,
                    Axes.Y);

    /**
     * A three-dimensional Cartesian CS with
     * <var>{@linkplain Axes#X x}</var>,
     * <var>{@linkplain Axes#Y y}</var>,
     * <var>{@linkplain Axes#Z z}</var>
     * axis in metres.
     */
    public static final DefaultCartesianCS CARTESIAN_3D = new DefaultCartesianCS(
                    name(Vocabulary.Keys.Cartesian3d),
                    Axes.X,
                    Axes.Y,
                    Axes.Z);

    /**
     * A two-dimensional Cartesian CS with
     * <var>{@linkplain Axes#COLUMN column}</var>,
     * <var>{@linkplain Axes#ROW row}</var>
     * axis.
     */
    public static final DefaultCartesianCS GRID = new DefaultCartesianCS(
                    name(Vocabulary.Keys.Grid),
                    Axes.COLUMN,
                    Axes.ROW);

    /**
     * A two-dimensional Cartesian CS with
     * <var>{@linkplain Axes#DISPLAY_X display x}</var>,
     * <var>{@linkplain Axes#DISPLAY_Y display y}</var>
     * axis.
     */
    public static final DefaultCartesianCS DISPLAY = new DefaultCartesianCS(
                    name(Vocabulary.Keys.Display),
                    Axes.DISPLAY_X,
                    Axes.DISPLAY_Y);

    /**
     * A three-dimensional spherical CS with
     * <var>{@linkplain Axes#SPHERICAL_LONGITUDE longitude}</var>,
     * <var>{@linkplain Axes#SPHERICAL_LATITUDE latitude}</var>,
     * <var>{@linkplain Axes#GEOCENTRIC_RADIUS radius}</var>
     * axis.
     */
    public static final DefaultSphericalCS SPHERICAL = new DefaultSphericalCS(
                    name(Vocabulary.Keys.Geocentric),
                    Axes.SPHERICAL_LONGITUDE,
                    Axes.SPHERICAL_LATITUDE,
                    Axes.GEOCENTRIC_RADIUS);

    /**
     * A two-dimensional ellipsoidal CS with
     * <var>{@linkplain Axes#GEODETIC_LONGITUDE geodetic longitude}</var>,
     * <var>{@linkplain Axes#GEODETIC_LATITUDE geodetic latitude}</var>
     * axis in decimal degrees.
     */
    public static final DefaultEllipsoidalCS GEODETIC_2D = new DefaultEllipsoidalCS(
                    name(Vocabulary.Keys.Geodetic2d),
                    Axes.GEODETIC_LONGITUDE,
                    Axes.GEODETIC_LATITUDE);

    /**
     * A three-dimensional ellipsoidal CS with
     * <var>{@linkplain Axes#GEODETIC_LONGITUDE geodetic longitude}</var>,
     * <var>{@linkplain Axes#GEODETIC_LATITUDE geodetic latitude}</var>,
     * <var>{@linkplain Axes#ELLIPSOIDAL_HEIGHT ellipsoidal height}</var>
     * axis.
     */
    public static final DefaultEllipsoidalCS GEODETIC_3D = new DefaultEllipsoidalCS(
                    name(Vocabulary.Keys.Geodetic3d),
                    Axes.GEODETIC_LONGITUDE,
                    Axes.GEODETIC_LATITUDE,
                    Axes.ELLIPSOIDAL_HEIGHT);

    /**
     * Creates a comparator.
     */
    private PredefinedCS() {
    }

    /**
     * Creates a name for the predefined constants in subclasses. The name is an unlocalized String
     * object. However, since this method is used for creation of convenience objects only (not for
     * objects created from an "official" database), the "unlocalized" name is actually chosen
     * according the user's locale at class initialization time. The same name is also added in
     * a localizable form as an alias. Since the {@link #nameMatches} convenience method checks
     * the alias, it still possible to consider two objects are equivalent even if their names
     * were formatted in different locales.
     */
    private static Map<String,Object> name(final int key) {
        final Map<String,Object> properties = new HashMap<>(4);
        final InternationalString name = Vocabulary.formatInternational(key);
        properties.put(NAME_KEY,  name.toString());
        properties.put(ALIAS_KEY, name);
        return properties;
    }

    /**
     * Convenience method for checking object dimension validity.
     *
     * @param  name The name of the argument to check.
     * @param  coordinates The coordinate array to check.
     * @throws MismatchedDimensionException if the coordinate doesn't have the expected dimension.
     */
    private static void ensureDimensionMatch(final CoordinateSystem cs, final String name, final double[] coordinates)
            throws MismatchedDimensionException
    {
        final int dimension = cs.getDimension();
        if (coordinates.length != dimension) {
            throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.MismatchedDimension_3,
                    name, coordinates.length, dimension));
        }
    }

    private static DefaultCartesianCS createCartesian(final Map<String,?> properties, final CoordinateSystemAxis[] axis) {
        switch (axis.length) {
            case 2: return new DefaultCartesianCS(properties, axis[0], axis[1]);
            case 3: return new DefaultCartesianCS(properties, axis[0], axis[1], axis[2]);
            default: throw new IllegalArgumentException();
        }
    }

    static DefaultEllipsoidalCS createEllipsoidal(final Map<String,?> properties, final CoordinateSystemAxis[] axis) {
        switch (axis.length) {
            case 2: return new DefaultEllipsoidalCS(properties, axis[0], axis[1]);
            case 3: return new DefaultEllipsoidalCS(properties, axis[0], axis[1], axis[2]);
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Computes the distance between two points. This method is not available for all coordinate systems.
     * For example, the {@linkplain DefaultEllipsoidalCS ellipsoidal CS} doesn't have sufficient information.
     * <p>
     * In the default Geotk implementation, this method is supported by the following class:
     * <p>
     * <ul>
     *   <li>All one-dimensional coordinate systems ({@link DefaultLinearCS},
     *       {@link DefaultVerticalCS}, {@link DefaultTimeCS}), in which case this method
     *       returns the absolute difference between the given ordinate values.</li>
     *   <li>{@link DefaultCartesianCS#distance(double[], double[])}.</li>
     * </ul>
     *
     * @param  coord1 Coordinates of the first point.
     * @param  coord2 Coordinates of the second point.
     * @return The distance between {@code coord1} and {@code coord2}.
     * @throws UnsupportedOperationException if this coordinate system can't compute distances.
     * @throws MismatchedDimensionException if a coordinate doesn't have the expected dimension.
     */
    public static Measure distance(final CoordinateSystem cs, final double[] coord1, final double[] coord2)
            throws UnsupportedOperationException, MismatchedDimensionException
    {
        if (cs instanceof CartesianCS) {
            // Temporary patch replacing method overriding until we completed the migration to Apache SIS.
            return distance((CartesianCS) cs, coord1, coord2);
        }
        ensureDimensionMatch(cs, "coord1", coord1);
        ensureDimensionMatch(cs, "coord2", coord2);
        if (cs.getDimension() == 1) {
            return new Measure(Math.abs(coord1[0] - coord2[0]), cs.getAxis(0).getUnit());
        }
        throw new UnsupportedOperationException(Errors.format(Errors.Keys.UnsupportedCoordinateSystem_1, cs.getClass()));
    }

    /**
     * Computes the distance between two points.
     *
     * @param  coord1 Coordinates of the first point.
     * @param  coord2 Coordinates of the second point.
     * @return The distance between {@code coord1} and {@code coord2}.
     * @throws UnsupportedOperationException if this coordinate system can't compute distances.
     * @throws MismatchedDimensionException if a coordinate doesn't have the expected dimension.
     */
    private static Measure distance(final CartesianCS cs, final double[] coord1, final double[] coord2)
            throws UnsupportedOperationException, MismatchedDimensionException
    {
        ensureDimensionMatch(cs, "coord1", coord1);
        ensureDimensionMatch(cs, "coord2", coord2);
        final Unit<?> unit = getDistanceUnit(cs);
        final UnitConverter[] converters = new UnitConverter[cs.getDimension()];
        for (int i=0; i<converters.length; i++) {
            final Unit<?> axisUnit = cs.getAxis(i).getUnit();
            try {
                converters[i] = axisUnit.getConverterToAny(unit);
            } catch (ConversionException e) {
                throw new UnsupportedOperationException(Errors.format(
                        Errors.Keys.IncompatibleUnit_1, axisUnit), e);
            }
        }
        final double[] delta = new double[converters.length];
        for (int i=0; i<converters.length; i++) {
            final UnitConverter  c = converters[i];
            delta[i] = c.convert(coord1[i]) - c.convert(coord2[i]);
        }
        return new Measure(MathFunctions.magnitude(delta), unit);
    }

    /**
     * Suggests an unit for measuring distances in this coordinate system. The default
     * implementation scans all {@linkplain CoordinateSystemAxis#getUnit() axis units},
     * then returns the "largest" one (e.g. kilometre instead of metre).
     *
     * @return Suggested distance unit.
     */
    private static Unit<?> getDistanceUnit(final CoordinateSystem cs) {
        Unit<?> unit = null;
        double maxScale = 0;
        for (int i=cs.getDimension(); --i>=0;) {
            final Unit<?> candidate = cs.getAxis(i).getUnit();
            if (candidate != null) {
                final double scale = Math.abs(Units.toStandardUnit(candidate));
                if (scale > maxScale) {
                    unit = candidate;
                    maxScale = scale;
                }
            }
        }
        return unit;
    }

    /**
     * Returns all axis in the specified unit. This method was used for implementation of
     * {@code usingUnit} methods in CS subclasses.
     *
     * @param  unit The unit for the new axes.
     * @param  ignore {@link SI#METRE} for ignoring linear units, {@link SI#RADIAN} for ignoring
     *         angular units, or {@code null} for none.
     * @return New axes using the specified unit, or {@code null} if no change is needed.
     * @throws ConversionException If the specified unit is incompatible with the expected one.
     *
     * @see DefaultCartesianCS#usingUnit(Unit)
     * @see DefaultEllipsoidalCS#usingUnit(Unit)
     */
    private static CoordinateSystemAxis[] axisUsingUnit(final CoordinateSystem cs, final Unit<?> unit, final Unit<?> ignore) throws ConversionException {
        final int dimension = cs.getDimension();
        CoordinateSystemAxis[] newAxis = null;
        for (int i=0; i<dimension; i++) {
            final CoordinateSystemAxis a = cs.getAxis(i);
            final Unit<?> current = a.getUnit();
            if (!unit.equals(current) && (ignore == null || !ignore.equals(unit.toSI()))) {
                final CoordinateSystemAxis converted = Axes.usingUnit(a, unit);
                if (converted != a) {
                    if (newAxis == null) {
                        newAxis = new CoordinateSystemAxis[dimension];
                        for (int j=0; j<i; j++) {
                            newAxis[j] = cs.getAxis(j);
                        }
                    }
                    newAxis[i] = converted;
                }
            }
        }
        return newAxis;
    }

    /**
     * Returns a coordinate system with the same properties than the given one except for axis units.
     *
     * @param  unit The unit for the new axis.
     * @return A coordinate system with axis using the specified units.
     * @throws IllegalArgumentException If the specified unit is incompatible with the expected one.
     */
    public static CartesianCS usingUnit(final CartesianCS cs, final Unit<?> unit) throws IllegalArgumentException {
        final CoordinateSystemAxis[] axes;
        try {
            axes = axisUsingUnit(cs, unit, null);
        } catch (ConversionException e) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.IncompatibleUnit_1, unit), e);
        }
        if (axes == null) {
            return cs;
        }
        return createCartesian(org.geotoolkit.referencing.IdentifiedObjects.getProperties(cs, null), axes);
    }

    /**
     * Returns a coordinate system with the same properties than the current one except for
     * axis units. If this coordinate system already uses the given unit, then this method
     * returns {@code this}.
     *
     * @param  unit The unit for the new axis.
     * @return A coordinate system with axis using the specified units, or {@code this}.
     * @throws IllegalArgumentException If the specified unit is incompatible with the expected one.
     */
    public static EllipsoidalCS usingUnit(final EllipsoidalCS cs, final Unit<?> unit) throws IllegalArgumentException {
        final CoordinateSystemAxis[] axes;
        try {
            axes = PredefinedCS.axisUsingUnit(cs, unit, Units.isLinear(unit) ? SI.RADIAN : SI.METRE);
        } catch (ConversionException e) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.IncompatibleUnit_1, unit), e);
        }
        if (axes == null) {
            return cs;
        }
        return createEllipsoidal(org.geotoolkit.referencing.IdentifiedObjects.getProperties(cs, null), axes);
    }
}
