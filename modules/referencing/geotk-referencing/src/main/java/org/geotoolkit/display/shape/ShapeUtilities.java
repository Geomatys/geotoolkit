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
     *
     * @deprecated Moved to Apache SIS as {@link org.apache.sis.geometry.Shapes2D#intersectionPoint(Line2D, Line2D)}.
     */
    @Deprecated
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
     *
     * @deprecated Moved to Apache SIS as {@link org.apache.sis.geometry.Shapes2D#nearestColinearPoint(Line2D, Point2D)}.
     */
    @Deprecated
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
     *
     * @deprecated Moved to Apache SIS as {@link org.apache.sis.geometry.Shapes2D#colinearPoint(Line2D, Point2D, double)}.
     */
    @Deprecated
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
     * Returns a circle passing by the 3 given points.
     *
     * @param  P1 The first point.
     * @param  P2 The second point.
     * @param  P3 The third point.
     * @return A circle passing by the given points.
     *
     * @deprecated Moved to Apache SIS as {@link org.apache.sis.geometry.Shapes2D#circle(Point2D, Point2D, Point2D)
     */
    @Deprecated
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
