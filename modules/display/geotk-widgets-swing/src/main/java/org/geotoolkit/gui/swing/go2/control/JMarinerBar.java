/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
 * JMarinerBar shows S-52 Context configuration informations.
 *
 * @author johann sorel
 * @module pending
 */
public class JMarinerBar extends AbstractMapControlBar {

    private final MarinerAction ACTION_CONFIG = new MarinerAction();

    /**
     * Creates a new instance of JMarinerBar
     */
    public JMarinerBar() {
        this(null);
    }

    /**
     * Creates a new instance of JMarinerBar
     * @param pane : related Map2D or null
     */
    public JMarinerBar(final JMap2D pane) {
        add(ACTION_CONFIG);
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
    }
}
