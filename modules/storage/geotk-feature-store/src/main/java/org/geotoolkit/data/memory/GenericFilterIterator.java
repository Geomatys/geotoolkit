/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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

package org.geotoolkit.data.memory;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.sis.util.Classes;
import org.opengis.filter.Filter;

/**
 * Basic support for a Iterator that filter objects based on the given filter.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
class GenericFilterIterator<F> implements Iterator<F> {

    protected final Iterator<F> iterator;
    protected final Filter filter;
    protected F next = null;

    /**
     * Creates a new instance of GenericFilterIterator
     *
     * @param iterator to filter
     * @param Filter filter
     */
    private GenericFilterIterator(final Iterator<F> iterator, final Filter filter) {
        this.iterator = iterator;
        this.filter = filter;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public F next() {
        if (hasNext()) {
            // hasNext() ensures that next != null
            final F f = next;
            next = null;
            return f;
        } else {
            throw new NoSuchElementException("No such Feature exsists");
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() {
        if (next != null) {
            return true;
        }

        F peek;
        while (iterator.hasNext()) {
            peek = iterator.next();

            if (filter.evaluate(peek)) {
                next = peek;
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void remove() {
        iterator.remove();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(Classes.getShortClassName(this));
        sb.append("[Filter=").append(filter).append("]\n");
        String subIterator = "\u2514\u2500\u2500" + iterator.toString(); //move text to the right
        subIterator = subIterator.replaceAll("\n", "\n\u00A0\u00A0\u00A0"); //move text to the right
        sb.append(subIterator);
        return sb.toString();
    }

    /**
     * Wrap a FeatureIterator with a filter.
     */
    static <F> Iterator<F> wrap(final Iterator<F> reader, final Filter filter){
        return new GenericFilterIterator(reader, filter);
    }

}
