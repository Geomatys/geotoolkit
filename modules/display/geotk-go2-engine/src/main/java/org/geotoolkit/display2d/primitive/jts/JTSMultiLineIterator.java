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

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import java.awt.geom.AffineTransform;

/**
 * Simple and efficient path iterator for JTS LineString.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 * @since 2.9
 */
public final class JTSMultiLineIterator extends JTSGeometryIterator<MultiLineString> {

    private int coordinateCount;
    /** True if the line is a ring */
    private boolean isClosed;

    //global geometry state
    private int nbGeom = 0;
    private int currentGeom = -1;
    private boolean done = false;
    
    //sub geometry state
    private CoordinateSequence currentSequence = null;
    private int currentCoord = -1;
    private boolean subGeomDone = false;

    /**
     * Creates a new instance of LineIterator
     *
     * @param ls The line string the iterator will use
     * @param trs The affine transform applied to coordinates during iteration
     */
    public JTSMultiLineIterator(MultiLineString ls, AffineTransform trs) {
        super(ls,trs);
        setGeometry(ls);
    }

    @Override
    public void setGeometry(MultiLineString geom) {
        super.setGeometry(geom);
        if(geom != null){
            nbGeom = geom.getNumGeometries();
            nextSubGeom();
        }
    }

    private void nextSubGeom(){
        if(++currentGeom >= nbGeom){
            //nothing left, we are done
            currentSequence = null;
            currentCoord = -1;
            subGeomDone = true;
            done = true;
        }else{
            final LineString subGeom = ((LineString)geometry.getGeometryN(currentGeom));
            currentSequence = subGeom.getCoordinateSequence();
            coordinateCount = currentSequence.size();

            if(coordinateCount == 0){
                //no point in this line, skip it
                nextSubGeom();
            }else{
                isClosed = subGeom instanceof LinearRing;
                currentCoord = 0;
                subGeomDone = false;
                done = false;
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void reset(){
        currentGeom = -1;
        nextSubGeom();
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
        if(++currentCoord > coordinateCount){
            //we go to the size, even if we don't have a coordinate at this index,
            //to indicate we close the path
            //no more points in this segment
            nextSubGeom();
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(double[] coords) {
        if (currentCoord == 0) {
            coords[0] = currentSequence.getX(currentCoord);
            coords[1] = currentSequence.getY(currentCoord);
            transform.transform(coords, 0, coords, 0, 1);
            return SEG_MOVETO;
        } else if (currentCoord == coordinateCount) {
            return SEG_CLOSE;
        } else {
            coords[0] = currentSequence.getX(currentCoord);
            coords[1] = currentSequence.getY(currentCoord);
            transform.transform(coords, 0, coords, 0, 1);            
            return SEG_LINETO;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(float[] coords) {
        if (currentCoord == 0) {
            coords[0] = (float) currentSequence.getX(currentCoord);
            coords[1] = (float) currentSequence.getY(currentCoord);
            transform.transform(coords, 0, coords, 0, 1);
            return SEG_MOVETO;
        } else if (currentCoord == coordinateCount) {
            return SEG_CLOSE;
        } else {
            coords[0] = (float) currentSequence.getX(currentCoord);
            coords[1] = (float) currentSequence.getY(currentCoord);
            transform.transform(coords, 0, coords, 0, 1);
            return SEG_LINETO;
        }
    }

	
}
