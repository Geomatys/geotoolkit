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
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.sis.feature.FeatureExt;
import org.geotoolkit.gui.swing.util.EmptyCellRenderer;
import org.opengis.feature.Attribute;
import org.opengis.feature.FeatureAssociation;
import org.opengis.feature.Property;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public final class TableCellEditorRenderer {

    private TableCellEditorRenderer() {
    }

    public static class Renderer extends DefaultTableCellRenderer{

        private final PropertyValueEditor sub;

        public Renderer(PropertyValueEditor sub) {
            this.sub = sub;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final JComponent model = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if(value instanceof DefaultMutableTreeNode){
                value = ((DefaultMutableTreeNode)value).getUserObject();
            }

            if(value instanceof Property){
                final Property prop = (Property) value;
                sub.setValue(FeatureExt.getType(prop), prop.getValue());
                ((Attribute)prop).setValue(sub.getValue());
            }

            EmptyCellRenderer.mimicStyle(model, sub);
            return sub;
        }

    }

    public static class Editor extends AbstractCellEditor implements TableCellEditor {

        private final PropertyValueEditor sub;

        public Editor(PropertyValueEditor sub) {
            this.sub = sub;
        }

        @Override
        public Object getCellEditorValue() {
            return sub.getValue();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if(value instanceof DefaultMutableTreeNode){
                value = ((DefaultMutableTreeNode)value).getUserObject();
            }

            if(value instanceof Property){
                final Property prop = (Property) value;
                sub.setValue(FeatureExt.getType(prop), prop.getValue());
            }

            return sub;
        }

    }



}
