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

import com.vividsolutions.jts.geom.Point;
import java.awt.geom.AffineTransform;

/**
 * Simple and efficient path iterator for JTS Point.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @since 2.9
 */
public final class PointIterator extends GeometryIterator<Point> {
        
    private boolean done = false;
    
    /**
     * Creates a new PointIterator object.
     *
     * @param point The point
     * @param at The affine transform applied to coordinates during iteration
     */
    public PointIterator(Point point,AffineTransform trs) {
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
        coords[0] = geometry.getX();
        coords[1] = geometry.getY();
        transform.transform(coords, 0, coords, 0, 1);
        return SEG_MOVETO;
    }

}
