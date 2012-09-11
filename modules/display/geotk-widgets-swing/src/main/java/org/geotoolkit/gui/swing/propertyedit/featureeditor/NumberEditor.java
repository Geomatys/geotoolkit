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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.PropertyType;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class NumberEditor extends PropertyValueEditor {

    private final JSpinner component = new JSpinner();

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
        //change model based on property
        if (propertyType != null && propertyType instanceof AttributeType) {
            final AttributeType type = (AttributeType) propertyType;
            final Class clazz = type.getBinding();

            if (clazz == Double.class || clazz == Float.class || clazz == Number.class || clazz == BigDecimal.class ||
                clazz == double.class || clazz == float.class) {
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
        return component.getValue();
    }

}
