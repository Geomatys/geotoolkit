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
public final class JTSGeomCollectionIterator extends JTSGeometryIterator<GeometryCollection> {

    private int currentGeom;
    private PathIterator currentIterator;
    private boolean done = false;

    public JTSGeomCollectionIterator(GeometryCollection gc, AffineTransform trs) {
        super(gc,trs);
        reset();
    }

    @Override
    public void reset(){
        currentGeom = 0;
        done = false;
        currentIterator = (geometry.isEmpty()) ?
            new JTSEmptyIterator() :
            getIterator(geometry.getGeometryN(0));
    }

    /**
     * Returns the specific iterator for the geometry passed.
     *
     * @param candidate The geometry whole iterator is requested
     *
     * @return the specific iterator for the geometry passed.
     */
    private JTSGeometryIterator getIterator(Geometry candidate) {
        JTSGeometryIterator iterator = null;

        if (candidate.isEmpty()) {
            iterator = new JTSEmptyIterator();
        }else if (candidate instanceof Point) {
            iterator = new JTSPointIterator((Point)candidate, transform);
        } else if (candidate instanceof Polygon) {
            iterator = new JTSPolygonIterator((Polygon)candidate, transform);
        } else if (candidate instanceof LineString) {
            iterator = new JTSLineIterator((LineString)candidate, transform);
        } else if (candidate instanceof GeometryCollection) {
            iterator = new JTSGeomCollectionIterator((GeometryCollection)candidate,transform);
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
