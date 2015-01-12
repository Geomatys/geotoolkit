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
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXLineCapExpression extends FXStyleElementController<FXLineCapExpression, Expression> {

    @FXML
    private FXSpecialExpressionButton special;
    @FXML
    private ToggleButton uiRound;
    @FXML
    private ToggleButton uiSquare;
    @FXML
    private ToggleButton uiButt;
    
    private ToggleGroup group;
        
    public FXLineCapExpression(){
        super();
    }
    
    @Override
    public Class<Expression> getEditedClass() {
        return Expression.class;
    }

    @Override
    public Expression newValue() {
        return StyleConstants.STROKE_CAP_BUTT;
    }
    
    @Override
    public void initialize() {
        super.initialize();
        
        group = new ToggleGroup();
        uiRound.setToggleGroup(group);
        uiSquare.setToggleGroup(group);
        uiButt.setToggleGroup(group);
        
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue==uiButt){
                    value.set(StyleConstants.STROKE_CAP_BUTT);
                }else if(newValue==uiRound){
                    value.set(StyleConstants.STROKE_CAP_ROUND);
                }else if(newValue==uiSquare){
                    value.set(StyleConstants.STROKE_CAP_SQUARE);
                }
                special.valueProperty().setValue(value.get());
            }
        });
                
        special.valueProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue observable, Expression oldValue, Expression newValue) {
                value.set(newValue);
            }
        });        
    }

    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        special.setLayer(layer);
    }
    
    @Override
    protected void updateEditor(Expression styleElement) {
        special.valueProperty().set(styleElement);
        
        final Toggle selected = group.getSelectedToggle();
        if(StyleConstants.STROKE_CAP_BUTT.equals(styleElement)){
            if(selected!=uiButt) group.selectToggle(uiButt);
        }else if(StyleConstants.STROKE_CAP_ROUND.equals(styleElement)){
            if(selected!=uiRound) group.selectToggle(uiRound);
        }else if(StyleConstants.STROKE_CAP_SQUARE.equals(styleElement)){
            if(selected!=uiSquare) group.selectToggle(uiSquare);
        }else{
            group.selectToggle(null);
        }
    }
    
}
