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
import java.awt.geom.AffineTransform;

/**
 * Simple and efficient path iterator for JTS LineString.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 * @since 2.9
 */
public final class JTSLineIterator extends JTSGeometryIterator<LineString> {

    private final CoordinateSequence coordinates;
    private final int coordinateCount;
    /** True if the line is a ring */
    private final boolean isClosed;

    private int currentCoord = 0;
    private boolean done = false;

    /**
     * Creates a new instance of LineIterator
     *
     * @param ls The line string the iterator will use
     * @param trs The affine transform applied to coordinates during iteration
     */
    public JTSLineIterator(LineString ls, AffineTransform trs) {
        super(ls,trs);
        coordinates = ls.getCoordinateSequence();
        coordinateCount = coordinates.size();
        isClosed = ls instanceof LinearRing;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void reset(){
        done = false;
        currentCoord = 0;
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
        if (((currentCoord == (coordinateCount - 1)) && !isClosed)
            || ((currentCoord == coordinateCount) && isClosed)) {
            done = true;
        } else {
            currentCoord++;
        }
    }

	/**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(double[] coords) {
        if (currentCoord == 0) {
            coords[0] = (double) coordinates.getX(0);
            coords[1] = (double) coordinates.getY(0);
            transform.transform(coords, 0, coords, 0, 1);
            return SEG_MOVETO;
        } else if ((currentCoord == coordinateCount) && isClosed) {
            return SEG_CLOSE;
        } else {
            coords[0] = coordinates.getX(currentCoord);
            coords[1] = coordinates.getY(currentCoord);
            transform.transform(coords, 0, coords, 0, 1);            
            return SEG_LINETO;
        }
    }
	
}
