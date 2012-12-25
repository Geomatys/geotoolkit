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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Collections;
import java.util.Objects;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.measure.unit.NonSI;
import javax.measure.converter.UnitConverter;
import javax.measure.converter.LinearConverter;
import javax.measure.converter.ConversionException;
import javax.xml.bind.annotation.XmlElement;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.Matrix;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.util.InternationalString;

import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.converter.Classes;
import org.geotoolkit.measure.Measure;
import org.apache.sis.measure.Units;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.referencing.operation.matrix.GeneralMatrix;
import org.geotoolkit.internal.referencing.AxisDirections;
import org.geotoolkit.io.wkt.Formatter;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;

import static org.geotoolkit.util.Utilities.hash;
import static org.geotoolkit.util.Utilities.deepEquals;
import static org.geotoolkit.util.ArgumentChecks.ensureNonNull;


/**
 * The set of coordinate system axes that spans a given coordinate space. A coordinate system (CS)
 * is derived from a set of (mathematical) rules for specifying how coordinates in a given space
 * are to be assigned to points. The coordinate values in a coordinate tuple shall be recorded in
 * the order in which the coordinate system axes are recorded, whenever those
 * coordinates use a coordinate reference system that uses this coordinate system.
 * <p>
 * This class is conceptually <cite>abstract</cite>, even if it is technically possible to
 * instantiate it. Typical applications should create instances of the most specific subclass with
 * {@code Default} prefix instead. An exception to this rule may occurs when it is not possible to
 * identify the exact type. For example it is not possible to infer the exact coordinate system from
 * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
 * Known Text</cite></A> is some cases (e.g. in a {@code LOCAL_CS} element). In such exceptional
 * situation, a plain {@code AbstractCS} object may be instantiated.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see DefaultCoordinateSystemAxis
 * @see javax.measure.unit.Unit
 * @see org.geotoolkit.referencing.datum.AbstractDatum
 * @see org.geotoolkit.referencing.crs.AbstractCRS
 *
 * @since 2.0
 * @module
 */
@Immutable
public class AbstractCS extends AbstractIdentifiedObject implements CoordinateSystem {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 6757665252533744744L;

    /**
     * Base axis to use for checking directions. This is used in order to trap
     * inconsistency like an axis named "Northing" with South direction.
     */
    private static final DefaultCoordinateSystemAxis[] DIRECTION_CHECKS = {
        DefaultCoordinateSystemAxis.NORTHING,
        DefaultCoordinateSystemAxis.EASTING,
        DefaultCoordinateSystemAxis.SOUTHING,
        DefaultCoordinateSystemAxis.WESTING
    };

    /**
     * The axis for this coordinate system at the specified dimension.
     */
    @XmlElement
    private final CoordinateSystemAxis[] axis;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private AbstractCS() {
        this(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new coordinate system with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param cs The coordinate system to copy.
     *
     * @since 2.2
     */
    public AbstractCS(final CoordinateSystem cs) {
        super(cs);
        if (cs instanceof AbstractCS) {
            axis = ((AbstractCS) cs).axis;
        } else {
            axis = new CoordinateSystemAxis[cs.getDimension()];
            for (int i=0; i<axis.length; i++) {
                axis[i] = cs.getAxis(i);
            }
        }
    }

    /**
     * Constructs a coordinate system from a name.
     *
     * @param name  The coordinate system name.
     * @param axis  The set of axis.
     */
    public AbstractCS(final String name, final CoordinateSystemAxis... axis) {
        this(Collections.singletonMap(NAME_KEY, name), axis);
    }

    /**
     * Constructs a coordinate system from a set of properties. The properties map is given
     * unchanged to the {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map)
     * super-class constructor}.
     *
     * @param properties   Set of properties. Should contains at least {@code "name"}.
     * @param axis         The set of axis.
     */
    public AbstractCS(final Map<String,?> properties, final CoordinateSystemAxis... axis) {
        super(properties);
        ensureNonNull("axis", axis);
        this.axis = axis.clone();
        for (int i=0; i<axis.length; i++) {
            ensureNonNull("axis", i, axis);
            final AxisDirection direction = axis[i].getDirection();
            ensureNonNull("direction", direction);
            /*
             * Ensures that axis direction and units are compatible with the
             * coordinate system to be created. For example CartesianCS will
             * accepts only linear or dimensionless units.
             */
            if (!isCompatibleDirection(direction)) {
                // TOOD: localize name()
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.ILLEGAL_AXIS_ORIENTATION_$2, direction.name(), getClass()));
            }
            final Unit<?> unit = axis[i].getUnit();
            ensureNonNull("unit", unit);
            if (!isCompatibleUnit(direction, unit)) {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.INCOMPATIBLE_UNIT_$1, unit));
            }
            /*
             * Ensures there is no axis along the same direction
             * (e.g. two North axis, or an East and a West axis).
             */
            final AxisDirection check = AxisDirections.absolute(direction);
            if (!check.equals(AxisDirection.OTHER)) {
                for (int j=i; --j>=0;) {
                    if (check.equals(AxisDirections.absolute(axis[j].getDirection()))) {
                        // TODO: localize name()
                        final String nameI = axis[i].getDirection().name();
                        final String nameJ = axis[j].getDirection().name();
                        throw new IllegalArgumentException(Errors.format(
                                Errors.Keys.COLINEAR_AXIS_$2, nameI, nameJ));
                    }
                }
            }
            /*
             * Checks for some inconsistency in naming and direction. For example if the axis
             * is named "Northing", then the direction must be North. Exceptions to this rule
             * are the directions along a meridian from a pole. For example a "Northing" axis
             * may have a "South along 180 deg" direction.
             */
            final String name = axis[i].getName().getCode();
            for (int j=0; j<DIRECTION_CHECKS.length; j++) {
                final DefaultCoordinateSystemAxis candidate = DIRECTION_CHECKS[j];
                if (candidate.nameMatches(name)) {
                    final AxisDirection expected = candidate.getDirection();
                    if (!direction.equals(expected)) {
                        DirectionAlongMeridian m = DirectionAlongMeridian.parse(direction);
                        /*
                         * Note: for the check below, maybe it would have be nice to use:
                         *
                         * if (m == null || m.baseDirection.equals(AxisDirections.opposite(expected))
                         *
                         * but the EPSG database contains many axis named "Northing" with
                         * direction like "South along 180 deg", so it doesn't seem to be
                         * considered as a contradiction...
                         */
                        if (m == null) {
                            throw new IllegalArgumentException(Errors.format(
                                    Errors.Keys.INCONSISTENT_AXIS_ORIENTATION_$2,
                                    name, direction.name()));
                        }
                    }
                }
            }
        }
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
     * Returns {@code true} if the specified axis direction is allowed for this coordinate
     * system. This method is invoked at construction time for checking argument validity.
     * The default implementation returns {@code true} for all axis directions. Subclasses
     * will overrides this method in order to put more restrictions on allowed axis directions.
     *
     * @param direction The direction to test for compatibility.
     * @return {@code true} if the given direction is compatible with this coordinate system.
     */
    protected boolean isCompatibleDirection(final AxisDirection direction) {
        return true;
    }

    /**
     * Returns {@code true} is the specified unit is legal for the specified axis direction.
     * This method is invoked at construction time for checking units compatibility. The default
     * implementation returns {@code true} in all cases. Subclasses can override this method and
     * check for compatibility with {@linkplain SI#METRE metre} or
     * {@linkplain NonSI#DEGREE_ANGLE degree} units.
     *
     * @param direction The direction of the axis having the given unit.
     * @param unit The unit to test for compatibility.
     * @return {@code true} if the given unit is compatible with this coordinate system.
     *
     * @since 2.2
     */
    protected boolean isCompatibleUnit(final AxisDirection direction, final Unit<?> unit) {
        return true;
    }

    /**
     * Returns the dimension of the coordinate system.
     * This is the number of axis.
     */
    @Override
    public int getDimension() {
        return axis.length;
    }

    /**
     * Returns the axis for this coordinate system at the specified dimension.
     *
     * @param  dimension The zero based index of axis.
     * @return The axis at the specified dimension.
     * @throws IndexOutOfBoundsException if {@code dimension} is out of bounds.
     */
    @Override
    public CoordinateSystemAxis getAxis(final int dimension) throws IndexOutOfBoundsException {
        return axis[dimension];
    }

    /**
     * Returns the axis direction for the specified coordinate system.
     *
     * @param  cs The coordinate system.
     * @return The axis directions for the specified coordinate system.
     */
    private static AxisDirection[] getAxisDirections(final CoordinateSystem cs) {
        final AxisDirection[] axis = new AxisDirection[cs.getDimension()];
        for (int i=0; i<axis.length; i++) {
            axis[i] = cs.getAxis(i).getDirection();
        }
        return axis;
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
     */
    public static Matrix swapAndScaleAxis(final CoordinateSystem sourceCS,
                                          final CoordinateSystem targetCS)
            throws IllegalArgumentException, ConversionException
    {
        if (!Classes.implementSameInterfaces(sourceCS.getClass(), targetCS.getClass(), CoordinateSystem.class)) {
            throw new IllegalArgumentException(Errors.format(
                      Errors.Keys.INCOMPATIBLE_COORDINATE_SYSTEM_TYPE));
        }
        final AxisDirection[] sourceAxis = getAxisDirections(sourceCS);
        final AxisDirection[] targetAxis = getAxisDirections(targetCS);
        final GeneralMatrix matrix = new GeneralMatrix(sourceAxis, targetAxis);
        assert Arrays.equals(sourceAxis, targetAxis) == matrix.isIdentity() : matrix;
        /*
         * The previous code computed a matrix for swapping axis. Usually, this
         * matrix contains only 0 and 1 values with only one "1" value by row.
         * For example, the matrix operation for swapping x and y axis is:
         *          ┌ ┐   ┌         ┐ ┌ ┐
         *          │y│   │ 0  1  0 │ │x│
         *          │x│ = │ 1  0  0 │ │y│
         *          │1│   │ 0  0  1 │ │1│
         *          └ ┘   └         ┘ └ ┘
         * Now, take in account units conversions. Each matrix's element (j,i)
         * is multiplied by the conversion factor from sourceCS.getUnit(i) to
         * targetCS.getUnit(j). This is an element-by-element multiplication,
         * not a matrix multiplication. The last column is processed in a special
         * way, since it contains the offset values.
         */
        final int sourceDim = matrix.getNumCol()-1;
        final int targetDim = matrix.getNumRow()-1;
        assert sourceDim == sourceCS.getDimension() : sourceCS;
        assert targetDim == targetCS.getDimension() : targetCS;
        for (int j=0; j<targetDim; j++) {
            final Unit<?> targetUnit = targetCS.getAxis(j).getUnit();
            for (int i=0; i<sourceDim; i++) {
                final double element = matrix.getElement(j,i);
                if (element == 0) {
                    // There is no dependency between source[i] and target[j]
                    // (i.e. axis are orthogonal).
                    continue;
                }
                final Unit<?> sourceUnit = sourceCS.getAxis(i).getUnit();
                if (Objects.equals(sourceUnit, targetUnit)) {
                    // There is no units conversion to apply
                    // between source[i] and target[j].
                    continue;
                }
                final UnitConverter converter = sourceUnit.getConverterToAny(targetUnit);
                if (!(converter instanceof LinearConverter)) {
                    throw new ConversionException(Errors.format(
                              Errors.Keys.NON_LINEAR_UNIT_CONVERSION_$2, sourceUnit, targetUnit));
                }
                final double offset = converter.convert(0);
                final double scale  = Units.derivative(converter, 0);
                matrix.setElement(j,i, element*scale);
                matrix.setElement(j,sourceDim, matrix.getElement(j,sourceDim) + element*offset);
            }
        }
        return matrix;
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
     */
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
    final void ensureDimensionMatch(final String name, final double[] coordinates)
            throws MismatchedDimensionException
    {
        if (coordinates.length != axis.length) {
            throw new MismatchedDimensionException(Errors.format(
                    Errors.Keys.MISMATCHED_DIMENSION_$3,
                    name, coordinates.length, axis.length));
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
    public Measure distance(final double[] coord1, final double[] coord2)
            throws UnsupportedOperationException, MismatchedDimensionException
    {
        ensureDimensionMatch("coord1", coord1);
        ensureDimensionMatch("coord2", coord2);
        if (axis.length == 1) {
            return new Measure(Math.abs(coord1[0] - coord2[0]), axis[0].getUnit());
        }
        throw new UnsupportedOperationException(Errors.format(Errors.Keys.UNSUPPORTED_COORDINATE_SYSTEM_$1, getClass()));
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
    final CoordinateSystemAxis[] axisUsingUnit(final Unit<?> unit, final Unit<?> ignore) throws ConversionException {
        CoordinateSystemAxis[] newAxis = null;
        for (int i=0; i<axis.length; i++) {
            final CoordinateSystemAxis a = axis[i];
            final Unit<?> current = a.getUnit();
            if (!unit.equals(current) && (ignore == null || !ignore.equals(unit.toSI()))) {
                final DefaultCoordinateSystemAxis converted =
                        DefaultCoordinateSystemAxis.castOrCopy(a).usingUnit(unit);
                if (converted != a) {
                    if (newAxis == null) {
                        newAxis = new CoordinateSystemAxis[axis.length];
                        System.arraycopy(axis, 0, newAxis, 0, i);
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
    final boolean axisColinearWith(final CoordinateSystem userCS) {
        if (userCS.getDimension() != getDimension()) {
            return false;
        }
        final DefaultCoordinateSystemAxis[] axis0 = getDefaultAxis(this);
        final DefaultCoordinateSystemAxis[] axis1 = getDefaultAxis(userCS);
next:   for (int i=0; i<axis0.length; i++) {
            final DefaultCoordinateSystemAxis direct   = axis0[i];
            final DefaultCoordinateSystemAxis opposite = direct.getOpposite();
            for (int j=0; j<axis1.length; j++) {
                final DefaultCoordinateSystemAxis candidate = axis1[j];
                if (candidate != null) {
                    if (candidate.equals(direct,   false, false) || (opposite != null &&
                        candidate.equals(opposite, false, false)))
                    {
                        axis1[j] = null; // Flags as already compared.
                        continue next;
                    }
                }
            }
            return false;
        }
        assert directionColinearWith(userCS);
        return true;
    }

    /**
     * Compares directions only, without consideration for the axis name.
     */
    final boolean directionColinearWith(final CoordinateSystem userCS) {
        if (userCS.getDimension() != axis.length) {
            return false;
        }
        final AxisDirection[] checks = new AxisDirection[axis.length];
        for (int i=0; i<checks.length; i++) {
            checks[i] = AxisDirections.absolute(userCS.getAxis(i).getDirection());
        }
next:   for (int i=0; i<axis.length; i++) {
            final AxisDirection direction = AxisDirections.absolute(axis[i].getDirection());
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
     * Compares the specified object with this coordinate system for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true; // Slight optimization.
        }
        if (super.equals(object, mode)) {
            switch (mode) {
                case STRICT: {
                    final AbstractCS that = (AbstractCS) object;
                    return Arrays.equals(this.axis, that.axis);
                }
                default: {
                    final int dimension = getDimension();
                    final CoordinateSystem that = (CoordinateSystem) object;
                    if (dimension == that.getDimension()) {
                        for (int i=0; i<dimension; i++) {
                            if (!deepEquals(getAxis(i), that.getAxis(i), mode)) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(Arrays.hashCode(axis), super.computeHashCode());
    }

    /**
     * Format the inner part of a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
     * Known Text</cite> (WKT)</A> element. Note that WKT is not yet defined for coordinate system.
     * Current implementation list the axis contained in this CS.
     *
     * @param  formatter The formatter to use.
     * @return The WKT element name. Current implementation default to the class name.
     */
    @Override
    public String formatWKT(final Formatter formatter) {
        for (int i=0; i<axis.length; i++) {
            formatter.append(axis[i]);
        }
        formatter.setInvalidWKT(CoordinateSystem.class);
        return super.formatWKT(formatter);
    }
}
