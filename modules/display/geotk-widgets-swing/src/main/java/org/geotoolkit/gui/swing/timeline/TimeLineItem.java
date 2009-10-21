/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.timeline;

import java.awt.Color;
import java.awt.Image;
import java.util.Date;

/**
 *
 * @author johann sorel
 * @module pending
 */
public interface TimeLineItem extends Comparable<TimeLineItem>{

    Date getDate();
    
    String getToolTip();
    
    Color getColor();
    
    Image getImage();
    
    Image getSelectedImage();
    
}
