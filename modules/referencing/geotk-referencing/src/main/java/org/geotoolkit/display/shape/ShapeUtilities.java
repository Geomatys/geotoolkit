/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.QuadCurve2D;
import org.geotoolkit.lang.Static;

import static java.lang.Math.*;


/**
 * Static methods operating on shapes from the {@link java.awt.geom} package.
 *
 * @author Martin Desruisseaux (MPO, IRD, Geomatys)
 * @version 3.20
 *
 * @since 1.1
 * @module
 */
public final class ShapeUtilities extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private ShapeUtilities() {
    }

    /**
     * Returns the intersection point between two line segments. The lines do not continue
     * to infinity; if the intersection do not occurs between the ending points {@linkplain
     * Line2D#getP1 P1} and {@linkplain Line2D#getP2 P2} of the two line segments, then this
     * method returns {@code null}.
     *
     * @param  a The first line segment.
     * @param  b The second line segment.
     * @return The intersection point, or {@code null} if none.
     */
    public static Point2D.Double intersectionPoint(final Line2D a, final Line2D b) {
        return org.apache.sis.internal.referencing.j2d.ShapeUtilities.intersectionPoint(
                a.getX1(), a.getY1(), a.getX2(), a.getY2(),
                b.getX1(), b.getY1(), b.getX2(), b.getY2());
    }

    /**
     * Returns the point on the given {@code line} segment which is closest to the given
     * {@code point}. Let {@code result} be the returned point. This method guarantees
     * (except for rounding errors) that:
     * <p>
     * <ul>
     *   <li>{@code result} is a point on the {@code line} segment. It is located between
     *       the {@linkplain Line2D#getP1 P1} and {@linkplain Line2D#getP2 P2} ending points
     *       of that line segment.</li>
     *   <li>The distance between the {@code result} point and the given {@code point} is
     *       the shortest distance among the set of points meeting the previous condition.
     *       This distance can be obtained with {@code point.distance(result)}.</li>
     * </ul>
     *
     * @param segment The line on which to search for a point.
     * @param point A point close to the given line.
     * @return The nearest point on the given line.
     *
     * @see #colinearPoint(Line2D, Point2D, double)
     */
    public static Point2D.Double nearestColinearPoint(final Line2D segment, final Point2D point) {
        return org.apache.sis.internal.referencing.j2d.ShapeUtilities.nearestColinearPoint(
                segment.getX1(), segment.getY1(),
                segment.getX2(), segment.getY2(),
                  point.getX(),    point.getY());
    }

    /**
     * Returns a point on the given {@code line} segment located at the given {@code distance}
     * from that line. Let {@code result} be the returned point. If {@code result} is not null,
     * then this method guarantees (except for rounding error) that:
     * <p>
     * <ul>
     *   <li>{@code result} is a point on the {@code line} segment. It is located between
     *       the {@linkplain Line2D#getP1 P1} and {@linkplain Line2D#getP2 P2} ending points
     *       of that line segment.</li>
     *   <li>The distance between the {@code result} and the given {@code point} is exactly
     *       equal to {@code distance}.</li>
     * </ul>
     * <p>
     * If no result point meets those conditions, then this method returns {@code null}.
     * If two result points meet those conditions, then this method returns the point
     * which is the closest to {@code line.getP1()}.
     *
     * @param line The line on which to search for a point.
     * @param point A point close to the given line.
     * @param distance The distance between the given point and the point to be returned.
     * @return A point on the given line located at the given distance from the given point.
     *
     * @see #nearestColinearPoint(Line2D, Point2D)
     */
    public static Point2D.Double colinearPoint(Line2D line, Point2D point, double distance) {
        return org.apache.sis.internal.referencing.j2d.ShapeUtilities.colinearPoint(
                line.getX1(), line.getY1(), line.getX2(), line.getY2(),
                point.getX(), point.getY(), distance);
    }

    /**
     * Returns a quadratic curve passing by the 3 given points. There is an infinity of quadratic
     * curves passing by 3 points. We can express the curve we are looking for as a parabolic
     * equation of the form {@code y=ax²+bx+c} but where the <var>x</var> axis is not necessarily
     * horizontal. The orientation of the <var>x</var> axis in the above equation is determined
     * by the {@code horizontal} parameter:
     * <p>
     * <ul>
     *   <li>A value of {@code true} means that the <var>x</var> axis must be horizontal.
     *       The quadratic curve will then look like an ordinary parabolic curve as we see
     *       in mathematic school book.</li>
     *   <li>A value of {@code false} means that the <var>x</var> axis must be parallel to the
     *       line segment joining the {@code P0} and {@code P2} ending points.</li>
     * </ul>
     * <p>
     * Note that if {@code P0.y == P2.y}, then both {@code horizontal} values produce the same
     * result.
     *
     * @param  P0 The starting point of the quadratic curve.
     * @param  P1 A point by which the quadratic curve must pass by.
     * @param  P2 The ending point of the quadratic curve.
     * @param  horizontal If {@code true}, the <var>x</var> axis is considered horizontal while
     *         computing the {@code y=ax²+bx+c} equation terms. If {@code false}, it is considered
     *         parallel to the line joining the {@code P0} and {@code P2} points.
     * @return A quadratic curve passing by the given points. The curve starts at {@code P0} and
     *         ends at {@code P2}. If two points are too close or if the three points are colinear,
     *         then this method returns {@code null}.
     */
    public static QuadCurve2D.Double fitParabol(
            final Point2D P0, final Point2D P1, final Point2D P2, final boolean horizontal)
    {
        return org.apache.sis.internal.referencing.j2d.ShapeUtilities.fitParabol(
                P0.getX(), P0.getY(),
                P1.getX(), P1.getY(),
                P2.getX(), P2.getY(), horizontal);
    }

    /**
     * Returns the control point of a quadratic curve passing by the 3 given points. There is an
     * infinity of quadratic curves passing by 3 points. We can express the curve we are looking
     * for as a parabolic equation of the form {@code y=ax²+bx+c} but where the <var>x</var> axis
     * is not necessarily horizontal. The orientation of the <var>x</var> axis in the above equation
     * is determined by the {@code horizontal} parameter:
     * <p>
     * <ul>
     *   <li>A value of {@code true} means that the <var>x</var> axis must be horizontal.
     *       The quadratic curve will then look like an ordinary parabolic curve as we see
     *       in mathematic school book.</li>
     *   <li>A value of {@code false} means that the <var>x</var> axis must be parallel to the
     *       line segment joining the {@code P0} and {@code P2} ending points.</li>
     * </ul>
     * <p>
     * Note that if {@code P0.y == P2.y}, then both {@code horizontal} values produce the same result.
     *
     * @param  P0 The starting point of the quadratic curve.
     * @param  P1 A point by which the quadratic curve must pass by.
     * @param  P2 The ending point of the quadratic curve.
     * @param  horizontal If {@code true}, the <var>x</var> axis is considered horizontal while
     *         computing the {@code y=ax²+bx+c} equation terms. If {@code false}, it is considered
     *         parallel to the line joining the {@code P0} and {@code P2} points.
     * @return The control point of a quadratic curve passing by the given points. The curve
     *         starts at {@code P0} and ends at {@code P2}. If two points are too
     *         close or if the three points are colinear, then this method returns {@code null}.
     *
     * @since 3.20
     */
    public static Point2D.Double parabolicControlPoint(
            final Point2D P0, final Point2D P1, final Point2D P2, final boolean horizontal)
    {
        return org.apache.sis.internal.referencing.j2d.ShapeUtilities.parabolicControlPoint(
                P0.getX(), P0.getY(),
                P1.getX(), P1.getY(),
                P2.getX(), P2.getY(), horizontal);
    }

    /**
     * Returns a circle passing by the 3 given points. The distance between the returned
     * point and any of the given points will be constant; it is the circle radius.
     *
     * @param  P1 The first point.
     * @param  P2 The second point.
     * @param  P3 The third point.
     * @return A circle passing by the given points.
     */
    public static Ellipse2D.Double fitCircle(final Point2D P1, final Point2D P2, final Point2D P3) {
        final Point2D center = org.apache.sis.internal.referencing.j2d.ShapeUtilities.circleCentre(
                P1.getX(), P1.getY(),
                P2.getX(), P2.getY(),
                P3.getX(), P3.getY());
        final double radius = center.distance(P2);
        return new Ellipse2D.Double(center.getX() - radius,
                                    center.getY() - radius,
                                    2*radius, 2*radius);
    }

    /**
     * Finds the extremum of the unique cubic curve which fit the two given points and derivatives.
     * First, this method finds the A, B, C and D coefficients for the following equation:
     *
     * <p><center><var>y</var> = A + B<var>x</var> + C<var>x</var><sup>2</sup> + D<var>x</var><sup>3</sup></center></p>
     *
     * Next, this method finds the extremum by finding the (<var>x</var>,<var>y</var>) values
     * that satisfy the following equation (which is the derivative of the above equation):
     *
     * <p><center>B + 2C<var>x</var> + 3D<var>x</var><sup>2</sup> = 0</center></p>
     *
     * A cubic curve can have two extremum, which are returned in a {@link Line2D} construct in
     * no particular order. The length of the returned line is the distance separating the two
     * extremum (often a useful information for determining if a quadratic equation would be a
     * sufficient approximation).
     * <p>
     * The line returned by this method may contains {@linkplain Double#NaN NaN} values if the
     * given geometry is actually a line segment ({@code dy1} = {@code dy2} = slope from P1 to
     * P2).
     *
     * @param  P1   The first point.
     * @param  dy1  The &part;<var>x</var>/&part;<var>y</var> value at the first point.
     * @param  P2   The second point.
     * @param  dy2  The &part;<var>x</var>/&part;<var>y</var> value at the second point.
     * @return The two points located on the extremum of the fitted cubic curve.
     *
     * @since 3.20
     */
    public static Line2D.Double cubicCurveExtremum(final Point2D P1, final double dy1,
                                                   final Point2D P2, final double dy2)
    {
        return cubicCurveExtremum(
                P1.getX(), P1.getY(), dy1,
                P2.getX(), P2.getY(), dy2);
    }

    /**
     * Finds the extremum of the unique cubic curve which fit the two given points and derivatives.
     * First, this method finds the A, B, C and D coefficients for the following equation:
     *
     * <blockquote><var>y</var> = A + B<var>x</var> + C<var>x</var><sup>2</sup> + D<var>x</var><sup>3</sup></blockquote>
     *
     * Next, this method finds the extremum by finding the (<var>x</var>,<var>y</var>) values
     * that satisfy the following equation (which is the derivative of the above equation):
     *
     * <blockquote>B + 2C<var>x</var> + 3D<var>x</var><sup>2</sup> = 0</blockquote>
     *
     * A cubic curve can have two extremum, which are returned in a {@link Line2D} construct in
     * no particular order. The length of the returned line is the distance separating the two
     * extremum (often a useful information for determining if a quadratic equation would be a
     * sufficient approximation).
     *
     * <p>The line returned by this method may contains {@linkplain Double#NaN NaN} values if the
     * given geometry is actually a line segment ({@code dy1} = {@code dy2} = slope from P1 to P2).</p>
     *
     * @param  x1   The <var>x</var> ordinate of the first point.
     * @param  y1   The <var>y</var> ordinate of the first point.
     * @param  dy1  The &part;<var>x</var>/&part;<var>y</var> value at the first point.
     * @param  x2   The <var>x</var> ordinate of the second point.
     * @param  y2   The <var>y</var> ordinate of the second point.
     * @param  dy2  The &part;<var>x</var>/&part;<var>y</var> value at the second point.
     * @return The two points located on the extremum of the fitted cubic curve.
     *
     * @since 3.20
     *
     * @deprecated Use {@link #cubicCurveExtremum(Point2D, double, Point2D, double)} instead.
     */
    // Note: in Apache SIS, this is CurveExtremum.resolve(...).
    @Deprecated
    public static Line2D.Double cubicCurveExtremum(double x1, double y1, final double dy1,
                                                   double x2, double y2, final double dy2)
    {
        /*
         * Equation for a cubic curve is y = A + Bx + Cx² + Dx³.
         * Before to compute, translate the curve such that (x1,y1) = (0,0),
         * which simplify a lot the equation. In such case:
         *
         *   A = 0
         *   B = dy1
         *   C and D: see code below.
         */
        x2 -= x1;
        y2 -= y1;
        final double d = (dy2 - dy1)   / x2;
        final double w = (dy1 - y2/x2) / x2;
        final double D = (2*w + d)     / x2;
        final double C = -3*w - d;
        /*
         * For location the minimum, we search the location where the derivative is null:
         *
         *    B + 2Cx + 3Dx² == 0    ⇒    x = (-b ± √(b² - 4ac)) / (2a)
         *
         * where, a = 3*D,  b = 2*C  and  c = B = dy1
         */
        final double a  = 3*D;
        final double b  = 2*C;
        final double q  = -0.5*(b + copySign(sqrt(b*b - 4*a*dy1), b));
        final double r1 = q / a;
        final double r2 = dy1 / q;
        return new Line2D.Double(
                x1 + r1, y1 + r1*(dy1 + r1*(C + r1*D)),
                x1 + r2, y1 + r2*(dy1 + r2*(C + r2*D)));
    }

    /**
     * Attempts to replace an arbitrary shape by one of the standard Java2D constructs.
     * For example if the given {@code path} is a {@link Path2D} containing only a single
     * line or a quadratic curve, then this method replaces it by a {@link Line2D} or
     * {@link QuadCurve2D} object respectively.
     *
     * @param  path The shape to replace by a simpler Java2D construct. This is generally
     *         an instance of {@link Path2D}, but doesn't have to.
     * @return A simpler Java construct, or {@code path} if no better construct is proposed.
     */
    public static Shape toPrimitive(final Shape path) {
        return org.apache.sis.internal.referencing.j2d.ShapeUtilities.toPrimitive(path);
    }

    /**
     * Returns a suggested value for the {@code flatness} argument in
     * {@link Shape#getPathIterator(AffineTransform,double)} for the specified shape.
     *
     * @param shape The shape for which to compute a flatness factor.
     * @return The suggested flatness factor.
     */
    public static double getFlatness(final Shape shape) {
        final Rectangle2D bounds = shape.getBounds2D();
        final double dx = bounds.getWidth();
        final double dy = bounds.getHeight();
        return max(0.025 * min(dx, dy),
                   0.001 * max(dx, dy));
    }
}
