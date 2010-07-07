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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.image.io.Palette;
import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.internal.image.ColorUtilities;
import org.geotoolkit.internal.swing.table.ColorRampRenderer;


/**
 * A combo box for selecting a color {@linkplain Palette palette}. The choices of available
 * palettes is provided by a {@linkplain PaletteFactory palette factory} given to the constructor.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.13
 * @module
 */
@SuppressWarnings("serial")
public class PaletteComboBox extends JComponent {
    /**
     * The factory used for loading colors from a palette name.
     */
    private final PaletteFactory factory;

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
     * @param factory The factory to use for loading colors from a palette name.
     */
    public PaletteComboBox(final PaletteFactory factory) {
        this.factory = factory;
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
            return ColorUtilities.toString((Color) item);
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

    /**
     * Returns the colors for the currently selected item, or {@code null} if none.
     *
     * @return The colors of the currently selected item, or {@code null} if none.
     *
     * @since 3.14
     */
    public Color[] getSelectedColors() {
        final Object item = comboBox.getSelectedItem();
        if (item instanceof ColorRampRenderer.Gradiant) {
            return ((ColorRampRenderer.Gradiant) item).getColors(factory);
        } else if (item instanceof Color) {
            return new Color[] {(Color) item};
        }
        return null;
    }

    /**
     * Sets the currently selected item by its color. This method searchs for a choices
     * providing the same colors than the given array. If such choices is found, it is
     * selected. Otherwise this method selects the "<cite>none</cite>" choice.
     *
     * @param colors The colors to select, or {@code null} if none.
     *
     * @since 3.14
     */
    public void setSelectedColors(final Color... colors) {
        int index = 0; // Index of the "none" choice.
        if (colors != null) {
            final Color singleton = (colors.length == 1) ? colors[0] : null;
            index = comboBox.getItemCount();
            while (--index != 0) {
                final Object candidate = comboBox.getItemAt(index);
                if (singleton != null && singleton.equals(candidate)) {
                    break;
                }
                if (candidate instanceof ColorRampRenderer.Gradiant && Arrays.equals(colors,
                        ((ColorRampRenderer.Gradiant) candidate).getColors(factory)))
                {
                    break;
                }
            }
        }
        comboBox.setSelectedIndex(index);
    }
}
