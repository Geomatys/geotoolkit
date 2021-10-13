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
import org.locationtech.jts.geom.Polygon;
import org.opengis.referencing.operation.MathTransform;

/**
 * Simple and efficient path iterator for JTS Polygon.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 * @since 2.9
 */
public final  class JTSPolygonIterator extends JTSGeometryIterator<Polygon> {

    /** The rings describing the polygon geometry */
    private LineString[] rings;

    /** The current ring during iteration */
    private int currentRing = 0;

    /** Current line coordinate */
    private int currentCoord = 0;

    /** The array of coordinates that represents the line geometry */
    private CoordinateSequence coords = null;
    private int csSize = 0;

    /** True when the iteration is terminated */
    private boolean done = false;

    /**
     * Creates a new PolygonIterator object.
     *
     * @param p The polygon
     * @param trs The affine transform applied to coordinates during iteration
     */
    public JTSPolygonIterator(final Polygon p, final MathTransform trs) {
        super(p,trs);
        setGeometry(p);
    }

    @Override
    public void setGeometry(final Polygon geom) {
        this.geometry = geom;
        if(geom != null){
            int numInteriorRings = geom.getNumInteriorRing();
            rings = new LineString[numInteriorRings + 1];
            rings[0] = geom.getExteriorRing();

            for (int i = 0; i < numInteriorRings; i++) {
                rings[i + 1] = geom.getInteriorRingN(i);
            }
        }
        reset();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void reset(){
        currentRing = 0;
        currentCoord = 0;
        coords = rings[0].getCoordinateSequence();
        csSize = coords.size()-1;
        done = false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(final double[] coords) {
        // first make sure we're not at the last element, this prevents us from exceptions
        // in the case where coords.size() == 0
        if (currentCoord == csSize) {
            return SEG_CLOSE;
        } else if (currentCoord == 0) {
            coords[0] = this.coords.getX(0);
            coords[1] = this.coords.getY(0);
            safeTransform(coords, 0, coords, 0, 1);
            return SEG_MOVETO;
        } else {
            coords[0] = this.coords.getX(currentCoord);
            coords[1] = this.coords.getY(currentCoord);
            safeTransform(coords, 0, coords, 0, 1);
            return SEG_LINETO;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(final float[] coords) {
        // first make sure we're not at the last element, this prevents us from exceptions
        // in the case where coords.size() == 0
        if (currentCoord == csSize) {
            return SEG_CLOSE;
        } else if (currentCoord == 0) {
            coords[0] = (float)this.coords.getX(0);
            coords[1] = (float)this.coords.getY(0);
            safeTransform(coords, 0, coords, 0, 1);
            return SEG_MOVETO;
        } else {
            coords[0] = (float)this.coords.getX(currentCoord);
            coords[1] = (float)this.coords.getY(currentCoord);
            safeTransform(coords, 0, coords, 0, 1);
            return SEG_LINETO;
        }
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public int getWindingRule() {
        return WIND_EVEN_ODD;
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
        if (currentCoord == csSize) {
            if (currentRing < (rings.length - 1)) {
                currentCoord = 0;
                currentRing++;
                coords = rings[currentRing].getCoordinateSequence();
                csSize = coords.size()-1;
            } else {
                done = true;
            }
        } else {
            currentCoord++;
        }
    }

}
