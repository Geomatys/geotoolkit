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
package org.geotoolkit.internal.swing;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;


/**
 * A combo box renderer which allows usage of separator. Note that a listener should
 * also be setup on the combo box, in order to unselect the separator if the user
 * select it. Example:
 *
 * {@preformat java
 *     comboBox.addActionListener(new ActionListener() {
 *         private Object oldChoice;
 *
 *         public void actionPerformed(final ActionEvent event) {
 *             final Object newChoice = choices.getSelectedItem();
 *             if (newChoice == SEPARATOR) {
 *                 ((JComboBox) event.getSource()).setSelectedItem(oldChoice);
 *             } else {
 *                 oldChoice = newChoice;
 *             }
 *         }
 *     });
 * }
 *
 * @param <E> The type of elements in the combo box.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 * @module
 */
@SuppressWarnings("serial")
public final class ComboBoxRenderer<E> extends JSeparator implements ListCellRenderer<E> {
    /**
     * The string to use as a separator.
     */
    public static final String SEPARATOR = "SEPARATOR";

    /**
     * The original cell renderer.
     */
    private final ListCellRenderer<? super E> original;

    /**
     * Creates a new renderer.
     *
     * @param original The original cell renderer.
     */
    private ComboBoxRenderer(final ListCellRenderer<? super E> original) {
        this.original = original;
    }

    /**
     * Installs a {@code ComboBoxRenderer} on the given combo box.
     *
     * @param <E> The type of elements in the combo box.
     * @param box The combo box on which to install a renderer.
     */
    public static <E> void install(final JComboBox<E> box) {
        box.setRenderer(new ComboBoxRenderer<>(box.getRenderer()));
    }

    /**
     * Returns the renderer for the given value.
     *
     * @param list         The list being paint.
     * @param value        The value being paint.
     * @param index        The index of the value in the list.
     * @param isSelected   {@code true} if the specified cell was selected.
     * @param cellHasFocus {@code true} if the specified cell has the focus.
     * @return A component whose {@code paint()} method will render the specified value.
     */
    @Override
    public Component getListCellRendererComponent(final JList<? extends E> list, final E value,
            final int index, final boolean isSelected, final boolean cellHasFocus)
    {
        if (value == SEPARATOR) {
            return this;
        }
        return original.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
