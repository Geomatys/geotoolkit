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

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import javax.imageio.spi.ServiceRegistry;
import net.jcip.annotations.NotThreadSafe;

import org.opengis.metadata.quality.ConformanceResult;

import org.geotoolkit.lang.Debug;
import org.apache.sis.util.CharSequences;
import org.geotoolkit.util.logging.Logging;
import org.apache.sis.util.Classes;
import org.geotoolkit.util.collection.XCollections;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;


/**
 * A registry for factories organized by categories. A category is an interface for a
 * <cite>service</cite> as described in the {@linkplain org.geotoolkit.factory package javadoc}.
 * {@code FactoryRegistry} extends {@link ServiceRegistry} with the following functionalities:
 *
 * <ul>
 *   <li><p>A {@link #scanForPlugins()} method that scans for plugins in
 *   the {@linkplain Class#getClassLoader registry class loader},
 *   the {@linkplain Thread#getContextClassLoader thread context class loader} and
 *   the {@linkplain ClassLoader#getSystemClassLoader system class loader}.</p></li>
 *
 *   <li><p>When more than one implementation is available for the same {@link Factory} subclass,
 *   an optional set of {@linkplain Hints hints} can specifies the criterion that the desired
 *   implementation must meets. If a factory implementation depends on other factories, the
 *   dependencies hints are checked recursively.</p></li>
 *
 *   <li><p><b>Optionally</b>, if no factory matches the given hints, a new instance can be
 *   {@linkplain DynamicFactoryRegistry#createServiceProvider automatically created}.</p></li>
 * </ul>
 *
 * When more than one {@linkplain Factory factory} implementation is registered for the same category,
 * the actual instance to be used is selected according their {@linkplain ServiceRegistry#setOrdering
 * ordering} and user-supplied {@linkplain Hints hints}. Hints have precedence. If more than one factory
 * matches the hints (including the common case where the user doesn't provide any hint at all), then
 * ordering matter.
 * <p>
 * <strong>NOTE: This class is not thread safe</strong>. Users are responsible
 * for synchronization. This is usually done in an utility class wrapping this
 * service registry (e.g. {@link org.geotoolkit.factory.FactoryFinder}).
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Richard Gould
 * @author Jody Garnett (Refractions)
 * @version 3.03
 *
 * @see Factory
 * @see Hints
 *
 * @since 2.1
 * @module
 */
@NotThreadSafe
public class FactoryRegistry extends ServiceRegistry {
    /**
     * The logger for all events related to factory registry.
     */
    protected static final Logger LOGGER = Logging.getLogger(FactoryRegistry.class);

    /**
     * The logger level for debug messages.
     */
    @Debug
    private static final Level DEBUG_LEVEL = Level.FINEST;

    /**
     * A copy of the global configuration defined through {@link FactoryIteratorProviders}
     * static methods. We keep a copy in every {@code FactoryRegistry} instance in order to
     * compare against the master {@link FactoryIteratorProviders#GLOBAL} and detect if the
     * configuration changed since the last time this registry was used.
     *
     * @see #synchronizeIteratorProviders
     */
    private final FactoryIteratorProviders globalConfiguration = new FactoryIteratorProviders();

    /**
     * The set of categories that need to be scanned for plugins, or {@code null} if none.
     * On initialization, all categories need to be scanned for plugins. After a category
     * has been first used, it is removed from this set so we don't scan for plugins again.
     */
    private Set<Class<?>> needScanForPlugins;

    /**
     * Categories under scanning. This is used by {@link #scanForPlugins(Collection,Class)}
     * as a guard against infinite recursivity (i.e. when a factory to be scanned request
     * an other dependency of the same category).
     */
    private final Set<Class<?>> scanningCategories = new HashSet<>();

    /**
     * Factories under testing for availability. This is used by
     * {@link #isAcceptable} as a guard against infinite recursivity.
     */
    private final Set<Class<? extends Factory>> testingAvailability = new HashSet<>();

    /**
     * Factories under testing for hints compatibility. This is used by
     * {@link #usesAcceptableHints} as a guard against infinite recursivity.
     */
    private final Set<Factory> testingHints = new HashSet<>();

    /**
     * If a factory is not available because of some exception, the exception. Otherwise {@code null}.
     * This is a temporary field used only during execution of {@code getServiceProvider} methods.
     */
    private transient Throwable failureCause;

    /**
     * Constructs a new registry for the specified category.
     *
     * @param category The single category.
     *
     * @since 2.4
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public FactoryRegistry(final Class<?> category) {
        this((Collection) Collections.singleton(category));
        // Safe because java.lang.Class is final.
    }

    /**
     * Constructs a new registry for the specified categories.
     *
     * @param categories The categories.
     *
     * @since 2.4
     */
    public FactoryRegistry(final Class<?>[] categories) {
        this(Arrays.asList(categories));
    }

    /**
     * Constructs a new registry for the specified categories.
     *
     * @param categories The categories.
     */
    public FactoryRegistry(final Collection<Class<?>> categories) {
        super(categories.iterator());
        for (final Iterator<Class<?>> it=getCategories(); it.hasNext();) {
            if (needScanForPlugins == null) {
                needScanForPlugins = new HashSet<>();
            }
            needScanForPlugins.add(it.next());
        }
    }

    /**
     * Returns the providers in the registry for the specified category, filter and hints.
     * Providers that are not {@linkplain Factory#availability available} will be ignored.
     * This method will {@linkplain #scanForPlugins() scan for plugins} the first time it
     * is invoked for the given category.
     *
     * @param <T>      The class represented by the {@code category} argument.
     * @param category The category to look for. Usually an interface class
     *                 (not the actual implementation class).
     * @param filter   The optional filter, or {@code null}.
     * @param hints    The optional user requirements, or {@code null}.
     * @param key      The key to use for looking for a user-provided instance
     *                 in the {@code hints} map, or {@code null} if none.
     * @return Factories ready to use for the specified category, filter and hints.
     *
     * @since 3.03 (derived from 2.3)
     */
    public <T> Iterator<T> getServiceProviders(final Class<T> category,
            final Filter filter, Hints hints, final Hints.ClassKey key)
    {
        final Class<?>[] requestedType;
        if (hints != null && key != null && hints.get(key) != null) {
            hints = hints.clone();
            final Object value = hints.remove(key);
            if (value instanceof Class<?>) {
                requestedType = new Class<?>[] {(Class<?>) value};
            } else if (value instanceof Class<?>[]) {
                requestedType = ((Class<?>[]) value).clone();
            } else {
                /*
                 * If the user gaves explicitly a factory, returns only that factory
                 * provided that it meets other hints. Otherwise returns an empty set.
                 */
                Collection<T> values = Collections.emptySet();
                if (key.getValueClass().isInstance(value)) {
                    final T provider = category.cast(value);
                    if (isAcceptable(provider, category, hints, filter, false)) {
                        values = Collections.singleton(provider);
                    }
                }
                return values.iterator();
            }
        } else {
            requestedType = null;
        }
        /*
         * The implementation of this method is very similar to the 'getUnfilteredProviders'
         * one except for filter handling. See the comments in 'getUnfilteredProviders' for
         * more implementation details.
         */
        if (scanningCategories.contains(category)) {
            // We reach this point if we accidentally allow more than
            // one thread to use the FactoryRegistry at a time.
            throw new RecursiveSearchException(category);
        }
        final Hints userHints = hints;
        final Filter hintsFilter = new Filter() {
            @Override public boolean filter(final Object provider) {
                if (requestedType != null) {
                    int i=0;
                    do if (i == requestedType.length) return false;
                    while (!requestedType[i++].isInstance(provider));
                }
                return isAcceptable(category.cast(provider), category, userHints, filter, false);
            }
        };
        synchronizeIteratorProviders();
        scanForPluginsIfNeeded(category);
        return getServiceProviders(category, hintsFilter, true);
    }

    /**
     * Implementation of {@link #getServiceProviders(Class, Filter, Hints)} without the filtering
     * applied by the {@link #isAcceptable(Object, Class, Hints, Filter)} method. If this filtering
     * is not already presents in the filter given to this method, then it must be applied on the
     * elements returned by the iterator. The later is preferable when:
     * <p>
     * <ul>
     *   <li>There is some cheaper tests to perform before {@code isAcceptable}.</li>
     *   <li>We don't want a restrictive filter in order to avoid trigging a classpath
     *       scan if this method doesn't found any element to iterate.</li>
     * </ul>
     * <p>
     * <b>Note:</b>
     * {@link #synchronizeIteratorProviders} should also be invoked once before this method.
     */
    final <T> Iterator<T> getUnfilteredProviders(final Class<T> category) {
        /*
         * If the map is not empty, then this mean that a scanning is under progress, i.e.
         * 'scanForPlugins' is currently being executed. This is okay as long as the user
         * is not asking for one of the categories under scanning. Otherwise, the answer
         * returned by 'getServiceProviders' would be incomplete because not all plugins
         * have been found yet. This can lead to some bugs hard to spot because this methoud
         * could complete normally but return the wrong plugin. It is safer to thrown an
         * exception so the user is advised that something is wrong.
         */
        if (scanningCategories.contains(category)) {
            throw new RecursiveSearchException(category);
        }
        scanForPluginsIfNeeded(category);
        return getServiceProviders(category, true);
    }

    /**
     * Returns the first provider in the registry for the specified category, using the specified
     * map of hints (if any). This method may {@linkplain #scanForPlugins scan for plugins} the
     * first time it is invoked. Except as a result of this scan, no new provider instance is
     * created by the default implementation of this method. The {@link DynamicFactoryRegistry}
     * class change this behavior however.
     *
     * @param  <T>      The class represented by the {@code category} argument.
     * @param  category The category to look for. Must be one of the categories declared to the
     *                  constructor. Usually an interface class (not the actual implementation
     *                  class).
     * @param  filter   An optional filter, or {@code null} if none.
     *                  This is used for example in order to select the first factory for some
     *                  {@linkplain org.opengis.referencing.AuthorityFactory#getAuthority authority}.
     * @param  hints    A {@linkplain Hints map of hints}, or {@code null} if none.
     * @param  key      The key to use for looking for a user-provided instance in the hints, or
     *                  {@code null} if none.
     * @return A factory {@linkplain Factory#availability available} for use for the specified
     *         category and hints. The returns type is {@code Object} instead of {@link Factory}
     *         because the factory implementation doesn't need to be a Geotk one.
     * @throws FactoryNotFoundException if no factory was found for the specified category, filter
     *         and hints.
     * @throws FactoryRegistryException if a factory can't be returned for some other reason.
     *
     * @see #getServiceProviders
     * @see DynamicFactoryRegistry#getServiceProvider
     */
    public <T> T getServiceProvider(final Class<T> category, final Filter filter,
            Hints hints, final Hints.ClassKey key) throws FactoryRegistryException
    {
        // The current 'failureCause' should be null,
        // except if this method is invoked recursively.
        final Throwable old = failureCause;
        try {
            return getOrCreateServiceProvider(category, filter, hints, key);
        } finally {
            failureCause = old;
            reset();
        }
    }

    /**
     * Makes this {@code FactoryRegistry} ready for next use. This method is
     * overridden by {@link DynamicFactoryRegistry} with additional cleanup.
     */
    void reset() {
    }

    /**
     * Implementation of {@link #getServiceProvider}, in a separated method for making easier to
     * encompass in a {@code try ... finally} block.  The {@code FactoryRegistry} implementation
     * does not create any new provider. However {@link DynamicFactoryRegistry} do override this
     * method in a way that may create new objects.
     */
    <T> T getOrCreateServiceProvider(final Class<T> category, final Filter filter,
            Hints hints, final Hints.ClassKey key) throws FactoryRegistryException
    {
        synchronizeIteratorProviders();
        final boolean debug = LOGGER.isLoggable(DEBUG_LEVEL);
        if (debug) {
            /*
             * We are not required to insert the method name ("GetServiceProvider") in the
             * message because it is part of the informations already stored by LogRecord,
             * and formatted by the default java.util.logging.SimpleFormatter.
             *
             * Conventions for the message part according java.util.logging.Logger javadoc:
             * - "ENTRY"  at the beginning of a method.
             * - "RETURN" at the end of a method, if successful.
             * - "THROW"  in case of failure.
             * - "CHECK"  ... is our own addition to Sun's convention for this method ...
             */
            debug("ENTRY", category, key, null, null);
        }
        Class<?> implementationType = null;
        if (key != null) {
            /*
             * Sanity check: make sure that the key class is appropriate for the category.
             */
            final Class<?> valueClass = key.getValueClass();
            if (!category.isAssignableFrom(valueClass)) {
                if (debug) {
                    debug("THROW", category, key, "unexpected type:", valueClass);
                }
                throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_KEY_1, key));
            }
            if (hints != null) {
                final Object hint = hints.get(key);
                if (hint != null) {
                    if (debug) {
                        debug("CHECK", category, key, "user provided a", hint.getClass());
                    }
                    if (category.isInstance(hint)) {
                        /*
                         * The factory implementation was given explicitly by the user.
                         * Nothing to do; we are done.
                         */
                        if (debug) {
                            debug("RETURN", category, key, "return hint as provided.", null);
                        }
                        return category.cast(hint);
                    }
                    /*
                     * Before to pass the hints to the private 'getServiceImplementation' method,
                     * remove the hint for the user-supplied key.  This is because this hint has
                     * been processed by this public 'getServiceProvider' method, and the policy
                     * is to remove the processed hints before to pass them to child dependencies
                     * (see the "Check recursively in factory dependencies" comment elswhere in
                     * this class).
                     *
                     * Use case: DefaultDataSourceTest invokes indirectly 'getServiceProvider'
                     * with a "CRS_AUTHORITY_FACTORY = ThreadedEpsgFactory.class" hint. However
                     * ThreadedEpsgFactory (in the org.geotoolkit.referencing.factory.epsg package)
                     * is a wrapper around DirectEpsgFactory, and defines this dependency through
                     * a "CRS_AUTHORITY_FACTORY = DirectEpsgFactory.class" hint. There is no way
                     * to match this hint for both factories in same time. Since we must choose
                     * one, we assume that the user is interested in the most top level one and
                     * discart this particular hint for the dependencies.
                     */
                    hints = hints.clone();
                    if (hints.remove(key) != hint) {
                        // Should never happen except on concurrent modification in an other thread.
                        throw new AssertionError(key);
                    }
                    /*
                     * If the user accepts many implementation classes, then try all of them in
                     * the preference order given by the user. The last class (or the singleton
                     * if the hint was not an array) will be tried using the "normal" path
                     * (oustide the loop) in order to get the error message in case of failure.
                     */
                    if (hint instanceof Class<?>[]) {
                        final Class<?>[] types = (Class<?>[]) hint;
                        final int length = types.length;
                        for (int i=0; i<length-1; i++) {
                            final Class<?> type = types[i];
                            if (debug) {
                                debug("CHECK", category, key, "consider hint[" + i + ']', type);
                            }
                            final T candidate = getServiceImplementation(category, type, filter, hints);
                            if (candidate != null) {
                                if (debug) {
                                    debug("RETURN", category, key, "found implementation", candidate.getClass());
                                }
                                return candidate;
                            }
                        }
                        if (length != 0) {
                            implementationType = types[length-1]; // Last try to be done below.
                        }
                    } else {
                        implementationType = (Class<?>) hint;
                    }
                }
            }
        }
        if (debug && implementationType != null) {
            debug("CHECK", category, key, "consider hint[last]", implementationType);
        }
        final T candidate = getServiceImplementation(category, implementationType, filter, hints);
        if (candidate != null) {
            if (debug) {
                debug("RETURN", category, key, "found implementation", candidate.getClass());
            }
            return candidate;
        }
        if (debug) {
            debug("THROW", category, key, "could not find implementation.", null);
        }
        /*
         * Before to thrown the exception, initialize its cause to 'failureCause' if the later
         * is set. Note that we really need to invoke the constructor without Throwable argument,
         * and we really needs to invoke 'Throwable.initCause(failureCause)' only if the failure
         * cause is not-null, because the DynamicFactoryRegistry subclass may perform an other
         * attempt to set the failure cause.
         */
        final String message = Errors.format(Errors.Keys.FACTORY_NOT_FOUND_1,
                (implementationType != null) ? implementationType : category);
        final FactoryNotFoundException e = new FactoryNotFoundException(message);
        initCause(e);
        throw e;
    }

    /**
     * Sets the failure cause of the given exception, if it is known. This method is invoked
     * by {@code DynamicFactoryRegistry} when it failed to use a factory which was compliant
     * with user-specified hints.
     *
     * @param e The exception for which to set the failure cause.
     */
    final void initCause(final FactoryNotFoundException e) {
        final Throwable c = failureCause;
        if (c != null && e.getClass() == FactoryNotFoundException.class && e.getCause() == null) {
            e.initCause(c);
        }
    }

    /**
     * Logs a debug message for {@link #getServiceProvider} method. Note: we are not required
     * to insert the method name ({@code "GetServiceProvider"}) in the message because it is
     * part of the informations already stored by {@link LogRecord}, and formatted by the
     * default {@link java.util.logging.SimpleFormatter}.
     *
     * @param status   {@code "ENTRY"}, {@code "RETURN"} or {@code "THROW"},
     *                 according {@link Logger} conventions.
     * @param category The category given to the {@link #getServiceProvider} method.
     * @param key      The key being examined, or {@code null}.
     * @param message  Optional message, or {@code null} if none.
     * @param type     Optional class to format after the message, or {@code null}.
     */
    @Debug
    private static void debug(final String status, final Class<?> category,
            final Hints.Key key, final String message, final Class<?> type)
    {
        final StringBuilder buffer = new StringBuilder(status);
        buffer.append(CharSequences.spaces(Math.max(1, 7-status.length())))
              .append('(').append(Classes.getShortName(category));
        if (key != null) {
            buffer.append(", ").append(key);
        }
        buffer.append(')');
        if (message != null) {
            buffer.append(": ").append(message);
        }
        if (type != null) {
            buffer.append(' ').append(Classes.getShortName(type)).append('.');
        }
        final LogRecord record = new LogRecord(DEBUG_LEVEL, buffer.toString());
        record.setSourceClassName(FactoryRegistry.class.getName());
        record.setSourceMethodName("getServiceProvider");
        record.setLoggerName(LOGGER.getName());
        LOGGER.log(record);
    }

    /**
     * Searches the first implementation in the registry matching the specified conditions.
     * This method is invoked only by the {@link #getServiceProvider(Class, Filter, Hints,
     * Hints.Key)} public method above; there is no recursivity there. This method does not
     * creates new instance if no matching factory is found.
     * <p>
     * This method is overridden by {@link DynamicFactoryRegistry} in order to search in its
     * cache if no instance was found by this method.
     *
     * @param  category           The category to look for. Usually an interface class.
     * @param  implementationType The desired class for the implementation, or {@code null} if none.
     * @param  filter             An optional filter, or {@code null} if none.
     * @param  hints              A {@linkplain Hints map of hints}, or {@code null} if none.
     * @return A factory for the specified category and hints, or {@code null} if none.
     */
    <T> T getServiceImplementation(final Class<T> category, final Class<?> implementationType,
                                   final Filter filter, final Hints hints)
    {
        for (final Iterator<T> it=getUnfilteredProviders(category); it.hasNext();) {
            final T candidate = it.next();
            // Implementation class must be tested before 'isAcceptable'
            // in order to avoid StackOverflowError in some situations.
            if (implementationType != null && !implementationType.isInstance(candidate)) {
                continue;
            }
            if (!isAcceptable(candidate, category, hints, filter, true)) {
                continue;
            }
            return candidate;
        }
        return null;
    }

    /**
     * Returns {@code true} if the specified {@code factory} meets the requirements specified by
     * a map of {@code hints} and the filter. This method is the entry point for the following
     * public methods:
     * <p>
     * <ul>
     *   <li>Singleton {@link #getServiceProvider (Class category, Filter, Hints, Hints.Key)}</li>
     *   <li>Iterator  {@link #getServiceProviders(Class category, Filter, Hints)}</li>
     * </ul>
     *
     * @param candidate The factory to check.
     * @param category  The factory category. Usually an interface.
     * @param hints     The optional user requirements, or {@code null}.
     * @param filter    The optional filter, or {@code null}.
     * @param wantCause {@code true} for storing the failure cause in the {@link #failureCause}
     *          field, or {@code false} for discarting the cause. This argument is {@code true}
     *          only when failure to find a factory will cause a {@link FactoryNotFoundException}
     *          to be thrown, in which case we will want to add the cause to the exception to be
     *          thrown.
     * @return {@code true} if the {@code factory} meets the user requirements.
     */
    final <T> boolean isAcceptable(final T candidate, final Class<T> category,
            final Hints hints, final Filter filter, final boolean wantCause)
    {
        /*
         * Note: availability() must be tested before checking the hints, because in current
         * Geotk implementation some hints computation are deferred until a connection to the
         * database is established (which availability() does in order to test the connection).
         */
        ConformanceResult failure = null;
        if (candidate instanceof Factory) {
            final Factory factory = (Factory) candidate;
            final Class<? extends Factory> type = factory.getClass();
            if (!testingAvailability.add(type)) {
                throw new RecursiveSearchException(type);
            }
            try {
                failure = factory.availability();
                if (Boolean.TRUE.equals(failure.pass())) {
                    failure = null; // Means "no failure".
                } else {
                    unavailable(factory);
                    if (!wantCause || failureCause != null) {
                        /*
                         * If the caller is not interested about the cause of the failure,
                         * or if a cause has already been reported for a previous factory,
                         * we can return immediately. Otherwise we need to remember that we
                         * failed, but continue nevertheless for making sure that the cause
                         * is pertinent (we don't want to report failures for factories that
                         * the user didn't asked for).
                         */
                        return false;
                    }
                }
            } finally {
                if (!testingAvailability.remove(type)) {
                    throw new AssertionError(type); // Should never happen.
                }
            }
        }
        /*
         * Applies the user-provided filter only after having tested for availability,
         * because some implementations may require a connection to a database. Note
         * however that the filter should still work; they may just be less accurate.
         */
        if (filter!=null && !filter.filter(candidate)) {
            return false;
        }
        /*
         * Now checks for implementation hints.
         */
        if (candidate instanceof Factory) {
            final Factory factory = (Factory) candidate;
            if (!XCollections.isNullOrEmpty(hints)) {
                /*
                 * Ask for implementation hints with special care against infinite recursivity.
                 * Some implementations use deferred algorithms fetching dependencies only when
                 * first needed. The call to getImplementationHints() is sometime a trigger for
                 * fetching dependencies (in order to return accurate hints).   For example the
                 * CachingCoordinateOperationFactory  implementation asks for an other instance
                 * of CoordinateOperationFactory, which can not be itself. Of course the caching
                 * class will checks that it is not caching itself, but its test happen too late
                 * for preventing a never-ending loop if we don't put a 'testingHints' guard here.
                 * It is also a safety against broken factory implementations.
                 */
                if (!testingHints.add(factory)) {
                    return false;
                }
                try {
                    if (!factory.hasCompatibleHints(hints)) {
                        return false;
                    }
                } finally {
                    if (!testingHints.remove(factory)) {
                        throw new AssertionError(factory); // Should never happen.
                    }
                }
            }
        }
        /*
         * Checks for optional user conditions supplied in FactoryRegistry subclasses. If
         * we pass this final test but the factory is not available (as detected sooner),
         * now we can remember the cause.
         */
        if (!isAcceptable(candidate, category, hints)) {
            return false;
        }
        if (failure != null) {
            if (failure instanceof Factory.Availability) {
                failureCause = ((Factory.Availability) failure).getFailureCause();
            }
            return false;
        }
        return true;
    }

    /**
     * Returns {@code true} if the specified {@code provider} meets the requirements specified by
     * a map of {@code hints}. The default implementation always returns {@code true}. There is no
     * need to override this method for {@link Factory} implementations, since their hints are
     * automatically checked. Override this method for non-Geotk implementations.
     * For example a JTS geometry factory finder may overrides this method in order to check
     * if a {@link com.vividsolutions.jts.geom.GeometryFactory} uses the required
     * {@link com.vividsolutions.jts.geom.CoordinateSequenceFactory}. Such method should be
     * implemented as below, since this method may be invoked for various kind of objects:
     *
     * {@preformat java
     *     if (provider instanceof GeometryFactory) {
     *         // ... Check the GeometryFactory state here.
     *     }
     * }
     *
     * @param <T>      The class represented by the {@code category} argument.
     * @param provider The provider to check.
     * @param category The factory category. Usually an interface.
     * @param hints    The user requirements, or {@code null} if none.
     * @return {@code true} if the {@code provider} meets the user requirements.
     *
     * @level advanced
     */
    protected <T> boolean isAcceptable(final T provider, final Class<T> category, final Hints hints) {
        return true;
    }

    /**
     * Invoked when a factory declares itself as unavailable. The default implementation does
     * nothing. Subclasses can override this method if they want to track those unavailable
     * factories.
     *
     * @param factory The factory which declares itself as unavailable.
     *
     * @see Factory#availability
     *
     * @since 3.01
     */
    void unavailable(final Factory factory) {
    }

    /**
     * Returns all class loaders to be used for scanning plugins. Current implementation
     * returns the following class loaders:
     * <p>
     * <ul>
     *   <li>{@linkplain Class#getClassLoader This object class loader}</li>
     *   <li>{@linkplain Thread#getContextClassLoader The thread context class loader}</li>
     *   <li>{@linkplain ClassLoader#getSystemClassLoader The system class loader}</li>
     * </ul>
     * <p>
     * The actual number of class loaders may be smaller if redundancies was found.
     * If some more classloaders should be scanned, they shall be added into the code
     * of this method.
     *
     * @return All classloaders to be used for scanning plugins.
     */
    public Set<ClassLoader> getClassLoaders() {
        final Set<ClassLoader> loaders = new HashSet<>(6);
        for (int i=0; i<4; i++) {
            final ClassLoader loader;
            try {
                switch (i) {
                    case 0:  loader = getClass().getClassLoader();                    break;
                    case 1:  loader = FactoryRegistry.class.getClassLoader();         break;
                    case 2:  loader = Thread.currentThread().getContextClassLoader(); break;
                    case 3:  loader = ClassLoader.getSystemClassLoader();             break;
                    // Add any supplementary class loaders here, if needed.
                    default: throw new AssertionError(i); // Should never happen.
                }
            } catch (SecurityException exception) {
                // We are not allowed to get a class loader.
                // Continue; some other class loader may be available.
                continue;
            }
            loaders.add(loader);
        }
        loaders.remove(null);
        /*
         * We now have a set of class loaders with duplicated object already removed
         * (e.g. system classloader == context classloader). However, we may still
         * have an other form of redundancie. A class loader may be the parent of an
         * other one. Try to remove those dependencies.
         */
        final ClassLoader[] asArray = loaders.toArray(new ClassLoader[loaders.size()]);
        for (int i=0; i<asArray.length; i++) {
            ClassLoader loader = asArray[i];
            try {
                while ((loader=loader.getParent()) != null) {
                    loaders.remove(loader);
                }
            } catch (SecurityException exception) {
                // We are not allowed to fetch the parent class loader.
                // Ignore (some redundancies may remains).
            }
        }
        if (loaders.isEmpty()) {
            LOGGER.warning("No class loaders available.");
        }
        return loaders;
    }

    /**
     * Scans for factory plug-ins on the application class path. This method is
     * needed because the application class path can theoretically change, or
     * additional plug-ins may become available. Rather than re-scanning the
     * classpath on every invocation of the API, the class path is scanned
     * automatically only on the first invocation. Clients can call this
     * method to prompt a re-scan. Thus this method need only be invoked by
     * sophisticated applications which dynamically make new plug-ins
     * available at runtime.
     *
     * @level advanced
     */
    public void scanForPlugins() {
        needScanForPlugins = null;
        final Set<ClassLoader> loaders = getClassLoaders();
        for (final Iterator<Class<?>> categories=getCategories(); categories.hasNext();) {
            final Class<?> category = categories.next();
            scanForPlugins(loaders, category);
        }
    }

    /**
     * Scans for factory plug-ins of the given category, with guard against recursivities.
     * The recursivity check make debugging easier than inspecting a {@link StackOverflowError}.
     *
     * @param loader The class loader to use.
     * @param category The category to scan for plug-ins.
     */
    private <T> void scanForPlugins(final Collection<ClassLoader> loaders, final Class<T> category) {
        if (!scanningCategories.add(category)) {
            throw new RecursiveSearchException(category);
        }
        try {
            final StringBuilder message = getLogHeader(category);
            boolean newServices = false;
            /*
             * First, scan META-INF/services directories (the default mechanism).
             */
            for (final ClassLoader loader : loaders) {
                newServices |= register(lookupProviders(category, loader), category, message);
            }
            /*
             * Next, query the user-provider iterators (if any).
             */
            final FactoryIteratorProvider[] fip = FactoryIteratorProviders.GLOBAL.getIteratorProviders();
            for (int i=0; i<fip.length; i++) {
                final Iterator<T> it = fip[i].iterator(category);
                if (it != null) {
                    newServices |= register(it, category, message);
                }
            }
            /*
             * Finally, log the list of registered factories.
             */
            if (newServices) {
                log("scanForPlugins", message);
            }
        } finally {
            if (!scanningCategories.remove(category)) {
                throw new AssertionError(category);
            }
        }
        /*
         * After loading all plugins for the current category, gives factories a chance to setup
         * their ordering relative to other factories. This operation must be inconditional, even
         * if there is no new plugins (newServices == false) because the scan may have registered
         * again existing plugins, in which case their previous ordering have been lost and must
         * be reset.
         */
        final Iterator<T> it = getServiceProviders(category, false);
        while (it.hasNext()) {
            final T candidate = it.next();
            if (candidate instanceof Factory) {
                final Factory factory = (Factory) candidate;
                factory.setOrdering(factory.new Organizer(this, category));
            }
        }
        pluginScanned(category);
    }

    /**
     * Scans the given category for plugins only if needed. After this method has been
     * invoked once for a given category, it will no longer scan for that category.
     */
    private <T> void scanForPluginsIfNeeded(final Class<?> category) {
        if (needScanForPlugins != null && needScanForPlugins.remove(category)) {
            if (needScanForPlugins.isEmpty()) {
                needScanForPlugins = null;
            }
            scanForPlugins(getClassLoaders(), category);
        }
    }

    /**
     * Invoked after the factories of the given category have been scanned. The default
     * implementation does nothing, which is usually the desired behavior (the public API
     * should be used instead). However {@link FactoryFinder} uses this method as an easy
     * hook for setting ordering based on the plugin vendor.
     *
     * @param category The category of the plugins which have been added.
     *
     * @since 3.02
     */
    void pluginScanned(final Class<?> category) {
    }

    /**
     * {@linkplain #registerServiceProvider Registers} all factories given by the
     * supplied iterator.
     *
     * @param factories The factories (or "service providers") to register.
     * @param category  the category under which to register the providers.
     * @param message   A buffer where to write the logging message.
     * @return {@code true} if at least one factory has been registered.
     */
    private <T> boolean register(final Iterator<T> factories, final Class<T> category, final StringBuilder message) {
        boolean newServices = false;
        final String lineSeparator = System.lineSeparator();
        while (factories.hasNext()) {
            T factory;
            try {
                factory = factories.next();
            } catch (OutOfMemoryError error) {
                // Makes sure that we don't try to handle this error.
                throw error;
            } catch (NoClassDefFoundError error) {
                /*
                 * A provider can't be registered because of some missing dependencies.
                 * This occurs for example when trying to register the WarpTransform2D
                 * math transform on a machine without JAI installation. Since the factory
                 * may not be essential (this is the case of WarpTransform2D), just skip it.
                 */
                loadingFailure(category, error, false);
                continue;
            } catch (ExceptionInInitializerError error) {
                /*
                 * If an exception occurred during class initialization, log the cause.
                 * The ExceptionInInitializerError alone doesn't help enough.
                 */
                final Throwable cause = error.getCause();
                if (cause != null) {
                    loadingFailure(category, cause, true);
                }
                throw error;
            } catch (Error error) {
                if (!Classes.getShortClassName(error).equals(
                     Classes.getShortName(ServiceConfigurationError.class)))
                {
                    /*
                     * In Java 6, ServiceLoader throws java.util.ServiceConfigurationError.
                     * In Java 4, ServiceRegistry throws sun.misc.ServiceConfigurationError.
                     * We want to catch those two errors and let all other propagate. However
                     * the later is not committed API and we don't known if Sun will replace it by
                     * the exception from java.util package in a future version. Using reflection
                     * allows us to catch both cases without relying to sun.misc package.
                     */
                    throw error;
                }
                /*
                 * Failed to register a factory for a reason probably related to the plugin
                 * initialisation. It may be some factory-dependent missing resources.
                 */
                loadingFailure(category, error, true);
                continue;
            }
            final Class<? extends T> factoryClass = factory.getClass().asSubclass(category);
            /*
             * If the factory implements more than one interface and an instance were
             * already registered, reuse the same instance instead of duplicating it.
             */
            final T replacement = getServiceProviderByClass(factoryClass);
            if (replacement != null) {
                factory = replacement;
                // Need to register anyway, because the category may not be the same.
            }
            if (registerServiceProvider(factory, category)) {
                /*
                 * The factory is now registered. Add it to the message to be logged. We will log
                 * all factories together in a single log event because some registration (e.g.
                 * MathTransformProviders) would be otherwise quite verbose.
                 */
                message.append(lineSeparator).append("  \u2022 ").append(factoryClass.getCanonicalName());
                if (replacement != null) {
                    message.append("  (shared)"); // TODO: localize
                }
                newServices = true;
            }
        }
        return newServices;
    }

    /**
     * Invoked when a factory can't be loaded. Logs a warning, but do not stop the process.
     */
    private static void loadingFailure(final Class<?> category, final Throwable error,
                                       final boolean showStackTrace)
    {
        final String         name = Classes.getShortName(category);
        final StringBuilder cause = new StringBuilder(Classes.getShortClassName(error));
        final String      message = error.getLocalizedMessage();
        if (message != null) {
            cause.append(": ").append(message);
        }
        final LogRecord record = Loggings.format(Level.WARNING,
                Loggings.Keys.CANT_LOAD_SERVICE_2, name, cause.toString());
        if (showStackTrace) {
            record.setThrown(error);
        }
        record.setSourceClassName(FactoryRegistry.class.getName());
        record.setSourceMethodName("scanForPlugins");
        record.setLoggerName(LOGGER.getName());
        LOGGER.log(record);
    }

    /**
     * Prepares a message to be logged if any provider has been registered.
     */
    private static StringBuilder getLogHeader(final Class<?> category) {
        return new StringBuilder(Loggings.getResources(null).getString(
                Loggings.Keys.FACTORY_IMPLEMENTATIONS_1, category));
    }

    /**
     * Logs the specified message after the registration of all providers for a given category.
     */
    private static void log(final String method, final StringBuilder message) {
        final LogRecord record = new LogRecord(Level.CONFIG, message.toString());
        record.setSourceClassName(FactoryRegistry.class.getName());
        record.setSourceMethodName(method);
        record.setLoggerName(LOGGER.getName());
        LOGGER.log(record);
    }

    /**
     * Synchronizes the content of the {@link #globalConfiguration} with
     * {@link FactoryIteratorProviders#GLOBAL}. New providers are {@linkplain #register registered}
     * immediately. Note that this method is typically invoked in a different thread than
     * {@link FactoryIteratorProviders} method calls.
     *
     * @see FactoryIteratorProviders#addFactoryIteratorProvider
     */
    private void synchronizeIteratorProviders() {
        final FactoryIteratorProvider[] newProviders =
                globalConfiguration.synchronizeIteratorProviders();
        if (newProviders == null) {
            return;
        }
        for (final Iterator<Class<?>> categories=getCategories(); categories.hasNext();) {
            final Class<?> category = categories.next();
            if (needScanForPlugins == null || !needScanForPlugins.contains(category)) {
                /*
                 * Register immediately the factories only if some other factories were already
                 * registered for this category,  because in such case scanForPlugin() will not
                 * be invoked automatically. If no factory are registered for this category, do
                 * nothing - we will rely on the lazy invocation of scanForPlugins() when first
                 * needed. We perform this check because getServiceProviders(category).hasNext()
                 * is the criterion used by FactoryRegistry in order to decide if it should invoke
                 * automatically scanForPlugins().
                 */
                for (int i=0; i<newProviders.length; i++) {
                    register(newProviders[i], category);
                }
            }
        }
    }

    /**
     * Registers every factories from the specified provider for the given category.
     */
    private <T> void register(final FactoryIteratorProvider provider, final Class<T> category) {
        final Iterator<T> it = provider.iterator(category);
        if (it != null) {
            final StringBuilder message = getLogHeader(category);
            if (register(it, category, message)) {
                log("synchronizeIteratorProviders", message);
            }
        }
    }

    /**
     * Sets pairwise ordering between all factories according a comparator. Calls to
     * <code>{@linkplain Comparator#compare compare}(factory1, factory2)</code> should returns:
     * <ul>
     *   <li>{@code -1} if {@code factory1} is preferred to {@code factory2}</li>
     *   <li>{@code +1} if {@code factory2} is preferred to {@code factory1}</li>
     *   <li>{@code 0} if there is no preferred order between {@code factory1} and
     *       {@code factory2}</li>
     * </ul>
     *
     * @param  <T>        The class represented by the {@code category} argument.
     * @param  category   The category to set ordering.
     * @param  comparator The comparator to use for ordering.
     * @return {@code true} if at least one ordering setting has been modified as a consequence
     *         of this call.
     *
     * @level advanced
     */
    public <T> boolean setOrdering(final Class<T> category, final Comparator<T> comparator) {
        boolean set = false;
        final List<T> previous = new ArrayList<>();
        for (final Iterator<T> it=getServiceProviders(category, false); it.hasNext();) {
            final T f1 = it.next();
            for (int i=previous.size(); --i>=0;) {
                final T f2 = previous.get(i);
                final int c;
                try {
                    c = comparator.compare(f1, f2);
                } catch (ClassCastException exception) {
                    /*
                     * This exception is expected if the user-supplied comparator follows strictly
                     * the java.util.Comparator specification and has determined that it can't
                     * compare the supplied factories. From ServiceRegistry point of view, it just
                     * means that the ordering between those factories will stay undeterminated.
                     */
                    continue;
                }
                if (c > 0) {
                    set |= setOrdering(category, f1, f2);
                } else if (c < 0) {
                    set |= setOrdering(category, f2, f1);
                }
            }
            previous.add(f1);
        }
        return set;
    }

    /**
     * Sets or unsets a pairwise ordering between all factories meeting a criterion. For example
     * in the CRS framework, this is used for setting ordering between all factories provided by
     * two vendors, or for two authorities. If one or both factories are not currently registered,
     * or if the desired ordering is already set/unset, nothing happens and false is returned.
     *
     * @param <T>      The class represented by the {@code base} argument.
     * @param base     The base category. Only categories {@linkplain Class#isAssignableFrom
     *                 assignable} to {@code base} will be processed.
     * @param service1 Filter for the preferred factory.
     * @param service2 Filter for the factory to which {@code service1} is preferred.
     * @return {@code true} if the ordering changed as a result of this call.
     *
     * @level advanced
     */
    public <T> boolean setOrdering(final Class<T> base, final Filter service1, final Filter service2) {
        return setOrUnsetOrdering(base, service1, service2, true);
    }

    /**
     * Unset an previously set ordering.
     *
     * @param <T>      The class represented by the {@code base} argument.
     * @param base     The base category. Only categories {@linkplain Class#isAssignableFrom
     *                 assignable} to {@code base} will be processed.
     * @param service1 Filter for the preferred factory.
     * @param service2 Filter for the factory to which {@code service1} was preferred.
     * @return {@code true} if the ordering changed as a result of this call.
     *
     * @level advanced
     */
    public <T> boolean unsetOrdering(final Class<T> base, final Filter service1, final Filter service2) {
        return setOrUnsetOrdering(base, service1, service2, false);
    }

    /**
     * Implementation of set/unsetOrdering.
     *
     * @param set {@code true} for setting the ordering, or {@code false} for unsetting.
     */
    final <T> boolean setOrUnsetOrdering(final Class<T> base,
            final Filter service1, final Filter service2, final boolean set)
    {
        boolean done = false;
        for (final Iterator<Class<?>> categories=getCategories(); categories.hasNext();) {
            final Class<?> candidate = categories.next();
            if (base.isAssignableFrom(candidate)) {
                final Class<? extends T> category = candidate.asSubclass(base);
                done |= setOrUnsetOrdering(category, set, service1, service2);
            }
        }
        return done;
    }

    /**
     * Helper method for the above. Defined as a separated method because of generic type.
     */
    private <T> boolean setOrUnsetOrdering(final Class<T> category, final boolean set,
                                           final Filter service1, final Filter service2)
    {
        boolean done = false;
        List<T> precedences = new ArrayList<>(); // The plugins of the service which have precedence.
        for (final Iterator<? extends T> it=getServiceProviders(category, true); it.hasNext();) {
            final T factory = it.next();
            if (service1.filter(factory)) {
                precedences.add(factory);
            }
        }
        if (precedences != null) {
            for (final Iterator<? extends T> it=getServiceProviders(category, false); it.hasNext();) {
                final T factory = it.next();
                if (service2.filter(factory)) {
                    for (final T precedence : precedences) {
                        if (precedence != factory) {
                            if (set) done |=   setOrdering(category, precedence, factory);
                            else     done |= unsetOrdering(category, precedence, factory);
                        }
                    }
                }
            }
        }
        return done;
    }

    /**
     * Returns a string representation of this registry for debugging purpose.
     * The default implementation list the providers in a tabular format.
     *
     * @return A string representation of this registry content.
     */
    @Override
    public String toString() {
        return new FactoryPrinter(this).toString();
    }
}
