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
package org.geotoolkit.util.collection;

import java.util.concurrent.Callable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.ref.SoftReference;
import net.jcip.annotations.ThreadSafe;


/**
 * A concurrent cache mechanism. This implementation is thread-safe and supports concurrency.
 * A cache entry can be locked when an object is in process of being created. The steps
 * are as below:
 * <p>
 * <ol>
 *   <li>Check if the value is already available in the map.
 *       If it is, return it immediately and we are done.</li>
 *   <li>Otherwise, get a lock and check again if the value is already available in the map
 *       (because the value could have been computed by an other thread between step 1 and
 *       the obtention of the lock). If it is, release the lock and we are done.</li>
 *   <li>Otherwise compute the value, store the result and release the lock.</li>
 * </ol>
 * <p>
 * The easiest way (except for exception handling) to use this class is to prepare a
 * {@link Callable} statement to be executed only if the object is not in the cache,
 * and to invoke the {@link #getOrCreate getOrCreate} method. Example:
 *
 * {@preformat java
 *     private final Cache<String,MyObject> cache = new Cache<String,MyObject>();
 *
 *     public MyObject getMyObject(final String key) throws MyCheckedException {
 *         try {
 *             return cache.getOrCreate(key, new Callable<MyObject>() {
 *                 MyObject call() throws FactoryException {
 *                     return createMyObject(key);
 *                 }
 *             });
 *         } catch (MyCheckedException e) {
 *             throw e;
 *         } catch (RuntimeException e) {
 *             throw e;
 *         } catch (Exception e) {
 *             throw new UndeclaredThrowableException(e);
 *         }
 *     }
 * }
 *
 * An alternative is to perform explicitly all the steps enumerated above. This alternative
 * avoid the creation of a temporary {@code Callable} statement which may never be executed,
 * and avoid the exception handling due to the {@code throws Exception} clause. Note that the
 * call to {@link Handler#putAndUnlock putAndUnlock} <strong>must</strong> be in the {@code finally}
 * block of a {@code try} block beginning immediately after the call to {@link #lock lock},
 * no matter what the result of the computation is (including {@code null}).
 *
 * {@preformat java
 *     private final Cache<String,MyObject> cache = new Cache<String,MyObject>();
 *
 *     public MyObject getMyObject(final String key) throws MyCheckedException {
 *         MyObject value = cache.peek(key);
 *         if (value == null) {
 *             final Cache.Handler<MyObject> handler = cache.lock(key);
 *             try {
 *                 value = handler.peek();
 *                 if (value == null) {
 *                     value = createMyObject(key);
 *                 }
 *             } finally {
 *                 handler.putAndUnlock(value);
 *             }
 *         }
 *         return value;
 *     }
 * }
 *
 *
 * {@section Eviction of eldest values}
 *
 * <ul>
 *   <li><p>The <cite>cost</cite> of a value is the value returned by {@link #cost}. The default
 *       implementation returns 1 in all cases, but subclasses can override this method for
 *       more elaborated cost computation.</p></li>
 *   <li><p>The <cite>total cost</cite> is the sum of the cost of all values held by strong
 *       reference in this cache. The total cost does not include the cost of values held
 *       by {@linkplain Reference weak or soft reference}.</p></li>
 *   <li><p>The <cite>cost limit</cite> is the maximal value allowed for the total cost. If
 *       the total cost exceed this value, then strong references to the eldest values are
 *       replaced by {@linkplain Reference weak or soft references} until the total cost
 *       become equals or lower than the cost limit.</p></li>
 * </ul>
 *
 * The total cost is given at construction time. If the {@link #cost} method has not been
 * overridden, then the total cost is the maximal amount of values to keep by strong references.
 *
 *
 * {@section Circular dependencies}
 *
 * This implementation assumes that there is no circular dependencies (or cyclic graph) between
 * the values in the cache. For example if creating <var>A</var> implies creating <var>B</var>,
 * then creating <var>B</var> is not allowed to implies (directly or indirectly) the creation of
 * <var>A</var>. If this rule is not meet, deadlock may occur randomly.
 *
 * @param <K> The type of key objects.
 * @param <V> The type of value objects.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.00
 * @module
 *
 * @deprecated Moved to Apache SIS {@link org.apache.sis.util.collection.Cache}.
 */
@ThreadSafe
@Deprecated
public class Cache<K,V> extends org.apache.sis.util.collection.Cache<K,V> {
    /**
     * Creates a new cache with a default initial capacity and cost limit of 100.
     * The oldest objects will be hold by {@linkplain WeakReference weak references}.
     */
    public Cache() {
        this(12, 100, false);
    }

    /**
     * Creates a new cache using the given initial capacity and cost limit. The initial capacity
     * is the expected number of values to be stored in this cache. More values are allowed, but
     * a little bit of CPU time may be saved if the expected capacity is known before the cache
     * is created.
     * <p>
     * The <cite>cost limit</cite> is the maximal value of the <cite>total cost</cite> (the sum
     * of the {@linkplain #cost cost} of all values) before to replace eldest strong references
     * by {@linkplain Reference weak or soft references}.
     *
     * @param initialCapacity the initial capacity.
     * @param costLimit The maximum number of objects to keep by strong reference.
     * @param soft If {@code true}, use {@link SoftReference} instead of {@link WeakReference}.
     */
    public Cache(int initialCapacity, final long costLimit, final boolean soft) {
        super(initialCapacity, costLimit, soft);
    }
}
