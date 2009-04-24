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
package org.geotoolkit.gui.swing.contexttree;

import java.util.EventListener;

/**
 * Listener for ContextTreeModel
 * 
 * @author Johann Sorel
 */
public interface TreeContextListener extends EventListener{
        
    /**
     * When a Context is added
     * 
     * @param event the event
     */
    public void contextAdded(TreeContextEvent event) ;
      
    /**
     * When a Context is removed
     * 
     * @param event the event
     */
    public void contextRemoved(TreeContextEvent event);
      
    /**
     * When a Context is activated
     * 
     * @param event the event
     */
    public void contextActivated(TreeContextEvent event);
      
    /**
     * When a Context moved
     * 
     * @param event the event
     */
    public void contextMoved(TreeContextEvent event);
        
}
