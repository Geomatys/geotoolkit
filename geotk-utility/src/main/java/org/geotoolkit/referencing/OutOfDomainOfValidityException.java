/*
 * Geotoolkit.org - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2014, Geomatys
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.geotoolkit.referencing;


/**
 * Date: 30/09/14
 * Time: 17:56
 *
 * @author Alexis Manin (Geomatys)
 */
public class OutOfDomainOfValidityException extends Exception {
    /**
     * Creates an exception with no cause and no details message.
     */
    public OutOfDomainOfValidityException() {
        super();
    }

    /**
     * Creates an exception with the specified details message.
     *
     * @param message The detail message.
     */
    public OutOfDomainOfValidityException(final String message) {
        super(message);
    }

    /**
     * Creates an exception with the specified cause and no details message.
     *
     * @param cause The cause for this exception.
     */
    public OutOfDomainOfValidityException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates an exception with the specified details message and cause.
     *
     * @param message The detail message.
     * @param cause The cause for this exception.
     */
    public OutOfDomainOfValidityException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
