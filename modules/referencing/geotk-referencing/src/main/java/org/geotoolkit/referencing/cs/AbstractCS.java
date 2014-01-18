/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Map;
import java.util.HashMap;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.measure.converter.ConversionException;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.Matrix;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.util.InternationalString;
import org.geotoolkit.measure.Measure;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.geotoolkit.internal.referencing.AxisDirections;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.referencing.cs.CoordinateSystems;

import static org.opengis.referencing.IdentifiedObject.*;


/**
 * @deprecated Moved to Apache SIS.
 *
 * @since 2.0
 * @module
 */
@Deprecated
public final class AbstractCS {
    private AbstractCS() {
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
    static Map<String,Object> name(final int key) {
        final Map<String,Object> properties = new HashMap<>(4);
        final InternationalString name = Vocabulary.formatInternational(key);
        properties.put(NAME_KEY,  name.toString());
        properties.put(ALIAS_KEY, name);
        return properties;
    }

    /**
     * Returns an affine transform between two coordinate systems. Only units and
     * axis order (e.g. transforming from
     * ({@linkplain AxisDirection#NORTH NORTH},{@linkplain AxisDirection#WEST WEST}) to
     * ({@linkplain AxisDirection#EAST EAST},{@linkplain AxisDirection#NORTH NORTH})
     * are taken in account.
     * <p>
     * <b>Example:</b> If coordinates in {@code sourceCS} are (<var>x</var>,<var>y</var>) pairs
     * in metres and coordinates in {@code targetCS} are (-<var>y</var>,<var>x</var>) pairs in
     * centimetres, then the transformation can be performed as below:
     *
     * {@preformat text
     *     ┌      ┐   ┌                ┐ ┌     ┐
     *     │-y(cm)│   │   0  -100    0 │ │ x(m)│
     *     │ x(cm)│ = │ 100     0    0 │ │ y(m)│
     *     │ 1    │   │   0     0    1 │ │ 1   │
     *     └      ┘   └                ┘ └     ┘
     * }
     *
     * @param  sourceCS The source coordinate system.
     * @param  targetCS The target coordinate system.
     * @return The conversion from {@code sourceCS} to {@code targetCS} as
     *         an affine transform. Only axis direction and units are taken in account.
     * @throws IllegalArgumentException if axis doesn't matches, or the CS doesn't have the
     *         same geometry.
     * @throws ConversionException if the units are not compatible, or the conversion is non-linear.
     *
     * @deprecated Moved to Apache SIS as {@link CoordinateSystems#swapAndScaleAxes(CoordinateSystem, CoordinateSystem)}.
     */
    @Deprecated
    public static Matrix swapAndScaleAxis(final CoordinateSystem sourceCS,
                                          final CoordinateSystem targetCS)
            throws IllegalArgumentException, ConversionException
    {
        return new GeneralMatrix(CoordinateSystems.swapAndScaleAxes(sourceCS, targetCS));
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
    public static CoordinateSystem standard(final CoordinateSystem cs)
            throws IllegalArgumentException
    {
        return PredefinedCS.standard(cs);
    }

    /**
     * Convenience method for checking object dimension validity.
     *
     * @param  name The name of the argument to check.
     * @param  coordinates The coordinate array to check.
     * @throws MismatchedDimensionException if the coordinate doesn't have the expected dimension.
     */
    static void ensureDimensionMatch(final CoordinateSystem cs, final String name, final double[] coordinates)
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
     * Computes the distance between two points. This method is not available for all coordinate
     * systems. For example, the {@linkplain DefaultEllipsoidalCS ellipsoidal CS} doesn't have
     * sufficient information.
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
        if (cs instanceof DefaultCartesianCS) {
            // Temporary patch replacing method overriding until we completed the migration to Apache SIS.
            return ((DefaultCartesianCS) cs).distance(coord1, coord2);
        }
        ensureDimensionMatch(cs, "coord1", coord1);
        ensureDimensionMatch(cs, "coord2", coord2);
        if (cs.getDimension() == 1) {
            return new Measure(Math.abs(coord1[0] - coord2[0]), cs.getAxis(0).getUnit());
        }
        throw new UnsupportedOperationException(Errors.format(Errors.Keys.UNSUPPORTED_COORDINATE_SYSTEM_1, cs.getClass()));
    }

    /**
     * Returns all axis in the specified unit. This method is used for implementation of
     * {@code usingUnit} methods in subclasses.
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
    static CoordinateSystemAxis[] axisUsingUnit(final CoordinateSystem cs, final Unit<?> unit, final Unit<?> ignore) throws ConversionException {
        final int dimension = cs.getDimension();
        CoordinateSystemAxis[] newAxis = null;
        for (int i=0; i<dimension; i++) {
            final CoordinateSystemAxis a = cs.getAxis(i);
            final Unit<?> current = a.getUnit();
            if (!unit.equals(current) && (ignore == null || !ignore.equals(unit.toSI()))) {
                final DefaultCoordinateSystemAxis converted =
                        DefaultCoordinateSystemAxis.castOrCopy(a).usingUnit(unit);
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
     * Returns every axis from the specified coordinate system as instance of
     * {@link DefaultCoordinateSystemAxis}. This allow usage of some methods
     * specific to that implementation.
     */
    private static DefaultCoordinateSystemAxis[] getDefaultAxis(final CoordinateSystem cs) {
        final DefaultCoordinateSystemAxis[] axis = new DefaultCoordinateSystemAxis[cs.getDimension()];
        for (int i=0; i<axis.length; i++) {
            final CoordinateSystemAxis a = cs.getAxis(i);
            DefaultCoordinateSystemAxis c = DefaultCoordinateSystemAxis.getPredefined(a);
            if (c == null) {
                c = DefaultCoordinateSystemAxis.castOrCopy(a);
            }
            axis[i] = c;
        }
        return axis;
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
    static boolean axisColinearWith(final CoordinateSystem standardCS, final CoordinateSystem userCS) {
        if (userCS.getDimension() != standardCS.getDimension()) {
            return false;
        }
        final DefaultCoordinateSystemAxis[] axis0 = getDefaultAxis(standardCS);
        final DefaultCoordinateSystemAxis[] axis1 = getDefaultAxis(userCS);
next:   for (int i=0; i<axis0.length; i++) {
            final DefaultCoordinateSystemAxis direct   = axis0[i];
            final DefaultCoordinateSystemAxis opposite = direct.getOpposite();
            for (int j=0; j<axis1.length; j++) {
                final DefaultCoordinateSystemAxis candidate = axis1[j];
                if (candidate != null) {
                    if (candidate.equalsMetadata(direct) || (opposite != null &&
                        candidate.equalsMetadata(opposite)))
                    {
                        axis1[j] = null; // Flags as already compared.
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
    static boolean directionColinearWith(final CoordinateSystem standardCS, final CoordinateSystem userCS) {
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
}
