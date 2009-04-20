/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Simple and efficient path iterator for JTS GeometryCollection.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @since 2.9
 */
public final class GeomCollectionIterator extends GeometryIterator<GeometryCollection> {

    private int currentGeom;
    private PathIterator currentIterator;
    private boolean done = false;

    public GeomCollectionIterator(GeometryCollection gc, AffineTransform trs) {
        super(gc,trs);
        reset();
    }

    private void reset(){
        currentGeom = 0;
        done = false;
        currentIterator = (geometry.isEmpty()) ?
            new EmptyIterator() :
            getIterator(geometry.getGeometryN(0));
    }

    /**
     * Returns the specific iterator for the geometry passed.
     *
     * @param candidate The geometry whole iterator is requested
     *
     * @return the specific iterator for the geometry passed.
     */
    private GeometryIterator getIterator(Geometry candidate) {
        GeometryIterator iterator = null;

        if (candidate.isEmpty()) {
            iterator = new EmptyIterator();
        }else if (candidate instanceof Point) {
            iterator = new PointIterator((Point)candidate, transform);
        } else if (candidate instanceof Polygon) {
            iterator = new PolygonIterator((Polygon)candidate, transform);
        } else if (candidate instanceof LineString) {
            iterator = new LineIterator((LineString)candidate, transform);
        } else if (candidate instanceof GeometryCollection) {
            iterator = new GeomCollectionIterator((GeometryCollection)candidate,transform);
        }

        return iterator;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(double[] coords) {
        return currentIterator.currentSegment(coords);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(float[] coords) {
        return currentIterator.currentSegment(coords);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getWindingRule() {
        return WIND_NON_ZERO;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isDone() {
        if(done){
            reset();
            return true;
        }else{
            return false;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void next() {
        if (currentIterator.isDone()) {
            if (currentGeom < (geometry.getNumGeometries() - 1)) {
                currentGeom++;
                currentIterator = getIterator(geometry.getGeometryN(currentGeom));
            } else {
                done = true;
            }
        } else {
            currentIterator.next();
        }
    }
    
}
