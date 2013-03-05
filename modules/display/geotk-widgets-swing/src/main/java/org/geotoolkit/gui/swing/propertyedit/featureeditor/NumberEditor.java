/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Johann Sorel
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
package org.geotoolkit.gui.swing.propertyedit.featureeditor;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.text.ParseException;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.geotoolkit.util.Converters;
import org.apache.sis.util.Classes;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.PropertyType;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class NumberEditor extends PropertyValueEditor {

    private final JSpinner component = new JSpinner();
    private Class expected;

    public NumberEditor() {
        super(new BorderLayout());
        add(BorderLayout.CENTER, component);
        component.setModel(new SpinnerNumberModel(0d, null, null, 1));
        component.setBorder(null);
    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        final Class binding = candidate.getBinding();
        return Number.class.isAssignableFrom(binding)
                || byte.class.equals(binding)
                || short.class.equals(binding)
                || int.class.equals(binding)
                || long.class.equals(binding)
                || float.class.equals(binding)
                || double.class.equals(binding);
    }

    @Override
    public void setValue(PropertyType propertyType, Object value) {
        expected=null;
        //change model based on property
        if (propertyType != null && propertyType instanceof AttributeType) {
            final AttributeType type = (AttributeType) propertyType;
            expected = type.getBinding();
            if(expected.isPrimitive()){
                if(expected == byte.class){
                    expected = Byte.class;
                }else if(expected == short.class){
                    expected = Short.class;
                }else if(expected == char.class){
                    expected = Character.class;
                }else if(expected == int.class){
                    expected = Integer.class;
                }else if(expected == long.class){
                    expected = Long.class;
                }else if(expected == float.class){
                    expected = Float.class;
                }else if(expected == double.class){
                    expected = Double.class;
                }
            }

            if (expected == Double.class || expected == Float.class || expected == Number.class || expected == BigDecimal.class) {
                component.setModel(new SpinnerNumberModel(Double.valueOf(0d), null, null, Double.valueOf(0.1d)));
            } else {
                component.setModel(new SpinnerNumberModel(0, null, null, 1));
            }

            //todo extract min/max values from filter
        }

        if (value instanceof Number) {
            component.setValue((Number) value);
        }else{
            component.setValue(0d);
        }
    }

    @Override
    public Object getValue() {
        try {
            component.commitEdit();
        } catch (ParseException ex) {
            //not important
        }
        Object val = component.getValue();
        if(expected != null){
            return Converters.convert(val, expected);
        }
        return val;
    }

}
