/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.gui.javafx.render2d.navigation;

import java.awt.geom.NoninvertibleTransformException;
import java.util.logging.Level;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import org.geotoolkit.display.container.GraphicContainer;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.FXMapAction;
import org.geotoolkit.internal.GeotkFXBundle;
import org.geotoolkit.internal.Loggers;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FXRepaintAction extends FXMapAction {
    public static final Image ICON = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_REFRESH, 16, FontAwesomeIcons.DEFAULT_COLOR), null);
    
    public FXRepaintAction(FXMap map) {
        super(map,GeotkFXBundle.getString(FXRepaintAction.class,"refresh"),GeotkFXBundle.getString(FXRepaintAction.class,"refresh"),ICON);
    }

    @Override
    public void handle(ActionEvent event) {
        if (map != null) {
            final GraphicContainer container = map.getCanvas().getContainer();
            if (container instanceof ContextContainer2D) {
                final Envelope rect = ((ContextContainer2D) container).getGraphicsEnvelope();
                try {
                    map.getCanvas().setVisibleArea(rect);
                } catch (TransformException | IllegalArgumentException | NoninvertibleTransformException ex) {
                    Loggers.JAVAFX.log(Level.WARNING, null, ex);
                }
            }
        }
    }
    
}
