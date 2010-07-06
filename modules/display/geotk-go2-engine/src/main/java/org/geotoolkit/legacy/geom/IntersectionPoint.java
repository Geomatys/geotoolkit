/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.legacy.geom;

import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import org.geotoolkit.geometry.DirectPosition2D;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.geometry.DirectPosition;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;


/**
 * Coordinate associated with an intersection between two lines. This class is reseved for internal
 * use in order to determine how we should reclose geometric shapes of islands and continents.
 * The point memorised by this class will come from the intersection of two lines:
 * one of the edges of the map (generally one of the 4 sides of a rectangle, but it could be another
 * geometric shape) with a line passing through the two first or the two last points of the 
 * shore line.  We will call the first line (that of the map edge) "<code>line</code>".
 * This class will memorise the scalar product between a vector passing through the first point
 * of <code>line</code> and the intersection point with a vector passing through the first and last
 * points of <code>line</code>. This scalar product can be viewed as a sort of measure of the distance
 * between the start of <code>line</code> and the intersection point.
 *
 * @version $Id: IntersectionPoint.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 * @module pending
 */
final class IntersectionPoint extends Point2D.Double implements Comparable {
    /**
     * Number of the edge on which this point was found. This information is left to the
     * discretion of the programmer, who will put in the information he wishes.
     * This number could be useful to help us later refind the line on which the intersection
     * point was found.
     */
    int border;

    /**
     * Distance squared between the intersection point and the point that was closest to it.
     * This information is not used by this class, except in the method {@link #toString}.
     * It is useful for debugging purposes, but also for choosing which intersection point to
     * delete if there are too many.  We will delete the point which is found furthest from 
     * its border.
     */
    double minDistanceSq = java.lang.Double.NaN;

    /**
     * Scalar product between the line on which the intersection point was found and a vector
     * going from the start of this line to the intersection point.
     */
    double scalarProduct;

    /**
     * Polyline to which the line with which we calculated an
     * intersection point belonged.
     */
    Polyline path;

    /**
     * Indicates whether the intersection point was calculated from the two first or the two last
     * points of the shoreline. If <code>append</code> has the value <code>true</code>, this means
     * that the intersection was calculated from the two last points of the shoreline.  To
     * reclose the geometric shape of the island or continent would imply therefore that we add
     * points to the end of the shoreline ("append"), as opposed to adding points to the start of
     * the shoreline ("prepend").
     */
    boolean append;

    /**
     * This point's coordinate system. This information is only used by the method
     * {@link #toString}, so that a coordinate can be written in latitude and longitude.
     */
    CoordinateReferenceSystem crs;

    /**
     * Constructs a point initialised at (0,0).
     */
    public IntersectionPoint() {
    }

    /**
     * Constructs a point initialised at the specified position.
     */
    public IntersectionPoint(final Point2D point) {
        super(point.getX(), point.getY());
    }

    /**
     * Memorises in this object the position of the specified point. The scalar product
     * of this point with the line <code>line</code> will also be calculated and placed
     * in the field {@link #scalarProduct}.
     *
     * @param point  Coordinates of the intersection.
     * @param line   Coordinates of the line on which the intersection <code>point</code> was
     *               found.
     * @param border Number of the line <code>line</code>. This information will be memorised in
     *               the field {@link #border} and is left to the discretion of the programmer.
     *               It is recommended to use a unique number for each line <code>line</code>,
     *               which grow in the same order as the lines <code>line</code> are swept.
     */
    final void setLocation(final Point2D point, final Line2D.Double line, final int border) {
        super.setLocation(point);
        final double dx = line.x2-line.x1;
        final double dy = line.y2-line.y1;
        scalarProduct = ((x-line.x1)*dx+(y-line.y1)*dy) / Math.sqrt(dx*dx + dy*dy);
        this.border = border;
    }

    /**
     * Compares this point with another. This comparison only involves
     * the position of these points on a particular segment. It will allow
     * the points to be classified in a clockwise fashion, or anticlockwise
     * depending on the way in which {@link PathIterator} is implemented.
     *
     * @param o Another intersection point with which to compare this one.
     * @return -1, 0 or +1 depending on whether this point, precedes, equals or
     *         follows point <code>o</code> in a particular direction (generally  
     *         clockwise).
     */
    public int compareTo(final IntersectionPoint pt) {
        if (border < pt.border) return -1;
        if (border > pt.border) return +1;
        if (scalarProduct < pt.scalarProduct) return -1;
        if (scalarProduct > pt.scalarProduct) return +1;
        return 0;
    }

    /**
     * Compares this point with another. This comparison only imvolves
     * the position of these points on a particular segment. It will allow
     * the classification of the points in a clockwise fashion, or anticlockwise
     * depending on the way in which {@link PathIterator} is implemented.
     *
     * @param o Another intersection point with which to compare this one.
     * @return -1, 0 or +1 depending on whether this point precedes, equals or follows
     *         point <code>o</code> in a particular direction (generally clockwise).
     */
    @Override
    public int compareTo(Object o) {
        return compareTo((IntersectionPoint) o);
    }

    /**
     * Indicates whether this intersection point is identical to point <code>o</code>.
     * This method is defined to be coherent with {@link #compareTo}, but isn't used.
     *
     * @return <code>true</code> if this intersection point is the same as <code>o</code>.
     */
    @Override
    public boolean equals(final Object o) {
        if (o instanceof IntersectionPoint) {
            return compareTo((IntersectionPoint) o) == 0;
        } else {
            return false;
        }
    }

    /**
     * Returns an almost unique code for this intersection point, based on
     * the scalar product and the line number. This code will be coherent
     * with the method {@link #equals}.
     *
     * @return An almost unique number for this intersection point.
     */
    @Override
    public int hashCode() {
        final long bits = java.lang.Double.doubleToLongBits(scalarProduct);
        return border ^ (int)bits ^ (int)(bits >>> 32);
    }

    /**
     * Sends a character string representation of this intersection
     * point (for debugging purposes only).
     *
     * @return Character string representing this intersection point.
     */
    @Override
    public String toString() {
        final CoordinateReferenceSystem WGS84 = DefaultGeographicCRS.WGS84;
        final StringBuffer buffer = new StringBuffer("IntersectionPoint[");
        if (crs != null) {
            try {
                DirectPosition dp2d = new DirectPosition2D(this);
                MathTransform transform = CRS.findMathTransform(crs, WGS84);
                dp2d = transform.transform(dp2d, dp2d);
                buffer.append(dp2d);
            } catch (TransformException exception) {
                buffer.append("error");
            } catch (FactoryException exception) {
                buffer.append("error");
            }
        } else {
            buffer.append((float) x);
            buffer.append(' ');
            buffer.append((float) y);
        }
        buffer.append(']');
        if (!java.lang.Double.isNaN(minDistanceSq)) {
            buffer.append(" at ");
            buffer.append((float) Math.sqrt(minDistanceSq));
        }
        if (crs != null) {
            buffer.append(' ');
            buffer.append(crs.getCoordinateSystem().getAxis(0).getUnit());
        }
        buffer.append(" from #");
        buffer.append(border);
        buffer.append(" (");
        buffer.append((float) scalarProduct);
        buffer.append(')');
        return buffer.toString();
    }
}
