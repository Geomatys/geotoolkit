/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.io;

import java.io.Reader;
import java.io.DataInput;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.geotoolkit.lang.Static;
import org.geotoolkit.internal.StringUtilities;


/**
 * Provides implementations of {@link LineReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 3.02
 * @module
 */
@Static
public final class LineReaders {
    /**
     * Do not allow instantiation of this class.
     */
    private LineReaders() {
    }

    /**
     * Wraps the given multi-lines text in a {@code LineReader}. Each occurence of {@code '\r'},
     * {@code '\n'} or {@code "\r\n"} in the text mark an <cite>End Of Line</cite> (EOL).
     *
     * @param text The multi-lines text to wrap.
     * @return A line reader for each line in the given text.
     */
    public static LineReader wrap(final String text) {
        return wrap(Arrays.asList(StringUtilities.splitLines(text)));
    }

    /**
     * Wraps the given collection in a {@code LineReader}. Each element will be converted
     * to a line by a call to {@link Object#toString()}. Null elements are not allowed.
     * <p>
     * If any {@link RuntimeException} is thrown during the iteration and this exception
     * has an {@link IOException} as its cause, then the {@code IOException} is rethrown
     * by the {@link LineReader#readLine()} method instead than the runtime exception.
     *
     * @param collection The collection to wrap.
     * @return A line reader for each element in the given string.
     */
    public static LineReader wrap(final Iterable<?> collection) {
        final Iterator<?> it = collection.iterator();
        return new LineReader() {
            @Override public String readLine() throws IOException {
                try {
                    return it.hasNext() ? it.next().toString() : null;
                } catch (RuntimeException e) {
                    final Throwable cause = e.getCause();
                    if (cause instanceof IOException) {
                        throw (IOException) cause;
                    }
                    throw e;
                }
            }
        };
    }

    /**
     * Wraps the given {@code DataInput} in a {@code LineReader}. If the given
     * input is already a {@code LineReader}, then it is returned unchanged.
     *
     * @param input The input to wrap.
     * @return The wrapped input.
     */
    public static LineReader wrap(final DataInput input) {
        if (input instanceof LineReader) {
            return ((LineReader) input);
        }
        return new LineReader() {
            @Override public String readLine() throws IOException {
                return input.readLine();
            }
        };
    }

    /**
     * Wraps the given {@code Reader} in a {@code LineReader}. If the given
     * reader is already a {@code LineReader}, then it is returned unchanged.
     *
     * @param reader The reader to wrap.
     * @return The wrapped reader.
     */
    public static LineReader wrap(final Reader reader) {
        if (reader instanceof LineReader) {
            return ((LineReader) reader);
        }
        if (reader instanceof BufferedReader) {
            final BufferedReader input = (BufferedReader) reader;
            return new LineReader() {
                @Override public String readLine() throws IOException {
                    return input.readLine();
                }
            };
        }
        return new Buffered(reader);
    }

    /**
     * A {@link BufferedReader} which implement the {@link LineReader} interface.
     *
     * @author Martin Desruisseaux (Geomatys)
     * @version 3.02
     *
     * @since 3.02
     * @module
     */
    private static final class Buffered extends BufferedReader implements LineReader {
        Buffered(final Reader in) {
            super(in);
        }
    }
}
