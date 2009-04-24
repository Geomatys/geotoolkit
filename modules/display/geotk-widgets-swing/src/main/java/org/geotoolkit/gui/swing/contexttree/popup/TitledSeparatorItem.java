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

import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTitledSeparator;

/**
 * popup control separator, use for JContextTreePopup
 * 
 * @author Johann Sorel
 * 
 */
public abstract class TitledSeparatorItem extends JXTitledSeparator implements TreePopupItem{
       
    public static final int LEFT = SwingConstants.LEFT;
    public static final int RIGHT = SwingConstants.RIGHT;
    public static final int CENTER = SwingConstants.CENTER;
        
    /** 
     * Creates a new instance of separator
     */
    public TitledSeparatorItem() {
        super();
    }
    
    /** 
     * Creates a new instance of separator
     * @param title
     */
    public TitledSeparatorItem(String title) {
        super(title);
    }
    
    /** 
     * Creates a new instance of separator
     * @param title
     * @param position of the texte, SwingConstants
     */
    public TitledSeparatorItem(String title,int position) {
        super(title,position);
    }
    
    /** 
     * Creates a new instance of separator
     * @param title
     * @param position of the texte, SwingConstants
     * @param img icon
     */
    public TitledSeparatorItem(String title,int position,Icon img) {
        super(title,position,img);
    }
    
    public Component getComponent(TreePath[] selection) {
        return this;
    }
    
    public boolean isValid(TreePath[] selection){
        return true;
    }
    
    
}
