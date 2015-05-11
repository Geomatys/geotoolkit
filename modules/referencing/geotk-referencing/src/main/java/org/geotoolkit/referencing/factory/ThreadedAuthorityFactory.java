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
package org.geotoolkit.referencing.factory;

import java.util.Map;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.awt.RenderingHints;

import org.opengis.util.FactoryException;

import org.geotoolkit.lang.Debug;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.internal.Threads;
import org.geotoolkit.resources.Errors;
import org.apache.sis.util.Classes;

import static org.apache.sis.util.ArgumentChecks.ensureStrictlyPositive;


/**
 * A caching authority factory which delegates to different instances of a backing store for
 * concurrency in multi-thread environment. This factory delays the {@linkplain #createBackingStore
 * creation of a backing store} until first needed, and {@linkplain AbstractAuthorityFactory#dispose
 * dispose} it after some timeout. This approach allows to establish a connection to a database (for
 * example) and keep it only for a relatively short amount of time.
 *
 * {@section Multi-threading}
 * If two or more threads are accessing this factory in same time, then two or more instances
 * of the backing store may be created. The maximal amount of instances to create is specified
 * at {@code ThreadedAuthorityFactory} construction time. If more backing store instances are
 * needed, some of the threads will block until an instance become available.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 2.1
 * @module
 */
public abstract class ThreadedAuthorityFactory extends CachingAuthorityFactory {
    /**
     * A backing store used by {@link ThreadedAuthorityFactory}. A new instance is created
     * every time a backing factory is {@linkplain ThreadedAuthorityFactory#release released}.
     * In a mono-thread application, there is typically only one instance at a given time.
     * However if more than one than one thread are requesting new objects concurrently,
     * than many instances may exist for the same {@code ThreadedAuthorityFactory}.
     */
    private static final class Store {
        /**
         * The factory used as a backing store, which has just been released
         * and made available for reuse.
         */
        final AbstractAuthorityFactory factory;

        /**
         * The timestamp at the time this object has been created. Because instances of
         * {@code Store} are created when backing stores are released, this is the time
         * when we finished using that {@linkplain #factory}.
         */
        final long timestamp;

        /**
         * Creates a new instance wrapping the given factory.
         * The factory must be already available for reuse.
         */
        Store(final AbstractAuthorityFactory factory) {
            this.factory = factory;
            timestamp = System.currentTimeMillis();
        }

        /**
         * Returns a string representation for debugging purpose only.
         */
        @Override
        public String toString() {
            return String.format("Store[%s at %tT]", Classes.getShortClassName(factory), timestamp);
        }
    }

    /**
     * The backing store instances previously created and released for future reuse.
     * Last used factories must be {@linkplain Deque#addLast added last}. This is
     * used as a LIFO stack.
     */
    private final Deque<Store> stores;

    /**
     * The amount of backing stores that can still be created. This number is decremented
     * in a synchronized block every time a backing store is in use, and incremented once
     * released.
     */
    private int remainingBackingStores;

    /**
     * Counts how many time a factory has been used in the current thread. This is used in order to
     * reuse the same factory (instead than creating new instance) when an {@code AuthorityFactory}
     * implementation invokes itself indirectly through the {@link CachingAuthorityFactory}. This
     * assumes that factory implementations are reentrant.
     */
    private static final class Usage {
        /**
         * The factory used as a backing store.
         */
        AbstractAuthorityFactory factory;

        /**
         * Incremented on every call to {@link #getBackingStore()} and decremented on every call
         * to {@link #release}. When this value reach zero, the factory is really released.
         */
        int count;

        /**
         * Returns a string representation for debugging purpose only.
         */
        @Override
        public String toString() {
            return "Usage[" + Classes.getShortClassName(factory) + ": " + count + ']';
        }
    }

    /**
     * The factory currently in use by the current thread.
     */
    private final ThreadLocal<Usage> current = new ThreadLocal<Usage>() {
        @Override protected Usage initialValue() {
            return new Usage();
        }
    };

    /**
     * The delay of inactivity (in milliseconds) before to close a backing store.
     * The default value is one day, which is long enough to be like "no timeout"
     * for a normal working day while keeping a safety limit. Subclasses will set
     * a shorter value more suitable to server environment.
     * <p>
     * Every access to this field must be performed in a synchronized block.
     */
    private long timeout = 24 * 60 * 60 * 1000L;

    /**
     * The maximal difference between the scheduled time and the actual time in order to
     * perform the factory disposal. This is used as a tolerance value for possible wait
     * time inaccuracy.
     */
    static final long TIMEOUT_RESOLUTION = 100;

    /**
     * The task to run for disposing expired workers. May be run many time.
     *
     * @see #disposeExpired()
     */
    private final Runnable disposerTask = new Runnable() {
        @Override public void run() {
            disposeExpired();
        }
    };

    /**
     * {@code true} if the call to {@link #disposeExpired} is scheduled for future execution
     * in the background thread.  A value of {@code true} implies that this factory contains
     * at least one active backing store. However the reciprocal is not true: this field may
     * be set to {@code false} while a worker factory is currently in use because this field
     * is set to {@code true} only when the worker factory is {@linkplain #release released}.
     * <p>
     * Note that we can not use {@link !stores.isEmpty()} as a replacement of {@code isActive}
     * because the queue is empty if all backing stores are currently in use.
     * <p>
     * Every access to this field must be performed in a synchronized block.
     */
    private boolean isActive;

    /**
     * Tells if {@link ReferencingFactoryContainer#hints} has been invoked. It must be
     * invoked exactly once. We will initialize the hints as late as possible because
     * it implies the creation of a backing factory, which may be costly.
     */
    private volatile boolean hintsInitialized;

    /**
     * Constructs an instance using the default setting. Subclasses are responsible for
     * creating an appropriate backing store when the {@link #createBackingStore} method
     * is invoked.
     *
     * @param userHints An optional set of hints, or {@code null} for the default ones.
     *
     * @since 2.2
     */
    protected ThreadedAuthorityFactory(final Hints userHints) {
        this(userHints, DEFAULT_MAX, 16);
        /*
         * NOTE: if the default maximum number of backing stores (currently 16) is augmented,
         * make sure to augment the number of runner threads in the "StressTest" class to a
         * greater amount.
         */
    }

    /**
     * Constructs an instance using the given setting. Subclasses are responsible for
     * creating an appropriate backing store when the {@link #createBackingStore} method
     * is invoked.
     *
     * @param userHints An optional set of hints, or {@code null} for the default ones.
     * @param maxStrongReferences The maximum number of objects to keep by strong reference.
     * @param maxBackingStores The maximal amount of backing stores to create. This is the
     *        maximal amount of threads that can use this factory without blocking each other
     *        when the requested objects are not in the cache.
     *
     * @since 3.00
     */
    protected ThreadedAuthorityFactory(final Hints userHints,
            final int maxStrongReferences, final int maxBackingStores)
    {
        super(userHints, maxStrongReferences);
        ensureNotSmaller("maxBackingStores", maxBackingStores, 1);
        stores = new LinkedList<>();
        remainingBackingStores = maxBackingStores;
    }

    /**
     * Returns the implementation hints. At the opposite of most factories that delegate their work
     * to an other factory (like the {@code CachingAuthorityFactory} parent class), this method does
     * <strong>not</strong> set {@link Hints#CRS_AUTHORITY_FACTORY} and its friends to the backing
     * store. This is because the backing stores may be created and destroyed at any time, while the
     * implementation hints are expected to be stable. Instead, the implementation hints of a
     * backing store are copied straight in this {@code ThreadedAuthorityFactory} hint map.
     */
    @Override
    public Map<RenderingHints.Key, ?> getImplementationHints() {
        if (!hintsInitialized && !unavailable()) {
            AbstractAuthorityFactory factory = null;
            try {
                factory = getBackingStore();
                try {
                    final Map<RenderingHints.Key, ?> toAdd;
                    toAdd = factory.getImplementationHints();
                    /*
                     * Double-check locking: was a deprecated practice before Java 5, but is okay
                     * since Java 5 provided that 'hintsInitialized' is volatile. It is important
                     * to invoke factory.getImplementationHints()  outside the synchronized block
                     * in order to reduce the risk of deadlock. It is not a big deal if its value
                     * is computed twice.
                     */
                    synchronized (this) {
                        if (!hintsInitialized) {
                            hintsInitialized = true;
                            hints.putAll(toAdd);
                        }
                    }
                } finally {
                    release();
                }
            } catch (FactoryException exception) {
                synchronized (this) {
                    unavailable(exception, factory);
                    hintsInitialized = true; // For preventing other tries.
                }
            }
        }
        return super.getImplementationHints();
    }

    /**
     * Returns the number of backing stores. This count does not include the backing stores
     * that are currently under execution. This method is used only for testing purpose.
     */
    @Debug
    final synchronized int countBackingStores() {
        return stores.size();
    }

    /**
     * Creates the backing store authority factory. This method is invoked the first time a
     * {@code createXXX(...)} method is invoked. It may also be invoked again if additional
     * factories are needed in different threads, or if all factories have been disposed
     * after the timeout.
     *
     * {@section Synchronization}
     * This method needs to be thread-safe. {@code ThreadedAuthorityFactory} does not hold a lock
     * when invoking this method. Subclasses are responsible to apply their own synchronization if
     * needed, but are encouraged to avoid doing so if possible.
     * <p>
     * In addition, implementations should not invoke {@link #availability() availability()},
     * {@link #getImplementationHints()}, {@link #getAuthority() getAuthority()},
     * {@link #getVendor() getVendor()} or any {@code createXXX()} method in order to avoid
     * never-ending loop. If hints are needed, use the code below instead but keep in mind
     * that the map may not contains the definitive set of hints at this stage:
     *
     * {@preformat java
     *     final Hints localHints = EMPTY_HINTS.clone();
     *     synchronized (this) {
     *         localHints.putAll(hints);
     *     }
     * }
     *
     * @return The backing store to uses in {@code createXXX(...)} methods.
     * @throws NoSuchFactoryException if the backing store has not been found.
     * @throws FactoryException if the creation of backing store failed for an other reason.
     */
    protected abstract AbstractAuthorityFactory createBackingStore()
            throws NoSuchFactoryException, FactoryException;

    /**
     * Returns a backing store authority factory. This method <strong>must</strong>
     * be used together with {@link #release} in a {@code try ... finally} block.
     *
     * @return The backing store to uses in {@code createXXX(...)} methods.
     * @throws FactoryException if the creation of backing store failed.
     */
    @Override
    final AbstractAuthorityFactory getBackingStore() throws FactoryException {
        /*
         * First checks if the current thread is already using a factory. If yes, we will
         * avoid creating new factories on the assumption that factories are reentrant.
         */
        final Usage usage = current.get();
        AbstractAuthorityFactory factory = usage.factory;
        if (factory == null) {
            synchronized (this) {
                /**
                 * If we have reached the maximal amount of backing stores allowed, wait for a backing
                 * store to become available. In theory the 2 seconds timeout is not necessary, but we
                 * put it as a safety in case we fail to invoke a notify() matching this wait(), for
                 * example someone else is waiting on this monitor or because the release(...) method
                 * threw an exception.
                 */
                while (remainingBackingStores == 0) {
                    try {
                        wait(2000);
                    } catch (InterruptedException e) {
                        // Someone doesn't want to let us sleep.
                        throw new FactoryException(e.getLocalizedMessage(), e);
                    }
                }
                /*
                 * Reuses the most recently used factory, if available. If there is no factory
                 * available for reuse, creates a new one. We don't add it to the queue now;
                 * it will be done by the release(...) method.
                 */
                final Store store = stores.pollLast();
                if (store != null) {
                    factory = store.factory; // Should never be null.
                }
                remainingBackingStores--; // Should be done last when we are sure to not fail.
            }
            /*
             * If there is a need to create a new factory, do that outside the synchronized
             * block because this creation may involve a lot of client code. This is better
             * for reducing the dead-lock risk. Subclasses are responsible of synchronizing
             * their createBackingStore() method if necessary.
             */
            try {
                assert usage.count == 0;
                if (factory == null) {
                    factory = createBackingStore();
                    if (factory == null) {
                        throw new NoSuchFactoryException(Errors.format(Errors.Keys.NO_DATA_SOURCE));
                    }
                }
                usage.factory = factory;
            } finally {
                /*
                 * If any kind of error occurred, restore the 'remainingBackingStores' field
                 * as if no code were executed.  This code would not have been needed if we
                 * were allowed to decrement 'remainingBackingStores' only as the very last
                 * step (when we know that everything else succeed). Unfortunately it needed
                 * to be decremented inside the synchronized block.
                 */
                if (factory == null) {
                    synchronized (this) {
                        remainingBackingStores++;
                    }
                }
            }
        }
        // Increment below is safe even if outside the synchronized block,
        // because each thread own exclusively its Usage instance
        usage.count++;
        return factory;
    }

    /**
     * Releases the backing store previously obtained with {@link #getBackingStore}.
     * This method marks the factory as available for reuse by other threads.
     */
    @Override
    final void release() {
        final Usage usage = current.get();
        if (--usage.count == 0) synchronized (this) {
            remainingBackingStores++; // Must be done first in case an exception happen after this point.
            final AbstractAuthorityFactory factory = usage.factory;
            usage.factory = null;
            stores.addLast(new Store(factory));
            /*
             * If the backing store we just released is the first one, awake the
             * disposer thread which was waiting for an indefinite amount of time.
             */
            if (!isActive) {
                isActive = true;
                Threads.executeDisposal(disposerTask, timeout);
            }
            notify(); // We released only one backing store, so awake only one thread - not all of them.
        }
        assert usage.count >= 0 && (usage.factory == null) == (usage.count == 0) : usage.count;
    }

    /**
     * Returns {@code true} if this factory contains at least one active backing store.
     * A backing store is "active" if it has been created for a previous request and not
     * yet disposed after a period of inactivity equals to the {@linkplain #getTimeout()
     * timeout}.
     * <p>
     * A return value of {@code false} typically implies that every connection to the
     * underlying database (if any) used by this factory have been closed. Note however
     * that this information is only approximative. Because of the concurrent nature of
     * {@code ThreadedAuthorityFactory}, we may have a small delay between the time when
     * the first connection is created and the time when this method returns {@code true}.
     *
     * @return {@code true} if this factory contains at least one active backing store.
     *
     * @since 3.00
     */
    public synchronized boolean isActive() {
        return isActive;
    }

    /**
     * Returns the current timeout.
     *
     * @return The current timeout.
     *
     * @since 3.00
     */
    public synchronized long getTimeout() {
        return timeout;
    }

    /**
     * Sets a timer for disposing the backing store after the specified amount of milliseconds
     * of inactivity. If a new backing store is needed after the disposal of the current one,
     * then the {@link #createBackingStore} method will be invoked again.
     * <p>
     * Note that the backing store disposal can be vetoed if {@link #canDisposeBackingStore}
     * returns {@code false}.
     *
     * @param delay The delay of inactivity (in milliseconds) before to close a backing store.
     */
    public synchronized void setTimeout(final long delay) {
        ensureStrictlyPositive("delay", delay);
        timeout = delay; // Will be taken in account after the next factory to dispose.
    }

    /**
     * Returns {@code true} if the given backing store can be disposed now. This method is invoked
     * automatically after the amount of time specified by {@link #setTimeout}, providing that the
     * factory was not used during that time. The default implementation always returns {@code true}.
     * Subclasses should override this method and returns {@code false} if they want to prevent the
     * backing store disposal under some circumstances.
     *
     * @param backingStore The backing store in process of being disposed.
     * @return {@code true} if the backing store can be disposed now.
     */
    protected boolean canDisposeBackingStore(final AbstractAuthorityFactory backingStore) {
        return true;
    }

    /**
     * Disposes the given backing store if possible.
     */
    private void dispose(final AbstractAuthorityFactory factory, final boolean shutdown) {
        if (shutdown || canDisposeBackingStore(factory)) {
            factory.dispose(shutdown);
        }
    }

    /**
     * Releases resources immediately instead of waiting for the garbage collector.
     * This method disposes all backing stores. This instance should not be used
     * anymore after this method has been invoked.
     *
     * @param shutdown {@code false} for normal disposal, or {@code true} if
     *        this method is invoked during the process of a JVM shutdown.
     */
    @Override
    protected void dispose(final boolean shutdown) {
        final AbstractAuthorityFactory[] factories;
        int count = 0;
        synchronized (this) {
            super.dispose(shutdown); // Mark the factory as not available anymore.
            remainingBackingStores = 0;
            factories = new AbstractAuthorityFactory[stores.size()];
            Store store;
            while ((store = stores.pollFirst()) != null) {
                factories[count++] = store.factory;
            }
        }
        // Disposes the factories from outside the synchronized lock.
        while (--count >= 0) {
            dispose(factories[count], shutdown);
        }
    }

    /**
     * Disposes the expired entries. This method should be invoked from the
     * {@link #disposerTask} only. It may reschedule the task again for an
     * other execution.
     */
    final void disposeExpired() {
        final long currentTimeMillis = System.currentTimeMillis();
        final AbstractAuthorityFactory[] factories;
        int count = 0;
        synchronized (this) {
            factories = new AbstractAuthorityFactory[stores.size()];
            final Iterator<Store> it = stores.iterator();
            while (it.hasNext()) {
                final Store store = it.next();
                /*
                 * Computes how much time we need to wait again before we can dispose the factory.
                 * If this time is greater than some arbitrary amount, do not dispose the factory
                 * and wait again.
                 */
                final long delay = timeout - (currentTimeMillis - store.timestamp);
                if (delay > TIMEOUT_RESOLUTION) {
                    // Found a factory which is not expired. Stop the search,
                    // since the iteration is expected to be ordered.
                    Threads.executeDisposal(disposerTask, delay);
                    break;
                }
                // Found an expired factory. Adds it to the list of
                // factories to dispose and search for other factories.
                factories[count++] = store.factory;
                it.remove();
            }
            /*
             * The stores list is empty if all worker factories in the queue have been disposed.
             * Note that some worker factories may still be active outside the queue, because the
             * workers are added to the queue only after completion of their work. In the later
             * case, release() will reschedule a new task.
             */
            isActive = !stores.isEmpty();
        }
        /*
         * Disposes the factories from outside the synchronized lock.
         */
        while (--count >= 0) {
            dispose(factories[count], false);
        }
    }
}
