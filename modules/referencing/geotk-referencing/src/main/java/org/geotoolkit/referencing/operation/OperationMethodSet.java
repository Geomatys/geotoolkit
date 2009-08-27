/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.referencing.operation;

import java.util.Iterator;
import java.util.AbstractSet;
import java.util.NoSuchElementException;

import org.opengis.referencing.operation.SingleOperation;
import org.opengis.referencing.operation.OperationMethod;

import org.geotoolkit.lang.Immutable;


/**
 * An immutable set wrapping an array. This implementation does <strong>not</strong> check
 * if all elements in the array are really unique; we assume that it was already verified
 * by {@link javax.imageio.spi.ServiceRegistry}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 2.0
 * @module
 */
@Immutable
final class OperationMethodSet extends AbstractSet<OperationMethod> {
    /**
     * The providers to iterate over.
     */
    private final MathTransformProvider[] providers;

    /**
     * The operation type we are looking for.
     */
    private final Class<? extends SingleOperation> type;

    /**
     * The size of this set, or -1 if not yet computed.
     */
    private int size = -1;

    /**
     * Constructs a set wrapping the given array.
     */
    public OperationMethodSet(final MathTransformProvider[] providers,
                              final Class<? extends SingleOperation> type)
    {
        this.providers = providers;
        this.type = type;
        if (type == null) {
            // Optimisation for a common case.
            size = providers.length;
        }
    }

    /**
     * Returns an iterator over the elements contained in this set.
     */
    @Override
    public Iterator<OperationMethod> iterator() {
        return new Iter(providers, type);
    }

    /**
     * Returns the number of elements in this set.
     */
    @Override
    public int size() {
        if (size < 0) {
            int c = 0;
            final Iterator<OperationMethod> it = iterator();
            while (it.hasNext()) {
                it.next();
                c++;
            }
            size = c;
        }
        return size;
    }

    /**
     * The iterator implementation.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.00
     */
    private static class Iter implements Iterator<OperationMethod> {
        /**
         * The providers to iterate over.
         */
        private final MathTransformProvider[] providers;

        /**
         * The operation type we are looking for.
         */
        private final Class<? extends SingleOperation> type;

        /**
         * Index of the next element to be returned.
         */
        private int cursor;

        /**
         * Creates a new iterator.
         */
        Iter(final MathTransformProvider[] providers, final Class<? extends SingleOperation> type) {
            this.providers = providers;
            this.type = type;
            skip();
        }

        /**
         * Skips invalid entries, begining at the current index.
         */
        private void skip() {
            if (type != null) {
                Class<? extends SingleOperation> t;
                while (cursor < providers.length) {
                    t = providers[cursor].getOperationType();
                    if (t == null || type.isAssignableFrom(t)) {
                        break;
                    }
                    cursor++;
                }
            }
        }

        /**
         * Checks if there is more elements.
         */
        @Override
        public boolean hasNext() {
            return cursor < providers.length;
        }

        /**
         * Returns the next element.
         */
        @Override
        public OperationMethod next() {
            final int c = cursor;
            if (c < providers.length) {
                cursor++;
                skip();
                return providers[c];
            }
            throw new NoSuchElementException();
        }

        /**
         * Always throws an exception, since this set is immutable.
         */
        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }
}
