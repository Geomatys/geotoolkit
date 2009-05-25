/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.test;

import java.util.Iterator;


/**
 * An iterator over the lines of a multilines string.
 * This iterator is insensitive to the EOL style (Windows or Unix).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
final class StringIterator implements Iterator<String> {
    /**
     * The multilines text to parse.
     */
    private final String text;

    /**
     * Begining of next line to returns.
     */
    private int next;

    /**
     * Creates an iterator for the given multilines text.
     *
     * @param text The multilines text to parse.
     */
    public StringIterator(final String text) {
        this.text = text;
    }

    /**
     * Returns {@code true} if there is more line to return.
     *
     * @return {@code true} if there is more line to return.
     */
    @Override
    public boolean hasNext() {
        return next < text.length();
    }

    /**
     * Returns the next line, without its ending EOL characters.
     *
     * @return The next line.
     */
    @Override
    @SuppressWarnings("fallthrough")
    public String next() {
        final int length = text.length();
        final int lower = next;
        int upper = next;
search: while (upper < length) {
            switch (text.charAt(upper)) {
                case '\r': {
                    if (++next == length || text.charAt(next) != '\n') {
                        break search;
                    }
                    // Fall through
                }
                case '\n': {
                    next++;
                    break search;
                }
            }
            next = ++upper;
        }
        return text.substring(lower, upper);
    }

    /**
     * Unsupported operation.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
