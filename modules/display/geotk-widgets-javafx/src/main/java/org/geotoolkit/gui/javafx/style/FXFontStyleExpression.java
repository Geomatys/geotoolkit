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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXFontStyleExpression extends FXExpression {


    private static final Image IMG_NORMAL;
    private static final Image IMG_ITALIC;
    static {
        IMG_NORMAL = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_TIMES,16,new Color(0, 0, 0, 0)),null);
        IMG_ITALIC = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_ITALIC,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    }

    private final ToggleGroup group = new ToggleGroup();
    private final ToggleButton uiNormal = new ToggleButton(null, new ImageView(IMG_NORMAL));
    private final ToggleButton uiItalic = new ToggleButton(null, new ImageView(IMG_ITALIC));
    private final HBox hbox = new HBox(uiNormal,uiItalic);

    public FXFontStyleExpression(){
        uiNormal.setToggleGroup(group);
        uiItalic.setToggleGroup(group);
        uiNormal.setContentDisplay(ContentDisplay.CENTER);
        uiItalic.setContentDisplay(ContentDisplay.CENTER);

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue==uiNormal){
                    value.set(StyleConstants.FONT_STYLE_NORMAL);
                }else if(newValue==uiItalic){
                    value.set(StyleConstants.FONT_STYLE_ITALIC);
                }
            }
        });
    }

    @Override
    public Expression newValue() {
        return StyleConstants.FONT_STYLE_NORMAL;
    }

    @Override
    protected Node getEditor() {
        return hbox;
    }

    @Override
    protected boolean canHandle(Expression styleElement) {

        final Toggle selected = group.getSelectedToggle();
        if(StyleConstants.FONT_STYLE_NORMAL.equals(styleElement)){
            if(selected!=uiNormal) group.selectToggle(uiNormal);
            return true;
        }else if(StyleConstants.FONT_STYLE_ITALIC.equals(styleElement)){
            if(selected!=uiItalic) group.selectToggle(uiItalic);
            return true;
        }

        return false;
    }

}
