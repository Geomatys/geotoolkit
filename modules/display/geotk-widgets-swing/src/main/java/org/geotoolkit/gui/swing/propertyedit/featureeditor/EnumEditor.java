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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import org.opengis.feature.AttributeType;
import org.opengis.feature.PropertyType;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class EnumEditor extends PropertyValueEditor implements ActionListener{

    private final JComboBox component = new JComboBox();

    public EnumEditor() {
        super(new BorderLayout());
        add(BorderLayout.CENTER, component);
        component.addActionListener(this);
        component.addFocusListener(this);
    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        return candidate instanceof AttributeType && Enum.class.equals(((AttributeType)candidate).getValueClass());
    }

    @Override
    public void setValue(PropertyType type, Object value) {
        component.setModel(new EnumComboBoxModel(((AttributeType)type).getValueClass()));

        if (value instanceof Enum) {
            component.setSelectedItem(value);
        }else{
            component.setSelectedItem(null);
        }
    }

    @Override
    public Object getValue() {
        return component.getSelectedItem();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        valueChanged();
    }

    @Override
    public void setEnabled(boolean enabled) {
        component.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return component.isEnabled();
    }
}
