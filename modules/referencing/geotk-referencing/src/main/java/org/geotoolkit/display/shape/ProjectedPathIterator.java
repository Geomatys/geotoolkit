/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.display.shape;

import java.awt.geom.Point2D;
import java.awt.geom.PathIterator;
import java.awt.geom.IllegalPathStateException;

import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.operation.MathTransforms;

import static java.lang.Math.*;
import static org.apache.sis.math.MathFunctions.SQRT_2;


/**
 * A path iterator which applies a map projection on the fly. This iterator extends
 * {@link Point2D.Double} for opportunist reasons only - users should not rely on this
 * implementation details.
 * <p>
 * The point inherited by this class is the location of the last point of the last
 * call to a {@code currentSegment(...)} method. This location has been transformed
 * by the {@linkplain #projection}.
 *
 * @author Rémi Maréchal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
@SuppressWarnings("serial")
final class ProjectedPathIterator extends Point2D.Double implements PathIterator {
    /**
     * Tolerance factor for determining when to replace a curve by a straight line.
     */
    private static final double TOLERANCE = 1E-5;

    /**
     * The original path iterator.
     */
    private final PathIterator iterator;

    /**
     * The transform to apply on the values returned by the path iterator.
     * This transform should be the concatenation of the map projection
     * given to the {@link ProjectedShape} constructor, together with the
     * affine transform given to the {@code getPathIterator(...)} method.
     */
    private final MathTransform2D projection;

    /**
     * A temporary buffer used when the {@link #currentSegment(float[])} method is invoked.
     * Note that this is the case when the shape is drawn with Java2D.
     */
    private final double[] buffer;

    /**
     * The original ordinate values of the last iteration step, before transformation.
     * The values of the last iteration steps after transformation are (x,y).
     */
    private transient double λ, φ;

    /**
     * The ordinate values of the last "move to" operation, both as original ordinates
     * (λ,φ) and projected ordinates (x,y).
     */
    private transient double λ0, φ0, x0, y0;

    /**
     * Derivative of the previous iteration step, or {@code null} if none.
     */
    private transient Matrix derivative;

    /**
     * Control points computed by {@link #transformSegment}.
     */
    private transient double ctrlx1, ctrly1, ctrlx2, ctrly2;

    /**
     * {@code true} if the next call to the {@code currentSegment} shall return {@code SEG_CLOSE}.
     */
    private transient boolean isClosing;

    /**
     * Creates a new path iterator which will apply the given transform on the
     * points returned by the given iterator.
     */
    ProjectedPathIterator(final PathIterator iterator, final MathTransform2D projection) {
        this.iterator   = iterator;
        this.projection = projection;
        this.buffer     = new double[2];
    }

    /**
     * Returns the winding rule specified by the original iterator.
     */
    @Override
    public int getWindingRule() {
        return iterator.getWindingRule();
    }

    /**
     * Returns {@code true} if there is no more points to iterate over.
     */
    @Override
    public boolean isDone() {
        return !isClosing && iterator.isDone();
    }

    /**
     * Moves the iterator to the next segment.
     */
    @Override
    public void next() {
        if (!isClosing) {
            iterator.next();
        }
    }

    /**
     * Transforms the given number of points in the given array. The last points is stored in the
     * (λ,φ) and (x,y) fields of this iterator, in order to be reused during the next iteration.
     */
    private void transform(final double[] coords, final int n) throws TransformException {
        final int i0 = (n-1) << 1;
        final int i1 = i0 + 1;
        λ = coords[i0];
        φ = coords[i1];
        projection.transform(coords, 0, coords, 0, n);
        x = coords[i0];
        y = coords[i1];
        derivative = null;
    }

    /**
     * Same method than above, but for floating point values. The last point is transformed
     * in a special way in order to keep the {@code double} precision.
     */
    private void transform(final float[] coords, int n) throws TransformException {
        if (--n != 0) {
            projection.transform(coords, 0, coords, 0, n);
        }
        final int i0 = n << 1;
        final int i1 = i0 + 1;
        x = λ = coords[i0];
        y = φ = coords[i1];
        final Point2D p = projection.transform(this, this);
        if (p != this) { // paranoiac check (should never happen)
            setLocation(p);
        }
        coords[i0] = (float) x;
        coords[i1] = (float) y;
        derivative = null;
    }

    /**
     * Returns the coordinates and type of the current path segment in the iteration.
     * This method handles line segments in a special way, by attempting to replace
     * them by a curve computed from the derivative at the two end points.
     */
    @Override
    @SuppressWarnings("fallthrough")
    public int currentSegment(final double[] coords) {
        if (isClosing) {
            isClosing = false;
            return SEG_CLOSE;
        }
        int type = iterator.currentSegment(coords);
        try {
            switch (type) {
                case SEG_MOVETO: {
                    λ0 = coords[0];
                    φ0 = coords[1];
                    transform(coords, 1);
                    x0 = coords[0];
                    y0 = coords[1];
                    break;
                }
                case SEG_QUADTO:  transform(coords, 2); break;
                case SEG_CUBICTO: transform(coords, 3); break;
                case SEG_CLOSE: {
                    if (x0 == x && y0 == y) {
                        break;
                    } else {
                        isClosing = true;
                        coords[0] = λ0;
                        coords[1] = φ0;
                    }
                    // Fall through
                }
                case SEG_LINETO: {
                    final double x1 = x;
                    final double y1 = y;
                    final double Δλ = coords[0] - λ;
                    final double Δφ = coords[1] - φ;
                    Matrix D1 = derivative;
                    if (D1 == null) {
                        x = λ;
                        y = φ;
                        D1 = projection.derivative(this);
                    }
                    λ = coords[0];
                    φ = coords[1];
                    derivativeAndTransform(coords);
                    type = transformSegment(Δλ, Δφ, x1, y1, D1);
                    int i=0;
                    switch (type) {
                        case SEG_CUBICTO: coords[i++] = ctrlx1; coords[i++] = ctrly1; // Fallthrough
                        case SEG_QUADTO:  coords[i++] = ctrlx2; coords[i++] = ctrly2; // Fallthrough
                        case SEG_LINETO:  coords[i++] =     x;  coords[i++] =     y;
                    }
                    break;
                }
            }
        } catch (TransformException cause) {
            IllegalPathStateException ex = new IllegalPathStateException(cause.getLocalizedMessage());
            ex.initCause(cause);
            throw ex;
        }
        return type;
    }

    /**
     * A copy of the above method, adapted for {@code float} arrays.
     */
    @Override
    @SuppressWarnings("fallthrough")
    public int currentSegment(final float[] coords) {
        if (isClosing) {
            isClosing = false;
            return SEG_CLOSE;
        }
        // Following block is for debugging purpose only. Set the condition to 'true' in order
        // to delegates to the method using full 'double' precision. This is sometime useful
        // for making sure that the results are the same.
        if (false) {
            final double[] buffer = new double[min(coords.length, 6)];
            final int type = currentSegment(buffer);
            for (int i=0; i<buffer.length; i++) {
                coords[i] = (float) buffer[i];
            }
            return type;
        }
        // Copy of currentSegment(double[]). Note that this copy contains many implicit casts
        // from 'float' to 'double'. So while the source code looks close to identical to the
        // above method, the compiled code is actually not the same. The part which is really
        // the same has been factorized out in the transformSegment(...) method.
        int type = iterator.currentSegment(coords);
        try {
            switch (type) {
                case SEG_MOVETO: {
                    λ0 = coords[0];
                    φ0 = coords[1];
                    transform(coords, 1);
                    x0 = coords[0];
                    y0 = coords[1];
                    break;
                }
                case SEG_QUADTO:  transform(coords, 2); break;
                case SEG_CUBICTO: transform(coords, 3); break;
                case SEG_CLOSE: {
                    if (x0 == x && y0 == y) {
                        break;
                    } else {
                        isClosing = true;
                        coords[0] = (float) λ0;
                        coords[1] = (float) φ0;
                    }
                    // Fall through
                }
                case SEG_LINETO: {
                    final double x1 = x;
                    final double y1 = y;
                    final double Δλ = coords[0] - λ;
                    final double Δφ = coords[1] - φ;
                    Matrix D1 = derivative;
                    if (D1 == null) {
                        x = λ;
                        y = φ;
                        D1 = projection.derivative(this);
                    }
                    buffer[0] = λ = coords[0];
                    buffer[1] = φ = coords[1];
                    derivativeAndTransform(buffer);
                    type = transformSegment(Δλ, Δφ, x1, y1, D1);
                    int i=0;
                    switch (type) {
                        case SEG_CUBICTO: coords[i++] = (float) ctrlx1; coords[i++] = (float) ctrly1; // Fallthrough
                        case SEG_QUADTO:  coords[i++] = (float) ctrlx2; coords[i++] = (float) ctrly2; // Fallthrough
                        case SEG_LINETO:  coords[i++] = (float)     x;  coords[i++] = (float)     y;
                    }
                    break;
                }
            }
        } catch (TransformException cause) {
            IllegalPathStateException ex = new IllegalPathStateException(cause.getLocalizedMessage());
            ex.initCause(cause);
            throw ex;
        }
        return type;
    }

    /**
     * Computes the normalized derivative at the given position and transforms that position.
     * The transformed position (if any) is stored in the ({@linkplain #x},{@linkplain #y})
     * fields. This method tries to perform the two operations in a single step if possible.
     */
    private void derivativeAndTransform(final double[] coords) throws TransformException {
        derivative = MathTransforms.derivativeAndTransform(projection, coords, 0, coords, 0);
        if (derivative == null) {
            throw new TransformException(Errors.format(Errors.Keys.CANT_COMPUTE_DERIVATIVE));
        }
        x = coords[0];
        y = coords[1];
    }

    /**
     * Transforms a line segment from P1 to P2, which may result in a quadratic or cubic curve.
     * <p>
     * <b>Notation:</b>
     * <ul>
     *   <li>The (λ,φ) variable names are ordinates in the source space.</li>
     *   <li>The (x,y) variable names are ordinates in the target space.</li>
     *   <li>The 1 and 2 suffixes denote the starting and ending points respectively.</li>
     *   <li>The Δ prefix denote the difference between the target and the source points.</li>
     * </ul>
     */
    private int transformSegment(
            final double Δλ, final double Δφ,
            final double x1, final double y1, final Matrix D1)
    {
        final double x2 = x;
        final double y2 = y;
        final Matrix D2 = derivative;

        // (Δx,Δy) is the vector from P1 to P2 in the (x,y) space.
        final double Δx = (x2 - x1);
        final double Δy = (y2 - y1);
        final double L12 = hypot(Δx, Δy);

        // (Δx1,Δy1) is the (Δx,Δy) vector that we would have if the derivative was D1 everywhere.
        // (Δx2,Δy2) is the (Δx,Δy) vector that we would have if the derivative was D2 everywhere.
        final double Δx1 = D1.getElement(0,0)*Δλ + D1.getElement(0,1)*Δφ;
        final double Δy1 = D1.getElement(1,0)*Δλ + D1.getElement(1,1)*Δφ;
        final double Δx2 = D2.getElement(0,0)*Δλ + D2.getElement(0,1)*Δφ;
        final double Δy2 = D2.getElement(1,0)*Δλ + D2.getElement(1,1)*Δφ;
        final double L1  = hypot(Δx1, Δy1);
        final double L2  = hypot(Δx2, Δy2);
        final double s1  = (Δx*Δx1 + Δy*Δy1) / (L12*L1);
        final double s2  = (Δx*Δx2 + Δy*Δy2) / (L12*L2);

        // Collinearity between two derivatives vectors and the segment.
        if (!(abs(abs(s1)-1) > TOLERANCE) && !(abs(abs(s2)-1) > TOLERANCE)) { // Use ! for catching NaN.
            return SEG_LINETO;
        }

        // Segment projection form circle bow, can be replaced by quadratique curve.
        if (s1 >= SQRT_2 / 2 && abs(s1-s2) <= TOLERANCE) {
            /*
             * Reference to Thomas W. Sederberg article : COMPUTER AIDED GEOMETRIC DESIGN
             * Computes α as a weight coefficient of the delta to add to the starting point.
             */
            final double α = abs((Δy2*Δx - Δx2*Δy) / (Δy2*Δx1 - Δx2*Δy1));
            ctrlx2 = x1 + Δx1*α;
            ctrly2 = y1 + Δy1*α;
            return SEG_QUADTO;
        }
        ctrlx1 = x1 + Δx1/3;
        ctrly1 = y1 + Δy1/3;
        ctrlx2 = x2 - Δx2/3;
        ctrly2 = y2 - Δy2/3;
        return SEG_CUBICTO;
    }
}
