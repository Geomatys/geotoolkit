/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.metadata;

import org.opengis.util.CodeList;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class MetadataIoException extends Exception {

    /**
     * The exception code.
     */
    private final CodeList exceptionCode;

    /**
     * The reason of the exception.
     */
    private final String locator;

    /**
     * Creates an exception with the specified details message.
     *
     * @param message The detail message.
     */
    public MetadataIoException(final String message) {
        this(message, null );
    }

    /**
     * Creates an exception with the specified details message and the exceptionCode chosen.
     *
     * @param message The detail message.
     * @param exceptionCode The exception code.
     */
    public MetadataIoException(final String message, final CodeList exceptionCode)
    {
        this(message, exceptionCode, null);
    }

    /**
     * Creates an exception with the specified details message, exception code and locator value.
     *
     * @param message The detail message.
     * @param exceptionCode The exception code.
     * @param locator What causes the exception.
     */
    public MetadataIoException(final String message, final CodeList exceptionCode, final String locator) {
        this(message, null, exceptionCode, locator);
    }

    /**
     * Creates an exception with the specified exception cause.
     *
     * @param cause The cause of this exception.
     */
    public MetadataIoException(final Exception cause) {
        this(cause, null);
    }

    /**
     * Creates an exception with the specified exception cause and code.
     *
     * @param cause The cause of this exception.
     * @param exceptionCode The exception code.
     */
    public MetadataIoException(final Exception cause, final CodeList exceptionCode) {
        this(cause, exceptionCode, null);
    }

    /**
     * Creates an exception with the specified exception cause and code, and locator value.
     *
     * @param cause The cause of this exception.
     * @param exceptionCode The exception code.
     * @param locator What causes the exception.
     */
    public MetadataIoException(final Exception cause, final CodeList exceptionCode, final String locator) {
        this(cause.getMessage(), cause, exceptionCode, locator);
    }

    /**
     * Creates an exception with the specified details message and cause.
     *
     * @param message The detail message.
     * @param cause The cause for this exception.
     * @param exceptionCode The exception code.
     */
    public MetadataIoException(final String message, final Exception cause, final CodeList exceptionCode) {
        this(message, cause, exceptionCode, null);
    }

    /**
     * Creates an exception with the specified exception cause and code, and locator value.
     *
     * @param message The detail message.
     * @param cause The cause of this exception.
     * @param exceptionCode The exception code.
     * @param locator What causes the exception.
     */
    public MetadataIoException(final String message, final Exception cause, final CodeList exceptionCode,
                                final String locator)
    {
        super(message, cause);
        this.exceptionCode = exceptionCode;
        this.locator = locator;
    }

    public CodeList getExceptionCode() {
        return exceptionCode;
    }

    public String getLocator() {
        return locator;
    }
}
