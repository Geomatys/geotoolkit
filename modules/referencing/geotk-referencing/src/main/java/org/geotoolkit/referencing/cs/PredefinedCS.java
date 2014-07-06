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

import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
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

import org.apache.sis.internal.referencing.AxisDirections;
import org.apache.sis.measure.Units;
import org.apache.sis.math.MathFunctions;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.cs.DefaultAffineCS;
import org.apache.sis.referencing.cs.DefaultCartesianCS;
import org.apache.sis.referencing.cs.DefaultSphericalCS;
import org.apache.sis.referencing.cs.DefaultEllipsoidalCS;
import org.apache.sis.referencing.cs.DefaultCompoundCS;
import org.apache.sis.referencing.cs.DefaultCoordinateSystemAxis;

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
public final class PredefinedCS extends Static implements Comparator<CoordinateSystem> {
    /**
     * A two-dimensional Cartesian CS with
     * <var>{@linkplain Axes#EASTING Easting}</var>,
     * <var>{@linkplain Axes#NORTHING Northing}</var>
     * axis in metres.
     */
    public static final DefaultCartesianCS PROJECTED = new DefaultCartesianCS(
                    name(Vocabulary.Keys.PROJECTED),
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
                    name(Vocabulary.Keys.GEOCENTRIC),
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
                    name(Vocabulary.Keys.CARTESIAN_2D),
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
                    name(Vocabulary.Keys.CARTESIAN_3D),
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
                    name(Vocabulary.Keys.GRID),
                    Axes.COLUMN,
                    Axes.ROW);

    /**
     * A two-dimensional Cartesian CS with
     * <var>{@linkplain Axes#DISPLAY_X display x}</var>,
     * <var>{@linkplain Axes#DISPLAY_Y display y}</var>
     * axis.
     */
    public static final DefaultCartesianCS DISPLAY = new DefaultCartesianCS(
                    name(Vocabulary.Keys.DISPLAY),
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
                    name(Vocabulary.Keys.GEOCENTRIC),
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
                    name(Vocabulary.Keys.GEODETIC_2D),
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
                    name(Vocabulary.Keys.GEODETIC_3D),
                    Axes.GEODETIC_LONGITUDE,
                    Axes.GEODETIC_LATITUDE,
                    Axes.ELLIPSOIDAL_HEIGHT);


    /**
     * An instance of {@link PredefinedCS}. Will be created only when first needed.
     */
    private static Comparator<CoordinateSystem> csComparator;

    /**
     * Our ordering for coordinate system objects.
     */
    @SuppressWarnings({"unchecked","rawtypes"})
    private final Class<? extends CoordinateSystem>[] types = new Class[] {
        CartesianCS  .class,
        AffineCS     .class,
        EllipsoidalCS.class,
        SphericalCS  .class,
        CylindricalCS.class,
        PolarCS      .class,
        VerticalCS   .class,
        TimeCS       .class,
        LinearCS     .class,
        UserDefinedCS.class
    };

    /**
     * Creates a comparator.
     */
    private PredefinedCS() {
    }

    /**
     * Compares the ordering between two coordinate systems. This comparator is used for sorting
     * the axis in an user-supplied compound CS in an order closes to some "standard" order.
     *
     * <p>This method is public as an implementation side-effect.
     * It will be removed from public API in a future Geotk version.</p>
     */
    @Override
    public int compare(final CoordinateSystem object1, final CoordinateSystem object2) {
        final Class<? extends CoordinateSystem> type1 = object1.getClass();
        final Class<? extends CoordinateSystem> type2 = object2.getClass();
        for (final Class<?> type : types) {
            final boolean a1 = type.isAssignableFrom(type1);
            final boolean a2 = type.isAssignableFrom(type2);
            if (a1) return a2 ? 0 : -1;
            if (a2) return a1 ? 0 : +1;
        }
        return 0;
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
                    Errors.Keys.MISMATCHED_DIMENSION_3,
                    name, coordinates.length, dimension));
        }
    }

    /**
     * Returns {@code true} if every axis in the specified {@code userCS} are colinear with axis
     * in this coordinate system. The comparison is insensitive to axis order and units. What
     * matter is axis names (because they are fixed by ISO 19111 specification) and directions.
     * <p>
     * If this method returns {@code true}, then there is good chances that this CS can be used
     * together with {@code userCS} as arguments to {@link #swapAndScaleAxis swapAndScaleAxis}.
     * <p>
     * This method should not be public because current implementation is not fully consistent
     * for every pair of CS. It tries to check the opposite direction in addition of the usual
     * one, but only a few pre-defined axis declare their opposite. This method should be okay
     * when invoked on pre-defined CS declared in this package. {@link PredefinedCS} uses this
     * method only that way.
     */
    private static boolean axisColinearWith(final CoordinateSystem standardCS, final CoordinateSystem userCS) {
        if (userCS.getDimension() != standardCS.getDimension()) {
            return false;
        }
        final int c0 = standardCS.getDimension();
        final int c1 = userCS    .getDimension();
        final boolean[] done = new boolean[c1];
next:   for (int i=0; i<c0; i++) {
            final CoordinateSystemAxis direct   = standardCS.getAxis(i);
            final CoordinateSystemAxis opposite = Axes.getOpposite(direct);
            for (int j=0; j<c1; j++) {
                if (!done[j]) {
                    final CoordinateSystemAxis candidate = userCS.getAxis(j);
                    if (Axes.equalsMetadata(candidate, direct) || (opposite != null &&
                        Axes.equalsMetadata(candidate, opposite)))
                    {
                        done[j] = true; // Flags as already compared.
                        continue next;
                    }
                }
            }
            return false;
        }
        assert directionColinearWith(standardCS, userCS);
        return true;
    }

    /**
     * Compares directions only, without consideration for the axis name.
     */
    private static boolean directionColinearWith(final CoordinateSystem standardCS, final CoordinateSystem userCS) {
        final int dimension = standardCS.getDimension();
        if (userCS.getDimension() != dimension) {
            return false;
        }
        final AxisDirection[] checks = new AxisDirection[dimension];
        for (int i=0; i<checks.length; i++) {
            checks[i] = AxisDirections.absolute(userCS.getAxis(i).getDirection());
        }
next:   for (int i=0; i<dimension; i++) {
            final AxisDirection direction = AxisDirections.absolute(standardCS.getAxis(i).getDirection());
            for (int j=0; j<checks.length; j++) {
                final AxisDirection candidate = checks[j];
                if (candidate != null && candidate.equals(direction)) {
                    checks[j] = null;  // Flags as already compared.
                    continue next;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Returns a coordinate system with "standard" axis order and units.
     * Most of the time, this method returns one of the predefined constants with axis in
     * (<var>longitude</var>,<var>latitude</var>) or (<var>X</var>,<var>Y</var>) order,
     * and units in degrees or metres. In some particular cases like
     * {@linkplain org.opengis.referencing.cs.CartesianCS Cartesian CS}, this method may
     * create a new instance on the fly. In every cases this method attempts to return a
     * <A HREF="http://en.wikipedia.org/wiki/Right_hand_rule">right-handed</A> coordinate
     * system, but this is not guaranteed.
     * <p>
     * This method is typically used together with {@link #swapAndScaleAxis swapAndScaleAxis}
     * for the creation of a transformation step before some
     * {@linkplain org.opengis.referencing.operation.MathTransform math transform}.
     * Example:
     *
     * {@preformat java
     *     Matrix step1 = swapAndScaleAxis(sourceCS, standard(sourceCS));
     *     Matrix step2 = ... some transform operating on standard axis ...
     *     Matrix step3 = swapAndScaleAxis(standard(targetCS), targetCS);
     * }
     *
     * A rational for standard axis order and units is explained in the <cite>Axis units and
     * direction</cite> section in the {@linkplain org.geotoolkit.referencing.operation.projection
     * description of map projection package}.
     *
     * @param  cs The coordinate system.
     * @return A constant similar to the specified {@code cs} with "standard" axis.
     * @throws IllegalArgumentException if the specified coordinate system is unknown to this method.
     *
     * @since 2.2
     *
     * @deprecated Replaced by {@link org.apache.sis.referencing.cs.AxesConvention#NORMALIZED}.
     */
    @Deprecated
    public static CoordinateSystem standard(final CoordinateSystem cs) throws IllegalArgumentException {
        final int dimension = cs.getDimension();
        if (cs instanceof CartesianCS) {
            switch (dimension) {
                case 2: {
                    if (axisColinearWith(PROJECTED, cs)) {
                        return PROJECTED;
                    }
                    if (axisColinearWith(GRID, cs)) {
                        return GRID;
                    }
                    if (directionColinearWith(CARTESIAN_2D, cs)) {
                        return CARTESIAN_2D;
                    }
                    return rightHanded((CartesianCS) cs);
                }
                case 3: {
                    if (axisColinearWith(GEOCENTRIC, cs)) {
                        return GEOCENTRIC;
                    }
                    if (directionColinearWith(CARTESIAN_3D, cs)) {
                        return CARTESIAN_3D;
                    }
                    return rightHanded((CartesianCS) cs);
                }
            }
        }
        if (cs instanceof AffineCS) {
            return rightHanded((AffineCS) cs);
        }
        if (cs instanceof EllipsoidalCS) {
            switch (dimension) {
                case 2: return GEODETIC_2D;
                case 3: return GEODETIC_3D;
            }
        }
        if (cs instanceof SphericalCS) {
            switch (dimension) {
                case 3: return SPHERICAL;
            }
        }
        if (cs instanceof VerticalCS) {
            switch (dimension) {
                case 1: {
                    return CommonCRS.Vertical.ELLIPSOIDAL.crs().getCoordinateSystem();
                }
            }
        }
        if (cs instanceof TimeCS) {
            switch (dimension) {
                case 1: return CommonCRS.Temporal.JULIAN.crs().getCoordinateSystem();
            }
        }
        if (cs instanceof DefaultCompoundCS) {
            final List<CoordinateSystem> components = ((DefaultCompoundCS) cs).getComponents();
            final CoordinateSystem[] user = new CoordinateSystem[components.size()];
            final CoordinateSystem[] std  = new CoordinateSystem[user.length];
            for (int i=0; i<std.length; i++) {
                std[i] = standard(user[i] = components.get(i));
            }
            if (csComparator == null) {
                csComparator = new PredefinedCS();
            }
            Arrays.sort(std, csComparator);
            return Arrays.equals(user, std) ? cs : new DefaultCompoundCS(std);
        }
        throw new IllegalArgumentException(Errors.format(
                Errors.Keys.UNSUPPORTED_COORDINATE_SYSTEM_1, cs.getName().getCode()));
    }

    /**
     * Reorder the axis in the specified Affine CS in an attempt to get a right-handed system.
     * Units are standardized to meters in the process. If no axis change is needed, then this
     * method returns {@code cs} unchanged.
     */
    private static AffineCS rightHanded(final AffineCS cs) {
        boolean changed = false;
        final int dimension = cs.getDimension();
        final CoordinateSystemAxis[] axis = new CoordinateSystemAxis[dimension];
        for (int i=0; i<dimension; i++) {
            /*
             * Gets the axis and replaces it by one of the predefined constants declared in
             * DefaultCoordinateSystemAxis, if possible. The predefined constants use ISO 19111
             * names with metres or degrees units, so it is pretty close to the "standard" axis
             * we are looking for.
             */
            CoordinateSystemAxis axe = axis[i] = cs.getAxis(i);
            DefaultCoordinateSystemAxis standard = Axes.getPredefined(axe);
            if (standard != null) {
                axe = standard;
            }
            /*
             * Changes units to meters. Every units in an affine CS should be linear or
             * dimensionless (the later is used for grid coordinates).  The 'usingUnit'
             * method will thrown an exception if the unit is incompatible. See
             * DefaultAffineCS.isCompatibleUnit(Unit).
             */
            final Unit<?> unit = axe.getUnit();
            if (!Unit.ONE.equals(unit) && !SI.METRE.equals(unit)) try {
                axe = Axes.usingUnit(axe, SI.METRE);
            } catch (ConversionException e) {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.INCOMPATIBLE_UNIT_1, SI.METRE), e);
            }
            changed |= (axe != axis[i]);
            axis[i] = axe;
        }
        /*
         * Sorts the axis in an attempt to create a right-handed system
         * and creates a new Coordinate System if at least one axis changed.
         */
        changed |= ComparableAxisWrapper.sort(axis);
        if (!changed) {
            return cs;
        }
        final Map<String,?> properties = org.geotoolkit.referencing.IdentifiedObjects.getProperties(cs, null);
        if (cs instanceof CartesianCS) {
            return createCartesian(properties, axis);
        }
        switch (axis.length) {
            case 2: return new DefaultAffineCS(properties, axis[0], axis[1]);
            case 3: return new DefaultAffineCS(properties, axis[0], axis[1], axis[2]);
            default: throw new AssertionError();
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
        throw new UnsupportedOperationException(Errors.format(Errors.Keys.UNSUPPORTED_COORDINATE_SYSTEM_1, cs.getClass()));
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
                        Errors.Keys.INCOMPATIBLE_UNIT_1, axisUnit), e);
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
            throw new IllegalArgumentException(Errors.format(Errors.Keys.INCOMPATIBLE_UNIT_1, unit), e);
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
            throw new IllegalArgumentException(Errors.format(Errors.Keys.INCOMPATIBLE_UNIT_1, unit), e);
        }
        if (axes == null) {
            return cs;
        }
        return createEllipsoidal(org.geotoolkit.referencing.IdentifiedObjects.getProperties(cs, null), axes);
    }
}
