/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
import org.opengis.referencing.FactoryException;
import org.geotoolkit.util.logging.Logging;

import org.junit.*;

import static org.junit.Assert.*;
import static org.geotoolkit.referencing.factory.ThreadedAuthorityFactory.TIMEOUT_RESOLUTION;


/**
 * Tests {@link ThreadedAuthorityFactory}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.00
 */
public final class ThreadedAuthorityFactoryTest {
    /**
     * Tests the disposal of backing factories after a timeout.
     *
     * @throws FactoryException Should never happen.
     * @throws InterruptedException Should never happen.
     */
    @Test
    public void testTimeout() throws FactoryException, InterruptedException {
        final DummyFactory.Threaded threaded = new DummyFactory.Threaded();
        threaded.setTimeout(TIMEOUT_RESOLUTION * 4);
        /*
         * Ask for one element, wait for the timeout and
         * check that the backing store is disposed.
         */
        assertTrue  ("Should have initially no worker.", threaded.factories().isEmpty());
        assertEquals("Should have initially no worker.", 0, threaded.countBackingStores());
        assertNotNull(threaded.createObject("WGS84"));

        List<DummyFactory> factories = threaded.factories();
        assertEquals("Expected a new worker.",      1, factories.size());
        assertEquals("Expected one valid worker.",  1, threaded.countBackingStores());
        assertFalse ("Should not be disposed yet.", factories.get(0).isDisposed());

        Thread.sleep(TIMEOUT_RESOLUTION * 2);
        assertEquals("Expected no new worker.",     factories, threaded.factories());
        assertEquals("Expected one valid worker.",  1, threaded.countBackingStores());
        assertFalse ("Should not be disposed yet.", factories.get(0).isDisposed());

        Thread.sleep(TIMEOUT_RESOLUTION * 3);
        assertEquals("Expected no new worker.",    factories, threaded.factories());
        waitIfAlive (threaded, factories.get(0));
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
        assertNotNull(threaded.createObject("WGS84-new"));
        factories = threaded.factories();
        assertEquals("Expected a new worker.",      2, factories.size());
        assertEquals("Expected one valid worker.",  1, threaded.countBackingStores());
        assertFalse ("Should not be disposed yet.", factories.get(1).isDisposed());

        Thread.sleep(TIMEOUT_RESOLUTION * 2);
        assertEquals("Expected no new worker.",     factories, threaded.factories());
        assertEquals("Expected one valid worker.",  1, threaded.countBackingStores());
        assertFalse ("Should not be disposed yet.", factories.get(1).isDisposed());
        /*
         * Ask again for a new element before the timeout is ellapsed and
         * check that the disposal of the backing store has been reported.
         */
        assertNotNull(threaded.createObject("WGS84-new2"));
        Thread.sleep(TIMEOUT_RESOLUTION);
        assertNotNull(threaded.createObject("WGS84-new3"));
        Thread.sleep(TIMEOUT_RESOLUTION * 2);

        assertEquals("Expected no new worker.",     factories, threaded.factories());
        assertEquals("Expected one valid worker.",  1, threaded.countBackingStores());
        assertFalse ("Should not be disposed yet.", factories.get(1).isDisposed());

        Thread.sleep(TIMEOUT_RESOLUTION * 3);
        assertEquals("Expected no new worker.",    factories, threaded.factories());
        waitIfAlive (threaded, factories.get(1));
        assertEquals("Worker should be disposed.", 0, threaded.countBackingStores());
        assertTrue  ("Worker should be disposed.", factories.get(1).isDisposed());
        assertTrue  ("Worker should be disposed.", factories.get(0).isDisposed());
    }

    /**
     * Asserts that the given worker is disposed. It should be disposed right at the time this
     * method is invoked. However if it is not, we will be lenient, give some time and try again.
     */
    private static void waitIfAlive(final ThreadedAuthorityFactory threaded, final DummyFactory worker)
            throws InterruptedException
    {
        /*
         * First, wait that ThreadedAuthorityFactory declares that it has no more backing store.
         * The removal of backing store is performed by the StoreDisposer daemon thread,  which
         * invoke factory.disposeExpired(). Because it is invoked in a background thread, it is
         * exposed to the hazard of thread scheduling.
         */
        int n = 10, c;
        while ((c = threaded.countBackingStores()) != 0) {
            final long delay = StoreDisposer.INSTANCE.getDelay();
            String message = "ThreadedAuthorityFactory should have discarted its worker factory.\n"
                    + "The factory active flag is '" + threaded.isActive() + "' (expected 'false').\n"
                    + "Maximum number of additional workers is " + threaded.remainingBackingStores() + ".\n"
                    + "There is " + c + " workers in the factory cache, " + (delay == 0
                    ? "but the StoreDisposer queue is empty (PROBLEM!)."
                    : "and StoreDisposer next job is scheduled in " + delay + " ms.");
            if (--n == 0) {
                fail(message);
            }
            Logging.getLogger(ThreadedAuthorityFactoryTest.class).warning(message);
            Thread.sleep(TIMEOUT_RESOLUTION);
            System.gc();
        }
        /*
         * Now the worker factory has been discarted by ThreadedAuthorityFactory, but maybe
         * not yet disposed because the call to worker.dispose() is performed in yet an other
         * background thread. So we need again to be lenient because of thread scheduling hazard.
         */
        n = 10;
        while (!worker.isDisposed()) {
            if (--n == 0) {
                fail("Worker should be disposed.");
            }
            Logging.getLogger(ThreadedAuthorityFactoryTest.class)
                    .warning("Worker should be disposed.");
            Thread.sleep(TIMEOUT_RESOLUTION);
        }
    }
}
