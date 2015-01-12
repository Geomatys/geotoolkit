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
import jidefx.scene.control.field.NumberField;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXNumberCell<S> extends FXTableCell<S, Object> {
    private final FXNumberSpinner field = new FXNumberSpinner();

    public FXNumberCell(NumberField.NumberType type) {
        field.getNumberField().setNumberType(type);
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
        field.getNumberField().requestFocus();
    }

    @Override
    public void commitEdit(Object newValue) {
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
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        if (item != null) {
            if(item instanceof Float || item instanceof Double){
                final String str = DecimalFormat.getNumberInstance().format(((Number)item).doubleValue());
                setText(str);
            }else{
                final String str = DecimalFormat.getIntegerInstance().format(((Number)item).doubleValue());
                setText(str);
            }
            
        }
    }
    
}
