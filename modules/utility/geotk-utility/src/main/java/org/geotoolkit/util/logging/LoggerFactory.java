/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
import org.geotoolkit.lang.ThreadSafe;
import org.geotoolkit.util.collection.WeakValueHashMap;


/**
 * A factory for Java {@link Logger} wrapping an other logging framework. This factory is used
 * only when wanting to log to an other framework than Java logging. The {@link #getLogger}
 * method returns some subclass of {@link Logger} (typicallly {@link LoggerAdapter}) that
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
 */
@ThreadSafe
public abstract class LoggerFactory<L> {
    /**
     * The Apache's <A HREF="http://jakarta.apache.org/commons/logging/">Commons-logging</A> framework.
     * This value can be given to {@link Logging#setLoggerFactory(String)} in order to force explicitly
     * usage of this logging framework. Note that this is usually not needed, since this framework is
     * selected automatically if {@code geotk-logging-commons.jar} is found on the classpath.
     *
     * @see Logging#setLoggerFactory(String)
     *
     * @since 3.00
     */
    public static final String COMMONS_LOGGING = "org.geotoolkit.util.logging.CommonsLoggerFactory";

    /**
     * The <A HREF="http://logging.apache.org/log4j">Log4J</A> framework. This value can be given to
     * {@link Logging#setLoggerFactory(String)} in order to force explicitly usage of this logging
     * framework. Note that this is usually not needed, since this framework is selected automatically
     * if {@code geotk-logging-log4j.jar} is found on the classpath.
     *
     * @see Logging#setLoggerFactory(String)
     *
     * @since 3.00
     */
    public static final String LOG4J = "org.geotoolkit.util.logging.Log4JLoggerFactory";

    /**
     * The logger class. We ask for this information right at construction time in order to
     * force a {@link NoClassDefFoundError} early rather than only the first time a message
     * is logged.
     */
    private final Class<L> loggerClass;

    /**
     * The loggers created up to date.
     */
    private final WeakValueHashMap<String,Logger> loggers;

    /**
     * Creates a new factory.
     *
     * @param loggerClass The class of the wrapped logger.
     */
    protected LoggerFactory(final Class<L> loggerClass) {
        this.loggerClass = loggerClass;
        loggers = new WeakValueHashMap<String,Logger>();
    }

    /**
     * Returns the logger of the specified name, or {@code null}. If this method has already been
     * invoked previously with the same {@code name} argument, then it may returns the same logger
     * provided that:
     * <p>
     * <ul>
     *   <li>the logger has not yet been garbage collected;</li>
     *   <li>the implementation instance (Log4J, SLF4J, <i>etc.</i>) returned by
     *       <code>{@linkplain #getImplementation getImplementation}(name)</code> has
     *       not changed.</li>
     * </ul>
     * <p>
     * Otherwise this method returns a new {@code Logger} instance, or {@code null} if the
     * standard Java logging framework should be used.
     *
     * @param  name The name of the logger.
     * @return The logger, or {@code null}.
     */
    public Logger getLogger(final String name) {
        final L target = getImplementation(name);
        if (target == null) {
            return null;
        }
        synchronized (loggers) {
            Logger logger = loggers.get(name);
            if (logger == null || !target.equals(unwrap(logger))) {
                logger = wrap(name, target);
                loggers.put(name, logger);
            }
            return logger;
        }
    }

    /**
     * Returns the base class of objects to be returned by {@link #getImplementation}. The
     * class depends on the underlying logging framework (Log4J, SLF4J, <i>etc.</i>).
     *
     * @return The type of loggers used for the implementation backend.
     */
    public Class<L> getImplementationClass() {
        return loggerClass;
    }

    /**
     * Returns the implementation to use for the logger of the specified name. The object to be
     * returned depends on the logging framework (Log4J, SLF4J, <i>etc.</i>). If the target
     * framework redirects logging events to Java logging, then this method should returns
     * {@code null} since we should not use wrapper at all.
     *
     * @param  name The name of the logger.
     * @return The logger as an object of the target logging framework (Log4J, SLF4J,
     *         <i>etc.</i>), or {@code null} if the target framework would redirect
     *         to the Java logging framework.
     */
    protected abstract L getImplementation(String name);

    /**
     * Wraps the specified {@linkplain #getImplementation implementation} in a Java logger.
     *
     * @param  name The name of the logger.
     * @param  implementation An implementation returned by {@link #getImplementation}.
     * @return A new logger wrapping the specified implementation.
     */
    protected abstract Logger wrap(String name, L implementation);

    /**
     * Returns the {@linkplain #getImplementation implementation} wrapped by the specified logger,
     * or {@code null} if none. If the specified logger is not an instance of the expected class,
     * then this method should returns {@code null}.
     *
     * @param  logger The logger to test.
     * @return The implementation wrapped by the specified logger, or {@code null} if none.
     */
    protected abstract L unwrap(Logger logger);
}
