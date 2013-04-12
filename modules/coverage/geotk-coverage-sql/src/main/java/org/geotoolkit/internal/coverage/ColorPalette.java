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
package org.geotoolkit.internal.coverage;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.image.io.PaletteFactory;
import org.geotoolkit.internal.image.ColorUtilities;

import static org.apache.sis.util.collection.Containers.isNullOrEmpty;



/**
 * The name of a color palette, as an item to be provided in a {@link javax.swing.JComboBox}.
 * The ARGB colors are loaded when first needed and cached for future reuse.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.14 (derived from 3.13)
 * @module
 */
@SuppressWarnings("serial")
public final class ColorPalette implements Serializable {
    /**
     * The name of the color palette.
     */
    public final String paletteName;

    /**
     * The colors, loaded when first needed.
     */
    private transient Color[] colors;

    /**
     * Creates a new {@code ColorPalette} for the given color palette name.
     *
     * @param paletteName The color palette name.
     */
    public ColorPalette(final String paletteName) {
        this.paletteName = paletteName;
    }

    /**
     * Returns the colors. This method loads the colors when first needed.
     *
     * @param  factory The factory to use for loading the colors.
     * @return The colors for the palette name given at construction time.
     */
    public Color[] getColors(final PaletteFactory factory) {
        if (colors == null) {
            try {
                colors = factory.getColors(paletteName);
            } catch (IOException e) {
                Logging.log(PaletteFactory.class, "getColors",
                        new LogRecord(Level.WARNING, e.getLocalizedMessage()));
            }
            if (colors == null) {
                colors = new Color[] {Color.BLACK, Color.WHITE};
            }
        }
        return colors.clone();
    }

    /**
     * Returns a string representation of the color palette, suitable for use in a GUI.
     */
    @Override
    public String toString() {
        return paletteName;
    }

    /**
     * Returns the {@code ColorPalette}s available for the given factory.
     * The palettes are returned in a {@link ComboBoxModel} for usage by
     * {@link org.geotoolkit.gui.swing.image.PaletteComboBox}.
     *
     * @param  factory The factory to use for getting the choices.
     * @return The choices, or {@code null} if none.
     */
    public static ComboBoxModel<ColorPalette> getChoices(final PaletteFactory factory) {
        final Set<String> names = factory.getAvailableNames();
        if (isNullOrEmpty(names)) {
            return null;
        }
        final Vector<ColorPalette> items = new Vector<>(names.size());
        for (final String n : names) {
            items.add(new ColorPalette(n));
        }
        return new DefaultComboBoxModel<>(items);
    }

    /**
     * Finds the name of a palette having the given colors.
     *
     * @param  colors The colors for which to find the palette name.
     * @param  factory The factory to use for fetching the colors from their name,
     *         or {@code null} for the {@linkplain PaletteFactory#getDefault() default one}.
     * @param  choices A list of palettes to use for inferring the palettes names,
     *         or {@code null} if none. This can be used for the common case where
     *         this list is already available from the {@link SampleDimensionPanel} GUI.
     * @return The name of the color palette, or {@code null} if no match were found or
     *         if the color is fully transparent.
     */
    public static String findName(final Color[] colors, ComboBoxModel<ColorPalette> choices, PaletteFactory factory) {
        /*
         * Determines the palette name or RGB code. In the simplest
         * case, we have a single Color object to format as "#RRGGBB".
         */
        if (colors.length == 1) {
            final Color singleton = colors[0];
            if (singleton != null && singleton.getAlpha() != 0) {
                return ColorUtilities.toString(singleton);
            }
        } else if (colors.length != 0) {
            /*
             * More complex case. Compares the colors against every palettes
             * defined in the given list. We will create the list ourself if
             * it was not explicitly provided.
             */
            if (factory == null) {
                factory = PaletteFactory.getDefault();
            }
            if (choices == null) {
                choices = getChoices(factory);
            }
            int index = choices.getSize();
            while (--index >= 0) {
                final ColorPalette candidate = choices.getElementAt(index);
                if (Arrays.equals(colors, candidate.getColors(factory))) {
                    return candidate.paletteName;
                }
            }
        }
        return null;
    }
}
