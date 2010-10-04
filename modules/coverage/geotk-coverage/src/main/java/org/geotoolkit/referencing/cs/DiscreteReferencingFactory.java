/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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

import java.util.Date;
import java.util.Arrays;
import javax.measure.unit.Unit;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Duration;

import org.opengis.referencing.cs.TimeCS;
import org.opengis.referencing.cs.VerticalCS;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.coverage.grid.GridGeometry;

import org.geotoolkit.lang.Static;
import org.geotoolkit.measure.Units;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.referencing.operation.matrix.XMatrix;
import org.geotoolkit.referencing.operation.matrix.MatrixFactory;


/**
 * Factory methods for creating {@link DiscreteCoordinateSystemAxis} and derived objects.
 * Every {@code createXXX(...)} methods provided in this class wrap an existing referencing
 * object and add discrete behavior to it.
 * <p>
 * <b>IMPORTANT NOTE:</b><br>
 * In current implementation, every factory methods defined in this class do <strong>not</strong>
 * clone the given ordinate arrays, because those arrays may be potentially large and the caller
 * way want to share the reference to some of them. <em>It is caller responsibility to not change
 * the ordinate arrays after they have been passed to factory methods</em>.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
@Static
public final class DiscreteReferencingFactory {
    /**
     * Do not allow instantiation of this class.
     */
    private DiscreteReferencingFactory() {
    }

    /**
     * Makes sure that an argument is non-null.
     */
    private static void ensureNonNull(final String name, final Object object) throws NullArgumentException {
        if (object == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
    }

    /**
     * Creates a new discrete axis wrapping the given axis with the given ordinate values.
     * If the given axis is already an instance of {@code DiscreteCoordinateSystemAxis} having
     * the given ordinates values (or the ordinates array is {@code null}), then that instance
     * is returned directly.
     *
     * @param  axis      The axis to wrap.
     * @param  ordinates The ordinate values. This array is <strong>not</strong> cloned.
     * @return A discrete coordinate system axis wrapping the given axis.
     */
    public static DiscreteCoordinateSystemAxis createDiscreteAxis(CoordinateSystemAxis axis, final double... ordinates) {
        ensureNonNull("axis", axis);
        if (canReuse(axis, ordinates)) {
            return (DiscreteCoordinateSystemAxis) axis;
        }
        if (axis instanceof DiscreteAxis) {
            axis = ((DiscreteAxis) axis).axis;
            if (canReuse(axis, ordinates)) {
                return (DiscreteCoordinateSystemAxis) axis;
            }
        }
        ensureNonNull("ordinates", ordinates);
        return new DiscreteAxis(axis, ordinates);
    }

    /**
     * Returns a CS instance wrapping the given CS with the given ordinate values for each axis.
     * If the given CS already have discrete axes with the given ordinate values, then it is
     * returned directly.
     *
     * {@section Grid geometry}
     * The instance returned by this method implements the {@link GridGeometry} interface. However
     * the <cite>grid to CRS</cite> transform is meaningful only if the ordinate values in the given
     * arrays are regularly spaced.
     *
     * @param  cs  The coordinate system to wrap.
     * @param  ordinates The ordinate values for each axis. The arrays are <strong>not</strong> cloned.
     * @return A new coordinate system wrapping the given one with discrete axes.
     * @throws IllegalArgumentException If the length of the {@code ordinates} array is not equals
     *         to the {@linkplain CoordinateSystem#getDimension() dimension} of the given coordinate
     *         system.
     */
    public static CoordinateSystem createDiscreteCS(CoordinateSystem cs, final double[]... ordinates)
            throws IllegalArgumentException
    {
        ensureNonNull("cs", cs);
        ensureNonNull("ordinates", ordinates);
        if (canReuse(cs, ordinates)) {
            return cs;
        }
        if (cs instanceof DiscreteCS) {
            cs = ((DiscreteCS) cs).cs;
            if (canReuse(cs, ordinates)) {
                return cs;
            }
        }
        if (cs instanceof CartesianCS)   return new DiscreteCS.Cartesian  ((CartesianCS)   cs, ordinates);
        if (cs instanceof EllipsoidalCS) return new DiscreteCS.Ellipsoidal((EllipsoidalCS) cs, ordinates);
        if (cs instanceof VerticalCS)    return new DiscreteCS.Vertical   ((VerticalCS)    cs, ordinates);
        if (cs instanceof TimeCS)        return new DiscreteCS.Time       ((TimeCS)        cs, ordinates);
        return new DiscreteCS(cs, ordinates);
    }

    /**
     * Returns a CRS instance wrapping the given CRS with the given ordinate values for each axis.
     * If the coordinate system of the given CRS already have discrete axes with the given ordinate
     * values, then the CRS is returned directly.
     *
     * {@section Grid geometry}
     * The instance returned by this method implements the {@link GridGeometry} interface. However
     * the <cite>grid to CRS</cite> transform is meaningful only if the ordinate values in the given
     * arrays are regularly spaced.
     *
     * @param  crs  The coordinate reference system to wrap.
     * @param  ordinates The ordinate values for each axis. The arrays are <strong>not</strong> cloned.
     * @return A new coordinate reference system wrapping the given one with discrete axes.
     * @throws IllegalArgumentException If the length of the {@code ordinates} array is not equals
     *         to the coordinate system {@linkplain CoordinateSystem#getDimension() dimension}.
     */
    public static CoordinateReferenceSystem createDiscreteCRS(CoordinateReferenceSystem crs, double[]... ordinates)
            throws IllegalArgumentException
    {
        ensureNonNull("crs", crs);
        ensureNonNull("ordinates", ordinates);
        if (crs instanceof CompoundCRS) {
            ordinates = replaceNulls((CompoundCRS) crs, ordinates, ordinates, 0);
        }
        if (canReuse(crs.getCoordinateSystem(), ordinates)) {
            return crs;
        }
        if (crs instanceof DiscreteCRS<?>) {
            crs = ((DiscreteCRS<?>) crs).crs;
            if (canReuse(crs.getCoordinateSystem(), ordinates)) {
                return crs;
            }
        }
        if (crs instanceof GeographicCRS) return new DiscreteCRS.Geographic((GeographicCRS) crs, ordinates);
        if (crs instanceof ProjectedCRS)  return new DiscreteCRS.Projected ((ProjectedCRS)  crs, ordinates);
        if (crs instanceof VerticalCRS)   return new DiscreteCRS.Vertical  ((VerticalCRS)   crs, ordinates);
        if (crs instanceof TemporalCRS)   return new DiscreteCRS.Temporal  ((TemporalCRS)   crs, ordinates);
        if (crs instanceof CompoundCRS)   return DiscreteCompoundCRS.create((CompoundCRS)   crs, ordinates);
        return new DiscreteCRS<CoordinateReferenceSystem>(crs, new DiscreteCS(crs.getCoordinateSystem(), ordinates));
    }

    /**
     * Returns {@code true} if the given coordinate system uses the given ordinate values for each
     * axis. If an ordinate array is null, it will be interpreted as "no change in ordinate values"
     * (i.e. the existing discrete axis will be kept unchanged).
     */
    private static boolean canReuse(final CoordinateSystem cs, final double[][] ordinates) {
        final int dimension = cs.getDimension();
        if (ordinates.length != dimension) {
            return false;
        }
        for (int i=0; i<dimension; i++) {
            if (!canReuse(cs.getAxis(i), ordinates[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the given axis uses the given ordinate values. If the
     * ordinate array is null, it will be interpreted as "no change in ordinate values"
     * (i.e. the existing discrete axis will be kept unchanged).
     */
    private static boolean canReuse(final CoordinateSystemAxis axis, final double[] ordinates) {
        if (!(axis instanceof DiscreteCoordinateSystemAxis)) {
            return false;
        }
        if (ordinates != null) {
            /*
             * Check if the specified ordinate values are the same than the ones
             * already declared in the axis. In such case, keep the axis instance.
             */
            if (axis instanceof DiscreteAxis) {
                // Optimized case for the DiscreteAxis case (direct array comparison).
                return Arrays.equals(((DiscreteAxis) axis).ordinates, ordinates);
            }
            final DiscreteCoordinateSystemAxis dx = (DiscreteCoordinateSystemAxis) axis;
            if (dx.length() != ordinates.length) {
                return false;
            }
            for (int i=0; i<ordinates.length; i++) {
                final Comparable<?> ordinate = dx.getOrdinateAt(i);
                if (!(ordinate instanceof Number) || Double.doubleToLongBits(ordinates[i]) !=
                        Double.doubleToLongBits(((Number) ordinate).doubleValue()))
                {
                    // Found an ordinate value which is not the same.
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Replaces {@code null} values in the given array by {@link DiscreteAxis#ordinates}, when
     * possible. The {@link DiscreteAxis} instances are fetched through the {@link CompoundCRS}
     * <strong>components</strong>, not from the {@code CompoundCRS} coordinate system. This is
     * a way to ensure that the axes of components are consistent with the axis of the compound
     * CRS, because the {@link #canReuse(CoordinateSystem, double[][])} method will check the
     * later.
     * <p>
     * The only purpose of this method is to force {@code canReuse} to return {@code false} if
     * the axes of components are inconsistent with the {@code CompoundCRS} axes. It happen in
     * {@link org.geotoolkit.referencing.adapters.NetcdfCRS}, which invoke the methods in this
     * class precisely in order to fix that inconsistency.
     *
     * @param  crs           The compound CRS.
     * @param  ordinates     The ordinates arrays.
     * @param  original      The ordinates arrays (needs to be supplied twice for the internal
     *                       working of this method).
     * @param  nextDimension Index of the first element to check in {@code ordinates}.
     * @return {@code ordinates}, or a new array if at least one {@code null} element has been
     *         replaced by a non-null element.
     */
    private static double[][] replaceNulls(final CompoundCRS crs, double[][] ordinates,
            final double[][] original, int nextDimension)
    {
scan:   for (final CoordinateReferenceSystem component : crs.getComponents()) {
            final CoordinateSystem cs = component.getCoordinateSystem();
            final int dimension = cs.getDimension();
            if (component instanceof CompoundCRS) {
                ordinates = replaceNulls((CompoundCRS) component, ordinates, original, nextDimension);
            }
            for (int i=0; i<dimension; i++) {
                if (nextDimension == ordinates.length) {
                    break scan;
                }
                if (ordinates[nextDimension] == null) {
                    final CoordinateSystemAxis axis = cs.getAxis(i);
                    if (axis instanceof DiscreteAxis) {
                        if (ordinates == original) {
                            ordinates = ordinates.clone();
                        }
                        ordinates[nextDimension] = ((DiscreteAxis) axis).ordinates;
                    }
                }
                nextDimension++;
            }
        }
        return ordinates;
    }

    /**
     * Computes a <cite>grid to CRS</cite> affine transform for the given axes, mapping
     * {@linkplain org.opengis.referencing.datum.PixelInCell#CELL_CENTER cell center}.
     * Caller shall ensure that the following conditions are meet (they are not verified
     * by this method, because the threshold for considering an axis as "regular" is
     * arbitrary and at caller choice):
     * <p>
     * <ul>
     *   <li>For each axis, the ordinate values shall be sorted in strictly increasing order,
     *       or in strictly decreasing order, without {@code NaN} values.</li>
     *   <li>The axis shall be <em>regular</em>, i.e. the interval between ordinate values
     *       shall be approximatively constant.</li>
     * </ul>
     *
     * @param  axes The axes to use for computing the transform.
     * @return The <cite>grid to CRS</cite> transform mapping cell centers for the given axes
     *         as a matrix, or {@code null} if none.
     */
    public static XMatrix getAffineTransform(final DiscreteCoordinateSystemAxis... axes) {
        ensureNonNull("axes", axes);
        final int dimension = axes.length;
        final XMatrix matrix = MatrixFactory.create(dimension + 1);
        for (int i=0; i<dimension; i++) {
            final DiscreteCoordinateSystemAxis axis = axes[i];
            /*
             * Compute the mean interval between ordinate values. The interval can be negative if
             * the ordinate values are decreasing. This code assumes that this axis is reasonably
             * regular (this is not verified).
             */
            final int n = axis.length() - 1;
            if (n < 0) {
                return null;
            }
            final Comparable<?> first = axis.getOrdinateAt(0);
            final Comparable<?> last  = axis.getOrdinateAt(n);
            final double start, end;
            if (first instanceof Number && last instanceof Number) {
                start = ((Number) first).doubleValue();
                end   = ((Number) last) .doubleValue();
            } else if (first instanceof Date && last instanceof Date && axis instanceof CoordinateSystemAxis) {
                final Unit<?> unit = ((CoordinateSystemAxis) axis).getUnit();
                if (unit == null) {
                    return null;
                }
                final UnitConverter converter = Units.MILLISECOND.getConverterTo(unit.asType(Duration.class));
                start = converter.convert((double) ((Date) first).getTime());
                end   = converter.convert((double) ((Date) last) .getTime());
            } else {
                return null;
            }
            if (n != 0) {
                final double scale = (end - start) / n ;
                if (Double.isNaN(scale) || scale == 0) {
                    return null;
                }
                matrix.setElement(i, i, scale);
            }
            matrix.setElement(i, dimension, start);
        }
        return matrix;
    }
}
