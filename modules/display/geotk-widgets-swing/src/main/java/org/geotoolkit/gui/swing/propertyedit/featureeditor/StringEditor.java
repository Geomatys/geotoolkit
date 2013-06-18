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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.opengis.feature.type.PropertyType;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class StringEditor extends PropertyValueEditor implements DocumentListener {

    private final JTextField textField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private JTextField current = textField;

    public StringEditor() {
        super(new BorderLayout());
        add(BorderLayout.CENTER, textField);
        textField.getDocument().addDocumentListener(this);
    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        return String.class.equals(candidate.getBinding());
    }

    @Override
    public void setValue(PropertyType propertyType, Object value) {
        removeAll();
        if(propertyType != null && propertyType.getName().getLocalPart().startsWith("pass")){
            add(BorderLayout.CENTER, passwordField);
            current = passwordField;
        }else{
            add(BorderLayout.CENTER, textField);
            current = textField;
        }

        if (value instanceof String) {
            current.setText((String) value);
        }else{
            current.setText("");
        }
    }

    @Override
    public Object getValue() {
        return current.getText();
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

}
