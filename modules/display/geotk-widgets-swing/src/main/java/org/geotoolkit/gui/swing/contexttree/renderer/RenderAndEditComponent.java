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
package org.geotoolkit.gui.swing.contexttree.renderer;

import javax.swing.border.EmptyBorder;

/**
 * Abstrat class extending JPanel, can be used for Render and Edition Cell
 * in the JContextTree
 * 
 * @author Johann Sorel
 */
public abstract class RenderAndEditComponent extends javax.swing.JPanel {

    /**
     * Abstrat class extending JPanel, can be used for Render and Edition Cell
     * in the JContextTree
     */
    public RenderAndEditComponent() {
        super();
        init();
    }   

    private void init(){
        setBorder(new EmptyBorder(1, 1, 1, 1));        
    }
    
    /**
     * initialize the component with the target object
     * 
     * @param obj 
     */
    public abstract void parse(Object obj);

    /**
     * 
     * @return new value when edition stop
     */
    public abstract Object getValue();
}
