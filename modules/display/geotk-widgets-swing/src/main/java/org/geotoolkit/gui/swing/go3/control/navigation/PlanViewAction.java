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


/**
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class PlanViewAction extends AbstractAction {

    public PlanViewAction() {
        super("PV");
        putValue(SMALL_ICON, IconBundle.getIcon("16_3d_planview"));
    }

    private A3DCanvas canvas = null;

    @Override
    public void actionPerformed(final ActionEvent arg0) {
        if (canvas != null) {
            canvas.setPlanView(!canvas.isPlanView());
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
