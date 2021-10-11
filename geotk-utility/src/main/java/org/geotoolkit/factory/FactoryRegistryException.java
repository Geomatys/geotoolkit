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
package org.geotoolkit.factory;


/**
 * Thrown when a factory can't be found or can't be instantiate.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
public class FactoryRegistryException extends RuntimeException {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = 8483095037433886648L;

    /**
     * Creates a new exception with the specified detail message.
     *
     * @param message The details message, or {@code null}.
     */
    public FactoryRegistryException(final String message) {
        super(message);
    }

    /**
     * Creates a new exception with the specified detail message and cause.
     *
     * @param message The details message, or {@code null}.
     * @param cause The cause, or {@code null}.
     */
    public FactoryRegistryException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
