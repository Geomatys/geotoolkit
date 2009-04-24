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
package org.geotoolkit.gui.swing.map.map2d;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * Specific Layout used for JLayeredPane, all added componant take
 * the complete size of the parent
 * 
 * @author Johann Sorel
 */
public class BufferLayout implements LayoutManager{

    private Dimension dim = new Dimension(0, 0);
    
    public BufferLayout() {
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public void removeLayoutComponent(Component comp) {
    }
    
    public Dimension preferredLayoutSize(Container parent) {
        return dim;
    }

    public Dimension minimumLayoutSize(Container parent) {
        return dim;
    }
    
    public void layoutContainer(Container parent) {
        int maxWidth = parent.getWidth();
        int maxHeight = parent.getHeight();
        int nComps = parent.getComponentCount();    
        
        for (int i = 0 ; i < nComps ; i++) {
            Component c = parent.getComponent(i);                
            c.setBounds(0, 0, maxWidth,maxHeight);
        }
    }
    
    
}
