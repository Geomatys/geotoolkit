/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.converter;

import java.util.List;
import java.util.LinkedList;


/**
 * Thrown when an object can not be {@linkplain ObjectConverter#convert converted}
 * from the <cite>source</cite> type the <cite>target</cite> type.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 *
 * @deprecated Replaced by Apache SIS {@link org.apache.sis.util.UnconvertibleObjectException}.
 */
@Deprecated
public class NonconvertibleObjectException extends Exception {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 3434744387048059588L;

    /**
     * If fallbacks were tried, the reasons why each attempt failed.
     * This list is initially {@code null} and filled only if needed.
     */
    private LinkedList<Exception> allAttempts;

    /**
     * Constructs a new exception with no message.
     */
    public NonconvertibleObjectException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message The detail message.
     */
    public NonconvertibleObjectException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause The cause.
     */
    public NonconvertibleObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause The cause.
     */
    public NonconvertibleObjectException(Throwable cause) {
        super(cause);
    }

    /**
     * Adds a fallback failure to the list of failed attempts.
     *
     * @param fallback The failure from the fallback.
     */
    final void add(final NonconvertibleObjectException fallback) {
        if ((allAttempts = fallback.allAttempts) != null) {
            fallback.allAttempts = null;
            allAttempts.addFirst(this);
        } else {
            allAttempts().add(fallback);
        }
    }

    /**
     * Returns the reasons why the conversion failed. The returned list contains typically
     * only one element, which is this exception. But if more than one converter were tried
     * before to give up, the exception thrown by those converters are listed as well.
     * <p>
     * The returned list is modifiable; callers can add additional causes to this list.
     *
     * @return The reasons why each attempt failed.
     */
    public final List<Exception> allAttempts() {
        if (allAttempts == null) {
            allAttempts = new LinkedList<>();
            allAttempts.add(this);
        }
        return allAttempts;
    }
}
