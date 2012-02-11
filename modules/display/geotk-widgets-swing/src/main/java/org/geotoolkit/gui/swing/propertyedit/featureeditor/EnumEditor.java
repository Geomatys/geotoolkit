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
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import org.opengis.feature.type.PropertyType;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class EnumEditor implements JFeatureOutLine.PropertyEditor {

    private final EnumRW r = new EnumRW();
    private final EnumRW w = new EnumRW();

    @Override
    public boolean canHandle(PropertyType candidate) {
        return Enum.class.equals(candidate.getBinding());
    }

    @Override
    public TableCellEditor getEditor(PropertyType property) {
        w.property = property;
        return w;
    }

    @Override
    public TableCellRenderer getRenderer(PropertyType property) {
        r.property = property;
        return r.getRenderer();
    }

    private static class EnumRW extends TableCellEditorRenderer {

        private final JComboBox component = new JComboBox();

        private EnumRW() {
            panel.setLayout(new BorderLayout());
            panel.add(BorderLayout.CENTER, component);
        }

        @Override
        protected void prepare() {
            component.setModel(new EnumComboBoxModel(property.getBinding()));
                
            if (value instanceof Enum) {
                component.setSelectedItem(value);
            }else{
                component.setSelectedItem(null);
            }
        }

        @Override
        public Object getCellEditorValue() {
            return component.getSelectedItem();
        }
    }
}
