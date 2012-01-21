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
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.opengis.feature.Property;
import org.opengis.feature.type.PropertyType;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class StringEditor implements JFeatureOutLine.PropertyEditor {

    private final StringRW r = new StringRW();
    private final StringRW w = new StringRW();

    @Override
    public boolean canHandle(PropertyType candidate) {
        return String.class.equals(candidate.getBinding());
    }

    @Override
    public TableCellEditor getEditor(Property property) {
        w.property = property;
        return w;
    }

    @Override
    public TableCellRenderer getRenderer(Property property) {
        r.property = property;
        return r.getRenderer();
    }

    private static class StringRW extends TableCellEditorRenderer {

        private final JTextField component = new JTextField();

        private StringRW() {
            panel.setLayout(new BorderLayout());
            panel.add(BorderLayout.CENTER, component);
        }

        @Override
        protected void prepare() {
            if (value instanceof String) {
                component.setText((String) value);
            }
        }

        @Override
        public Object getCellEditorValue() {
            return component.getText();
        }
    }
}
