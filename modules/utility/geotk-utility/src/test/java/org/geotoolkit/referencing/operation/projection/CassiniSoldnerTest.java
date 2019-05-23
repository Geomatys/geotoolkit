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

import java.awt.geom.Point2D;
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
     * Tests some identities related to the {@link CassiniOrMercator#mlfn} method.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testSimplePoint() throws TransformException {
        final CassiniSoldner cassini = CassiniSoldnerTest.create(false);
        /*
         * Now fix φ=45°, which implies tan(φ)=1.
         * Test using the CassiniSoldner spherical equation.
         */
        final Point2D.Double point = new Point2D.Double();
        final double domain = toRadians(5);
        final double step   = domain/100;
        for (double λ=-domain; λ<=domain; λ+=step) {
            final double yFromSimplified = PI/2 - atan(cos(λ));
            point.x = λ;
            point.y = PI/4;
            assertSame(point, cassini.transform(point, point));
            assertEquals("Given excentricity=0 and φ=45°, the spherical equation should simplify to a "
                    + "very simple expression, which we are testing here.", yFromSimplified, point.y, 1E-9);
            /*
             * In the equation below, PI/4 is actually mlfn(φ) where the excentricity=0 and φ=45°.
             * In our simplified case, the equation using the ellipsoidal formula seems to be an
             * approximation of PI/2 - atan(cos(λ)). This looks like a sine function (but is not
             * exactly a sine function).
             */
            final double λ2 = λ*λ;
            final double yEllps = PI/4 + λ2*(0.25 + λ2*0.41666666666666666666); // CassiniSoldner.C3
            assertEquals("Approximation of PI/2 - atan(cos(λ)", yFromSimplified, yEllps, 3E-5);
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
    @Ignore
    public void runGeoapiTest() throws FactoryException, TransformException {
        new GeoapiTest(mtFactory).testCassiniSoldner();
    }
}
