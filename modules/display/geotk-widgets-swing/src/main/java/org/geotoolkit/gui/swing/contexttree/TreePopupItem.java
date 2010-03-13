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
package org.geotoolkit.gui.swing.contexttree;

import java.awt.Component;

import javax.swing.tree.TreePath;
import org.geotoolkit.gui.swing.go2.Map2D;


/**
 * Interface used to build a Popup control for JContextTree
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public interface TreePopupItem {

    /**
     * An item might need to know on which map he can work. like for exemple
     * the Zoom to layer action.
     * @param map
     */
    void setMapView(Map2D map);

    Map2D getMapView();

    void setTree(JContextTree tree);

    JContextTree getTree();

    /**
     * return true if the control should by shown
     * @param selection 
     * @return 
     */
    boolean isValid(TreePath[] selection);
    
    /**
     * return the component to by shown
     * @param selection 
     * @return 
     */
    Component getComponent(TreePath[] selection);
    
}
