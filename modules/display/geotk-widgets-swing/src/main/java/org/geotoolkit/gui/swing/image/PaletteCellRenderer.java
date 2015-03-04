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
package org.geotoolkit.gui.swing.image;

import java.util.Locale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.ComboBoxModel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JComponent;
import javax.swing.DefaultListCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.internal.swing.ColorRamp;
import org.geotoolkit.internal.coverage.ColorPalette;


/**
 * The cell renderer for palette names in a table or a combo box.
 * This renderer can paint any of the following:
 * <p>
 * <ul>
 *   <li>A {@link String} as an ordinary label.</li>
 *   <li>A {@link ColorPalette} as the name of a color palette. The colors are fetched
 *       using the {@link PaletteFactory} given at construction time.</li>
 *   <li>A uniform (usually opaque) {@link Color}.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.14 (derived from 3.13)
 * @module
 */
@SuppressWarnings("serial")
final class PaletteCellRenderer extends DefaultListCellRenderer implements TableCellRenderer {
    /**
     * The factory to use for fetching the palette colors.
     */
    private final PaletteFactory factory;

    /**
     * The object on which to delegate the paint operations.
     */
    private final ColorRamp painter;

    /**
     * The currently selected item. Used in order to avoid setting the color palette twice.
     */
    private transient Object currentSelection;

    /**
     * {@code true} if this renderer should paint as a color ramp, or {@code false}
     * to paint as a label.
     */
    private transient boolean paintColors;

    /**
     * The available choices of colors. Shall contain instances of {@link ColorPalette}
     * or {@link Color}. Other types (especially {@link String}) will be ignored.
     */
    private final ComboBoxModel<?> choices;

    /**
     * Creates a new list for the given factory.
     *
     * @param choices The available choices of colors.
     * @param factory The factory to use for fetching color palettes from their name.
     * @param locale  The locale to use for formatting error messages and graduation labels.
     */
    public PaletteCellRenderer(final ComboBoxModel<?> choices, final PaletteFactory factory, final Locale locale) {
        this.choices = choices;
        this.factory = factory;
        painter = new ColorRamp();
        painter.setLocale(locale);
        painter.labelVisibles = false;
        setPreferredSize(new Dimension(100, 20));
        setHorizontalAlignment(CENTER);
    }

    /**
     * Configures a table cell for rendering the given value. This method first converts the
     * given value (usually a {@link String}) to a {@link Color} or {@link ColorPalette}
     * value.
     *
     * @return Always {@code this}.
     */
    @Override
    public Component getTableCellRendererComponent(final JTable table, Object value,
            final boolean isSelected, final boolean hasFocus, final int row, final int column)
    {
        if (value != null) {
            final String name = value.toString().trim();
            if (name.startsWith("#")) try {
                value = Color.decode(name);
            } catch (NumberFormatException e) {
                // Ignore: we will format the name as a string.
            } else {
                int i = choices.getSize();
                while (--i != 0) {
                    final Object candidate = choices.getElementAt(i);
                    if (candidate instanceof ColorPalette && name.equals(((ColorPalette) candidate).paletteName)) {
                        value = candidate;
                        break;
                    }
                }
            }
        }
        configure(table, value, isSelected);
        if (!paintColors) {
            // Render a text label.
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            setFont(table.getFont());
            setText((value == null) ? "" : value.toString());
            setEnabled(table.isEnabled());
            setBorder(noFocusBorder);
        }
        return this;
    }

    /**
     * Configures a list cell for rendering the given value.
     *
     * @return Always {@code this}.
     */
    @Override
    public Component getListCellRendererComponent(final JList<?> list, final Object value,
            final int index, final boolean isSelected, final boolean cellHasFocus)
    {
        configure(list, value, isSelected);
        if (paintColors) {
            return this;
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

    /**
     * Configures this component for rendering the given value. If the value is an instance of
     * {@link ColorPalette}, then this method will loads the colors using the palette factory when
     * first needed. If the colors can not be loaded, then a warning message is logged and a
     * gray scale color palette is used.
     * <p>
     * This method sets {@link #paintColors} to {@code true} if the value can be rendered as a
     * color ramp.
     *
     * @param component  The {@link JList} or {@link JTable} instance.
     * @param value The item to be rendered.
     * @param isSelected {@code true} if the item is on the selected row.
     */
    private void configure(final JComponent component, final Object value, final boolean isSelected) {
        final Object oldValue = currentSelection;
        currentSelection = value;
        final boolean isColorPalette = (value instanceof ColorPalette);
        paintColors = isColorPalette || (value instanceof Color);
        if (paintColors) {
            final Color background, foreground;
            if (!isSelected) {
                background = component.getBackground();
                foreground = component.getForeground();
            } else if (component instanceof JList) {
                final JList<?> list = (JList<?>) component;
                background = list.getSelectionBackground();
                foreground = list.getSelectionForeground();
            } else {
                final JTable table = (JTable) component;
                background = table.getSelectionBackground();
                foreground = table.getSelectionForeground();
            }
            setBackground(background);
            setForeground(foreground);
            if (!value.equals(oldValue)) {
                setText(null);
                final Color[] colors;
                if (isColorPalette) {
                    colors = ((ColorPalette) value).getColors(factory);
                } else {
                    colors = new Color[] {(Color) value};
                }
                painter.setColors(colors);
            }
            setBorder(noFocusBorder);
            setEnabled(component.isEnabled());
        }
    }

    /**
     * Paints the color ramp.
     *
     * @param graphics The graphics to use for painting the color palette.
     */
    @Override
    public void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);
        if (paintColors && painter.hasColors()) {
            final Rectangle bounds = getBounds();
            bounds.x = 6;
            bounds.y = 3;
            bounds.width  -= 12;
            bounds.height -= 6;
            painter.paint((Graphics2D) graphics, bounds, getFont(), getForeground());
        }
    }
}
