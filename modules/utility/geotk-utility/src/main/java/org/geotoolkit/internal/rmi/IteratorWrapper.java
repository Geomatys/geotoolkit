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
package org.geotoolkit.internal.rmi;

import java.util.Iterator;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import net.jcip.annotations.ThreadSafe;

import org.geotoolkit.internal.io.ObjectStream;


/**
 * Wraps an ordinary {@link Iterator} in an {@link ObjectStream}. If the backing
 * {@code Iterator} returns some {@code null} elements, then those elements will
 * be skipped in order to return {@code null} only when the iteration is finished.
 *
 * {@section Exception handling}
 * If an {@code Iterator} method throw an {@link UndeclaredThrowableException} with an
 * {@link IOException} as its cause, then the later will be unwrapped and rethrown.
 *
 * @param <E> The type of elements returned by the stream.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
@ThreadSafe
final class IteratorWrapper<E> implements ObjectStream<E> { // Must NOT implement Serializable
    /**
     * The wrapped iterator.
     */
    private final Iterator<E> iterator;

    /**
     * Creates a new wrapper for the given iterator.
     *
     * @param iterator The iterator to wrap.
     */
    public IteratorWrapper(final Iterator<E> iterator) {
        this.iterator = iterator;
    }

    /**
     * Returns the next element, or {@code null} if there is no more element to return.
     * <p>
     * This method is synchronized. See the thread-safety section in the {@link ObjectStream}
     * javadoc for the rational.
     *
     * @throws IOException If an I/O error occurred.
     */
    @Override
    public synchronized E next() throws IOException {
        try {
            while (iterator.hasNext()) {
                final E next = iterator.next();
                if (next != null) {
                    return next;
                }
            }
        } catch (UndeclaredThrowableException exception) {
            final Throwable cause = exception.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw exception;
        }
        return null;
    }

    /**
     * Closes the stream. This method delegates to the backing iterator
     * if it is {@linkplain Closeable closeable}.
     *
     * @throws IOException If an I/O error occurred.
     */
    @Override
    public synchronized void close() throws IOException {
        if (iterator instanceof Closeable) {
            ((Closeable) iterator).close();
        }
    }
}
