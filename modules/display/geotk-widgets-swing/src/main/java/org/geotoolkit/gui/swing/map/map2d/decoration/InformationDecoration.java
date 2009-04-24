/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.map.map2d.decoration;

/**
 * Information decoration
 * 
 * @author Johann Sorel
 */
public interface InformationDecoration extends MapDecoration{

    public static enum LEVEL{
        NORMAL,
        INFO,
        WARNING,
        ERROR
    }
    
    public static int DEFAULT_TIME = 10000;
    
    public void setPaintingIconVisible(boolean b);
    
    public boolean isPaintingIconVisible();
    
    public void displayLowLevelMessages(boolean display);
    
    public boolean isDisplayingLowLevelMessages();
    
    /**
     * 
     * @param text
     * @param time : time cant be inferior to 3000 (3seconds)
     * @param level
     */
    public void displayMessage(String text, int time, LEVEL level);
    
    
}
