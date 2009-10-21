/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.go3.control;

import javax.swing.JToolBar;

import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.gui.swing.go3.control.navigation.CameraSpeedSlider;
import org.geotoolkit.gui.swing.go3.control.navigation.SceneScalingSlider;
import org.geotoolkit.gui.swing.go3.control.navigation.ZoomAllAction;

/**
 * JMap2DControlBar is a JPanel to handle Navigation state for an Ardor3D map
 * ZoomIn/Out, pan, selection, refresh ...
 *
 * @author johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JNavigationBar extends JToolBar {

    private final ZoomAllAction actionZoomAll = new ZoomAllAction();
    private final CameraSpeedSlider cameraSpeed = new CameraSpeedSlider();
    private final SceneScalingSlider scaling = new SceneScalingSlider();

    private A3DCanvas map = null;

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
    public JNavigationBar(A3DCanvas pane) {
        add(actionZoomAll);
        add(cameraSpeed);
        add(scaling);
        setMap(pane);
    }

    /**
     * set the related Map2D
     * @param map2d : related Map2D
     */
    public void setMap(A3DCanvas map2d) {
        map = map2d;
        actionZoomAll.setMap(map);
        cameraSpeed.setMap(map);
        scaling.setMap(map);
    }
}
