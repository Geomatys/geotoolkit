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

import java.awt.image.RenderedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogRecord;

import org.opengis.coverage.grid.GridEnvelope;

import org.apache.sis.math.Statistics;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.apache.sis.util.CharSequences;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.util.logging.LogProducer;
import org.apache.sis.util.logging.PerformanceLevel;


/**
 * Base class for stressors.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.14
 */
public abstract class Stressor extends RequestGenerator implements Callable<Statistics>, LogProducer {
    /**
     * The logger to use for reporting information.
     */
    protected static final Logger LOGGER = Logging.getLogger(Stressor.class);

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
     * The time (in nanoseconds) when to stop the test. This is set by {@link StressorGroup}
     * when a stress is started, and set to the actual stop time when the stress is finished.
     */
    long stopTime;

    /**
     * The name of the thread that executed the task.
     */
    String threadName;

    /**
     * The level to use for logging message. If {@code null}, then the level shall
     * be selected by {@link PerformanceLevel#forDuration(long, TimeUnit)}.
     */
    private Level logLevel;

    /**
     * The image viewer, or {@code null} if none.
     */
    ImageViewer viewer;

    /**
     * Creates a new stressor.
     *
     * @param domain Contains the maximal extent of the random envelopes to be generated.
     */
    protected Stressor(final GeneralGridGeometry domain) {
        super(domain);
    }

    /**
     * Returns {@code true} if logging is enabled.
     */
    private boolean isLoggable() {
        Level level = logLevel;
        if (level == null) {
            level = PerformanceLevel.SLOWEST;
        }
        return LOGGER.isLoggable(level);
    }

    /**
     * Returns the current logging level. The default value is one of the {@link PerformanceLevel}
     * constants, determined according the duration of the read operation.
     */
    @Override
    public Level getLogLevel() {
        final Level level = logLevel;
        return (level != null) ? level : PerformanceLevel.PERFORMANCE;
    }

    /**
     * Sets the logging level to the given value. A {@code null} value restores
     * the default level documented in the {@link #getLogLevel()} method.
     */
    @Override
    public void setLogLevel(Level level) {
        logLevel = level;
    }

    /**
     * Invokes {@link #executeQuery(GeneralGridGeometry)} with random grid geometries
     * until the execution time has been elapsed. A slight pause is performed between
     * each query. Note that many threads may be running concurrently, so the CPU may
     * still busy at 100% despite this pause.
     *
     * @return The statistics about the execution time, in milliseconds.
     * @throws Exception If an error occurred during the test.
     */
    @Override
    public Statistics call() throws Exception {
        final String sourceClassName = getClass().getName();
        threadName = Thread.currentThread().getName();
        final Statistics statistics = new Statistics(null);
        final StringBuilder buffer;
        final int bufferBase;
        if (isLoggable()) {
            buffer = new StringBuilder("Thread ").append(threadName);
            buffer.append(CharSequences.spaces(THREAD_NAME_FIELD_LENGTH - buffer.length())).append(": ");
            bufferBase = buffer.length();
        } else {
            buffer = null;
            bufferBase = 0;
        }
        long nanoTime;
        while ((nanoTime = System.nanoTime()) < stopTime) {
            /*
             * Execute the method to stress.
             */
            final GeneralGridGeometry geometry = getRandomGrid();
            long time = System.nanoTime();
            final RenderedImage image = executeQuery(geometry);
            time = System.nanoTime() - time;
            /*
             * Show the result, if we are allowed to.
             */
            if (viewer != null) {
                viewer.setImage(image);
            }
            /*
             * Compute statistics about the ellapsed time.
             */
            final double time_ms = time / 1E+6; // To milliseconds.
            statistics.add(time_ms);
            /*
             * Format how long it took to execute the request, in milliseconds and in
             * millseconds by megabyte. Note: it should actually be "by megapixel",
             * but we assume that few peoples would understand a "Mp" unit...
             */
            if (buffer != null) {
                buffer.setLength(bufferBase);
                insertSpaces(bufferBase, buffer.append(Math.round(time_ms)), TIME_FIELD_LENGTH);
                buffer.append(" ms. Size=(");
                /*
                 * Format the grid envelope and the geographic bounding box.
                 */
                final GridEnvelope envelope = geometry.getExtent();
                final int dimension = envelope.getDimension();
                for (int i=0; i<dimension; i++) {
                    if (i != 0) {
                        buffer.append(" \u00D7"); // Multiplication symbol
                    }
                    insertSpaces(buffer.length(), buffer.append(envelope.getSpan(i)), GRID_SIZE_FIELD_LENGTH);
                }
                buffer.append("), scale=").append((float) mean(getScale(geometry)));
                buffer.append(", ").append(geometry.getEnvelope());
                /*
                 * Log progress information and wait.
                 */
                Level level = logLevel;
                if (level == null) {
                    level = PerformanceLevel.forDuration(time, TimeUnit.NANOSECONDS);
                }
                final LogRecord record = new LogRecord(level, buffer.toString());
                record.setSourceClassName(sourceClassName);
                record.setSourceMethodName("call");
                record.setLoggerName(LOGGER.getName());
                LOGGER.log(record);
            }
            try {
                Thread.sleep(random.nextInt(MAXIMAL_PAUSE) + 1);
            } catch (InterruptedException e) {
                // Go back to work...
            }
        }
        stopTime = nanoTime;
        dispose();
        return statistics;
    }

    /**
     * Inserts spaces at the given position in the given buffer, in order to align the values
     * in a field of the given length.
     */
    private static void insertSpaces(final int pos, final StringBuilder buffer, final int length) {
        buffer.insert(pos, CharSequences.spaces(length - (buffer.length() - pos)));
    }

    /**
     * Runs a query for the given grid geometry.
     *
     * @param  request The grid geometry to request.
     * @return An image which represent the result of the request, or {@code null} if none.
     * @throws Exception If an error occurred during the test.
     */
    protected abstract RenderedImage executeQuery(GeneralGridGeometry request) throws Exception;

    /**
     * Invoked when the test is done. The default implementation does nothing.
     * Subclasses shall override this method if they have any resources that
     * need to be disposed.
     *
     * @throws Exception If an error occurred while disposing the resources.
     *
     * @since 3.15
     */
    protected void dispose() throws Exception {
    }
}
