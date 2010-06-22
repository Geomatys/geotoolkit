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
package org.geotoolkit.gui.swing.image;

import java.util.Set;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.internal.swing.table.ColorRampRenderer;


/**
 * A combo box for selecting a color {@linkplain Palette palette}. The choices of available
 * palettes is provided by a {@linkplain PaletteFactory palette factory} given to the constructor.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @since 3.13
 * @module
 */
@SuppressWarnings("serial")
public class PaletteComboBox extends JComponent {
    /**
     * The combo box providing color palette choices.
     */
    private final JComboBox comboBox;

    /**
     * Creates a new combo box using the
     * {@linkplain PaletteFactory#getDefault() default palette factory}.
     */
    public PaletteComboBox() {
        this(PaletteFactory.getDefault());
    }

    /**
     * Creates a new combo box using the given palette factory. The combo box content will be
     * initialized to the {@linkplain PaletteFactory#getAvailableNames() available names}
     *
     * @param factory The palette factory to use.
     */
    public PaletteComboBox(final PaletteFactory factory) {
        final Vocabulary resources = Vocabulary.getResources(getLocale());
        Set<String> names = factory.getAvailableNames();
        if (names == null) {
            /*
             * Color palettes can not be found (note that this is not the same than an empty set,
             * which means "no color palette"). For now, assume that we have only the "grayscale"
             * palette. This palette exists in the default Geotk implementation. Even if it does
             * not exist, the current ColorRampRenderer behavior if the color palette is not found
             * is to fallback on a grayscale palette.
             */
            names = Collections.singleton("grayscale");
        }
        final List<Object> items = new ArrayList<Object>(names.size() + 1);
        items.add(resources.getString(Vocabulary.Keys.NONE));
        for (final String name : names) {
            items.add(new ColorRampRenderer.Gradiant(name));
        }
        comboBox = new JComboBox(items.toArray());
        comboBox.setPrototypeDisplayValue("grayscale"); // For preventing pre-rendering of all palettes.
        comboBox.setRenderer(new ColorRampRenderer(factory));
        setLayout(new BorderLayout());
        add(comboBox, BorderLayout.CENTER);
    }

    /**
     * Adds a uniform color (typically opaque) to the list of proposed choices.
     *
     * @param color The uniform color to add.
     */
    public void addColor(final Color color) {
        comboBox.addItem(color);
    }

    /**
     * Adds a default set of colors to the list of proposed choices.
     * The colors added are the constants declared in the {@link Color} class.
     */
    public void addDefaultColors() {
        addColor(Color.WHITE);
        addColor(Color.LIGHT_GRAY);
        addColor(Color.GRAY);
        addColor(Color.DARK_GRAY);
        addColor(Color.BLACK);
        addColor(Color.YELLOW);
        addColor(Color.ORANGE);
        addColor(Color.PINK);
        addColor(Color.MAGENTA);
        addColor(Color.RED);
        addColor(Color.GREEN);
        addColor(Color.CYAN);
        addColor(Color.BLUE);
    }

    /**
     * Returns the name of the currently selected item, or {@code null} if none.
     * <p>
     * <ul>
     *   <li>If the selected item is a color palette, then this method returns the
     *       name of that palette. This is typically one of the values listed in the
     *       {@link PaletteFactory} javadoc.</li>
     *   <li>If the selected item is a uniform color, then this method returns the
     *       {@code '#'} character followed by the hexadecimal code of that color.</li>
     *   <li>Otherwise this method returns {@code null}.</li>
     * </ul>
     *
     * @return The name of the currently selected item, or {@code null} if none.
     */
    public String getSelectedItem() {
        final Object item = comboBox.getSelectedItem();
        if (item instanceof ColorRampRenderer.Gradiant) {
            return item.toString();
        } else if (item instanceof Color) {
            int ARGB = ((Color) item).getRGB();
            final boolean isOpaque = (ARGB & 0xFF000000) == 0xFF000000;
            int size;
            if (isOpaque) {
                ARGB &= 0xFFFFFF;
                size = 6;
            } else {
                size = 8;
            }
            final String code = Integer.toHexString(ARGB).toUpperCase(Locale.US);
            size -= code.length();

            final StringBuilder buffer = new StringBuilder();
            buffer.append('#');
            while (--size >= 0) {
                buffer.append('0');
            }
            return buffer.append(code).toString();
        }
        return null;
    }

    /**
     * Sets the currently selected item by its color code or palette name.
     * <p>
     * <ul>
     *   <li>If the given name is {@code null}, then the "<cite>none</cite>" choice is
     *       selected.</li>
     *   <li>Otherwise if the given name starts with the {@code '#'} character, then the
     *       name is decoded as a color using the {@link Color#decode(String)} method
     *       and the corresponding color is selected.</li>
     *   <li>Otherwise the given name is used as the name of a color palette, and that
     *       palette is selected.</li>
     * </ul>
     * <p>
     * If no color or palette is found for the given name, then this method selects
     * the "<cite>none</cite>" choice (same as if the name is {@code null}).
     *
     * @param name The color code or palette name to select, or {@code null} if none.
     */
    public void setSelectedItem(String name) {
        int index = 0; // Index of the "none" choice.
        if (name != null) {
            boolean asString = true;
            index = comboBox.getItemCount();
            Object toSearch = name = name.trim();
            if (name.startsWith("#")) try {
                toSearch = Color.decode(name);
                asString = false;
            } catch (NumberFormatException e) {
                // Ignore: we will search for the name as a string.
            }
            while (--index != 0) {
                Object candidate = comboBox.getItemAt(index);
                if (asString) {
                    candidate = candidate.toString();
                }
                if (toSearch.equals(candidate)) {
                    break;
                }
            }
        }
        comboBox.setSelectedIndex(index);
    }
}
