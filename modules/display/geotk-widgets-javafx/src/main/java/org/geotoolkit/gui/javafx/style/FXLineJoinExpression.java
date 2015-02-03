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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLineJoinExpression extends FXExpression {

    private final ToggleGroup group = new ToggleGroup();
    private final ToggleButton uiBevel = new ToggleButton(null, new ImageView("/org/geotoolkit/gui/javafx/icon/crystalproject/16x16/actions/join_bevel.png"));
    private final ToggleButton uiMiter = new ToggleButton(null, new ImageView("/org/geotoolkit/gui/javafx/icon/crystalproject/16x16/actions/join_miter.png"));
    private final ToggleButton uiRound = new ToggleButton(null, new ImageView("/org/geotoolkit/gui/javafx/icon/crystalproject/16x16/actions/join_round.png"));
    private final HBox hbox = new HBox(uiBevel,uiMiter,uiRound);

    public FXLineJoinExpression(){
        uiRound.setToggleGroup(group);
        uiMiter.setToggleGroup(group);
        uiBevel.setToggleGroup(group);

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue==uiBevel){
                    value.set(StyleConstants.STROKE_JOIN_BEVEL);
                }else if(newValue==uiMiter){
                    value.set(StyleConstants.STROKE_JOIN_MITRE);
                }else if(newValue==uiRound){
                    value.set(StyleConstants.STROKE_JOIN_ROUND);
                }
            }
        });
    }
    
    @Override
    public Expression newValue() {
        return StyleConstants.STROKE_JOIN_BEVEL;
    }

    @Override
    protected Node getEditor() {
        return hbox;
    }

    @Override
    protected boolean canHandle(Expression styleElement) {
        
        final Toggle selected = group.getSelectedToggle();
        if(StyleConstants.STROKE_JOIN_BEVEL.equals(styleElement)){
            if(selected!=uiBevel) group.selectToggle(uiBevel);
            return true;
        }else if(StyleConstants.STROKE_JOIN_MITRE.equals(styleElement)){
            if(selected!=uiMiter) group.selectToggle(uiMiter);
            return true;
        }else if(StyleConstants.STROKE_JOIN_ROUND.equals(styleElement)){
            if(selected!=uiRound) group.selectToggle(uiRound);
            return true;
        }

        return false;
    }
    
}
