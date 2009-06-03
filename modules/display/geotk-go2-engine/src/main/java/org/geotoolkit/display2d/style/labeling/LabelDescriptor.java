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
package org.geotoolkit.display2d.style.labeling;

import java.awt.Font;
import java.awt.Paint;

/**
 * A labelDescriptor contains all informations relative to a String to paint
 * in the rendering context, like size,color, halo, ...
 * 
 * @author Johann Sorel (Geomatys)
 */
public interface LabelDescriptor {

    /**
     * @return Message text to display, can not be null
     */
    String getText();
    
    /**
     * @return Font to use, can not be null
     */
    Font getTextFont();
    
    /**
     * @return Paint to use for the text, can not be null
     */
    Paint getTextPaint();
    
    /**
     * @return width of the halo, can not be null
     */
    float getHaloWidth();
    
    /**
     * @return Paint for the halo, can not be null
     */
    Paint getHaloPaint();
    
}
