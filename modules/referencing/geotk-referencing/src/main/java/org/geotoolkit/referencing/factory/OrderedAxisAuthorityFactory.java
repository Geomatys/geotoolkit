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
package org.geotoolkit.referencing.factory;

import java.util.Arrays;
import java.util.Comparator;
import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.CRSAuthorityFactory;

import org.apache.sis.measure.Units;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.AuthorityFactoryFinder;
import org.geotoolkit.factory.FactoryRegistryException;
import org.apache.sis.internal.referencing.AxisDirections;
import org.geotoolkit.referencing.cs.Axes;
import org.geotoolkit.resources.Errors;
import org.apache.sis.measure.Angle;
import org.apache.sis.referencing.cs.CoordinateSystems;


/**
 * An authority factory which delegates all the work to an other factory, and reorder the axis in
 * some pre-determined order. This factory exists primarily for compatibility with external data
 * (for example Shapefiles) or applications (for example old WMS specifications) expecting
 * geographic coordinates in (<var>longitude</var>, <var>latitude</var>) order, while most
 * geographic CRS specified in the <A HREF="http://www.epsg.org">EPSG database</A> use the
 * opposite axis order.
 * <p>
 * The axis order can be specified at construction time as an array of {@linkplain AxisDirection
 * axis directions}. If no such array is explicitly specified, then the default order is:
 *
 * <blockquote>
 * {@link AxisDirection#EAST             EAST},
 * {@link AxisDirection#EAST_NORTH_EAST  EAST_NORTH_EAST},
 * {@link AxisDirection#NORTH_EAST       NORTH_EAST},
 * {@link AxisDirection#NORTH_NORTH_EAST NORTH_NORTH_EAST},
 * {@link AxisDirection#NORTH            NORTH},
 * {@link AxisDirection#UP               UP},
 * {@link AxisDirection#GEOCENTRIC_X     GEOCENTRIC_X},
 * {@link AxisDirection#GEOCENTRIC_Y     GEOCENTRIC_Y},
 * {@link AxisDirection#GEOCENTRIC_Z     GEOCENTRIC_Z},
 * {@link AxisDirection#COLUMN_POSITIVE  COLUMN_POSITIVE},
 * {@link AxisDirection#ROW_POSITIVE     ROW_POSITIVE},
 * {@link AxisDirection#DISPLAY_RIGHT    DISPLAY_RIGHT},
 * {@link AxisDirection#DISPLAY_UP       DISPLAY_UP} and
 * {@link AxisDirection#FUTURE           FUTURE}.
 * </blockquote>
 *
 * This means that, for example, axis with East or West direction will be placed before any
 * axis with North or South direction. Axis directions not specified in the table (for example
 * {@link AxisDirection#OTHER OTHER}) will be ordered last, as a <cite>right-handed</cite>
 * coordinate system if possible.
 * <p>
 * <strong>Notes:</strong>
 * <ul>
 *   <li>Colinear axis directions are considered equivalent.
 *       For example North and South are considered equivalent.</li>
 *   <li>The default direction order may changes in future Geotk version in order
 *       to fit what appears to be the most common usage on the market.</li>
 *   <li>The actual axis ordering is determined by the {@link #compare compare} method
 *       implementation. Subclasses may override this method if the want to provide a more
 *       sophesticated axis ordering.</li>
 *   <li>This class is named <cite>ordered axis authority factory</cite> instead of something like
 *       <cite>longitude first axis order</cite> because the axis order can be user-supplied. The
 *       (<var>longitude</var>, <var>latitude</var>) order just appears to be the default one.</li>
 * </ul>
 *
 * {@section Getting an instance}
 * For some authority factories, an instance of this class can be obtained by passing a
 * {@link Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER FORCE_LONGITUDE_FIRST_AXIS_ORDER} hint
 * to the <code>{@linkplain AuthorityFactoryFinder#getCRSAuthorityFactory
 * FactoryFinder.getCRSAuthorityFactory}(...)</code> method. Whatever this hint is supported
 * or not is authority dependent. Example:
 *
 * {@preformat java
 *     Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
 *     CRSAuthorityFactory factory = FactoryFinder.getCRSAuthorityFactory("EPSG", hints);
 *     CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem("EPSG:4326");
 * }
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.12
 *
 * @see Hints#FORCE_LONGITUDE_FIRST_AXIS_ORDER
 * @see Hints#FORCE_STANDARD_AXIS_UNITS
 *
 * @since 2.2
 * @module
 */
public class OrderedAxisAuthorityFactory extends TransformedAuthorityFactory
        implements CSAuthorityFactory, CRSAuthorityFactory, Comparator<CoordinateSystemAxis>
{
    /**
     * The default order for axis directions. Note that this array needs to contain only the
     * "{@linkplain AxisDirections#absolute absolute}" directions.
     * <p>
     * REMINDER: If this array is modified, don't forget to update the class javadoc above.
     */
    private static final AxisDirection[] DEFAULT_ORDER = {
        AxisDirection.EAST,
        AxisDirection.EAST_NORTH_EAST,
        AxisDirection.NORTH_EAST,
        AxisDirection.NORTH_NORTH_EAST,
        AxisDirection.NORTH,
        AxisDirection.UP,
        AxisDirection.GEOCENTRIC_X,
        AxisDirection.GEOCENTRIC_Y,
        AxisDirection.GEOCENTRIC_Z,
        AxisDirection.COLUMN_POSITIVE,
        AxisDirection.ROW_POSITIVE,
        AxisDirection.DISPLAY_RIGHT,
        AxisDirection.DISPLAY_DOWN,
        AxisDirection.FUTURE
    };

    /**
     * The rank to be given to each axis direction. The rank is stored at the indice
     * corresponding to the direction {@linkplain AxisDirection#ordinal ordinal} value.
     */
    private final int[] directionRanks;

    /**
     * {@code true} if this authority factory should also force the axis to their standard
     * direction. For example if {@code true}, then axis with increasing values toward South
     * will be converted to axis with increasing values toward North. The default value is
     * {@code false}.
     *
     * @see Hints#FORCE_STANDARD_AXIS_DIRECTIONS
     *
     * @since 2.3
     */
    protected final boolean forceStandardDirections;

    /**
     * {@code true} if this authority factory should also force all angular units to
     * decimal degrees and linear units to meters. The default value is {@code false}.
     *
     * @see Hints#FORCE_STANDARD_AXIS_UNITS
     *
     * @since 2.3
     */
    protected final boolean forceStandardUnits;

    /**
     * Creates a factory which will reorder the axis of all objects created by the default
     * authority factories. The factories are fetched using {@link AuthorityFactoryFinder}. This
     * constructor accepts the following hints:
     * <p>
     * <ul>
     *   <li>{@link Hints#FORCE_STANDARD_AXIS_UNITS}</li>
     *   <li>{@link Hints#FORCE_STANDARD_AXIS_DIRECTIONS}</li>
     *   <li>All hints understood by {@link AuthorityFactoryFinder}</li>
     * </ul>
     *
     * @param  authority The authority to wrap (example: {@code "EPSG"}). If {@code null},
     *         then all authority factories must be explicitly specified in the set of hints.
     * @param  userHints An optional set of hints, or {@code null} if none.
     * @param  axisOrder An array of axis directions that determine the axis order wanted,
     *         or {@code null} for the default axis order.
     * @throws FactoryRegistryException if at least one factory can not be obtained.
     * @throws IllegalArgumentException If at least two axis directions are colinear.
     *
     * @since 2.3
     */
    public OrderedAxisAuthorityFactory(final String authority, final Hints userHints,
            final AxisDirection[] axisOrder) throws FactoryRegistryException, IllegalArgumentException
    {
        super(authority, userHints);
        forceStandardUnits      = booleanValue(userHints, Hints.FORCE_STANDARD_AXIS_UNITS);
        forceStandardDirections = booleanValue(userHints, Hints.FORCE_STANDARD_AXIS_DIRECTIONS);
        directionRanks          = computeDirectionRanks(axisOrder);
        if (isInitialized()) {
            putCustomHints();
        }
    }

    /**
     * Creates a factory which will reorder the axis of all objects created by the supplied
     * factory. This constructor accepts the following optional hints:
     * <p>
     * <ul>
     *   <li>{@link Hints#FORCE_STANDARD_AXIS_UNITS}</li>
     *   <li>{@link Hints#FORCE_STANDARD_AXIS_DIRECTIONS}</li>
     * </ul>
     *
     * @param  factory   The factory that produces objects using arbitrary axis order.
     * @param  userHints An optional set of hints, or {@code null} if none.
     * @param  axisOrder An array of axis directions that determine the axis order wanted,
     *                   or {@code null} for the default axis order.
     * @throws IllegalArgumentException If at least two axis directions are colinear.
     *
     * @since 2.3
     */
    public OrderedAxisAuthorityFactory(final AbstractAuthorityFactory factory, final Hints userHints,
            final AxisDirection[] axisOrder) throws IllegalArgumentException
    {
        super(factory);
        forceStandardUnits      = booleanValue(userHints, Hints.FORCE_STANDARD_AXIS_UNITS);
        forceStandardDirections = booleanValue(userHints, Hints.FORCE_STANDARD_AXIS_DIRECTIONS);
        directionRanks          = computeDirectionRanks(axisOrder);
        if (isInitialized()) {
            putCustomHints();
        }
    }

    /**
     * Returns the boolean value for the specified hint.
     */
    private static boolean booleanValue(final Hints userHints, final Hints.Key key) {
        if (userHints != null) {
            final Boolean value = (Boolean) userHints.get(key);
            if (value != null) {
                return value.booleanValue();
            }
        }
        return false;
    }

    /**
     * Completes the set of hints according the value currently set in this object.
     * This method is invoked by constructors, or at initialization time if the search
     * for factories has been deferred.
     */
    @Override
    final void putCustomHints() {
        hints.put(Hints.FORCE_STANDARD_AXIS_UNITS,      Boolean.valueOf(forceStandardUnits));
        hints.put(Hints.FORCE_STANDARD_AXIS_DIRECTIONS, Boolean.valueOf(forceStandardDirections));
        // The following hint has no effect on this class behaviour,
        // but tells to the user what this factory do about axis order.
        if (compare(Axes.EASTING, Axes.NORTHING) < 0) {
            hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        }
    }

    /**
     * Modifies the given hints before to search for a backing store factory. This method is
     * invoked when first needed with copy of the hints given to the constructor. They are
     * the hints which will be given to {@code AuthorityFactoryFinder} for searching a factory.
     * <p>
     * This method sets to {@code FALSE} the hints related to axis directions and units since
     * those properties are handled by {@code OrderedAxisAuthorityFactory} and we don't want
     * the backing store to interfer with that.
     */
    @Override
    final void toBackingFactoryHints(final Hints hints) {
        hints.put(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.FALSE);
        hints.put(Hints.FORCE_STANDARD_AXIS_DIRECTIONS,   Boolean.FALSE);
        hints.put(Hints.FORCE_STANDARD_AXIS_UNITS,        Boolean.FALSE);
    }

    /**
     * Computes the rank for every direction in the specified. The rank is stored in an array
     * at the index corresponding to the direction {@linkplain AxisDirection#ordinal ordinal}
     * value. This method is used by constructors for computing the {@link #directionRanks} field.
     *
     * @throws IllegalArgumentException If at least two axis directions are colinear.
     */
    private static int[] computeDirectionRanks(AxisDirection[] axisOrder)
            throws IllegalArgumentException
    {
        if (axisOrder == null) {
            axisOrder = DEFAULT_ORDER;
        }
        int length = 0;
        for (int i=0; i<axisOrder.length; i++) {
            final int ordinal = AxisDirections.absolute(axisOrder[i]).ordinal() + 1;
            if (ordinal > length) {
                length = ordinal;
            }
        }
        final int[] directionRanks = new int[length];
        Arrays.fill(directionRanks, length);
        for (int i=0; i<axisOrder.length; i++) {
            final int ordinal  = AxisDirections.absolute(axisOrder[i]).ordinal();
            final int previous = directionRanks[ordinal];
            if (previous != length) {
                throw new IllegalArgumentException(Errors.format(Errors.Keys.COLINEAR_AXIS_2,
                        axisOrder[previous].name(), axisOrder[i].name()));
            }
            directionRanks[ordinal] = i;
        }
        return directionRanks;
    }

    /**
     * Returns the rank for the specified axis. Any axis that were not specified
     * at construction time will ordered after all known axis.
     */
    private int rank(final AxisDirection axis) {
        int c = AxisDirections.absolute(axis).ordinal();
        c = (c >= 0 && c < directionRanks.length) ? directionRanks[c] : directionRanks.length;
        return c;
    }

    /**
     * Compares two axis for order. This method is invoked automatically by the
     * {@link #replace(CoordinateSystem) replace} method for ordering the axis in a
     * coordinate system.
     * <p>
     * The default implementation orders the axis according their
     * {@linkplain CoordinateSystemAxis#getDirection direction}, using the direction
     * table given at {@linkplain #OrderedAxisAuthorityFactory(AbstractAuthorityFactory,
     * Hints, AxisDirection[]) construction time} (see also the class description) first,
     * or in an order forming a <cite>right-handed</cite> coordinate system if the axis
     * direction was not specified at construction time. Subclasses can override this
     * method if they want to define an other axis ordering.
     *
     * @param  axis1 The first axis to compare.
     * @param  axis2 The second axis to compare.
     * @return A negative integer if {@code axis1} should appears before {@code axis2}, or a
     *         positive number if {@code axis2} should appears before {@code axis1}, or 0 if
     *         the two axis are unordered one relative to the other.
     *
     * @since 2.3
     */
    @Override
    public int compare(final CoordinateSystemAxis axis1, final CoordinateSystemAxis axis2) {
        final AxisDirection dir1 = axis1.getDirection();
        final AxisDirection dir2 = axis2.getDirection();
        final int rank = rank(dir1);
        int c = rank - rank(dir2);
        if (c == 0 && rank == directionRanks.length) {
            // Both axes are not in the list of known directions.
            // We may have a more complex direction like "South along 90°E".
            final Angle angle = CoordinateSystems.angle(dir1, dir2);
            if (angle != null) {
                final double θ = angle.degrees();
                if (θ > 0) return -1; // Right handed coordinate system
                if (θ < 0) return +1; // Left handed coordinate system
                // Otherwise leave unchanged.
            }
        }
        return c;
    }

    /**
     * Replaces the specified unit, if applicable. This method is invoked automatically by the
     * {@link #replace(CoordinateSystem)} method. The default implementation replaces the unit
     * only if the {@link Hints#FORCE_STANDARD_AXIS_UNITS FORCE_STANDARD_AXIS_UNITS} hint was
     * specified as {@link Boolean#TRUE TRUE} at construction time. In such case, the default
     * substitution table is:
     * <p>
     * <ul>
     *   <li>Any {@linkplain Units#isLinear linear units} converted to
     *       {@linkplain SI#METRE metres}</li>
     *   <li>Any {@linkplain Units#isAngular angular units} converted to
     *       {@linkplain NonSI#DEGREE_ANGLE decimal degrees}</li>
     *   <li>Any {@linkplain Units#isTemporal temporal units} converted to
     *       {@linkplain NonSI#DAY days}</li>
     * </ul>
     *
     * @since 2.3
     */
    @Override
    protected Unit<?> replace(final Unit<?> units) {
        if (forceStandardUnits) {
            if (Units.isLinear(units)) {
                return SI.METRE;
            }
            if (Units.isAngular(units)) {
                return NonSI.DEGREE_ANGLE;
            }
            if (Units.isTemporal(units)) {
                return NonSI.DAY;
            }
        }
        return units;
    }

    /**
     * Replaces the specified direction, if applicable. This method is invoked automatically by the
     * {@link #replace(CoordinateSystem)} method. The default implementation replaces the direction
     * only if the {@link Hints#FORCE_STANDARD_AXIS_DIRECTIONS FORCE_STANDARD_AXIS_DIRECTIONS} hint
     * was specified as {@link Boolean#TRUE TRUE} at construction time.
     *
     * @since 2.3
     */
    @Override
    protected AxisDirection replace(final AxisDirection direction) {
        return (forceStandardDirections) ? AxisDirections.absolute(direction) : direction;
    }
}
