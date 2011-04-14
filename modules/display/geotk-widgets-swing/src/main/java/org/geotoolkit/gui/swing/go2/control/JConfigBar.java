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
package org.geotoolkit.gui.swing.go2.control;

import org.geotoolkit.gui.swing.go2.JMap2D;

/**
 * JMap2DControlBar is a JPanel to handle Navigation decoration and debugging panel
 *
 * @author johann sorel
 * @module pending
 */
public class JConfigBar extends AbstractMapControlBar {

    private final ConfigAction ACTION_CONFIG = new ConfigAction();
    private final DebugAction ACTION_DEBUG = new DebugAction();

    /**
     * Creates a new instance of JMap2DControlBar
     */
    public JConfigBar() {
        this(null);
    }

    /**
     * Creates a new instance of JMap2DControlBar
     * @param pane : related Map2D or null
     */
    public JConfigBar(final JMap2D pane) {
        add(ACTION_CONFIG);
        add(ACTION_DEBUG);
        setMap(pane);
    }

    /**
     * set the related Map2D
     * @param map2d : related Map2D
     */
    @Override
    public void setMap(final JMap2D map2d) {
        super.setMap(map2d);
        ACTION_CONFIG.setMap(map);
        ACTION_DEBUG.setMap(map);
    }
}
