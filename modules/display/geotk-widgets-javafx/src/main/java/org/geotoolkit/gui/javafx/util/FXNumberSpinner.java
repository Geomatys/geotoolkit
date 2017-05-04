/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014-2015, Geomatys
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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import jidefx.scene.control.decoration.DecorationPane;
import org.apache.sis.util.ObjectConverters;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXNumberSpinner extends DecorationPane{

    private final Spinner numberField;
    private final ObjectProperty<Number> valueProperty = new SimpleObjectProperty<>(0.0);

    public FXNumberSpinner() {
        super(new Spinner(new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.MIN_VALUE, Double.MAX_VALUE, 0,1)));
        numberField = (Spinner) getContent();
        numberField.setEditable(true);

        valueProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                newValue = (Number) ObjectConverters.convert(newValue, (Class) numberField.getValueFactory().getValue().getClass());
                numberField.getValueFactory().setValue(newValue);
            }
        });

        numberField.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                valueProperty.set((Number)newValue);
            }
        });
    }

    public Spinner getSpinner() {
        return numberField;
    }

    public ObjectProperty<Number> valueProperty(){
        return valueProperty;
    }

}
