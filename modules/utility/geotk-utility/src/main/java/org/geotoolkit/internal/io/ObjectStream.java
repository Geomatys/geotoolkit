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
package org.geotoolkit.internal.io;

import java.util.Iterator;
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * A stream over objects read from memory, a file or sent through a network. This interface can be
 * seen as an iterator where the methods may throw {@link IOException} or {@link RemoteException}.
 * The stream is not allowed to returns {@code null} elements, since the {@code null} value
 * indicates the end of stream.
 *
 * {@section Thread safety}
 * It is implementor responsibility to ensure that {@code ObjectStream} is thread-safe
 * (only the stream; the returned objects don't need to be thread-safe). Thread-safety
 * is a requirement because a stream can be wrapped in RMI objects, in which case many
 * calls from different remote machines may happen simultaneously.
 *
 * {@section Serialization}
 * {@code ObjectStream} implementations are usually not {@linkplain Serializable serializable} since
 * we don't want to cause the serialization of an entire dataset (collection, file, <i>etc.</i>).
 * When a particular instance of {@code ObjectStream} is serializable, this is usually a remote object
 * exported for use through <cite>Remote Method Invocation</cite> (RMI).
 *
 * @param <E> The type of elements returned by the stream.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public interface ObjectStream<E> extends Closeable, Remote {
    /**
     * Returns the next element, or {@code null} if there is no more element to return. This
     * method combines the functionality of {@link Iterator#hasNext} and {@link Iterator#next}
     * in a single method in order to make synchronization for thread-safety easier.
     *
     * @return The next element to return, or {@code null} if none.
     * @throws IOException If an I/O error occurred.
     */
    E next() throws IOException;

    /**
     * Closes this stream and releases any system resources associated with it. If the
     * stream is already closed then invoking this method <em>is required</em> to have
     * no effect. The later requirement is necessary for thread-safety.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    void close() throws IOException;
}
