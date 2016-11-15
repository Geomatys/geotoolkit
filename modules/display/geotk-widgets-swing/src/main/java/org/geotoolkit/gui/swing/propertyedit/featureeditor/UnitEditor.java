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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.measure.Unit;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.apache.sis.measure.Units;
import org.opengis.feature.AttributeType;
import org.opengis.feature.PropertyType;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @author Quentin Boileau (Geomatys)
 */
public class UnitEditor extends PropertyValueEditor implements ActionListener {

    private final JComboBox component = new JComboBox();
    private final static List<Unit> UNITS = new ArrayList<>();
    static {
        for (final Field field : Units.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers())) try {
                final Object obj = field.get(null);
                if (obj instanceof Unit<?>) {
                    UNITS.add((Unit) obj);
                }
            } catch (IllegalAccessException e) {
                // Ignore.
            }
        }
    }

    public UnitEditor() {
        super(new BorderLayout());
        component.addFocusListener(this);
        component.setModel(new ListComboBoxModel(UNITS));
        component.setRenderer(new DefaultListCellRenderer(){

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);

                if(value instanceof Unit){
                    final Unit unit = (Unit) value;
                    String str = "";
                    try{
                        str = unit.toString();
                    }catch(Exception ex){
                        str = "-";
                    }
                    this.setText(str);
                }

                return this;
            }

        });

        add(BorderLayout.CENTER, component);

    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        return candidate instanceof AttributeType && Unit.class.equals(((AttributeType)candidate).getValueClass());
    }

    @Override
    public void setValue(PropertyType type, Object value) {
        if (value instanceof Unit) {
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
