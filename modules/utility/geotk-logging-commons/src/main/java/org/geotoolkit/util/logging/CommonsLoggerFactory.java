/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.apache.sis.util.logging.LoggerFactory;


/**
 * A factory for loggers that redirect all Java logging events to the Apache
 * <A HREF="http://jakarta.apache.org/commons/logging/">Commons-logging</A> framework.
 * <p>
 * It is not necessary to use this class directly. The Geotk library will use this factory
 * automatically if the {@code geotk-logging-commons.jar} file is present on the classpath.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 * @module
 */
public class CommonsLoggerFactory extends LoggerFactory<Log> {
    /**
     * Constructs a default factory.
     *
     * @throws NoClassDefFoundError if Apache {@code Log} class was not found on the classpath.
     */
    public CommonsLoggerFactory() throws NoClassDefFoundError {
        super(Log.class);
    }

    /**
     * Returns the name of the logging framework.
     */
    @Override
    public String getName() {
        return "Apache Commons logging";
    }

    /**
     * Returns the implementation to use for the logger of the specified name,
     * or {@code null} if the logger would delegates to Java logging anyway.
     */
    @Override
    protected Log getImplementation(final String name) {
        final Log log = LogFactory.getLog(name);
        if (log instanceof Jdk14Logger) {
            return null;
        }
        return log;
    }

    /**
     * Wraps the specified {@linkplain #getImplementation implementation} in a Java logger.
     */
    @Override
    protected Logger wrap(String name, Log implementation) {
        return new CommonsLogger(name, implementation);
    }

    /**
     * Returns the {@linkplain #getImplementation implementation} wrapped by the specified logger,
     * or {@code null} if none.
     */
    @Override
    protected Log unwrap(final Logger logger) {
        if (logger instanceof CommonsLogger) {
            return ((CommonsLogger) logger).logger;
        }
        return null;
    }
}
