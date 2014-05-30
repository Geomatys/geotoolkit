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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.feature.type.PropertyType;

/**
 * Throw PropertyChange event when TextField text change.
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class FileEditor extends PropertyValueEditor implements ActionListener, DocumentListener {

    private final JTextField component = new JTextField();
    private final JButton chooseButton = new JButton("...");

    public FileEditor() {
        super(new BorderLayout());
        add(BorderLayout.CENTER, component);
        add(BorderLayout.EAST, chooseButton);
        component.getDocument().addDocumentListener(this);
        component.addFocusListener(this);
        chooseButton.addActionListener(this);
        chooseButton.addFocusListener(this);
        chooseButton.setMargin(new Insets(0, 0, 0, 0));
    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        return File.class.equals(candidate.getBinding());
    }

    @Override
    public void setValue(PropertyType type, Object value) {
        if (value instanceof File) {
            component.setText( ((File) value).toURI().toString() );
        }else{
            component.setText("");
        }
    }

    @Override
    public Object getValue() {
        final String str = component.getText();
        final File value = new File(str);
        return value;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setMultiSelectionEnabled(false);

        final String prevPath = getPreviousPath();
        if (prevPath != null) {
            chooser.setCurrentDirectory(new File(prevPath));
        }

        final int response = chooser.showDialog(chooseButton, MessageBundle.getString("ok"));
        if(response == JFileChooser.APPROVE_OPTION){
            final File f = chooser.getSelectedFile();
            if(f!=null){
                setPreviousPath(f.getAbsolutePath());
                setValue(null, f);
                valueChanged();
            }
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        valueChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        valueChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        valueChanged();
    }

    @Override
    public void setEnabled(boolean enabled) {
        component.setEnabled(enabled);
        chooseButton.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return component.isEnabled() && chooseButton.isEnabled();
    }

    public static String getPreviousPath() {
        final Preferences prefs = Preferences.userNodeForPackage(FileEditor.class);
        return prefs.get("path", null);
    }

    public static void setPreviousPath(final String path) {
        final Preferences prefs = Preferences.userNodeForPackage(FileEditor.class);
        prefs.put("path", path);
    }


}
