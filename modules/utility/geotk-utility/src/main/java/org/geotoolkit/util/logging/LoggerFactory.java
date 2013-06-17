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
package org.geotoolkit.util.logging;

import java.util.logging.Logger;
import net.jcip.annotations.ThreadSafe;


/**
 * A factory for Java {@link Logger} wrapping an other logging framework. This factory is used
 * only when wanting to log to an other framework than Java logging. The {@link #getLogger(String)}
 * method returns some subclass of {@link Logger} (typically {@link LoggerAdapter}) that
 * forward directly all log methods to an other framework.
 *
 * @param <L> The type of loggers used for the implementation backend. This is the type
 *            used by external frameworks like Log4J.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see Logging
 * @see LoggerAdapter
 *
 * @since 2.4
 * @level advanced
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.util.logging.LoggerFactory}.
 */
@ThreadSafe
public abstract class LoggerFactory<L> extends org.apache.sis.util.logging.LoggerFactory<L> {
    /**
     * Creates a new factory.
     *
     * @param loggerClass The class of the wrapped logger.
     */
    protected LoggerFactory(final Class<L> loggerClass) {
        super(loggerClass);
    }
}
