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

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.opengis.referencing.IdentifiedObject;


/**
 * A table cell renderer for {@link IdentifiedObject} values.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 * @module
 *
 * @todo Future version should provides more sophisticated rendering, for example
 *       a button for showing a popup with more properties.
 */
public final class IdentifiedObjectRenderer extends DefaultTableCellRenderer {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 9009246728194595592L;

    /**
     * Creates a new renderer.
     */
    public IdentifiedObjectRenderer() {
    }

    /**
     * Returns the component for rendering the given object.
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table, Object value,
            final boolean isSelected, final boolean hasFocus, final int row, final int column)
    {
        if (value instanceof IdentifiedObject) {
            value = ((IdentifiedObject) value).getName();
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
