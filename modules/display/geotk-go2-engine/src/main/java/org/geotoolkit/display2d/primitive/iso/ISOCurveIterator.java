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

import java.awt.geom.AffineTransform;

import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.LineString;
import org.opengis.geometry.coordinate.PointArray;
import org.opengis.geometry.primitive.Curve;

/**
 * Simple and efficient path iterator for ISO Curve.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class ISOCurveIterator extends ISOGeometryIterator<Curve> {

    private final PointArray coordinates;
    private final int coordinateCount;
    /** True if the line is a ring */
    private final boolean isClosed;

    private int currentCoord = 0;
    private boolean done = false;

    /**
     * Creates a new instance of LineIterator
     *
     * @param cu The curve the iterator will use
     * @param trs The affine transform applied to coordinates during iteration
     */
    public ISOCurveIterator(Curve cu, AffineTransform trs) {
        super(cu,trs);
        LineString ls = cu.asLineString(0, 0);
        coordinates = ls.getControlPoints();
        coordinateCount = coordinates.size();
        isClosed = cu.isCycle();
    }

    private void reset(){
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
		if (
            ((currentCoord == (coordinateCount - 1)) && !isClosed)
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
            DirectPosition pos = coordinates.get(0).getDirectPosition();
            coords[0] = pos.getOrdinate(0);
            coords[1] = pos.getOrdinate(1);
            transform.transform(coords, 0, coords, 0, 1);
            return SEG_MOVETO;
        } else if ((currentCoord == coordinateCount) && isClosed) {
            return SEG_CLOSE;
        } else {
            DirectPosition pos = coordinates.get(currentCoord).getDirectPosition();
            coords[0] = pos.getOrdinate(0);
            coords[1] = pos.getOrdinate(1);
            transform.transform(coords, 0, coords, 0, 1);            
            return SEG_LINETO;
        }
	}
	
}
