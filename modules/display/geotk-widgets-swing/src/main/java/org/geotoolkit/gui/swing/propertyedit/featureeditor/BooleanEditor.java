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
import javax.swing.JCheckBox;
import org.opengis.feature.type.PropertyType;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class BooleanEditor extends PropertyValueEditor implements ActionListener{

    private final JCheckBox component = new JCheckBox();

    public BooleanEditor() {
        super(new BorderLayout());
        add(BorderLayout.CENTER, component);
        component.setOpaque(false);
        component.addActionListener(this);
    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        return Boolean.class.equals(candidate.getBinding());
    }

    @Override
    public void setValue(PropertyType type, Object value) {
        component.setSelected(Boolean.TRUE.equals(value));
    }

    @Override
    public Object getValue() {
        return component.isSelected();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        firePropertyChange(PROP_VALUE, null, getValue());
    }

}
