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
package org.geotoolkit.gui.swing.referencing;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


/**
 * Renderer for an authority code in a {@link javax.swing.JComboBox}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @see javax.swing.plaf.basic.BasicComboBoxRenderer
 *
 * @since 3.12
 * @module
 */
@SuppressWarnings("serial")
final class AuthorityCodeRenderer implements ListCellRenderer {
    /**
     * The original renderer.
     */
    private final ListCellRenderer renderer;

    /**
     * Creates a new renderer.
     *
     * @param original The original renderer.
     */
    AuthorityCodeRenderer(final ListCellRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Return a component that has been configured to display the specified value.
     * Note that it is recommanded to fix the list cell size to a fixed value in
     * order to avoid invoking this method for computing the preferred size.
     */
    @Override
    public Component getListCellRendererComponent(final JList list, final Object value,
            final int index, final boolean isSelected, final boolean cellHasFocus)
    {
        final Component c = renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if ((value instanceof AuthorityCode) && ((AuthorityCode) value).failure()) {
            c.setForeground(Color.RED);
        }
        return c;
    }
}
