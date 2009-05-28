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
package org.geotoolkit.gui.swing.map.map2d;

import java.awt.Component;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;
import org.geotoolkit.gui.swing.map.map2d.decoration.MapDecoration;
import org.geotoolkit.gui.swing.map.map2d.decoration.InformationDecoration;

/**
 * Map2D interface, used for mapcontext viewing
 * 
 * @author Johann Sorel
 */
public interface Map2D {
        
    /**
     * must be called when the map2d is not used anymore.
     * to avoid memoryleack if it uses thread or other resources
     */
    public void dispose();
                        
    /**
     * get the visual component 
     * @return Component
     */
    public Component getComponent();
    
    public ReferencedCanvas2D getCanvas();
        
    //----------------------map decorations-------------------------------------
    /**
     * set the top InformationDecoration of the map2d widget
     * @param info , can't be null
     */
    public void setInformationDecoration(InformationDecoration info);    
    /**
     * get the top InformationDecoration of the map2d widget
     * @return InformationDecoration
     */
    public InformationDecoration getInformationDecoration();
    /**
     * set the decoration behind the map
     * @param back : MapDecoration, can't be null
     */
    public void setBackgroundDecoration(MapDecoration back);
    /**
     * get the decoration behind the map
     * @return MapDecoration : or null if no back decoration
     */
    public MapDecoration getBackgroundDecoration();
    /**
     * add a Decoration between the map and the information top decoration
     * @param deco : MapDecoration to add
     */
    public void addDecoration(MapDecoration deco);
    /**
     * insert a MapDecoration at a specific index
     * @param index : index where to isert the decoration
     * @param deco : MapDecoration to add
     */
    public void addDecoration(int index, MapDecoration deco);
    /**
     * get the index of a MapDecoration
     * @param deco : MapDecoration to find
     * @return index of the MapDecoration
     * @throw ClassCastException or NullPointerException
     */
    public int getDecorationIndex(MapDecoration deco);
    /**
     * remove a MapDecoration
     * @param deco : MapDecoration to remove
     */
    public void removeDecoration(MapDecoration deco);
    /**
     * get an array of all MapDecoration
     * @return array of MapDecoration
     */
    public MapDecoration[] getDecorations();
        
}
