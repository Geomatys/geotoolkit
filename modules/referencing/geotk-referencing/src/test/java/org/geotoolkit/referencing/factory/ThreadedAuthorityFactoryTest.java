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
package org.geotoolkit.referencing.factory;

import java.util.List;
import org.opengis.util.FactoryException;
import org.apache.sis.util.logging.Logging;

import org.junit.*;

import static org.junit.Assume.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.factory.ThreadedAuthorityFactory.TIMEOUT_RESOLUTION;


/**
 * Tests {@link ThreadedAuthorityFactory}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.00
 */
public final strictfp class ThreadedAuthorityFactoryTest {
    /**
     * The timeout used for this test.
     */
    private static final long TIMEOUT = TIMEOUT_RESOLUTION * 4;

    /**
     * Tests the disposal of backing factories after a timeout.
     *
     * @throws FactoryException Should never happen.
     * @throws InterruptedException Should never happen.
     */
    @Test
    public void testTimeout() throws FactoryException, InterruptedException {
        final DummyFactory.Threaded threaded = new DummyFactory.Threaded();
        threaded.setTimeout(TIMEOUT);
        /*
         * Ask for one element, wait for the timeout and
         * check that the backing store is disposed.
         */
        long workTime = System.currentTimeMillis();
        assertTrue  ("Should have initially no worker.", threaded.factories().isEmpty());
        assertEquals("Should have initially no worker.", 0, threaded.countBackingStores());
        assertNotNull(threaded.createObject("WGS84"));

        List<DummyFactory> factories = threaded.factories();
        assertEquals("Expected a new worker.",      1, factories.size());
        assertEquals("Expected one valid worker.",  1, threaded.countBackingStores());
        assertFalse ("Should not be disposed yet.", factories.get(0).isDisposed());

        sleepWithoutExceedingTimeout(workTime, 2*TIMEOUT_RESOLUTION);
        assertEquals("Expected no new worker.",     factories, threaded.factories());
        assertEquals("Expected one valid worker.",  1, threaded.countBackingStores());
        assertFalse ("Should not be disposed yet.", factories.get(0).isDisposed());

        sleepUntilAfterTimeout(3*TIMEOUT_RESOLUTION, threaded);
        assertEquals("Expected no new worker.",    factories, threaded.factories());
        assertEquals("Worker should be disposed.", 0, threaded.countBackingStores());
        assertTrue  ("Worker should be disposed.", factories.get(0).isDisposed());
        /*
         * Ask again for the same object and check that no new backing
         * store were created because the value was taken from the cache.
         */
        assertNotNull(threaded.createObject("WGS84"));
        assertEquals("Expected no new worker.",    factories, threaded.factories());
        assertEquals("Worker should be disposed.", 0, threaded.countBackingStores());
        /*
         * Ask for one element and check that
         * a new backing store is created.
         */
        workTime = System.currentTimeMillis();
        assertNotNull(threaded.createObject("WGS84-new"));
        factories = threaded.factories();
        assertEquals("Expected a new worker.",      2, factories.size());
        assertEquals("Expected one valid worker.",  1, threaded.countBackingStores());
        assertFalse ("Should not be disposed yet.", factories.get(1).isDisposed());

        sleepWithoutExceedingTimeout(workTime, 2*TIMEOUT_RESOLUTION);
        assertEquals("Expected no new worker.",     factories, threaded.factories());
        assertEquals("Expected one valid worker.",  1, threaded.countBackingStores());
        assertFalse ("Should not be disposed yet.", factories.get(1).isDisposed());
        /*
         * Ask again for a new element before the timeout is elapsed and
         * check that the disposal of the backing store has been reported.
         */
        workTime = System.currentTimeMillis();
        assertNotNull(threaded.createObject("WGS84-new2"));
        sleepWithoutExceedingTimeout(workTime, TIMEOUT_RESOLUTION);
        assertNotNull(threaded.createObject("WGS84-new3"));
        sleepWithoutExceedingTimeout(workTime, 2*TIMEOUT_RESOLUTION);
        assertEquals("Expected one valid worker.",  1, threaded.countBackingStores());
        assertFalse ("Should not be disposed yet.", factories.get(1).isDisposed());
        assertEquals("Expected no new worker.",     factories, threaded.factories());

        sleepUntilAfterTimeout(3*TIMEOUT_RESOLUTION, threaded);
        assertEquals("Expected no new worker.",     factories, threaded.factories());
        assertEquals("Worker should be disposed.",  0, threaded.countBackingStores());
        assertTrue  ("Worker should be disposed.",  factories.get(1).isDisposed());
        assertTrue  ("Worker should be disposed.",  factories.get(0).isDisposed());
    }

    /**
     * Sleeps and ensures that the sleep time did not exceeded the timeout. The sleep time could
     * be greater if the test machine is under heavy load (for example a Jenkins server), in which
     * case we will cancel the test without failure.
     */
    private static void sleepWithoutExceedingTimeout(final long previousTime, final long waitTime) throws InterruptedException {
        Thread.sleep(waitTime);
        assumeTrue(System.currentTimeMillis() - previousTime < TIMEOUT);
    }

    /**
     * Sleeps a time long enough so that we exceed the timeout time. After this method call, the
     * workers should be disposed. However if they are not, then we will wait a little bit more.
     * <p>
     * The workers should be disposed right after the sleep time. However the workers disposal is
     * performed by a shared (Geotk-library wide) {@link ScheduledThreadPoolExecutor} instance
     * which invoke {@link ThreadedAuthorityFactory#disposeExpired()}. Because the later is
     * invoked in a background thread, it is exposed to the hazard of thread scheduling.
     */
    private static void sleepUntilAfterTimeout(final long waitTime,
            final ThreadedAuthorityFactory threaded) throws InterruptedException
    {
        Thread.sleep(waitTime);
        int n = 3;
        while (threaded.isActive()) {
            Logging.getLogger(ThreadedAuthorityFactoryTest.class).warning(
                    "Execution of ThreadedAuthorityFactory.disposeExpired() has been delayed.");
            Thread.sleep(TIMEOUT);
            System.gc();
            if (--n == 0) {
                break;
            }
        }
    }
}
