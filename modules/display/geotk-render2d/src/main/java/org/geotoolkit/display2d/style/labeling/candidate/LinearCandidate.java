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

import java.awt.Shape;
import org.geotoolkit.display2d.style.labeling.LinearLabelDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LinearCandidate extends Candidate<LinearLabelDescriptor> {

    private final Shape shape;

    public LinearCandidate(final LinearLabelDescriptor desc, final Shape shape) {
        super(desc);
        this.shape = shape;
    }

    public Shape getShape() {
        return shape;
    }
    
}
