/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import org.jdesktop.swingx.JXDatePicker;
import org.opengis.feature.AttributeType;
import org.opengis.feature.PropertyType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class DateEditor extends PropertyValueEditor implements ActionListener{

    private JXDatePicker datePicker = new JXDatePicker();

    public DateEditor() {
        super(new BorderLayout());
        add(BorderLayout.CENTER, datePicker);
        datePicker.setOpaque(false);
        datePicker.addActionListener(this);
        datePicker.getEditor().addFocusListener(this);
    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        return candidate instanceof AttributeType && Date.class.equals(((AttributeType)candidate).getValueClass());
    }

    @Override
    public void setValue(PropertyType type, Object value) {
        datePicker.setDate((Date)value);
    }

    @Override
    public Object getValue() {
        return datePicker.getDate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        valueChanged();
    }

    @Override
    public void setEnabled(boolean enabled) {
        datePicker.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return datePicker.isEnabled();
    }
}
