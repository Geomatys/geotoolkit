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

import java.util.concurrent.Callable;

import org.geotoolkit.math.Statistics;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageStoreException;


/**
 * Base class for stressors.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.14
 */
public abstract class Stressor extends RequestGenerator implements Callable<Statistics> {
    /**
     * The maximal pause to wait between two queries, in milliseconds.
     */
    private static final int MAXIMAL_PAUSE = 250;

    /**
     * The time (in milliseconds) when to stop the test.
     */
    private final long stopTime;

    /**
     * Creates a new stressor.
     *
     * @param domain Contains the maximal extent of the random envelopes to be generated.
     * @param stopTime The time (in milliseconds) when to stop the test.
     */
    protected Stressor(final GeneralGridGeometry domain, final long stopTime) {
        super(domain);
        this.stopTime = stopTime;
    }

    /**
     * Invokes {@link #executeQuery(GeneralGridGeometry)} with random grid geometries
     * until the execution time has been ellapsed. A slight pause is performed between
     * each query. Note that many threads may be running concurrently, so the CPU may
     * still busy at 100% despite this pause.
     *
     * @return The statistics about the execution time, in seconds.
     * @throws CoverageStoreException If an error occured during the test.
     */
    @Override
    public Statistics call() throws CoverageStoreException {
        final Statistics statistics = new Statistics();
        while (System.currentTimeMillis() < stopTime) {
            final GeneralGridGeometry geometry = getRandomGrid();
            long time = System.nanoTime();
            executeQuery(geometry);
            time = System.nanoTime() - time;
            statistics.add(time / 1E+9);
            try {
                Thread.sleep(random.nextInt(MAXIMAL_PAUSE) + 1);
            } catch (InterruptedException e) {
                // Go back to work...
            }
        }
        return statistics;
    }

    /**
     * Runs a query for the given grid geometry.
     *
     * @param  request The grid geometry to request.
     * @throws CoverageStoreException If an error occured during the test.
     */
    protected abstract void executeQuery(GeneralGridGeometry request) throws CoverageStoreException;
}
