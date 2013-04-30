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

import org.junit.*;
import org.apache.sis.test.DependsOn;
import org.geotoolkit.referencing.operation.transform.CoordinateDomain;

import static java.lang.StrictMath.*;
import static java.lang.Double.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.operation.provider.LambertConformal1SP.PARAMETERS;


/**
 * Tests the {@link LambertConformal} class. We test using various values of the latitude
 * of origin. We do not test with various values of standard parallels, because it is just
 * as other way to set the value of the <var>n</var> field in {@code LambertConformal}. As
 * long as we make this value varying, the latitude of origin is the simplest approach.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.19
 *
 * @since 3.00
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class LambertConformalTest extends ProjectionTestBase {
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
     * @param  latitudeOfOrigin The latitude of origin, in decimal degrees.
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
            transform = new LambertConformal.Spherical(parameters);
            validate();
            assertTrue(isSpherical());
            stressLongitudeRolling(CoordinateDomain.GEOGRAPHIC);
        }
    }

    /**
     * Creates a projection and derivates a few points.
     *
     * @throws TransformException Should never happen.
     *
     * @since 3.14
     */
    @Test
    public void testDerivative() throws TransformException {
        tolerance = 1E-9;
        transform = create(false, 45);
        assertTrue(isSpherical());
        validate();

        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};
        verifyDerivative(toRadians( 0), toRadians( 0));
        verifyDerivative(toRadians(15), toRadians(30));
        verifyDerivative(toRadians(10), toRadians(60));

        transform = create(true, 45);
        assertFalse(isSpherical());
        validate();
        verifyDerivative(toRadians(15), toRadians(40));
    }

    /**
     * Runs the tests defined in the GeoAPI-conformance module.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void runGeoapiTest() throws FactoryException, TransformException {
        new GeoapiTest(mtFactory).testLambertConicConformal1SP();
        new GeoapiTest(mtFactory).testLambertConicConformal2SP();
        new GeoapiTest(mtFactory).testLambertConicConformalBelgium();
    }
}
