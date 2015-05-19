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

package org.geotoolkit.gui.javafx.parameter;

import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStringEditor extends FXValueEditor{

    private final TextField textField = new TextField();

    public FXStringEditor() {
        textField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(currentValue!=null){
                    currentValue.setValue(textField.getText());
                }
            }
        });
    }
    
    @Override
    public boolean canHandle(Class binding) {
        return CharSequence.class.isAssignableFrom(binding) || Character.class.isAssignableFrom(binding);
    }

    @Override
    public void setValue(Property value) {
        super.setValue(value);
        textField.setText(value.getValue()==null ? "" : value.getValue().toString());
    }
    
    @Override
    public Node getComponent() {
        return textField;
    }
    
}
