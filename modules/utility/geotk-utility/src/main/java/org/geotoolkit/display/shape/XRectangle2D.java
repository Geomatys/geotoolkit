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
package org.geotoolkit.display.shape;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Serializable;
import org.apache.sis.internal.referencing.j2d.IntervalRectangle;



/**
 * Serializable rectangle capable to handle infinite values. Instead of using {@code x},
 * {@code y}, {@code width} and {@code height} fields, this class uses {@link #xmin},
 * {@link #xmax}, {@link #ymin} and {@link #ymax} fields. This choice provides two benefits:
 * <p>
 * <ul>
 *   <li>Allows this class to works correctly with {@linkplain java.lang.Double#POSITIVE_INFINITY
 *       infinites} and {@linkplain java.lang.Double#NaN NaN} values (the <var>width</var> and
 *       <var>height</var> alternative has ambiguities).</li>
 *   <li>Slightly faster {@code contains} and {@code intersects} methods since there is no
 *       addition or subtraction to perform.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.10
 *
 * @since 1.2
 * @module
 *
 * @deprecated There is no need anymore to use this class for serialization purpose since standard Java2D
 *             classes are now serializable. Furthermore code are encouraged to reduce Java2D dependency
 *             and use more {@code Envelope} instead, for better support of multi-dimensional data and
 *             easier portability to JavaFX or Android platforms.
 */
public class XRectangle2D extends IntervalRectangle implements Serializable {
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(xmin);
        out.writeDouble(xmax);
        out.writeDouble(ymin);
        out.writeDouble(ymax);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        xmin = in.readDouble();
        xmax = in.readDouble();
        ymin = in.readDouble();
        ymax = in.readDouble();
    }

    /**
     * A small number for testing intersection between an arbitrary shape and a rectangle.
     */
    private static final double EPS = 1E-6;

    /**
     * An immutable instance of a {@link Rectangle2D} with bounds extending toward
     * infinities. The {@code getMinX()} and {@code getMinY()} methods return always
     * {@linkplain java.lang.Double#NEGATIVE_INFINITY negative infinity} while the
     * {@code getMaxX()} and {@code getMaxY()} methods return always
     * {@linkplain java.lang.Double#POSITIVE_INFINITY positive infinity}.
     * <p>
     * This rectangle can be given as argument to the {@linkplain #XRectangle2D(Rectangle2D)
     * constructor} for initializing a new {@code XRectangle2D} to infinite bounds.
     */
    public static final Rectangle2D INFINITY = new InfiniteRectangle2D();

    /**
     * Constructs a default rectangle initialized to {@code (0,0,0,0)}.
     */
    public XRectangle2D() {
    }

    /**
     * Construct a rectangle with the specified location and dimension.
     * This constructor uses the same signature than {@link Rectangle2D}
     * for consistency with Java2D practice.
     *
     * @param x Minimal <var>x</var> ordinate value.
     * @param y Minimal <var>y</var> ordinate value.
     * @param width The rectangle width.
     * @param height The rectangle height.
     */
    public XRectangle2D(final double x, final double y, final double width, final double height) {
        this.xmin = x;
        this.ymin = y;
        this.xmax = x + width;
        this.ymax = y + height;
    }

    /**
     * Constructs a rectangle with the same coordinates than the supplied rectangle.
     *
     * @param rect The rectangle, or {@code null} in none (in which case this constructor
     *             is equivalents to the no-argument constructor). Use {@link #INFINITY} for
     *             initializing this {@code XRectangle2D} with infinite bounds.
     */
    public XRectangle2D(final Rectangle2D rect) {
        if (rect != null) {
            setRect(rect);
        }
    }

    /**
     * Creates a rectangle using maximal <var>x</var> and <var>y</var> values
     * rather than width and height. This factory avoid the problem of NaN
     * values when extremum are infinite numbers.
     *
     * @param xmin Minimal <var>x</var> ordinate value.
     * @param ymin Minimal <var>y</var> ordinate value.
     * @param xmax Maximal <var>x</var> ordinate value.
     * @param ymax Maximal <var>y</var> ordinate value.
     * @return A new rectangle initialized to the given bounds.
     */
    public static XRectangle2D createFromExtremums(final double xmin, final double ymin,
                                                   final double xmax, final double ymax)
    {
        final XRectangle2D rect = new XRectangle2D();
        rect.xmin = xmin;
        rect.ymin = ymin;
        rect.xmax = xmax;
        rect.ymax = ymax;
        return rect;
    }

    /**
     * Tests if the interior and/or the edge of two rectangles intersect. This method is similar to
     * {@link Rectangle2D#intersects(Rectangle2D)} except for the following points:
     *
     * <ul>
     *   <li>This method works correctly with {@linkplain Double#POSITIVE_INFINITY infinites} and
     *       {@linkplain Double#NaN NaN} values.</li>
     *   <li>This method does not test only if the <em>interiors</em> intersect. It tests for the edges as well.
     *       In other words, this method tests bounds as <em>closed</em> intervals rather then open intervals.</li>
     * </ul>
     *
     * <div class="note"><b>Rational:</b>
     * usage of closed interval is required if at least one rectangle may be the bounding box of a perfectly
     * horizontal or vertical line; such a bounding box has 0 width or height.</div>
     *
     * This method is said <cite>inclusive</cite> because it tests bounds as closed interval
     * rather then open interval (the default Java2D behavior). Usage of closed interval is
     * required if at least one rectangle may be the bounding box of a perfectly horizontal
     * or vertical line; such a bounding box has 0 width or height.
     *
     * @param  rect1  the first rectangle to test.
     * @param  rect2  the second rectangle to test.
     * @return {@code true} if the interior and/or the edge of the two specified rectangles intersects.
     */
    public static boolean intersectInclusive(final Rectangle2D rect1, final Rectangle2D rect2) {
        final double xmin1 = rect1.getMinX();
        final double xmax1 = rect1.getMaxX(); if (!(xmax1 >= xmin1)) return false;
        final double ymin1 = rect1.getMinY();
        final double ymax1 = rect1.getMaxY(); if (!(ymax1 >= ymin1)) return false;
        final double xmin2 = rect2.getMinX();
        final double xmax2 = rect2.getMaxX(); if (!(xmax2 >= xmin2)) return false;
        final double ymin2 = rect2.getMinY();
        final double ymax2 = rect2.getMaxY(); if (!(ymax2 >= ymin2)) return false;
        return (xmax2 >= xmin1 &&
                ymax2 >= ymin1 &&
                xmin2 <= xmax1 &&
                ymin2 <= ymax1);
    }

    /**
     * Tests if the interior of the {@code Shape} intersects the interior of a specified
     * rectangle. This method might conservatively return {@code true} when there is a high
     * probability that the rectangle and the shape intersect, but the calculations to accurately
     * determine this intersection are prohibitively expensive.
     * <p>
     * This method is similar to {@link Shape#intersects(Rectangle2D)}, except that
     * it tests also rectangle with zero {@linkplain Rectangle2D#getWidth width} or
     * {@linkplain Rectangle2D#getHeight height} (which are {@linkplain Rectangle2D#isEmpty empty}
     * according {@link Shape} contract). However, rectangle with negative width or height are still
     * considered as empty.
     * <p>
     * This method is said <cite>inclusive</cite> because it try to mimic
     * {@link #intersectInclusive(Rectangle2D, Rectangle2D)} behavior, at
     * least for rectangle with zero width or height.
     *
     * @param  shape The shape.
     * @param  rect  The rectangle to test for inclusion.
     * @return {@code true} if the interior of the shape and  the interior of the specified
     *         rectangle intersect, or are both highly likely to intersect.
     */
    public static boolean intersectInclusive(final Shape shape, final Rectangle2D rect) {
        double x      = rect.getX();
        double y      = rect.getY();
        double width  = rect.getWidth();
        double height = rect.getHeight();
        if(width == 0 && height == 0) {
            width = EPS;
            height = EPS;
        } else if (width == 0) {
            width = height*EPS;
            x -= 0.5*width;
        } else if (height == 0) {
            height = width*EPS;
            y -= 0.5*height;
        }
        return shape.intersects(x, y, width, height);
    }

    /**
     * Tests if the interior of the {@code inner} rectangle is contained in the interior and/or the edge
     * of the {@code outter} rectangle. This method is similar to {@link Rectangle2D#contains(Rectangle2D)}
     * except for the following points:
     *
     * <ul>
     *   <li>This method works correctly with {@linkplain Double#POSITIVE_INFINITY infinites} and
     *       {@linkplain Double#NaN NaN} values.</li>
     *   <li>This method does not test only the <em>interiors</em> of {@code outter}. It tests for the edges as well.
     *       In other words, this method tests bounds as <em>closed</em> intervals rather then open intervals.</li>
     * </ul>
     *
     * <div class="note"><b>Rational:</b>
     * usage of closed interval is required if at least one rectangle may be the bounding box of a perfectly
     * horizontal or vertical line; such a bounding box has 0 width or height.</div>
     *
     * This method is said <cite>inclusive</cite> because it tests bounds as closed interval
     * rather then open interval (the default Java2D behavior). Usage of closed interval is
     * required if at least one rectangle may be the bounding box of a perfectly horizontal
     * or vertical line; such a bounding box has 0 width or height.
     *
     * @param  outter  the first rectangle to test.
     * @param  inner   the second rectangle to test.
     * @return {@code true} if the interior of {@code inner} is inside the interior and/or the edge of {@code outter}.
     *
     * @todo Check for negative width or height (should returns {@code false}).
     */
    public static boolean containsInclusive(final Rectangle2D outter, final Rectangle2D inner) {
        return outter.getMinX() <= inner.getMinX() && outter.getMaxX() >= inner.getMaxX() &&
               outter.getMinY() <= inner.getMinY() && outter.getMaxY() >= inner.getMaxY();
    }
}
