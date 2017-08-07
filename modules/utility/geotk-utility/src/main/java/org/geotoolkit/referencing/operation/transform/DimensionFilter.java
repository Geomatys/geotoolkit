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
 */
package org.geotoolkit.referencing.operation.transform;

import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.geotoolkit.internal.referencing.SeparableTransform;
import org.apache.sis.referencing.operation.transform.PassThroughTransform;
import org.apache.sis.referencing.operation.transform.TransformSeparator;


/**
 * A utility class for the separation of {@linkplain ConcatenatedTransform concatenation} of
 * {@linkplain PassThroughTransform pass through transforms}. Given an arbitrary
 * {@linkplain MathTransform math transform}, this utility class will returns a new math transform
 * that operates only of a given set of source dimensions. For example if the supplied
 * {@code transform} has (<var>x</var>, <var>y</var>, <var>z</var>) inputs and
 * (<var>longitude</var>, <var>latitude</var>, <var>height</var>) outputs, then
 * the following code:
 *
 * {@preformat java
 *     addSourceDimensionRange(0, 2);
 *     MathTransform mt = separate(transform);
 * }
 *
 * will returns a transform with (<var>x</var>, <var>y</var>) inputs and (probably)
 * (<var>longitude</var>, <var>latitude</var>) outputs. The later can be verified with
 * a call to {@link #getTargetDimensions}.
 *
 * @author Martin Desruisseaux (IRD)
 * @author Simone Giannecchini (Geosolutions)
 * @version 3.00
 *
 * @todo This class contains a set of static methods that could be factored out in
 *       some kind of {@code org.geotoolkit.util.SortedIntegerSet} implementation.
 *
 * @todo Consider providing a {@code subTransform(DimensionFilter)} method in
 *       {@link AbstractMathTransform}, and move some {@code DimensionFilter}
 *       code in {@code AbstractMathTransform} sub-classes. This would allow us
 *       to separate transforms that are defined in downstream modules, like NetCDF.
 *
 * @since 2.1
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link TransformSeparator}.
 */
@Deprecated
public class DimensionFilter extends TransformSeparator {
    public DimensionFilter(MathTransform transform) {
        super(transform);
    }

    public DimensionFilter(MathTransform transform, MathTransformFactory factory) {
        super(transform, factory);
    }

    /**
     * Adds an input dimension to keep. The {@code dimension} applies to the source dimensions
     * of the transform to be given to <code>{@linkplain #separate separate}(transform)</code>.
     * The number must be in the range 0 inclusive to
     * <code>transform.{@linkplain MathTransform#getSourceDimensions getSourceDimensions()}</code>
     * exclusive.
     *
     * @param  dimension The dimension to add.
     * @throws IllegalArgumentException if {@code dimension} is negative.
     */
    public void addSourceDimension(final int dimension) throws IllegalArgumentException {
        addSourceDimensions(dimension);
    }

    /**
     * Adds an output dimension to keep. The {@code dimension} applies to the target dimensions
     * of the transform to be given to <code>{@linkplain #separate separate}(transform)</code>.
     * The number must be in the range 0 inclusive to
     * <code>transform.{@linkplain MathTransform#getTargetDimensions getTargetDimensions()}</code>
     * exclusive.
     *
     * @param  dimension The dimension to add.
     * @throws IllegalArgumentException if {@code dimension} is negative.
     */
    public void addTargetDimension(final int dimension) throws IllegalArgumentException {
        addTargetDimensions(dimension);
    }

    /**
     * Separates the specified math transform. This method returns a math transform that uses
     * only the specified {@linkplain #getSourceDimensions source dimensions} and returns only
     * the specified {@linkplain #getTargetDimensions target dimensions}. Special case:
     * <p>
     * <ul>
     *   <li><p>If {@linkplain #getSourceDimensions source dimensions} are unspecified, then the
     *       returned transform will expects all source dimensions as input but will produces only
     *       the specified {@linkplain #getTargetDimensions target dimensions} as output.</p></li>
     *
     *   <li><p>If {@linkplain #getTargetDimensions target dimensions} are unspecified, then the
     *       returned transform will expects only the specified {@linkplain #getSourceDimensions
     *       source dimensions} as input, and the target dimensions will be inferred
     *       automatically.</p></li>
     * </ul>
     *
     * @return The separated math transform.
     * @throws FactoryException if the transform can't be separated.
     */
    @Override
    public MathTransform separate() throws FactoryException {
        /*
         * -------- HACK BEGINS --------
         * Special case for NetCDF transforms. Actually we should generalize the approach used
         * here by providing an abstract protected method in AbstractMathTransform which expect
         * a DimensionFilter (maybe to be renamed) in argument. The default implementation would
         * check for the trivial case allowing to return 'this', and throw an exception for non-
         * trivial cases. Appropriate subclasses (PassthroughTransform, ConcatenatedTransform,
         * etc.) should implement that method.
         */
        if (transform instanceof SeparableTransform) {
            final MathTransform candidate = ((SeparableTransform) transform).subTransform(sourceDimensions, targetDimensions);
            if (candidate != null) {
                // BAD HACK - Presume that source and target dimensions are the same.
                // This is often the case with NetCDF files, but is not garanteed.
                if (sourceDimensions == null) sourceDimensions = targetDimensions;
                if (targetDimensions == null) targetDimensions = sourceDimensions;
                return candidate;
            }
        }
        /*
         * -------- END OF HACK --------
         */
        return super.separate();
    }

    /**
     * Separates the math transform on the basis of {@linkplain #sourceDimensions input dimensions}.
     * The remaining {@linkplain #targetDimensions output dimensions} will be selected automatically
     * according the specified input dimensions.
     *
     * @param  step The transform to reduces.
     * @return A transform expecting only the specified input dimensions.
     * @throws FactoryException if the transform is not separable.
     */
    @Override
    protected MathTransform filterSourceDimensions(final MathTransform step, final int[] dimensions) throws FactoryException {
        /*
         * -------- HACK BEGINS -------- (same than in 'separate(...)')
         */
        if (step instanceof SeparableTransform) {
            final MathTransform candidate = ((SeparableTransform) step).subTransform(sourceDimensions, targetDimensions);
            if (candidate != null) {
                if (sourceDimensions == null) sourceDimensions = targetDimensions;
                if (targetDimensions == null) targetDimensions = sourceDimensions;
                return candidate;
            }
        }
        /*
         * -------- END OF HACK --------
         */
        return super.filterSourceDimensions(step, dimensions);
    }
}
