/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.util.logging;

import java.util.logging.Logger;


/**
 * Redirects logging to two loggers. This is used only when more than one {@link LoggerFactory}
 * is found on the classpath. This should never happen, but if it happen anyway we will send the
 * log records to all registered loggers in order to have a behavior slightly more determinist
 * than picking an arbitrary logger.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
final class DualLoggerFactory extends LoggerFactory<DualLogger> {
    /**
     * The factories of loggers on which to delegate logging events.
     */
    private final LoggerFactory<?> first, second;

    /**
     * Creates a new factory which will delegate the logging events to the loggers
     * created by the two given factories.
     */
    DualLoggerFactory(final LoggerFactory<?> first, final LoggerFactory<?> second) {
        super(DualLogger.class);
        this.first  = first;
        this.second = second;
    }

    /**
     * Returns the implementation to use for the logger of the specified name,
     * or {@code null} if the logger would delegates to Java logging anyway.
     */
    @Override
    protected DualLogger getImplementation(final String name) {
        return new DualLogger(name, first.getLogger(name), second.getLogger(name));
    }

    /**
     * Wraps the specified {@linkplain #getImplementation implementation} in a Java logger.
     */
    @Override
    protected Logger wrap(final String name, final DualLogger implementation) {
        return implementation;
    }

    /**
     * Returns the {@linkplain #getImplementation implementation} wrapped by the specified logger,
     * or {@code null} if none.
     */
    @Override
    protected DualLogger unwrap(final Logger logger) {
        if (logger instanceof DualLogger) {
            return (DualLogger) logger;
        }
        return null;
    }
}
