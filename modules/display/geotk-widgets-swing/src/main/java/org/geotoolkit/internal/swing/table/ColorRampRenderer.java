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
package org.geotoolkit.internal.swing.table;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.io.Serializable;
import java.io.IOException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JList;
import javax.swing.DefaultListCellRenderer;

import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.internal.image.ColorRamp;
import org.geotoolkit.internal.image.ColorUtilities;


/**
 * The cell renderer for palette names in a table or a combo box.
 * This renderer can paint any of the following:
 * <p>
 * <ul>
 *   <li>A {@link String} as an ordinary label.</li>
 *   <li>A {@link Gradiant} as the name of a color palette. The colors are fetched
 *       using the {@link PaletteFactory} given at construction time.</li>
 *   <li>A uniform (usually opaque) {@link Color}.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @since 3.13
 * @module
 */
@SuppressWarnings("serial")
public final class ColorRampRenderer extends DefaultListCellRenderer {
    /**
     * The name of a color palette. The ARGB colors are loaded when first needed.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.13
     *
     * @since 3.13
     * @module
     */
    public static final class Gradiant implements Serializable {
        /**
         * The name of the color palette.
         */
        private final String paletteName;

        /**
         * The ARGB colors, created when first needeD.
         */
        private transient int[] ARGB;

        /**
         * Creates a new {@code Gradiant} for the given color palette name.
         *
         * @param paletteName The color palette name.
         */
        public Gradiant(final String paletteName) {
            this.paletteName = paletteName;
        }

        /**
         * Returns the ARGB colors. This method loads the colors when first needed.
         *
         * @param  factory The factory to use for loading the colors.
         * @return The ARGB codes for the palette name given at construction time.
         */
        public int[] getColors(final PaletteFactory factory) {
            if (ARGB == null) {
                Color[] colors;
                try {
                    colors = factory.getColors(paletteName);
                } catch (IOException e) {
                    final LogRecord record = new LogRecord(Level.WARNING, e.getLocalizedMessage());
                    record.setSourceClassName(PaletteFactory.class.getName());
                    record.setSourceMethodName("getColors");
                    Logging.log(PaletteFactory.class, record);
                    colors = new Color[] {Color.BLACK, Color.WHITE};
                }
                ARGB = new int[Math.max(colors.length, 64)];
                ColorUtilities.expand(colors, ARGB, 0, ARGB.length);
            }
            return ARGB;
        }

        /**
         * Returns the name of the color palette.
         */
        @Override
        public String toString() {
            return paletteName;
        }
    }

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
     * Also used in order to determine if the object to be rendered is a label, a gradiant
     * or an opaque color.
     */
    private transient Object currentSelection;

    /**
     * Creates a new list for the given factory.
     *
     * @param factory The factory to use for fetching color palettes from their name.
     */
    public ColorRampRenderer(final PaletteFactory factory) {
        this.factory = factory;
        painter = new ColorRamp();
        painter.labelVisibles = false;
        setPreferredSize(new Dimension(100, 20));
        setHorizontalAlignment(CENTER);
    }

    /**
     * Configures this component for rendering the given value. If the value is an instance of
     * {@link Gradiant}, then this method will loads the colors using the palette factory when
     * first needed. If the colors can not be loaded, then a warning message is logged and a
     * gray scale color palette is used.
     *
     * @param value The item to be rendered.
     */
    @Override
    public Component getListCellRendererComponent(final JList list, final Object value,
            final int index, final boolean isSelected, final boolean cellHasFocus)
    {
        final Object oldValue = currentSelection;
        currentSelection = value;
        final boolean isGradiant = (value instanceof Gradiant);
        if (isGradiant || value instanceof Color) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            if (!value.equals(oldValue)) {
                setText(null);
                final int[] ARGB;
                if (isGradiant) {
                    ARGB = ((Gradiant) value).getColors(factory);
                } else {
                    ARGB = new int[] {((Color) value).getRGB()};
                }
                painter.setColors(ARGB);
            }
            return this;
        } else {
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
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
        if (!(currentSelection instanceof String) && painter.hasColors()) {
            final Rectangle bounds = getBounds();
            bounds.x = 6;
            bounds.y = 3;
            bounds.width  -= 12;
            bounds.height -= 6;
            painter.paint((Graphics2D) graphics, bounds, getFont(), getForeground());
        }
    }
}
