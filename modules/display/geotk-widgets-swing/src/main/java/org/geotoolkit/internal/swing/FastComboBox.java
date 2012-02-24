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

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import org.geotoolkit.lang.Workaround;


/**
 * Workaround for the slow behavior of the default {@link JComboBox#getSelectedIndex()} method.
 * The default Swing implementation iterates over all elements in the {@link ComboBoxModel}.
 * When the value of those elements are deferred (for example because they are fetched from
 * a database), it can slow down the widget a lot.
 * <p>
 * The workaround works only if the {@link ComboBoxModel} implements the {@link Model}
 * interface provided in this class.
 *
 * @param <E> The type of elements in the model.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.12
 *
 * @since 3.12
 * @module
 */
@SuppressWarnings("serial")
@Workaround(library="JDK", version="1.6")
public class FastComboBox<E> extends JComboBox<E> {
    /**
     * Creates a new combo box.
     *
     * @param model The data model.
     */
    public FastComboBox(final Model<E> model) {
        super(model);
    }

    /**
     * Returns the index of the currently selected element.
     */
    @Override
    public int getSelectedIndex() {
        if (dataModel instanceof Model<?>) {
            return ((Model<E>) dataModel).getSelectedIndex();
        }
        return super.getSelectedIndex();
    }

    /**
     * The model which is needed for making {@link FastComboBox} effective.
     *
     * @param <E> The type of elements in the model.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.12
     *
     * @since 3.12
     * @module
     */
    public interface Model<E> extends ComboBoxModel<E> {
        /**
         * Returns the index of the currently selected element.
         *
         * @return The index of the currently selected element, or -1 if none.
         */
        int getSelectedIndex();
    }
}
