/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
import static java.lang.StrictMath.*;
import static org.junit.Assert.*;
import static org.geotoolkit.referencing.operation.provider.Polyconic.PARAMETERS;


/**
 * Tests the {@link Polyconic} class.
 *
 * @author Simon Reynard (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.19
 *
 * @since 3.11
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class PolyconicTest extends ProjectionTestBase {
    /**
     * Creates a default test suite.
     */
    public PolyconicTest() {
        super(Polyconic.class, null);
    }

    /**
     * Returns a new instance of {@link Polyconic}.
     *
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @return Newly created projection.
     */
    private static Polyconic create(final boolean ellipse, final double latitudeOfOrigin) {
        final UnitaryProjection.Parameters parameters = parameters(PARAMETERS, ellipse);
        parameters.latitudeOfOrigin = latitudeOfOrigin;
        if (ellipse) {
            return new Polyconic(parameters);
        } else {
            return new Polyconic.Spherical(parameters);
        }
    }

    /**
     * Creates a projection and tests the derivatives at a few points.
     *
     * @throws TransformException Should never happen.
     *
     * @since 3.19
     */
    @Test
    public void testDerivative() throws TransformException {
        tolerance = 1E-9;
        transform = create(false, 0);
        assertTrue(isSpherical());
        validate();

        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};
        verifyDerivative(toRadians(5), toRadians( 3));
        verifyDerivative(toRadians(0), toRadians(50));
        verifyDerivative(toRadians(3), toRadians(47));

        transform = create(true, 0);
        assertFalse(isSpherical());
        validate();
        verifyDerivative(toRadians(5), toRadians( 3));
        verifyDerivative(toRadians(0), toRadians(50));
        verifyDerivative(toRadians(3), toRadians(47));
    }

    /**
     * Runs the test defined in the GeoAPI-conformance module.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void runGeoapiTest() throws FactoryException, TransformException {
        new GeoapiTest(mtFactory).testPolyconic();
    }
}
