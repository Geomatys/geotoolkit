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
import static org.geotoolkit.referencing.operation.provider.AlbersEqualArea.PARAMETERS;


/**
 * Tests the {@link AlbersEqualArea} class. We test using various values of standard parallels.
 * We do not test with various values of the latitude of origin, because its only effect is to
 * modify the translation term on the <var>y</var> axis.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
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
        final Parameters parameters = parameters(wrap(PARAMETERS), ellipse, 2);
        parameters.parameter("standard_parallel_1").setValue(phi1);
        parameters.parameter("standard_parallel_2").setValue(phi2);
        if (ellipse) {
            return new AlbersEqualArea(new org.geotoolkit.referencing.operation.provider.AlbersEqualArea(), parameters);
        } else {
            return new AlbersEqualArea.Spherical(new org.geotoolkit.referencing.operation.provider.AlbersEqualArea(), parameters);
        }
    }

    /**
     * Tests the unitary projection on an ellipse.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testUnitaryOnEllipse() throws TransformException {
        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};
        for (int phi1=-90; phi1<=90; phi1+=45) {
            for (int phi2=-90; phi2<=90; phi2+=45) {
                if (phi1 != -phi2) {
                    transform = create(true, phi1, phi2);
                    tolerance = TOLERANCE;
                    validate();
                    assertFalse(isSpherical());
                    verifyInDomain(CoordinateDomain.GEOGRAPHIC_RADIANS, 218639110);
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
        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};
        for (int phi1=-90; phi1<=90; phi1+=45) {
            for (int phi2=-90; phi2<=90; phi2+=60) {
                if (phi1 != -phi2) {
                    transform = create(false, phi1, phi2);
                    tolerance = TOLERANCE;
                    validate();
                    assertTrue(isSpherical());
                    verifyInDomain(CoordinateDomain.GEOGRAPHIC_RADIANS, 304204376);
                }
            }
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
