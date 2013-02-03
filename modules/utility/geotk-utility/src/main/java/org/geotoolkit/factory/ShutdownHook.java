/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.Iterator;
import javax.imageio.spi.ServiceRegistry;
import net.jcip.annotations.ThreadSafe;

import org.apache.sis.util.ArraysExt;
import org.geotoolkit.internal.Threads;
import org.geotoolkit.internal.io.TemporaryFile;


/**
 * Disposes every factories on JVM shutdown. Performs also other shutdown service that
 * are better to be executed only after factories disposal, like executors shutdown.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @see Threads#ensureShutdownHookRegistered()
 *
 * @since 3.00
 * @module
 */
@ThreadSafe
final class ShutdownHook extends Thread {
    static {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }

    /**
     * The registries on which to dispose the factories on shutdown.
     */
    private static ServiceRegistry[] registries;

    /**
     * Creates the singleton instance.
     */
    private ShutdownHook() {
        super(Threads.RESOURCE_DISPOSERS, "ShutdownHook");
    }

    /**
     * Adds the given registry to the list of registry to dispose on shutdown.
     */
    static synchronized void register(final ServiceRegistry registry) {
        ServiceRegistry[] registries = ShutdownHook.registries;
        if (registries == null) {
            registries = new ServiceRegistry[] {registry};
        } else {
            registries = ArraysExt.append(registries, registry);
        }
        ShutdownHook.registries = registries;
    }

    /**
     * Disposes every factories. Note that some factories perform their disposal work in a
     * background thread, so we need to shutdown the thread executor only after we finished
     * to requested the disposal of every factories.
     */
    @Override
    public synchronized void run() {
        final ServiceRegistry[] registries = ShutdownHook.registries;
        if (registries != null) {
            ShutdownHook.registries = null;
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
        }
        /*
         * The following method should be invoked only when we think there is not any code still
         * runnning that may invoke Threads.executor(boolean). It is actually hard to ensure that,
         * but a search on Threads.SHUTDOWN_HOOKS and Threads.executeDisposal(Runnable) is helpful.
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
