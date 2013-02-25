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
package org.geotoolkit.util;


/**
 * Throws when a null argument has been given to a method that doesn't accept them.
 * This exception extends {@link NullPointerException} in order to stress out that
 * the error is an illegal argument rather than an unexpected usage of a null pointer
 * inside a method body.
 *
 * {@note We could argue that this exception should extend <code>IllegalArgumentException</code>.
 *        However <code>NullPointerException</code> has become a more widely adopted practice and
 *        is now the recommended one in the <cite>Effective Java</cite> book.}
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see ArgumentChecks#ensureNonNull(String, Object)
 *
 * @since 3.00
 * @module
 *
 * @deprecated Replaced by Apache SIS {@link org.apache.sis.util.NullArgumentException}.
 */
@Deprecated
public class NullArgumentException extends org.apache.sis.util.NullArgumentException {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -9191547216229354211L;

    /**
     * Constructs an exception with no detail message.
     */
    public NullArgumentException() {
        super();
    }

    /**
     * Constructs an exception with the specified detail message.
     *
     * @param message The detail message.
     */
    public NullArgumentException(final String message) {
        super(message);
    }
}
