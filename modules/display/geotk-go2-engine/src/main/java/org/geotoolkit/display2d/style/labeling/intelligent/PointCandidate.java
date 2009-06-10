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

package org.geotoolkit.display2d.style.labeling.intelligent;

import java.awt.Shape;
import java.awt.geom.Point2D;
import org.geotoolkit.display2d.style.labeling.LabelDescriptor;
import org.geotoolkit.display2d.style.labeling.PointLabelDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class PointCandidate implements Candidate {

    private final PointLabelDescriptor desc;
    private final Shape shape;
    private final Point2D point;
    private final float height;
    private final float width;

    public PointCandidate(PointLabelDescriptor desc, Shape shape, Point2D point,float height, float width) {
        this.desc = desc;
        this.shape = shape;
        this.point = point;
        this.height = height;
        this.width = width;
    }

    public float getAlpha() {
        return desc.getRotation();
    }

    public float getHeight() {
        return height;
    }

    public Point2D getPoint() {
        return point;
    }

    public float getWidth() {
        return width;
    }

    @Override
    public LabelDescriptor getDescriptor() {
        return desc;
    }

    @Override
    public Shape getShape() {
        return shape;
    }
    
    @Override
    public boolean intersect(Candidate c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
