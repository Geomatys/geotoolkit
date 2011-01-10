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
package org.geotoolkit.gui.swing.contexttree.menu;

import java.awt.Component;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.tree.TreePath;

import org.geotoolkit.gui.swing.contexttree.JContextTree;
import org.geotoolkit.gui.swing.contexttree.TreePopupItem;
import org.geotoolkit.gui.swing.go2.JMap2D;

/**
 * Default popup control separator, use for JContextTreePopup
 * 
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class SeparatorItem extends JSeparator implements TreePopupItem{

    private JContextTree tree = null;
    private JMap2D map = null;

    /** 
     * Creates a new instance of separator
     */
    public SeparatorItem() {
        super();
        setOrientation(SwingConstants.HORIZONTAL);
    }
    
    @Override
    public boolean isValid(final TreePath[] selection) {
        return true;
    }

    @Override
    public Component getComponent(final TreePath[] selection) {
        return this;
    }

    @Override
    public void setTree(final JContextTree tree) {
        this.tree = tree;
    }

    @Override
    public JContextTree getTree() {
        return tree;
    }

    @Override
    public void setMapView(final JMap2D map) {
        this.map = map;
    }

    @Override
    public JMap2D getMapView() {
        return map;
    }
    
}
