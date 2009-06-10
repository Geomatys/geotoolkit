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
import org.geotoolkit.display2d.style.labeling.LabelDescriptor;
import org.geotoolkit.display2d.style.labeling.LinearLabelDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class LinearCandidate implements Candidate {

    private final LinearLabelDescriptor desc;
    private final Shape shape;

    public LinearCandidate(LinearLabelDescriptor desc, Shape shape) {
        this.desc = desc;
        this.shape = shape;
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
