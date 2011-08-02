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

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;

import org.junit.*;
import org.geotoolkit.test.Depend;
import org.geotoolkit.referencing.operation.transform.CoordinateDomain;

import static java.lang.StrictMath.*;
import static java.lang.Double.*;
import static org.opengis.test.Assert.*;
import static org.geotoolkit.referencing.operation.provider.Mercator1SP.*;
import static org.geotoolkit.referencing.operation.projection.UnitaryProjectionTest.LN_INFINITY;


/**
 * Tests the {@link Mercator} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Simon Reynard (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.19
 *
 * @since 3.00
 */
@Depend({ProjectionParametersTest.class, UnitaryProjectionTest.class})
public final class MercatorTest extends ProjectionTestBase {
    /**
     * Tolerance level for comparing floating point numbers.
     */
    private static final double TOLERANCE = 1E-12;

    /**
     * Creates a default test suite.
     */
    public MercatorTest() {
        super(Mercator.class, null);
    }

    /**
     * Returns a new instance of {@link Mercator}.
     *
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @return Newly created projection.
     */
    static Mercator create(final boolean ellipse) {
        final UnitaryProjection.Parameters parameters = parameters(PARAMETERS, ellipse);
        if (ellipse) {
            return new Mercator(parameters);
        } else {
            return new Mercator.Spherical(parameters);
        }
    }

    /**
     * Tests the projection at a few extreme points.
     *
     * @throws ProjectionException Should never happen.
     */
    @Test
    public void testExtremes() throws ProjectionException {
        boolean ellipse = false;
        do {
            transform = create(ellipse);
            tolerance = TOLERANCE;
            validate();

            assertEquals ("Not a number",     NaN,                    transform(NaN),           TOLERANCE);
            assertEquals ("Out of range",     NaN,                    transform(+2),            TOLERANCE);
            assertEquals ("Out of range",     NaN,                    transform(-2),            TOLERANCE);
            assertEquals ("Forward 0°N",      0,                      transform(0),             TOLERANCE);
            assertEquals ("Forward 90°N",     POSITIVE_INFINITY,      transform(+PI/2),         TOLERANCE);
            assertEquals ("Forward 90°S",     NEGATIVE_INFINITY,      transform(-PI/2),         TOLERANCE);
            assertEquals ("Forward (90+ε)°N", POSITIVE_INFINITY,      transform(+nextUp(PI/2)), TOLERANCE);
            assertEquals ("Forward (90+ε)°S", NEGATIVE_INFINITY,      transform(-nextUp(PI/2)), TOLERANCE);
            assertBetween("Forward (90-ε)°N", +MIN_VALUE, +MAX_VALUE, transform(-nextUp(-PI/2)));
            assertBetween("Forward (90-ε)°S", -MAX_VALUE, -MIN_VALUE, transform(+nextUp(-PI/2)));

            assertEquals ("Not a number",     NaN,   inverseTransform(NaN),                TOLERANCE);
            assertEquals ("Inverse 0 m",      0,     inverseTransform(0),                  TOLERANCE);
            assertEquals ("Inverse +∞",       +PI/2, inverseTransform(POSITIVE_INFINITY),  TOLERANCE);
            assertEquals ("Inverse +∞ appr.", +PI/2, inverseTransform(LN_INFINITY + 1),    TOLERANCE);
            assertEquals ("Inverse -∞",       -PI/2, inverseTransform(NEGATIVE_INFINITY),  TOLERANCE);
            assertEquals ("Inverse -∞ appr.", -PI/2, inverseTransform(-(LN_INFINITY + 1)), TOLERANCE);
        } while ((ellipse = !ellipse) == true);
    }

    /**
     * Tests the unitary projection on an ellipse.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testUnitaryOnEllipse() throws TransformException {
        final Mercator projection;
        transform = projection = create(true);
        assertFalse(isSpherical());
        assertEquals(0.08181919084262157, projection.excentricity, TOLERANCE);

        validate();
        tolerance = TOLERANCE;
        stress(CoordinateDomain.GEOGRAPHIC_RADIANS, 84018710);
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
        final Mercator projection;
        transform = projection = create(false);
        assertTrue(isSpherical());
        assertEquals(0.0, projection.excentricity, 0.0);

        validate();
        tolerance = TOLERANCE;
        stress(CoordinateDomain.GEOGRAPHIC_RADIANS, 514639509);
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
            transform = new Mercator.Spherical(parameters);
            assertTrue(isSpherical());
            validate();
            stressLongitudeRolling(CoordinateDomain.GEOGRAPHIC);
        }
    }

    /**
     * Creates a projection and tests the derivatives at a few points.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testDerivative() throws TransformException {
        tolerance = 1E-9;
        final double delta = toRadians(1.0 / 60) / 1852; // Approximatively one metre.
        derivativeDeltas = new double[] {delta, delta};

        // Tests spherical formulas
        transform = create(false);
        assertTrue(isSpherical());
        validate();
        verifyDerivative(toRadians(15), toRadians( 30));
        verifyDerivative(toRadians(10), toRadians(-60));

        // Tests ellipsoidal formulas
        transform = create(true);
        validate();
        verifyDerivative(toRadians(15), toRadians( 30));
        verifyDerivative(toRadians(10), toRadians(-60));
    }

    /**
     * Tests the estimation of error. We expect an error close to zero everywhere,
     * even at poles.
     *
     * @throws ProjectionException Should never happen.
     */
    @Test
    public void testErrorFitting() throws ProjectionException {
        boolean ellipse = true;
        do {
            final ErrorFitting error = new ErrorFitting(create(ellipse));
            transform = error.projection();
            assertEquals(!ellipse, isSpherical());
            validate();
            error.fit(180, 90);
            assertEquals(65341, error.delta.count());
            assertEquals(0.0,   error.delta.minimum(), TOLERANCE);
            assertEquals(0.0,   error.delta.maximum(), TOLERANCE);
            assertEquals(0.0,   error.delta.mean(),    TOLERANCE);
            assertEquals(0.0,   error.delta.rms(),     TOLERANCE);
        } while ((ellipse = !ellipse) == false);
    }

    /**
     * Creates a projection using the {@link Mercator1SP} provider and projects
     * the point given in the "example" section of EPSG documentation.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     *
     * @deprecated This test is partially replaced by {@link org.opengis.test.referencing.MathTransformTest}.
     * The GeoAPI test is not yet a complete replacement however, since it doesn't test the spherical formulas.
     */
    @Test
    @Deprecated
    public void testMercator1SP() throws FactoryException, TransformException {
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Mercator (1SP)");
        parameters.parameter("semi-major axis").setValue(6377397.155);
        parameters.parameter("semi-minor axis").setValue(6377397.155 * (1 - 1/299.15281));
        parameters.parameter("Latitude of natural origin").setValue(0.0);
        parameters.parameter("Longitude of natural origin").setValue(110.0);
        parameters.parameter("Scale factor at natural origin").setValue(0.997);
        parameters.parameter("False easting").setValue(3900000.0);
        parameters.parameter("False northing").setValue(900000.0);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());
        final double[] point    = new double[] {120, -3};
        final double[] expected = new double[] {5009726.58, 569150.82};
        tolerance = 0.005;
        verifyTransform(point, expected);
        /*
         * Tries again with a spherical model, which requires a high tolerance value
         * (2 kilometres here). The purpose is to test the spherical formulas, which
         * have their own implementation in the Spherical nested class.
         */
        parameters.parameter("semi-major axis").setValue(6371000.0);
        parameters.parameter("semi-minor axis").setValue(6371000.0);
        transform = mtFactory.createParameterizedTransform(parameters);
        tolerance = 2000;
        assertTrue(isSpherical());
        verifyTransform(point, expected);
        /*
         * Checks the derivative. Note that at the difference of testDerivative(),
         * the units are now degrees instead than radians.
         */
        tolerance = 1E-3; // Scale magnitude will be about 110861.
        final double delta = (1.0 / 60) / 1852; // Approximatively one metre.
        derivativeDeltas = new double[] {delta, delta};
        verifyDerivative(toRadians( 0), toRadians(  0));
        verifyDerivative(toRadians(15), toRadians( 30));
        verifyDerivative(toRadians(10), toRadians(-60));
    }

    /**
     * Creates a projection using the {@link Mercator2SP} provider and projects
     * the point given in the "example" section of EPSG documentation.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     *
     * @deprecated This test is partially replaced by {@link org.opengis.test.referencing.MathTransformTest}.
     * The GeoAPI test is not yet a complete replacement however, since it doesn't test the spherical formulas.
     */
    @Test
    @Deprecated
    public void testMercator2SP() throws FactoryException, TransformException {
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Mercator (2SP)");
        parameters.parameter("semi-major axis").setValue(6378245.0);
        parameters.parameter("semi-minor axis").setValue(6378245.0 * (1 - 1/298.3));
        parameters.parameter("Latitude of 1st standard parallel").setValue(42.0);
        parameters.parameter("Longitude of natural origin").setValue(51.0);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());
        final double[] point    = new double[] {53, 53};
        final double[] expected = new double[] {165704.29, 5171848.07};
        tolerance = 0.005;
        verifyTransform(point, expected);
        /*
         * Tries again with a spherical model, which requires a high tolerance value
         * (12 kilometres here). The purpose is to test the spherical formulas, which
         * have their own implementation in the Spherical nested class.
         */
        parameters.parameter("semi-major axis").setValue(SPHERE_RADIUS);
        parameters.parameter("semi-minor axis").setValue(SPHERE_RADIUS);
        transform = mtFactory.createParameterizedTransform(parameters);
        tolerance = 12000;
        assertTrue(isSpherical());
        verifyTransform(point, expected);
        /*
         * Checks the derivative. Note that at the difference of testDerivative(),
         * the units are now degrees instead than radians.
         */
        tolerance = 1E-3; // Scale magnitude will be about 82634.
        final double delta = (1.0 / 60) / 1852; // Approximatively one metre.
        derivativeDeltas = new double[] {delta, delta};
        verifyDerivative(toRadians( 0), toRadians(  0));
        verifyDerivative(toRadians(15), toRadians( 30));
        verifyDerivative(toRadians(10), toRadians(-60));
    }

    /**
     * Tests the Google projection. The length of the semi-minor axis is ignored
     * by definition of that particular projection. The point used here is given
     * in the "example" section of EPSG documentation.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     *
     * @since 3.03
     *
     * @deprecated This test is partially replaced by {@link org.opengis.test.referencing.MathTransformTest}.
     * The GeoAPI test is not yet a complete replacement however, since it doesn't test the spherical formulas.
     */
    @Test
    @Deprecated
    public void testGoogle() throws FactoryException, TransformException {
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Popular Visualisation Pseudo Mercator");
        parameters.parameter("semi-major axis").setValue(6378137.0);
        parameters.parameter("semi-minor axis").setValue(6378137.0 * (1 - 1/298.2572236));
        parameters.parameter("Latitude of natural origin").setValue(0.0);
        parameters.parameter("Longitude of natural origin").setValue(0.0);
        parameters.parameter("False easting").setValue(0.0);
        parameters.parameter("False northing").setValue(0.0);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertTrue(isSpherical());
        final double[] point    = new double[] {-(100+20.0/60), 24+(22+54.433/60)/60};
        final double[] expected = new double[] {-11169055.58, 2800000.00};
        tolerance = 0.005;
        verifyTransform(point, expected);
    }

    /**
     * Creates a projection using the {@link MillerCylindrical}
     * provider and projects the point given in the "example" section of
     * <a href="http://api.ign.fr/geoportail/api/doc/fr/developpeur/wmsc.html">IGN documentation</a>
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     *
     * @deprecated This test is partially replaced by {@link org.opengis.test.referencing.MathTransformTest}.
     * The GeoAPI test is not yet a complete replacement however, since it doesn't test the spherical formulas.
     */
    @Test
    @Deprecated
    public void testMiller() throws FactoryException, TransformException {
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Miller_Cylindrical");
        parameters.parameter("semi-major axis").setValue(6378137);
        parameters.parameter("semi-minor axis").setValue(6378137);
        transform = mtFactory.createParameterizedTransform(parameters);

        final double[] point    = new double[] {2.478917, 48.805639};
        final double[] expected = new double[] {275951.78, 5910061.78};
        tolerance = 0.005;
        verifyTransform(point, expected);
    }
}
