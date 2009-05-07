/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2002-2007, GeoTools Project Managment Committee (PMC)
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
package org.geotoolkit.gui.swing.go.control;

import java.awt.Component;
import javax.swing.JToolBar;

import org.geotoolkit.gui.swing.go.GoMap2D;
import org.geotoolkit.gui.swing.go.control.navigation.ZoomAllAction;
import org.geotoolkit.gui.swing.go.control.navigation.ZoomInAction;
import org.geotoolkit.gui.swing.go.control.navigation.PanAction;
import org.geotoolkit.gui.swing.go.control.navigation.ZoomOutAction;
import org.geotoolkit.gui.swing.go.control.navigation.RefreshAction;

/**
 * JMap2DControlBar is a JPanel to handle Navigation state for a NavigableMap2D
 * ZoomIn/Out, pan, selection, refresh ...
 *
 * @author johann Sorel (Puzzle-GIS)
 */
public class JNavigationBar extends JToolBar implements MapControlBar{

    private final ZoomAllAction actionZoomAll = new ZoomAllAction();
    private final ZoomInAction actionZoomIn = new ZoomInAction();
    private final ZoomOutAction actionZoomOut = new ZoomOutAction();
    private final PanAction actionZoomPan = new PanAction();
    private final RefreshAction actionRefresh = new RefreshAction();

    private GoMap2D map = null;

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
    public JNavigationBar(GoMap2D pane) {
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
    public void setMap(GoMap2D map2d) {
        map = map2d;
        actionRefresh.setMap(map);
        actionZoomAll.setMap(map);
        actionZoomIn.setMap(map);
        actionZoomOut.setMap(map);
        actionZoomPan.setMap(map);
    }

    @Override
    public GoMap2D getMap() {
        return map;
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
