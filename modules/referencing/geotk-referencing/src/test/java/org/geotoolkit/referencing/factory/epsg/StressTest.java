/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.epsg;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.awt.geom.Point2D;
import java.util.concurrent.CountDownLatch;
import java.util.Map;

import org.apache.sis.math.Statistics;

import org.apache.sis.util.Classes;
import org.junit.*;
import static org.junit.Assume.assumeNotNull;


/**
 * Stresses the {@link ThreadedEpsgFactory}.
 *
 * @author Jody Garnett (Refractions)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.4
 */
public final strictfp class StressTest extends EpsgFactoryTestBase {
    /**
     * Number of thread running concurrently. This number must be greater than the default
     * number of workers in {@link ThreadedEpsgFactory}, which is currently {@value #MAX_WORKERS}.
     */
    private static final int RUNNER_COUNT = 32;

    /**
     * The default maximal number of workers. Actually this number is hard coded in
     * {@link ThreadedAuthorityFactory}, but we can't access it since we are not in
     * the same package and we don't want to make this value public (because it is
     * too likely to change as we experiment tradeoff).
     */
    private static final int MAX_WORKERS = 16;

    /**
     * Number of iterations to perform in each thread.
     */
    static final int ITERATIONS = 300;

    /**
     * Creates a test suite for the default database.
     */
    public StressTest() {
        super(ThreadedEpsgFactory.class);
    }

    /**
     * Tests the execution of many concurrent threads.
     *
     * @throws Throwable If any kind of error occurred (may be from a client thread).
     */
    @Test
    public final void testRunners() throws Throwable {
        assumeNotNull(factory);

        final CountDownLatch lock = new CountDownLatch(1);
        final ClientThread runners[] = new ClientThread[RUNNER_COUNT];
        for (int i=0; i<RUNNER_COUNT; i++) {
            final ClientThread thread = new ClientThread(i+1, factory, lock);
            runners[i] = thread;
            thread.start(); // Will block on the count down latch.
        }
        /*
         * Lets every threads go and wait for them to complete.
         * We measure the time elapsed in this process.
         */
        long timeElapsed = System.currentTimeMillis();
        lock.countDown();
        for (int i=0; i<RUNNER_COUNT; i++) {
            runners[i].join();
        }
        timeElapsed = System.currentTimeMillis() - timeElapsed;
        /*
         * Gets the metric et reports the first failure found in each thread.
         * If a failure has been found, it will cause the test to fail at the
         * end of this method.
         */
        final Map<Integer, Point2D.Double> result = ClientThread.createEmptyResultMap();
        final Statistics statistics = new Statistics(null);
        Throwable exception = null;
        for (int i=0; i<RUNNER_COUNT; i++) {
            final ClientThread thread = runners[i];
            final Throwable e = thread.exception;
            if (e != null) {
                // Remember the first exception (to be throw below).
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
                System.err.println(Classes.getShortClassName(e) + " in thread " +
                        thread.id + " for code " + thread.badCode);
            }
            statistics.combine(thread.statistics);
            // Check the consistency between different threads.
            for (final Map.Entry<Integer, Point2D.Double> entry : thread.result.entrySet()) {
                ClientThread.assertConsistent(result, entry.getKey(), entry.getValue());
            }
        }
        /*
         * Reports the metric.
         */
        final PrintWriter out = StressTest.out;
        if (out != null) {
            final int cumulativeIteration = statistics.count();
            final double averageTime    = statistics.mean() / 1E+6;
            final double throughput     = 1000 / averageTime;
            final double minTime        = statistics.minimum() / 1E+6;
            final double maxTime        = statistics.maximum() / 1E+6;
            out.println("Number of clients: " + RUNNER_COUNT);
            out.println("Number of workers: " + MAX_WORKERS);
            out.println("Iterations/thread: " + ITERATIONS);
            out.println("Cumulative Iter.:  " + cumulativeIteration);
            out.println("Average Time (ms): " + averageTime);
            out.println("Overall Time (ms): " + timeElapsed);
            out.println("Throughput (kHz):  " + throughput);
            out.println("Minimum time (ms): " + minTime);
            out.println("Maximum time (ms): " + maxTime);
            out.println("Number CRS codes:  " + result.size() + " / " + ClientThread.CODES.length);
            out.flush();
            /*
             * Append results to file.
             */
            if (false) {
                final String content =
                        ""   + RUNNER_COUNT +
                        ", " + MAX_WORKERS +
                        ", " + ITERATIONS +
                        ", " + averageTime +
                        ", " + timeElapsed +
                        ", " + cumulativeIteration +
                        ", " + throughput +
                        ", " + minTime +
                        ", " + maxTime;
                final File file = new File(System.getProperty("user.home"), "epsg-stress.csv");
                final boolean created = file.createNewFile();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                    if (created) {
                        bw.write("THREADS, MAX_WORKERS, ITERATIONS_PER_THREAD, " +
                                "AVG_TIME, TOTAL_TIME, TOTAL_RUNS, THROUGHPUT, MIN_TIME, MAX_TIME");
                        bw.newLine();
                    }
                    bw.write(content);
                    bw.newLine();
                }
            }
        }
        /*
         * If the test failed, reports the first failure.
         */
        if (exception != null) {
            throw exception;
        }
    }
}
