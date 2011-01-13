/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import org.junit.*;
import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.test.Depend;
import org.geotoolkit.referencing.operation.transform.CoordinateDomain;

import static java.lang.Double.*;
import static java.lang.Math.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.operation.provider.LambertAzimuthalEqualArea.*;


/**
 * Tests the {@link LambertAzimuthalEqualArea} class. We test using various values
 * of the latitude of origin, which is the only parameter impacting the internal
 * coefficients of that class (except for the excentricity).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@Depend(MercatorTest.class)
public class LambertAzimuthalEqualAreaTest extends ProjectionTestBase {
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
        final UnitaryProjection.Parameters parameters = parameters(PARAMETERS, ellipse);
        parameters.latitudeOfOrigin = latitudeOfOrigin;
        if (ellipse) {
            return new LambertAzimuthalEqualArea(parameters);
        } else {
            return new LambertAzimuthalEqualArea.Spherical(parameters);
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
            tolerance = TOLERANCE;
            validate();
            assertFalse(isSpherical());
            stress(CoordinateDomain.GEOGRAPHIC_RADIANS, 484117986);
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
            stress(CoordinateDomain.GEOGRAPHIC_RADIANS, 862247543);
        }
    }

    /**
     * Tests longitude rolling. Testing on the sphere is sufficient, since the
     * assertions contained in the {@code Spherical} class will compare with
     * the ellipsoidal case.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testLongitudeRolling() throws TransformException {
        tolerance = TOLERANCE;
        for (int centralMeridian=-180; centralMeridian<=180; centralMeridian+=45) {
            final UnitaryProjection.Parameters parameters = parameters(PARAMETERS, false);
            parameters.centralMeridian = centralMeridian;
            parameters.latitudeOfOrigin = 45;
            transform = new LambertAzimuthalEqualArea.Spherical(parameters);
            validate();
            assertTrue(isSpherical());
            stressLongitudeRolling(CoordinateDomain.GEOGRAPHIC);
        }
    }

    /**
     * Tests the estimation of error.
     *
     * @throws ProjectionException Should never happen.
     */
    @Test
    public void testErrorFitting() throws ProjectionException {
        boolean ellipse = true;
        do {
            for (int phi=-90; phi<=90; phi+=15) {
                final ErrorFitting error = new ErrorFitting(create(ellipse, phi));
                transform = error.projection();
                assertEquals(!ellipse, isSpherical());
                validate();
                error.fit(178, 88, 2);
                assertEquals(15931, error.delta.count());
                assertEquals(0.0,   error.delta.minimum(), TOLERANCE);
                assertEquals(0.0,   error.delta.maximum(), TOLERANCE);
                assertEquals(0.0,   error.delta.mean(),    TOLERANCE);
                assertEquals(0.0,   error.delta.rms(),     TOLERANCE);
            }
        } while ((ellipse = !ellipse) == false);
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
     * Creates a projection using the provider and projects the
     * point given in the "example" section of EPSG documentation.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testKnownPoint() throws FactoryException, TransformException {
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Lambert Azimuthal Equal Area");
        parameters.parameter("semi-major axis").setValue(6378137.0);
        parameters.parameter("semi-minor axis").setValue(6378137.0 * (1 - 1/298.2572221));
        parameters.parameter("Latitude of natural origin").setValue(52.0);
        parameters.parameter("Longitude of natural origin").setValue(10.0);
        parameters.parameter("False easting").setValue(4321000.00);
        parameters.parameter("False northing").setValue(3210000.00);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());

        final double[] point    = new double[] {5, 50};
        final double[] expected = new double[] {3962799.45, 2999718.85};
        tolerance = 0.005;
        verifyTransform(point, expected);
        /*
         * Tries again with a spherical model, which requires a high tolerance value
         * (410 metres here). The purpose is to test the spherical formulas, which
         * have their own implementation in the Spherical nested class.
         */
        spherical(parameters, 52);
        transform = mtFactory.createParameterizedTransform(parameters);
        tolerance = 410;
        assertTrue(isSpherical());
        verifyTransform(point, expected);
    }
}
