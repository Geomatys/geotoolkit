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
package org.geotoolkit.gui.javafx.style;

import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import org.geotoolkit.cql.CQL;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.filter.FXCQLEditor;
import org.geotoolkit.map.MapLayer;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXSpecialExpressionButton extends HBox {

    public static final String EXPRESSION_PROPERTY = "expression";

    private static final Image ICON_EXP_NO = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_PENCIL, 16, FontAwesomeIcons.DISABLE_COLOR),null);
    private static final Image ICON_EXP_YES = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_PENCIL, 16, FontAwesomeIcons.DEFAULT_COLOR),null);
    private static final Image ICON_ERASE = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_ERASER, 16, FontAwesomeIcons.DEFAULT_COLOR),null);
    
    private final SimpleObjectProperty<Expression> exp = new SimpleObjectProperty(){
        @Override
        public void set(Object newValue) {
            super.set(newValue);
            parse((Expression)newValue);
        }
        
    };
    private MapLayer layer = null;

    private final Button guiEdit = new Button();
    private final Button guiErase = new Button();
    
    public FXSpecialExpressionButton(){
        setBackground(Background.EMPTY);
        getChildren().add(guiEdit);
        getChildren().add(guiErase);

        guiEdit.setBorder(Border.EMPTY);
        guiEdit.setBackground(Background.EMPTY);
        guiEdit.setGraphic(new ImageView(ICON_EXP_NO));

        guiErase.setBorder(Border.EMPTY);
        guiErase.setBackground(Background.EMPTY);
        guiErase.setGraphic(new ImageView(ICON_ERASE));

        guiEdit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                try{
                    final Expression ne = FXCQLEditor.showDialog(FXSpecialExpressionButton.this, layer, exp);
                    if(!Objects.equals(ne, exp.getValue())){
                        exp.set(ne);
                    }
                }catch(CQLException ex){
                    ex.printStackTrace();
                }
            }
        });

        guiErase.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                exp.set(null);
            }
        });
    }

    public void setLayer(final MapLayer layer){
        this.layer = layer;
    }

    public MapLayer getLayer(){
        return layer;
    }

    public void parse(final Expression exp){
        String tooltip = null;
        if(exp==null){
            guiEdit.setGraphic(new ImageView(ICON_EXP_NO));
            guiErase.setVisible(false);
        }else{
            guiEdit.setGraphic(new ImageView(ICON_EXP_YES));
            guiErase.setVisible(true);
            tooltip = CQL.write(exp);
        }
        
        guiEdit.setTooltip(new Tooltip(tooltip));
        guiErase.setTooltip(new Tooltip(tooltip));
    }

    public ObjectProperty<Expression> valueProperty(){
        return exp;
    }
    
}
