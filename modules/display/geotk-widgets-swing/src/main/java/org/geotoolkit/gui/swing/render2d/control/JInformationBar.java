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

import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.control.information.InformationAction;
import org.geotoolkit.gui.swing.render2d.control.information.MeasureAreaAction;
import org.geotoolkit.gui.swing.render2d.control.information.MeasureLenghtAction;

/**
 * Information bar
 *
 * @author johann sorel (Puzzle-GIS)
 * @module
 */
public class JInformationBar extends AbstractMapControlBar{

    private final MeasureLenghtAction actionLenght;
    private final MeasureAreaAction actionArea;
    private final InformationAction actionInfo;

    public JInformationBar() {
        this(null);
    }

    /**
     * Creates a new instance of JMap2DControlBar
     * @param pane : related Map2D or null
     */
    public JInformationBar(final JMap2D pane) {

        actionLenght = new MeasureLenghtAction();
        actionArea = new MeasureAreaAction();
        actionInfo = new InformationAction();

        add(actionLenght);
        add(actionArea);
        add(actionInfo);
        setMap(pane);
    }

    /**
     * set the related Map2D
     * @param map2d : related Map2D
     */
    @Override
    public void setMap(final JMap2D map2d) {
        super.setMap(map2d);
        actionLenght.setMap(map);
        actionArea.setMap(map);
        actionInfo.setMap(map);
    }

}
