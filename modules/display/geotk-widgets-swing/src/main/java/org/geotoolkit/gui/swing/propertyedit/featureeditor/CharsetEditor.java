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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.opengis.feature.type.PropertyType;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class CharsetEditor implements JFeatureOutLine.PropertyEditor {

    private final CharsetRW r = new CharsetRW();
    private final CharsetRW w = new CharsetRW();

    @Override
    public boolean canHandle(PropertyType candidate) {
        return Charset.class.equals(candidate.getBinding());
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

    private static class CharsetRW extends TableCellEditorRenderer {

        private final JComboBox component = new JComboBox();

        private CharsetRW() {
            panel.setLayout(new BorderLayout());
            panel.add(BorderLayout.NORTH, component);
            
            final List<Charset> sets = new ArrayList<Charset>(Charset.availableCharsets().values());
            component.setModel(new ListComboBoxModel(sets));
        }

        @Override
        protected void prepare() {
            if (value instanceof Charset) {
                component.setSelectedItem(value);
            }
        }

        @Override
        public Object getCellEditorValue() {
            return component.getSelectedItem();
        }
    }
}
