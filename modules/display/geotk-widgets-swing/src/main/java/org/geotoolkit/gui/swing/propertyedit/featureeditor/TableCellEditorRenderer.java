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

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.netbeans.swing.outline.Outline;
import org.opengis.feature.type.PropertyType;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public abstract class TableCellEditorRenderer extends AbstractCellEditor implements TableCellEditor {

    private final TableCellRenderer renderer = new DefaultTableCellRenderer() {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final JComponent model = (JComponent) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            TableCellEditorRenderer.this.value = value;
            final JComponent c = TableCellEditorRenderer.this.getComponent(table, isSelected, row, column);
            TableCellEditorRenderer.this.mimicStyle(model, c);
            return c;
        }
    };
    protected final JPanel panel = new JPanel();
    protected PropertyType property;
    protected Object value;

    private void mimicStyle(final JComponent model, final JComponent candidate) {
        candidate.setBackground(model.getBackground());
        candidate.setForeground(model.getForeground());
        candidate.setOpaque(model.isOpaque());
        candidate.setBorder(model.getBorder());
        candidate.setFont(model.getFont());
    }

    @Override
    public Object getCellEditorValue() {
        return value;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.value = value;

        final JComponent candidate = getComponent(table, isSelected, row, column);

        final JComponent model = (JComponent) renderer.getTableCellRendererComponent(
                table, value, isSelected, true, row, column);
        TableCellEditorRenderer.this.mimicStyle(model, candidate);
        return candidate;
    }

    protected abstract void prepare();

    protected JComponent getComponent(JTable table, boolean isSelected, int row, int column) {
        prepare();

        if (table instanceof Outline) {
            final Outline ol = (Outline) table;
            final int height = ol.getRowHeight(row);
            if (height < panel.getPreferredSize().height) {
                ol.setRowHeight(panel.getPreferredSize().height);
            }
        }

        return panel;
    }

    public TableCellRenderer getRenderer() {
        return renderer;
    }
}
