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
package org.geotoolkit.gui.swing.contexttree.popup;

import java.awt.Component;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.tree.TreePath;

/**
 * Default popup control separator, use for JContextTreePopup
 * 
 * @author Johann Sorel
 * 
 */
public class SeparatorItem extends JSeparator implements TreePopupItem{
       
    
    /** 
     * Creates a new instance of separator
     */
    public SeparatorItem() {
        super();
        setOrientation(SwingConstants.HORIZONTAL);
    }
    
    public boolean isValid(TreePath[] selection) {
        return true;
    }

    public Component getComponent(TreePath[] selection) {
        return this;
    }
    
}
