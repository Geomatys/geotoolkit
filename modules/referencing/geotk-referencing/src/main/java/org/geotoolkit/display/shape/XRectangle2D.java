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
import java.io.Serializable;

import org.apache.sis.util.Classes;


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
 */
public class XRectangle2D extends Rectangle2D implements Serializable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -1918221103635749436L;

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

    /** Minimal <var>x</var> ordinate value. */ protected double xmin;
    /** Minimal <var>y</var> ordinate value. */ protected double ymin;
    /** Maximal <var>x</var> ordinate value. */ protected double xmax;
    /** Maximal <var>y</var> ordinate value. */ protected double ymax;

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
     * Determines whether this rectangle is empty. If this rectangle has at least one
     * {@linkplain java.lang.Double#NaN NaN} value, then it is considered empty.
     *
     * @return {@code true} if this rectangle is empty; {@code false} otherwise.
     */
    @Override
    public boolean isEmpty() {
        return !(xmin < xmax && ymin < ymax);
    }

    /**
     * Returns the minimal <var>x</var> ordinate value.
     *
     * @return The minimal <var>x</var> ordinate value.
     */
    @Override
    public double getX() {
        return xmin;
    }

    /**
     * Returns the minimal <var>y</var> ordinate value.
     *
     * @return The minimal <var>y</var> ordinate value.
     */
    @Override
    public double getY() {
        return ymin;
    }

    /**
     * Returns the width of the rectangle.
     *
     * @return The width of the rectangle.
     */
    @Override
    public double getWidth() {
        return xmax - xmin;
    }

    /**
     * Returns the height of the rectangle.
     *
     * @return The height of the rectangle.
     */
    @Override
    public double getHeight() {
        return ymax - ymin;
    }

    /**
     * Returns the minimal <var>x</var> ordinate value.
     */
    @Override
    public double getMinX() {
        return xmin;
    }

    /**
     * Returns the minimal <var>y</var> ordinate value.
     */
    @Override
    public double getMinY() {
        return ymin;
    }

    /**
     * Returns the maximal <var>x</var> ordinate value.
     */
    @Override
    public double getMaxX() {
        return xmax;
    }

    /**
     * Returns the maximal <var>y</var> ordinate value.
     */
    @Override
    public double getMaxY() {
        return ymax;
    }

    /**
     * Returns the <var>x</var> ordinate of the center of the rectangle.
     */
    @Override
    public double getCenterX() {
        return (xmin + xmax) * 0.5;
    }

    /**
     * Returns the <var>y</var> ordinate of the center of the rectangle.
     */
    @Override
    public double getCenterY() {
        return (ymin + ymax) * 0.5;
    }

    /**
     * Sets the location and size of this rectangle to the specified values.
     *
     * @param x The <var>x</var> minimal ordinate value.
     * @param y The <var>y</var> minimal ordinate value.
     * @param width The rectangle width.
     * @param height The rectangle height.
     */
    @Override
    public void setRect(final double x, final double y, final double width, final double height) {
        this.xmin = x;
        this.ymin = y;
        this.xmax = x + width;
        this.ymax = y + height;
    }

    /**
     * Sets this rectangle to be the same as the specified rectangle.
     *
     * @param r The rectangle to copy values from.
     */
    @Override
    public void setRect(final Rectangle2D r) {
        this.xmin = r.getMinX();
        this.ymin = r.getMinY();
        this.xmax = r.getMaxX();
        this.ymax = r.getMaxY();
    }

    /**
     * Sets the framing rectangle to the given rectangle. The default implementation delegates
     * to {@link #setRect(Rectangle2D)}. This is consistent with the default implementation of
     * {@link #setFrame(double, double, double, double)}, which delegates to the corresponding
     * method of {@link #setRect(double, double, double, double) setRect}.
     */
    @Override
    public void setFrame(final Rectangle2D r) {
        setRect(r);
    }

    /**
     * Tests if the interior of this rectangle intersects the interior of a
     * specified set of rectangular coordinates.
     *
     * @param  x The <var>x</var> minimal ordinate value.
     * @param  y The <var>y</var> minimal ordinate value.
     * @param  width The rectangle width.
     * @param  height The rectangle height.
     * @return {@code true} if this rectangle intersects the interior of the
     *         specified set of rectangular coordinates; {@code false} otherwise.
     */
    @Override
    public boolean intersects(final double x,     final double y,
                              final double width, final double height)
    {
        if (!(xmin < xmax && ymin < ymax && width > 0 && height > 0)) {
            return false;
        } else {
            return (x < xmax && y < ymax && x+width > xmin && y+height > ymin);
        }
    }

    /**
     * Tests if the interior of this shape intersects the interior of a specified rectangle.
     *
     * @param  rect The specified rectangle.
     * @return {@code true} if this shape and the specified rectangle intersect each other.
     *
     * @see #intersectInclusive(Rectangle2D, Rectangle2D)
     */
    @Override
    public boolean intersects(final Rectangle2D rect) {
        if (!(xmin < xmax && ymin < ymax)) {
            return false;
        } else {
            final double xmin2 = rect.getMinX();
            final double xmax2 = rect.getMaxX(); if (!(xmax2 > xmin2)) return false;
            final double ymin2 = rect.getMinY();
            final double ymax2 = rect.getMaxY(); if (!(ymax2 > ymin2)) return false;
            return (xmin2 < xmax && ymin2 < ymax && xmax2 > xmin && ymax2 > ymin);
        }
    }

    /**
     * Tests if the interior and/or the edge of two rectangles intersect. This method
     * is similar to {@link #intersects(Rectangle2D)} except for the following points:
     * <p>
     * <ul>
     *   <li>This method doesn't test only if the <em>interiors</em> intersect.
     *       It tests for the edges as well.</li>
     *   <li>This method tests also rectangle with zero {@linkplain Rectangle2D#getWidth width} or
     *       {@linkplain Rectangle2D#getHeight height} (which are {@linkplain Rectangle2D#isEmpty
     *       empty} according {@link Shape} contract). However, rectangle with negative width or
     *       height are still considered as empty.</li>
     *   <li>This method work correctly with {@linkplain java.lang.Double#POSITIVE_INFINITY
     *       infinites} and {@linkplain java.lang.Double#NaN NaN} values.</li>
     * </ul>
     * <p>
     * This method is said <cite>inclusive</cite> because it tests bounds as closed interval
     * rather then open interval (the default Java2D behavior). Usage of closed interval is
     * required if at least one rectangle may be the bounding box of a perfectly horizontal
     * or vertical line; such a bounding box has 0 width or height.
     *
     * @param  rect1 The first rectangle to test.
     * @param  rect2 The second rectangle to test.
     * @return {@code true} if the interior and/or the edge of the two specified rectangles
     *         intersects.
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
     * Tests if the interior of this rectangle entirely
     * contains the specified set of rectangular coordinates.
     *
     * @param  x The <var>x</var> minimal ordinate value.
     * @param  y The <var>y</var> minimal ordinate value.
     * @param  width The rectangle width.
     * @param  height The rectangle height.
     * @return {@code true} if this rectangle entirely contains specified set of rectangular
     *         coordinates; {@code false} otherwise.
     */
    @Override
    public boolean contains(final double x,     final double y,
                            final double width, final double height)
    {
        if (!(xmin < xmax && ymin < ymax && width > 0 && height > 0)) {
            return false;
        } else {
            return (x >= xmin && y >= ymin && (x+width) <= xmax && (y+height) <= ymax);
        }
    }

    /**
     * Tests if the interior of this shape entirely contains the specified rectangle.
     * This methods overrides the default {@link Rectangle2D} implementation in order
     * to work correctly with {@linkplain java.lang.Double#POSITIVE_INFINITY infinites}
     * and {@linkplain java.lang.Double#NaN NaN} values.
     *
     * @param  rect The specified rectangle.
     * @return {@code true} if this shape entirely contains the specified rectangle.
     */
    @Override
    public boolean contains(final Rectangle2D rect) {
        if (!(xmin < xmax && ymin < ymax)) {
            return false;
        } else {
            final double xmin2 = rect.getMinX();
            final double xmax2 = rect.getMaxX(); if (!(xmax2 > xmin2)) return false;
            final double ymin2 = rect.getMinY();
            final double ymax2 = rect.getMaxY(); if (!(ymax2 > ymin2)) return false;
            return (xmin2 >= xmin && ymin2 >= ymin && xmax2 <= xmax && ymax2 <= ymax);
        }
    }

    /**
     * Tests if a specified coordinate is inside the boundary of this {@code Rectangle2D}.
     *
     * @param x the <var>x</var> coordinates to test.
     * @param y the <var>y</var> coordinates to test.
     * @return {@code true} if the specified coordinates are inside the boundary of this
     *         rectangle, {@code false} otherwise.
     */
    @Override
    public boolean contains(final double x, final double y) {
        return (x >= xmin && y >= ymin && x < xmax && y < ymax);
    }

    /**
     * Tests if the interior of the {@code inner} rectangle is contained in the interior
     * and/or the edge of the {@code outter} rectangle. This method is similar to
     * {@link #contains(Rectangle2D)} except for the following points:
     * <p>
     * <ul>
     *   <li>This method doesn't test only the <em>interiors</em> of {@code outter}.
     *       It tests for the edges as well.</li>
     *   <li>This method tests also rectangle with zero {@linkplain Rectangle2D#getWidth width} or
     *       {@linkplain Rectangle2D#getHeight height} (which are {@linkplain Rectangle2D#isEmpty
     *       empty} according {@link Shape} contract).</li>
     *   <li>This method work correctly with {@linkplain java.lang.Double#POSITIVE_INFINITY
     *       infinites} and {@linkplain java.lang.Double#NaN NaN} values.</li>
     * </ul>
     * <p>
     * This method is said <cite>inclusive</cite> because it tests bounds as closed interval
     * rather then open interval (the default Java2D behavior). Usage of closed interval is
     * required if at least one rectangle may be the bounding box of a perfectly horizontal
     * or vertical line; such a bounding box has 0 width or height.
     *
     * @param  outter The first rectangle to test.
     * @param  inner The second rectangle to test.
     * @return {@code true} if the interior of {@code inner} is inside the interior
     *         and/or the edge of {@code outter}.
     *
     * @todo Check for negative width or height (should returns {@code false}).
     */
    public static boolean containsInclusive(final Rectangle2D outter, final Rectangle2D inner) {
        return outter.getMinX() <= inner.getMinX() && outter.getMaxX() >= inner.getMaxX() &&
               outter.getMinY() <= inner.getMinY() && outter.getMaxY() >= inner.getMaxY();
    }

    /**
     * Determines where the specified coordinates lie with respect to this rectangle.
     * This method computes a binary OR of the appropriate mask values indicating,
     * for each side of this {@code Rectangle2D}, whether or not the specified coordinates
     * are on the same side of the edge as the rest of this {@code Rectangle2D}.
     *
     * @return The logical OR of all appropriate out codes.
     *
     * @see #OUT_LEFT
     * @see #OUT_TOP
     * @see #OUT_RIGHT
     * @see #OUT_BOTTOM
     */
    @Override
    public int outcode(final double x, final double y) {
        int out = 0;
        if (!(xmax > xmin)) out |= OUT_LEFT | OUT_RIGHT;
        else if (x < xmin)  out |= OUT_LEFT;
        else if (x > xmax)  out |= OUT_RIGHT;

        if (!(ymax > ymin)) out |= OUT_TOP | OUT_BOTTOM;
        else if (y < ymin)  out |= OUT_TOP;
        else if (y > ymax)  out |= OUT_BOTTOM;
        return out;
    }

    /**
     * Returns a new {@code Rectangle2D} object representing the
     * intersection of this rectangle with the specified one.
     *
     * @param  rect The {@code Rectangle2D} to be intersected with this rectangle.
     * @return The largest {@code Rectangle2D} contained in both the specified
     *         rectangle and this one.
     *
     * @see #intersect(Rectangle2D)
     */
    @Override
    public Rectangle2D createIntersection(final Rectangle2D rect) {
        final XRectangle2D r = new XRectangle2D();
        r.xmin = Math.max(xmin, rect.getMinX());
        r.ymin = Math.max(ymin, rect.getMinY());
        r.xmax = Math.min(xmax, rect.getMaxX());
        r.ymax = Math.min(ymax, rect.getMaxY());
        return r;
    }

    /**
     * Returns a new {@code Rectangle2D} object representing the
     * union of this rectangle with the specified one.
     *
     * @param  rect The {@code Rectangle2D} to be combined with this rectangle.
     * @return The smallest {@code Rectangle2D} containing both the specified
     *         {@code Rectangle2D} and this one.
     */
    @Override
    public Rectangle2D createUnion(final Rectangle2D rect) {
        final XRectangle2D r = new XRectangle2D();
        r.xmin = Math.min(xmin, rect.getMinX());
        r.ymin = Math.min(ymin, rect.getMinY());
        r.xmax = Math.max(xmax, rect.getMaxX());
        r.ymax = Math.max(ymax, rect.getMaxY());
        return r;
    }

    /**
     * Adds a point, specified by the arguments {@code x} and {@code y}, to this rectangle.
     * The resulting {@code Rectangle2D} is the smallest rectangle that contains both the
     * original rectangle and the specified point.
     * <p>
     * After adding a point, a call to {@code contains} with the added point as an argument
     * does not necessarily return {@code true}. The {@code contains} method does not return
     * {@code true} for points on the right or bottom edges of a rectangle. Therefore, if the
     * added point falls on the left or bottom edge of the enlarged rectangle, {@code contains}
     * returns {@code false} for that point.
     *
     * @param x X ordinate value of the point to add.
     * @param y Y ordinate value of the point to add.
     */
    @Override
    public void add(final double x, final double y) {
        if (x < xmin) xmin = x;
        if (x > xmax) xmax = x;
        if (y < ymin) ymin = y;
        if (y > ymax) ymax = y;
    }

    /**
     * Adds a {@code Rectangle2D} object to this rectangle.
     * The resulting rectangle is the union of the two {@code Rectangle2D} objects.
     *
     * @param rect The {@code Rectangle2D} to add to this rectangle.
     */
    @Override
    public void add(final Rectangle2D rect) {
        double t;
        if ((t = rect.getMinX()) < xmin) xmin = t;
        if ((t = rect.getMaxX()) > xmax) xmax = t;
        if ((t = rect.getMinY()) < ymin) ymin = t;
        if ((t = rect.getMaxY()) > ymax) ymax = t;
    }

    /**
     * Intersects a {@link Rectangle2D} object with this rectangle. The resulting
     * rectangle is the intersection of the two {@code Rectangle2D} objects.
     * <p>
     * Invoking this method is equivalent to invoking the following code, except
     * that this method behaves correctly with infinite values.
     *
     * {@preformat java
     *     Rectangle2D.intersect(this, rect, this);
     * }
     *
     * @param rect The {@code Rectangle2D} to intersect with this rectangle.
     *
     * @see #intersect(Rectangle2D, Rectangle2D, Rectangle2D)
     * @see #createIntersection(Rectangle2D)
     *
     * @since 3.10
     */
    public void intersect(final Rectangle2D rect) {
        double t;
        if ((t = rect.getMinX()) > xmin) xmin = t;
        if ((t = rect.getMaxX()) < xmax) xmax = t;
        if ((t = rect.getMinY()) > ymin) ymin = t;
        if ((t = rect.getMaxY()) < ymax) ymax = t;
    }

    /**
     * Returns the {@code String} representation of this {@code Rectangle2D}.
     * The ordinate order is (<var>x</var><sub>min</sub>, <var>y</var><sub>min</sub>,
     * <var>x</var><sub>max</sub>, <var>y</var><sub>max</sub>), which is consistent
     * with the {@link #createFromExtremums(double, double, double, double)} constructor
     * and with the {@code BBOX} <cite>Well Known Text</cite> (WKT) syntax.
     *
     * @return a {@code String} representing this {@code Rectangle2D}.
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) +
                "[xmin=" + xmin +
                " ymin=" + ymin +
                " xmax=" + xmax +
                " ymax=" + ymax + ']';
    }
}
