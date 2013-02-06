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
package org.geotoolkit.util.collection;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;

import org.apache.sis.util.CharSequences;
import org.geotoolkit.io.TableWriter;
import org.geotoolkit.math.Statistics;
import org.geotoolkit.test.TestBase;

import org.junit.*;
import static org.junit.Assert.*;
import static java.lang.StrictMath.*;


/**
 * Tests the {@link Cache} with simple tests.
 *
 * @author Cory Horner (Refractions)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final strictfp class CacheTest extends TestBase {
    /**
     * Tests with two values.
     */
    @Test
    public void testSimple() { // LGPL
        final Integer  key1 = 1;
        final Integer  key2 = 2;
        final String value1 = "value 1";

        final Cache<Integer,String> cache = new Cache<>();
        assertNull(cache.peek(key1));
        assertTrue(cache.isEmpty());

        final Cache.Handler<String> handler = cache.lock(key1);
        assertNull(handler.peek());
        handler.putAndUnlock(value1);
        assertEquals(value1, cache.peek(key1));
        assertNull(cache.peek(key2));
        assertEquals(1, cache.size());
        assertEquals(Collections.singleton(key1), cache.keySet());
        assertEquals(Collections.singleton(new SimpleEntry<>(key1, value1)), cache.entrySet());
    }

    /**
     * Tests the cache when two threads are using it concurrently.
     *
     * @throws InterruptedException Should never happen.
     */
    @Test
    public void testConcurrent() throws InterruptedException { // LGPL
        final Integer  key1 = 1;
        final String value1 = "value 1";
        final Integer  key2 = 2;
        final String value2 = "value 2";
        final Cache<Integer,String> cache = new Cache<>();
        final AtomicInteger failures = new AtomicInteger();
        final class ReaderThread extends Thread {
            volatile String value;

            @Override public void run() {
                // Try to get the value 1 from the cache.
                try {
                    final Cache.Handler<String> handler = cache.lock(key1);
                    assertTrue(handler instanceof Cache<?,?>.Work.Wait);
                    value = handler.peek();
                    assertNotNull(value);
                    handler.putAndUnlock(value);
                    assertSame(value, cache.peek(key1));
                } catch (Throwable e) {
                    e.printStackTrace(System.out);
                    failures.incrementAndGet();
                }
                // Put value 2 in the cache.
                try {
                    final Cache.Handler<String> handler = cache.lock(key2);
                    assertTrue(handler instanceof Cache<?,?>.Work);
                    assertNull(handler.peek());
                    handler.putAndUnlock(value2);
                    assertSame(value2, cache.peek(key2));
                } catch (Throwable e) {
                    e.printStackTrace(System.out);
                    failures.incrementAndGet();
                }
            }
        }
        /*
         * Locks the cache as if we were writing.
         */
        final Cache.Handler<String> handler = cache.lock(key1);
        assertTrue(handler instanceof Cache<?,?>.Work);
        /*
         * Creates another thread which starts writing and blocks. Lets this thread
         * sleep a bit for making sure that the other thread is really blocked.
         */
        final ReaderThread thread = new ReaderThread();
        thread.start();
        Thread.sleep(300);
        assertNull("The WriterThread should be blocked.", thread.value);
        /*
         * Write. This will release the lock and let the WriterThread continue its job.
         */
        handler.putAndUnlock(value1);
        thread.join();
        assertEquals("There is failures in some background thread.", 0, failures.get());
        assertSame(value1, thread.value);
        assertSame(value1, cache.peek(key1));
        assertSame(value2, cache.peek(key2));
        assertSame(value2, cache.get (key2));
        /*
         * Checks the content of the collection.
         */
        assertEquals(2, cache.size());
        final Set<Map.Entry<Integer,String>> entries = new HashSet<>(4);
        assertTrue(entries.add(new SimpleEntry<>(key1, value1)));
        assertTrue(entries.add(new SimpleEntry<>(key2, value2)));
        assertEquals(entries, cache.entrySet());
        assertEquals(value1, cache.remove(key1)); assertFalse(cache.isEmpty());
        assertEquals(value2, cache.remove(key2)); assertTrue (cache.isEmpty());
    }

    /**
     * Starts many threads writing in the same cache, witch some likehood that two threads
     * ask for the same key.
     *
     * @throws InterruptedException Should never happen.
     */
    @Test
    public void stress() throws InterruptedException {
        final int count = 10000;
        final Cache<Integer,Integer> cache = new Cache<>();
        final AtomicInteger failures = new AtomicInteger();
        final class WriterThread extends Thread {
            int hit;

            @Override public void run() {
                for (int i=0; i<count; i++) {
                    final Integer key = i;
                    final Integer expected = new Integer(i * i); // We really want new instance.
                    final Integer value;
                    try {
                        value = cache.getOrCreate(key, new Callable<Integer>() {
                            @Override public Integer call() {
                                return expected;
                            }
                        });
                        assertEquals(expected, value);
                    } catch (Throwable e) {
                        e.printStackTrace(System.out);
                        failures.incrementAndGet();
                        continue;
                    }
                    if (expected == value) { // Identity comparison (not value comparison).
                        hit++;
                        yield(); // Gives a chance to other threads.
                    }
                }
            }
        }
        final WriterThread[] threads = new WriterThread[50];
        for (int i=0; i<threads.length; i++) {
            threads[i] = new WriterThread();
        }
        for (int i=0; i<threads.length; i++) {
            threads[i].start();
        }
        for (int i=0; i<threads.length; i++) {
            threads[i].join();
        }
        /*
         * Verifies the values.
         */
        final Statistics beforeGC = new Statistics();
        for (final Map.Entry<Integer,Integer> entry : cache.entrySet()) {
            final int key = entry.getKey();
            final int value = entry.getValue();
            assertEquals(key*key, value);
            beforeGC.add(key);
        }
        assertEquals("There is failures in some background thread.", 0, failures.get());
        assertTrue("Should not have more entries than what we put in.", cache.size() <= count);
        assertFalse("Some entries should be retained by strong references.", cache.isEmpty());
        if (out != null) {
            out.println();
            out.println("The numbers below are for tuning the test only. The output is somewhat");
            out.println("random so we can not check it in a test suite.  However if the test is");
            out.println("properly tuned, most values should be non-zero.");
            out.println();
            out.println("Number of times a cached value has been reused, for each thread:");
            for (int i=0; i<threads.length;) {
                final String n = String.valueOf(threads[i++].hit);
                out.print(CharSequences.spaces(6 - n.length()));
                out.print(n);
                if ((i % 10) == 0) {
                    out.println();
                }
            }
            out.println();
            out.println("Now observe how the background thread cleans the cache.");
            long time = System.nanoTime();
            for (int i=0; i<10; i++) {
                final long t = System.nanoTime();
                out.printf("Cache size: %4d (after %3d ms)%n", cache.size(), round((t - time) / 1E+6));
                time = t;
                Thread.sleep(250);
                if (i >= 2) {
                    System.gc();
                }
            }
            out.println();
            out.flush();
        }
        System.gc();
        Thread.sleep(100);
        System.gc();
        final Statistics afterGC = new Statistics();
        for (final Map.Entry<Integer,Integer> entry : cache.entrySet()) {
            final int key = entry.getKey();
            final int value = entry.getValue();
            assertEquals(key*key, value);
            afterGC.add(key);
        }
        assertTrue("Number of entries should not increase while we are not writing in the cache.",
                afterGC.count() <= beforeGC.count());
        if (out != null) {
            final TableWriter table = new TableWriter(out, " \u2502 ");
            table.setMultiLinesCells(true);
            table.write("Statistics on the keys before garbage collection:");
            table.nextColumn();
            table.write("Statistics on the keys after garbage collection.\n" +
                        "The minimum and the mean values should be greater.");
            table.nextLine();
            table.write(beforeGC.toString());
            table.nextColumn();
            table.write(afterGC.toString());
            try {
                table.flush();
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
    }
}
