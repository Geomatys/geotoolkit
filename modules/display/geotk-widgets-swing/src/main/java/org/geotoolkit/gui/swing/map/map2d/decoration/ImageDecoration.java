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

import javax.swing.JComponent;

import org.geotoolkit.gui.swing.map.map2d.Map2D;
import org.jdesktop.swingx.JXImagePanel;

/**
 * Image Decoration
 * 
 * @author Johann Sorel
 */
public class ImageDecoration extends JXImagePanel implements MapDecoration{

    
    public ImageDecoration(){}
    
    public void refresh() {
        revalidate();
        repaint();
    }

    public JComponent geComponent() {
        return this;
    }
    
    public void setMap2D(Map2D map) {
        
    }

    public Map2D getMap2D() {
        return null;
    }

    public void dispose() {
    }
}
