/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.measure.Latitude;
import org.geotoolkit.measure.Longitude;

import static java.lang.Math.*;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geotoolkit.referencing.operation.projection.UnitaryProjection.*;


/**
 * Static methods for assertions. This is used only when Java 1.4 assertions are enabled.
 * A projected point is compared with the inverse transform and an exception is thrown if
 * the distance is over some projection-dependent threshold.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
@Static
final class Assertions {
    /**
     * Maximum difference allowed when comparing the result of an inverse projections,
     * in radians. A value of 1E-7 radians is approximatively 0.5 kilometres. Note that
     * inverse projections are typically less accurate than forward projections. This
     * tolerance is set to such high value for avoiding too intrusive assertion errors.
     * This is okay only for catching gross programming errors.
     */
    private static final double INVERSE_TOLERANCE = 1E-7;

    /**
     * Maximum difference allowed when comparing the result of forward projections,
     * in distance on the unit ellipse. A value of 1E-7 is approximatively 0.1 metres.
     */
    private static final double FORWARD_TOLERANCE = 1E-7;

    /**
     * A conservative factory by which to increase the value returned by
     * {@link UnitaryProjection#getErrorEstimate}. We arbitrarily tolerate
     * 50% more than the provided estimate.
     */
    static final double ERROR_SCALE = 1.5;

    /**
     * Do not allows instantiation of this class.
     */
    private Assertions() {
    }

    /**
     * Returns the orthodromic distance between the two specified points using a spherical
     * approximation. The given points must be longitude and latitude angles in radians.
     * The returned value is the distance on a sphere of radius 1.
     */
    private static double orthodromicDistance(final double x1, final double y1,
                                              final double x2, final double y2)
    {
        final double dx = abs(x2 - x1) % (2*PI);
        double rho = sin(y1)*sin(y2) + cos(y1)*cos(y2)*cos(dx);
        if (rho > +1) {assert rho <= +(1+ITERATION_TOLERANCE) : rho; rho = +1;}
        if (rho < -1) {assert rho >= -(1+ITERATION_TOLERANCE) : rho; rho = -1;}
        return acos(rho);
    }

    /**
     * Checks if the transform of {@code point} is close enough to {@code expected}. "Close enough"
     * means that the two points are separated by a distance shorter than the value returned by
     * {@link UnitaryProjection#getErrorEstimate}.
     *
     * @param transform
     *          The transform to test.
     * @param inverse
     *          {@code true} for an inverse transform instead of a direct one. In this case
     *          the units of {@code srcPts} and {@code target} are interchanged compared to
     *          the {@code false} case.
     * @param expected
     *          Point to compare to, in units of distance on the unit ellipse
     *          if {@code inverse} is {@code false}.
     * @param srcPts
     *          Point to transform, in radians if {@code inverse} is {@code false}.
     *
     * @return Always {@code true}.
     * @throws ProjectionError If the check failed.
     * @throws ProjectionException If the projection failed for an other reason.
     */
    static boolean checkReciprocal(final UnitaryProjection transform, final boolean inverse,
                                   final double[] expected, int dstOff,
                                   final double[] srcPts,   int srcOff,
                                   int numPts)
            throws ProjectionException
    {
        /*
         * If the arrays overlap, we will perform the check only on
         * the portion of the array that doesn't overlap (if any).
         */
        if (srcPts == expected) {
            int n;
            n = (srcOff - dstOff) / 2;
            if (n >= 0) {
                if (n < numPts) {
                    numPts = n;
                }
            } else {
                n = -n;
                if (n < numPts) {
                    numPts -= n;
                    srcOff += numPts * 2;
                    dstOff += numPts * 2;
                    numPts = n;
                }
            }
        }
        /*
         * Now performs the check, allocating the buffer only if needed
         * (numPts may had set to zero as a result of the above check).
         */
        if (--numPts >= 0) {
            final double[] buffer = new double[2];
            do {
                final double longitude;
                final double latitude;
                final double distance;
                if (inverse) {
                    // Computes orthodromic distance (spherical model).
                    transform.inverseTransform(srcPts, srcOff, buffer, 0);
                    distance = orthodromicDistance(longitude = buffer[0],
                                                   latitude  = buffer[1],
                                                   expected[dstOff++],
                                                   expected[dstOff++]);
                } else {
                    // Computes cartesian distance.
                    transform.transform(srcPts, srcOff, buffer, 0);
                    longitude = srcPts[srcOff++];
                    latitude  = srcPts[srcOff++];
                    distance  = Math.hypot(expected[dstOff++] - buffer[0],
                                           expected[dstOff++] - buffer[1]);
                }
                final double estimate = transform.getErrorEstimate(longitude, latitude);
                if (distance > estimate*ERROR_SCALE + FORWARD_TOLERANCE) {
                    /*
                     * Do not fail for NaN values. For other cases we must throw a ProjectionException,
                     * not an AssertionError, because some code like CRS.transform(CoordinateOperation,
                     * ...) will project points that are know to be suspicious by surrounding them in
                     * "try ... catch" statements. Failure are normal in their case and we want to let
                     * them handle the exception the way they are used to.
                     */
                    final Parameters parameters = transform.getUnmarshalledParameters();
                    throw new ProjectionError(Errors.format(Errors.Keys.PROJECTION_CHECK_FAILED_$4,
                            distance * parameters.semiMajor,
                            new Longitude(toDegrees(longitude) - parameters.centralMeridian),
                            new Latitude (toDegrees(latitude)  - parameters.latitudeOfOrigin),
                            transform.getName()));
                }
                srcOff += 2;
            } while (--numPts >= 0);
        }
        return true;
    }

    /**
     * Checks if transform using spherical formulas produces the same result
     * than ellipsoidal formulas. This method is invoked during assertions only.
     *
     * @param  expected The (easting,northing) computed by ellipsoidal formulas.
     * @param  offset Index of the coordinate in the {@code expected} array.
     * @param  x The easting computed by spherical formulas on the unit sphere.
     * @param  y The northing computed by spherical formulas on the unit sphere.
     * @param  tolerance The tolerance (optional).
     * @return Always {@code true} if the {@code tolerance} value is valid.
     * @throws ProjectionException if the comparison failed.
     */
    static boolean checkTransform(final double[] expected, int offset,
            final double x, final double y, final double tolerance)
            throws ProjectionException
    {
        compare("x", expected[offset++], x, tolerance);
        compare("y", expected[offset++], y, tolerance); // NOSONAR: offset incremeted as a matter of principle.
        return tolerance < POSITIVE_INFINITY;
    }

    /**
     * Default version of {@link #checkTransform(double,double,double[],int)}.
     */
    static boolean checkTransform(double[] expected, int offset, double x, double y)
            throws ProjectionException
    {
        return checkTransform(expected, offset, x, y, FORWARD_TOLERANCE);
    }

    /**
     * Checks if inverse transform using spherical formulas produces the same result
     * than ellipsoidal formulas. This method is invoked during assertions only.
     *
     * {@note This method ignores the longitude if the latitude is at a pole,
     *        because in such case the longitude is meanless.}
     *
     * @param  expected  The (longitude,latitude) computed by ellipsoidal formulas.
     * @param  offset    Index of the coordinate in the {@code expected} array.
     * @param  lambda    The longitude computed by spherical formulas, in radians.
     * @param  phi       The latitude computed by spherical formulas, in radians.
     * @param  tolerance The tolerance (optional).
     * @return Always {@code true} if the {@code tolerance} value is valid.
     * @throws ProjectionException if the comparison failed.
     */
    static boolean checkInverseTransform(final double[] expected, final int offset,
            final double lambda, final double phi, final double tolerance)
            throws ProjectionException
    {
        compare("latitude", expected[offset+1], phi, tolerance);
        if (abs(PI/2 - abs(phi)) > ANGLE_TOLERANCE) {
            compare("longitude", expected[offset], lambda, tolerance);
        }
        return tolerance < POSITIVE_INFINITY;
    }

    /**
     * Default version of {@link #checkInverseTransform(double,double,double[],int,double)}.
     */
    static boolean checkInverseTransform(double[] expected, int offset, double lambda, double phi)
            throws ProjectionException
    {
        return checkInverseTransform(expected, offset, lambda, phi, INVERSE_TOLERANCE);
    }

    /**
     * Compares two value for equality up to some tolerance threshold. This is used during
     * assertions only. The comparison does not fail if at least one value to compare is
     * {@link Double#NaN} or infinity.
     * <p>
     * <strong>Hack:</strong> if the {@code variable} name starts by lower-case {@code L}
     * (as in "longitude" and "latitude"), then the value is assumed to be an angle in
     * radians. This is used for formatting an error message, if needed.
     *
     * @throws ProjectionException if the comparison failed.
     */
    private static void compare(String variable, double expected, double actual, double tolerance)
            throws ProjectionException
    {
        if (abs(expected - actual) > tolerance) {
            if (variable.charAt(0) == 'l') {
                actual   = toDegrees(actual);
                expected = toDegrees(expected);
            } else if (abs(actual) > 30 && abs(expected) > 30) {
                /*
                 * If the projected point tend toward infinity, treats the value as if is was
                 * really infinity. Note that 30 is considered as "close to infinity" because
                 * of the result we get when projecting 90°N using Mercator spherical formula:
                 *
                 *     y = log(tan(π/4 + φ/2))
                 *
                 * Because there is no exact representation of π/2 in base 2, the tangent
                 * function gives 1.6E+16 instead of infinity, which leads the logarithmic
                 * function to give us 37.3.
                 *
                 * This behavior is tested in MercatorTest.testSphericalFormulas().
                 */
                if (signum(actual) == signum(expected)) {
                    return;
                }
            }
            throw new ProjectionError(Errors.format(Errors.Keys.TEST_FAILURE_$3, variable, expected, actual));
        }
    }
}
