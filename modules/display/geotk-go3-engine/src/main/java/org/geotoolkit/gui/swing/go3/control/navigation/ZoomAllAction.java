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
package org.geotoolkit.gui.swing.go3.control.navigation;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import org.geotoolkit.display3d.canvas.A3DCanvas;
import org.geotoolkit.map.MapContext;


/**
 * @author Johann Sorel (Puzzle-GIS)
 */
public class ZoomAllAction extends AbstractAction {

//    private static final ImageIcon ICON_ZOOM_ALL = IconBundle.getInstance().getIcon("16_zoom_all");

    public ZoomAllAction() {
        super("ZA");
    }

    private A3DCanvas map = null;

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (map != null) {
            MapContext context = map.getContainer2().getContext();
            if(context == null) return;
            map.getController().setCameraPosition(0, 20, 0);
        }
    }

    public A3DCanvas getMap() {
        return map;
    }

    public void setMap(A3DCanvas map) {
        this.map = map;
        setEnabled(map != null);
    }
}
