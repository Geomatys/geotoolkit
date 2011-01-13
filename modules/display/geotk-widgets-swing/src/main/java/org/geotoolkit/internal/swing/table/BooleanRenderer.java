/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


/**
 * A table cell renderer for boolean values displayed as checkbox.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.05
 * @module
 */
@SuppressWarnings("serial")
public final class BooleanRenderer extends JCheckBox implements TableCellRenderer {
    /**
     * Creates a new instance of Boolean renderer.
     */
    public BooleanRenderer() {
    }

    /**
     * Returns the component to use for rendering the given boolean value.
     *
     * @param  value The value, which must be an instance of {@link Boolean}.
     * @return The renderer, which is {@code this}.
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value,
            final boolean isSelected, final boolean hasFocus, final int row, final int column)
    {
        setSelected((Boolean) value);
        final Color fg, bg;
        if (isSelected) {
            fg = table.getSelectionForeground();
            bg = table.getSelectionBackground();
        } else {
            fg = table.getForeground();
            bg = table.getBackground();
        }
        setForeground(fg);
        setBackground(bg);
        setFont(table.getFont());
        return this;
    }
}
