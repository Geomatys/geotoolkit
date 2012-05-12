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
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.map.MapContext;


/**
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class ZoomAllAction extends AbstractAction {

    public ZoomAllAction() {
        super("ZA");
        putValue(SMALL_ICON, IconBundle.getIcon("16_zoom_all"));
    }

    private A3DCanvas canvas = null;

    @Override
    public void actionPerformed(final ActionEvent arg0) {
        if (canvas != null) {
            MapContext context = canvas.getA3DContainer().getContext();
            if(context == null) return;
            //canvas.getController().setCameraPosition(0, 20, 0);
        }
    }

    public A3DCanvas getCanvas() {
        return canvas;
    }

    public void setCanvas(final A3DCanvas map) {
        this.canvas = map;
        setEnabled(map != null);
    }
}
