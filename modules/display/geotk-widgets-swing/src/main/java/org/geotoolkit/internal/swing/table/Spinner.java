/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;


/**
 * A spinner field using an editor without border. This text field
 * is better suited than the default one for use as a table cell editor.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.15
 * @module
 */
@SuppressWarnings("serial")
final class Spinner extends JSpinner {
    /**
     * Creates a new spinner.
     */
    public Spinner() {
    }

    /**
     * Creates a new editor without border.
     */
    @Override
    protected JComponent createEditor(final SpinnerModel model) {
        final JComponent editor = super.createEditor(model);
        editor.setBorder(null);
        return editor;
    }
}
