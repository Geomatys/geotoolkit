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

import org.junit.*;
import org.apache.sis.test.DependsOn;
import org.geotoolkit.referencing.operation.transform.CoordinateDomain;

import static java.lang.StrictMath.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.operation.provider.AlbersEqualArea.PARAMETERS;


/**
 * Tests the {@link AlbersEqualArea} class. We test using various values of standard parallels.
 * We do not test with various values of the latitude of origin, because its only effect is to
 * modify the translation term on the <var>y</var> axis.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.19
 *
 * @since 3.00
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class AlbersEqualAreaTest extends ProjectionTestBase {
    /**
     * Tolerance level for comparing floating point numbers. Since the Albers Equal Area
     * projection is implemented with iterative methods, we can not ask more than the
     * tolerance value used in iterations.
     */
    private static final double TOLERANCE = UnitaryProjection.ITERATION_TOLERANCE;

    /**
     * Creates a default test suite.
     */
    public AlbersEqualAreaTest() {
        super(AlbersEqualArea.class, null);
    }

    /**
     * Returns a new instance of {@link AlbersEqualArea}. See the class javadoc for an
     * explanation about why we ask only for the standard parallels and not the latitude
     * of origin.
     *
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @param  phi1 First standard parallel.
     * @param  phi2 Second standard parallel.
     * @return Newly created projection.
     */
    private static AlbersEqualArea create(final boolean ellipse, double phi1, double phi2) {
        final UnitaryProjection.Parameters parameters = parameters(PARAMETERS, ellipse, 2, UnitaryProjection.Parameters.class);
        parameters.standardParallels[0] = phi1;
        parameters.standardParallels[1] = phi2;
        if (ellipse) {
            return new AlbersEqualArea(parameters);
        } else {
            return new AlbersEqualArea.Spherical(parameters);
        }
    }

    /**
     * Tests the unitary projection on an ellipse.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testUnitaryOnEllipse() throws TransformException {
        for (int phi1=-90; phi1<=90; phi1+=45) {
            for (int phi2=-90; phi2<=90; phi2+=45) {
                if (phi1 != -phi2) {
                    transform = create(true, phi1, phi2);
                    tolerance = TOLERANCE;
                    validate();
                    assertFalse(isSpherical());
                    stress(CoordinateDomain.GEOGRAPHIC_RADIANS, 218639110);
                }
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
        for (int phi1=-90; phi1<=90; phi1+=45) {
            for (int phi2=-90; phi2<=90; phi2+=60) {
                if (phi1 != -phi2) {
                    transform = create(false, phi1, phi2);
                    tolerance = TOLERANCE;
                    validate();
                    assertTrue(isSpherical());
                    stress(CoordinateDomain.GEOGRAPHIC_RADIANS, 304204376);
                }
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
            transform = new AlbersEqualArea.Spherical(parameters);
            validate();
            assertTrue(isSpherical());
            stressLongitudeRolling(CoordinateDomain.GEOGRAPHIC);
        }
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

        // Test spherical
        transform = create(false, 10, 60);
        assertTrue(isSpherical());
        validate();
        verifyDerivative(toRadians(5), toRadians(30));

        // Test ellipsoidal
        transform = create(true, 10, 60);
        assertFalse(isSpherical());
        validate();
        verifyDerivative(toRadians(5), toRadians(30));
    }
}
