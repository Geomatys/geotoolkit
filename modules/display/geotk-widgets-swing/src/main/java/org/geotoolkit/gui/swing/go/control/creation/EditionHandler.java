/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.go.control.creation;

import javax.swing.ImageIcon;
import org.geotoolkit.gui.swing.go.GoMap2D;

/**
 * Edition handler 
 * @author Johann Sorel
 */
public interface EditionHandler {

    /**
     * 
     * @param map2d
     */
    void install(GoMap2D map2d);

    void installListeners(GoMap2D map2d);

    /**
     * 
     */
    void uninstall();

    void uninstallListeners();

    /**
     * 
     * @return
     */
    boolean isInstalled();
    
    void cancelEdition();
    
    String getTitle();

    ImageIcon getIcon();
}
