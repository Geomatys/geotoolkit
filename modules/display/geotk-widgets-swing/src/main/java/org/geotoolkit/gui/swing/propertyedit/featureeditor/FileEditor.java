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
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.geotoolkit.gui.swing.propertyedit.JFeatureOutLine;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.opengis.feature.type.PropertyType;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class FileEditor implements JFeatureOutLine.PropertyEditor {

    private final FileRW r = new FileRW();
    private final FileRW w = new FileRW();

    @Override
    public boolean canHandle(PropertyType candidate) {
        return File.class.equals(candidate.getBinding());
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

    private static class FileRW extends TableCellEditorRenderer implements ActionListener {

        private final JTextField component = new JTextField();
        private final JButton chooseButton = new JButton("...");
        

        private FileRW() {
            panel.setLayout(new BorderLayout());
            panel.add(BorderLayout.CENTER, component);
            panel.add(BorderLayout.EAST, chooseButton);
            chooseButton.addActionListener(this);
        }

        @Override
        protected void prepare() {
            if (value instanceof File) {
                component.setText( ((File) value).toURI().toString() );
            }else{
                component.setText("");
            }
        }

        @Override
        public Object getCellEditorValue() {
            final String str = component.getText();
            value = new File(str);
            return value;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setMultiSelectionEnabled(false);
            final int response = chooser.showDialog(chooseButton, MessageBundle.getString("ok"));
            if(response == JFileChooser.APPROVE_OPTION){
                final File f = chooser.getSelectedFile();
                if(f!=null){
                    value = f;
                    prepare();
                }
            }
        }
        
    }
}
