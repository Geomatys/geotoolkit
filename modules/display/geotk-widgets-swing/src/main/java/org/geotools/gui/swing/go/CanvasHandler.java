/*
 *    GeoTools - An Open Source Java GIS Tookit
 *    http://geotools.org
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gui.swing.go;

import java.awt.Component;

import org.geotoolkit.display.canvas.ReferencedCanvas2D;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface CanvasHandler {
    
    ReferencedCanvas2D getCanvas();    
    
    void install(Component component);
    
    void uninstall(Component component);
        
}
