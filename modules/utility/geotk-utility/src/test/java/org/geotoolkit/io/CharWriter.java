/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.io;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;


/**
 * Delegates calls to {@code write(String)} toward {@code write(char[])}. Also delegates calls
 * to {@code write(char[])} toward {@code write(char)}. Used in order to explore more code paths
 * in writer implementations.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public class CharWriter extends FilterWriter {
    /**
     * Constructs a {@code CharWriter} object delegating to the given writer.
     *
     * @param  out A writer object to provide the underlying stream.
     */
    public CharWriter(final Writer out) {
        super(out);
    }

    /**
     * Writes a string.
     *
     * @param  string  String to be written.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void write(final String string) throws IOException {
        synchronized (lock) {
            out.write(string.toCharArray());
        }
    }

    /**
     * Writes a portion of a string.
     *
     * @param  string  String to be written.
     * @param  offset  Offset from which to start reading characters.
     * @param  length  Number of characters to be written.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void write(final String string, int offset, int length) throws IOException {
        synchronized (lock) {
            out.write(string.toCharArray(), offset, length);
        }
    }

    /**
     * Writes a portion of an array of characters.
     *
     * @param  cbuf    Buffer of characters to be written.
     * @param  offset  Offset from which to start reading characters.
     * @param  length  Number of characters to be written.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void write(final char cbuf[], int offset, int length) throws IOException {
        synchronized (lock) {
            while (--length >= 0) {
                out.write(cbuf[offset++]);
            }
        }
    }
}
