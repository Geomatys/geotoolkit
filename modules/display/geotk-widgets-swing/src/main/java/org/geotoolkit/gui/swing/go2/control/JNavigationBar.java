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

import java.awt.Component;
import javax.swing.JToolBar;

import org.geotoolkit.gui.swing.go2.Map2D;
import org.geotoolkit.gui.swing.go2.control.navigation.ZoomAllAction;
import org.geotoolkit.gui.swing.go2.control.navigation.ZoomInAction;
import org.geotoolkit.gui.swing.go2.control.navigation.PanAction;
import org.geotoolkit.gui.swing.go2.control.navigation.ZoomOutAction;
import org.geotoolkit.gui.swing.go2.control.navigation.RefreshAction;

/**
 * JMap2DControlBar is a JPanel to handle Navigation state for a NavigableMap2D
 * ZoomIn/Out, pan, selection, refresh ...
 *
 * @author johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JNavigationBar extends JToolBar implements MapControlBar{

    private final ZoomAllAction actionZoomAll;
    private final ZoomInAction actionZoomIn;
    private final ZoomOutAction actionZoomOut;
    private final PanAction actionZoomPan;
    private final RefreshAction actionRefresh;

    private Map2D map = null;

    /**
     * Creates a new instance of JMap2DControlBar
     */
    public JNavigationBar() {
        this(null);
    }

    /**
     * Creates a new instance of JMap2DControlBar
     * @param pane : related Map2D or null
     */
    public JNavigationBar(Map2D pane) {
        this(pane,false);
    }

    public JNavigationBar(Map2D pane, boolean bigIcons) {
        actionZoomAll = new ZoomAllAction(bigIcons);
        actionZoomIn = new ZoomInAction(bigIcons);
        actionZoomOut = new ZoomOutAction(bigIcons);
        actionZoomPan = new PanAction(bigIcons);
        actionRefresh = new RefreshAction(bigIcons);

        add(actionZoomAll);
        add(actionRefresh);
        add(actionZoomIn);
        add(actionZoomOut);
        add(actionZoomPan);
        setMap(pane);
    }

    /**
     * set the related Map2D
     * @param map2d : related Map2D
     */
    public void setMap(Map2D map2d) {
        map = map2d;
        actionRefresh.setMap(map);
        actionZoomAll.setMap(map);
        actionZoomIn.setMap(map);
        actionZoomOut.setMap(map);
        actionZoomPan.setMap(map);
    }

    @Override
    public Map2D getMap() {
        return map;
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
