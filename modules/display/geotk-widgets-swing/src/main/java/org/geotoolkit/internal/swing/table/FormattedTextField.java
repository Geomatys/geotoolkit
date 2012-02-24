/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.swing.table;

import java.text.Format;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import javax.swing.JFormattedTextField;


/**
 * A formatted text field which doesn't consume the "Enter" key. This text field
 * is better suited than the default one for use as a table cell editor.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
@SuppressWarnings("serial")
final class FormattedTextField extends JFormattedTextField {
    /**
     * Creates a new text field for the given format.
     *
     * @param format The format to use for the formatting the field.
     */
    public FormattedTextField(final Format format) {
        super(format);
    }

    /**
     * Overridden in order to not consume the Enter key.
     */
    @Override
    protected boolean processKeyBinding(final KeyStroke ks, final KeyEvent e,
            final int condition, final boolean pressed)
    {
        boolean consumed = super.processKeyBinding(ks, e, condition, pressed);
        if (consumed && condition == WHEN_FOCUSED && ks.getKeyCode() == KeyEvent.VK_ENTER) {
            consumed = false;
        }
        return consumed;
    }
}
