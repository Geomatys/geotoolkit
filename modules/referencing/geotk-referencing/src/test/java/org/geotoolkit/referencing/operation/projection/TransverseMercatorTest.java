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

import java.awt.geom.Point2D;

import org.junit.*;
import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.TransformException;

import org.geotoolkit.test.Depend;

import static java.lang.Math.*;
import static org.geotoolkit.referencing.operation.provider.TransverseMercator.PARAMETERS;
import static org.geotoolkit.referencing.operation.projection.TransverseMercator.Parameters;


/**
 * Tests the {@link TransverseMercator} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.00
 */
@Depend(MercatorTest.class)
public class TransverseMercatorTest extends ProjectionTestCase {
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
                    source[0] = Math.toRadians(x);
                    source[1] = Math.toRadians(y);
                    sphere.transform(source, 0, expected, 0, 1);
                    projection.transform(source, 0, actual, 0, 1);
                    final double e = Math.hypot(expected[0]-actual[0], expected[1]-actual[1]);
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
     * Creates a projection using the provider and projects the
     * point given in the "example" section of EPSG documentation.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void testKnownPoint() throws FactoryException, TransformException {
        final ParameterValueGroup parameters = mtFactory.getDefaultParameters("Transverse Mercator");
        parameters.parameter("semi-major axis").setValue(6377563.396);
        parameters.parameter("semi-minor axis").setValue(6377563.396 * (1 - 1/299.32496));
        parameters.parameter("Latitude of natural origin").setValue(49.0);
        parameters.parameter("Longitude of natural origin").setValue(-2.0);
        parameters.parameter("Scale factor at natural origin").setValue(0.9996013);
        parameters.parameter("False easting").setValue(400000.00);
        parameters.parameter("False northing").setValue(-100000.00);
        transform = mtFactory.createParameterizedTransform(parameters);
        assertFalse(isSpherical());

        final double[] point    = new double[] {30.0/60, 50+30.0/60};
        final double[] expected = new double[] {577274.99, 69740.50};
        tolerance = 0.005;
        verifyTransform(point, expected);
    }

    /**
     * Creates a projection and derivates a few points.
     *
     * @throws TransformException Should never happen.
     *
     * @since 3.16
     */
    @Test
    public void testDerivative() throws TransformException {
        tolerance = 1E-9;
        transform = create(false);
        assertTrue(isSpherical());
        validate();

        final double delta = toRadians(1.0 / 60) / 1852; // Approximatively one metre.
        final Point2D.Double point = new Point2D.Double();
        checkDerivative2D(point, delta);
        point.x = toRadians(-3); point.y = toRadians(30); checkDerivative2D(point, delta);
        point.x = toRadians(+6); point.y = toRadians(60); checkDerivative2D(point, delta);
    }
}
