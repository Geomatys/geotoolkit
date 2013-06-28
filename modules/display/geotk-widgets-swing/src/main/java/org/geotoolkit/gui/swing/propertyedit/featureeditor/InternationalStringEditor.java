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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.feature.type.PropertyType;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 */
public class InternationalStringEditor extends PropertyValueEditor implements DocumentListener {

    private final JTextField textField = new JTextField();
    private JTextField current = null;

    public InternationalStringEditor() {
        super(new BorderLayout());
    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        return InternationalString.class.equals(candidate.getBinding());
    }

    @Override
    public void setValue(PropertyType type, Object value) {
        removeAll();
        add(BorderLayout.CENTER, textField);
        textField.getDocument().addDocumentListener(this);
        textField.addFocusListener(this);
        current = textField;
        if (value instanceof InternationalString) {
            current.setText(((InternationalString) value).toString());
        }else{
            current.setText("");
        }
    }

    @Override
    public Object getValue() {
        return new SimpleInternationalString(current.getText());
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
        current.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return current.isEnabled();
    }
}
