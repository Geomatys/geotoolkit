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
package org.geotoolkit.factory;

import java.util.Set;
import java.util.LinkedHashSet;
import net.jcip.annotations.ThreadSafe;

import org.apache.sis.util.ArraysExt;


/**
 * The list of registered {@linkplain FactoryIteratorProvider factory iterator providers}.
 *
 * @author Martin Desruisseaux (IRD)
 * @since 2.4
 *
 * @version 3.00
 * @module
 */
@ThreadSafe
final class FactoryIteratorProviders {
    /**
     * The system-wide configuration. This is the instance configured by
     * the public static methods provided in this class.
     */
    static final FactoryIteratorProviders GLOBAL = new FactoryIteratorProviders();

    /**
     * Incremented every time a modification is performed. There is no need to use
     * {@link java.util.concurrent.atomic.AtomicInteger} because all access to this
     * field will be done in a synchronized block.
     */
    private int modifications = 0;

    /**
     * Alternative scanning methods used by {@link FactoryRegistry#scanForPlugins(Collection,Class)}
     * in addition of the default lookup mechanism. Will be created only when first needed.
     */
    private Set<FactoryIteratorProvider> iteratorProviders;

    /**
     * Creates an initially empty set of factories.
     */
    FactoryIteratorProviders() {
    }

    /**
     * Synchronizes the content of the {@link #iteratorProviders} map with the {@linkplain #GLOBAL
     * global} one. New providers are returned for later {@linkplain FactoryRegistry#register
     * registration}. Note that this method is typically invoked in a different thread than
     * {@link FactoryIteratorProviders} public static method calls.
     *
     * @return The new iterators providers {@linkplain #addFactoryIteratorProvider added} since
     *         the last time this method was invoked, or {@code null} if none.
     */
    final FactoryIteratorProvider[] synchronizeIteratorProviders() {
        /*
         * Do not synchronize "this". Only the GLOBAL instance needs to be synchronized. We
         * make no thread-safety guarantee for the others, as in the FactoryRegistry contract.
         */
        FactoryIteratorProvider[] newProviders = null;
        int count = 0;
        synchronized (GLOBAL) {
            if (modifications == GLOBAL.modifications) {
                return null;
            }
            modifications = GLOBAL.modifications;
            if (GLOBAL.iteratorProviders == null) {
                /*
                 * Should never happen. If GLOBAL.iteratorProviders was null, then every
                 * 'modifications' count should be 0 and this method should have returned 'null'.
                 */
                throw new AssertionError(modifications);
            }
            /*
             * If 'removeFactoryIteratorProvider(...)' has been invoked since the last time
             * this method was run, then synchronize 'iteratorProviders' accordingly. Current
             * implementation do not unregister the factories that were created by those iterators.
             */
            if (iteratorProviders != null) {
                iteratorProviders.retainAll(GLOBAL.iteratorProviders);
            } else if (!GLOBAL.iteratorProviders.isEmpty()) {
                iteratorProviders = new LinkedHashSet<>();
            }
            /*
             * If 'addFactoryIteratorProvider(...)' has been invoked since the last time
             * this method was run, then synchronize 'iteratorProviders' accordingly. We
             * keep trace of new providers in order to allow 'FactoryRegistry' to use them
             * for an immediate scanning.
             */
            int remaining = GLOBAL.iteratorProviders.size();
            for (final FactoryIteratorProvider candidate : GLOBAL.iteratorProviders) {
                if (iteratorProviders.add(candidate)) {
                    if (newProviders == null) {
                        newProviders = new FactoryIteratorProvider[remaining];
                    }
                    newProviders[count++] = candidate;
                }
                remaining--;
            }
        }
        // Note: newProviders may be null.
        return ArraysExt.resize(newProviders, count);
    }

    /**
     * Adds an alternative way to search for factory implementations. The public
     * facade for this method is {@link Factories#addFactoryIteratorProvider}.
     *
     * @param  provider A new provider for factory iterators.
     * @return {@code true} if the given provider has been added, or {@code false}
     *         if it was already present.
     */
    synchronized boolean addFactoryIteratorProvider(FactoryIteratorProvider provider) {
        if (iteratorProviders == null) {
            iteratorProviders = new LinkedHashSet<>();
        }
        if (iteratorProviders.add(provider)) {
            modifications++;
            return true;
        }
        return false;
    }

    /**
     * Removes a provider that was previously {@linkplain #addFactoryIteratorProvider added}.
     * Note that factories already obtained from the specified provider will not be
     * {@linkplain FactoryRegistry#deregisterServiceProvider deregistered} by this method.
     *
     * @param  provider The provider to remove.
     * @return {@code true} if the given provider has been removed, or {@code false}
     *         if it was not present.
     */
    synchronized boolean removeFactoryIteratorProvider(FactoryIteratorProvider provider) {
        if (iteratorProviders != null) {
            if (iteratorProviders.remove(provider)) {
                modifications++;
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all iterator providers. This method do not returns any live collection
     * since the array will be used outside the synchronized block.
     */
    synchronized FactoryIteratorProvider[] getIteratorProviders() {
        if (iteratorProviders == null) {
            return new FactoryIteratorProvider[0];
        }
        return iteratorProviders.toArray(new FactoryIteratorProvider[iteratorProviders.size()]);
    }
}
