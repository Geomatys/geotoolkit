/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2004-2008, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.display2d.style;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.geotoolkit.display2d.geom.CompressionLevel;
import org.geotoolkit.display2d.geom.UnmodifiableGeometryException;
import org.geotoolkit.math.Statistics;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CachedShape extends org.geotoolkit.display2d.geom.Geometry{


    private Geometry jtsSource = null;

    public void setGeometry(Geometry geom){

    }



    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCoordinateReferenceSystem(CoordinateReferenceSystem coordinateSystem) throws TransformException, UnmodifiableGeometryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getPointCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Rectangle2D getBounds2D() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(Point2D point) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(Shape shape) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean intersects(Shape shape) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public float compress(CompressionLevel level) throws FactoryException, TransformException, UnmodifiableGeometryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Statistics getResolution() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setResolution(double resolution) throws FactoryException, TransformException, UnmodifiableGeometryException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PathIterator getPathIterator(AffineTransform transform) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
