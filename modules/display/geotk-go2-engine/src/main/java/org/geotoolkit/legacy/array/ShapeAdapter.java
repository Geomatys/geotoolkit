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
package org.geotoolkit.legacy.array;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import org.geotoolkit.referencing.operation.matrix.XAffineTransform;


/**
 * Exposes a {@link PointArray} as a shape. This shape is not designed for map rendering.
 * It is rather used for debugging purpose, as well as rendering lines in some simplier
 * context (e.g. {@link org.geotoolkit.gui.swing.Plot2D}).
 *
 * @version $Id: ShapeAdapter.java 17672 2006-01-19 00:25:55Z desruisseaux $
 * @author Martin Desruisseaux
 *
 * @see PointArray#toShape
 * @see org.geotoolkit.gui.swing.Plot2D
 *
 * @deprecated Replaced by {@link org.geotoolkit.geometry.array.ShapeAdapter}
 *             as part of the port of J2D-renderer to the new GO-1 based API. Note that it is not
 *             possible to mix J2D-renderer classes with GO-1 rendering engine. Migration from
 *             J2D-renderer to the GO-1 API will requires the replacement of all deprecated classes
 *             together. Because the new GO-1 rendering engine is a work in progress, see
 *             <A HREF="http://jira.codehaus.org/browse/GEOT-776">GEOT-776</A> in order to determine
 *             if enough functionalites have been ported for yours need.
 */
final class ShapeAdapter implements Shape, Serializable {
    /**
     * Serial version for compatibility with previous version.
     */
    private static final long serialVersionUID = -2980114321322639753L;

    /**
     * An optional transform to apply on coordinates, or <code>null</code> if none.
     */
    private final AffineTransform transform;

    /**
     * The shape bounds, or <code>null</code> if it is not yet computed.
     */
    private Rectangle2D bounds;

    /**
     * The array of (<var>x</var>,<var>y</var>) coordinates.
     */
    private final PointArray array;

    /**
     * Wrap the specified array in a shape.
     *
     * @param array     The array of (<var>x</var>,<var>y</var>) coordinates
     * @param transform An optional transform to apply on coordinates, or <code>null</code> if none.
     */
    public ShapeAdapter(final PointArray array, AffineTransform transform) {
        if (transform==null || transform.isIdentity()) {
            transform = null;
        }
        this.transform = transform;
        this.array     = array;
    }
    
    /**
     * Tests if a specified point is inside the boundary of the shape.
     * The default implementation conservatively returns <code>false</code>.
     */
    @Override
    public boolean contains(Point2D p) {
        return false;
    }
    
    /**
     * Tests if the specified coordinates are inside the boundary of the shape.
     * The default implementation conservatively returns <code>false</code>.
     */
    @Override
    public boolean contains(double x, double y) {
        return false;
    }

    /**
     * Tests if the interior of the shape entirely contains the specified rectangle.
     * The default implementation conservatively returns <code>false</code>.
     */
    @Override
    public boolean contains(Rectangle2D rectangle) {
        return false;
    }
    
    /**
     * Tests if the interior of the shape entirely contains the specified rectangular area.
     * The default implementation conservatively returns <code>false</code>.
     */
    @Override
    public boolean contains(double x, double y, double w, double h) {
        return false;
    }
    
    /**
     * Tests if the interior of the shape intersects the interior of a specified rectangle.
     */
    @Override
    public boolean intersects(Rectangle2D r) {
        return getInternalBounds2D().intersects(r);
    }
    
    /**
     * Tests if the interior of the shape intersects the interior of a specified rectangle.
     */
    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return getInternalBounds2D().intersects(x,y,w,h);
    }
    
    /**
     * Returns an integer rectangle that completely encloses the shape.
     */
    @Override
    public Rectangle getBounds() {
        return getInternalBounds2D().getBounds();
    }
    
    /**
     * Returns a high precision and more accurate bounding box of the shape.
     */
    @Override
    public Rectangle2D getBounds2D() {
        return (Rectangle2D) getInternalBounds2D().clone();
    }

    /**
     * Returns the bounding box of the shape. If the bounding box has already been computed,
     * it is returned directly. Otherwise, it it computed now and cached for future reuse.
     */
    private Rectangle2D getInternalBounds2D() {
        if (bounds == null) {
            bounds = array.getBounds2D();
            if (bounds == null) {
                bounds = new Rectangle2D.Float();
            } else if (transform != null) {
                bounds = XAffineTransform.transform(transform, bounds, bounds);
            }
        }
        return bounds;
    }
    
    /**
     * Returns an iterator object that iterates along the shape boundary.
     * If the <var>x</var> or <var>y</var> vectors contains {@linkplain Double#NaN NaN} values,
     * then those values are interpreted as holes (i.e. {@link PathIterator#SEG_MOVETO}
     * instructions).
     */
    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        if (at==null || at.isIdentity()) {
            at = transform;
        } else if (transform != null) {
            at = new AffineTransform(at);
            at.concatenate(transform);
        }
        return array.getPathIterator(at);
    }
    
    /**
     * Returns an iterator object that iterates along the shape boundary.
     * If the <var>x</var> or <var>y</var> vectors contains {@linkplain Double#NaN NaN} values,
     * then those values are interpreted as holes (i.e. {@link PathIterator#SEG_MOVETO}
     * instructions).
     */
    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return getPathIterator(at);
    }

    /**
     * The path iterator for the data to plot.
     *
     * @version $Id: ShapeAdapter.java 17672 2006-01-19 00:25:55Z desruisseaux $
     * @author Martin Desruisseaux
     */
    static final class Iterator extends Point2D.Double implements PathIterator {
        /** The current index in the iteration. */
        private final PointIterator it;

        /** The affine transform. */
        private final AffineTransform at;

        /** The value to returns. */
        private int move;

        /** Construct an iterator */
        public Iterator(final PointIterator it, final AffineTransform at) {
            this.it = it;
            this.at = at;
            next();
            if (move == SEG_LINETO) {
                move = SEG_MOVETO;
            }
        }
        
        /** Returns the winding rule for determining the interior of the path. */
        public int getWindingRule() {
            return WIND_EVEN_ODD;
        }
        
        /** Tests if the iteration is complete. */
        public boolean isDone() {
            return move == SEG_CLOSE;
        }

        /** Returns the coordinates and type of the current path segment in the iteration. */
        public int currentSegment(final double[] coords) {
            coords[0] = x;
            coords[1] = y;
            return move;
        }
        
        /** Returns the coordinates and type of the current path segment in the iteration. */
        public int currentSegment(final float[] coords) {
            coords[0] = (float)x;
            coords[1] = (float)y;
            return move;
        }
        
        /** Moves the iterator to the next segment of the path. */
        public void next() {
            move = SEG_LINETO;
            while (it.hasNext()) {
                x = it.nextX();
                y = it.nextY();
                if (!java.lang.Double.isNaN(x) && !java.lang.Double.isNaN(y)) {
                    if (at!=null) {
                        at.transform(this, this);
                    }
                    return;
                }
                move = SEG_MOVETO;
            }
            move = SEG_CLOSE;
        }
    }
}
