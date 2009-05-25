/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Arrays;
import java.util.Iterator;
import javax.imageio.spi.ServiceRegistry;
import org.geotoolkit.internal.FactoryUtilities;


/**
 * Disposes every factories on JVM shutdown.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
final class ShutdownHook extends Thread {
    /**
     * The single shutdown hook instance.
     */
    static final ShutdownHook INSTANCE = new ShutdownHook();

    /**
     * How long to wait for the threads running {@link Factory#dispose} to die.  This is the
     * maximal time - we will wait that much time only if some thread are blocked or waiting
     * for network connections.
     */
    private static final int TIMEOUT = 12000;

    /**
     * The registries on which to dispose the factories on shutdown.
     */
    private ServiceRegistry[] registries = new ServiceRegistry[0];

    /**
     * Creates the singleton instance.
     */
    private ShutdownHook() {
        super("Factories shutdown");
        Runtime.getRuntime().addShutdownHook(this);
    }

    /**
     * Adds the given registry to the list of registry to dispose on shutdown.
     */
    synchronized void register(final ServiceRegistry registry) {
        final int n = registries.length;
        registries = Arrays.copyOf(registries, n + 1);
        registries[n] = registry;
    }

    /**
     * Disposes every factories.
     */
    @Override
    public synchronized void run() {
        final long limit = System.currentTimeMillis() + TIMEOUT;
        for (final ServiceRegistry registry : registries) {
            for (final Iterator<Class<?>> it=registry.getCategories(); it.hasNext();) {
                final Class<?> category = it.next();
                for (final Iterator<?> i=registry.getServiceProviders(category, false); i.hasNext();) {
                    final Object factory = i.next();
                    if (factory instanceof Factory) {
                        ((Factory) factory).dispose(true);
                    }
                }
            }
        }
        /*
         * Waits for disposal to complete. We wait at most 2 seconds per thread,
         * which is an arbitrary timeout.
         */
        final Thread[] threads = new Thread[FactoryUtilities.DISPOSAL_GROUP.activeCount()];
        int count = FactoryUtilities.DISPOSAL_GROUP.enumerate(threads);
        while (--count >= 0) {
            try {
                threads[count].join(Math.max(100, limit - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                // Someone doesn't want to let us sleep. Abandon the wait for this thread
                // and go wait for other threads. Do not log anything since we are in the
                // process of a shutdown and the logging system is not available anymore.
            }
        }
    }
}
