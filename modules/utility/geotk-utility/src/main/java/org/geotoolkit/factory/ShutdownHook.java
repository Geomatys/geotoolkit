/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import org.geotoolkit.internal.Threads;
import org.geotoolkit.lang.ThreadSafe;


/**
 * Disposes every factories on JVM shutdown.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.00
 * @module
 */
@ThreadSafe
final class ShutdownHook extends Thread {
    /**
     * The single shutdown hook instance.
     */
    static final ShutdownHook INSTANCE = new ShutdownHook();
    static {
        Runtime.getRuntime().addShutdownHook(INSTANCE);
    }

    /**
     * The registries on which to dispose the factories on shutdown.
     */
    private ServiceRegistry[] registries = new ServiceRegistry[0];

    /**
     * Creates the singleton instance.
     */
    private ShutdownHook() {
        super(Threads.SHUTDOWN_HOOKS, "FactoryCleaner");
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
         * The following method should be invoked only when we think there is not any code still
         * runnning that may invoke Threads.executor(boolean). It is actually hard to ensure that,
         * but a search on Threads.SHUTDOWN_HOOKS and Threads.executor(boolean) is helpful.
         */
        if (!Threads.shutdown()) {
            // We can't use java.util.logging at this point since we are shutting down.
            System.err.println("WARNING: some background threads were not terminated.");
        }
    }
}
