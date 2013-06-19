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
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.geotoolkit.resources.Errors;
import org.apache.sis.util.logging.Logging;
import org.apache.sis.util.Classes;


/**
 * A {@linkplain FactoryRegistry factory registry} having the additional capability to create new
 * factory instances on the fly. Instances can be created dynamically when the following conditions
 * are meet:
 * <p>
 * <ul>
 *   <li>{@link FactoryRegistry#getServiceProvider FactoryRegistry.getServiceProvider(...)}
 *       found no suitable instance for the given hints.<li>
 *   <li>At least one registered factory has a public constructor expecting a single
 *       {@link Hints} argument.</li>
 * </ul>
 * <p>
 * New factories are cached using {@linkplain WeakReference weak references}. Calls to
 * {@link #getServiceProvider getServiceProvider} first check if a previously created
 * factory can fit.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Jody Garnett (Refractions)
 * @version 3.03
 *
 * @since 2.1
 * @module
 */
public class DynamicFactoryRegistry extends FactoryRegistry {
    /**
     * The array of classes for searching the one-argument constructor.
     */
    private static final Class<?>[] HINTS_ARGUMENT = new Class<?>[] {Hints.class};

    /**
     * List of factories already created. Used as a cache.
     */
    private final Map<Class<?>, List<Reference<?>>> cache = new HashMap<>();

    /**
     * Objects under construction for each implementation class.
     * Used by {@link #safeCreate} as a guard against infinite recursivity.
     */
    private final Set<Class<?>> underConstruction = new HashSet<>();

    /**
     * The factories which have been declared unavailable. Used in order to avoid
     * trying the same factory twice when we already failed in a previous attempt.
     */
    private final Set<Class<? extends Factory>> unavailables = new HashSet<>();

    /**
     * Constructs a new registry for the specified category.
     *
     * @param category The single category.
     *
     * @since 2.4
     */
    public DynamicFactoryRegistry(final Class<?> category) {
        super(category);
    }

    /**
     * Constructs a new registry for the specified categories.
     *
     * @param categories The categories.
     *
     * @since 2.4
     */
    public DynamicFactoryRegistry(final Class<?>[] categories) {
        super(categories);
    }

    /**
     * Constructs a new registry for the specified categories.
     *
     * @param categories The categories.
     */
    public DynamicFactoryRegistry(final Collection<Class<?>> categories) {
        super(categories);
    }

    /**
     * Returns the providers available in the cache.
     *
     * @return The list of cached providers (never {@code null}).
     */
    private <T> List<Reference<T>> getCachedProviders(final Class<T> category) {
        List<Reference<?>> c = cache.get(category);
        if (c == null) {
            c = new LinkedList<>();
            cache.put(category, c);
        }
        @SuppressWarnings({"unchecked","rawtypes"})
        final List<Reference<T>> cheat = (List) c;
        /*
         * Should be safe because we created an empty list, there is no other place where this
         * list is created and from this point we can only insert elements restricted to type T.
         */
        return cheat;
    }

    /**
     * Caches the specified factory under the specified category.
     */
    private <T> void cache(final Class<T> category, final T factory) {
        getCachedProviders(category).add(new WeakReference<>(factory));
    }

    /**
     * Makes this {@code FactoryRegistry} ready for next use.
     */
    @Override
    final void reset() {
        super.reset();
        unavailables.clear();
    }

    /**
     * Returns a provider for the specified category, using the specified map of hints (if any).
     * If a provider matching the requirements is found in the registry, it is returned. Otherwise,
     * a new provider is created and returned. This creation step is the only difference between
     * this method and the {@linkplain FactoryRegistry#getServiceProvider super-class method}.
     *
     * @param  <T> The category to look for.
     * @param  category The category to look for.
     * @param  filter   An optional filter, or {@code null} if none.
     * @param  hints    A {@linkplain Hints map of hints}, or {@code null} if none.
     * @param  key      The key to use for looking for a user-provided instance in the hints, or
     *                  {@code null} if none.
     * @return A factory for the specified category and hints (never {@code null}).
     * @throws FactoryNotFoundException if no factory was found, and the specified hints don't
     *         provide sufficient information for creating a new factory.
     * @throws FactoryRegistryException if the factory can't be created for some other reason.
     */
    @Override
    final <T> T getOrCreateServiceProvider(final Class<T> category, final Filter filter,
            Hints hints, final Hints.ClassKey key) throws FactoryRegistryException
    {
        final FactoryNotFoundException notFound;
        try {
            return super.getOrCreateServiceProvider(category, filter, hints, key);
        } catch (FactoryNotFoundException exception) {
            // Will be rethrown later in case of failure to create the factory.
            notFound = exception;
        }
        /*
         * No existing factory found. Creates one using reflection. First, we
         * check if an implementation class was explicitly specified by the user.
         */
        final Class<?>[] types;
        if (hints == null || key == null) {
            types = null;
        } else {
            final Object hint = hints.get(key);
            if (hint == null) {
                types = null;
            } else {
                if (hint instanceof Class<?>[]) {
                    types = (Class<?>[]) hint;
                } else {
                    types = new Class<?>[] {(Class<?>) hint};
                    // Should not fail, since non-class argument should
                    // have been accepted by 'super.getServiceProvider'.
                }
                /*
                 * At this point we have a list of classes that we can try to instantiate.
                 * Invokes the constructor of the non-abstract classes. Note that we need
                 * to use a set of hints in which the "key" argument has been removed for
                 * the same reasons than in super.getServiceProvider(...) method. This is
                 * okay since we check the value of this hint explicitly in this method.
                 * This apply to the block of code after the loop as well.
                 */
                hints = hints.clone();
                if (hints.remove(key) != hint) {
                    // Should never happen unless changed concurrently in an other thread.
                    throw new AssertionError(hint);
                }
                for (int i=0; i<types.length; i++) {
                    final Class<?> type = types[i];
                    if (type!=null && category.isAssignableFrom(type)) {
                        if (unavailables.contains(type)) {
                            // We already tried this factory before and failed.
                            continue;
                        }
                        final int modifiers = type.getModifiers();
                        if (!Modifier.isAbstract(modifiers)) {
                            final T candidate = createSafe(category, type, hints);
                            if (candidate == null) {
                                continue;
                            }
                            if (isAcceptable(candidate, category, hints, filter, true)) {
                                cache(category, candidate);
                                return candidate;
                            }
                            dispose(candidate);
                        }
                    }
                }
            }
        }
        /*
         * No implementation hint provided. Search the first implementation
         * accepting a Hints argument. No-args constructor will be ignored.
         * Note: all Factory objects should be fully constructed by now,
         * since the super-class has already iterated over all factories.
         */
        for (final Iterator<T> it=getUnfilteredProviders(category); it.hasNext();) {
            final T factory = it.next();
            final Class<?> implementationType = factory.getClass();
            if (unavailables.contains(implementationType)) {
                // We already tried this factory before and failed.
                continue;
            }
            if (!Classes.isAssignableToAny(implementationType, types)) {
                continue;
            }
            if (filter!=null && !filter.filter(factory)) {
                continue;
            }
            final T candidate;
            try {
                candidate = createSafe(category, implementationType, hints);
            } catch (FactoryNotFoundException exception) {
                // The factory has a dependency which has not been found.
                // Be tolerant to that kind of error.
                Logging.recoverableException(LOGGER, DynamicFactoryRegistry.class, "getServiceProvider", exception);
                continue;
            } catch (FactoryRegistryException exception) {
                if (exception.getCause() instanceof NoSuchMethodException) {
                    // No accessible constructor with the expected argument.
                    // Try an other implementation.
                    continue;
                } else {
                    // Other kind of error, probably unexpected.
                    // Let the exception propagates.
                    throw exception;
                }
            }
            if (candidate == null) {
                continue;
            }
            if (isAcceptable(candidate, category, hints, filter, true)) {
                cache(category, candidate);
                return candidate;
            }
            dispose(candidate);
        }
        /*
         * Before to thrown the exception, check if a new cause is available. We may have a new
         * cause because all factories were available when we didn't asked for any hints, but
         * one of them failed when we tried to instantiate it with the user-supplied hints. It
         * may be for example because the user supplied a different JDBC connection.
         */
        initCause(notFound);
        throw notFound;
    }

    /**
     * Invoked when a factory declares itelf as unavailable. This method is used
     * for remembering that it is not worth to try creating that factory again.
     *
     * @param factory The factory which declares itself as unavailable.
     *
     * @since 3.01
     */
    @Override
    final void unavailable(final Factory factory) {
        unavailables.add(factory.getClass());
        super.unavailable(factory);
    }

    /**
     * If no instance was found in the method from the parent class, searches in the cache.
     *
     * @param  category           The category to look for. Usually an interface class.
     * @param  implementationType The desired class for the implementation, or {@code null} if none.
     * @param  filter             An optional filter, or {@code null} if none.
     * @param  hints              A {@linkplain Hints map of hints}, or {@code null} if none.
     * @return A factory for the specified category and hints, or {@code null} if none.
     */
    @Override
    final <T> T getServiceImplementation(final Class<T> category, final Class<?> implementationType,
                                         final Filter filter, final Hints hints)
    {
        T candidate = super.getServiceImplementation(category, implementationType, filter, hints);
        if (candidate != null) {
            return candidate;
        }
        final List<Reference<T>> cached = getCachedProviders(category);
        for (final Iterator<Reference<T>> it=cached.iterator(); it.hasNext();) {
            candidate = it.next().get();
            if (candidate == null) {
                it.remove();
                continue;
            }
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
     * Invokes {@link #createServiceProvider}, but checks against recursive calls. If the specified
     * implementation is already under construction, returns {@code null} in order to tell to
     * {@link #getServiceProvider} that it need to search for an other implementation. This is
     * needed for avoiding infinite recursivity when a factory is a wrapper around an ther factory
     * of the same category. For example this is the case of
     * {@link org.geotoolkit.referencing.operation.CachingCoordinateOperationFactory}.
     */
    private <T> T createSafe(final Class<T> category, final Class<?> implementation, final Hints hints) {
        if (!underConstruction.add(implementation)) {
            return null;
        }
        try {
            return createServiceProvider(category, implementation, hints);
        } finally {
            if (!underConstruction.remove(implementation)) {
                throw new AssertionError();
            }
        }
    }

    /**
     * Creates a new instance of the specified factory using the specified hints.
     * The default implementation tries to instantiate the given implementation class
     * using the first of the following constructors found:
     * <p>
     * <ul>
     *   <li>Constructor with a single {@link Hints} argument.</li>
     *   <li>No-argument constructor, except if the implementation class is already a registered
     *       provider (in which case it has already been evaluated by {@link FactoryRegistry}).</li>
     * </ul>
     *
     * @param  <T> The category to instantiate.
     * @param  category The category to instantiate.
     * @param  implementation The factory class to instantiate.
     * @param  hints The implementation hints.
     * @return The factory.
     * @throws FactoryRegistryException if the factory creation failed.
     *
     * @level advanced
     */
    protected <T> T createServiceProvider(final Class<T> category, final Class<?> implementation, final Hints hints)
            throws FactoryRegistryException
    {
        Throwable cause;
        try {
            Constructor<?> constructor;
            try {
                constructor = implementation.getConstructor(HINTS_ARGUMENT);
                return category.cast(constructor.newInstance(new Object[] {hints}));
            } catch (NoSuchMethodException exception) {
                // Constructor does not exist or is not accessible.
                // We will fallback on the no-argument constructor.
                cause = exception;
            }
            /*
             * No public constructor expecting a Hints argument. Search for a no-argument
             * constructor, provided that the registry doesn't already contains an object
             * of that class (otherwise it should have been found before this point). The
             * search for non-registered factory may happen if user's hints contain an
             * implementation class.
             */
            if (super.getServiceProviderByClass(implementation) == null) try {
                constructor = implementation.getConstructor((Class<?>[]) null);
                return category.cast(constructor.newInstance((Object[]) null));
            } catch (NoSuchMethodException exception) {
                /*
                 * No accessible constructor. Do not store the cause;
                 * we will retrown the previous exception instead.
                 */
            }
        } catch (InvocationTargetException exception) {
            /*
             * Exception in the invoked constructor. We will wrap the cause in a
             * FactoryRegistryException, discarting the InvocationTargetException.
             */
            cause = exception.getCause();
            if (cause instanceof FactoryRegistryException) {
                throw (FactoryRegistryException) cause;
            }
        } catch (ReflectiveOperationException exception) {
            /*
             * Constructor is not public or the class is abstract. This exception should never
             * happen since we asked for Class.getConstructor(...), which returns only public
             * constructors, and we checked for non-abstract class before to invoke this method.
             */
            cause = exception;
        }
        throw new FactoryRegistryException(Errors.format(
                Errors.Keys.CANT_CREATE_FACTORY_FOR_TYPE_1, implementation), cause);
    }

    /**
     * Disposes the specified factory. This method is invoked when a factory has been created,
     * and then {@code DynamicFactoryRegistry} determined that the factory doesn't meet user's
     * requirements.
     */
    private static void dispose(final Object factory) {
        if (factory instanceof Factory) {
            ((Factory) factory).dispose(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scanForPlugins() {
        cache.clear();
        super.scanForPlugins();
    }
}
