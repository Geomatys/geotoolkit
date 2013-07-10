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

import org.junit.*;
import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;


import org.apache.sis.test.DependsOn;
import static java.lang.StrictMath.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.operation.provider.TransverseMercator.PARAMETERS;
import static org.geotoolkit.referencing.operation.projection.TransverseMercator.Parameters;


/**
 * Tests the {@link TransverseMercator} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.19
 *
 * @since 3.00
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class TransverseMercatorTest extends ProjectionTestBase {
    /**
     * Creates a default test suite.
     */
    public TransverseMercatorTest() {
        super(TransverseMercator.class, null);
    }

    /**
     * Returns a new instance of {@link TransverseMercator}.
     *
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @return Newly created projection.
     */
    private static TransverseMercator create(final boolean ellipse) {
        final TransverseMercator.Parameters parameters = parameters(PARAMETERS, ellipse, 0,
                TransverseMercator.Parameters.class);
        if (ellipse) {
            return new TransverseMercator(parameters);
        } else {
            return new TransverseMercator.Spherical(parameters);
        }
    }

    /**
     * Computes the difference between the ellipsoidal formulas and the spherical ones.
     * The spherical formulas are assumed exact.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testAssertions() throws TransformException {
        final ParameterValueGroup values = PARAMETERS.createValue();
        values.parameter("semi_major").setValue(SPHERE_RADIUS);
        values.parameter("semi_minor").setValue(SPHERE_RADIUS);
        final TransverseMercator.Spherical sphere =
                new TransverseMercator.Spherical(new Parameters(PARAMETERS, values));
        // Buffers to be recycled.
        final double[] source   = new double[2];
        final double[] expected = new double[2];
        final double[] actual   = new double[2];
        final double[] errors   = new double[180];
        for (int ivf=200; ivf<400; ivf+=10) {
            values.parameter("semi_minor").setValue(SPHERE_RADIUS * (1 - 1 / (double) ivf));
            final TransverseMercator projection = new TransverseMercator(new Parameters(PARAMETERS, values));
            for (int x=0; x<180; x++) {
                double error = 0;
                for (int y=-90; y<90; y+=5) {
                    source[0] = toRadians(x);
                    source[1] = toRadians(y);
                    sphere.transform(source, 0, expected, 0, 1);
                    projection.transform(source, 0, actual, 0, 1);
                    final double e = hypot(expected[0]-actual[0], expected[1]-actual[1]);
                    if (e > error) {
                        error = e;
                    }
                }
                if (error > errors[x]) {
                    errors[x] = error;
                }
            }
        }
        // Not doing anything useful with the errors array at this time.
        // The above test was mostly to ensure that no assertion failure occur.
    }

    /**
     * Creates a projection and derivates a few points.
     *
     * @throws TransformException Should never happen.
     *
     * @since 3.16
     */
    @Test
    public void testSphericalDerivative() throws TransformException {
        tolerance = 1E-9;
        transform = create(false);
        assertTrue(isSpherical());
        validate();

        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};
        verifyDerivative(toRadians( 0), toRadians( 0));
        verifyDerivative(toRadians(-3), toRadians(30));
        verifyDerivative(toRadians(+6), toRadians(60));
    }

    /**
     * Creates a projection and derivates a few points.
     *
     * @throws TransformException Should never happen.
     *
     * @since 3.19
     */
    @Test
    public void testEllipsoidalDerivative() throws TransformException {
        tolerance = 1E-9;
        transform = create(true);
        assertFalse(isSpherical());
        validate();

        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};
        verifyDerivative(toRadians( 0), toRadians( 0));
        verifyDerivative(toRadians(-3), toRadians(30));
        verifyDerivative(toRadians(+6), toRadians(60));
    }

    /**
     * Runs the test defined in the GeoAPI-conformance module.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void runGeoapiTest() throws FactoryException, TransformException {
        new GeoapiTest(mtFactory).testTransverseMercator();
    }
}
