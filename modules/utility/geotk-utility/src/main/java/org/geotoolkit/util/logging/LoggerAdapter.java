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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

import org.geotoolkit.lang.Debug;


/**
 * An adapter that redirect all Java logging events to an other logging framework. This
 * class redefines the {@link #severe(String) severe}, {@link #warning(String) warning},
 * {@link #info(String) info}, {@link #config(String) config}, {@link #fine(String) fine},
 * {@link #finer(String) finer} and {@link #finest(String) finest} methods as <em>abstract</em>
 * ones. Subclasses should implement those methods in order to map Java logging levels to
 * the backend logging framework.
 * <p>
 * All {@link #log(Level,String) log} methods are overridden in order to redirect to one of the
 * above-cited methods. Note that this is the opposite approach than the Java logging framework
 * one, which implements everything on top of {@link Logger#log(LogRecord)}. This adapter is
 * defined in terms of {@link #severe(String) severe} &hellip; {@link #finest(String) finest}
 * methods instead because external frameworks like
 * <a href="http://commons.apache.org/logging/">Commons-logging</a>
 * don't work with {@link LogRecord}, and sometime provides nothing else than convenience methods
 * equivalent to {@link #severe(String) severe} &hellip; {@link #finest(String) finest}.
 *
 * {@section Restrictions}
 * Because the configuration is expected to be fully controlled by the external logging
 * framework, every configuration methods inherited from {@link Logger} are disabled:
 * <p>
 * <ul>
 *   <li>{@link #addHandler}
 *       since the handling is performed by the external framework.</li>
 *
 *   <li>{@link #setUseParentHandlers}
 *       since this adapter never delegates to the parent handlers. This is consistent with the
 *       previous item and avoid mixing loggings from the external framework with Java loggings.</li>
 *
 *   <li>{@link #setParent}
 *       since this adapter should not inherits any configuration from a parent logger using the
 *       Java logging framework.</li>
 *
 *   <li>{@link #setFilter}
 *       for keeping this {@code LoggerAdapter} simple.</li>
 * </ul>
 * <p>
 * Since {@code LoggerAdapter}s do not hold any configuration by themselves, it is not strictly
 * necessary to {@linkplain java.util.logging.LogManager#addLogger add them to the log manager}.
 * The adapters can be created, garbage-collected and recreated again while preserving their
 * behavior since their configuration is entirely contained in the external logging framework.
 *
 * {@section Localization}
 * This logger is always created without resource bundles. Localizations must be performed through
 * explicit calls to {@code logrb} or {@link #log(LogRecord)} methods. This is sufficient for
 * Geotk needs, which performs all localizations through the later. Note that those methods
 * will be slower in this {@code LoggerAdapter} than the default {@link Logger} because this
 * adapter localizes and formats records immediately instead of letting the {@linkplain Handler}
 * performs this work only if needed.
 *
 * {@section Logging levels}
 * If a log record {@linkplain Level level} is not one of the predefined ones, then this class
 * maps to the first level below the specified one. For example if a log record has some level
 * between {@link Level#FINE FINE} and {@link Level#FINER FINER}, then the {@link #finer finer}
 * method will be invoked. See {@link #isLoggable} for implementation tips taking advantage of
 * this rule.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @see Logging
 *
 * @since 2.4
 * @level advanced
 * @module
 *
 * @deprecated Moved to Apache SIS as {@link org.apache.sis.util.logging.LoggerAdapter}
 */
@Deprecated
public abstract class LoggerAdapter extends org.apache.sis.util.logging.LoggerAdapter {
    /**
     * Creates a new logger.
     *
     * @param name The logger name.
     */
    protected LoggerAdapter(final String name) {
        super(name);
    }
}
