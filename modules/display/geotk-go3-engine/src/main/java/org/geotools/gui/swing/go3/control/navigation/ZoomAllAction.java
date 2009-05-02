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
package org.geotools.gui.swing.go3.control.navigation;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.geotools.display3d.canvas.A3DCanvas;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.map.MapContext;
import org.opengis.geometry.Envelope;


/**
 * @author Johann Sorel (Puzzle-GIS)
 */
public class ZoomAllAction extends AbstractAction {

    private static final ImageIcon ICON_ZOOM_ALL = IconBundle.getInstance().getIcon("16_zoom_all");

    public ZoomAllAction() {
        super("",ICON_ZOOM_ALL);
    }

    private A3DCanvas map = null;

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (map != null) {
            MapContext context = map.getContainer2().getContext();
            if(context == null) return;

            Envelope env;
            try {
                env = context.getBounds();
                map.getController().setCameraPosition(env.getMedian(0),env.getMedian(1),5);
            } catch (IOException ex) {
                Logger.getLogger(ZoomAllAction.class.getName()).log(Level.SEVERE, null, ex);
            }

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
