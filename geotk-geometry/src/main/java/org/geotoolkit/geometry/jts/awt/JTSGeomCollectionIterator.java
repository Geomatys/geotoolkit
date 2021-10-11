/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.geometry.jts.awt;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.referencing.operation.MathTransform;

/**
 * Simple and efficient path iterator for JTS GeometryCollection.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 * @since 2.9
 */
public class JTSGeomCollectionIterator extends JTSGeometryIterator<GeometryCollection> {

    protected int nbGeom = 1;
    protected int currentGeom;
    protected JTSGeometryIterator currentIterator;
    protected boolean done = false;

    public JTSGeomCollectionIterator(final GeometryCollection gc, final MathTransform trs) {
        super(gc,trs);
        reset();
    }

    @Override
    public void reset(){
        currentGeom = 0;
        done = false;
        nbGeom = geometry.getNumGeometries();
        if(geometry != null && nbGeom > 0){
            prepareIterator(geometry.getGeometryN(0));
        }else{
            done = true;
        }
    }

    @Override
    public void setGeometry(final GeometryCollection geom) {
        super.setGeometry(geom);
        if(geom == null){
            nbGeom = 0;
        }else{
            nbGeom = geom.getNumGeometries();
        }
    }

    /**
     * Returns the specific iterator for the geometry passed.
     *
     * @param candidate The geometry whole iterator is requested
     */
    protected void prepareIterator(final Geometry candidate) {

        //try to reuse the previous iterator.
        if (candidate.isEmpty()) {
            if(currentIterator instanceof JTSEmptyIterator){
                //nothing to do
            }else{
                currentIterator = JTSEmptyIterator.INSTANCE;
            }

        }else if (candidate instanceof Point) {
            if(currentIterator instanceof JTSPointIterator){
                currentIterator.setGeometry(candidate);
            }else{
                currentIterator = new JTSPointIterator((Point)candidate, transform);
            }

        } else if (candidate instanceof Polygon) {
            if(currentIterator instanceof JTSPolygonIterator){
                currentIterator.setGeometry(candidate);
            }else{
                currentIterator = new JTSPolygonIterator((Polygon)candidate, transform);
            }

        } else if (candidate instanceof LineString) {
            if(currentIterator instanceof JTSLineIterator){
                currentIterator.setGeometry(candidate);
            }else{
                currentIterator = new JTSLineIterator((LineString)candidate, transform);
            }

        } else if (candidate instanceof GeometryCollection) {
            if(currentIterator instanceof JTSGeomCollectionIterator){
                currentIterator.setGeometry(candidate);
            }else{
                currentIterator = new JTSGeomCollectionIterator((GeometryCollection)candidate, transform);
            }

        }else{
            currentIterator = JTSEmptyIterator.INSTANCE;
        }

    }

    @Override
    public void setTransform(final MathTransform trs) {
        if(currentIterator != null){
            currentIterator.setTransform(trs);
        }
        super.setTransform(trs);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(final double[] coords) {
        return currentIterator.currentSegment(coords);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(final float[] coords) {
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
        return done;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void next() {
        currentIterator.next();

        if (currentIterator.isDone()) {
            if (currentGeom < (nbGeom - 1)) {
                currentGeom++;
                prepareIterator(geometry.getGeometryN(currentGeom));
            } else {
                done = true;
            }
        }
    }

}
