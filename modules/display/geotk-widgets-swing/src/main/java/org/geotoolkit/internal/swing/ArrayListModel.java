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

import javax.swing.AbstractListModel;
import org.apache.sis.util.ArraysExt;


/**
 * An implementation of {@link AbstractListModel} based on an array. The list content can be
 * specified by a call to the {@link #setElements(Object[])} method. The later method detects
 * the changes in the array content and invokes a {@code fireXXX} method with the smallest
 * range of changes that it can compute.
 *
 * @param <E> Type of elements contained in this model.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.11
 *
 * @since 3.11
 * @module
 */
@SuppressWarnings("serial")
public final class ArrayListModel<E> extends AbstractListModel {
    /**
     * The elements in this model, or {@code null} if none.
     */
    private E[] elements;

    /**
     * Creates a new, initially empty, list.
     */
    public ArrayListModel() {
    }

    /**
     * Sets the elements in this list. This method does <strong>not</strong> clone the given array.
     * We allow this shortcut to ourself because this class is not in a public package.
     *
     * @param newElements The new elements of this list, or {@code null} if none.
     */
    public void setElements(E[] newElements) {
        if (newElements != null && newElements.length == 0) {
            newElements = null;
        }
        int lower = 0; // Index of the first element inserted.
        int keept = 0; // Number of old elements which were keept.
        final E[] old = elements;
        if (old != null) {
            int upper = old.length - 1;
            if (newElements != null) {
                int newUpper = newElements.length - 1;
                final int length = Math.min(upper, newUpper);
                while (lower < length && old[lower].equals(newElements[lower])) {
                    lower++;
                }
                while (upper > lower && old[upper].equals(newElements[newUpper])) {
                    upper--;
                    newUpper--;
                }
            }
            /*
             * [lower ... upper] (inclusive) is now the range of lines to remove or to change.
             */
            if (upper >= lower) {
                if (newElements != null && old.length == newElements.length) {
                    elements = newElements;
                    fireContentsChanged(this, lower, upper);
                    return;
                }
                final int numRemoved = (upper - lower) + 1;
                elements = ArraysExt.remove(old, lower, numRemoved);
                fireIntervalRemoved(this, lower, upper);
                keept = old.length - numRemoved;
            }
        }
        elements = newElements;
        if (newElements != null) {
            final int upper = lower + (newElements.length - 1) - keept; // Inclusive
            if (upper >= lower) {
                fireIntervalAdded(this, lower, upper);
            }
        }
    }

    /**
     * Returns the number of elements in this list.
     */
    @Override
    public int getSize() {
        final E[] elements = this.elements;
        return (elements != null) ? elements.length : 0;
    }

    /**
     * Returns the element at the given index.
     *
     * @param index The index of the element to return.
     */
    @Override
    public E getElementAt(final int index) {
        return elements[index];
    }
}
