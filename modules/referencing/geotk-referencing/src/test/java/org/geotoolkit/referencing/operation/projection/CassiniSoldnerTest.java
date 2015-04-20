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
import org.apache.sis.parameter.Parameters;
import org.apache.sis.test.DependsOn;
import org.junit.*;

import static org.junit.Assert.*;
import static java.lang.StrictMath.*;
import static org.geotoolkit.referencing.operation.provider.CassiniSoldner.PARAMETERS;


/**
 * Tests the {@link CassiniSoldner} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 *
 * @since 3.00
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class CassiniSoldnerTest extends ProjectionTestBase {
    /**
     * Creates a default test suite.
     */
    public CassiniSoldnerTest() {
        super(CassiniSoldner.class, null);
    }

    /**
     * Returns a new instance of {@link CassiniSoldner}.
     *
     * @param  ellipse {@code false} for a sphere, or {@code true} for WGS84 ellipsoid.
     * @return Newly created projection.
     */
    static CassiniSoldner create(final boolean ellipse) {
        final Parameters parameters = parameters(wrap(PARAMETERS), ellipse, 0);
        if (ellipse) {
            return new CassiniSoldner(new org.geotoolkit.referencing.operation.provider.CassiniSoldner(), parameters);
        } else {
            return new CassiniSoldner.Spherical(new org.geotoolkit.referencing.operation.provider.CassiniSoldner(), parameters);
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
        final double delta = toRadians(100.0 / 60) / 1852; // Approximatively 100 metres.
        derivativeDeltas = new double[] {delta, delta};

        // Tests spherical formulas
        tolerance = 1E-9;
        transform = create(false);
        assertTrue(isSpherical());
        validate();
        verifyDerivative(toRadians(+3), toRadians(-6));
        verifyDerivative(toRadians(-4), toRadians(40));

        // Tests ellipsoidal formulas
        tolerance = 1E-8;
        transform = create(true);
        assertFalse(isSpherical());
        validate();
        verifyDerivative(toRadians(+3), toRadians(-10));
        verifyDerivative(toRadians(-4), toRadians(+10));
    }

    /**
     * Runs the test defined in the GeoAPI-conformance module.
     *
     * @throws FactoryException   Should never happen.
     * @throws TransformException Should never happen.
     */
    @Test
    public void runGeoapiTest() throws FactoryException, TransformException {
        new GeoapiTest(mtFactory).testCassiniSoldner();
    }
}
