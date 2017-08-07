/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.referencing.operation.TransformException;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.test.DependsOn;
import org.apache.sis.referencing.operation.transform.CoordinateDomain;
import org.junit.*;

import static java.lang.StrictMath.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.operation.provider.Orthographic.PARAMETERS;


/**
 * Tests the {@link Orthographic} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 *
 * @since 3.00
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class OrthographicTest extends ProjectionTestBase {
    /**
     * Tolerance level for comparing floating point numbers.
     */
    private static final double TOLERANCE = 1E-11, DERIVATIVE_TOLERANCE = 1E-9;

    /**
     * Creates a default test suite.
     */
    public OrthographicTest() {
        super(Orthographic.class, null);
    }

    /**
     * Returns a new instance of {@link Orthographic}.
     *
     * @param  cx Longitude of projection centre.
     * @param  cY Latitude of projection centre.
     * @return Newly created projection.
     */
    private static Orthographic create(final double cx, final double cy) {
        final Parameters parameters = parameters(wrap(PARAMETERS), false, 0);
        parameters.parameter("central_meridian").setValue(cx);
        parameters.parameter("latitude_of_origin").setValue(cy);
        return new Orthographic(new org.geotoolkit.referencing.operation.provider.Orthographic(), parameters);
    }

    /**
     * Tests the unitary equatorial projection on a sphere.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testEquatorial() throws TransformException {
        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};
        transform = create(0, 0);
        tolerance = TOLERANCE;
        validate();
//      assertTrue(isSpherical());
        verifyInDomain(CoordinateDomain.GEOGRAPHIC_RADIANS_HALF_λ, 209067359);

        // Test the derivative on the same MathTransform than above.
        tolerance = DERIVATIVE_TOLERANCE;
        verifyDerivative(toRadians(5), toRadians(3));
    }

    /**
     * Tests the unitary polar projection on a sphere.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testPolar() throws TransformException {
        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};
        boolean south = false;
        do {
            transform = create(0, south ? -90 : 90);
            tolerance = TOLERANCE;
            validate();
//          assertTrue(isSpherical());
            /*
             * Note: we would expect CoordinateDomain.GEOGRAPHIC_RADIANS_SOUTH in the South case,
             * but then the latitudes are multiplied by -1 by the normalize affine transform. The
             * result is equivalent to using positive latitudes in the first place.
             */
            verifyInDomain(CoordinateDomain.GEOGRAPHIC_RADIANS_NORTH, 753524735);

            // Test the derivative on the same MathTransform than above.
            tolerance = DERIVATIVE_TOLERANCE;
            verifyDerivative(toRadians(5), toRadians(85));
        } while ((south = !south) == true);
    }

    /**
     * Tests the derivatives of the unitary oblique projection on a sphere.
     *
     * @throws TransformException Should never happen.
     *
     * @since 3.18
     */
    @Test
    public void testDerivative() throws TransformException {
        tolerance = DERIVATIVE_TOLERANCE;
        transform = create(10, 60);
        validate();

        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};
        verifyDerivative(toRadians(5), toRadians(30));
    }
}
