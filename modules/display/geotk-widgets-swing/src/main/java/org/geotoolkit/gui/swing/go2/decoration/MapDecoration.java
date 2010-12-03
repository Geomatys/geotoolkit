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

import javax.swing.JComponent;
import org.geotoolkit.gui.swing.go2.JMap2D;


/**
 * MapDecoration are used to enrich a Map2D component. Thoses are added over
 * the map or there can be one under the map.
 * Decoration exemples : minimap, scalebar, navigation buttons, image in background ...
 *
 * @author Johann Sorel
 * @module pending
 */
public interface MapDecoration {

    /**
     * called by the jdefaultmap2d when the decoration should
     * reset completely
     */
    public void refresh();
    
    /**
     * must be called when the decoration is not used anymore.
     * to avoid memoryleack if it uses thread or other resources
     */
    public void dispose();
    
    /**
     * set the related map2d
     * @param map the map2D
     */
    public void setMap2D(JMap2D map);
    
    /**
     * 
     * @return Map2D, the related map2d of this decoration
     */
    public JMap2D getMap2D();
    
    /**
     * 
     * @return JComponent, the component which will be added at the map2D 
     */
    public JComponent geComponent();
    
}
