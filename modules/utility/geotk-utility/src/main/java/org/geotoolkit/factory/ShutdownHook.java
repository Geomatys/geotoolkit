/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.factory;

import java.util.Arrays;
import java.util.Iterator;
import javax.imageio.spi.ServiceRegistry;
import net.jcip.annotations.ThreadSafe;

import org.geotoolkit.internal.Threads;
import org.geotoolkit.internal.io.TemporaryFile;


/**
 * Disposes every factories on JVM shutdown. Performs also other shutdown service that
 * are better to be executed only after factories disposal, like executors shutdown.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @see Threads#ensureShutdownHookRegistered()
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

    /**
     * The registries on which to dispose the factories on shutdown.
     */
    private ServiceRegistry[] registries;

    /**
     * Creates the singleton instance.
     */
    private ShutdownHook() {
        super(Threads.RESOURCE_DISPOSERS, "ShutdownHook");
    }

    /**
     * Adds the given registry to the list of registry to dispose on shutdown.
     */
    synchronized void register(final ServiceRegistry registry) {
        ServiceRegistry[] registries = this.registries;
        if (registries == null) {
            Runtime.getRuntime().addShutdownHook(this);
            registries = new ServiceRegistry[] {registry};
        } else {
            final int n = registries.length;
            registries = Arrays.copyOf(registries, n + 1);
            registries[n] = registry;
        }
        this.registries = registries;
    }

    /**
     * Disposes every factories.
     */
    @Override
    public synchronized void run() {
        final ServiceRegistry[] registries = this.registries;
        if (registries == null) {
            return;
        }
        this.registries = null;
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
         * but a search on Threads.SHUTDOWN_HOOKS and Threads.executor(boolean,boolean) is helpful.
         */
        Threads.shutdown();
        /*
         * Delete the temporary file after there is presumably no running service.
         */
        while (TemporaryFile.deleteAll()) {
            Thread.yield();
            // The loop exists as a paranoiac action in case TemporaryFile.deleteOnExit(...)
            // is being invoked concurrently, but it should never happen.
        }
    }
}
