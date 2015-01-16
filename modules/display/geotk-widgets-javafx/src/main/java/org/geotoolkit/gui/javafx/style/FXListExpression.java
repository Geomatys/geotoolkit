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
import javafx.scene.control.ChoiceBox;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXListExpression extends FXStyleElementController<Expression> {

    @FXML private FXSpecialExpressionButton special;
    @FXML private ChoiceBox<Expression> uiChoice;
    
    public FXListExpression(){
    }
    
    @Override
    public Class<Expression> getEditedClass() {
        return Expression.class;
    }

    @Override
    public Expression newValue() {
        return StyleConstants.DEFAULT_STROKE_WIDTH;
    }

    public ChoiceBox<Expression> getChoiceBox() {
        return uiChoice;
    }

    @Override
    public void initialize() {
        super.initialize();
        
        uiChoice.valueProperty().addListener(new ChangeListener<Expression>() {

            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                value.set(uiChoice.valueProperty().get());
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
        uiChoice.valueProperty().set(styleElement);
    }
    
}
