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
package org.geotoolkit.gui.swing.go2.decoration;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.geotoolkit.gui.swing.go2.Map2D;

/**
 * Color Decoration
 * 
 * @author Johann Sorel
 * @module pending
 */
public class ColorDecoration extends JPanel implements MapDecoration{

    public ColorDecoration(){
        super();
        setOpaque(true);
        setBackground(Color.WHITE);
    }
    
    @Override
    public void refresh() {
        revalidate();
        repaint();
    }

    @Override
    public JComponent geComponent() {
        return this;
    }

    @Override
    public void setMap2D(Map2D map) {
        
    }

    @Override
    public Map2D getMap2D() {
        return null;
    }

    @Override
    public void dispose() {
    }

}
