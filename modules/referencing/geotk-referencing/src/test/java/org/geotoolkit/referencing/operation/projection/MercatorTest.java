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
import static org.opengis.test.Assert.*;
import static org.geotoolkit.referencing.operation.provider.Mercator1SP.PARAMETERS;
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
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class MercatorTest extends ProjectionTestBase {
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
        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
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
     * Runs the tests defined in the GeoAPI-conformance module.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void runGeoapiTest() throws FactoryException, TransformException {
        new GeoapiTest(mtFactory).testMercator1SP();
        new GeoapiTest(mtFactory).testMercator2SP();
        new GeoapiTest(mtFactory).testPseudoMercator();
        new GeoapiTest(mtFactory).testMiller();
    }
}
