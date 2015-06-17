/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.resources.Errors;


/**
 * Thrown when {@link FactoryRegistry} is invoked recursively for the same category. This exception
 * is often the result of a programming error. It happen typically when an implementation of some
 * {@code FooFactory} interface queries in their constructor, directly or indirectly,
 * {@link FactoryRegistry#getServiceProvider getServiceProvider} for the same category (namely
 * {@code FooFactory.class}). Factories implemented as wrappers around other factories of the same
 * kind are the most likely to fall in this canvas. If this {@code RecursiveSearchException}
 * was not throw, the application would typically dies with a {@link StackOverflowError}.
 * <p>
 * A workaround for this exception is to invoke {@code getServiceProvider} outside the constuctor,
 * when a method first need it.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.3
 * @level advanced
 * @module
 */
public class RecursiveSearchException extends FactoryRegistryException {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -2028654588882874110L;

    /**
     * Creates a new exception with the specified detail message.
     *
     * @param message The details message, or {@code null}.
     */
    public RecursiveSearchException(final String message) {
        super(message);
    }

    /**
     * Creates a new exception with a default message determined from the specified category.
     *
     * @param category The category for which a recursive call is detected.
     */
    public RecursiveSearchException(final Class<?> category) {
        super(Errors.format(Errors.Keys.RecursiveCall_1, category));
    }
}
