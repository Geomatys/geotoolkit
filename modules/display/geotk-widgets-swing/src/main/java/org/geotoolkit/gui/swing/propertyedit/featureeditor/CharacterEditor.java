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
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import org.opengis.feature.AttributeType;
import org.opengis.feature.PropertyType;


/**
 * Editor for one Character.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class CharacterEditor extends PropertyValueEditor implements DocumentListener {

    private JTextField current = new JTextField();

    public CharacterEditor() {
        super(new BorderLayout());
        current.setDocument(new CustomDocument());
        add(BorderLayout.CENTER, current);
    }

    @Override
    public boolean canHandle(PropertyType candidate) {
        return candidate instanceof AttributeType && Character.class.equals(((AttributeType)candidate).getValueClass());
    }

    @Override
    public void setValue(PropertyType propertyType, Object value) {
        removeAll();
        add(BorderLayout.CENTER, current);
        if (value instanceof Character) {
            current.setText(((Character) value).toString());
        }else{
            current.setText("");
        }

        current.getDocument().addDocumentListener(this);
        current.addFocusListener(this);
    }

    @Override
    public Object getValue() {
        if (!(current.getText().isEmpty())) {
            return Character.valueOf(current.getText().charAt(0));
        }
        return null;
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
        //never fired in JTextField
    }

    @Override
    public void setEnabled(boolean enabled) {
        current.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return current.isEnabled();
    }

    /**
     * Custom document to keep only last typed character in the TextField text.
     */
    private class CustomDocument extends  PlainDocument {

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            String newChar = str.substring(str.length()-1);
            this.remove(0, this.getLength());
            super.insertString(0, newChar, a);
        }

        @Override
        public void remove(int offs, int len) throws BadLocationException {
            super.remove(offs, len);
        }
    }

}
