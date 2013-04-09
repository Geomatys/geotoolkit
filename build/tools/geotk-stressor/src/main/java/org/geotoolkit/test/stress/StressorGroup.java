/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.awt.Window;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.geotoolkit.io.TableWriter;
import org.geotoolkit.io.ExpandedTabWriter;
import org.geotoolkit.internal.Threads;
import org.apache.sis.math.Statistics;
import org.geotoolkit.util.NullArgumentException;


/**
 * A group of {@link Stressor}s. Each stressor will be run in its own thread.
 *
 * @param <S> The type of stressor to be managed by this group.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.14
 */
public class StressorGroup<S extends Stressor> implements Runnable, ThreadFactory {
    /**
     * The output stream where to print reports, and the error stream where to reports error
     * (also used for log messages).
     *
     * @since 3.15
     */
    protected final PrintWriter out, err;

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
    private final List<S> stressors;

    /**
     * The executor to use for running the test threads.
     */
    private final ExecutorService executor;

    /**
     * The test duration, in milliseconds.
     */
    private final long duration;

    /**
     * The frame where to show the request result, or {@code null} if none.
     *
     * @see #createFrame()
     */
    private Window frame;

    /**
     * Creates a new {@code StressorGroup} instance.
     *
     * @param duration The test duration, in milliseconds.
     */
    public StressorGroup(final long duration) {
        this(duration, new PrintWriter(System.out, true), new PrintWriter(System.err, true));
    }

    /**
     * Creates a new {@code StressorGroup} instance.
     *
     * @param duration The test duration, in milliseconds.
     * @param out      The output stream where to print reports.
     * @param err      The error stream where to reports error (also used for log messages).
     */
    public StressorGroup(final long duration, final PrintWriter out, final PrintWriter err) {
        this.threadGroup = new ThreadGroup(Threads.GEOTOOLKIT, "Stressors");
        this.threadCount = new AtomicInteger();
        this.stressors   = new ArrayList<>();
        this.executor    = Executors.newCachedThreadPool(this);
        this.duration    = duration;
        this.out         = out;
        this.err         = err;
    }

    /**
     * Returns the stressors which have been {@linkplain #added added} to this group.
     *
     * @return The stressors added to this group.
     *
     * @since 3.15
     */
    public List<S> getStressors() {
        return Collections.unmodifiableList(stressors);
    }

    /**
     * Adds a stressor to the queue of stressors to execute. This stressor will be executed
     * (together will all other stressors) when the {@link #run()} method will be invoked.
     *
     * @param stressor The stressor to add.
     */
    public void add(final S stressor) {
        if (frame != null) {
            throw new IllegalStateException("Can not add any more stressor.");
        }
        if (stressor == null) {
            throw new NullArgumentException();
        }
        stressors.add(stressor);
    }

    /**
     * Creates a frame where to show the result of requests. This method can be invoked at
     * most once, after all stressors have been {@linkplain #add added} to this group.
     */
    public void createFrame() {
        if (frame != null) {
            throw new IllegalStateException("Frame already created");
        }
        frame = ImageViewer.show(stressors);
    }

    /**
     * Starts a thread for each stressor.
     */
    @Override
    public void run() {
        final long startTime = System.nanoTime();
        for (final Stressor stressor : stressors) {
            stressor.stopTime = startTime + duration * 1000000;
        }
        final List<Future<Statistics>> tasks;
        try {
            tasks = executor.invokeAll(stressors);
            executor.shutdown();
            if (!executor.awaitTermination(duration + 60000, TimeUnit.MILLISECONDS)) {
                out.flush();
                err.println("Timeout elapsed.");
            }
        } catch (InterruptedException e) {
            out.flush();
            e.printStackTrace(err);
            return;
        }
        err.flush();
        final Statistics  responseTime = new Statistics(null);
        final Statistics  throughput   = new Statistics(null);
        final TableWriter table        = new TableWriter(out);
        final PrintWriter printer      = new PrintWriter(new ExpandedTabWriter(table, 2));
        table.setMultiLinesCells(true);
        table.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        table.write("Thread");            table.nextColumn();
        table.write("Time (ms)");         table.nextColumn();
        table.write("Throughput (/min)"); table.nextColumn();
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
                responseTime.add(statistics);
                printer.print(statistics);
                table.nextColumn();
                final double th = statistics.count() / statistics.sum() * 60000;
                throughput.add(th);
                printer.print((float) th);
            }
            table.nextLine();
            table.nextLine(TableWriter.SINGLE_HORIZONTAL_LINE);
        }
        printer.print("Global");
        table.nextColumn();
        printer.print(responseTime);
        table.nextColumn();
        printer.print(throughput);
        table.nextLine();
        table.nextLine(TableWriter.DOUBLE_HORIZONTAL_LINE);
        printer.flush();
        if (frame != null) {
            frame.dispose();
        }
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
