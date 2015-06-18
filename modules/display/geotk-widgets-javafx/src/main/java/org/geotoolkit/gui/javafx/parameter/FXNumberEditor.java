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
import java.util.logging.Level;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;
import org.apache.sis.util.Numbers;
import org.apache.sis.util.ObjectConverter;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.internal.Loggers;
import org.opengis.feature.AttributeType;
import org.opengis.parameter.ParameterDescriptor;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXNumberEditor extends FXValueEditor {

    private final Spinner spinner = new Spinner();
    
    public FXNumberEditor(Spi originatingSpi) {
        super(originatingSpi);
        currentAttributeType.addListener(this::updateValueFactory);
        currentParamDesc.addListener(this::updateValueFactory);
        updateValueFactory(null, null, null);
    }
    
    @Override
    public Node getComponent() {
        return spinner;
    }

    @Override
    public Property valueProperty() {
        return spinner.getValueFactory().valueProperty();
    }

    @Override
    protected Class getValueClass() {
        Class valueClass = super.getValueClass();
        if (valueClass.isPrimitive()) 
            return Numbers.primitiveToWrapper(valueClass);
        else if (Number.class.isAssignableFrom(valueClass)) 
            return valueClass;
        else
            return Double.class;
    }
    
    /**
     * Update spinner configuration according to given descriptor/ type.
     * @param observable Not used
     * @param oldValue Not used
     * @param newValue Not used
     */
    protected void updateValueFactory(final ObservableValue observable, final Object oldValue, final Object newValue) {
        final Class valueClass = getValueClass();
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
            
        } else if (Float.class.isAssignableFrom(valueClass)) {
            Float minF = (minValue == null? Float.NaN : ObjectConverters.convert(minValue, Float.class));
            Float maxF = (maxValue == null? Float.NaN : ObjectConverters.convert(maxValue, Float.class));
            factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(Float.isNaN(minF)? -Float.MAX_VALUE : minF, Float.isNaN(maxF)? Float.MAX_VALUE : maxF, 0);
        } else if (Double.class.isAssignableFrom(valueClass)) {
            Double minD = (minValue == null? Double.NaN : ObjectConverters.convert(minValue, Double.class));
            Double maxD = (maxValue == null? Double.NaN : ObjectConverters.convert(maxValue, Double.class));
            factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(Double.isNaN(minD)? -Double.MAX_VALUE : minD, Double.isNaN(maxD)? Double.MAX_VALUE : maxD, 0);
        } else {
            Integer minI = (minValue == null? Integer.MIN_VALUE : ObjectConverters.convert(minValue, Integer.class));
            Integer maxI = (maxValue == null? Integer.MAX_VALUE : ObjectConverters.convert(maxValue, Integer.class));
            factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(minI, maxI, 0);
        }
        
        /* We try to get a converter to allow user to type text in the spinner.
         * If we fail at obtaining one, the spinner will not be editable.
         */
        try {
            final ObjectConverter<Object, String> converter = ObjectConverters.find(valueClass, String.class);
            final ObjectConverter<String, Object> inverse = converter.inverse();
            factory.setConverter(new StringConverter() {
                @Override
                public String toString(Object object) {
                    return converter.apply(object);
                }

                @Override
                public Object fromString(String string) {
                    return inverse.apply(string);
                }
            });
            spinner.setEditable(true);
        } catch (Exception e) {
            Loggers.JAVAFX.log(Level.FINE, null, e);
            spinner.setEditable(false);
        }
        
        spinner.setValueFactory(factory);
    }
    
    
    public static final class Spi extends FXValueEditorSpi {
    
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
        public FXValueEditor createEditor() {
            return new FXNumberEditor(this);
        }
    }    
}
