/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

import java.io.Serializable;
import java.io.ObjectStreamException;
import org.geotoolkit.internal.io.ObjectStream;


/**
 * A stream which contains no element.
 *
 * @param <E> The type of elements returned by the stream.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
final class EmptyStream<E> implements ObjectStream<E>, Serializable {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -4799666676230159495L;

    /**
     * The singleton instance
     */
    private static final EmptyStream INSTANCE = new EmptyStream();

    /**
     * Returns the iterator over an empty collection.
     *
     * @param <E> The type of elements (ignored).
     * @return The iterator over an empty collection.
     */
    @SuppressWarnings("unchecked")
    public static <E> ObjectStream<E> instance() {
        return INSTANCE;
    }

    /**
     * Do not allow instantiation of this class, except for the singleton.
     */
    private EmptyStream() {
    }

    /**
     * Returns {@code null} if all case.
     */
    @Override
    public E next() {
        return null;
    }

    /**
     * Nothing to do for an empty collection.
     */
    @Override
    public void close() {
    }

    /**
     * Invoked on deserialization.
     * Replaces the deserialized instance by the unique one.
     */
    Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }
}
