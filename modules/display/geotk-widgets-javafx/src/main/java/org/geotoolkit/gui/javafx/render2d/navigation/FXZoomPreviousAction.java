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

import org.geotoolkit.gui.javafx.render2d.FXMap;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FXZoomPreviousAction extends FXMapAction {
    public static final Image ICON = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_CHEVRON_LEFT, 16, FontAwesomeIcons.DEFAULT_COLOR), null);
    
    public FXZoomPreviousAction(FXMap map) {
        super(map,MessageBundle.getString("map_zoom_previous"),MessageBundle.getString("map_zoom_previous"),ICON);
        
        map.getCanvas().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                
            }
        });
        
    }
    
    @Override
    public void handle(ActionEvent event) {
        if (map != null) {
            map.setHandler(new FXZoomInHandler(map));
        }
    }
    
}
