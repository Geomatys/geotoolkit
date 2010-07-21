/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.test.stress;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;

import org.geotoolkit.math.Statistics;


/**
 * A group of {@link Stressor}s. Each stressor will be run in its own thread.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.14
 */
public class StressorGroup {
    /**
     * The stressors.
     */
    private final List<Stressor> stressors;

    /**
     * The executor to use for running the test threads.
     */
    private final ExecutorService executor;

    /**
     * The test duration, in milliseconds.
     */
    private final long duration;

    /**
     * Creates a new {@code StressorGroup} instance.
     *
     * @param duration The test duration, in milliseconds.
     */
    public StressorGroup(final long duration) {
        this.stressors = new ArrayList<Stressor>();
        this.executor  = Executors.newSingleThreadExecutor();
        this.duration  = duration;
    }

    /**
     * Starts a thread for each stressor.
     *
     * @throws InterruptedException If the test has been interrupted.
     */
    public void run() throws InterruptedException {
        final List<Future<Statistics>> tasks = executor.invokeAll(stressors);
        if (!executor.awaitTermination(duration + 60000, TimeUnit.MILLISECONDS)) {
            System.err.println("Timeout elapsed.");
            return;
        }
        final Statistics global = new Statistics();
        for (final Future<Statistics> task : tasks) {
            final Statistics statistics;
            try {
                statistics = task.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
                continue;
            }
            System.out.println(statistics);
            global.add(statistics);
        }
        System.out.println();
        System.out.println("Global statistics:");
        System.out.println(global);
    }
}
