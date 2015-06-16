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
import static org.geotoolkit.gui.javafx.style.FXStyleElementController.getFilterFactory;
import org.geotoolkit.gui.javafx.util.FXNumberSpinner;
import org.geotoolkit.style.StyleConstants;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXNumberExpression extends FXExpression {

    private FXNumberSpinner uiNumber = new FXNumberSpinner();
    
    public FXNumberExpression(){
        super();

        uiNumber.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                value.set(getFilterFactory().literal(uiNumber.valueProperty().get()));
            }
        });

    }
    
    @Override
    public Expression newValue() {
        return StyleConstants.DEFAULT_STROKE_WIDTH;
    }

    @Override
    protected FXNumberSpinner getEditor() {
        return uiNumber;
    }

    @Override
    protected boolean canHandle(Expression exp) {
        if(exp==null) return false;
        try{
            final Number n = (Number) exp.evaluate(exp, (Class)uiNumber.getSpinner().getValueFactory().getValue().getClass());
            if(n!=null){
                uiNumber.getSpinner().getValueFactory().setValue(n);
                return true;
            }
        }catch(Exception ex){}
        return false;
    }

}
