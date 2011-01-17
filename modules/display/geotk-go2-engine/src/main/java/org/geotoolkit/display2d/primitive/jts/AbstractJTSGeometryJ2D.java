/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.display2d.primitive.jts;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;

import org.geotoolkit.util.logging.Logging;

/**
 * A thin wrapper that adapts a JTS geometry to the Shape interface so that the geometry can be used
 * by java2d without coordinate cloning.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @version 2.9
 * @module pending
 */
public abstract class AbstractJTSGeometryJ2D<T extends Geometry> implements Shape, Cloneable {

    /** The wrapped JTS geometry */
    protected T geometry;

    /** An additional AffineTransform */
    protected final AffineTransform transform;

    public AbstractJTSGeometryJ2D(final T geom) {
        this(geom, JTSGeometryIterator.IDENTITY);
    }

    /**
     * Creates a new GeometryJ2D object.
     *
     * @param geom - the wrapped geometry
     */
    public AbstractJTSGeometryJ2D(final T geom, final AffineTransform trs) {
        this.geometry = geom;
        this.transform = trs;
    }

    /**
     * Sets the geometry contained in this lite shape. Convenient to reuse this
     * object instead of creating it again and again during rendering
     *
     * @param g
     */
    public void setGeometry(final T g) {
        this.geometry = g;
    }

    /**
     * @return the current wrapped geometry
     */
    public T getGeometry() {
        return geometry;
    }

    protected AffineTransform getInverse(){
        try {
            return transform.createInverse();
        } catch (NoninvertibleTransformException ex) {
            Logging.getLogger(JTSGeometryJ2D.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean contains(final Rectangle2D r) {
        return contains(r.getMinX(),r.getMinY(),r.getWidth(),r.getHeight());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean contains(final Point2D p) {
        final AffineTransform inverse = getInverse();
        inverse.transform(p, p);
        final Coordinate coord = new Coordinate(p.getX(), p.getY());
        final Geometry point = geometry.getFactory().createPoint(coord);
        return geometry.contains(point);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean contains(final double x, final double y) {
        final Point2D p = new Point2D.Double(x, y);
        return contains(p);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean contains(final double x, final double y, final double w, final double h) {
        Geometry rect = createRectangle(x, y, w, h);
        return geometry.contains(rect);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Rectangle getBounds() {
        return getBounds2D().getBounds();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Rectangle2D getBounds2D() {
        final Envelope env = geometry.getEnvelopeInternal();
        final Point2D p1 = new Point2D.Double(env.getMinX(), env.getMinY());
        transform.transform(p1, p1);
        final Point2D p2 = new Point2D.Double(env.getMaxX(), env.getMaxY());
        transform.transform(p2, p2);

        final Rectangle2D rect = new Rectangle2D.Double(p1.getX(), p1.getY(), 0, 0);
        rect.add(p2);
        return rect;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PathIterator getPathIterator(final AffineTransform at, final double flatness) {
        return getPathIterator(at);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean intersects(final Rectangle2D r) {
        final Geometry rect = createRectangle(
                r.getMinX(),
                r.getMinY(),
                r.getWidth(),
                r.getHeight());
        return geometry.intersects(rect);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean intersects(final double x, final double y, final double w, final double h) {
        Geometry rect = createRectangle(x, y, w, h);
        return geometry.intersects(rect);
    }

    /**
     * Creates a jts Geometry object representing a rectangle with the given
     * parameters
     *
     * @param x left coordinate
     * @param y bottom coordinate
     * @param w width
     * @param h height     *
     * @return a rectangle with the specified position and size
     */
    private Geometry createRectangle(final double x, final double y, final double w, final double h) {
        final AffineTransform inverse = getInverse();
        final Point2D p1 = new Point2D.Double(x, y);
        inverse.transform(p1, p1);
        final Point2D p2 = new Point2D.Double(x+w, y+h);
        inverse.transform(p2, p2);

        final Coordinate[] coords = {
            new Coordinate(p1.getX(), p1.getY()),
            new Coordinate(p1.getX(), p2.getY()),
            new Coordinate(p2.getX(), p2.getY()),
            new Coordinate(p2.getX(), p1.getY()),
            new Coordinate(p1.getX(), p1.getY())
        };
        final LinearRing lr = geometry.getFactory().createLinearRing(coords);
        return geometry.getFactory().createPolygon(lr, null);
    }

    @Override
    public AbstractJTSGeometryJ2D clone()  {
        return null; //TODO
    }

}
