/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.geom.Point2D;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;

import org.geotoolkit.util.Cloneable;


/**
 * Holds the coordinates for a two-dimensional position within some coordinate reference system.
 * This class inherits {@linkplain #x x} and {@linkplain #y y} fields. But despite their names,
 * they don't need to be oriented toward {@linkplain AxisDirection#EAST East} and {@linkplain
 * AxisDirection#NORTH North}. The (<var>x</var>,<var>y</var>) axis can have any orientation and
 * should be understood as "<cite>ordinate 0</cite>" and "<cite>ordinate 1</cite>" values instead.
 * This is not specific to this implementation; in Java2D too, the visual axis orientation depend
 * on the {@linkplain java.awt.Graphics2D#getTransform affine transform in the graphics context}.
 * <p>
 * The rational for avoiding axis orientation restriction is that other {@link DirectPosition}
 * implementation do not have such restriction, and it would be hard to generalize. For example
 * there is no clear "x" or "y" classification for {@linkplain AxisDirection#NORTH_EAST North-East}
 * direction.
 * <p>
 * {@section Caution when used in collections}
 * <strong>Do not mix instances of this class with ordinary {@link Point2D} instances in a
 * {@link java.util.HashSet} or as {@link java.util.HashMap} keys.</strong> It is not possible to
 * meet both {@link Point2D#hashCode} and {@link DirectPosition#hashCode} contract, and this class
 * chooses to implements the later. Consequently, {@link #hashCode} is inconsistent with
 * {@link Point2D#equals} but is consistent with {@link DirectPosition#equals}.
 * <p>
 * In other words, it is safe to add instances of {@code DirectPosition2D} in a
 * {@code HashSet<DirectPosition>}, but it is unsafe to add them in a {@code HashSet<Point2D>}.
 * Collections that do not rely on {@link Object#hashCode}, like {@link java.util.ArrayList},
 * are safe in all cases.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @see DirectPosition1D
 * @see GeneralDirectPosition
 * @see java.awt.geom.Point2D
 *
 * @since 2.0
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.geometry.DirectPosition2D}.
 */
@Deprecated
public class DirectPosition2D extends org.apache.sis.geometry.DirectPosition2D implements Cloneable {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 835130287438466997L;

    /**
     * Constructs a position initialized to (0,0) with a {@code null} coordinate reference system.
     */
    public DirectPosition2D() {
    }

    /**
     * Constructs a position with the specified coordinate reference system.
     *
     * @param crs The coordinate reference system, or {@code null}.
     */
    public DirectPosition2D(final CoordinateReferenceSystem crs) {
        super(crs);
    }

    /**
     * Constructs a 2D position from the specified ordinates. Despite their name,
     * the (<var>x</var>,<var>y</var>) coordinates don't need to be oriented toward
     * ({@linkplain AxisDirection#EAST East}, {@linkplain AxisDirection#NORTH North}).
     * Those parameter names simply match the {@linkplain #x x} and {@linkplain #y y}
     * fields. See the <a href="#skip-navbar_top">class javadoc</a> for details.
     *
     * @param x The <var>x</var> value.
     * @param y The <var>y</var> value.
     */
    public DirectPosition2D(final double x, final double y) {
        super(x,y);
    }

    /**
     * Constructs a 2D position from the specified ordinates in the specified CRS. Despite
     * their name, the (<var>x</var>,<var>y</var>) coordinates don't need to be oriented toward
     * ({@linkplain AxisDirection#EAST East}, {@linkplain AxisDirection#NORTH North}).
     * Those parameter names simply match the {@linkplain #x x} and {@linkplain #y y}
     * fields. The actual axis orientations are determined by the specified CRS.
     * See the <a href="#skip-navbar_top">class javadoc</a> for details.
     *
     * @param crs The coordinate reference system, or {@code null}.
     * @param x The <var>x</var> value.
     * @param y The <var>y</var> value.
     */
    public DirectPosition2D(final CoordinateReferenceSystem crs,
                            final double x, final double y)
    {
        super(crs, x, y);
    }

    /**
     * Constructs a position from the specified {@link Point2D}.
     *
     * @param point The point to copy.
     */
    public DirectPosition2D(final Point2D point) {
        super(point.getX(), point.getY());
        if (point instanceof DirectPosition) {
            setCoordinateReferenceSystem(((DirectPosition) point).getCoordinateReferenceSystem());
        }
    }

    /**
     * Constructs a position initialized to the same values than the specified point.
     *
     * @param  point The point to copy.
     * @throws MismatchedDimensionException if this point doesn't have the expected dimension.
     */
    public DirectPosition2D(final DirectPosition point) throws MismatchedDimensionException {
        super(point);
    }

    /**
     * Constructs a position initialized to the values parsed from the given string in
     * <cite>Well Known Text</cite> (WKT) format. The given string is typically a {@code POINT}
     * element like below:
     *
     * {@preformat wkt
     *     POINT(6 10)
     * }
     *
     * @param  wkt The {@code POINT} or other kind of element to parse.
     * @throws NumberFormatException If a number can not be parsed.
     * @throws IllegalArgumentException If the parenthesis are not balanced.
     * @throws MismatchedDimensionException If the given point is not two-dimensional.
     *
     * @see AbstractDirectPosition#toString(DirectPosition)
     * @see org.geotoolkit.measure.CoordinateFormat
     *
     * @since 3.09
     */
    public DirectPosition2D(final String wkt) throws NumberFormatException, IllegalArgumentException {
        super(wkt);
    }

    /**
     * Sets this coordinate to the specified direct position. If the specified position
     * contains a {@linkplain CoordinateReferenceSystem coordinate reference system},
     * then the CRS for this position will be set to the CRS of the specified position.
     *
     * @param  position The new position for this point.
     * @throws MismatchedDimensionException if this point doesn't have the expected dimension.
     */
    public void setLocation(final DirectPosition position) throws MismatchedDimensionException {
        AbstractDirectPosition.ensureDimensionMatch("position", position.getDimension(), 2);
        setCoordinateReferenceSystem(position.getCoordinateReferenceSystem());
        x = position.getOrdinate(0);
        y = position.getOrdinate(1);
    }

    /**
     * Returns a {@link Point2D} with the same coordinate as this direct position.
     *
     * @return This position as a point.
     */
    public Point2D toPoint2D() {
        return new Point2D.Double(x,y);
    }
}
