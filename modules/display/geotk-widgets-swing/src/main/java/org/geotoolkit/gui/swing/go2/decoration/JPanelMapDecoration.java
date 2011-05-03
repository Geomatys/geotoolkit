/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

import javax.swing.JComponent;
import javax.swing.JPanel;
import org.geotoolkit.gui.swing.go2.JMap2D;

/**
 * A translucent empty JPanel implementing MapDecoration.
 * This class can be used as a base decoration.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JPanelMapDecoration extends JPanel implements MapDecoration{

    protected JMap2D map = null; 
    
    public JPanelMapDecoration(){
        setOpaque(false);
    }
    
    @Override
    public void refresh() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void setMap2D(JMap2D map) {
        this.map = map;
    }

    @Override
    public JMap2D getMap2D() {
        return map;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
    
}
