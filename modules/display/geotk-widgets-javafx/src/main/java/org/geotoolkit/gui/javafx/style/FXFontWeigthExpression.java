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
public class FXFontWeigthExpression extends FXExpression {


    private static final Image IMG_NORMAL;
    private static final Image IMG_BOLD;
    static {
        IMG_NORMAL = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_TIMES,16,new Color(0, 0, 0, 0)),null);
        IMG_BOLD = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_BOLD,16,FontAwesomeIcons.DEFAULT_COLOR),null);
    }

    private final ToggleGroup group = new ToggleGroup();
    private final ToggleButton uiNormal = new ToggleButton(null, new ImageView(IMG_NORMAL));
    private final ToggleButton uiBold = new ToggleButton(null, new ImageView(IMG_BOLD));
    private final HBox hbox = new HBox(uiNormal,uiBold);

    public FXFontWeigthExpression(){
        uiNormal.setToggleGroup(group);
        uiBold.setToggleGroup(group);
        uiNormal.setContentDisplay(ContentDisplay.CENTER);
        uiBold.setContentDisplay(ContentDisplay.CENTER);

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue==uiNormal){
                    value.set(StyleConstants.FONT_WEIGHT_NORMAL);
                }else if(newValue==uiBold){
                    value.set(StyleConstants.FONT_WEIGHT_BOLD);
                }
            }
        });
    }
    
    @Override
    public Expression newValue() {
        return StyleConstants.FONT_WEIGHT_NORMAL;
    }

    @Override
    protected Node getEditor() {
        return hbox;
    }

    @Override
    protected boolean canHandle(Expression styleElement) {
        
        final Toggle selected = group.getSelectedToggle();
        if(StyleConstants.FONT_WEIGHT_NORMAL.equals(styleElement)){
            if(selected!=uiNormal) group.selectToggle(uiNormal);
            return true;
        }else if(StyleConstants.FONT_WEIGHT_BOLD.equals(styleElement)){
            if(selected!=uiBold) group.selectToggle(uiBold);
            return true;
        }

        return false;
    }
    
}
