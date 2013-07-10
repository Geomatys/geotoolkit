/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;

import org.apache.sis.test.DependsOn;
import static org.junit.Assert.*;
import static java.lang.StrictMath.*;
import static org.geotoolkit.referencing.operation.provider.Stereographic.PARAMETERS;
import static org.geotoolkit.referencing.operation.projection.Stereographic.Parameters;


/**
 * Tests the {@link Stereographic} class.
 *
 * @author Rémi Maréchal (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class StereographicTest extends ProjectionTestBase {
    /**
     * Creates a default test suite.
     */
    public StereographicTest() {
        super(Stereographic.class, null);
    }

    /**
     * Returns a new instance of {@link Stereographic}.
     *
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @return Newly created projection.
     */
    private static Stereographic create(final boolean ellipse) {
        final UnitaryProjection.Parameters parameters = parameters(PARAMETERS, ellipse);
        if (ellipse) {
            return new Stereographic(parameters);
        } else {
            return new Stereographic.Spherical(parameters);
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
        final Stereographic.Spherical sphere =
                new Stereographic.Spherical(new Parameters(PARAMETERS, values));
        // Buffers to be recycled.
        final double[] source   = new double[2];
        final double[] expected = new double[2];
        final double[] actual   = new double[2];
        final double[] errors   = new double[180];

        for (int ivf=200; ivf<400; ivf+=10) {
            values.parameter("semi_minor").setValue(SPHERE_RADIUS * (1 - 1 / (double) ivf));
            final Stereographic projection = new Stereographic(new Parameters(PARAMETERS, values));
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
     */
    @Test
    public void testSphericalDerivative() throws TransformException {
        tolerance = 1E-9;
        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.

        //test derivée sphérique
        transform = create(false);
        assertTrue(isSpherical());
        derivativeDeltas = new double[] {delta, delta};
        verifyDerivative(toRadians( 0), toRadians( 0));
        verifyDerivative(toRadians(-3), toRadians(30));
        verifyDerivative(toRadians(+6), toRadians(60));
    }

    /**
     * Creates a projection and derivates a few points.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testEllipsoidalDerivative() throws TransformException {
        tolerance = 1E-9;
        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.

        //test derivée ellipsoidalle
        transform = create(true);
        derivativeDeltas = new double[] {delta, delta};
        verifyDerivative(toRadians( 0), toRadians( 0));
        verifyDerivative(toRadians(-3), toRadians(30));
        verifyDerivative(toRadians(+6), toRadians(60));
    }
}