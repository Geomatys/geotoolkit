/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.go.control.selection;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import javax.swing.ImageIcon;
import org.geotoolkit.gui.swing.go.GoMap2D;
import org.geotoolkit.gui.swing.resource.IconBundle;

/**
 * Selection action
 * 
 * @author Johann Sorel (Puzzle-GIS)
 */
public class SelectAction extends AbstractAction {

    private static final ImageIcon ICON_SELECT = IconBundle.getInstance().getIcon("16_select");
    private final SelectFilterChooser guiFilter;
    private final SelectHandlerChooser guiHandler;

    private GoMap2D map = null;

    public SelectAction(SelectFilterChooser gui_filter, SelectHandlerChooser gui_handler) {
        super("",ICON_SELECT);
        this.guiFilter = gui_filter;
        this.guiHandler = gui_handler;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (map != null) {

//            ((SelectableMap2D) map).setActionState(ACTION_STATE.SELECT);
        }
    }

    public GoMap2D getMap() {
        return map;
    }

    public void setMap(GoMap2D map) {
        this.map = map;
        setEnabled(map != null && map instanceof GoMap2D);
    }
}
