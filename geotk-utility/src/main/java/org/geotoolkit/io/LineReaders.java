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
package org.geotoolkit.io;

import java.io.Reader;
import java.io.DataInput;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Closeable;
import java.util.Arrays;
import java.util.Iterator;

import org.geotoolkit.lang.Static;
import org.apache.sis.util.CharSequences;


/**
 * Provides implementations of {@link LineReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.02
 * @module
 */
public final class LineReaders extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private LineReaders() {
    }

    /**
     * Wraps the given multi-lines text in a {@code LineReader}. Each occurrence of {@code '\r'},
     * {@code '\n'} or {@code "\r\n"} in the text mark an <cite>End Of Line</cite> (EOL).
     *
     * @param text The multi-lines text to wrap.
     * @return A line reader for each line in the given text.
     */
    public static LineReader wrap(final String text) {
        return wrap(Arrays.asList(CharSequences.splitOnEOL(text)));
    }

    /**
     * Wraps the given collection in a {@code LineReader}. Each element will be converted
     * to a line by a call to {@link Object#toString()}. Null elements are not allowed.
     * <p>
     * If the {@link Iterator} implements the {@link Closeable} interface, then the
     * {@code LineReader.close()} method will delegate to {@code Iterator.close()}.
     * <p>
     * If any {@link RuntimeException} is thrown during the iteration and the
     * {@linkplain RuntimeException#getCause() exception cause} is an instance of
     * {@link IOException}, then the {@code IOException} is unwrapped and rethrown
     * by the {@link LineReader#readLine()} method.
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

            @Override public void close() throws IOException {
                if (it instanceof Closeable) {
                    ((Closeable) it).close();
                }
            }
        };
    }

    /**
     * Wraps the given {@code DataInput} in a {@code LineReader}. If the given
     * input is already a {@code LineReader}, then it is returned unchanged.
     * <p>
     * If the {@link DataInput} implements the {@link Closeable} interface, then the
     * {@code LineReader.close()} method will delegate to {@code DataInput.close()}.
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

            @Override public void close() throws IOException {
                if (input instanceof Closeable) {
                    ((Closeable) input).close();
                }
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

                @Override public void close() throws IOException {
                    input.close();
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
