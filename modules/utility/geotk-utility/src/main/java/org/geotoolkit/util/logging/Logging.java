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

import java.util.Arrays;
import java.util.Comparator;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;

import org.geotoolkit.resources.Errors;
import org.geotoolkit.lang.Configuration;
import org.geotoolkit.lang.Static;
import org.apache.sis.util.ArraysExt;
import org.apache.sis.util.Exceptions;
import org.apache.sis.util.Classes;
import org.apache.sis.util.logging.MonolineFormatter;


/**
 * A set of utilities method for configuring loggings in Geotk. Library implementors shall fetch
 * their logger using the {@link #getLogger(Class)} static method provided in this class rather
 * than {@link Logger#getLogger(String)}, in order to give Geotk a chance to redirect the logging
 * to an other framework like <A HREF="http://commons.apache.org/logging/">Commons-logging</A> or
 * <A HREF="http://logging.apache.org/log4j">Log4J</A>.
 * <p>
 * This method provides also a {@link #log(Class, String, LogRecord)} convenience static method, which
 * {@linkplain LogRecord#setLoggerName set the logger name} of the given record before to log it.
 * An other worthy static method is {@link #unexpectedException(Class, String, Throwable)}, for
 * reporting an anomalous but nevertheless non-fatal exception.
 *
 * {@section Configuration}
 * The log records can be redirected explicitly to an other logging framework using the
 * following method call (replace {@link org.geotoolkit.util.logging.CommonsLoggerFactory}
 * by any other framework if desired):
 *
 * {@preformat java
 *     Logging.GEOTOOLKIT.setLoggerFactory(CommonsLoggerFactory.class);
 * }
 *
 * Note however that the above method invocation is performed automatically if the
 * {@code geotk-logging-commons.jar} or the {@code geotk-logging-log4j.jar} file is
 * found on the classpath, so it usually doesn't need to be invoked explicitely.
 * See the {@link #scanLoggerFactory()} method for more details on automatic logger
 * factory registration.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.4
 * @module
 */
public final class Logging extends Static {
    /**
     * Compares {@link Logging} or {@link String} objects for alphabetical order.
     */
    private static final Comparator<Object> COMPARATOR = new Comparator<Object>() {
        @Override public int compare(final Object o1, final Object o2) {
            final String n1 = (o1 instanceof Logging) ? ((Logging) o1).name : o1.toString();
            final String n2 = (o2 instanceof Logging) ? ((Logging) o2).name : o2.toString();
            return n1.compareTo(n2);
        }
    };

    /**
     * An empty array of loggings. Also used for locks.
     */
    private static final Logging[] EMPTY = new Logging[0];

    /**
     * Logging configuration that apply to all packages.
     */
    public static final Logging ALL = new Logging();
    static { // Must be run after ALL assignation and before GEOTOOLKIT (or any other Logging) creation.
        ALL.scanLoggerFactory();
    }

    /**
     * Logging configuration that apply only to {@code org.geotoolkit} packages.
     */
    public static final Logging GEOTOOLKIT = getLogging("org.geotoolkit");

    /**
     * The name of the base package.
     *
     * @since 3.15
     */
    public final String name;

    /**
     * The children {@link Logging} objects.
     * <p>
     * The plain array used there is not efficient for adding new items (an {@code ArrayList}
     * would be more efficient), but we assume that very few new items will be added. Furthermore
     * a plain array is efficient for reading, and the later is way more common than the former.
     */
    private Logging[] children = EMPTY;

    /**
     * The factory for creating loggers, or {@code null} if none. If {@code null}
     * (the default), then the standard Java logging framework will be used.
     *
     * @see #setLoggerFactory
     */
    private LoggerFactory<?> factory;

    /**
     * {@code true} if every {@link Logging} instances use the same {@link LoggerFactory}.
     * This is an optimization for a very common case.
     */
    private static boolean sameLoggerFactory = true;

    /**
     * Creates an instance for the root logger. This constructor should not be used
     * for anything else than {@link #ALL} construction; use {@link #getLogging} instead.
     */
    private Logging() {
        name = "";
    }

    /**
     * Creates an instance for the specified base logger. This constructor
     * should not be public; use {@link #getLogging} instead.
     *
     * @param parent The parent {@code Logging} instance.
     * @param name   The logger name for the new instance.
     */
    private Logging(final Logging parent, final String name) {
        this.name = name;
        factory = parent.factory;
        assert name.startsWith(parent.name) : name;
    }

    /**
     * Logs the given record to the logger associated to the given class.
     * This convenience method performs the following steps:
     * <p>
     * <ul>
     *   <li>Get the logger using {@link #getLogger(Class)};</li>
     *   <li>{@linkplain LogRecord#setLoggerName(String) Set the logger name} of the given record,
     *       if not already set;</li>
     *   <li>Unconditionally {@linkplain LogRecord#setSourceClassName(String) set the source class
     *       name} to the {@linkplain Class#getCanonicalName() canonical name} of the given class;</li>
     *   <li>Unconditionally {@linkplain LogRecord#setSourceMethodName(String) set the source method
     *       name} to the given value;</li>
     *   <li>{@linkplain Logger#log(LogRecord) Log} the modified record.</li>
     * </ul>
     *
     * @param classe The class for which to obtain a logger.
     * @param method The name of the method which is logging a record.
     * @param record The record to log.
     *
     * @since 3.20 (derived from 3.00)
     */
    public static void log(final Class<?> classe, final String method, final LogRecord record) {
        record.setSourceClassName(classe.getCanonicalName());
        record.setSourceMethodName(method);
        final Logger logger = getLogger(classe);
        if (record.getLoggerName() == null) {
            record.setLoggerName(logger.getName());
        }
        logger.log(record);
    }

    /**
     * Returns a logger for the specified class. This convenience method invokes
     * {@link #getLogger(String)} with the package name as the logger name.
     *
     * @param  classe The class for which to obtain a logger.
     * @return A logger for the specified class.
     *
     * @since 2.5
     */
    public static Logger getLogger(Class<?> classe) {
        Class<?> outer;
        while ((outer = classe.getEnclosingClass()) != null) {
            classe = outer;
        }
        String name = classe.getName();
        final int separator = name.lastIndexOf('.');
        name = (separator >= 1) ? name.substring(0, separator) : "";
        return getLogger(name);
    }

    /**
     * Returns a logger for the specified name. If a {@linkplain LoggerFactory logger factory} has
     * been set, then this method first {@linkplain LoggerFactory#getLogger ask to the factory}.
     * It gives Geotk a chance to redirect logging events to
     * <A HREF="http://commons.apache.org/logging/">commons-logging</A>
     * or some equivalent framework.
     * <p>
     * If no factory was found or if the factory choose to not redirect the loggings, then this
     * method returns the usual <code>{@linkplain Logger#getLogger Logger.getLogger}(name)</code>.
     *
     * @param  name The logger name.
     * @return A logger for the specified name.
     */
    public static Logger getLogger(final String name) {
        synchronized (EMPTY) {
            final Logging logging = sameLoggerFactory ? ALL : getLogging(name, false);
            if (logging != null) { // Paranoiac check ('getLogging' should not returns null).
                final LoggerFactory<?> factory = logging.factory;
                assert getLogging(name, false).factory == factory : name;
                if (factory != null) {
                    final Logger logger = factory.getLogger(name);
                    if (logger != null) {
                        return logger;
                    }
                }
            }
        }
        return Logger.getLogger(name);
    }

    /**
     * Returns a {@code Logging} instance for the specified base logger. This instance is
     * used for controlling logging configuration in Geotk. For example methods like
     * {@link #forceMonolineConsoleOutput} are invoked on a {@code Logging} instance.
     * <p>
     * {@code Logging} instances follow the same hierarchy than {@link Logger}, i.e.
     * {@code "org.geotoolkit"} is the parent of {@code "org.geotoolkit.referencing"},
     * {@code "org.geotoolkit.metadata"}, <i>etc</i>.
     *
     * @param name The base logger name.
     * @return The logging instance for the given name.
     */
    public static Logging getLogging(final String name) {
        synchronized (EMPTY) {
            return getLogging(name, true);
        }
    }

    /**
     * Returns a logging instance for the specified base logger. If no instance if found for
     * the specified name and {@code create} is {@code true}, then a new instance will be
     * created. Otherwise the nearest parent is returned.
     *
     * @param base The root logger name.
     * @param create {@code true} if this method is allowed to create new {@code Logging} instance.
     * @return The logging instance for the given name.
     */
    private static Logging getLogging(final String base, final boolean create) {
        assert Thread.holdsLock(EMPTY);
        Logging logging = ALL;
        if (!base.isEmpty()) {
            int offset = 0;
            do {
                Logging[] children = logging.children;
                offset = base.indexOf('.', offset);
                final String name = (offset >= 0) ? base.substring(0, offset) : base;
                int i = Arrays.binarySearch(children, name, COMPARATOR);
                if (i < 0) {
                    // No exact match found.
                    if (!create) {
                        // We are not allowed to create new Logging instance.
                        // 'logging' is the nearest parent, so stop the loop now.
                        break;
                    }
                    i = ~i;
                    children = ArraysExt.insert(children, i, 1);
                    children[i] = new Logging(logging, name);
                    logging.children = children;
                }
                logging = children[i];
            } while (++offset != 0);
        }
        return logging;
    }

    /**
     * For testing purpose only; don't make this method public.
     */
    final Logging[] getChildren() {
        synchronized (EMPTY) {
            return children.clone();
        }
    }

    /**
     * Returns the logger factory, or {@code null} if none. This method returns the logger set
     * by the last call to {@link #setLoggerFactory} on this {@code Logging} instance or on one
     * of its parent.
     *
     * @return The current logger factory.
     */
    public LoggerFactory<?> getLoggerFactory() {
        synchronized (EMPTY) {
            return factory;
        }
    }

    /**
     * Sets a new logger factory for this {@code Logging} instance and every children. The
     * specified factory will be used by <code>{@linkplain #getLogger(String) getLogger}(name)</code>
     * when {@code name} is this {@code Logging} name or one of its children.
     * <p>
     * If the factory is set to {@code null} (the default), then the standard Logging framework
     * will be used.
     *
     * @param factory The new logger factory, or {@code null} if none.
     */
    @Configuration
    public void setLoggerFactory(final LoggerFactory<?> factory) {
        synchronized (EMPTY) {
            this.factory = factory;
            for (int i=0; i<children.length; i++) {
                children[i].setLoggerFactory(factory);
            }
            sameLoggerFactory = sameLoggerFactory(ALL.children, ALL.factory); // NOSONAR: really want static field.
        }
    }

    /**
     * Returns {@code true} if all children use the specified factory.
     * Used in order to detect a possible optimization for this very common case.
     */
    private static boolean sameLoggerFactory(final Logging[] children, final LoggerFactory<?> factory) {
        assert Thread.holdsLock(EMPTY);
        for (int i=0; i<children.length; i++) {
            final Logging logging = children[i];
            if (logging.factory != factory || !sameLoggerFactory(logging.children, factory)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Wraps a unchecked {@link NoClassDefFoundError} into a checked {@link ClassNotFoundException}.
     */
    private static ClassNotFoundException factoryNotFound(String name, NoClassDefFoundError error) {
        return new ClassNotFoundException(Errors.format(Errors.Keys.FACTORY_NOT_FOUND_1, name), error);
    }

    /**
     * Scans the classpath for logger factories. The fully qualified factory classname shall be
     * declared in the following file:
     *
     * {@preformat text
     *     META-INF/services/org.geotoolkit.util.logging.LoggerFactory
     * }
     *
     * The first factory found on the classpath is given to {@link #setLoggerFactory(Class)}.
     * If it can't be used (for example because of missing dependency), then the second factory
     * is tried, <i>etc.</i> until an acceptable factory is found.
     * <p>
     * This method usually doesn't need to be invoked explicitly, since it is automatically
     * invoked on {@code Logging} class initialization. However developers may invoke it if
     * new {@code LoggerFactory}s are added later on the classpath of a running JVM.
     *
     * @since 3.00
     */
    @Configuration
    public void scanLoggerFactory() {
        LoggerFactory<?> factory = null;
        for (final LoggerFactory<?> found : ServiceLoader.load(LoggerFactory.class)) {
            if (factory == null) {
                factory = found;
            } else {
                factory = new DualLoggerFactory(factory, found);
            }
        }
        setLoggerFactory(factory);
    }

    /**
     * Configures the default {@linkplain ConsoleHandler console handler} in order to log records
     * on a single line instead of two lines. More specifically, for each {@code ConsoleHandler}
     * using a {@link SimpleFormatter}, this method replaces the simple formatter by an instance
     * of {@link MonolineFormatter}. If no {@code ConsoleHandler} are found, then a new one is
     * created.
     *
     * {@note This method may have no effect if the loggings are redirected to an other
     *        logging framework than the standard Java one.}
     *
     * In addition, this method can set the handler levels. If the level is non-null, then every
     * {@link Handler}s using the monoline formatter may be set to the specified level. Whatever
     * the given level is used or not depends on current configuration. The choice is based on
     * heuristic rules that may change in any future version. Developers are encouraged to avoid
     * non-null level except for debugging purpose, since a user trying to configure his logging
     * properties file may find confusing to see his setting ignored.
     *
     * @param level The desired logging level, or {@code null} to left it unchanged.
     *
     * @see <a href="http://java.sun.com/javase/6/docs/technotes/guides/logging/overview.html">Java Logging Overview</a>
     * @see org.geotoolkit.lang.Setup
     */
    @Configuration
    public void forceMonolineConsoleOutput(final Level level) {
        final Logger logger = Logger.getLogger(name); // Really Java logging, not the redirected one.
        synchronized (EMPTY) {
            final MonolineFormatter f = MonolineFormatter.install(logger, level);
            if (f != null) {
                f.setSourceFormat("class:short");
            }
            if (level != null) {
                // If a level was specified, changes to a finer level if needed
                // (e.g. from FINE to FINER, but not the opposite).
                final Level current = logger.getLevel();
                if (current == null || current.intValue() > level.intValue()) {
                    logger.setLevel(level);
                }
            }
        }
    }

    /**
     * Flushes all {@linkplain Handler handlers} used by the logger named {@link #name}.
     * If that logger {@linkplain Logger#getUseParentHandlers() uses parent handlers},
     * then those handlers will be flushed as well.
     * <p>
     * If the log records seem to be interleaved with the content of {@link System#out}
     * or {@link System#err}, invoking this method before to write to the standard streams
     * may help.
     *
     * @since 3.18
     *
     * @see Handler#flush()
     */
    public void flush() {
        for (Logger logger=getLogger(name); logger!=null; logger=logger.getParent()) {
            for (final Handler handler : logger.getHandlers()) {
                handler.flush();
            }
            if (!logger.getUseParentHandlers()) {
                break;
            }
        }
    }

    /**
     * Invoked when an unexpected error occurs. This method logs a message at the
     * {@link Level#WARNING WARNING} level to the specified logger. The originating
     * class name and method name are inferred from the error stack trace, using the
     * first {@linkplain StackTraceElement stack trace element} for which the class
     * name is inside a package or sub-package of the logger name. For example if
     * the logger name is {@code "org.geotoolkit.image"}, then this method will uses
     * the first stack trace element where the fully qualified class name starts with
     * {@code "org.geotoolkit.image"} or {@code "org.geotoolkit.image.io"}, but not
     * {@code "org.geotoolkit.imageio"}.
     *
     * @param  logger Where to log the error.
     * @param  error  The error that occurred.
     * @return {@code true} if the error has been logged, or {@code false} if the logger
     *         doesn't log anything at the {@link Level#WARNING WARNING} level.
     */
    public static boolean unexpectedException(final Logger logger, final Throwable error) {
        return unexpectedException(logger, null, null, error, Level.WARNING);
    }

    /**
     * Invoked when an unexpected error occurs. This method logs a message at the
     * {@link Level#WARNING WARNING} level to the specified logger. The originating
     * class name and method name can optionally be specified. If any of them is
     * {@code null}, then it will be inferred from the error stack trace as in
     * {@link #unexpectedException(Logger, Throwable)}.
     * <p>
     * Explicit value for class and method names are sometime preferred to automatic
     * inference for the following reasons:
     *
     * <ul>
     *   <li><p>Automatic inference is not 100% reliable, since the Java Virtual Machine
     *       is free to omit stack frame in optimized code.</p></li>
     *   <li><p>When an exception occurred in a private method used internally by a public
     *       method, we sometime want to log the warning for the public method instead,
     *       since the user is not expected to know anything about the existence of the
     *       private method. If a developer really want to know about the private method,
     *       the stack trace is still available anyway.</p></li>
     * </ul>
     *
     * @param logger  Where to log the error.
     * @param classe  The class where the error occurred, or {@code null}.
     * @param method  The method where the error occurred, or {@code null}.
     * @param error   The error.
     * @return {@code true} if the error has been logged, or {@code false} if the logger
     *         doesn't log anything at the {@link Level#WARNING WARNING} level.
     *
     * @see #recoverableException(Logger, Class, String, Throwable)
     * @see #severeException(Logger, Class, String, Throwable)
     */
    public static boolean unexpectedException(final Logger logger, final Class<?> classe,
                                              final String method, final Throwable error)
    {
        final String classname = (classe != null) ? classe.getName() : null;
        return unexpectedException(logger, classname, method, error, Level.WARNING);
    }

    /**
     * Invoked when an unexpected error occurs. This method logs a message at the
     * {@link Level#WARNING WARNING} level to a logger inferred from the given class.
     *
     * @param classe  The class where the error occurred.
     * @param method  The method where the error occurred, or {@code null}.
     * @param error   The error.
     * @return {@code true} if the error has been logged, or {@code false} if the logger
     *         doesn't log anything at the {@link Level#WARNING WARNING} level.
     *
     * @since 2.5
     *
     * @see #recoverableException(Class, String, Throwable)
     */
    public static boolean unexpectedException(Class<?> classe, String method, Throwable error) {
        return unexpectedException((Logger) null, classe, method, error);
    }

    /**
     * Implementation of {@link #unexpectedException(Logger, Class, String, Throwable)}.
     *
     * @param logger  Where to log the error, or {@code null}.
     * @param classe  The fully qualified class name where the error occurred, or {@code null}.
     * @param method  The method where the error occurred, or {@code null}.
     * @param error   The error.
     * @param level   The logging level.
     * @return {@code true} if the error has been logged, or {@code false} if the logger
     *         doesn't log anything at the specified level.
     */
    private static boolean unexpectedException(Logger logger, String classe, String method,
                                               final Throwable error, final Level level)
    {
        /*
         * Checks if loggable, inferring the logger from the classe name if needed.
         */
        if (error == null) {
            return false;
        }
        if (logger == null && classe != null) {
            final int separator = classe.lastIndexOf('.');
            final String paquet = (separator >= 1) ? classe.substring(0, separator-1) : "";
            logger = getLogger(paquet);
        }
        if (logger != null && !logger.isLoggable(level)) {
            return false;
        }
        /*
         * Loggeable, so complete the null argument from the stack trace if we can.
         */
        if (logger==null || classe==null || method==null) {
            String paquet = (logger != null) ? logger.getName() : null;
            final StackTraceElement[] elements = error.getStackTrace();
            for (int i=0; i<elements.length; i++) {
                /*
                 * Searches for the first stack trace element with a classname matching the
                 * expected one. We compare preferably against the name of the class given
                 * in argument, or against the logger name (taken as the package name) otherwise.
                 */
                final StackTraceElement element = elements[i];
                final String classname = element.getClassName();
                if (classe != null) {
                    if (!classname.equals(classe)) {
                        continue;
                    }
                } else if (paquet != null) {
                    if (!classname.startsWith(paquet)) {
                        continue;
                    }
                    final int length = paquet.length();
                    if (classname.length() > length) {
                        // We expect '.' but we accept also '$' or end of string.
                        final char separator = classname.charAt(length);
                        if (Character.isJavaIdentifierPart(separator)) {
                            continue;
                        }
                    }
                }
                /*
                 * Now that we have a stack trace element from the expected class (or any
                 * element if we don't know the class), make sure that we have the right method.
                 */
                final String methodName = element.getMethodName();
                if (method != null && !methodName.equals(method)) {
                    continue;
                }
                /*
                 * Now computes every values that are null, and stop the loop.
                 */
                if (paquet == null) {
                    final int separator = classname.lastIndexOf('.');
                    paquet = (separator >= 1) ? classname.substring(0, separator-1) : "";
                    logger = getLogger(paquet);
                    if (!logger.isLoggable(level)) {
                        return false;
                    }
                }
                if (classe == null) {
                    classe = classname;
                }
                if (method == null) {
                    method = methodName;
                }
                break;
            }
            /*
             * The logger may stay null if we have been unable to find a suitable
             * stack trace. Fallback on the global logger.
             */
            if (logger == null) {
                logger = getLogger(Logger.GLOBAL_LOGGER_NAME);
                if (!logger.isLoggable(level)) {
                    return false;
                }
            }
        }
        /*
         * Now prepare the log message. If we have been unable to figure out a source class and
         * method name, we will fallback on Java logging default mechanism, which may returns a
         * less relevant name than our attempt to use the logger name as the package name.
         */
        final StringBuilder buffer = new StringBuilder(Classes.getShortClassName(error));
        String message = error.getLocalizedMessage();
        if (message != null) {
            buffer.append(": ").append(message);
        }
        message = buffer.toString();
        message = Exceptions.formatChainedMessages(null, message, error);
        final LogRecord record = new LogRecord(level, message);
        if (classe != null) {
            record.setSourceClassName(classe);
        }
        if (method != null) {
            record.setSourceMethodName(method);
        }
        if (level.intValue() > 500) {
            record.setThrown(error);
        }
        record.setLoggerName(logger.getName());
        logger.log(record);
        return true;
    }

    /**
     * Invoked when a recoverable error occurs. This method is similar to
     * {@link #unexpectedException(Class,String,Throwable) unexpectedException}
     * except that it doesn't log the stack trace and uses a lower logging level.
     *
     * @param classe  The class where the error occurred.
     * @param method  The method name where the error occurred.
     * @param error   The error.
     * @return {@code true} if the error has been logged, or {@code false} if the logger
     *         doesn't log anything at the {@link Level#FINE FINE} level.
     *
     * @see #unexpectedException(Class, String, Throwable)
     *
     * @since 2.5
     */
    public static boolean recoverableException(final Class<?> classe, final String method,
                                               final Throwable error)
    {
        return recoverableException(null, classe, method, error);
    }

    /**
     * Invoked when a recoverable error occurs. This method is similar to
     * {@link #unexpectedException(Logger,Class,String,Throwable) unexpectedException}
     * except that it doesn't log the stack trace and uses a lower logging level.
     *
     * @param logger  Where to log the error.
     * @param classe  The class where the error occurred.
     * @param method  The method name where the error occurred.
     * @param error   The error.
     * @return {@code true} if the error has been logged, or {@code false} if the logger
     *         doesn't log anything at the {@link Level#FINE FINE} level.
     *
     * @see #unexpectedException(Logger, Class, String, Throwable)
     * @see #severeException(Logger, Class, String, Throwable)
     *
     * @since 2.5
     */
    public static boolean recoverableException(final Logger logger, final Class<?> classe,
                                               final String method, final Throwable error)
    {
        final String classname = (classe != null) ? classe.getName() : null;
        return unexpectedException(logger, classname, method, error, Level.FINE);
    }

    /**
     * Invoked when a severe error occurs. This method is similar to
     * {@link #unexpectedException(Logger,Class,String,Throwable) unexpectedException}
     * except that it logs the message at the {@link Level#SEVERE SEVERE} level.
     *
     * @param logger  Where to log the error.
     * @param classe  The class where the error occurred.
     * @param method  The method name where the error occurred.
     * @param error   The error.
     * @return {@code true} if the error has been logged, or {@code false} if the logger
     *         doesn't log anything at the {@link Level#SEVERE SEVERE} level.
     *
     * @see #unexpectedException(Logger, Class, String, Throwable)
     * @see #recoverableException(Logger, Class, String, Throwable)
     *
     * @since 3.00
     */
    public static boolean severeException(final Logger logger, final Class<?> classe,
                                          final String method, final Throwable error)
    {
        final String classname = (classe != null) ? classe.getName() : null;
        return unexpectedException(logger, classname, method, error, Level.SEVERE);
    }
}
