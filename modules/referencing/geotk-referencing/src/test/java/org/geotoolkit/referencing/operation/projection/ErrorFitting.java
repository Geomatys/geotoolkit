/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.projection;

import java.util.Set;
import java.util.HashSet;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import static java.lang.Math.*;

import org.geotoolkit.math.Plane;
import org.geotoolkit.math.Statistics;

import static org.opengis.test.Assert.*;
import static org.geotoolkit.util.Utilities.hashMapCapacity;
import static org.geotoolkit.referencing.operation.projection.Assertions.ERROR_SCALE;
import static org.geotoolkit.referencing.operation.projection.UnitaryProjection.ITERATION_TOLERANCE;


/**
 * Computes a model of the errors in a projection. "Error" is defined as the difference
 * between a coordinate and the result of projecting it and applying the inverse projection
 * right after.
 * <p>
 * The model is assumed of the form
 * <var>error</var> = c + cx*<var>&lambda;</var>² + cy*<var>&phi;</var>²
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
final class ErrorFitting {
    /**
     * Set to {@code true} for debugging the {@link #fit} method. This is usually not needed;
     * the {@code assertEquals} right after the loop should detect broken algorithm.
     */
    private static final boolean DEBUG = false;

    /**
     * The normalize affine transform to be applied before the projection. This is initialized
     * to the normalize affine transform of the projection given at construction time. However
     * callers can modify this transform afterward, for example in order to apply a scale of 2
     * and reducing the geographic region to be tested as a trick for using a step of 2 (thus
     * testing less points).
     */
    private final AffineTransform normalize;

    /**
     * The projection to be tested.
     */
    private final UnitaryProjection projection;

    /**
     * The model to be computed by {@link #fit}
     */
    public final Plane model;

    /**
     * Statistics about the differences between the errors computed by the model
     * and the actual errors.
     */
    public final Statistics delta;

    /**
     * Creates a new object which will compute the errors for the given projection.
     *
     * @param projection The projection to be tested.
     */
    public ErrorFitting(final UnitaryProjection projection) {
        this.projection = projection;
        normalize = new AffineTransform(projection.parameters.normalize(true));
        model = new Plane();
        delta = new Statistics();
    }

    /**
     * Returns the unitary projection to be tested.
     */
    public UnitaryProjection projection() {
        return projection;
    }

    /**
     * Computes the error of current projection, and computes a regression plane fitting
     * those errors. This method sets the {@link #model} and {@link #delta} fields to the
     * result of the computation.
     *
     * @param  maxLongitude Maximal longitude value in degrees, inclusive.
     * @param  maxLatitude  Maximal latitude value in degrees, inclusive.
     * @return An estimation of the Pearson correlation coefficient.
     * @throws ProjectionException if a projection failed.
     */
    final double fit(final int maxLongitude, final int maxLatitude) throws ProjectionException {
        assertBetween("Illegal longitude.", 1, 180, maxLongitude);
        assertBetween("Illegal latitude.",  1,  90, maxLatitude);
        final int n = 4*(maxLongitude * maxLatitude) + 2*(maxLongitude + maxLatitude) + 1;
        final double[] x = new double[n];
        final double[] y = new double[n];
        final double[] z = new double[n];
        final Set<Point> debug = DEBUG ? new HashSet<Point>(hashMapCapacity(n)) : null;
        /*
         * Tests the first point, which is located at (0,0).
         * The loop after will test all other points.
         */
        final double[] buffer = new double[2];
        normalize.transform(buffer, 0, buffer, 0, 1);
        x[0] = buffer[0];
        y[0] = buffer[1];
        projection.transform(buffer, 0, buffer, 0);
        projection.inverseTransform(buffer, 0, buffer, 0);
        z[0] = hypot(buffer[0]-x[0], buffer[1]-y[0]);
        x[0] *= x[0];
        y[0] *= y[0];
        int count = 1;
        if (DEBUG) {
            debug.add(new Point());
        }
        /*
         * The rather convolved loops below start the scan at the projection center, and go
         * toward borders. Assuming that the error is minimal at the center and increases
         * toward borders, this iteration order tends to put smallest errors first in the
         * z array, thus reducing the risk of rounding errors in the Plane.fit(...) method.
         */
        final int stop = max(maxLongitude, maxLatitude);
        for (int scan=1; scan <= stop; scan++) {
            /*
             * Given m = min(scan, maxLatitude) (no -), the yi iteration
             * order below is: {0, -1, +1, -2, +2, -3, +3, ..., -m, +m}.
             */
            final int ys = -min(scan, maxLatitude);
            for (int yi = 0; yi >= ys; yi = (yi >= 0 ? ~yi : -yi)) {
                final boolean isBorder = (abs(yi) == scan);
                final int xs = -min(scan, maxLongitude);
                for (int xi=(isBorder ? 0 : -scan); xi >= xs; xi = (xi >= 0 ? ~xi : -xi)) {
                    if (DEBUG) {
                        assertEquals(count, debug.size());
                        if (!debug.add(new Point(xi,yi))) {
                            fail("Duplicated coordinate: " + yi + "°N " + xi + "°E");
                        }
                        if (count >= n) {
                            fail("Out of bounds: " + yi + "°N " + xi + "°E");
                        }
                    }
                    buffer[0] = xi;
                    buffer[1] = yi;
                    normalize.transform(buffer, 0, buffer, 0, 1);
                    final double lambda = buffer[0];
                    final double phi    = buffer[1];
                    try {
                        projection.transform(buffer, 0, buffer, 0);
                        projection.inverseTransform(buffer, 0, buffer, 0);
                    } catch (ProjectionException e) {
                        System.out.flush();
                        System.err.println("The coordinate that failed is (" + toDegrees(lambda) +
                                ", " + toDegrees(phi) + ")° at index (" + xi + ", " + yi + ").");
                        throw e;
                    }
                    final double model = projection.getErrorEstimate(lambda, phi);
                    final double error = hypot(buffer[0] - lambda, buffer[1] - phi);
                    if (error > model*ERROR_SCALE + ITERATION_TOLERANCE) {
                        fail("Expected an error of " + model + " but calculated " + error);
                    }
                    x[count] = lambda * lambda;
                    y[count] = phi * phi;
                    z[count] = error;
                    count++;
                }
            }
        }
        assertEquals(n, count);
        final double r = model.fit(x, y, z);
        delta.reset();
        for (count=0; count<n; count++) {
            delta.add(model.z(x[count], y[count]) - z[count]);
        }
        return r;
    }

    /**
     * Same as above, but using a step different than 1°. Note that this method modifies
     * the {@link #normalize} affine transform.
     *
     * @param  maxLongitude Maximal longitude value in degrees, inclusive.
     * @param  maxLatitude  Maximal latitude value in degrees, inclusive.
     * @param  step The step in degrees.
     * @return An estimation of the Pearson correlation coefficient.
     * @throws ProjectionException if a projection failed.
     */
    final double fit(final int maxLongitude, final int maxLatitude, final int step) throws ProjectionException {
        assertEquals("Maximal longitude must be a divisor of the step.", 0, maxLongitude % step);
        assertEquals("Maximal latitude must be a divisor of the step.",  0, maxLatitude  % step);
        normalize.scale(step, step);
        return fit(maxLongitude / step, maxLatitude / step);
    }

    /**
     * Returns a string representation of the computation result.
     */
    @Override
    public String toString() {
        final String lineSeparator = System.getProperty("line.separator", "\n");
        return "Model: " + model + lineSeparator + "Error statistics: " + lineSeparator + delta;
    }
}
