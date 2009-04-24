/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.maptree;

import java.awt.Component;

import javax.swing.tree.TreePath;


/**
 * Interface used to build a Popup control for JContextTree
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public interface TreePopupItem {
        
    /**
     * return true if the control should by shown
     * @param selection 
     * @return 
     */
    public boolean isValid(TreePath[] selection);
    
    /**
     * return the component to by shown
     * @param selection 
     * @return 
     */
    public Component getComponent(TreePath[] selection);
    
}
