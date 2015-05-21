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

import java.util.Collection;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.apache.sis.util.Numbers;
import org.apache.sis.util.ObjectConverters;
import org.opengis.feature.AttributeType;
import org.opengis.parameter.ParameterDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXNumberEditor extends FXValueEditor{

    private final Spinner spinner = new Spinner();
    
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
    public Node getComponent() {
        return spinner;
    }

    @Override
    public Property valueProperty() {
        return spinner.getValueFactory().valueProperty();
    }
    
    protected void updateValueFactory(final ObservableValue observable, final Object oldValue, final Object newValue) {
        final Class valueClass = Numbers.primitiveToWrapper(getValueClass());
        
        final Object minValue;
        final Object maxValue;
        final Collection valueList;
        if (newValue instanceof ParameterDescriptor) {
            final ParameterDescriptor desc = (ParameterDescriptor) newValue;
            minValue = desc.getMinimumValue();
            maxValue = desc.getMaximumValue();
            valueList = desc.getValidValues();
            
        } else if (newValue instanceof AttributeType) {
            final AttributeType aType = (AttributeType) newValue;
            valueList = extractChoices(aType);
            // TODO : extract min and max 
            minValue = null;
            maxValue = null;
        } else {
            minValue = null;
            maxValue = null;
            valueList = null;
        }
        
        final SpinnerValueFactory factory;
        if (valueList != null && !valueList.isEmpty()) {
            factory = new SpinnerValueFactory.ListSpinnerValueFactory(FXCollections.observableArrayList(valueList));
            
        } else if (Double.class.isAssignableFrom(valueClass) || Float.class.isAssignableFrom(valueClass)) {
            Double minD = (minValue == null? Double.NaN : ObjectConverters.convert(minValue, Double.class));
            Double maxD = (maxValue == null? Double.NaN : ObjectConverters.convert(maxValue, Double.class));
            factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.isNaN(minD)? -Double.MAX_VALUE : minD, Double.isNaN(maxD)? Double.MAX_VALUE : maxD);
        } else {
            Integer minI = (minValue == null? Integer.MIN_VALUE : ObjectConverters.convert(minValue, Integer.class));
            Integer maxI = (maxValue == null? Integer.MIN_VALUE : ObjectConverters.convert(maxValue, Integer.class));
            factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(minI, maxI);
        }
        
        spinner.setValueFactory(factory);
    }
}
