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
package org.geotoolkit.gui.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * Specific Layout used for JLayeredPane, all added componant take
 * the complete size of the parent
 * 
 * @author Johann Sorel
 * @module pending
 */
public class BufferLayout implements LayoutManager{
    
    public BufferLayout() {}

    @Override
    public void addLayoutComponent(final String name, final Component comp) {}

    @Override
    public void removeLayoutComponent(final Component comp) {}
    
    @Override
    public Dimension preferredLayoutSize(final Container parent) {
        return new Dimension(0, 0);
    }

    @Override
    public Dimension minimumLayoutSize(final Container parent) {
        return new Dimension(0, 0);
    }
    
    @Override
    public void layoutContainer(final Container parent) {
        int maxWidth = parent.getWidth();
        int maxHeight = parent.getHeight();
        int nComps = parent.getComponentCount();    
        
        for (int i = 0 ; i < nComps ; i++) {
            Component c = parent.getComponent(i);                
            c.setBounds(0, 0, maxWidth,maxHeight);
        }
    }
    
}
