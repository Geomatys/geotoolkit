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

import java.util.Set;
import java.util.List;
import java.util.Locale;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.concurrent.Callable;
import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ComboBoxModel;
import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.geotoolkit.image.color.ColorUtilities;

import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.image.palette.Palette;
import org.geotoolkit.image.palette.PaletteFactory;
import org.geotoolkit.image.io.SpatialImageReadParam;
import org.geotoolkit.internal.coverage.ColorPalette;


/**
 * A combo box for selecting a color {@linkplain Palette palette}. The choices of available
 * palettes is provided by a {@linkplain PaletteFactory palette factory} given to the constructor.
 * <p>
 * This combo box can also be used as a cell editor in a {@link JTable},
 * by invoking the {@link #useAsTableCellEditor(TableColumn)} method.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
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
    private final JComboBox<Object> comboBox;

    /**
     * Creates a new combo box using the
     * {@linkplain PaletteFactory#getDefault() default palette factory}.
     */
    public PaletteComboBox() {
        this(null);
    }

    /**
     * Creates a new combo box using the given palette factory. The combo box content will be
     * initialized to the {@linkplain PaletteFactory#getAvailableNames() available names}.
     *
     * @param factory The factory to use for loading colors from a palette name, or
     *        {@code null} for the {@linkplain PaletteFactory#getDefault() default}.
     */
    public PaletteComboBox(PaletteFactory factory) {
        if (factory == null) {
            factory = PaletteFactory.getDefault();
        }
        this.factory = factory;
        final Locale locale = getLocale();
        final Vocabulary resources = Vocabulary.getResources(locale);
        Set<String> names = factory.getAvailableNames();
        if (names == null) {
            /*
             * Color palettes can not be found (note that this is not the same than an empty set,
             * which means "no color palette"). For now, assume that we have only the "grayscale"
             * palette. This palette exists in the default Geotk implementation. Even if it does
             * not exist, the current ColorRampRenderer behavior if the color palette is not found
             * is to fallback on a grayscale palette.
             */
            names = Collections.singleton(SpatialImageReadParam.DEFAULT_PALETTE_NAME);
        }
        final List<Object> items = new ArrayList<>(names.size() + 1);
        items.add(resources.getString(Vocabulary.Keys.NONE));
        for (final String name : names) {
            items.add(new ColorPalette(name));
        }
        comboBox = new JComboBox<>(items.toArray());
        comboBox.setPrototypeDisplayValue(SpatialImageReadParam.DEFAULT_PALETTE_NAME); // For preventing pre-rendering of all palettes.
        comboBox.setRenderer(new PaletteCellRenderer(comboBox.getModel(), factory, locale));
        setLayout(new BorderLayout());
        add(comboBox, BorderLayout.CENTER);
    }

    /**
     * Adds a uniform color (typically opaque) to the list of proposed choices.
     * The color will be inserted before the gradients.
     *
     * @param color The uniform color to add.
     */
    public void addColor(final Color color) {
        final int size = comboBox.getItemCount();
        // The element at index 0 is "none", so we need to skip it.
        for (int i=1; i<size; i++) {
            if (!(comboBox.getItemAt(i) instanceof Color)) {
                comboBox.insertItemAt(color, i);
                return;
            }
        }
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
        if (item instanceof ColorPalette) {
            return ((ColorPalette) item).paletteName;
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
            index = comboBox.getItemCount();
            Object toSearch = name = name.trim();
            if (name.startsWith("#")) try {
                toSearch = Color.decode(name);
            } catch (NumberFormatException e) {
                // Ignore: we will search for the name as a string.
            }
            while (--index != 0) {
                Object candidate = comboBox.getItemAt(index);
                if (candidate instanceof ColorPalette) {
                    candidate = ((ColorPalette) candidate).paletteName;
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
        if (item instanceof ColorPalette) {
            return ((ColorPalette) item).getColors(factory);
        } else if (item instanceof Color) {
            return new Color[] {(Color) item};
        }
        return null;
    }

    /**
     * Sets the currently selected item by its color. This method searches for a choices
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
                if (candidate instanceof ColorPalette) {
                    final ColorPalette cp = (ColorPalette) candidate;
                    if (Arrays.equals(colors, cp.getColors(factory))) {
                        break;
                    }
                }
            }
        }
        comboBox.setSelectedIndex(index);
    }

    /**
     * Uses this {@code PaletteComboBox} as the {@linkplain TableCellEditor table cell editor}
     * and the {@linkplain TableCellRenderer table cell renderer} of the given table column.
     * <p>
     * This {@code PaletteComboBox} instance should not be used for any other purpose after
     * this method call.
     *
     * @param column The table column on which to set this combo box as the editor and renderer.
     *
     * @since 3.14
     */
    public void useAsTableCellEditor(final TableColumn column) {
        // See javax.swing.DefaultCellEditor(JComboBox) source code.
        comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        column.setCellEditor(new CellEditor());
        column.setCellRenderer((TableCellRenderer) comboBox.getRenderer());
    }

    /**
     * A cell editor for using {@link PaletteComboBox} in a {@linkplain javax.swing.JTable}.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.14
     *
     * @since 3.14
     * @module
     */
    @SuppressWarnings("serial")
    private final class CellEditor extends AbstractCellEditor implements TableCellEditor, Callable<ComboBoxModel<Object>> {
        /**
         * Returns {@code true} if the cell can be edited. We require a double click,
         * otherwise the combo box drop down list appears and disappears too often.
         */
        @Override
        public boolean isCellEditable(final EventObject event) {
            return !(event instanceof MouseEvent) || ((MouseEvent) event).getClickCount() >= 2;
        }

        /**
         * Gets the name of the selected palette, or the opaque color code.
         */
        @Override
        public String getCellEditorValue() {
            return PaletteComboBox.this.getSelectedItem();
        }

        /**
         * Configures the {@link PaletteComboBox} to the given value, and returns it.
         */
        @Override
        public Component getTableCellEditorComponent(final JTable table, Object value,
                final boolean isSelected, final int row, final int column)
        {
            PaletteComboBox.this.setSelectedItem((String) value);
            return PaletteComboBox.this;
        }

        /**
         * A trick for allowing {@link org.geotoolkit.gui.swing.coverage.CategoryTable}
         * to get the list of available palettes. Should not be invoked by client code.
         */
        @Override
        public ComboBoxModel<Object> call() {
            return comboBox.getModel();
        }
    }
}
