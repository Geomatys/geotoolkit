/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.gui.swing;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;
import java.lang.reflect.Array;
import javax.swing.table.AbstractTableModel;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.internal.swing.SwingUtilities;


/**
 * A {@linkplain javax.swing.table.TableModel table model} backed by a {@linkplain List list}. The
 * list is given to the constructor and retained by direct reference - it is <strong>not</strong>
 * cloned, because it may contain millions of elements, sometime through a custom {@link List}
 * implementation fetching the information on-the-fly from a database. Consequently if the content
 * of the list is modified externally, then a {@code fireXXX} method (inherited from
 * {@link AbstractTableModel} must be invoked explicitly. Note that those {@code fireXXX} methods
 * don't need to be invoked if the list is modified through the methods provided in this class.
 *
 * {@section Multi-threading}
 * Unless otherwise specified, methods in this class must be invoked from the Swing thread.
 * Exceptions are the {@code getElements} method, which will be automatically executed in
 * the Swing thread no matter the invoker thread. This convenience does <strong>not</strong>
 * apply to methods inherited from the super-class, and may not apply to methods defined in
 * the sub-classes.
 *
 * {@section Serialization}
 * This model is serialiable if the underlying list is serializable.
 *
 * @param <E> The type of elements in the list backing this table model.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @since 3.00
 * @module
 */
public abstract class ListTableModel<E> extends AbstractTableModel {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = 3543567151179489778L;

    /**
     * The type of elements.
     */
    private final Class<E> type;

    /**
     * The list of row elements. This is a direct reference to the list given by the user at
     * construction time, not a clone. If this list is modified externaly, then the appropriate
     * {@code fireXXX} method must be invoked explicitly.
     */
    protected final List<E> elements;

    /**
     * Creates a new table model backed by an {@link ArrayList}.
     *
     * @param type The type of elements in the list.
     */
    protected ListTableModel(final Class<E> type) {
        this.type = type;
        elements = new ArrayList<E>();
    }

    /**
     * Creates a new table model for the given list. The given list is retained by direct
     * reference - it is not cloned. Consequently if the content of this list is modified
     * externaly, then one of the {@code fireXXX} method inherited from {@link AbstractTableModel}
     * must be invoked explicitly.
     *
     * @param type The type of elements in the list.
     * @param elements The list of elements to display in a table.
     */
    protected ListTableModel(final Class<E> type, final List<E> elements) {
        this.type = type;
        this.elements = elements;
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return The number of rows.
     */
    @Override
    public int getRowCount() {
        return elements.size();
    }

    /**
     * Creates an array of the given length.
     */
    @SuppressWarnings("unchecked")
    private E[] createArray(final int length) {
        return (E[]) Array.newInstance(type, length);
    }

    /**
     * For internal use by {@link ListTableModel#getElements}: asks for the elements from
     * the Swing thread. The elements can be asked in three different ways. Only one of
     * those ways can be used at a time.
     */
    private final class Get implements Runnable {
        /** The result to be returned. */ E[] array;
        /** One possible way to query. */ boolean all;
        /** One possible way to query. */ int lower, upper;
        /** One possible way to query. */ int[] selection;

        @Override public void run() {
            List<E> elements = ListTableModel.this.elements;
            int[] selection = this.selection;
            if (selection != null) {
                array = createArray(selection.length);
                for (int i=0; i<selection.length; i++) {
                    array[i] = elements.get(selection[i]);
                }
                return;
            } else if (!all) {
                elements = elements.subList(lower, upper);
            }
            array = elements.toArray(createArray(elements.size()));
        }
    }

    /**
     * Returns a snapshot of every elements contained in this model. This method
     * will be executed in the Swing thread even if invoked from an other thread.
     *
     * @return A snapshot of every elements in this model.
     */
    public E[] getElements() {
        final Get task = new Get();
        task.all = true;
        SwingUtilities.invokeAndWait(task);
        return task.array;
    }

    /**
     * Returns a snapshot of a range of elements contained in this model. This method
     * will be executed in the Swing thread even if invoked from an other thread.
     *
     * @param  lower Index of the first element to be returned.
     * @param  upper Index after the last element to be returned.
     * @return A snapshot of the given range of elements in this model.
     */
    public E[] getElements(final int lower, final int upper) {
        final Get task = new Get();
        task.lower = lower;
        task.upper = upper;
        SwingUtilities.invokeAndWait(task);
        return task.array;
    }

    /**
     * Returns a snapshot of selected elements. This method will be executed
     * in the Swing thread even if invoked from an other thread.
     *
     * @param  selection The indices of selected elements, typically given by
     *         {@link javax.swing.JTable#getSelectedRows()}.
     * @return A snapshot of the selected elements in this model.
     */
    public E[] getElements(final int[] selection) {
        final Get task = new Get();
        task.selection = selection;
        SwingUtilities.invokeAndWait(task);
        return task.array;
    }

    /**
     * Sets the elements. All previous elements are discarded before to add the new ones.
     *
     * @param elts The new elements.
     *
     * @since 3.13
     */
    public void setElements(final E... elts) {
        elements.clear();
        elements.addAll(Arrays.asList(elts));
        fireTableDataChanged();
    }

    /**
     * Adds all elements from the given collection. The default implementation invokes
     * {@link List#addAll(Collection)} and uses the change of {@linkplain List#size list size}
     * for computing the index to be given to the {@link #fireTableRowsInserted(int,int)} method.
     * Consequently the list implementation doesn't need to accept every elements. Note however
     * that the list must not change in a concurrent thread, otherwise the change event to be
     * fired may have an inacurate index range.
     *
     * @param  toAdd The elements to add.
     * @throws UnsupportedOperationException if the underlying {@linkplain #elements} list
     *         is not modifiable.
     */
    public void add(final Collection<? extends E> toAdd) throws UnsupportedOperationException {
        if (!toAdd.isEmpty()) {
            final int insertAt = elements.size();
            if (elements.addAll(toAdd)) {
                final int last = elements.size() - 1;
                if (last >= insertAt) {
                    fireTableRowsInserted(insertAt, last);
                }
            }
        }
    }

    /**
     * Inserts at the given position all elements from the given collection. The elements in this
     * table that are after the insertion point are shifted to larger index.
     * <p>
     * The default implementation invokes {@link List#addAll(int,Collection)} and uses the
     * change of {@linkplain List#size list size} for computing the index to be given to the
     * {@link #fireTableRowsInserted(int,int)} method. Consequently the list implementation
     * doesn't need to accept every elements. Note however that the list must not change in
     * a concurrent thread, otherwise the change event to be fired may have an inacurate index
     * range.
     *
     * @param  insertAt The insertion point. The first element will be inserted at that position.
     * @param  toAdd The elements to add.
     * @throws UnsupportedOperationException if the underlying {@linkplain #elements} list
     *         is not modifiable.
     * @throws IndexOutOfBoundsException if the given index is out of range.
     */
    public void insert(final int insertAt, final Collection<? extends E> toAdd)
            throws UnsupportedOperationException, IndexOutOfBoundsException
    {
        if (!toAdd.isEmpty()) {
            int count = elements.size();
            if (elements.addAll(insertAt, toAdd)) {
                count = elements.size() - count;
                if (count > 0) {
                    fireTableRowsInserted(insertAt, insertAt + count - 1);
                }
            }
        }
    }

    /**
     * Removes a range of rows (or elements). Note that the upper value is inclusive,
     * which is different than the <cite>Java Collection</cite> usage but consistent
     * with the <cite>Swing</cite> usage.
     *
     * @param  lower Index of the first row to remove, inclusive.
     * @param  upper Index of the last row to remove, <strong>inclusive</strong>.
     * @throws UnsupportedOperationException if the underlying {@linkplain #elements} list
     *         is not modifiable.
     */
    public void remove(final int lower, final int upper) throws UnsupportedOperationException {
        if (lower == upper) {
            elements.remove(lower);
        } else {
            elements.subList(lower, upper+1).clear();
        }
        fireTableRowsDeleted(lower, upper);
    }

    /**
     * Removes the given elements from the list. The default implementation delegates the work
     * to {@link #remove(int,int)}. Consecutive indexes will be removed in a single call of the
     * above method.
     *
     * @param  indices The index of elements to remove.
     * @throws UnsupportedOperationException if the underlying {@linkplain #elements} list
     *         is not modifiable.
     */
    public void remove(int[] indices) throws UnsupportedOperationException {
        // We must iterate in reverse order, because the
        // index after the removed elements will change.
        int i = indices.length;
        if (i != 0) {
            if (!ArraysExt.isSorted(indices, false)) {
                indices = indices.clone();
                Arrays.sort(indices);
            }
            int upper = indices[--i];
            int lower = upper;
            while (i != 0) {
                int previous = indices[--i];
                if (previous != lower - 1) {
                    remove(lower, upper); // Reminder: upper is inclusive
                    upper = previous;
                }
                lower = previous;
            }
            remove(lower, upper); // Reminder: upper is inclusive
        }
    }

    /**
     * Removes duplicated elements. If two elements are equal in the sense of the
     * {@link Object#equals(Object)} method, then the first one is removed from the
     * {@linkplain #elements} list. We keep the last element instead of the first one
     * on the assumption that the last element is the most recently added.
     *
     * @return The number of duplicates which have been found.
     * @throws UnsupportedOperationException if the underlying {@linkplain #elements} list
     *         is not modifiable.
     */
    public int removeDuplicates() throws UnsupportedOperationException {
        int count = 0;
        final int[] indices = new int[elements.size()];
        final Map<E,Integer> previous = new HashMap<E,Integer>();
        final ListIterator<E> it = elements.listIterator();
        while (it.hasNext()) {
            E element = it.next();
            final Integer p = previous.put(element, it.previousIndex());
            if (p != null) {
                indices[count++] = p;
            }
        }
        if (count != 0) {
            remove(ArraysExt.resize(indices, count));
        }
        return count;
    }

    /**
     * Sorts the elements in their natural order.
     *
     * @return {@code true} if the row order changed as a result of this method call.
     * @throws UnsupportedOperationException if the underlying {@linkplain #elements} list
     *         is not modifiable.
     * @throws ClassCastException if the list contains elements that are not
     *         <cite>mutually comparable</cite>.
     */
    public boolean sort() throws UnsupportedOperationException, ClassCastException {
        boolean changed = false;
        if (!elements.isEmpty()) {
            final E[] array = elements.toArray(createArray(elements.size()));
            Arrays.sort(array);
            final ListIterator<E> it = elements.listIterator();
            for (int i=0; i<array.length; i++) {
                final E t = array[i];
                if (it.next() != t) {
                    it.set(t);
                    changed = true;
                }
            }
            if (changed) {
                fireTableRowsUpdated(0, elements.size() - 1);
            }
        }
        return changed;
    }
}
