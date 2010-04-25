/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.internal.swing;

import javax.swing.text.Document;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;


/**
 * Listens for any kind of changes in a document. This listener can be very costly; consequently
 * it shall be used only for very small documents (typically a {@link javax.swing.JTextField}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
public abstract class DocumentChangeListener implements DocumentListener {
    /**
     * Creates a default listener.
     */
    protected DocumentChangeListener() {
    }

    /**
     * Invoked when characters were inserted.
     * The default implementation delegates to {@link #changedUpdate(DocumentEvent)}.
     *
     * @param event Contains the range of characters inserted.
     */
    @Override
    public void insertUpdate(final DocumentEvent event) {
        changedUpdate(event);
    }

    /**
     * Invoked when characters were removed.
     * The default implementation delegates to {@link #changedUpdate(DocumentEvent)}.
     *
     * @param event Contains the range of characters removed.
     */
    @Override
    public void removeUpdate(final DocumentEvent event) {
        changedUpdate(event);
    }

    /**
     * Invoked when characters were modified.
     *
     * @param event Contains the range of characters modified.
     */
    @Override
    public void changedUpdate(final DocumentEvent event) {
        final Document doc = event.getDocument();
        final String text;
        try {
            text = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            throw new AssertionError(e);
        }
        textChanged(doc, text);
    }

    /**
     * Invoked when the text changed.
     *
     * @param document The document.
     * @param text The new text.
     */
    protected abstract void textChanged(Document document, String text);
}
