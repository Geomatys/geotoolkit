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

import org.opengis.util.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.apache.sis.referencing.operation.transform.CoordinateDomain;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.test.DependsOn;
import org.junit.*;

import static java.lang.Double.*;
import static java.lang.StrictMath.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.operation.provider.LambertAzimuthalEqualArea.PARAMETERS;


/**
 * Tests the {@link LambertAzimuthalEqualArea} class. We test using various values
 * of the latitude of origin, which is the only parameter impacting the internal
 * coefficients of that class (except for the eccentricity).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 *
 * @since 3.00
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class LambertAzimuthalEqualAreaTest extends ProjectionTestBase {
    /**
     * Tolerance level for comparing floating point numbers.
     */
    private static final double TOLERANCE = 1E-8;

    /**
     * Creates a default test suite.
     */
    public LambertAzimuthalEqualAreaTest() {
        super(LambertAzimuthalEqualArea.class, null);
    }

    /**
     * Returns a new instance of {@link LambertAzimuthalEqualArea}.
     *
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @param  latitudeOfOrigin The latitude of origin.
     * @return Newly created projection.
     */
    private static LambertAzimuthalEqualArea create(final boolean ellipse, final double latitudeOfOrigin) {
        final Parameters parameters = parameters(wrap(PARAMETERS), ellipse, 0);
        parameters.parameter("latitude_of_origin").setValue(latitudeOfOrigin);
        if (ellipse) {
            return new LambertAzimuthalEqualArea(new org.geotoolkit.referencing.operation.provider.LambertAzimuthalEqualArea(), parameters);
        } else {
            return new LambertAzimuthalEqualArea.Spherical(new org.geotoolkit.referencing.operation.provider.LambertAzimuthalEqualArea(), parameters);
        }
    }

    /**
     * Tests the projection at a few extreme points in the polar case. We test on the sphere
     * only, which imply the test using ellipsoidal formulas as well through Java assertions.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testPolar() throws TransformException {
        transform = concatenated(create(false, 90));
        tolerance = TOLERANCE;
        validate();
        /*
         * Project the origin. Result should be (0,0). Do not test the inverse projection
         * since the longitude could be anything and still be the North pole. We test that
         * by projecting again with an other longitude, set to 45°, and expect the same result.
         */
        final double[] point = new double[] {0, 90};
        transform.transform(point, 0, point, 0, 1);
        assertEquals(0, point[0], tolerance);
        assertEquals(0, point[1], tolerance);
        transform.inverse().transform(point, 0, point, 0, 1);
        assertEquals( 0, point[0], 180);
        assertEquals(90, point[1], tolerance);

        point[0] = 45;
        point[1] = 90;
        transform.transform(point, 0, point, 0, 1);
        assertEquals(0, point[0], tolerance);
        assertEquals(0, point[1], tolerance);
        transform.inverse().transform(point, 0, point, 0, 1);
        assertEquals(45, point[0], 180);
        assertEquals(90, point[1], tolerance);
        /*
         * Project a point on the equator, at 0° and 180° longitude. Result should
         * be (0, sqrt(2)) positive or negative depending on the longitude.
         */
        point[0] = 0;
        point[1] = 0;
        transform.transform(point, 0, point, 0, 1);
        assertEquals(0, point[0], tolerance);
        assertEquals(-sqrt(2), point[1] / SPHERE_RADIUS, tolerance);
        transform.inverse().transform(point, 0, point, 0, 1);
        assertEquals(0, point[0], tolerance);
        assertEquals(0, point[1], tolerance);

        point[0] = 180;
        point[1] = 0;
        transform.transform(point, 0, point, 0, 1);
        assertEquals(0, point[0], tolerance);
        assertEquals(sqrt(2), point[1] / SPHERE_RADIUS, tolerance);
        transform.inverse().transform(point, 0, point, 0, 1);
        assertEquals(180, point[0], tolerance);
        assertEquals(  0, point[1], tolerance);
        /*
         * Project the antipode. Result would be (0, -2) if this operation was allowed.
         * Actually the formulas would work, but every points on a circle or radius 2
         * would be the pole so returning a single value may not be appropriate. Proj4
         * was returning an error code in this case.
         */
        point[0] = 0;
        point[1] = -90;
        transform.transform(point, 0, point, 0, 1);
        assertEquals(NaN, point[0] / SPHERE_RADIUS, tolerance);
        assertEquals(NaN, point[1] / SPHERE_RADIUS, tolerance);
    }

    /**
     * Tests the projection at a few extreme points in the oblique case. We test on the sphere
     * only, which imply the test using ellipsoidal formulas as well through Java assertions.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testOblique() throws TransformException {
        transform = concatenated(create(false, 45));
        tolerance = TOLERANCE;
        validate();

        // Projects the origin.
        final double[] point    = new double[] {0, 45};
        final double[] expected = new double[] {0, 0};
        verifyTransform(point, expected);

        // Project the antipode.
        point[0] = 180;
        point[1] = -45;
        transform.transform(point, 0, point, 0, 1);
        assertEquals(NaN, point[0] / SPHERE_RADIUS, tolerance);
        assertEquals(NaN, point[1] / SPHERE_RADIUS, tolerance);

        // Project the almost-antipode.
        point[0] = 180;
        point[1] = -44.9;
        transform.transform(point, 0, point, 0, 1);
        assertEquals(0, point[0] / SPHERE_RADIUS, tolerance);
        assertEquals(2, point[1] / SPHERE_RADIUS, 1E-6);
    }

    /**
     * Tests the unitary projection on an ellipse.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testUnitaryOnEllipse() throws TransformException {
        for (int phi=-90; phi<=90; phi+=15) {
            transform = create(true, phi);
            tolerance = 1E-2;   // TODO: investigate why the difference is so large.
            validate();
            assertFalse(isSpherical());
            final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
            derivativeDeltas = new double[] {delta, delta};
            verifyInDomain(CoordinateDomain.GEOGRAPHIC_RADIANS, 484117986);
        }
    }

    /**
     * Tests the unitary projection on a sphere. This is actually a more expensive check
     * than the one on ellipse because the spherical case contains assertion statements
     * comparing the results with the ones obtained from ellipsoidal formulas.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testUnitaryOnSphere() throws TransformException {
        for (int phi=-90; phi<=90; phi+=15) {
            transform = create(false, phi);
            tolerance = TOLERANCE;
            validate();
            assertTrue(isSpherical());
            final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
            derivativeDeltas = new double[] {delta, delta};
            verifyInDomain(CoordinateDomain.GEOGRAPHIC_RADIANS, 862247543);
        }
    }

    /**
     * Tests a point which is was known problematic in GeoTools 2.x.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testSpecific() throws TransformException {
        transform = create(false, -75);
        tolerance = TOLERANCE;
        assertTrue(isSpherical());
        final double[] point = new double[] {toRadians(-90), toRadians(-8)};
        transform.transform(point, 0, point, 0, 1);
        transform.inverse().transform(point, 0, point, 0, 1);
        assertEquals(-90, toDegrees(point[0]), tolerance);
        assertEquals( -8, toDegrees(point[1]), tolerance);
    }

    /**
     * Creates a projection and tests the derivatives at a few points.
     *
     * @throws TransformException Should never happen.
     *
     * @since 3.18
     */
    @Test
    public void testDerivative() throws TransformException {
        tolerance = 1E-9;
        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};

        // Polar projection.
        transform = create(true, 90);
        validate();
        verifyDerivative(toRadians(-6), toRadians(80));

        // Intentionally above the pole.
        verifyDerivative(toRadians(-6), toRadians(100));

        // Polar projection, spherical formulas.
        transform = create(false, 90);
        validate();
        verifyDerivative(toRadians(-6), toRadians(85));

        // Equatorial projection, spherical formulas.
        transform = create(false, 0);
        validate();
        verifyDerivative(toRadians(3), toRadians(4));

        // Oblique projection, ellipsoidal formulas.
        transform = create(true, 8);
        validate();
        verifyDerivative(toRadians(-6), toRadians(2));

        // Oblique projection, spherical formulas.
        transform = create(false, 8);
        validate();
        verifyDerivative(toRadians(-6), toRadians(2));
    }

    /**
     * Runs the test defined in the GeoAPI-conformance module.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void runGeoapiTest() throws FactoryException, TransformException {
        new GeoapiTest(mtFactory).testLambertAzimuthalEqualArea();
    }
}
