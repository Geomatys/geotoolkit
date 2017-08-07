/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1999-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation.projection;

import org.opengis.referencing.operation.Matrix;
import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;
import org.apache.sis.referencing.operation.projection.ProjectionException;

import static java.lang.Math.*;
import static java.lang.Double.POSITIVE_INFINITY;
import static org.geotoolkit.referencing.operation.projection.UnitaryProjection.*;


/**
 * Static methods for assertions. This is used only when Java 1.4 assertions are enabled.
 * When a point has been projected using spherical formulas, compare with the same point
 * transformed using spherical formulas and throw an exception if the result differ.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 *
 * @since 2.0
 * @module
 */
final class Assertions extends Static {
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
     * Maximum difference allowed between spherical and ellipsoidal formulas when
     * comparing derivatives. Units are metres.
     */
    private static final double DERIVATIVE_TOLERANCE = 1E-1;

    /**
     * Do not allows instantiation of this class.
     */
    private Assertions() {
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
    static boolean checkTransform(final double[] expected, final int offset,
            final double x, final double y, final double tolerance)
            throws ProjectionException
    {
        if (expected != null) {
            compare("x", expected[offset  ], x, tolerance);
            compare("y", expected[offset+1], y, tolerance);
        }
        return tolerance < POSITIVE_INFINITY;
    }

    /**
     * Default version of {@link #checkTransform(double,double,double[],int)}.
     */
    static boolean checkTransform(final double[] expected, final int offset, final double x, final double y)
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
     * @param  λ         The longitude computed by spherical formulas, in radians.
     * @param  φ         The latitude computed by spherical formulas, in radians.
     * @param  tolerance The tolerance (optional).
     * @return Always {@code true} if the {@code tolerance} value is valid.
     * @throws ProjectionException if the comparison failed.
     */
    static boolean checkInverseTransform(final double[] expected, final int offset,
            final double λ, final double φ, final double tolerance)
            throws ProjectionException
    {
        compare("latitude", expected[offset+1], φ, tolerance);
        if (abs(PI/2 - abs(φ)) > ANGLE_TOLERANCE) {
            compare("longitude", expected[offset], λ, tolerance);
        }
        return tolerance < POSITIVE_INFINITY;
    }

    /**
     * Default version of {@link #checkInverseTransform(double,double,double[],int,double)}.
     */
    static boolean checkInverseTransform(final double[] expected, final int offset, final double λ, final double φ)
            throws ProjectionException
    {
        return checkInverseTransform(expected, offset, λ, φ, INVERSE_TOLERANCE);
    }

    /**
     * Checks if derivatives using spherical formulas produces the same result than ellipsoidal
     * formulas. This method is invoked during assertions only. The spherical formulas are used
     * for the "expected" results since they are simpler than the ellipsoidal formulas.
     *
     * @since 3.18
     */
    static boolean checkDerivative(final Matrix spherical, final Matrix ellipsoidal)
            throws ProjectionException
    {
        if (spherical != null || ellipsoidal != null) { // NullPointerException is ok if only one is null.
            compare("m00", spherical.getElement(0,0), ellipsoidal.getElement(0,0), DERIVATIVE_TOLERANCE);
            compare("m01", spherical.getElement(0,1), ellipsoidal.getElement(0,1), DERIVATIVE_TOLERANCE);
            compare("m10", spherical.getElement(1,0), ellipsoidal.getElement(1,0), DERIVATIVE_TOLERANCE);
            compare("m11", spherical.getElement(1,1), ellipsoidal.getElement(1,1), DERIVATIVE_TOLERANCE);
        }
        return true;
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
    private static void compare(final String variable, double expected, double actual, final double tolerance)
            throws ProjectionException
    {
        final double delta = abs(expected - actual);
        if (delta > tolerance) {
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
            throw new ProjectionException(Errors.format(Errors.Keys.TestFailure_3, variable,
                    String.valueOf(expected), String.valueOf(actual)) + // Force full precision.
                    "(Δ" + variable + '=' + delta + " ε=" + tolerance + ')');
        }
    }
}
