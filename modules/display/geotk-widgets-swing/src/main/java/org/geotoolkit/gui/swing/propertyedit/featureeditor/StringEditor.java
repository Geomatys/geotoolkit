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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.opengis.feature.type.PropertyType;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class StringEditor extends VersatileEditor {

    private final StringRW r = new StringRW();
    private final StringRW w = new StringRW();

    @Override
    public TableCellEditorRenderer getReadingRenderer() {
        return r;
    }

    @Override
    public TableCellEditorRenderer getWritingRenderer() {
        return w;
    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        return String.class.equals(candidate.getBinding());
    }

    @Override
    public TableCellEditor getEditor(PropertyType property) {
        w.propertyType = property;
        r.update();
        return w;
    }

    @Override
    public TableCellRenderer getRenderer(PropertyType property) {
        r.propertyType = property;
        r.update();
        return r.getRenderer();
    }

    private static class StringRW extends TableCellEditorRenderer {

        private final JTextField textField = new JTextField();
        private final JPasswordField passwordField = new JPasswordField();
        private JTextField current = null;

        private StringRW() {
            panel.setLayout(new BorderLayout());
            panel.add(BorderLayout.CENTER, textField);
        }

        private void update(){
            panel.removeAll();
            if(propertyType != null && propertyType.getName().getLocalPart().startsWith("pass")){
                panel.add(BorderLayout.CENTER, passwordField);
                current = passwordField;
            }else{
                panel.add(BorderLayout.CENTER, textField);
                current = textField;
            }
        }

        @Override
        protected void prepare() {
            update();
            if (value instanceof String) {
                current.setText((String) value);
            }else{
                current.setText("");
            }
        }

        @Override
        public Object getCellEditorValue() {
            update();
            return current.getText();
        }
    }
}
