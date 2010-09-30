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

import java.util.List;
import java.util.Arrays;

import org.opengis.referencing.cs.TimeCS;
import org.opengis.referencing.cs.VerticalCS;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.EllipsoidalCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.coverage.grid.GridGeometry;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.referencing.operation.matrix.XMatrix;
import org.geotoolkit.referencing.operation.matrix.MatrixFactory;


/**
 * Factory methods for creating {@link DiscreteCoordinateSystemAxis} and derived objects.
 * Every {@code createXXX} methods provided in this class wrap an existing referencing object
 * and add discrete behavior to it.
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
     *
     * @param  axis      The axis to wrap.
     * @param  ordinates The ordinate values. This array is <strong>not</strong> cloned.
     * @return A discrete coordinate system axis wrapping the given axis.
     */
    public static DiscreteCoordinateSystemAxis createDiscreteAxis(CoordinateSystemAxis axis, final double... ordinates) {
        ensureNonNull("axis", axis);
        ensureNonNull("ordinates", ordinates);
        if (axis instanceof DiscreteAxis) {
            final DiscreteAxis candidate = (DiscreteAxis) axis;
            if (Arrays.equals(ordinates, candidate.ordinates)) {
                return candidate;
            }
            axis = candidate.axis;
        }
        return new DiscreteAxis(axis, ordinates);
    }

    /**
     * Creates a new CS instance wrapping the given CS with the given ordinate values for each axis.
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
        if (cs instanceof DiscreteCS) {
            final DiscreteCS candidate = (DiscreteCS) cs;
            if (candidate.useOrdinates(ordinates)) {
                return candidate;
            }
            cs = candidate.cs;
        }
        if (cs instanceof CartesianCS)   return new DiscreteCS.Cartesian  ((CartesianCS)   cs, ordinates);
        if (cs instanceof EllipsoidalCS) return new DiscreteCS.Ellipsoidal((EllipsoidalCS) cs, ordinates);
        if (cs instanceof VerticalCS)    return new DiscreteCS.Vertical   ((VerticalCS)    cs, ordinates);
        if (cs instanceof TimeCS)        return new DiscreteCS.Time       ((TimeCS)        cs, ordinates);
        return new DiscreteCS(cs, ordinates);
    }

    /**
     * Creates a new CRS instance wrapping the given CRS with the given ordinate values for each axis.
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
    public static CoordinateReferenceSystem createDiscreteCRS(CoordinateReferenceSystem crs, final double[]... ordinates)
            throws IllegalArgumentException
    {
        ensureNonNull("crs", crs);
        ensureNonNull("ordinates", ordinates);
        if (crs instanceof DiscreteCRS<?>) {
            final DiscreteCRS<?> candidate = (DiscreteCRS<?>) crs;
            if (candidate.cs.useOrdinates(ordinates)) {
                return candidate;
            }
            crs = candidate.crs;
        }
        if (crs instanceof GeographicCRS) return new DiscreteCRS.Geographic((GeographicCRS) crs, ordinates);
        if (crs instanceof ProjectedCRS)  return new DiscreteCRS.Projected ((ProjectedCRS)  crs, ordinates);
        if (crs instanceof VerticalCRS)   return new DiscreteCRS.Vertical  ((VerticalCRS)   crs, ordinates);
        if (crs instanceof TemporalCRS)   return new DiscreteCRS.Temporal  ((TemporalCRS)   crs, ordinates);
        if (crs instanceof CompoundCRS) {
            final CompoundCRS compound = (CompoundCRS) crs;
            final List<CoordinateReferenceSystem> components = compound.getComponents();
            final DiscreteCRS<?>[] discretes = new DiscreteCRS<?>[components.size()];
            int lower = 0;
            for (int i=0; i<discretes.length; i++) {
                final CoordinateReferenceSystem component = components.get(i);
                final int upper = lower + component.getCoordinateSystem().getDimension();
                discretes[i] = (DiscreteCRS<?>) createDiscreteCRS(component,
                        Arrays.copyOfRange(ordinates, lower, upper));
                lower = upper;
            }
            return new DiscreteCRS.Compound(compound, discretes);
        }
        return new DiscreteCRS<CoordinateReferenceSystem>(crs, new DiscreteCS(crs.getCoordinateSystem(), ordinates));
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
            if (n > 0) {
                final Comparable<?> first, last;
                if ((first = axis.getOrdinateAt(0)) instanceof Number &&
                    (last  = axis.getOrdinateAt(n)) instanceof Number)
                {
                    final double start = ((Number) first).doubleValue();
                    final double scale = (((Number) last).doubleValue() - start) / n;
                    if (!Double.isNaN(scale) && scale != 0) {
                        matrix.setElement(i, i, scale);
                        matrix.setElement(i, dimension, start);
                        continue; // Set other matrix coefficients.
                    }
                }
            }
            return null; // Not numeric axes, or not enough ordinate values.
        }
        return matrix;
    }
}
