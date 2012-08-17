/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.coverage.grid.GridGeometry;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.geotoolkit.util.NullArgumentException;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.operation.matrix.XMatrix;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.geotoolkit.referencing.operation.transform.LinearTransform;


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
 * @version 3.16
 *
 * @since 3.15
 * @module
 */
public final class DiscreteReferencingFactory extends Static {
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
    public static DiscreteCoordinateSystemAxis<?> createDiscreteAxis(CoordinateSystemAxis axis, final double... ordinates) {
        ensureNonNull("axis", axis);
        if (canReuse(axis, ordinates)) {
            return (DiscreteCoordinateSystemAxis<?>) axis;
        }
        if (axis instanceof DiscreteAxis) {
            axis = ((DiscreteAxis) axis).axis;
            if (canReuse(axis, ordinates)) {
                return (DiscreteCoordinateSystemAxis<?>) axis;
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
     * arrays are regularly spaced. This is not verified because the criterion for deciding if an
     * axis is "regular" is arbitrary.
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
     * arrays are regularly spaced. This is not verified because the criterion for deciding if an
     * axis is "regular" is arbitrary.
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
        return new DiscreteCRS<>(crs, new DiscreteCS(crs.getCoordinateSystem(), ordinates));
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
        if (!(axis instanceof DiscreteCoordinateSystemAxis<?>)) {
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
            final DiscreteCoordinateSystemAxis<?> dx = (DiscreteCoordinateSystemAxis<?>) axis;
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
     * Callers shall ensure that the following conditions are meet (they are not verified
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
     *         as a matrix, or {@code null} if such matrix can not be computed.
     */
    public static XMatrix getAffineTransform(final DiscreteCoordinateSystemAxis<?>... axes) {
        ensureNonNull("axes", axes);
        return getAffineTransform(null, axes);
    }

    /**
     * Computes a <cite>grid to CRS</cite> affine transform for the given CRS, mapping
     * {@linkplain org.opengis.referencing.datum.PixelInCell#CELL_CENTER cell center}.
     * This method processes with the following steps:
     * <p>
     * <ol>
     *   <li>If the given CRS implements the {@link GridGeometry} interface, then this method
     *       checks the value returned by the {@link GridGeometry#getGridToCRS()} method. If
     *       that value is a linear transform, then {@linkplain LinearTransform#getMatrix()
     *       its matrix} is returned.</li>
     *   <li>Otherwise if the given CRS is an instance of {@link CompoundCRS}, then the above
     *       check is performed for each {@linkplain CompoundCRS#getComponents() component} and
     *       the component matrix are assembled in a single matrix.</li>
     *   <li>Otherwise if all axes in the given CRS are discrete, then this method gets those axes
     *       and delegates to the {@link #getAffineTransform(DiscreteCoordinateSystemAxis[])} method.
     *       Note that the conditions documented in the above method apply.</li>
     *   <li>Otherwise this method returns {@code null}.</li>
     * </ol>
     *
     * @param  crs The Coordinate Reference System for which to get the <cite>grid to CRS</cite>
     *         affine transform.
     * @return The <cite>grid to CRS</cite> transform mapping cell centers for the CRS axes
     *         as a matrix, or {@code null} if such matrix can not be computed.
     *
     * @since 3.16
     */
    public static Matrix getAffineTransform(final CoordinateReferenceSystem crs) {
        ensureNonNull("crs", crs);
        if (crs instanceof GridGeometry) {
            final Matrix matrix = Matrices.getMatrix(((GridGeometry) crs).getGridToCRS());
            if (matrix != null) {
                return matrix;
            }
        }
        return createAffineTransform(crs);
    }

    /**
     * Implementation of {@link #getAffineTransform(CoordinateReferenceSystem)} without
     * the check for instance of {@link GridGeometry}. This method is the fallback for
     * both the above-cited method, and for {@link #getGridToCRS(GridGeometry, PixelInCell)}.
     */
    private static Matrix createAffineTransform(final CoordinateReferenceSystem crs) {
        final CoordinateSystem cs = crs.getCoordinateSystem();
        final DiscreteCoordinateSystemAxis<?>[] axes = new DiscreteCoordinateSystemAxis<?>[cs.getDimension()];
        for (int i=0; i<axes.length; i++) {
            final CoordinateSystemAxis axis = cs.getAxis(i);
            if (axis instanceof DiscreteCoordinateSystemAxis<?>) {
                axes[i] = (DiscreteCoordinateSystemAxis<?>) axis;
            }
        }
        if (crs instanceof CompoundCRS) {
            return getAffineTransform((CompoundCRS) crs, axes);
        } else {
            return getAffineTransform(crs, axes);
        }
    }

    /**
     * Implementation of {@link #getAffineTransform(DiscreteCoordinateSystemAxis[])} with an
     * optional CRS. If non-null, the temporal component of the given CRS is used in order to
     * convert dates to numerical values.
     *
     * @param  crs  The Coordinate Reference System object that own the given axes.
     * @param  axes The axes to use for computing the transform. If this array contains any
     *              null element, then this method returns {@code null}.
     * @return The <cite>grid to CRS</cite> transform mapping cell centers for the given axes
     *         as a matrix, or {@code null} if such matrix can not be computed.
     */
    static XMatrix getAffineTransform(final CoordinateReferenceSystem crs, final DiscreteCoordinateSystemAxis<?>[] axes) {
        final int dimension = axes.length;
        final XMatrix matrix = Matrices.create(dimension + 1);
        for (int i=0; i<dimension; i++) {
            final DiscreteCoordinateSystemAxis<?> axis = axes[i];
            final int n;
            if (axis == null || (n = axis.length() - 1) < 0) {
                // No discrete values.
                return null;
            }
            /*
             * Compute the mean interval between ordinate values. The interval can be negative if
             * the ordinate values are decreasing. This code assumes that this axis is reasonably
             * regular (this is not verified).
             */
            final Comparable<?> first = axis.getOrdinateAt(0);
            final Comparable<?> last  = axis.getOrdinateAt(n);
            final double start, end;
            if (first instanceof Number && last instanceof Number) {
                start = ((Number) first).doubleValue();
                end   = ((Number) last) .doubleValue();
            } else if (first instanceof Date && last instanceof Date) {
                CoordinateReferenceSystem temporalCRS = CRS.getSubCRS(crs, i, i+1);
                if (temporalCRS instanceof DiscreteCRS<?>) {
                    temporalCRS = ((DiscreteCRS<?>) temporalCRS).crs;
                }
                if (!(temporalCRS instanceof TemporalCRS)) {
                    return null;
                }
                final DefaultTemporalCRS converter = DefaultTemporalCRS.castOrCopy((TemporalCRS) temporalCRS);
                start = converter.toValue((Date) first);
                end   = converter.toValue((Date) last);
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

    /**
     * Invokes {@link #getAffineTransform(CoordinateReferenceSystem, DiscreteCoordinateSystemAxis[])},
     * then overwrite the matrix coefficients by the ones computed by the CRS components. We process
     * that way because:
     * <p>
     * <ul>
     *   <li>Some CRS component may compute their own transform in a different way. For example
     *       {@link org.geotoolkit.referencing.adapters.NetcdfCRS} returns a custom implementation
     *       if an axis is irregular.</li>
     *   <li>We invoke {@code getAffineTransform(CoordinateReferenceSystem, ...)} first in order
     *       to have at least a translation term when the component can not compute its own "grid
     *       to CRS" transform.</li>
     * </ul>
     *
     * @param  crs  The Coordinate Reference System object that own the given axes.
     * @param  axes The axes to use for computing the transform.
     * @return The <cite>grid to CRS</cite> transform mapping cell centers for the given axes
     *         as a matrix, or {@code null} if such matrix can not be computed.
     *
     * @since 3.16
     */
    static XMatrix getAffineTransform(final CompoundCRS crs, final DiscreteCoordinateSystemAxis<?>[] axes) {
        final XMatrix matrix = getAffineTransform((CoordinateReferenceSystem) crs, axes);
        if (matrix != null) {
            final int lastColumn = matrix.getNumCol() - 1;
            int rowOffset = 0;
            for (final CoordinateReferenceSystem component : crs.getComponents()) {
                final int dimension = component.getCoordinateSystem().getDimension();
                /*
                 * If the CRS is an instance of DiscreteCRS<?>, then the grid geometry computed by
                 * that CRS would have identical coefficients than the one computed above (because
                 * the DiscreteCRS.getGridToCRS() delegates to getAffineTransform(...) as we did).
                 * So it is not worth to call component.getGridToCRS() again.
                 */
                if (!(component instanceof DiscreteCRS<?>) && component instanceof GridGeometry) {
                    final MathTransform tr = ((GridGeometry) component).getGridToCRS();
                    if (tr instanceof LinearTransform) {
                        /*
                         * Copies the scale and translation terms from the matrix computed by the
                         * individual component. The matrix is usually square, but not always. So
                         * we need to check its size:
                         *
                         * - If the matrix of the sub-transform is square, then we presume that
                         *   the source ordinates are a sub-set of the full grid ordinates. So
                         *   the copy of ordinate values need to be applied at the right offset.
                         *
                         * - If the matrix of the sub-transform is not square, then we presume
                         *   that the source ordinates are the full grid ordinates. So we need
                         *   to copy the full row.
                         */
                        final Matrix sub = ((LinearTransform) tr).getMatrix();
                        final int sourceDim = tr.getSourceDimensions(); // Also the translation column.
                        final int columnOffset = (sourceDim == dimension) ? rowOffset : 0;
                        assert sub.getNumRow() == dimension + 1 : tr;
                        assert sub.getNumCol() == sourceDim + 1 : tr;
                        for (int j=0; j<dimension; j++) {
                            final int dj = j + rowOffset;
                            for (int i=0; i<sourceDim; i++) {
                                matrix.setElement(dj, i+columnOffset, sub.getElement(j, i));
                            }
                            matrix.setElement(dj, lastColumn, sub.getElement(j, sourceDim));
                        }
                    } else {
                        /*
                         * If the component considers that there is no valid grid to CRS, set
                         * the scale coefficient to NaN. The other coefficients are already 0,
                         * which is correct. The translation term is keep unchanged, because
                         * it still valid.
                         */
                        for (int j=0; j<dimension; j++) {
                            final int dj = j + rowOffset;
                            matrix.setElement(dj, dj, Double.NaN);
                        }
                    }
                }
                rowOffset += dimension;
            }
        }
        return matrix;
    }

    /**
     * Returns the <cite>grid to CRS</cite> affine transform for the given grid geometry.
     * <p>
     * <ol>
     *   <li>First, this method invokes one of the following {@code getGridToCRS()} methods:
     *     <ul>
     *       <li>{@link GeneralGridGeometry#getGridToCRS(PixelInCell)} if the given grid geometry
     *           is a compatible instance and the {@code pixelInCell} argument is non-null;</li>
     *       <li>{@link GridGeometry#getGridToCRS()} otherwise. This later method shall implicitly
     *           use {@link PixelInCell#CELL_CENTER} as per OGC 01-004 specification, but departure
     *           is possible if the user has overridden the method.</li>
     *     </ul></li>
     *   <li>If the transform returned in the above step is linear, then
     *       {@linkplain LinearTransform#getMatrix() its matrix} is returned.</li>
     *   <li>Otherwise if the given geometry is also a CRS with discrete axes (for example
     *       {@link org.geotoolkit.referencing.adapters.NetcdfCRS}), then this method performs
     *       the same calculation than {@link #getAffineTransform(CoordinateReferenceSystem)}.</li>
     *   <li>Otherwise this method returns {@code null}.</li>
     * </ol>
     *
     * @param  geometry The geometry for which to get the <cite>grid to CRS</cite> affine transform.
     * @param  pixelInCell Whatever the transform should map the cell center or corner, or {@code null}
     *         for the default (typically {@linkplain PixelInCell#CELL_CENTER cell center}).
     * @return The <cite>grid to CRS</cite> transform mapping cell centers for the CRS axes
     *         as a matrix, or {@code null} if such matrix can not be computed.
     *
     * @see GeneralGridGeometry#getGridToCRS(PixelInCell)
     * @see PixelTranslation
     *
     * @since 3.16
     */
    public static Matrix getAffineTransform(final GridGeometry geometry, PixelInCell pixelInCell) {
        ensureNonNull("geometry", geometry);
        MathTransform tr;
        if (pixelInCell != null && geometry instanceof GeneralGridGeometry) {
            tr = ((GeneralGridGeometry) geometry).getGridToCRS(pixelInCell);
            pixelInCell = null; // Indicates that the offset is already applied.
        } else {
            tr = geometry.getGridToCRS();
        }
        Matrix gridToCRS = Matrices.getMatrix(tr);
        /*
         * If the grid geometry does not define "grid to CRS" transform, or if that
         * transform is not linear, compute a transform from the discrete axes.
         */
        if (gridToCRS == null && geometry instanceof CoordinateReferenceSystem) {
            gridToCRS = createAffineTransform((CoordinateReferenceSystem) geometry);
        }
        /*
         * If the caller asked for the pixel corner rather than pixel center,
         * applies a translation of 0.5 pixel along all dimensions.
         */
        if (gridToCRS != null && pixelInCell != null) {
            final double offset = PixelTranslation.getPixelTranslation(pixelInCell);
            if (offset != 0) {
                final int lastColumn = gridToCRS.getNumCol() - 1;
                for (int j=gridToCRS.getNumRow(); --j>=0;) {
                    double sum = 0;
                    for (int i=0; i<lastColumn; i++) {
                        sum += offset * gridToCRS.getElement(j, i);
                    }
                    sum += gridToCRS.getElement(j, lastColumn); // Do it last for reducing rounding errors.
                    gridToCRS.setElement(j, lastColumn, sum);
                }
            }
        }
        return gridToCRS;
    }
}
