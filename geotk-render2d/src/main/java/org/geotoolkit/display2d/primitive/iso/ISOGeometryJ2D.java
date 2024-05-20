/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.display2d.primitive.iso;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.geometry.JTSGeometryFactory;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSPrimitiveFactory;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.coordinate.PolyhedralSurface;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.SurfaceBoundary;

/**
 * A thin wrapper that adapts a ISO geometry to the Shape interface so that the geometry can be used
 * by java2d without coordinate cloning.
 *
 * @author Johann Sorel (Geomatys)
 * @version 2.9
 * @module
 */
public class ISOGeometryJ2D implements Shape, Cloneable {

    private ISOGeometryIterator<? extends Geometry> iterator = null;

    /** The wrapped ISO geometry */
    private Geometry geometry;

    /**
     * Creates a new GeometryJ2D object.
     *
     * @param geom - the wrapped geometry
     */
    public ISOGeometryJ2D(final Geometry geom) {
        this.geometry = geom;
    }

    /**
     * Sets the geometry contained in this lite shape. Convenient to reuse this
     * object instead of creating it again and again during rendering
     */
    public void setGeometry(final Geometry g) {
        this.geometry = g;
        this.iterator = null;
    }

    /**
     * @return the current wrapped geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean contains(final Rectangle2D r) {
        Geometry rect = createRectangle(
                r.getMinX(),
                r.getMinY(),
                r.getWidth(),
                r.getHeight());
        return geometry.contains(rect);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean contains(final Point2D p) {
        return geometry.contains(new DirectPosition2D(p.getX(), p.getY()));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean contains(final double x, final double y) {
        return geometry.contains(new DirectPosition2D(x,y));
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
        Envelope env = geometry.getEnvelope();
        return new Rectangle(
                (int)(env.getMinimum(0)+0.5),
                (int)(env.getMinimum(1)+0.5),
                (int)(env.getSpan(0)+0.5),
                (int)(env.getSpan(1)+0.5));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Rectangle2D getBounds2D() {
        Envelope env = geometry.getEnvelope();
        return new Rectangle2D.Double(
                env.getMinimum(0),
                env.getMinimum(1),
                env.getSpan(0),
                env.getSpan(1));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public PathIterator getPathIterator(final AffineTransform at) {

        if(iterator == null){
//            if (this.geometry.isEmpty()) {
//                iterator = new ISOEmptyIterator();
//            }else
            if (this.geometry instanceof Point) {
                iterator = new ISOPointIterator((Point) geometry, at);
            } else if (this.geometry instanceof PolyhedralSurface) {
                iterator = new ISOPolyhedralSurfaceIterator((PolyhedralSurface) geometry, at);
            } else if (this.geometry instanceof Curve) {
                iterator = new ISOCurveIterator((Curve)geometry, at);
            } else if (this.geometry instanceof MultiPrimitive) {
                iterator = new ISOMultiPrimitiveIterator((MultiPrimitive)geometry,at);
            }
        }else{
            iterator.setTransform(at);
        }

        return iterator;
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
        Geometry rect = createRectangle(
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
     * @param h height
     * @return a rectangle with the specified position and size
     */
    private Geometry createRectangle(final double x, final double y, final double w, final double h) {
        final var gf = new JTSGeometryFactory(geometry.getCoordinateReferenceSystem());
        final var pf = new JTSPrimitiveFactory(geometry.getCoordinateReferenceSystem());

        final List<Position> points = new ArrayList<Position>();
        points.add(new DirectPosition2D(x,   y));
        points.add(new DirectPosition2D(x+w, y));
        points.add(new DirectPosition2D(x+w, y+h));
        points.add(new DirectPosition2D(x,   y+h));
        points.add(new DirectPosition2D(x,   y));

        CurveSegment segment = gf.createLineString(points);
        OrientableCurve curve = pf.createCurve(Collections.singletonList(segment));
        Ring exterior = pf.createRing(Collections.singletonList(curve));

        SurfaceBoundary boundary = pf.createSurfaceBoundary(exterior, null);

        return boundary;
    }

}
