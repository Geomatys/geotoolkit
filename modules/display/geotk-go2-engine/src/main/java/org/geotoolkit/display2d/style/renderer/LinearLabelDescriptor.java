/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004 - 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.display2d.style.renderer;

import java.awt.Shape;

/**
 * Label descriptor for a text along a shape.
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface LinearLabelDescriptor extends LabelDescriptor{
    
    /**
     * Get the gap between each label to render. this shall be used only
     * when the label is repeated.
     * @return float
     */
    float getGap();
    
    /**
     * Get the initial gap, this is the space between the start of the shape
     * and the begining of the label.
     * @return float
     */
    float getInitialGap();
    
    /**
     * Get the distance between the shape and the label.
     * @return float
     */
    float getOffSet();
    
    /**
     * This parameter tell us if we must repeat the label along the line.
     * If so we shall use the gap value between each label.
     * @return boolean
     */
    boolean isRepeated();
    
    /**
     * If true the label shall fallow the shape path.
     * if not it should be drawn horizontal.
     * @return boolean
     */
    boolean isAligned();
    
    /**
     * This parameter tell us if we should simplify the geometry
     * to better render the text.
     * @return boolean
     */
    boolean isGeneralized();
    
    /**
     * @return Shape, the shape along which the label must be painted.
     * can not be null
     */
    Shape getLineplacement();
    
}
