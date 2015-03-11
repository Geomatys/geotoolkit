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
package org.geotoolkit.geometry;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.cs.RangeMeaning;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.CoordinateOperation;
import org.opengis.referencing.operation.TransformException;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.lang.Static;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.display.shape.XRectangle2D;
import org.geotoolkit.display.shape.ShapeUtilities;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.operation.matrix.AffineTransforms2D;
import org.apache.sis.referencing.operation.transform.AbstractMathTransform;
import org.apache.sis.internal.referencing.DirectPositionView;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Utility methods for envelopes. This utility class is made up of static functions working
 * with arbitrary implementations of GeoAPI interfaces.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @author Andrea Aime (TOPP)
 * @author Johann Sorel (Geomatys)
 * @version 3.20
 *
 * @see CRS
 *
 * @since 3.19 (derived from 2.4)
 * @module
 */
public final class Envelopes extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private Envelopes() {
    }

    /**
     * Returns the domain of validity for the specified coordinate reference system,
     * or {@code null} if unknown. The returned envelope is expressed in terms of the
     * specified CRS.
     * <p>
     * This method performs the work documented in the
     * {@link CRS#getEnvelope(CoordinateReferenceSystem)} method.
     * It is defined in this class for convenience.
     *
     * @param  crs The coordinate reference system, or {@code null}.
     * @return The envelope in terms of the specified CRS, or {@code null} if none.
     *
     * @see CRS#getEnvelope(CoordinateReferenceSystem)
     * @see org.apache.sis.geometry.GeneralEnvelope#reduceToDomain(boolean)
     */
    public static Envelope getDomainOfValidity(final CoordinateReferenceSystem crs) {
        return CRS.getEnvelope(crs);
    }

    /**
     * A buckle method for calculating derivative and coordinate transformation in a single step,
     * if the given {@code derivative} argument is {@code true}.
     *
     * @param transform The transform to use.
     * @param srcPts The array containing the source coordinate at offset 0.
     * @param dstPts the array into which the transformed coordinate is returned.
     * @param dstOff The offset to the location of the transformed point that is
     *               stored in the destination array.
     * @param derivate {@code true} for computing the derivative, or {@code false} if not needed.
     * @return The matrix of the transform derivative at the given source position, or {@code null}
     *         if the {@code derivate} argument is {@code false}.
     * @throws TransformException If the point can't be transformed or if a problem occurred while
     *         calculating the derivative.
     */
    private static Matrix derivativeAndTransform(final MathTransform transform, final double[] srcPts,
            final double[] dstPts, final int dstOff, final boolean derivate) throws TransformException
    {
        if (transform instanceof AbstractMathTransform) {
            return ((AbstractMathTransform) transform).transform(srcPts, 0, dstPts, dstOff, derivate);
        }
        // Derivative must be calculated before to transform the coordinate.
        final Matrix derivative = derivate ? transform.derivative(new DirectPositionView(srcPts, 0, transform.getSourceDimensions())) : null;
        transform.transform(srcPts, 0, dstPts, dstOff, 1);
        return derivative;
    }

    /**
     * Transforms the given envelope to the specified CRS. If any argument is null, or if the
     * {@linkplain Envelope#getCoordinateReferenceSystem() envelope CRS} is null or the same
     * instance than the given target CRS, then the given envelope is returned unchanged.
     * Otherwise a new transformed envelope is returned.
     *
     * {@section Performance tip}
     * If there is many envelopes to transform with the same source and target CRS, then it
     * is more efficient to get the {@linkplain CoordinateOperation coordinate operation} or
     * {@linkplain MathTransform math transform} once for ever and invoke one of the methods
     * below.
     *
     * @param  envelope The envelope to transform (may be {@code null}).
     * @param  targetCRS The target CRS (may be {@code null}).
     * @return A new transformed envelope, or directly {@code envelope} if no change was required.
     * @throws TransformException If a transformation was required and failed.
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.geometry.Envelopes}.
     */
    @Deprecated
    public static Envelope transform(Envelope envelope, final CoordinateReferenceSystem targetCRS)
            throws TransformException
    {
        return org.apache.sis.geometry.Envelopes.transform(envelope, targetCRS);
    }

    /**
     * Transforms an envelope using the given {@linkplain MathTransform math transform}.
     * The transformation is only approximative: the returned envelope may be bigger than
     * necessary, or smaller than required if the bounding box contains a pole.
     * <p>
     * This method can not handle the case where the envelope contains the North or South pole,
     * or when it cross the &plusmn;180&deg; longitude, because {@linkplain MathTransform math
     * transforms} does not carry sufficient informations. For a more robust envelope
     * transformation, use {@link #transform(CoordinateOperation, Envelope)} instead.
     *
     * @param  transform The transform to use.
     * @param  envelope Envelope to transform, or {@code null}. This envelope will not be modified.
     * @return The transformed envelope, or {@code null} if {@code envelope} was null.
     * @throws TransformException if a transform failed.
     *
     * @see #transform(CoordinateOperation, Envelope)
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.geometry.Envelopes}.
     */
    @Deprecated
    public static GeneralEnvelope transform(final MathTransform transform, final Envelope envelope)
            throws TransformException
    {
        ensureNonNull("transform", transform);
        if (envelope == null) {
            return null;
        }
        if (transform instanceof MathTransform2D && envelope.getDimension() == 2) {
            final XRectangle2D tmp = XRectangle2D.createFromExtremums(
                    envelope.getMinimum(0), envelope.getMinimum(1),
                    envelope.getMaximum(0), envelope.getMaximum(1));
            transform((MathTransform2D) transform, tmp, tmp);
            return new GeneralEnvelope(
                    new double[] {tmp.getMinX(), tmp.getMinY()},
                    new double[] {tmp.getMaxX(), tmp.getMaxY()});
        }
        return org.apache.sis.geometry.Envelopes.transform(transform, envelope);
    }

    /**
     * Transforms an envelope using the given {@linkplain CoordinateOperation coordinate operation}.
     * The transformation is only approximative: the returned envelope may be bigger than the
     * smallest possible bounding box, but should not be smaller in most cases.
     * <p>
     * This method can handle the case where the envelope contains the North or South pole,
     * or when it cross the &plusmn;180&deg; longitude.
     *
     * {@note If the envelope CRS is non-null, then the caller should ensure that the operation
     * source CRS is the same than the envelope CRS. In case of mismatch, this method transforms
     * the envelope to the operation source CRS before to apply the operation. This extra step
     * may cause a lost of accuracy. In order to prevent this method from performing such
     * pre-transformation (if not desired), callers can ensure that the envelope CRS is
     * <code>null</code> before to call this method.}
     *
     * @param  operation The operation to use.
     * @param  envelope Envelope to transform, or {@code null}. This envelope will not be modified.
     * @return The transformed envelope, or {@code null} if {@code envelope} was null.
     * @throws TransformException if a transform failed.
     *
     * @see #transform(MathTransform, Envelope)
     *
     * @deprecated Moved to Apache SIS {@link org.apache.sis.geometry.Envelopes}.
     */
    @Deprecated
    public static GeneralEnvelope transform(final CoordinateOperation operation, Envelope envelope)
            throws TransformException
    {
        return org.apache.sis.geometry.Envelopes.transform(operation, envelope);
    }

    /**
     * Transforms a rectangular envelope using the given {@linkplain MathTransform math transform}.
     * The transformation is only approximative: the returned envelope may be bigger than
     * necessary, or smaller than required if the bounding box contains a pole.
     * <p>
     * Invoking this method is equivalent to invoking the following:
     *
     * {@preformat java
     *   transform(transform, new GeneralEnvelope(envelope)).toRectangle2D()
     * }
     *
     * Note that this method can not handle the case where the rectangle contains the North or South
     * pole, or when it cross the &plusmn;180&deg; longitude, because {@linkplain MathTransform
     * math transforms} do not carry sufficient informations. For a more robust rectangle
     * transformation, use {@link #transform(CoordinateOperation, Rectangle2D, Rectangle2D)}
     * instead.
     *
     * @param  transform   The transform to use. Source and target dimension must be 2.
     * @param  envelope    The rectangle to transform (may be {@code null}).
     * @param  destination The destination rectangle (may be {@code envelope}).
     *         If {@code null}, a new rectangle will be created and returned.
     * @return {@code destination}, or a new rectangle if {@code destination} was non-null
     *         and {@code envelope} was null.
     * @throws TransformException if a transform failed.
     *
     * @see #transform(CoordinateOperation, Rectangle2D, Rectangle2D)
     * @see org.geotoolkit.referencing.operation.matrix.XAffineTransform#transform(AffineTransform, Rectangle2D, Rectangle2D)
     */
    public static Rectangle2D transform(final MathTransform2D transform,
                                        final Rectangle2D     envelope,
                                              Rectangle2D     destination)
            throws TransformException
    {
        ensureNonNull("transform", transform);
        if (transform instanceof AffineTransform) {
            // Common case implemented in a more efficient way (less points to transform).
            return AffineTransforms2D.transform((AffineTransform) transform, envelope, destination);
        }
        return transform(transform, envelope, destination, new double[2]);
    }

    /**
     * Implementation of {@link #transform(MathTransform, Rectangle2D, Rectangle2D)} with the
     * opportunity to save the projected center coordinate. This method sets {@code point} to
     * the center of the source envelope projected to the target CRS.
     */
    @SuppressWarnings("fallthrough")
    private static Rectangle2D transform(final MathTransform2D transform,
                                         final Rectangle2D     envelope,
                                               Rectangle2D     destination,
                                         final double[]        point)
            throws TransformException
    {
        if (envelope == null) {
            return null;
        }
        double xmin = Double.POSITIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        /*
         * Notation (as if we were applying a map projection, but this is not necessarily the case):
         *   - (λ,φ) are ordinate values before projection.
         *   - (x,y) are ordinate values after projection.
         *   - D[00|01|10|11] are the ∂x/∂λ, ∂x/∂φ, ∂y/∂λ and ∂y/∂φ derivatives respectively.
         *   - Variables with indice 0 are for the very first point in iteration order.
         *   - Variables with indice 1 are for the values of the previous iteration.
         *   - Variables with indice 2 are for the current values in the iteration.
         *   - P1-P2 form a line segment to be checked for curvature.
         */
        double x0=0, y0=0, λ0=0, φ0=0;
        double x1=0, y1=0, λ1=0, φ1=0;
        Matrix D0=null, D1=null, D2=null;
        // x2 and y2 defined inside the loop.
        boolean isDerivativeSupported = true;
        for (int i=0; i<=8; i++) {
            /*
             * Iteration order (center must be last):
             *
             *   (6)────(5)────(4)
             *    |             |
             *   (7)    (8)    (3)
             *    |             |
             *   (0)────(1)────(2)
             */
            double λ2, φ2;
            switch (i) {
                case 0: case 6: case 7: λ2 = envelope.getMinX();    break;
                case 1: case 5: case 8: λ2 = envelope.getCenterX(); break;
                case 2: case 3: case 4: λ2 = envelope.getMaxX();    break;
                default: throw new AssertionError(i);
            }
            switch (i) {
                case 0: case 1: case 2: φ2 = envelope.getMinY();    break;
                case 3: case 7: case 8: φ2 = envelope.getCenterY(); break;
                case 4: case 5: case 6: φ2 = envelope.getMaxY();    break;
                default: throw new AssertionError(i);
            }
            point[0] = λ2;
            point[1] = φ2;
            try {
                D1 = D2;
                D2 = derivativeAndTransform(transform, point, point, 0, isDerivativeSupported && i != 8);
            } catch (TransformException e) {
                if (!isDerivativeSupported) {
                    throw e; // Derivative were already disabled, so something went wrong.
                }
                isDerivativeSupported = false; D2 = null;
                point[0] = λ2;
                point[1] = φ2;
                transform.transform(point, 0, point, 0, 1);
                recoverableException(e); // Log only if the above call was successful.
            }
            double x2 = point[0];
            double y2 = point[1];
            if (x2 < xmin) xmin = x2;
            if (x2 > xmax) xmax = x2;
            if (y2 < ymin) ymin = y2;
            if (y2 > ymax) ymax = y2;
            switch (i) {
                case 0: { // Remember the first point.
                    λ0=λ2; x0=x2;
                    φ0=φ2; y0=y2;
                    D0=D2;
                    break;
                }
                case 8: { // Close the iteration with the first point.
                    λ2=λ0; x2=x0; // Discard P2 because it is the rectangle center.
                    φ2=φ0; y2=y0;
                    D2=D0;
                    break;
                }
            }
            /*
             * At this point, we expanded the rectangle using the projected points. Now try
             * to use the information provided by derivatives at those points, if available.
             * For the following block, notation is:
             *
             *   - s  are ordinate values in the source space (λ or φ)
             *   - t  are ordinate values in the target space (x or y)
             *
             * They are not necessarily in the same dimension. For example would could have
             * s=λ while t=y. This is typically the case when inspecting the top or bottom
             * line segment of the rectangle.
             *
             * The same technic is also applied in the transform(MathTransform, Envelope) method.
             * The general method is more "elegant", at the cost of more storage requirement.
             */
            if (D1 != null && D2 != null) {
                final int srcDim;
                final double s1, s2; // Ordinate values in source space (before projection)
                switch (i) {
                    case 1: case 2: case 5: case 6: {assert φ2==φ1; srcDim=0; s1=λ1; s2=λ2; break;} // Horizontal segment
                    case 3: case 4: case 7: case 8: {assert λ2==λ1; srcDim=1; s1=φ1; s2=φ2; break;} // Vertical segment
                    default: throw new AssertionError(i);
                }
                final double min, max;
                if (s1 < s2) {min=s1; max=s2;}
                else         {min=s2; max=s1;}
                int tgtDim = 0;
                do { // Executed exactly twice, for dimensions 0 and 1 in the projected space.
                    final Line2D.Double extremum = ShapeUtilities.cubicCurveExtremum(
                            s1, (tgtDim == 0) ? x1 : y1, D1.getElement(tgtDim, srcDim),
                            s2, (tgtDim == 0) ? x2 : y2, D2.getElement(tgtDim, srcDim));
                    /*
                     * At this point we found the extremum of the projected line segment
                     * using a cubic curve t = A + Bs + Cs² + Ds³ approximation.  Before
                     * to add those extremum into the projected bounding box, we need to
                     * ensure that the source ordinate is inside the the original
                     * (unprojected) bounding box.
                     */
                    boolean isP2 = false;
                    do { // Executed exactly twice, one for each point.
                        final double se = isP2 ? extremum.x2 : extremum.x1;
                        if (se > min && se < max) {
                            final double te = isP2 ? extremum.y2 : extremum.y1;
                            if ((tgtDim == 0) ? (te < xmin || te > xmax) : (te < ymin || te > ymax)) {
                                /*
                                 * At this point, we have determined that adding the extremum point
                                 * to the rectangle would have expanded it. However we will not add
                                 * that point directly, because maybe its position is not quite right
                                 * (since we used a cubic curve approximation). Instead, we project
                                 * the point on the rectangle border which is located vis-à-vis the
                                 * extremum. Our tests show that the correction can be as much as 50
                                 * metres.
                                 */
                                final double oldX = point[0];
                                final double oldY = point[1];
                                if (srcDim == 0) {
                                    point[0] = se;
                                    point[1] = φ1; // == φ2 since we have an horizontal segment.
                                } else {
                                    point[0] = λ1; // == λ2 since we have a vertical segment.
                                    point[1] = se;
                                }
                                transform.transform(point, 0, point, 0, 1);
                                final double x = point[0];
                                final double y = point[1];
                                if (x < xmin) xmin = x;
                                if (x > xmax) xmax = x;
                                if (y < ymin) ymin = y;
                                if (y > ymax) ymax = y;
                                point[0] = oldX;
                                point[1] = oldY;
                            }
                        }
                    } while ((isP2 = !isP2) == true);
                } while (++tgtDim == 1);
            }
            λ1=λ2; x1=x2;
            φ1=φ2; y1=y2;
            D1=D2;
        }
        if (destination != null) {
            destination.setRect(xmin, ymin, xmax-xmin, ymax-ymin);
        } else {
            destination = XRectangle2D.createFromExtremums(xmin, ymin, xmax, ymax);
        }
        /*
         * Note: a previous version had an "assert" statement here comparing our calculation
         * with the calculation performed by the more general method working on Envelope. We
         * verified that the same values (coordinate points and derivatives) were ultimately
         * passed to the ShapeUtilities.cubicCurveExtremum(...) method, so we would expect
         * the same result. However the iteration order is different. The result seems
         * insensitive to iteration order most of the time, but not always. However, it seems
         * that the cases were the results are different are the cases where the methods working
         * with CoordinateOperation object wipe out that difference anyway.
         */
        return destination;
    }

    /**
     * Transforms a rectangular envelope using the given {@linkplain CoordinateOperation coordinate
     * operation}. The transformation is only approximative: the returned envelope may be bigger
     * than the smallest possible bounding box, but should not be smaller in most cases.
     * <p>
     * Invoking this method is equivalent to invoking the following:
     *
     * {@preformat java
     *     transform(operation, new GeneralEnvelope(envelope)).toRectangle2D()
     * }
     *
     * This method can handle the case where the rectangle contains the North or South pole,
     * or when it cross the &plusmn;180&deg; longitude.
     *
     * @param  operation The operation to use. Source and target dimension must be 2.
     * @param  envelope The rectangle to transform (may be {@code null}).
     * @param  destination The destination rectangle (may be {@code envelope}).
     *         If {@code null}, a new rectangle will be created and returned.
     * @return {@code destination}, or a new rectangle if {@code destination} was non-null
     *         and {@code envelope} was null.
     * @throws TransformException if a transform failed.
     *
     * @see #transform(MathTransform2D, Rectangle2D, Rectangle2D)
     * @see org.geotoolkit.referencing.operation.matrix.XAffineTransform#transform(AffineTransform, Rectangle2D, Rectangle2D)
     */
    public static Rectangle2D transform(final CoordinateOperation operation,
                                        final Rectangle2D         envelope,
                                              Rectangle2D         destination)
            throws TransformException
    {
        ensureNonNull("operation", operation);
        if (envelope == null) {
            return null;
        }
        final MathTransform transform = operation.getMathTransform();
        if (!(transform instanceof MathTransform2D)) {
            throw new MismatchedDimensionException(Errors.format(Errors.Keys.NO_TRANSFORM2D_AVAILABLE));
        }
        MathTransform2D mt = (MathTransform2D) transform;
        final double[] center = new double[2];
        destination = transform(mt, envelope, destination, center);
        /*
         * If the source envelope crosses the expected range of valid coordinates, also projects
         * the range bounds as a safety. See the comments in transform(Envelope, ...).
         */
        final CoordinateReferenceSystem sourceCRS = operation.getSourceCRS();
        if (sourceCRS != null) {
            final CoordinateSystem cs = sourceCRS.getCoordinateSystem();
            if (cs != null && cs.getDimension() == 2) { // Paranoiac check.
                CoordinateSystemAxis axis = cs.getAxis(0);
                double min = envelope.getMinX();
                double max = envelope.getMaxX();
                Point2D.Double pt = null;
                for (int i=0; i<4; i++) {
                    if (i == 2) {
                        axis = cs.getAxis(1);
                        min = envelope.getMinY();
                        max = envelope.getMaxY();
                    }
                    final double v = (i & 1) == 0 ? axis.getMinimumValue() : axis.getMaximumValue();
                    if (!(v > min && v < max)) {
                        continue;
                    }
                    if (pt == null) {
                        pt = new Point2D.Double();
                    }
                    if ((i & 2) == 0) {
                        pt.x = v;
                        pt.y = envelope.getCenterY();
                    } else {
                        pt.x = envelope.getCenterX();
                        pt.y = v;
                    }
                    destination.add(mt.transform(pt, pt));
                }
            }
        }
        /*
         * Now takes the target CRS in account...
         */
        final CoordinateReferenceSystem targetCRS = operation.getTargetCRS();
        if (targetCRS == null) {
            return destination;
        }
        final CoordinateSystem targetCS = targetCRS.getCoordinateSystem();
        if (targetCS == null || targetCS.getDimension() != 2) {
            // It should be an error, but we keep this method tolerant.
            return destination;
        }
        /*
         * Checks for singularity points. See the transform(CoordinateOperation, Envelope)
         * method for comments about the algorithm. The code below is the same algorithm
         * adapted for the 2D case and the related objects (Point2D, Rectangle2D, etc.).
         *
         * The 'border' variable in the loop below is used in order to compress 2 dimensions
         * and 2 extremums in a single loop, in this order: (xmin, xmax, ymin, ymax).
         */
        TransformException warning = null;
        Point2D sourcePt = null;
        Point2D targetPt = null;
        int includedBoundsValue = 0; // A bitmask for each (dimension, extremum) pairs.
        for (int border=0; border<4; border++) { // 2 dimensions and 2 extremums compacted in a flag.
            final int dimension = border >>> 1;  // The dimension index being examined.
            final CoordinateSystemAxis axis = targetCS.getAxis(dimension);
            if (axis == null) { // Should never be null, but check as a paranoiac safety.
                continue;
            }
            final double extremum = (border & 1) == 0 ? axis.getMinimumValue() : axis.getMaximumValue();
            if (Double.isInfinite(extremum) || Double.isNaN(extremum)) {
                continue;
            }
            if (targetPt == null) {
                try {
                    mt = mt.inverse();
                } catch (NoninvertibleTransformException exception) {
                    recoverableException(exception);
                    return destination;
                }
                targetPt = new Point2D.Double();
            }
            switch (dimension) {
                case 0: targetPt.setLocation(extremum,  center[1]); break;
                case 1: targetPt.setLocation(center[0], extremum ); break;
                default: throw new AssertionError(border);
            }
            try {
                sourcePt = mt.transform(targetPt, sourcePt);
            } catch (TransformException exception) {
                if (warning == null) {
                    warning = exception;
                } else {
                    warning.addSuppressed(exception);
                }
                continue;
            }
            if (envelope.contains(sourcePt)) {
                destination.add(targetPt);
                includedBoundsValue |= (1 << border);
            }
        }
        /*
         * Iterate over all dimensions of type "WRAPAROUND" for which minimal or maximal axis
         * values have not yet been included in the envelope. We could inline this check inside
         * the above loop, but we don't in order to have a chance to exclude the dimensions for
         * which the point have already been added.
         *
         * See transform(CoordinateOperation, Envelope) for more comments about the algorithm.
         */
        if (includedBoundsValue != 0) {
            /*
             * Bits mask transformation:
             *   1) Swaps the two dimensions               (YyXx  →  XxYy)
             *   2) Insert a space between each bits       (XxYy  →  X.x.Y.y.)
             *   3) Fill the space with duplicated values  (X.x.Y.y.  →  XXxxYYyy)
             *
             * In terms of bit positions 1,2,4,8 (not bit values), we have:
             *
             *   8421  →  22881144
             *   i.e. (ymax, ymin, xmax, xmin)  →  (xmax², ymax², xmin², ymin²)
             *
             * Now look at the last part: (xmin², ymin²). The next step is to perform a bitwise
             * AND operation in order to have only both of the following conditions:
             *
             *   Borders not yet added to the envelope: ~(ymax, ymin, xmax, xmin)
             *   Borders in which a singularity exists:  (xmin, xmin, ymin, ymin)
             *
             * The same operation is repeated on the next 4 bits for (xmax, xmax, ymax, ymax).
             */
            int toTest = ((includedBoundsValue & 1) << 3) | ((includedBoundsValue & 4) >>> 1) |
                         ((includedBoundsValue & 2) << 6) | ((includedBoundsValue & 8) << 2);
            toTest |= (toTest >>> 1); // Duplicate the bit values.
            toTest &= ~(includedBoundsValue | (includedBoundsValue << 4));
            /*
             * Forget any axes that are not of kind "WRAPAROUND". Then get the final
             * bit pattern indicating which points to test. Iterate over that bits.
             */
            if ((toTest & 0x33333333) != 0 && !isWrapAround(targetCS.getAxis(0))) toTest &= 0xCCCCCCCC;
            if ((toTest & 0xCCCCCCCC) != 0 && !isWrapAround(targetCS.getAxis(1))) toTest &= 0x33333333;
            while (toTest != 0) {
                final int border = Integer.numberOfTrailingZeros(toTest);
                final int bitMask = 1 << border;
                toTest &= ~bitMask; // Clear now the bit, for the next iteration.
                final int dimensionToAdd = (border >>> 1) & 1;
                final CoordinateSystemAxis toAdd = targetCS.getAxis(dimensionToAdd);
                final CoordinateSystemAxis added = targetCS.getAxis(dimensionToAdd ^ 1);
                double x = (border & 1) == 0 ? toAdd.getMinimumValue() : toAdd.getMaximumValue();
                double y = (border & 4) == 0 ? added.getMinimumValue() : added.getMaximumValue();
                if (dimensionToAdd != 0) {
                    final double t=x; x=y; y=t;
                }
                targetPt.setLocation(x, y);
                try {
                    sourcePt = mt.transform(targetPt, sourcePt);
                } catch (TransformException exception) {
                    if (warning == null) {
                        warning = exception;
                    } else {
                        warning.addSuppressed(exception);
                    }
                    continue;
                }
                if (envelope.contains(sourcePt)) {
                    destination.add(targetPt);
                }
            }
        }
        if (warning != null) {
            recoverableException(warning);
        }
        return destination;
    }

    /**
     * Returns {@code true} if the given axis is of kind "Wrap Around".
     */
    private static boolean isWrapAround(final CoordinateSystemAxis axis) {
        return RangeMeaning.WRAPAROUND.equals(axis.getRangeMeaning());
    }

    /**
     * Invoked when a recoverable exception occurred. Those exceptions must be minor enough
     * that they can be silently ignored in most cases.
     */
    private static void recoverableException(final TransformException exception) {
        Logging.recoverableException(Envelopes.class, "transform", exception);
    }
    
    /**
     * Returns {@code true} if {@link Envelope} contain at least one 
     * {@link Double#NaN} value, else {@code false}.
     * 
     * @param envelope the envelope which will be verify.
     * @return {@code true} if {@link Envelope} contain at least one {@link Double#NaN} value, else {@code false}.
     */
    public static boolean containNAN(final Envelope envelope) {
        ArgumentChecks.ensureNonNull("Envelopes.containNAN()", envelope);
        for (int d = 0, dim = envelope.getDimension(); d < dim; d++) {
            if (Double.isNaN(envelope.getMinimum(d))
             || Double.isNaN(envelope.getMaximum(d))) return true;
        }
        return false;
    }
}
