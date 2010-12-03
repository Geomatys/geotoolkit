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

import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.go2.control.information.InformationAction;
import org.geotoolkit.gui.swing.go2.control.information.MesureAreaAction;
import org.geotoolkit.gui.swing.go2.control.information.MesureLenghtAction;

/**
 * Information bar
 *
 * @author johann sorel (Puzzle-GIS)
 * @module pending
 */
public class JInformationBar extends JToolBar implements MapControlBar{

    private final MesureLenghtAction actionLenght;
    private final MesureAreaAction actionArea;
    private final InformationAction actionInfo;
    private JMap2D map = null;

    public JInformationBar() {
        this(null);
    }

    /**
     * Creates a new instance of JMap2DControlBar
     * @param pane : related Map2D or null
     */
    public JInformationBar(JMap2D pane) {
        this(pane,false);
    }

    public JInformationBar(JMap2D pane,boolean bigIcons) {

        actionLenght = new MesureLenghtAction(bigIcons);
        actionArea = new MesureAreaAction(bigIcons);
        actionInfo = new InformationAction(bigIcons);

        add(actionLenght);
        add(actionArea);
        add(actionInfo);
        setMap(pane);
    }

    /**
     * set the related Map2D
     * @param map2d : related Map2D
     */
    public void setMap(JMap2D map2d) {
        map = map2d;
        actionLenght.setMap(map);
        actionArea.setMap(map);
        actionInfo.setMap(map);
    }

    @Override
    public JMap2D getMap() {
        return map;
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
