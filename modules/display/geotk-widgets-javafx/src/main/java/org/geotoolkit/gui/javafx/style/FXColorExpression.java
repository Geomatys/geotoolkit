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

import java.awt.Color;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import org.geotoolkit.gui.javafx.util.FXUtilities;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXColorExpression extends FXStyleElementController<FXColorExpression, Expression> {

    @FXML
    private FXSpecialExpressionButton uiSpecial;

    @FXML
    private ColorPicker uiPicker;
    
    public FXColorExpression(){
    }
    
    @Override
    public Class<Expression> getEditedClass() {
        return Expression.class;
    }

    @Override
    public Expression newValue() {
        return StyleConstants.DEFAULT_FILL_COLOR;
    }

    @Override
    public void initialize() {
        super.initialize();
        
        uiPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                value.set(getFilterFactory().literal(FXUtilities.toSwingColor(uiPicker.getValue())));
                uiSpecial.valueProperty().setValue(value.get());
            }
        });
        
        uiSpecial.valueProperty().addListener(new ChangeListener<Expression>() {
            @Override
            public void changed(ObservableValue observable, Expression oldValue, Expression newValue) {
                value.set(newValue);
            }
        });
    }
    
    @Override
    protected void updateEditor(Expression styleElement) {
        super.updateEditor(styleElement);
        uiSpecial.valueProperty().set(styleElement);
        final Color color = styleElement.evaluate(null,Color.class);
        if(color!=null){
            uiPicker.setValue(FXUtilities.toFxColor(color));
        }
    }
    
}
