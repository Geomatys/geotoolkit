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
package org.geotoolkit.util.collection;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.internal.Threads;


/**
 * A thread replacing strong references in a {@link Cache} by weak references. We use a
 * single thread for that purpose in order to avoid the need for synchronization. This is
 * also an opportunist way to return the result of a cache operation faster and complete
 * this secondary work in a background thread.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
final class CacheReferences extends Thread {
    /**
     * Invoked in the {@link CachReferences} thread after a new entry has been added in a
     * {@link Cache}. The implementation must checks if some strong references need to be
     * replaced by weak of soft references.
     */
    static interface Handler {
        /**
         * Performs the replacement of strong references.
         * Will be invoked from the {@link CacheReferences} thread only.
         */
        void adjustReferences();

        /**
         * Invoked if we failed to add this handler in the queue of handlers
         * to process. This method may be invoked from any thread.
         */
        void cancel();
    }

    /**
     * The singleton instance.
     */
    static final CacheReferences INSTANCE = new CacheReferences();
    static {
        // Call to Thread.start() must be outside the constructor
        // (Reference: Goetz et al.: "Java Concurrency in Practice").
        INSTANCE.start();
    }

    /**
     * The queue where to add put worker that just added new elements to their cache.
     * We specify an arbitrary capacity limit in case new elements arrive much faster
     * than we can process them, but we should never even approach such high limit.
     */
    private final BlockingQueue<Handler> queue = new LinkedBlockingQueue<Handler>(100000);

    /**
     * Ensures that we have a singleton instance of this class.
     */
    private CacheReferences() {
        super(Threads.REFERENCE_CLEANERS, "CacheReferences");
        setPriority(MAX_PRIORITY - 2);
        setDaemon(true);
    }

    /**
     * Adds the specified worker to the queue of workers which have just set a value in their
     * cache. This method can be invoked from any thread and typically returns immediatly,
     * unless we really have a huge amount of items waiting to be processed.
     */
    final void add(final Handler worker) {
        if (!queue.offer(worker)) try {
            queue.put(worker);
        } catch (InterruptedException e) {
            /*
             * We blocked and someone doesn't want to let us sleep. Maybe the CacheReferences
             * thread is dead or some other serious problem happen. Do not cache the value;
             * maybe we will not cache anything anymore.
             */
            worker.cancel();
            Logging.severeException(null, Cache.Handler.class, "unlock", e);
        }
    }

    /**
     * The thread loop.
     */
    @Override
    public void run() {
        /*
         * The Queue should never be null. However some strange behavior has been reported
         * in org.geotoolkit.util.collection.WeakCollectionCleaner.run() and we are applying
         * the same defensive check here.
         */
        while (queue != null) {
            try {
                final Handler worker = queue.take();
                worker.adjustReferences();
            } catch (InterruptedException exception) {
                // Somebody doesn't want to lets us sleep... Go back to work.
            } catch (Exception exception) {
                Logging.unexpectedException(CacheReferences.class, "run", exception);
            } catch (AssertionError exception) {
                Logging.unexpectedException(CacheReferences.class, "run", exception);
                // Do not kill the thread on assertion failure, in order to
                // keep the same behaviour as if assertions were turned off.
            }
        }
        Logging.getLogger(CacheReferences.class).severe("Daemon stopped."); // Should never happen.
    }
}
