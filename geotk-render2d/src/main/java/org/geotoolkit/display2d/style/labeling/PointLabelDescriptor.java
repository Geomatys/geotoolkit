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
package org.geotoolkit.display2d.style.labeling;

/**
 * Point Label descriptor.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface PointLabelDescriptor extends LabelDescriptor {

    /**
     * @return anchor X value. Within the label.
     */
    float getAnchorX();

    /**
     * @return anchor Y value. Within the label.
     */
    float getAnchorY();

    /**
     * @return displacement X value. in Pixel unit.
     */
    float getDisplacementX();

    /**
     * @return displacement Y value. in Pixel unit.
     */
    float getDisplacementY();

    /**
     * @return rotation. in degree.
     */
    float getRotation();

}
