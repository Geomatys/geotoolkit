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

import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.opengis.referencing.operation.MathTransform;

/**
 * Simple and efficient path iterator for JTS LineString.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 * @since 2.9
 */
public final class DecimateJTSLineIterator extends JTSGeometryIterator<LineString> {

    private final CoordinateSequence coordinates;
    private final int coordinateCount;
    /** True if the line is a ring */
    private final boolean isClosed;

    private int lastCoord = 0;
    private int currentIndex = 0;
    private boolean done = false;
    private final double[] resolution;

    private final double[] currentCoord = new double[2];

    /**
     * Creates a new instance of LineIterator
     *
     * @param ls The line string the iterator will use
     * @param trs The affine transform applied to coordinates during iteration
     */
    public DecimateJTSLineIterator(final LineString ls, final MathTransform trs, final double[] resolution) {
        super(ls,trs);
        coordinates = ls.getCoordinateSequence();
        coordinateCount = coordinates.size();
        isClosed = ls instanceof LinearRing;
        this.resolution = resolution;
        currentCoord[0] = coordinates.getX(0);
        currentCoord[1] = coordinates.getY(0);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void reset(){
        done = false;
        currentIndex = 0;
        currentCoord[0] = coordinates.getX(0);
        currentCoord[1] = coordinates.getY(0);
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

        while(true){
            if (((currentIndex == (coordinateCount - 1)) && !isClosed)
                || ((currentIndex == coordinateCount) && isClosed)) {
                done = true;
                break;
            }

            currentIndex++;
            double candidateX = coordinates.getX(currentIndex);
            double candidateY = coordinates.getY(currentIndex);

            if(Math.abs(candidateX-currentCoord[0]) >= resolution[0] || Math.abs(candidateY-currentCoord[1]) >= resolution[1]){
                currentCoord[0] = candidateX;
                currentCoord[1] = candidateY;
                break;
            }

        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(final double[] coords) {
        if (currentIndex == 0) {
            safeTransform(currentCoord, 0, coords, 0, 1);
            return SEG_MOVETO;
        } else if ((currentIndex == coordinateCount) && isClosed) {
            return SEG_CLOSE;
        } else {
            safeTransform(currentCoord, 0, coords, 0, 1);
            return SEG_LINETO;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(final float[] coords) {
        if (currentIndex == 0) {
            safeTransform(currentCoord, 0, coords, 0, 1);
            return SEG_MOVETO;
        } else if ((currentIndex == coordinateCount) && isClosed) {
            return SEG_CLOSE;
        } else {
            safeTransform(currentCoord, 0, coords, 0, 1);
            return SEG_LINETO;
        }
    }

}
