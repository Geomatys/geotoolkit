/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import java.text.NumberFormat;
import java.text.FieldPosition;
import java.util.Locale;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * A table cell renderer where the first column has the color of labels.
 * It can be used for simulating a row header in a single table. This is
 * different than setting the row header in the usual Swing way (through
 * {@link javax.swing.JScrollPane#setRowHeaderView}) in that the column
 * can be masked by a horizontal scrolling.
 * <p>
 * Applications wanting a real row header with label colors should use
 * {@link org.geotoolkit.internal.SwingUtilities#setupAsRowHeader} instead.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public class LabeledRenderer extends DefaultTableCellRenderer {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -6578459385224136885L;

    /**
     * The background color for the row header.
     */
    private final Color headerBackground;

    /**
     * The foreground color for the row header.
     */
    private final Color headerForeground;

    /**
     * The background to use for unselected cells other than the first column.
     * The default value is {@code null}. Subclasses can assign a different
     * value before to invoke {@code super.getTableCellRendererComponent}.
     */
    protected Color background;

    /**
     * The foreground to use for unselected cells other than the first column.
     * The default value is {@code null}. Subclasses can assign a different
     * value before to invoke {@code super.getTableCellRendererComponent}.
     */
    protected Color foreground;

    /**
     * Constructs a cell renderer.
     *
     * @param locale The locale of the widget which will contain this renderer.
     */
    public LabeledRenderer(final Locale locale) {
        headerBackground = UIManager.getColor("Label.background", locale);
        headerForeground = UIManager.getColor("Label.foreground", locale);
    }

    /**
     * Returns the renderer to use for rendering a cell. A different color
     * will be used for the first column, which is also non-editable.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (column == 0) {
            hasFocus   = false;
            setBackground(isSelected ? null : headerBackground);
            setForeground(isSelected ? null : headerForeground);
        } else {
            setBackground(background);
            setForeground(foreground);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    /**
     * A table cell renderer where values are numbers, and the first column contains labels
     * for the row.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     *
     * @since 3.00
     * @module
     */
    public static class Numeric extends LabeledRenderer {
        /**
         * For cross-version compatibility.
         */
        private static final long serialVersionUID = 7217422489680595777L;

        /**
         * The number format to use.
         */
        private final NumberFormat format;

        /**
         * Constructs a cell renderer.
         *
         * @param locale The locale of the widget which will contain this renderer.
         * @param integer {@code true} for formatting integers, or {@code false} for real numbers.
         */
        public Numeric(final Locale locale, final boolean integer) {
            super(locale);
            format = integer ? NumberFormat.getIntegerInstance(locale) : NumberFormat.getNumberInstance(locale);
            setHorizontalAlignment(RIGHT);
        }

        /**
         * Invoked by {@code getTableCellRendererComponent} then the value needs to be given
         * to the {@code JLabel} which is used as a renderer.
         */
        @Override
        protected void setValue(final Object value) {
            String text = "";
            if (value instanceof Number) {
                text = format.format(((Number) value).doubleValue(),
                        new StringBuffer(), new FieldPosition(0)).append("  ").toString();
            }
            setText(text);
        }
    }
}
