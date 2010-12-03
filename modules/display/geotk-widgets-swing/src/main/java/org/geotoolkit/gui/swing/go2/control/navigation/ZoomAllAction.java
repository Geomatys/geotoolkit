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
package org.geotoolkit.gui.swing.go2.control.navigation;

import java.awt.event.ActionEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.util.logging.Logging;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;


/**
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class ZoomAllAction extends AbstractAction {

    private static final Logger LOGGER = Logging.getLogger(ZoomAllAction.class);

    private static final ImageIcon ICON_ZOOM_ALL_16 = IconBundle.getInstance().getIcon("16_zoom_all");
    private static final ImageIcon ICON_ZOOM_ALL_24 = IconBundle.getInstance().getIcon("24_zoom_all");

    public ZoomAllAction() {
        this(false);
    }

    public ZoomAllAction(boolean big) {
        super("",(big)? ICON_ZOOM_ALL_24 : ICON_ZOOM_ALL_16);
        putValue(SHORT_DESCRIPTION, MessageBundle.getString("map_zoom_all"));
    }

    private JMap2D map = null;

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (map != null) {
            Envelope rect = map.getCanvas().getContainer().getGraphicsEnvelope();
            try {
                map.getCanvas().getController().setVisibleArea(rect);
            } catch (TransformException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            } catch (NoninvertibleTransformException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            } 
        }
    }

    public JMap2D getMap() {
        return map;
    }

    public void setMap(JMap2D map) {
        this.map = map;
        setEnabled(map != null);
    }
}
