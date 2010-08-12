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
import java.util.concurrent.Callable;

import org.opengis.coverage.grid.GridEnvelope;

import org.geotoolkit.math.Statistics;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.util.Strings;


/**
 * Base class for stressors.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.14
 */
public abstract class Stressor extends RequestGenerator implements Callable<Statistics> {
    /**
     * The length (in characters) of the thread field.
     */
    private static final int THREAD_NAME_FIELD_LENGTH = 9;

    /**
     * The length (in characters) of the time field (in milliseconds).
     */
    private static final int TIME_FIELD_LENGTH = 5;

    /**
     * The length (in characters) of a single integer number for the grid size.
     */
    private static final int GRID_SIZE_FIELD_LENGTH = 5;

    /**
     * The maximal pause to wait between two queries, in milliseconds.
     */
    private static final int MAXIMAL_PAUSE = 250;

    /**
     * The time (in milliseconds) when to stop the test.
     * This is set by {@link StressorGroup} when a stress is started.
     */
    protected long stopTime;

    /**
     * The name of the thread that executed the task.
     */
    String threadName;

    /**
     * Statistics about elapsed time by "megabyte" (actually "megapixel").
     */
    Statistics statsByMpx;

    /**
     * The output stream where to print reports. It will be set to the
     * {@link StressorGroup#out} value before {@link #call()} is invoked.
     *
     * @since 3.15
     */
    PrintWriter out;

    /**
     * Creates a new stressor.
     *
     * @param domain Contains the maximal extent of the random envelopes to be generated.
     */
    protected Stressor(final GeneralGridGeometry domain) {
        super(domain);
    }

    /**
     * Invokes {@link #executeQuery(GeneralGridGeometry)} with random grid geometries
     * until the execution time has been elapsed. A slight pause is performed between
     * each query. Note that many threads may be running concurrently, so the CPU may
     * still busy at 100% despite this pause.
     *
     * @return The statistics about the execution time, in milliseconds.
     * @throws CoverageStoreException If an error occurred during the test.
     */
    @Override
    public Statistics call() throws CoverageStoreException {
        if (out == null) {
            out = new PrintWriter(System.out, true);
        }
        threadName = Thread.currentThread().getName();
        statsByMpx = new Statistics();
        final Statistics statistics = new Statistics();
        final StringBuilder buffer = new StringBuilder("Thread ").append(threadName);
        buffer.append(Strings.spaces(THREAD_NAME_FIELD_LENGTH - buffer.length())).append(": ");
        final int bufferBase = buffer.length();
        while (System.currentTimeMillis() < stopTime) {
            /*
             * Execute the method to stress.
             */
            final GeneralGridGeometry geometry = getRandomGrid();
            long time = System.nanoTime();
            executeQuery(geometry);
            time = System.nanoTime() - time;
            /*
             * Compute statistics about the ellapsed time.
             */
            long area = 1;
            final GridEnvelope envelope = geometry.getGridRange();
            final int dimension = envelope.getDimension();
            for (int i=0; i<dimension; i++) {
                area *= envelope.getSpan(i);
            }
            final double time_ms = time / 1E+6; // To milliseconds.
            final double time_mb = time_ms / (area / (1024*1024.0));
            statistics.add(time_ms);
            statsByMpx.add(time_mb);
            /*
             * Format how long it took to execute the request, in milliseconds and in
             * millseconds by megabyte. Note: it should actually be "by megapixel",
             * but we assume that few peoples would understand a "Mp" unit...
             */
            buffer.setLength(bufferBase);
            insertSpaces(bufferBase, buffer.append(Math.round(time_ms)), TIME_FIELD_LENGTH);
            buffer.append(" ms  ");
            insertSpaces(buffer.length(), buffer.append(Math.round(time_mb)), TIME_FIELD_LENGTH);
            buffer.append(" ms/Mb. Size=(");
            /*
             * Format the grid envelope and the geographic bounding box.
             */
            for (int i=0; i<dimension; i++) {
                if (i != 0) {
                    buffer.append(" \u00D7"); // Multiplication symbol
                }
                insertSpaces(buffer.length(), buffer.append(envelope.getSpan(i)), GRID_SIZE_FIELD_LENGTH);
            }
            out.println(buffer.append("), ").append(geometry.getEnvelope()));
            out.flush();
            try {
                Thread.sleep(random.nextInt(MAXIMAL_PAUSE) + 1);
            } catch (InterruptedException e) {
                // Go back to work...
            }
        }
        return statistics;
    }

    /**
     * Inserts spaces at the given position in the given buffer, in order to align the values
     * in a field of the given length.
     */
    private static void insertSpaces(final int pos, final StringBuilder buffer, final int length) {
        buffer.insert(pos, Strings.spaces(length - (buffer.length() - pos)));
    }

    /**
     * Runs a query for the given grid geometry.
     *
     * @param  request The grid geometry to request.
     * @throws CoverageStoreException If an error occurred during the test.
     */
    protected abstract void executeQuery(GeneralGridGeometry request) throws CoverageStoreException;
}
