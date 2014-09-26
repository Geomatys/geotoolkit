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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import jidefx.scene.control.decoration.DecorationPane;
import jidefx.scene.control.decoration.DecorationUtils;
import jidefx.scene.control.decoration.Decorator;
import jidefx.scene.control.decoration.PredefinedDecorators;
import jidefx.scene.control.field.NumberField;
import org.geotoolkit.math.XMath;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXNumberSpinner extends DecorationPane{

    private final NumberField numberField;
    private final ObjectProperty<Number> minValue = new SimpleObjectProperty<>(Double.NEGATIVE_INFINITY);
    private final ObjectProperty<Number> maxValue = new SimpleObjectProperty<>(Double.POSITIVE_INFINITY);
    private final ObjectProperty<Number> stepValue = new SimpleObjectProperty<>(1.0);
    
    public FXNumberSpinner() {
        super(new NumberField(NumberField.NumberType.Normal));
        numberField = (NumberField) getContent();
        final Decorator<Button> deco1 = PredefinedDecorators.getInstance().getIncreaseButtonDecoratorSupplier().get();
        final Decorator<Button> deco2 = PredefinedDecorators.getInstance().getDecreaseButtonDecoratorSupplier().get();
        deco1.getNode().setOnAction((ActionEvent event) -> {nextUp();});
        deco2.getNode().setOnAction((ActionEvent event) -> {nextDown();});
        DecorationUtils.installAll(numberField, deco1,deco2);
    }

    public NumberField getNumberField() {
        return numberField;
    }
    
    public ObjectProperty<Number> valueProperty(){
        return numberField.valueProperty();
    }
    
    public ObjectProperty<NumberField.NumberType> numberTypeProperty(){
        return numberField.numberTypeProperty();
    }

    public ObjectProperty<Number> minValueProperty() {
        return minValue;
    }
    
    public ObjectProperty<Number> maxValueProperty() {
        return maxValue;
    }
    
    public ObjectProperty<Number> stepValueProperty() {
        return stepValue;
    }
    
    private void nextUp(){
        try{
            double d = Double.valueOf(numberField.getText());
            d += stepValue.get().doubleValue();
            d = XMath.clamp(d, minValue.get().doubleValue(), maxValue.get().doubleValue());
            if(numberTypeProperty().get().equals(NumberField.NumberType.Integer)){
                numberField.setValue((int)d);
            }else{
                numberField.setValue(d);
            }
        }catch(NumberFormatException ex){}
    }
    
    private void nextDown(){
        try{
            double d = Double.valueOf(numberField.getText());
            d -= stepValue.get().doubleValue();
            d = XMath.clamp(d, minValue.get().doubleValue(), maxValue.get().doubleValue());
            if(numberTypeProperty().get().equals(NumberField.NumberType.Integer)){
                numberField.setValue((int)d);
            }else{
                numberField.setValue(d);
            }
        }catch(NumberFormatException ex){}
    }
    
}
