/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2004-2008, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.gui.swing.go;

import org.geotoolkit.display2d.canvas.J2DCanvas;

import org.geotools.gui.swing.map.map2d.Map2D;

/**
 *
 * @author Johann Sorel
 */
public interface GoMap2D extends Map2D{
    /**
     * The name of the {@linkplain PropertyChangeEvent property change event} fired when the
     * {@linkplain AWTCanvas2D#getHandler canvas handler} changed.
     */
    public static final String HANDLER_PROPERTY = "handler";
    
    public CanvasHandler getHandler();

    public void setHandler(CanvasHandler handler);

    @Override
    public J2DCanvas getCanvas();

}
