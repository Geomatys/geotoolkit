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
import javafx.scene.control.ChoiceBox;
import jidefx.scene.control.editor.ChoiceBoxEditor;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXListExpression extends FXExpression {

    private final ChoiceBox<Expression> uiChoice = new ChoiceBoxEditor<>();
    
    public FXListExpression(){
        uiChoice.valueProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue<? extends Expression> observable, Expression oldValue, Expression newValue) {
                value.set(uiChoice.valueProperty().get());
            }
        });
        uiChoice.setMaxWidth(Double.MAX_VALUE);
    }
    
    @Override
    public Expression newValue() {
        return StyleConstants.DEFAULT_STROKE_WIDTH;
    }

    @Override
    public ChoiceBox<Expression> getEditor() {
        return uiChoice;
    }

    @Override
    protected boolean canHandle(Expression exp) {
        if(uiChoice.getItems().contains(exp)){
            uiChoice.valueProperty().set(exp);
            return true;
        }else{
            return false;
        }
    }
    
}
