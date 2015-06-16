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
package org.geotoolkit.gui.javafx.util;

import java.text.DecimalFormat;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.SpinnerValueFactory;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXNumberCell<S> extends FXTableCell<S, Number> {
    protected final FXNumberSpinner field = new FXNumberSpinner();
    
    public FXNumberCell(Class clazz) {
        if(clazz==Integer.class){
            field.getSpinner().setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 1));
        }else{
            field.getSpinner().setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE, Double.MAX_VALUE, 0, 1));
        }
        setGraphic(field);
        setAlignment(Pos.CENTER_RIGHT);
        setContentDisplay(ContentDisplay.CENTER);
    }

    @Override
    public void terminateEdit() {
        commitEdit(field.valueProperty().get());
    }

    @Override
    public void startEdit() {
        Number value = (Number) getItem();
        if (value == null) {
            value = 0;
        }
        field.valueProperty().set(value);
        super.startEdit();
        setText(null);
        setGraphic(field);
        field.getSpinner().requestFocus();
    }

    @Override
    public void commitEdit(Number newValue) {
        itemProperty().set(newValue);
        super.commitEdit(newValue);
        updateItem(newValue, false);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        updateItem(getItem(), false);
    }

    @Override
    protected void updateItem(Number item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(null);
        if (item != null) {
            setText(DecimalFormat.getNumberInstance().format(item));
        } else {
            setText(null);
        }
    }
    
    public static class Float<T> extends FXNumberCell<T> {

        public Float() {
            super(Double.class);
        }

        @Override
        public void commitEdit(Number newValue) {
            super.commitEdit(newValue.floatValue()); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void terminateEdit() {
            super.commitEdit(this.field.valueProperty().get().floatValue());
        }
    }
}
