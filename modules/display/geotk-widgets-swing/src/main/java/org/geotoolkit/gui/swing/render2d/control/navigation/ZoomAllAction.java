/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.gui.swing.render2d.control.navigation;

import java.awt.event.ActionEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import org.geotoolkit.display.container.GraphicContainer;
import org.geotoolkit.display2d.container.ContextContainer2D;

import org.geotoolkit.gui.swing.render2d.control.AbstractMapAction;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;


/**
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class ZoomAllAction extends AbstractMapAction {

    private static final ImageIcon ICON = IconBuilder.createIcon(FontAwesomeIcons.ICON_GLOBE, 16, FontAwesomeIcons.DEFAULT_COLOR);

    public ZoomAllAction() {
        putValue(SMALL_ICON, ICON);
        putValue(SHORT_DESCRIPTION, MessageBundle.format("map_zoom_all"));
    }

    @Override
    public void actionPerformed(final ActionEvent arg0) {
        if (map != null) {
            final GraphicContainer container = map.getCanvas().getContainer();
            if(container instanceof ContextContainer2D){
                final Envelope rect = ((ContextContainer2D)container).getGraphicsEnvelope();
                try {
                    map.getCanvas().setVisibleArea(rect);
                } catch (TransformException | IllegalArgumentException | NoninvertibleTransformException ex) {
                    getLogger().log(Level.WARNING, null, ex);
                }
            }
        }
    }

}
