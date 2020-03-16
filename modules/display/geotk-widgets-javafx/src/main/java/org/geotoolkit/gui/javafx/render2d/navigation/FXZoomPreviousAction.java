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

import java.awt.geom.AffineTransform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import org.controlsfx.glyphfont.FontAwesome;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.render2d.FXMap;
import org.geotoolkit.gui.javafx.render2d.FXMapAction;
import org.geotoolkit.internal.GeotkFX;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FXZoomPreviousAction extends FXMapAction {
    public static final Image ICON = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_CHEVRON_LEFT, 16, FontAwesomeIcons.DEFAULT_COLOR), null);

    public FXZoomPreviousAction(FXMap map) {
        super(map,GeotkFX.getString(FXZoomPreviousAction.class,"zoom_previous"),GeotkFX.getString(FXZoomPreviousAction.class,"zoom_previous"),GeotkFX.createGlyph(FontAwesome.Glyph.CHEVRON_LEFT));
    }

    @Override
    public void setMap(FXMap map) {
        super.setMap(map);
        disabledProperty().unbind();
        disabledProperty().bind(map.getNextPreviousList().previousProperty().isNull());
    }

    @Override
    public void accept(ActionEvent event) {
        if (map != null) {
            final AffineTransform env = map.getNextPreviousList().previousProperty().get();
            map.getCanvas().setCenterTransform(env);
            map.getNextPreviousList().previous();
        }
    }

}
