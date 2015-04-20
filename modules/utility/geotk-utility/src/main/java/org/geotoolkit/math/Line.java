/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1998-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.awt.geom.Point2D;

import static java.lang.Double.*;


/**
 * Equation of a line in a two dimensional space (<var>x</var>,<var>y</var>).
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @see Point2D
 * @see Line2D
 * @see Plane
 *
 * @since 1.0
 * @module
 */
public class Line extends org.apache.sis.math.Line {
    /**
     * Small value for rounding errors.
     */
    private static final double EPS = 1E-12;

    /**
     * Construct an initially uninitialized line. All methods will returns {@link Double#NaN}.
     */
    public Line() {
    }

    /**
     * Construct a line with the specified slope and offset.
     * The linear equation will be <var>y</var>=<var>slope</var>*<var>x</var>+<var>y0</var>.
     *
     * @param slope The slope.
     * @param y0 The <var>y</var> value at <var>x</var>==0.
     *
     * @see #setLine(double, double)
     */
    public Line(final double slope, final double y0) {
        super(slope, y0);
    }

    /**
     * Returns the intersection point between this line and the specified one.
     * If both lines are parallel, then this method returns {@code null}.
     *
     * @param  line The line to intersect.
     * @return The intersection point, or {@code null}.
     */
    public Point2D intersectionPoint(final Line line) {
        final double x, y;
        if (isInfinite(slope())) {
            if (isInfinite(line.slope())) {
                return null;
            }
            x = x0();
            y = line.y(x);
        } else {
            if (isInfinite(line.slope())) {
                x = line.x0();
            } else {
                x = (y0() - line.y0()) / (line.slope() - slope());
                if (isInfinite(x)) {
                    return null;
                }
            }
            y = y(x);
        }
        return new Point2D.Double(x,y);
    }

    /**
     * Returns the intersection point between this line and the specified bounded line.
     * If both lines are parallel or if the specified {@code line} doesn't reach
     * this line (since {@link Line2D} do not extends toward infinities), then this
     * method returns {@code null}.
     *
     * @param  line The bounded line to intersect.
     * @return The intersection point, or {@code null}.
     */
    public Point2D intersectionPoint(final Line2D line) {
        final double x1 = line.getX1();
        final double y1 = line.getY1();
        final double x2 = line.getX2();
        final double y2 = line.getY2();
        double x,y;
        double m = (y2-y1) / (x2-x1);
        if (isInfinite(slope())) {
            x = x0();
            y = x*m + (y2 - m*x2);
        } else {
            if (!isInfinite(m)) {
                x = (y0() - (y2 - m*x2)) / (m - slope());
            } else {
                x = 0.5 * (x1 + x2);
            }
            y = x*slope() + y0();
        }
        double eps;
        /*
         * Ensures that the intersection is in the range of valid x values.
         */
        eps = EPS * Math.abs(x);
        if (x1 <= x2) {
            if (!(x >= x1-eps && x <= x2+eps)) {
                return null;
            }
        } else {
            if (!(x <= x1+eps && x >= x2-eps)) {
                return null;
            }
        }
        /*
         * Ensures that the intersection is in the range of valid y values.
         */
        eps = EPS * Math.abs(y);
        if (y1 <= y2) {
            if (!(y >= y1-eps && y <= y2+eps)) {
                return null;
            }
        } else {
            if (!(y <= y1-eps && y >= y2+eps)) {
                return null;
            }
        }
        return new Point2D.Double(x,y);
    }

    /**
     * Returns the nearest point on this line from the specified point.
     *
     * @param  point An arbitrary point.
     * @return The point on this line which is the nearest of the specified {@code point}.
     */
    public Point2D nearestColinearPoint(final Point2D point) {
        if (!isInfinite(slope())) {
            final double x = ((point.getY() - y0()) * slope() + point.getX()) / (slope() * slope() + 1);
            return new Point2D.Double(x, x*slope() + y0());
        } else {
            return new Point2D.Double(x0(), point.getY());
        }
    }

    /**
     * Computes the base of a isosceles triangle having the specified summit and side length.
     * The base will be colinear with this line. In other words, this method compute two
     * points (<var>x1</var>,<var>y1</var>) and (<var>x2</var>,<var>y2</var>) located in
     * such a way that:
     * <ul>
     *   <li>Both points are on this line.</li>
     *   <li>The distance between any of the two points and the specified {@code summit}
     *       is exactly {@code sideLength}.</li>
     * </ul>
     *
     * @param  summit The summit of the isosceles triangle.
     * @param  sideLength The length for the two sides of the isosceles triangle.
     * @return The base of the isoscele triangle, colinear with this line, or {@code null}
     *         if the base can't be computed. If non-null, then the triangle is the figure formed
     *         by joining (<var>x1</var>,<var>y1</var>), (<var>x2</var>,<var>y2</var>) and
     *         {@code summit}.
     */
    public Line2D isoscelesTriangleBase(final Point2D summit, double sideLength) {
        sideLength *= sideLength;
        if (slope() == 0) {
            final double  x =        summit.getX();
            final double dy = y0() - summit.getY();
            final double dx = Math.sqrt(sideLength - dy*dy);
            if (isNaN(dx)) {
                return null;
            }
            return new Line2D.Double(x+dx, y0(), x-dx, y0());
        }
        if (isInfinite(slope())) {
            final double  y =        summit.getY();
            final double dx = x0() - summit.getX();
            final double dy = Math.sqrt(sideLength - dx*dx);
            if (isNaN(dy)) {
                return null;
            }
            return new Line2D.Double(x0(), y+dy, x0(), y-dy);
        }
        final double x  = summit.getX();
        final double y  = summit.getY();
        final double dy = y0() - y + slope() * x;
        final double B  = -slope() * dy;
        final double A  = slope() * slope() + 1;
        final double C  = Math.sqrt(B*B + A*(sideLength - dy*dy));
        if (isNaN(C)) {
            return null;
        }
        final double x1 = (B+C)/A + x;
        final double x2 = (B-C)/A + x;
        return new Line2D.Double(x1, slope() * x1 + y0(), x2, slope() * x2 + y0());
    }
}
