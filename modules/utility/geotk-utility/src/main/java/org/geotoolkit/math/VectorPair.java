/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.math;

import java.awt.geom.Line2D;
import java.io.Serializable;
import javax.vecmath.MismatchedSizeException;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Utility methods related to a pair of {@link Vector} objects. The {@code VectorPair} constructor
 * expects two vectors in argument, namely <var>X</var> and <var>Y</var>. Every operations defined
 * in this class create <cite>views</cite> of the given vectors; they never copy or calculate data
 * values. Because they are views, callers should not change the values of the original vectors,
 * unless propagation to the views is really wanted.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public class VectorPair implements Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 8330893190189236019L;

    /**
     * The vectors specified to the {@code VectorPair} constructor,
     * or the result of the last operation performed on this object.
     */
    protected Vector X, Y;

    /**
     * Creates a new pair of vector. Whatever the two given vectors need to have the
     * same length or not depends on the operation to be applied on this pair of vectors.
     *
     * @param X The first vector of the pair.
     * @param Y The second vector of the pair.
     */
    public VectorPair(final Vector X, final Vector Y) {
        ensureNonNull("X", this.X = X);
        ensureNonNull("Y", this.Y = Y);
    }

    /**
     * Returns the vector of <var>x</var> values.
     *
     * @return The vector of <var>x</var> values.
     */
    public Vector getX() {
        return X;
    }

    /**
     * Returns the vector of <var>y</var> values.
     *
     * @return The vector of <var>y</var> values.
     */
    public Vector getY() {
        return Y;
    }

    /**
     * Returns the length of the two vectors, or thrown an exception if they don't
     * have the same length.
     *
     * @return The vector length, which is the same for the two vectors.
     * @throws MismatchedSizeException if the two vectors don't have the same length.
     */
    public int length() throws MismatchedSizeException {
        final int length = Y.size();
        if (length != X.size()) {
            throw new MismatchedSizeException(Errors.format(Errors.Keys.MISMATCHED_ARRAY_LENGTH));
        }
        return length;
    }

    /**
     * Merges consecutive colinear segments, if any. More specifically for any index <var>i</var>,
     * if the point having the coordinate {@code (x[i], y[i])} lies on the line segment defined by
     * the two end points {@code (x[i-1], y[i-1])} and {@code (x[i+1], y[i+1])}, then the point at
     * index <var>i</var> will be dropped.
     * <p>
     * If the (<var>X</var>,<var>Y</var>) vectors are data to be plotted, then this method can be
     * used for simplifying the data without visual impact.
     *
     * @param  xTolerance The maximal distance measured along the <var>x</var> axis to consider
     *         that a point lies on a line segment.
     * @param  yTolerance The maximal distance measured along the <var>y</var> axis to consider
     *         that a point lies on a line segment.
     * @throws MismatchedSizeException If the <var>X</var> and <var>Y</var> vectors don't have
     *         the same length.
     */
    public void omitColinearPoints(final double xTolerance, double yTolerance) throws MismatchedSizeException {
        final int length = length(); // Invoked first for forcing an inconditional check of vectors length.
        final double ratio = yTolerance / xTolerance;
        if (!Double.isNaN(ratio)) {
            final Vector X = this.X;
            final Vector Y = this.Y;
            int[] indices  = null;
            int count = 0;
            if (length >= 3) {
                yTolerance *= yTolerance;
                double x1 = X.doubleValue(0) * ratio;
                double y1 = Y.doubleValue(0);
                double x2 = X.doubleValue(1) * ratio;
                double y2 = Y.doubleValue(1);
                for (int i=2; i<length; i++) {
                    double x3 = X.doubleValue(i) * ratio;
                    double y3 = Y.doubleValue(i);
                    final double dsq = Line2D.ptSegDistSq(x1, y1, x3, y3, x2, y2);
                    if (dsq <= yTolerance) {
                        // Found a colinear point: (x2,y2) is on the line segment (x1,y1)-(x3,y3)
                        if (indices == null) {
                            indices = new int[length - 1];
                            while (count < i) {
                                indices[count] = count++;
                            }
                        }
                        // Overwrite the point in the middle (x2,y2) with the last one (x3,y3).
                        indices[count-1] = i;
                    } else {
                        // The point is not colinear, so remember that we must keep it.
                        if (indices != null) {
                            indices[count++] = i;
                        }
                        x1 = x2;
                        y1 = y2;
                    }
                    x2 = x3;
                    y2 = y3;
                }
            }
            if (indices != null) {
                indices = ArraysExt.resize(indices, count);
                this.X = X.view(indices);
                this.Y = Y.view(indices);
            }
        }
    }

    /**
     * Computes views of (<var>X</var>,<var>Y</var>) vectors where every diagonal line is replaced
     * by a horizontal line followed by a vertical line. For this purpose, every <var>x</var> and
     * <var>y</var> values are repeated once. A graph of the resulting vectors would have the visual
     * appareance of a stair, or the outer limit of a histogram.
     * <p>
     * When invoked with {@code direction=0}, This method can be used before to plot
     * (<var>X</var>,<var>Y</var>) data where <var>X</var> can takes only some fixed values,
     * in order to visualy emphase its discontinuous nature. When invoked with positive or
     * negative {@code direction} argument, this method can be used for plotting upper or lower
     * limit respectively (for example computed from a standard deviation) of the above data.
     *
     * {@section On input}
     * Starting with <var>X</var><sub>i</sub> and <var>Y</var><sub>i</sub> as the input vectors before
     * this method is invoked, take <var>s</var> to be the length of the <var>Y</var><sub>i</sub> vector,
     * then the length of the <var>X</var><sub>i</sub> vector must be <var>s</var>+1.
     *
     * {@section On output}
     * Let define <var>X</var><sub>o</sub> and <var>Y</var><sub>o</sub> the output vectors after
     * this method has been invoked. The length of those two vectors will be at most 2<var>s</var>
     * (they will be exactly of that length if {@code abs(direction) <= 1}). The first point and the
     * last point in the output vectors will be the same than the first point and the last point in
     * the input vectors. More specifically, assuming that the output vectors have the maximal length:
     * <p>
     * <ul>
     *   <li>(<var>X</var><sub>o</sub>[0], <var>Y</var><sub>o</sub>[0]) =
     *       (<var>X</var><sub>i</sub>[0], <var>Y</var><sub>i</sub>[0])</li>
     *   <li>(<var>X</var><sub>o</sub>[2s-1], <var>Y</var><sub>o</sub>[2s-1]) =
     *       (<var>X</var><sub>i</sub>[s], <var>Y</var><sub>i</sub>[s-1])</li>
     * </ul>
     * <p>
     * It is often a good idea to invoke {@link #omitColinearPoints} after this method.
     *
     * @param direction Controls the order of horizontal and vertical lines.<ul>
     *     <li>If zero (neutral), then the order of line segments will always be <cite>horizontal
     *         line followed by vertical line</cite>, which produce the appearance of a histogram
     *         outer limit.</li>
     *     <li>If +1 or -1, then some vertical lines may appear before their horizontal counterpart,
     *         when it makes the value of <var>y</var> higher (if {@code direction} is +1) or lower
     *         (if {@code direction} is -1) than they would otherwise be. So {@code direction} can
     *         be interpreted as the sign of the allowed change of <var>y</var> values.</li>
     *     <li>If +2 or -2, then the same reordering than +1 or -1 is executed, followed by:<ul>
     *         <li>If {@code direction} is +2, the removal of lower point when the <var>y</var>
     *             values are going down and up again at the same <var>x</var> value.</li>
     *         <li>If {@code direction} is -2, the removal of higher point when the <var>y</var>
     *             values are going up and down again at the same <var>x</var> value.</li>
     *         </ul>
     *         So +2 and -2 argument can be interpreted as shifting the main <var>y</var> value
     *         toward positive or negative infinity slightly more than what +1 or -1 does.</li>
     *   </ul>
     * @throws MismatchedSizeException If the length of the <var>X</var> vector is not equal to
     *         the length of the <var>Y</var> vector + 1.
     */
    public void makeStepwise(int direction) throws MismatchedSizeException {
        final Vector X = this.X;
        final Vector Y = this.Y;
        final int length = Y.size();
        if (length+1 != X.size()) {
            throw new MismatchedSizeException();
        }
        int[] Xi = new int[length*2];
        int[] Yi = new int[length*2];
        if (length != 0) {
            boolean swap = false;
            double x0 = X.doubleValue(0);
            double y0 = Y.doubleValue(0);
            for (int i=0,j=0; i<length; i++) {
                if (direction != 0) {
                    if (i+1 == length) {
                        // We have reached the end of the vector. Never swap the last
                        // index, otherwise we get an IndexOutOfBoundsException.
                        swap = false;
                    } else {
                        final double xi = X.doubleValue(i+1);
                        final double yi = Y.doubleValue(i+1);
                        int xs = (xi > x0) ? +1 : (xi < x0) ? -1 : 0; // We want 0 for NaN.
                        if (xs != 0) {
                            boolean up = (direction >= 0) ^ (xs < 0) ^ swap;
                            if (up ? (yi > y0) : (yi < y0)) {
                                swap = !swap;
                            }
                        }
                        x0 = xi;
                        y0 = yi;
                    }
                }
                Xi[j]   = i;
                Yi[j++] = i;
                Xi[j]   = swap ? i : i+1;
                Yi[j++] = swap ? i+1 : i;
            }
            assert ArraysExt.isSorted(Xi, false);
            assert ArraysExt.isSorted(Yi, false);
            /*
             * At this point we are done. However if 'direction' is different than 0, then the
             * index swapping may have caused situations where a Y value goes down, then up at
             * the same X value.  The code below will erase the down point (or the opposite if
             * 'direction' is negative instead than positive).
             */
            if (Math.abs(direction) >= 2) {
                direction = XMath.sgn(direction);
                int size = Xi.length;
                for (int i=size; --i>=2;) {
                    if (Xi[i] == Xi[i-2]) {
                        final double y1, y2;
                        y0 = Y.doubleValue(Yi[i-2]) * direction;
                        y1 = Y.doubleValue(Yi[i-1]) * direction;
                        y2 = Y.doubleValue(Yi[i  ]) * direction;
                        if (y1 < Math.min(y0, y2)) {
                            System.arraycopy(Xi, i, Xi, i-1, size-i);
                            System.arraycopy(Yi, i, Yi, i-1, size-i);
                            size--;
                        }
                    }
                }
                Xi = ArraysExt.resize(Xi, size);
                Yi = ArraysExt.resize(Yi, size);
            }
        }
        this.X = X.view(Xi);
        this.Y = Y.view(Yi);
    }
}
