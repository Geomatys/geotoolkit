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
import org.opengis.geometry.primitive.Point;

/**
 * Simple and efficient path iterator for ISO Point.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class ISOPointIterator extends ISOGeometryIterator<Point> {
        
    private boolean done = false;
    
    /**
     * Creates a new PointIterator object.
     *
     * @param point The point
     * @param trs The affine transform applied to coordinates during iteration
     */
    public ISOPointIterator(Point point,AffineTransform trs) {
        super(point,trs);
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
    public void next() {
        done = true;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isDone() {
        if(done){
            done = false;
            return true;
        }else{
            return done;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int currentSegment(double[] coords) {
        DirectPosition pos = geometry.getDirectPosition();
        coords[0] = pos.getOrdinate(0);
        coords[1] = pos.getOrdinate(1);
        transform.transform(coords, 0, coords, 0, 1);
        return SEG_MOVETO;
    }

}
