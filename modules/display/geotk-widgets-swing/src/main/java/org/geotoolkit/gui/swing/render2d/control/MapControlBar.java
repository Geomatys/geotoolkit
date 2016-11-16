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
package org.geotoolkit.gui.swing.render2d.control;

import java.awt.Component;
import org.geotoolkit.gui.swing.render2d.JMap2D;

/**
 *
 * @author eclesia
 * @module
 */
public interface MapControlBar {

    /**
     * set the related Map2D
     * @param map2d : related Map2D
     */
    void setMap(JMap2D map);

    /**
     * Get the related Map this tool bar is working on.
     * @return JMap2D or null
     */
    JMap2D getMap();

    /**
     * @return the tool bar itself
     */
    Component getComponent();

}
