/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.util.XArrays;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.NullArgumentException;


/**
 * Utility methods related to a pair of {@link Vector} objects. The {@code VectorPair} constructor
 * expects two vectors in argument, namely <var>X</var> and <var>Y</var>. Every operations defined
 * in this class create <cite>views</cite> of the given vectors; they never copy or calculate data
 * values. Because they are views, callers should not change the values of the original vectors,
 * unless propagation to the views is really wanted.
 *
 * {@section Serialization}
 * {@code VectorPair} is serializable if the two original vectors are serializable
 * (which they usually are).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
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
     * Makes sure an argument is non-null.
     *
     * @param  name   Argument name.
     * @param  object User argument.
     * @throws NullArgumentException if {@code object} is null.
     */
    private static void ensureNonNull(String name, Object object) throws NullArgumentException {
        if (object == null) {
            throw new NullArgumentException(Errors.format(Errors.Keys.NULL_ARGUMENT_$1, name));
        }
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
            int indices[] = null;
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
                indices = XArrays.resize(indices, count);
                this.X = X.view(indices);
                this.Y = Y.view(indices);
            }
        }
    }

    /**
     * Computes views of (<var>X</var>,<var>Y</var>) vectors where every diagonal line is replaced
     * by a horizontal line followed by a vertical line. For this purpose, every <var>x</var> and
     * <var>y</var> values are repeated once. A graph of the resulting vectors would have the visual
     * appareance of a stair, or the outer limit of histograms.
     * <p>
     * This method can be used before to plot (<var>X</var>,<var>Y</var>) data where <var>X</var>
     * can takes only some fixed values, in order to visualy emphase its discontinuous nature.
     *
     * {@section On input}
     * Lets define <var>X</var><sub>i</sub> and <var>Y</var><sub>i</sub> the input vectors before
     * this method is invoked. If <var>s</var> is the length of the <var>Y</var><sub>i</sub> vector,
     * then the length of the <var>X</var><sub>i</sub> vector must be <var>s</var>+1.
     *
     * {@section On output}
     * Lets define <var>X</var><sub>o</sub> and <var>Y</var><sub>o</sub> the output vectors after
     * this method has been invoked. The length of those two vectors will be 2<var>s</var>. The
     * first point and the last point in the output vectors will be the same than the first point
     * and the last point in the input vectors. More specifically:
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
     * @param  direction Controls the order of horizontal and vertical lines. If zero (neutral),
     *         then the order of line segments will always be <cite>horizontal line followed by
     *         vertical line</cite>, which produce the appearance of an histogram outer limit.
     *         If positive or negative, then some vertical lines may appear before their horizontal
     *         counterpart, when it makes the value of <var>y</var> higher (if {@code direction} is
     *         positive) or lower (if {@code direction} is negative) than the would otherwise be.
     * @throws MismatchedSizeException If the length of the <var>X</var> vector is not equal to
     *         the length of the <var>Y</var> vector + 1.
     *
     * @todo Value of {@code direction} different than zero are not yet implemented.
     */
    public void makeStepwise(final int direction) throws MismatchedSizeException {
        final int length = Y.size();
        if (length+1 != X.size()) {
            throw new MismatchedSizeException();
        }
        final int[] indices = new int[length*2];
        for (int i=0,j=0; i<length; i++) {
            indices[j++] = indices[j++] = i;
        }
        Y = Y.view(indices);
        System.arraycopy(indices, 1, indices, 0, indices.length-1);
        indices[indices.length-1] = length;
        X = X.view(indices);
    }
}
