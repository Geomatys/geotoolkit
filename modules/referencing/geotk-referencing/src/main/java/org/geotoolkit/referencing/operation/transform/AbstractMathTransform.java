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
package org.geotoolkit.referencing.operation.transform;

import java.util.List;
import java.util.Arrays;
import java.io.Serializable;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.IllegalPathStateException;
import net.jcip.annotations.ThreadSafe;

import org.opengis.metadata.Identifier;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import org.geotoolkit.io.wkt.Formatter;
import org.geotoolkit.io.wkt.FormattableObject;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.display.shape.ShapeUtilities;
import org.geotoolkit.referencing.operation.matrix.Matrices;
import org.apache.sis.util.Classes;
import org.apache.sis.util.Utilities;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.util.LenientComparable;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;

import static org.geotoolkit.util.Utilities.hash;
import static org.apache.sis.util.ArgumentChecks.ensureDimensionMatches;


/**
 * Provides a default implementation for most methods required by the {@link MathTransform}
 * interface. {@code AbstractMathTransform} provides a convenient base class from which
 * transform implementations can be easily derived. It also defines a few additional
 * Geotk-specific methods for convenience of performance.
 * <p>
 * The simplest way to implement this abstract class is to provide an implementation for the
 * following methods only:
 * <p>
 * <ul>
 *   <li>{@link #getSourceDimensions()}</li>
 *   <li>{@link #getTargetDimensions()}</li>
 *   <li>{@link #transform(double[],int,double[],int,boolean)}</li>
 * </ul>
 * <p>
 * However more performance may be gained by overriding the other {@code transform} method as well.
 *
 * {@section Two-dimensional transforms}
 * {@code AbstractMathTransform} implements also the methods required by the {@link MathTransform2D}
 * interface, but <strong>does not</strong> implements that interface directly. Subclasses must add
 * the "{@code implements MathTransform2D}" clause themselves, or extend the {@link AbstractMathTransform2D}
 * base class, if they know to map two-dimensional coordinate systems.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 1.2
 * @module
 */
@ThreadSafe
public abstract class AbstractMathTransform extends FormattableObject
        implements MathTransform, Parameterized, LenientComparable
{
    /**
     * Maximum buffer size when creating temporary arrays. Must not be too big, otherwise the
     * cost of allocating the buffer may be greater than the benefit of transforming array of
     * coordinates. Remember that double number occupy 8 bytes, so a buffer of size 512 will
     * actually consumes 4 kb of memory.
     */
    static final int MAXIMUM_BUFFER_SIZE = 512;

    /**
     * Maximum amount of {@link TransformException} to catch while transforming a block of
     * {@value #MAXIMUM_BUFFER_SIZE} ordinate values in an array. The default implementation of
     * {@code transform} methods set un-transformable coordinates to {@linkplain Double#NaN NaN}
     * before to let the exception propagate. However if more then {@value} exceptions occur in
     * a block of {@value #MAXIMUM_BUFFER_SIZE} <em>ordinates</em> (not coordinates), then we
     * will give up. We put a limit in order to avoid slowing down the application too much if
     * a whole array is not transformable.
     * <p>
     * Note that in case of failure, the first {@code TransformException} is still propagated;
     * we do not "eat" it. We just set the ordinates to {@code NaN} before to let the propagation
     * happen. If no exception handling should be performed at all, then {@code MAXIMUM_FAILURES}
     * can be set to 0.
     * <p>
     * Having {@code MAXIMUM_BUFFER_SIZE} sets to 512 and {@code MAXIMUM_FAILURES} sets to 32
     * means that we tolerate about 6.25% of un-transformable points.
     */
    static final int MAXIMUM_FAILURES = 32;

    /**
     * The cached hash code value, or 0 if not yet computed. This field is calculated only when
     * first needed. We do not declare it {@code volatile} because it is not a big deal if this
     * field is calculated many time, and the same value should be produced by all computations.
     * The only possible outdated value is 0, which is okay.
     *
     * @since 3.18
     */
    private transient int hashCode;

    /**
     * Constructs a math transform.
     */
    protected AbstractMathTransform() {
    }

    /**
     * Returns a name for this math transform (never {@code null}). This convenience methods
     * returns the name of the {@linkplain #getParameterDescriptors parameter descriptors} if
     * any, or the short class name otherwise.
     *
     * @return A name for this math transform (never {@code null}).
     *
     * @since 2.5
     */
    public String getName() {
        final ParameterDescriptorGroup descriptor = getParameterDescriptors();
        if (descriptor != null) {
            final Identifier identifier = descriptor.getName();
            if (identifier != null) {
                final String code = identifier.getCode();
                if (code != null) {
                    return code;
                }
            }
        }
        return Classes.getShortClassName(this);
    }

    /**
     * Gets the dimension of input points.
     */
    @Override
    public abstract int getSourceDimensions();

    /**
     * Gets the dimension of output points.
     */
    @Override
    public abstract int getTargetDimensions();

    /**
     * {@inheritDoc}
     */
    @Override
    public ParameterDescriptorGroup getParameterDescriptors() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParameterValueGroup getParameterValues() {
        return null;
    }

    /**
     * Tests whether this transform does not move any points.
     * The default implementation always returns {@code false}.
     */
    @Override
    public boolean isIdentity() {
        return false;
    }

    /**
     * Constructs an error message for the {@link MismatchedDimensionException}.
     *
     * @param argument  The argument name with the wrong number of dimensions.
     * @param dimension The wrong dimension.
     * @param expected  The expected dimension.
     */
    static String mismatchedDimension(final String argument, final int dimension, final int expected) {
        return Errors.format(Errors.Keys.MISMATCHED_DIMENSION_$3, argument, dimension, expected);
    }

    /**
     * Transforms the specified {@code ptSrc} and stores the result in {@code ptDst}.
     * The default implementation performs the following steps:
     * <p>
     * <ul>
     *   <li>Ensures that the {@linkplain #getSourceDimensions() source} and
     *       {@linkplain #getTargetDimensions() target dimensions} of this math
     *       transform are equal to 2.</li>
     *   <li>Delegates to the {@link #transform(double[],int,double[],int,boolean)}
     *       method using a temporary array of doubles.</li>
     * </ul>
     *
     * @param  ptSrc The coordinate point to be transformed.
     * @param  ptDst The coordinate point that stores the result of transforming {@code ptSrc},
     *               or {@code null} if a new point should be created.
     * @return The coordinate point after transforming {@code ptSrc} and storing the result in
     *         {@code ptDst}, or in a new point if {@code ptDst} was null.
     * @throws MismatchedDimensionException If this transform doesn't map two-dimensional
     *         coordinate systems.
     * @throws TransformException If the point can't be transformed.
     *
     * @see MathTransform2D#transform(Point2D,Point2D)
     */
    public Point2D transform(final Point2D ptSrc, final Point2D ptDst) throws TransformException {
        int dim;
        if ((dim = getSourceDimensions()) != 2) {
            throw new MismatchedDimensionException(mismatchedDimension("ptSrc", 2, dim));
        }
        if ((dim = getTargetDimensions()) != 2) {
            throw new MismatchedDimensionException(mismatchedDimension("ptDst", 2, dim));
        }
        final double[] ord = new double[] {ptSrc.getX(), ptSrc.getY()};
        transform(ord, 0, ord, 0, false);
        if (ptDst != null) {
            ptDst.setLocation(ord[0], ord[1]);
            return ptDst;
        } else {
            return new Point2D.Double(ord[0], ord[1]);
        }
    }

    /**
     * Transforms the specified {@code ptSrc} and stores the result in {@code ptDst}.
     * The default implementation performs the following steps:
     * <p>
     * <ul>
     *   <li>Ensures that the dimension of the given points are consistent with the
     *       {@linkplain #getSourceDimensions() source} and {@linkplain #getTargetDimensions()
     *       target dimensions} of this math transform.</li>
     *   <li>Delegates to the {@link #transform(double[],int,double[],int,boolean)} method.</li>
     * </ul>
     *
     * @param  ptSrc the coordinate point to be transformed.
     * @param  ptDst the coordinate point that stores the result of transforming {@code ptSrc},
     *         or {@code null}.
     * @return the coordinate point after transforming {@code ptSrc} and storing the result
     *         in {@code ptDst}, or a newly created point if {@code ptDst} was null.
     * @throws MismatchedDimensionException if {@code ptSrc} or
     *         {@code ptDst} doesn't have the expected dimension.
     * @throws TransformException if the point can't be transformed.
     */
    @Override
    public DirectPosition transform(final DirectPosition ptSrc, DirectPosition ptDst)
            throws TransformException
    {
        final int dimSource = getSourceDimensions();
        final int dimTarget = getTargetDimensions();
        ensureDimensionMatches("ptSrc", dimSource, ptSrc);
        if (ptDst != null) {
            ensureDimensionMatches("ptDst", dimTarget, ptDst);
            /*
             * Transforms the coordinates using a temporary 'double[]' buffer,
             * and copies the transformation result in the destination position.
             */
            final double[] array;
            if (dimSource >= dimTarget) {
                array = ptSrc.getCoordinate();
            } else {
                array = new double[dimTarget];
                for (int i=dimSource; --i>=0;) {
                    array[i] = ptSrc.getOrdinate(i);
                }
            }
            transform(array, 0, array, 0, false);
            for (int i=0; i<dimTarget; i++) {
                ptDst.setOrdinate(i, array[i]);
            }
        } else {
            /*
             * Destination not set.  We are going to create the destination here.  Since we know
             * that the destination will be the Geotk implementation, write directly into the
             * 'ordinates' array.
             */
            final GeneralDirectPosition destination = new GeneralDirectPosition(dimTarget);
            final double[] source;
            if (dimSource <= dimTarget) {
                source = destination.ordinates;
                for (int i=0; i<dimSource; i++) {
                    source[i] = ptSrc.getOrdinate(i);
                }
            } else {
                source = ptSrc.getCoordinate();
            }
            transform(source, 0, destination.ordinates, 0, false);
            ptDst = destination;
        }
        return ptDst;
    }

    /**
     * Transforms a single coordinate point in an array, and optionally computes the transform
     * derivative at that location. Invoking this method is conceptually equivalent to running
     * the following:
     *
     * {@preformat java
     *     Matrix derivative = null;
     *     if (derivate) {
     *         double[] ordinates = Arrays.copyOfRange(srcPts, srcOff, srcOff + getSourceDimensions());
     *         derivative = this.derivative(new GeneralDirectPosition(ordinates));
     *     }
     *     this.transform(srcPts, srcOff, dstPts, dstOff, 1);  // May overwrite srcPts.
     *     return derivative;
     * }
     *
     * However this method provides two advantages:
     * <p>
     * <ul>
     *   <li>It is usually easier to implement for {@code AbstractMathTransform} subclasses.
     *   The default {@link #transform(double[],int,double[],int,int)} method implementation
     *   will invoke this method in a loop, taking care of the {@linkplain IterationStrategy
     *   iteration strategy} depending on the argument value.</li>
     *
     *   <li>When both the transformed point and its derivative are needed, this method may be
     *   significantly faster than invoking the {@code transform} and {@code derivative} methods
     *   separately because many internal calculations are the same. Computing those two information
     *   in a single step can help to reduce redundant calculation.</li>
     * </ul>
     *
     * {@section Implementation note}
     * The source and destination may overlap. Consequently, implementors must read all source
     * ordinate values before to start writing the transformed ordinates in the destination array.
     *
     * @param srcPts The array containing the source coordinate (can not be {@code null}).
     * @param srcOff The offset to the point to be transformed in the source array.
     * @param dstPts the array into which the transformed coordinate is returned.
     *               May be the same than {@code srcPts}. May be {@code null} if
     *               only the derivative matrix is desired.
     * @param dstOff The offset to the location of the transformed point that is
     *               stored in the destination array.
     * @param derivate {@code true} for computing the derivative, or {@code false} if not needed.
     * @return The matrix of the transform derivative at the given source position, or {@code null}
     *         if the {@code derivate} argument is {@code false}.
     * @throws TransformException If the point can't be transformed or if a problem occurred while
     *         calculating the derivative.
     *
     * @see #derivative(DirectPosition)
     * @see #transform(DirectPosition, DirectPosition)
     * @see org.geotoolkit.referencing.operation.MathTransforms#derivativeAndTransform(MathTransform, double[], int, double[], int)
     *
     * @since 3.20 (derived from 3.00)
     */
    public abstract Matrix transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, boolean derivate)
            throws TransformException;

    /**
     * Transforms a list of coordinate point ordinal values. This method is provided for
     * efficiently transforming many points. The supplied array of ordinal values will
     * contain packed ordinal values. For example if the source dimension is 3, then the
     * ordinates will be packed in this order:
     *
     * <blockquote>
     * (<var>x<sub>0</sub></var>,<var>y<sub>0</sub></var>,<var>z<sub>0</sub></var>,
     *  <var>x<sub>1</sub></var>,<var>y<sub>1</sub></var>,<var>z<sub>1</sub></var> ...).
     * </blockquote>
     *
     * The default implementation invokes {@link #transform(double[],int,double[],int,boolean)}
     * in a loop, using an {@linkplain IterationStrategy iteration strategy} determined from the
     * arguments for iterating over the points.
     *
     * @param srcPts The array containing the source point coordinates.
     * @param srcOff The offset to the first point to be transformed in the source array.
     * @param dstPts The array into which the transformed point coordinates are returned.
     *               May be the same than {@code srcPts}.
     * @param dstOff The offset to the location of the first transformed point that is
     *               stored in the destination array.
     * @param numPts The number of point objects to be transformed.
     * @throws TransformException if a point can't be transformed. Some implementations will stop
     *         at the first failure, wile some other implementations will fill the un-transformable
     *         points with {@linkplain Double#NaN NaN} values, continue and throw the exception
     *         only at end. Implementations that fall in the later case should set the {@linkplain
     *         TransformException#getLastCompletedTransform last completed transform} to {@code this}.
     */
    @Override
    public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        if (numPts <= 0) {
            return;
        }
        /*
         * If case of overlapping source and destination arrays, determines if we should iterate
         * over the coordinates in ascending/descending order or copy the data in a temporary buffer.
         * The "offFinal" and "dstFinal" variables will be used only in the BUFFER_TARGET case.
         */
        double[] dstFinal = null;
        int offFinal = 0;
        int srcInc = getSourceDimensions();
        int dstInc = getTargetDimensions();
        if (srcPts == dstPts) {
            switch (IterationStrategy.suggest(srcOff, srcInc, dstOff, dstInc, numPts)) {
                case ASCENDING: {
                    break;
                }
                case DESCENDING: {
                    srcOff += (numPts-1) * srcInc; srcInc = -srcInc;
                    dstOff += (numPts-1) * dstInc; dstInc = -dstInc;
                    break;
                }
                default: // Following should alway work even for unknown cases.
                case BUFFER_SOURCE: {
                    srcPts = Arrays.copyOfRange(srcPts, srcOff, srcOff + numPts*srcInc);
                    srcOff = 0;
                    break;
                }
                case BUFFER_TARGET: {
                    dstFinal = dstPts; dstPts = new double[numPts * dstInc];
                    offFinal = dstOff; dstOff = 0;
                    break;
                }
            }
        }
        /*
         * Now apply the coordinate transformation, invoking the user-overrideable method
         * for each individual point. In case of failure, we will set the ordinates to NaN
         * and continue with other points, up to some maximal amount of failures.
         */
        TransformException failure = null;
        int failureCount = 0; // Count ordinates, not coordinates.
        int blockStart   = 0;
        do {
            try {
                transform(srcPts, srcOff, dstPts, dstOff, false);
            } catch (TransformException exception) {
                /*
                 * If an exception occurred, let it propagate if we reached the maximum amount
                 * of exceptions we try to handle. We do NOT invoke setLastCompletedTransform
                 * in this case since we gave up.
                 */
                failureCount += Math.abs(srcInc);
                if (failureCount > MAXIMUM_FAILURES) {
                    throw failure;
                }
                /*
                 * Otherwise fills the ordinate values to NaN and count the number of exceptions,
                 * so we known when to give up if there is too much of them. The first exception
                 * will be propagated at the end of this method.
                 */
                Arrays.fill(dstPts, dstOff, dstOff + Math.abs(dstInc), Double.NaN);
                if (failure == null) {
                    failure = exception; // Keep only the first failure.
                    blockStart = srcOff;
                } else {
                    failure.addSuppressed(exception);
                    if (Math.abs(srcOff - blockStart) > MAXIMUM_BUFFER_SIZE) {
                        failureCount = 0; // We started a new block of coordinates.
                        blockStart = srcOff;
                    }
                }
            }
            srcOff += srcInc;
            dstOff += dstInc;
        } while (--numPts != 0);
        if (dstFinal != null) {
            System.arraycopy(dstPts, 0, dstFinal, offFinal, dstPts.length);
        }
        /*
         * If some points failed to be transformed, let the first exception propagate.
         * But before doing so we declare that this transform has nevertheless be able
         * to process all coordinate points, setting them to NaN when transform failed.
         */
        if (failure != null) {
            failure.setLastCompletedTransform(this);
            throw failure;
        }
    }

    /**
     * Transforms a list of coordinate point ordinal values. The default implementation
     * delegates to {@link #transform(double[],int,double[],int,int)} using a temporary
     * array of doubles.
     *
     * @param srcPts The array containing the source point coordinates.
     * @param srcOff The offset to the first point to be transformed in the source array.
     * @param dstPts The array into which the transformed point coordinates are returned.
     *               May be the same than {@code srcPts}.
     * @param dstOff The offset to the location of the first transformed point that is
     *               stored in the destination array.
     * @param numPts The number of point objects to be transformed.
     * @throws TransformException if a point can't be transformed. Some implementations will stop
     *         at the first failure, wile some other implementations will fill the un-transformable
     *         points with {@linkplain Double#NaN NaN} values, continue and throw the exception
     *         only at end. Implementations that fall in the later case should set the {@linkplain
     *         TransformException#getLastCompletedTransform last completed transform} to {@code this}.
     */
    @Override
    public void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        if (numPts <= 0) {
            return;
        }
        final int dimSource  = getSourceDimensions();
        final int dimTarget  = getTargetDimensions();
        final int dimLargest = Math.max(dimSource, dimTarget);
        /*
         * Computes the number of points in the buffer in such a way that the buffer
         * can contain at least one point and is not larger than MAXIMUM_BUFFER_SIZE
         * (except if a single point is larger than that).
         */
        int numBufferedPts = numPts;
        int bufferSize = numPts * dimLargest;
        if (bufferSize > MAXIMUM_BUFFER_SIZE) {
            numBufferedPts = Math.max(1, MAXIMUM_BUFFER_SIZE / dimLargest);
            bufferSize = numBufferedPts * dimLargest;
        }
        /*
         * We need to check if writing the transformed coordinates in the same array than the source
         * coordinates will cause an overlapping problem. However we can consider the whole buffer as
         * if it was a single coordinate with a very large dimension. Doing so increase the chances
         * that IterationStrategy.suggest(...) doesn't require us an other buffer  (hint: the -1 in
         * suggest(...) mathematic matter and reflect the contract saying that the input coordinate
         * must be fully read before the output coordinate is written - which is the behavior we get
         * with our buffer).
         */
        int srcInc = dimSource * numBufferedPts;
        int dstInc = dimTarget * numBufferedPts;
        int srcStop = srcInc; // src|dstStop will be used and modified in the do..while loop later.
        int dstStop = dstInc;
        if (srcPts == dstPts) {
            final int numPass = (numPts + numBufferedPts-1) / numBufferedPts; // Round toward higher integer.
            switch (IterationStrategy.suggest(srcOff, srcInc, dstOff, dstInc, numPass)) {
                case ASCENDING: {
                    break;
                }
                case DESCENDING: {
                    final int delta = numPts - numBufferedPts;
                    srcOff += delta * dimSource;
                    dstOff += delta * dimTarget;
                    srcInc = -srcInc;
                    dstInc = -dstInc;
                    break;
                }
                default: {
                    srcPts = Arrays.copyOfRange(srcPts, srcOff, srcOff + numPts*dimSource);
                    srcOff = 0;
                    break;
                }
            }
        }
        /*
         * Computes the offset of the first source coordinate in the buffer. The offset of the
         * first destination coordinate will always be zero.   We compute the source offset in
         * such a way that the default transform(double[],int,double[],int,int) implementation
         * should never needs to copy the source coordinates in yet an other temporary buffer.
         * We will verify that with an assert statement inside the do loop.
         */
        final int bufferedSrcOff = (dimSource >= dimTarget) ? 0 : dstStop - srcStop;
        final double[] buffer = new double[bufferSize];
        TransformException failure = null;
        do {
            if (numPts < numBufferedPts) {
                numBufferedPts = numPts;
                srcStop = numPts * dimSource;
                dstStop = numPts * dimTarget;
                if (srcInc < 0) {
                    // If we were applying IterationStrategy.DESCENDING, then srcOff and dstOff
                    // may be negative at this point because the last pass may not fill all the
                    // buffer space. We need to apply the correction below.
                    srcOff -= (srcStop + srcInc);
                    dstOff -= (dstStop + dstInc);
                }
            }
            for (int i=0; i<srcStop; i++) {
                buffer[bufferedSrcOff + i] = (double) srcPts[srcOff + i];
            }
            assert !IterationStrategy.suggest(bufferedSrcOff, dimSource, 0, dimTarget, numBufferedPts).needBuffer;
            try {
                transform(buffer, bufferedSrcOff, buffer, 0, numBufferedPts);
            } catch (TransformException exception) {
                /*
                 * If an exception occurred but the transform nevertheless declares having been
                 * able to process all coordinate points (setting to NaN those that can't be
                 * transformed), we will keep the first exception (to be propagated at the end
                 * of this method) and continue. Otherwise we will stop immediately.
                 */
                if (exception.getLastCompletedTransform() != this) {
                    throw exception;
                } else if (failure == null) {
                    failure = exception; // Keep only the first exception.
                } else {
                    failure.addSuppressed(exception);
                }
            }
            for (int i=0; i<dstStop; i++) {
                dstPts[dstOff + i] = (float) buffer[i];
            }
            srcOff += srcInc;
            dstOff += dstInc;
            numPts -= numBufferedPts;
        } while (numPts != 0);
        if (failure != null) {
            throw failure;
        }
    }

    /**
     * Transforms a list of coordinate point ordinal values. The default implementation
     * delegates to {@link #transform(double[],int,double[],int,int)} using a temporary
     * array of doubles.
     *
     * @param srcPts The array containing the source point coordinates.
     * @param srcOff The offset to the first point to be transformed in the source array.
     * @param dstPts The array into which the transformed point coordinates are returned.
     * @param dstOff The offset to the location of the first transformed point that is
     *               stored in the destination array.
     * @param numPts The number of point objects to be transformed.
     * @throws TransformException if a point can't be transformed. Some implementations will stop
     *         at the first failure, wile some other implementations will fill the un-transformable
     *         points with {@linkplain Double#NaN NaN} values, continue and throw the exception
     *         only at end. Implementations that fall in the later case should set the {@linkplain
     *         TransformException#getLastCompletedTransform last completed transform} to {@code this}.
     *
     * @since 2.5
     */
    @Override
    public void transform(final double[] srcPts, int srcOff,
                          final float [] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        if (numPts <= 0) {
            return;
        }
        final int dimSource = getSourceDimensions();
        final int dimTarget = getTargetDimensions();
        int numBufferedPts = numPts;
        int bufferSize = numPts * dimTarget;
        if (bufferSize > MAXIMUM_BUFFER_SIZE) {
            numBufferedPts = Math.max(1, MAXIMUM_BUFFER_SIZE / dimTarget);
            bufferSize = numBufferedPts * dimTarget;
        }
        int srcLength = numBufferedPts * dimSource;
        int dstLength = numBufferedPts * dimTarget;
        final double[] buffer = new double[bufferSize];
        TransformException failure = null;
        do {
            if (numPts < numBufferedPts) {
                numBufferedPts = numPts;
                srcLength = numPts * dimSource;
                dstLength = numPts * dimTarget;
            }
            try {
                transform(srcPts, srcOff, buffer, 0, numBufferedPts);
            } catch (TransformException exception) {
                // Same comment than in transform(float[], ...,float[], ...)
                if (exception.getLastCompletedTransform() != this) {
                    throw exception;
                } else if (failure == null) {
                    failure = exception;
                } else {
                    failure.addSuppressed(exception);
                }
            }
            for (int i=0; i<dstLength; i++) {
                dstPts[dstOff++] = (float) buffer[i];
            }
            srcOff += srcLength;
            numPts -= numBufferedPts;
        } while (numPts != 0);
        if (failure != null) {
            throw failure;
        }
    }

    /**
     * Transforms a list of coordinate point ordinal values. The default implementation
     * delegates to {@link #transform(double[],int,double[],int,int)} using a temporary
     * array of doubles if necessary.
     *
     * @param srcPts The array containing the source point coordinates.
     * @param srcOff The offset to the first point to be transformed in the source array.
     * @param dstPts The array into which the transformed point coordinates are returned.
     * @param dstOff The offset to the location of the first transformed point that is
     *               stored in the destination array.
     * @param numPts The number of point objects to be transformed.
     * @throws TransformException if a point can't be transformed. Some implementations will stop
     *         at the first failure, wile some other implementations will fill the un-transformable
     *         points with {@linkplain Double#NaN NaN} values, continue and throw the exception
     *         only at end. Implementations that fall in the later case should set the {@linkplain
     *         TransformException#getLastCompletedTransform last completed transform} to {@code this}.
     *
     * @since 2.5
     */
    @Override
    public void transform(final float [] srcPts, int srcOff,
                          final double[] dstPts, int dstOff, int numPts)
            throws TransformException
    {
        if (numPts <= 0) {
            return;
        }
        final int dimSource = getSourceDimensions();
        final int dimTarget = getTargetDimensions();
        if (dimSource == dimTarget) {
            final int n = numPts * dimSource;
            for (int i=0; i<n; i++) {
                dstPts[dstOff + i] = srcPts[srcOff + i];
            }
            transform(dstPts, dstOff, dstPts, dstOff, numPts);
            return;
        }
        int numBufferedPts = numPts;
        int bufferSize = numPts * dimSource;
        if (bufferSize > MAXIMUM_BUFFER_SIZE) {
            numBufferedPts = Math.max(1, MAXIMUM_BUFFER_SIZE / dimSource);
            bufferSize = numBufferedPts * dimSource;
        }
        int srcLength = numBufferedPts * dimSource;
        int dstLength = numBufferedPts * dimTarget;
        final double[] buffer = new double[bufferSize];
        TransformException failure = null;
        do {
            if (numPts < numBufferedPts) {
                numBufferedPts = numPts;
                srcLength = numPts * dimSource;
                dstLength = numPts * dimTarget;
            }
            for (int i=0; i<srcLength; i++) {
                buffer[i] = (double) srcPts[srcOff++];
            }
            try {
                transform(buffer, 0, dstPts, dstOff, numBufferedPts);
            } catch (TransformException exception) {
                // Same comment than in transform(float[], ...,float[], ...)
                if (exception.getLastCompletedTransform() != this) {
                    throw exception;
                } else if (failure == null) {
                    failure = exception;
                } else {
                    failure.addSuppressed(exception);
                }
            }
            dstOff += dstLength;
            numPts -= numBufferedPts;
        } while (numPts != 0);
        if (failure != null) {
            throw failure;
        }
    }

    /**
     * Transform the specified shape. The default implementation computes quadratic curves
     * using three points for each line segment in the shape. The returned object is often
     * a {@link Path2D}, but may also be a {@link Line2D} or a {@link QuadCurve2D} if such
     * simplification is possible.
     *
     * @param  shape Shape to transform.
     * @return Transformed shape, or {@code shape} if this transform is the identity transform.
     * @throws IllegalStateException if this transform doesn't map 2D coordinate systems.
     * @throws TransformException if a transform failed.
     *
     * @see MathTransform2D#createTransformedShape(Shape)
     */
    public Shape createTransformedShape(final Shape shape) throws TransformException {
        return isIdentity() ? shape : createTransformedShape(shape, null, null, false);
    }

    /**
     * Transforms a geometric shape. This method always copy transformed coordinates in a new
     * object. The new object is often a {@link Path2D}, but may also be a {@link Line2D} or a
     * {@link QuadCurve2D} if such simplification is possible.
     *
     * @param  shape         The geometric shape to transform.
     * @param  preTransform  An optional affine transform to apply <em>before</em> the
     *                       transformation using {@code this}, or {@code null} if none.
     * @param  postTransform An optional affine transform to apply <em>after</em> the transformation
     *                       using {@code this}, or {@code null} if none.
     * @param  horizontal    {@code true} for forcing parabolic equation.
     *
     * @return The transformed geometric shape.
     * @throws MismatchedDimensionException if this transform doesn't is not two-dimensional.
     * @throws TransformException If a transformation failed.
     */
    final Shape createTransformedShape(final Shape           shape,
                                       final AffineTransform preTransform,
                                       final AffineTransform postTransform,
                                       final boolean         horizontal)
            throws TransformException
    {
        int dim;
        if ((dim = getSourceDimensions()) != 2 || (dim = getTargetDimensions()) != 2) {
            throw new MismatchedDimensionException(mismatchedDimension("shape", 2, dim));
        }
        final PathIterator     it = shape.getPathIterator(preTransform);
        final Path2D.Double  path = new Path2D.Double(it.getWindingRule());
        final double[]     buffer = new double[6];

        double ax=0, ay=0;  // Coordinate of the last point before transform.
        double px=0, py=0;  // Coordinate of the last point after  transform.
        for (; !it.isDone(); it.next()) {
            switch (it.currentSegment(buffer)) {
                default: {
                    throw new IllegalPathStateException();
                }
                case PathIterator.SEG_CLOSE: {
                    /*
                     * Closes the geometric shape and continues the loop. We use the 'continue'
                     * instruction here instead of 'break' because we don't want to execute the
                     * code after the switch (addition of transformed points into the path - there
                     * is no such point in a SEG_CLOSE).
                     */
                    path.closePath();
                    continue;
                }
                case PathIterator.SEG_MOVETO: {
                    /*
                     * Transforms the single point and adds it to the path. We use the 'continue'
                     * instruction here instead of 'break' because we don't want to execute the
                     * code after the switch (addition of a line or a curve - there is no such
                     * curve to add here; we are just moving the cursor).
                     */
                    ax = buffer[0];
                    ay = buffer[1];
                    transform(buffer, 0, buffer, 0, 1);
                    px = buffer[0];
                    py = buffer[1];
                    path.moveTo(px, py);
                    continue;
                }
                case PathIterator.SEG_LINETO: {
                    /*
                     * Inserts a new control point at 'buffer[0,1]'. This control point will
                     * be initialised with coordinates in the middle of the straight line:
                     *
                     *  x = 0.5*(x1+x2)
                     *  y = 0.5*(y1+y2)
                     *
                     * This point will be transformed after the 'switch', which is why we use
                     * the 'break' statement here instead of 'continue' as in previous case.
                     */
                    buffer[0] = 0.5*(ax + (ax=buffer[0]));
                    buffer[1] = 0.5*(ay + (ay=buffer[1]));
                    buffer[2] = ax;
                    buffer[3] = ay;
                    break;
                }
                case PathIterator.SEG_QUADTO: {
                    /*
                     * Replaces the control point in 'buffer[0,1]' by a new control point lying
                     * on the quadratic curve. Coordinates for a point in the middle of the curve
                     * can be computed with:
                     *
                     *  x = 0.5*(ctrlx + 0.5*(x1+x2))
                     *  y = 0.5*(ctrly + 0.5*(y1+y2))
                     *
                     * There is no need to keep the old control point because it was not lying
                     * on the curve.
                     */
                    buffer[0] = 0.5*(buffer[0] + 0.5*(ax + (ax=buffer[2])));
                    buffer[1] = 0.5*(buffer[1] + 0.5*(ay + (ay=buffer[3])));
                    break;
                }
                case PathIterator.SEG_CUBICTO: {
                    /*
                     * Replaces the control point in 'buffer[0,1]' by a new control point lying
                     * on the cubic curve. Coordinates for a point in the middle of the curve
                     * can be computed with:
                     *
                     *  x = 0.25*(1.5*(ctrlx1+ctrlx2) + 0.5*(x1+x2));
                     *  y = 0.25*(1.5*(ctrly1+ctrly2) + 0.5*(y1+y2));
                     *
                     * There is no need to keep the old control point because it was not lying
                     * on the curve.
                     *
                     * NOTE: Le point calculé est bien sur la courbe, mais n'est pas
                     *       nécessairement représentatif. Cet algorithme remplace les
                     *       deux points de contrôles par un seul, ce qui se traduit par
                     *       une perte de souplesse qui peut donner de mauvais résultats
                     *       si la courbe cubique était bien tordue. Projeter une courbe
                     *       cubique ne me semble pas être un problème simple, mais ce
                     *       cas devrait être assez rare. Il se produira le plus souvent
                     *       si on essaye de projeter un cercle ou une ellipse, auxquels
                     *       cas l'algorithme actuel donnera quand même des résultats
                     *       tolérables.
                     */
                    buffer[0] = 0.25*(1.5*(buffer[0]+buffer[2]) + 0.5*(ax + (ax=buffer[4])));
                    buffer[1] = 0.25*(1.5*(buffer[1]+buffer[3]) + 0.5*(ay + (ay=buffer[5])));
                    buffer[2] = ax;
                    buffer[3] = ay;
                    break;
                }
            }
            /*
             * Applies the transform on the point in the buffer, and append the transformed points
             * to the general path. Try to add them as a quadratic line, or as a straight line if
             * the computed control point is colinear with the starting and ending points.
             */
            transform(buffer, 0, buffer, 0, 2);
            final Point2D ctrlPoint = ShapeUtilities.parabolicControlPoint(px, py,
                                                     buffer[0], buffer[1],
                                                     buffer[2], buffer[3],
                                                     horizontal);
            px = buffer[2];
            py = buffer[3];
            if (ctrlPoint != null) {
                path.quadTo(ctrlPoint.getX(), ctrlPoint.getY(), px, py);
            } else {
                path.lineTo(px, py);
            }
        }
        /*
         * La projection de la forme géométrique est terminée. Applique
         * une transformation affine si c'était demandée, puis retourne
         * une version si possible simplifiée de la forme géométrique.
         */
        if (postTransform != null) {
            path.transform(postTransform);
        }
        return ShapeUtilities.toPrimitive(path);
    }

    /**
     * Gets the derivative of this transform at a point.
     * The default implementation performs the following steps:
     * <p>
     * <ul>
     *   <li>Ensure that this math transform {@linkplain #getSourceDimensions() source dimensions}
     *       is equals to 2. Note that the {@linkplain #getTargetDimensions() target dimensions}
     *       can be anything, not necessarily 2 (so this transform doesn't need to implement the
     *       {@link MathTransform2D} interface).</li>
     *   <li>Copy the coordinate in a temporary array and pass that array to the
     *       {@link #transform(double[], int, double[], int, boolean)} method,
     *       with the {@code derivate} boolean argument set to {@code true}.</li>
     *   <li>If the later method returned a non-null matrix, returns that matrix.
     *       Otherwise throws {@link TransformException}.</li>
     * </ul>
     *
     * @param  point The coordinate point where to evaluate the derivative.
     * @return The derivative at the specified point as a 2&times;2 matrix.
     * @throws MismatchedDimensionException if the input dimension is not 2.
     * @throws TransformException if the derivative can't be evaluated at the specified point.
     *
     * @see MathTransform2D#derivative(Point2D)
     */
    public Matrix derivative(final Point2D point) throws TransformException {
        final int dimSource = getSourceDimensions();
        if (dimSource != 2) {
            throw new MismatchedDimensionException(mismatchedDimension("point", 2, dimSource));
        }
        final double[] coordinate = new double[] {point.getX(), point.getY()};
        final Matrix derivative = transform(coordinate, 0, null, 0, true);
        if (derivative == null) {
            throw new TransformException(Errors.format(Errors.Keys.CANT_COMPUTE_DERIVATIVE));
        }
        return derivative;
    }

    /**
     * Gets the derivative of this transform at a point.
     * The default implementation performs the following steps:
     * <p>
     * <ul>
     *   <li>Ensure that the {@code point} dimension is equals to this math transform
     *       {@linkplain #getSourceDimensions() source dimensions}.</li>
     *   <li>Copy the coordinate in a temporary array and pass that array to the
     *       {@link #transform(double[], int, double[], int, boolean)} method,
     *       with the {@code derivate} boolean argument set to {@code true}.</li>
     *   <li>If the later method returned a non-null matrix, returns that matrix.
     *       Otherwise throws {@link TransformException}.</li>
     * </ul>
     *
     * @param  point The coordinate point where to evaluate the derivative.
     * @return The derivative at the specified point (never {@code null}).
     * @throws NullPointerException if the derivative dependents on coordinate
     *         and {@code point} is {@code null}.
     * @throws MismatchedDimensionException if {@code point} doesn't have the expected dimension.
     * @throws TransformException if the derivative can't be evaluated at the specified point.
     */
    @Override
    public Matrix derivative(final DirectPosition point) throws TransformException {
        final int dimSource = getSourceDimensions();
        final double[] coordinate = point.getCoordinate();
        if (coordinate.length != dimSource) {
            throw new MismatchedDimensionException(mismatchedDimension("point", coordinate.length, dimSource));
        }
        final Matrix derivative = transform(coordinate, 0, null, 0, true);
        if (derivative == null) {
            throw new TransformException(Errors.format(Errors.Keys.CANT_COMPUTE_DERIVATIVE));
        }
        return derivative;
    }

    /**
     * Returns the inverse transform of this object. The default implementation
     * returns {@code this} if this transform is an identity transform, and throws
     * a {@link NoninvertibleTransformException} otherwise. Subclasses should override
     * this method.
     */
    @Override
    public MathTransform inverse() throws NoninvertibleTransformException {
        if (isIdentity()) {
            return this;
        }
        throw new NoninvertibleTransformException(Errors.format(Errors.Keys.NONINVERTIBLE_TRANSFORM));
    }

    /**
     * Concatenates in an optimized way this math transform with the given one. A new math
     * transform is created to perform the combined transformation. The {@code applyOtherFirst}
     * value determines the transformation order as bellow:
     * <p>
     * <ul>
     *   <li>If {@code applyOtherFirst} is {@code true}, then transforming a point
     *       <var>p</var> by the combined transform is equivalent to first transforming
     *       <var>p</var> by {@code other} and then transforming the result by {@code this}.</li>
     *   <li>If {@code applyOtherFirst} is {@code false}, then transforming a point
     *       <var>p</var> by the combined transform is equivalent to first transforming
     *       <var>p</var> by {@code this} and then transforming the result by {@code other}.</li>
     * </ul>
     * <p>
     * If no special optimization is available for the combined transform, then this method
     * returns {@code null}. In the later case, the concatenation will be prepared by
     * {@link DefaultMathTransformFactory} using a generic {@link ConcatenatedTransform}.
     * <p>
     * The default implementation always returns {@code null}. This method is ought to be
     * overridden by subclasses capable of concatenating some combination of transforms in a
     * special way. Examples are {@link ExponentialTransform1D} and {@link LogarithmicTransform1D}.
     *
     * @param  other The math transform to apply.
     * @param  applyOtherFirst {@code true} if the transformation order is {@code other}
     *         followed by {@code this}, or {@code false} if the transformation order is
     *         {@code this} followed by {@code other}.
     * @return The combined math transform, or {@code null} if no optimized combined
     *         transform is available.
     */
    MathTransform concatenate(final MathTransform other, final boolean applyOtherFirst) {
        return null;
    }

    /**
     * Returns a hash value for this transform. This method invokes {@link #computeHashCode()}
     * when first needed and caches the value for future invocations. Subclasses should override
     * {@code computeHashCode()} instead than this method.
     *
     * @return The hash code value. This value may change between different execution of the
     *         Geotk library.
     */
    @Override
    public final int hashCode() { // No need to synchronize; ok if invoked twice.
        int hash = hashCode;
        if (hash == 0) {
            hash = computeHashCode();
            if (hash == 0) {
                hash = -1;
            }
            hashCode = hash;
        }
        assert hash == -1 || hash == computeHashCode() : this;
        return hash;
    }

    /**
     * Computes a hash value for this transform. This method is invoked by {@link #hashCode()}
     * when first needed.
     *
     * @return The hash code value. This value may change between different execution of the
     *         Geotk library.
     *
     * @since 3.18
     */
    protected int computeHashCode() {
        return hash(getClass(), hash(getSourceDimensions(), getTargetDimensions()));
    }

    /**
     * Compares the specified object with this math transform for strict equality.
     * This method is implemented as below (omitting assertions):
     *
     * {@preformat java
     *     return equals(other, ComparisonMode.STRICT);
     * }
     *
     * @param  object The object to compare with this transform.
     * @return {@code true} if the given object is a transform of the same class and using
     *         the same parameter values.
     */
    @Override
    public final boolean equals(final Object object) {
        final boolean eq = equals(object, ComparisonMode.STRICT);
        // If objects are equal, then they must have the same hash code value.
        assert !eq || hashCode() == object.hashCode() : this;
        return eq;
    }

    /**
     * Compares the specified object with this math transform for equality. The default
     * implementation returns {@code true} if the following conditions are meet:
     * <p>
     * <ul>
     *   <li>{@code object} is an instance of the same class than {@code this}. We require the
     *        same class because there is no interface for the various kinds of transform.</li>
     *   <li>The {@linkplain #getParameterDescriptors() parameter descriptors} are equal according
     *       the given comparison mode.</li>
     * </ul>
     * <p>
     * The {@linkplain #getParameterValues() parameter values} are <strong>not</strong> compared
     * because subclasses can typically compare those values more efficiently by accessing to
     * their member fields.
     *
     * @param  object The object to compare with this transform.
     * @param  mode The strictness level of the comparison. Default to {@link ComparisonMode#STRICT STRICT}.
     * @return {@code true} if the given object is a transform of the same class and if, given
     *         identical source position, the {@linkplain #transform(DirectPosition,DirectPosition)
     *         transformed} position would be the equals.
     *
     * @since 3.18
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        // Do not check 'object==this' here, since this
        // optimization is usually done in subclasses.
        if (object != null && getClass() == object.getClass()) {
            final AbstractMathTransform that = (AbstractMathTransform) object;
            /*
             * If the classes are the same, then the hash codes should be computed in the same
             * way. Since those codes are cached, this is an efficient way to quickly check if
             * the two objects are different.
             */
            if (mode.ordinal() < ComparisonMode.APPROXIMATIVE.ordinal()) {
                final int tc = hashCode;
                if (tc != 0) {
                    final int oc = that.hashCode;
                    if (oc != 0 && tc != oc) {
                        return false;
                    }
                }
            }
            // See the policy documented in the LenientComparable javadoc.
            if (mode.ordinal() >= ComparisonMode.IGNORE_METADATA.ordinal()) {
                return true;
            }
            return Utilities.deepEquals(this.getParameterDescriptors(),
                                        that.getParameterDescriptors(), mode);
        }
        return false;
    }

    /**
     * Helper method for implementation of {@link #equals(Object, ComparisonMode)} methods in
     * {@link LinearTransform} implementations. Those implementations shall replace completely the
     * {@link #equals(Object, ComparisonMode)} default implementation, <strong>except</strong> for
     * {@link ComparisonMode#STRICT} which should continue to rely on the default implementation.
     * The pattern is:
     *
     * {@preformat java
     *     public boolean equals(Object object, ComparisonMode mode) {
     *         if (object == this) { // Slight optimization
     *             return true;
     *         }
     *         if (mode != ComparisonMode.STRICT) {
     *             return equals(this, object, mode);
     *         }
     *         if (super.equals(object, mode)) {
     *             // Compare the internal fields here.
     *         }
     *         return false;
     *     }
     * }
     *
     * Note that this pattern considers {@link ComparisonMode#BY_CONTRACT} as synonymous to
     * {@code IGNORE_METADATA} rather than {@code STRICT}. This is valid if we consider that
     * the behavior of the math transform is completely specified by its matrix.
     *
     * @param  t1  The first transform to compare.
     * @param  t2  The second transform to compare, or {@code null} if none.
     * @param  mode The strictness level of the comparison.
     * @return {@code true} if both transforms are equal.
     *
     * @since 3.18
     */
    static boolean equals(final LinearTransform t1, final Object t2, final ComparisonMode mode) {
        if (t2 instanceof LinearTransform) {
            final Matrix m1 = t1.getMatrix();
            if (m1 != null) {
                final Matrix m2 = ((LinearTransform) t2).getMatrix();
                if (m1 instanceof org.apache.sis.util.LenientComparable) {
                    return ((org.apache.sis.util.LenientComparable) m1).equals(m2, mode);
                }
                return Matrices.equals(m1, m2, mode);
            }
        }
        return false;
    }

    /**
     * Formats the inner part of a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html#PARAM_MT"><cite>Well
     * Known Text</cite> (WKT)</A> element. The default implementation formats all parameter values
     * returned by {@link #getParameterValues}. The parameter group name is used as the math
     * transform name.
     *
     * @param  formatter The formatter to use.
     * @return The WKT element name, which is {@code "PARAM_MT"} in the default implementation.
     */
    @Override
    public String formatWKT(final Formatter formatter) {
        final ParameterValueGroup parameters = getParameterValues();
        if (parameters != null) {
            formatter.append(formatter.getName(parameters.getDescriptor()));
            formatter.append(parameters);
        }
        return "PARAM_MT";
    }

    /**
     * Strictly reserved to {@link AbstractMathTransform2D}, which will
     * override this method. The default implementation must do nothing.
     * <p>
     * This method is invoked only by {@link ConcatenatedTransform#getPseudoSteps()}
     * in order to get the {@link ParameterValueGroup} of a map projection, or to
     * format a {@code PROJCS} WKT.
     *
     * @param  transforms The full chain of concatenated transforms.
     * @param  index      The index of this transform in the {@code transforms} chain.
     * @param  inverse    Always {@code false}, except if we are formatting the inverse transform.
     * @return Index of the last transform processed. Iteration should continue at that index + 1.
     *
     * @see AbstractMathTransform2D#beforeFormat(List, int, boolean)
     * @see ConcatenatedTransform#getPseudoSteps()
     */
    int beforeFormat(List<Object> transforms, int index, boolean inverse) {
        return index;
    }

    /**
     * Makes sure that an argument is non-null. This is a
     * convenience method for subclass constructors.
     *
     * @param  name   Argument name.
     * @param  object User argument.
     * @throws InvalidParameterValueException if {@code object} is null.
     */
    protected static void ensureNonNull(final String name, final Object object)
            throws InvalidParameterValueException
    {
        if (object == null) {
            throw new InvalidParameterValueException(Errors.format(
                        Errors.Keys.NULL_ARGUMENT_$1, name), name, object);
        }
    }

    /**
     * Ensures that the specified longitude stay within the [-<var>bound</var> &hellip;
     * <var>bound</var>] range. This method is typically invoked before to project geographic
     * coordinates. It may add or subtract some amount of 2&times;<var>bound</var>
     * from <var>x</var>.
     * <p>
     * The <var>bound</var> value is typically 180 if the longitude is express in degrees,
     * or {@link Math#PI} it the longitude is express in radians. But it can also be some
     * other value if the longitude has already been multiplied by a scale factor before
     * this method is invoked.
     *
     * @param  x The longitude.
     * @param  bound The absolute value of the minimal and maximal allowed value, or
     *         {@link Double#POSITIVE_INFINITY} if no rolling should be applied.
     * @return The longitude between &plusmn;<var>bound</var>.
     */
    protected static double rollLongitude(double x, final double bound) {
        /*
         * Note: we could do the same than the code below with this single line
         * (assuming bound == PI):
         *
         *     return x - (2*PI) * floor(x / (2*PI) + 0.5);
         *
         * However the code below tries to reduce the amount of floating point operations: only
         * a division followed by a cast to (long) in the majority of cases. The multiplication
         * happen only if there is effectively a rolling to apply. All the remaining operations
         * are using integer arithmetic, so it should be fast.
         *
         * Note: usage of long instead of int is necessary, otherwise overflows do occur.
         */
        long n = (long) (x / bound); // Really want rounding toward zero.
        if (n != 0) {
            if (n < 0) {
                if ((n &= ~1) == -2) { // If odd number, decrement to the previous even number.
                    if (x == -bound) { // Special case for this one: don't rool to +180°.
                        return x;
                    }
                }
            } else if ((n & 1) != 0) {
                n++; // If odd number, increment to the next even number.
            }
            x -= n * bound;
        }
        return x;
    }

    /**
     * Default implementation for inverse math transform. This inner class is the inverse of
     * the enclosing {@link AbstractMathTransform}. It is serializable only if the enclosing
     * math transform is also serializable.
     *
     * @author Martin Desruisseaux (IRD)
     * @version 3.00
     *
     * @since 2.0
     * @module
     */
    protected abstract class Inverse extends AbstractMathTransform implements Serializable {
        /**
         * Serial number for inter-operability with different versions. This serial number is
         * especilly important for inner classes, since the default {@code serialVersionUID}
         * computation will not produce consistent results across implementations of different
         * Java compiler. This is because different compilers may generate different names for
         * synthetic members used in the implementation of inner classes. See:
         *
         * http://developer.java.sun.com/developer/bugParade/bugs/4211550.html
         */
        private static final long serialVersionUID = 3528274816628012283L;

        /**
         * Constructs an inverse math transform.
         */
        protected Inverse() {
        }

        /**
         * Returns a name for this math transform (never {@code null}). The default implementation
         * returns the direct transform name concatenated with localized flavor (when available)
         * of "(Inverse transform)".
         *
         * @return A name for this math transform (never {@code null}).
         *
         * @since 2.5
         */
        @Override
        public String getName() {
            return AbstractMathTransform.this.getName() +
                    " (" + Vocabulary.format(Vocabulary.Keys.INVERSE_TRANSFORM) + ')';
        }

        /**
         * Gets the dimension of input points. The default
         * implementation returns the dimension of output
         * points of the enclosing math transform.
         */
        @Override
        public int getSourceDimensions() {
            return AbstractMathTransform.this.getTargetDimensions();
        }

        /**
         * Gets the dimension of output points. The default
         * implementation returns the dimension of input
         * points of the enclosing math transform.
         */
        @Override
        public int getTargetDimensions() {
            return AbstractMathTransform.this.getSourceDimensions();
        }

        /**
         * Gets the derivative of this transform at a point. The default
         * implementation computes the inverse of the matrix returned by
         * the enclosing math transform.
         */
        @Override
        public Matrix derivative(final Point2D point) throws TransformException {
            return Matrices.invert(AbstractMathTransform.this.derivative(this.transform(point, null)));
        }

        /**
         * Gets the derivative of this transform at a point. The default
         * implementation computes the inverse of the matrix returned by
         * the enclosing math transform.
         */
        @Override
        public Matrix derivative(final DirectPosition point) throws TransformException {
            return Matrices.invert(AbstractMathTransform.this.derivative(this.transform(point, null)));
        }

        /**
         * Returns the enclosing class as a private method for protecting from user override.
         */
        private AbstractMathTransform enclosing() {
            return AbstractMathTransform.this;
        }

        /**
         * Returns the inverse of this math transform, which is the enclosing math transform.
         * This behavior should not be changed since some implementation assume that the inverse
         * of {@code this} is always {@code AbstractMathTransform.this}.
         */
        @Override
        public MathTransform inverse() {
            return AbstractMathTransform.this;
        }

        /**
         * Tests whether this transform does not move any points.
         * The default implementation delegates this tests to the
         * enclosing math transform.
         */
        @Override
        public boolean isIdentity() {
            return AbstractMathTransform.this.isIdentity();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected int computeHashCode() {
            return hash(AbstractMathTransform.this.hashCode(), super.computeHashCode());
        }

        /**
         * Compares the specified object with this inverse math transform for equality. The
         * default implementation tests if {@code object} in an instance of the same class
         * than {@code this}, and if so compares their enclosing {@code AbstractMathTransform}.
         */
        @Override
        public boolean equals(final Object object, final ComparisonMode mode) {
            if (object == this) {
                // Slight optimization
                return true;
            }
            if (object != null && object.getClass() == getClass()) {
                final Inverse that = (Inverse) object;
                return AbstractMathTransform.this.equals(that.enclosing(), mode);
            } else {
                return false;
            }
        }

        /**
         * Formats the inner part of a
         * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html#INVERSE_MT"><cite>Well
         * Known Text</cite> (WKT)</A> element. If this inverse math transform
         * has any parameter values, then this method format the WKT as in the
         * {@linkplain AbstractMathTransform#formatWKT super-class method}. Otherwise
         * this method format the math transform as an <code>"INVERSE_MT"</code> entity.
         *
         * @param  formatter The formatter to use.
         * @return The WKT element name, which is {@code "PARAM_MT"} or
         *         {@code "INVERSE_MT"} in the default implementation.
         */
        @Override
        public String formatWKT(final Formatter formatter) {
            final ParameterValueGroup parameters = getParameterValues();
            if (parameters != null) {
                formatter.append(formatter.getName(parameters.getDescriptor()));
                formatter.append(parameters);
                return "PARAM_MT";
            } else {
                formatter.append((FormattableObject) AbstractMathTransform.this);
                return "INVERSE_MT";
            }
        }
    }
}
