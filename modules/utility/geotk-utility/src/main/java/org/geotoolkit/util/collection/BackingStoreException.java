/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.collection;

import java.io.IOException;
import java.sql.SQLException;


/**
 * Thrown to indicate that an operation could not complete because of a failure in the backing
 * store (a file or a database). This exception is thrown by collection implementations that are
 * not allowed to throw checked exceptions. This exception usually has an {@link IOException} or
 * a {@link SQLException} as its {@linkplain #getCause cause}.
 * <p>
 * This method provides a {@link #unwrapOrRethrow(Class)} convenience method which can be used
 * for rethrowing the cause as in the example below. This allows client code to behave as if a
 * {@link java.util.Collection} interface was allowed to declare checked exceptions.
 *
 * {@preformat java
 *     void myMethod() throws IOException {
 *         Collection c = ...;
 *         try {
 *             c.doSomeStuff();
 *         } catch (BackingStoreException e) {
 *             throw e.unwrapOrRethrow(IOException.class);
 *         }
 *     }
 * }
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.09
 *
 * @since 3.09 (derived from 2.3)
 * @module
 */
public class BackingStoreException extends RuntimeException {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -1714319767053628606L;

    /**
     * Constructs a new exception with no detail message.
     */
    public BackingStoreException() {
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message, saved for later retrieval by the {@link #getMessage()} method.
     */
    public BackingStoreException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause, saved for later retrieval by the {@link #getCause()} method.
     */
    public BackingStoreException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message, saved for later retrieval by the {@link #getMessage()} method.
     * @param cause the cause, saved for later retrieval by the {@link #getCause()} method.
     */
    public BackingStoreException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns the underlying {@linkplain #getCause() cause} as an exception of the given type,
     * or rethrow the exception. More specifically, this method makes the following choices:
     * <p>
     * <ul>
     *   <li>If the cause {@linkplain Class#isInstance(Object) is an instance} of the given
     *       type, returns the cause.</li>
     *   <li>Otherwise if the cause is an instance of {@link RuntimeException}, throws
     *       that exception.</li>
     *   <li>Otherwise rethrows {@code this}.</li>
     * </ul>
     * <p>
     * This method should be used as in the example below:
     *
     * {@preformat java
     *     void myMethod() throws IOException {
     *         Collection c = ...;
     *         try {
     *             c.doSomeStuff();
     *         } catch (BackingStoreException e) {
     *             throw e.unwrapOrRethrow(IOException.class);
     *         }
     *     }
     * }
     *
     * @param  <E>  The type of the exception to unwrap.
     * @param  type The type of the exception to unwrap.
     * @return The cause as an exception of the given type (never {@code null}).
     * @throws RuntimeException If the cause is an instance of {@code RuntimeException},
     *         in which case that instance is rethrown.
     * @throws BackingStoreException if the cause is neither the given type or an instance
     *         of {@link RuntimeException}, in which case {@code this} exception is rethrown.
     */
    @SuppressWarnings("unchecked")
    public <E extends Exception> E unwrapOrRethrow(final Class<E> type)
            throws RuntimeException, BackingStoreException
    {
        final Throwable cause = getCause();
        if (type.isInstance(cause)) {
            return (E) cause;
        } else if (cause instanceof RuntimeException) {
            throw (RuntimeException) cause;
        } else {
            throw this;
        }
    }
}
