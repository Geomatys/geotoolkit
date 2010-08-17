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

import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.geotoolkit.io.TableWriter;
import org.geotoolkit.io.ExpandedTabWriter;
import org.geotoolkit.math.Statistics;
import org.geotoolkit.util.NullArgumentException;


/**
 * A group of {@link Stressor}s. Each stressor will be run in its own thread.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.14
 */
public class StressorGroup implements Runnable, ThreadFactory {
    /**
     * The output stream where to print reports.
     *
     * @since 3.15
     */
    protected final PrintWriter out;

    /**
     * The group of all threads to be created by this stressor.
     *
     * @since 3.15
     */
    protected final ThreadGroup threadGroup;

    /**
     * The number of thread created up to date.
     */
    private final AtomicInteger threadCount;

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
     * Whatever the request results shall be shown in windows.
     */
    private final boolean view;

    /**
     * Creates a new {@code StressorGroup} instance.
     *
     * @param duration The test duration, in milliseconds.
     */
    public StressorGroup(final long duration) {
        this(duration, new PrintWriter(System.out, true), false);
    }

    /**
     * Creates a new {@code StressorGroup} instance.
     *
     * @param duration The test duration, in milliseconds.
     * @param out      The output stream where to print reports.
     * @param view     Whatever the request results shall be shown in windows.
     */
    public StressorGroup(final long duration, final PrintWriter out, final boolean view) {
        this.threadGroup = new ThreadGroup("Stressors");
        this.threadCount = new AtomicInteger();
        this.stressors   = new ArrayList<Stressor>();
        this.executor    = Executors.newCachedThreadPool(this);
        this.duration    = duration;
        this.out         = out;
        this.view        = view;
    }

    /**
     * Adds a stressor to the queue of stressors to execute. This stressor will be executed
     * (together will all other stressors) when the {@link #run()} method will be invoked.
     *
     * @param stressor The stressor to add.
     */
    public void add(final Stressor stressor) {
        if (stressor == null) {
            throw new NullArgumentException();
        }
        stressor.out = out;
        stressors.add(stressor);
    }

    /**
     * Starts a thread for each stressor.
     */
    @Override
    public void run() {
        if (view) {
            ImageViewer.show(stressors);
        }
        final long startTime = System.currentTimeMillis();
        for (final Stressor stressor : stressors) {
            stressor.stopTime = startTime + duration;
        }
        final List<Future<Statistics>> tasks;
        try {
            tasks = executor.invokeAll(stressors);
            executor.shutdown();
            if (!executor.awaitTermination(duration + 60000, TimeUnit.MILLISECONDS)) {
                out.println("Timeout elapsed.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace(out);
            return;
        }
        final Statistics  statsRaw   = new Statistics();
        final Statistics  statsByMpx = new Statistics();
        final TableWriter table      = new TableWriter(out);
        final PrintWriter printer    = new PrintWriter(new ExpandedTabWriter(table, 2));
        table.setMultiLinesCells(true);
        table.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        table.write("Thread");       table.nextColumn();
        table.write("Time (ms)");    table.nextColumn();
        table.write("Time (ms/Mb)"); table.nextColumn();
        table.nextLine();
        table.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        final int numTasks = stressors.size();
        for (int i=0; i<numTasks; i++) {
            final Stressor stressor = stressors.get(i);
            printer.print(stressor.threadName);
            table.nextColumn();
            final Future<Statistics> task = tasks.get(i);
            Statistics statistics = null;
            try {
                statistics = task.get();
            } catch (InterruptedException e) {
                e.printStackTrace(printer);
            } catch (ExecutionException e) {
                e.getCause().printStackTrace(printer);
            }
            if (statistics != null) {
                statsRaw.add(statistics);
                statsByMpx.add(stressor.statsByMpx);
                printer.print(statistics);
                table.nextColumn();
                printer.print(stressor.statsByMpx);
            }
            table.nextLine();
            table.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        }
        printer.print("Global");
        table.nextColumn();
        printer.print(statsRaw);
        table.nextColumn();
        printer.print(statsByMpx);
        table.nextLine();
        table.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        printer.flush();
    }

    /**
     * Creates a new thread for the given task. This method is invoked automatically
     * by the {@linkplain ExecutorService executor} when needed.
     *
     * @param  task The task to be run.
     * @return A new thread for the given task.
     */
    @Override
    public Thread newThread(final Runnable task) {
        String name = String.valueOf(threadCount.incrementAndGet());
        if (name.length() < 2) {
            name = '0' + name;
        }
        return new Thread(threadGroup, task, name);
    }
}
