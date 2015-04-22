/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.style;

import java.awt.Color;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.internal.GeotkFX;

/**
 * Button used to switch between simple and advanced mode in the styl editor panels.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXMode extends ToggleButton {

    private static final Color BLUE = new Color(100, 180, 255);
    public static final Image ICON_SIMPLE    = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_TINT,16,BLUE),null);
    public static final Image ICON_ADVANCED  = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_COGS,16,BLUE),null);

    public FXMode() {
        this.selectedProperty().bindBidirectional(FXStyleElementController.ADVANCED_MODE);
        getStylesheets().add(GeotkFX.CSS_PATH);
        setId("corner");
        setAlignment(Pos.TOP_RIGHT);
        setTextAlignment(TextAlignment.RIGHT);
        selectedProperty().addListener(this::changed);
        changed(null, Boolean.TRUE, Boolean.TRUE);
    }

    private void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue){
        if(isSelected()){
            setText(GeotkFX.getString(this, "advanced"));
            setGraphic(new ImageView(ICON_ADVANCED));
        }else{
            setText(GeotkFX.getString(this, "simple"));
            setGraphic(new ImageView(ICON_SIMPLE));
        }
    }

}
