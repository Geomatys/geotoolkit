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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.render2d.FXCanvasHandler;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.FXMapAction;
import org.geotoolkit.internal.GeotkFXBundle;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FXZoomOutAction extends FXMapAction {
    
    public static final Image ICON = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_SEARCH_MINUS, 16, FontAwesomeIcons.DEFAULT_COLOR), null);
    
    public FXZoomOutAction(FXMap map) {
        super(map,GeotkFXBundle.getString(FXZoomOutAction.class,"zoom_out"),GeotkFXBundle.getString(FXZoomOutAction.class,"zoom_out"),ICON);
        
        map.getHandlerProperty().addListener(new ChangeListener<FXCanvasHandler>() {
            @Override
            public void changed(ObservableValue<? extends FXCanvasHandler> observable, FXCanvasHandler oldValue, FXCanvasHandler newValue) {
                selectedProperty.set(newValue instanceof FXZoomOutHandler);
            }
        });
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void handle(ActionEvent event) {
        if (map != null ) {
            map.setHandler(new FXZoomOutHandler(map));
        }
    }

}
