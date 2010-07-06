/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;

import org.junit.*;
import org.geotoolkit.test.Depend;
import org.geotoolkit.referencing.operation.transform.CoordinateDomain;

import static java.lang.Math.*;
import static java.lang.Double.*;
import static org.geotoolkit.referencing.operation.provider.LambertConformal1SP.*;


/**
 * Tests the {@link LambertConformal} class. We test using various values of the latitude
 * of origin. We do not test with various values of standard parallels, because it is just
 * as other way to set the value of the <var>n</var> field in {@code LambertConformal}. As
 * long as we make this value varying, the latitude of origin is the simpliest approach.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
@Depend(MercatorTest.class)
public final class LambertConformalTest extends ProjectionTestCase {
    /**
     * Tolerance level for comparing floating point numbers.
     */
    private static final double TOLERANCE = 1E-12;

    /**
     * Creates a default test suite.
     */
    public LambertConformalTest() {
        super(LambertConformal.class, null);
    }

    /**
     * Returns a new instance of {@link LambertConformal}. See the class javadoc for an
     * explanation about why we ask only for the latitude of origin and not the standard
     * parallels.
     *
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @param  latitudeOfOrigin The latitude of origin.
     * @return Newly created projection.
     */
    private static LambertConformal create(final boolean ellipse, final double latitudeOfOrigin) {
        final UnitaryProjection.Parameters parameters = parameters(PARAMETERS, ellipse);
        parameters.latitudeOfOrigin = latitudeOfOrigin;
        if (ellipse) {
            return new LambertConformal(parameters);
        } else {
            return new LambertConformal.Spherical(parameters);
        }
    }

    /**
     * Tests the projection at a few extreme points.
     *
     * @throws ProjectionException Should never happen.
     */
    @Test
    public void testExtremes() throws ProjectionException {
        final double INF = POSITIVE_INFINITY;
        boolean ellipse = false;
        do {
            transform = create(ellipse, 45);
            tolerance = TOLERANCE;
            validate();

            assertEquals ("Not a number",     NaN, transform(NaN),               TOLERANCE);
            assertEquals ("Out of range",     NaN, transform(+2),                TOLERANCE);
            assertEquals ("Out of range",     NaN, transform(-2),                TOLERANCE);
            assertEquals ("Forward 0°N",      1,   transform(0),                 TOLERANCE);
            assertEquals ("Forward 90°N",     0,   transform(+PI/2),             TOLERANCE);
            assertEquals ("Forward 90°S",     INF, transform(-PI/2),             TOLERANCE);
            assertEquals ("Forward (90+ε)°N", 0,   transform(+nextUp(+PI/2)),    TOLERANCE);
            assertEquals ("Forward (90+ε)°S", INF, transform(-nextUp( PI/2)),    TOLERANCE);
            assertEquals ("Forward (90-ε)°N", 0,   transform(-nextUp(-PI/2)), 10*TOLERANCE);

            assertEquals ("Not a number", NaN, inverseTransform(NaN),        TOLERANCE);
            assertEquals ("Inverse 0",  +PI/2, inverseTransform( 0),         TOLERANCE);
            assertEquals ("Inverse +1",     0, inverseTransform(+1),         TOLERANCE);
            assertEquals ("Inverse -1",     0, inverseTransform(-1, true),   TOLERANCE);
            assertEquals ("Inverse +∞", -PI/2, inverseTransform(INF),        TOLERANCE);
            assertEquals ("Inverse -∞", -PI/2, inverseTransform(-INF, true), TOLERANCE);

            // Like the north case, but with sign inversed.
            transform = create(ellipse, -45);
            validate();

            assertEquals ("Not a number",     NaN, transform(NaN),               TOLERANCE);
            assertEquals ("Out of range",     NaN, transform(+2),                TOLERANCE);
            assertEquals ("Out of range",     NaN, transform(-2),                TOLERANCE);
            assertEquals ("Forward 0°N",      1,   transform(0),                 TOLERANCE);
            assertEquals ("Forward 90°N",     INF, transform(+PI/2),             TOLERANCE);
            assertEquals ("Forward 90°S",     0,   transform(-PI/2),             TOLERANCE);
            assertEquals ("Forward (90+ε)°N", INF, transform(+nextUp(+PI/2)),    TOLERANCE);
            assertEquals ("Forward (90+ε)°S", 0,   transform(-nextUp( PI/2)),    TOLERANCE);
            assertEquals ("Forward (90-ε)°S", 0,   transform( nextUp(-PI/2)), 10*TOLERANCE);

            assertEquals ("Not a number", NaN, inverseTransform(NaN),        TOLERANCE);
            assertEquals ("Inverse 0",  -PI/2, inverseTransform( 0),         TOLERANCE);
            assertEquals ("Inverse +∞", +PI/2, inverseTransform(INF),        TOLERANCE);
            assertEquals ("Inverse -∞", +PI/2, inverseTransform(-INF, true), TOLERANCE);
        } while ((ellipse = !ellipse) == true);
    }

    /**
     * Tests the unitary projection on an ellipse.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testUnitaryOnEllipse() throws TransformException {
        for (int phi=-90; phi<=90; phi+=15) {
            if (phi != 0) {
                transform = create(true, phi);
                tolerance = TOLERANCE;
                validate();
                assertFalse(isSpherical());
                stress(CoordinateDomain.GEOGRAPHIC_RADIANS, 268617081);
            }
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
            if (phi != 0) {
                transform = create(false, phi);
                tolerance = TOLERANCE;
                validate();
                assertTrue(isSpherical());
                stress(CoordinateDomain.GEOGRAPHIC_RADIANS, 588974557);
            }
        }
    }

    /**
     * Tests longitude rolling. Testing on the sphere is suffisient, since the
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
            transform = new LambertConformal.Spherical(parameters);
            validate();
            assertTrue(isSpherical());
            stressLongitudeRolling(CoordinateDomain.GEOGRAPHIC);
        }
    }

    /**
     * Tests the estimation of error. We expect an error close to zero everywhere,
     * except at poles which are omitted.
     *
     * @throws ProjectionException Should never happen.
     */
    @Test
    public void testErrorFitting() throws ProjectionException {
        boolean ellipse = true;
        do {
            for (int phi=-90; phi<=90; phi+=30) {
                if (phi != 0) {
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
            }
        } while ((ellipse = !ellipse) == false);
    }

    /**
     * Creates a projection using the {@link LambertConformal1SP} provider and
     * projects the point given in the "example" section of EPSG documentation.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testLambertConformal1SP() throws FactoryException, TransformException {
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Lambert Conic Conformal (1SP)");
        parameters.parameter("semi-major axis").setValue(6378206.400);
        parameters.parameter("semi-minor axis").setValue(6378206.400 * (1 - 1/294.97870));
        parameters.parameter("Latitude of natural origin").setValue(18.0);
        parameters.parameter("Longitude of natural origin").setValue(-77.0);
        parameters.parameter("Scale factor at natural origin").setValue(1.0);
        parameters.parameter("False easting").setValue(250000.00);
        parameters.parameter("False northing").setValue(150000.00);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());
        final double[] point    = new double[] {-(76 + (56 + 37.26/60)/60), 17 + (55 + 55.80/60)/60};
        final double[] expected = new double[] {255966.58, 142493.51};
        tolerance = 0.005;
        verifyTransform(point, expected);
        /*
         * Tries again with a spherical model, which requires a high tolerance value
         * (25 metres here). The purpose is to test the spherical formulas, which
         * have their own implementation in the Spherical nested class.
         */
        spherical(parameters, 18);
        transform = mtFactory.createParameterizedTransform(parameters);
        tolerance = 25;
        assertTrue(isSpherical());
        verifyTransform(point, expected);
    }

    /**
     * Creates a projection using the {@link LambertConformal2SP} provider and
     * projects the point given in the "example" section of EPSG documentation.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testLambertConformal2SP() throws FactoryException, TransformException {
        final double feets = 3.2808333333333333333; // Conversion from metre to feets.
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Lambert Conic Conformal (2SP)");
        parameters.parameter("semi-major axis").setValue(6378206.400);
        parameters.parameter("semi-minor axis").setValue(6378206.400 * (1 - 1/294.97870));
        parameters.parameter("Latitude of 1st standard parallel").setValue(28 + 23.0/60);
        parameters.parameter("Latitude of 2nd standard parallel").setValue(30 + 17.0/60);
        parameters.parameter("Latitude of false origin").setValue(27 + 50.0/60);
        parameters.parameter("Longitude of false origin").setValue(-99.0);
        parameters.parameter("Easting at false origin").setValue(2000000/feets);
        parameters.parameter("Northing at false origin").setValue(0*feets);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());
        final double[] point    = new double[] {-96.0, 28 + 30.0/60};
        final double[] expected = new double[] {2963503.91/feets, 254759.80/feets};
        tolerance = 0.005;
        verifyTransform(point, expected);
        /*
         * Tries again with a spherical model, which requires a high tolerance value
         * (600 metres here). The purpose is to test the spherical formulas, which
         * have their own implementation in the Spherical nested class.
         */
        parameters.parameter("semi-major axis").setValue(SPHERE_RADIUS);
        parameters.parameter("semi-minor axis").setValue(SPHERE_RADIUS);
        transform = mtFactory.createParameterizedTransform(parameters);
        tolerance = 600;
        assertTrue(isSpherical());
        verifyTransform(point, expected);
    }

    /**
     * Creates a projection using the {@link LambertConformal2SP.Belgium} provider
     * and projects the point given in the "example" section of EPSG documentation.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testBelgium() throws FactoryException, TransformException {
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Lambert Conic Conformal (2SP Belgium)");
        parameters.parameter("semi-major axis").setValue(6378388);
        parameters.parameter("semi-minor axis").setValue(6378388 * (1 - 1/297.0));
        parameters.parameter("Latitude of 1st standard parallel").setValue(49 + 50.0/60);
        parameters.parameter("Latitude of 2nd standard parallel").setValue(51 + 10.0/60);
        parameters.parameter("Latitude of false origin").setValue(90.0);
        parameters.parameter("Longitude of false origin").setValue(4 + (21 + 24.983/60)/60);
        parameters.parameter("Easting at false origin").setValue(150000.01);
        parameters.parameter("Northing at false origin").setValue(5400088.44);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());
        final double[] point    = new double[] {5 + (48 + 26.533/60)/60, 50 + (40 + 46.461/60)/60};
        final double[] expected = new double[] {251763.20, 153034.13};
        tolerance = 0.005;
        verifyTransform(point, expected);
        /*
         * Tries again with a spherical model, which requires a high tolerance value
         * (8 kilometres here). The purpose is to test the spherical formulas, which
         * have their own implementation in the Spherical nested class.
         */
        spherical(parameters, 50);
        transform = mtFactory.createParameterizedTransform(parameters);
        tolerance = 8000;
        assertTrue(isSpherical());
        verifyTransform(point, expected);
    }
}
