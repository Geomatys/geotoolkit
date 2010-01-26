/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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

package org.geotoolkit.display2d.style.labeling.candidate;

import java.awt.geom.Rectangle2D;
import org.geotoolkit.display2d.style.labeling.PointLabelDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class InPolygonCandidate extends Candidate<PointLabelDescriptor> {

    public final int width;
    public final int upper;
    public final int lower;
    public float x;
    public float y;

    public float correctionX = 0;
    public float correctionY = 0;


    public InPolygonCandidate(PointLabelDescriptor desc, int width, int upper, int lower, float x, float y) {
        super(desc);
        this.width = width;
        this.upper = upper;
        this.lower = lower;
        this.x = x;
        this.y = y;
    }

    public float getCorrectedX(){
        return x + correctionX;
    }

    public float getCorrectedY(){
        return y + correctionY;
    }

    public Rectangle2D getBounds(){
        return new Rectangle2D.Double(
                (int)getCorrectedX(),
                (int)getCorrectedY()-upper,
                width,
                upper+lower);
    }

}
