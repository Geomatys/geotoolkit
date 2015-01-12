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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.apache.sis.util.ObjectConverters;
import org.apache.sis.util.UnconvertibleObjectException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXNumberEditor extends FXValueEditor{

    private final TextField textField = new TextField();

    public FXNumberEditor() {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(currentValue!=null){
                    try{
                        Object val = ObjectConverters.convert(textField.getText(), getValueClass());
                        if(val!=null) currentValue.setValue(val);
                    }catch(UnconvertibleObjectException ex){}
                }
            }
        });
    }
    
    @Override
    public boolean canHandle(Class binding) {
        return Number.class.isAssignableFrom(binding)
                || byte.class.equals(binding)
                || short.class.equals(binding)
                || int.class.equals(binding)
                || long.class.equals(binding)
                || float.class.equals(binding)
                || double.class.equals(binding);
    }

    @Override
    public void setValue(Property value) {
        super.setValue(value);
        textField.setText(String.valueOf(value.getValue()));
    }
    
    @Override
    public Node getComponent() {
        return textField;
    }
    
}
