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

import java.awt.geom.Point2D;

import org.junit.*;
import org.apache.sis.test.DependsOn;

import org.apache.sis.referencing.operation.transform.AbstractMathTransform1D;
import static java.lang.StrictMath.*;
import static org.junit.Assert.*;
import org.opengis.referencing.operation.TransformException;


/**
 * Tests the {@link CassiniOrMercator} class. This class uses {@link CassiniSoldner}
 * for testing purpose.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Rémi Maréchal (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 */
@DependsOn(UnitaryProjectionTest.class)
public final strictfp class CassiniOrMercatorTest extends ProjectionTestBase {
    /**
     * Creates a default test suite.
     */
    public CassiniOrMercatorTest() {
        super(CassiniOrMercator.class, null);
    }

    /**
     * Tests some identities related to the {@link CassiniOrMercator#mlfn} method.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testDmlfn() throws TransformException {
        final CassiniSoldner cassini = CassiniSoldnerTest.create(false);
        assertEquals("Expected spherical projection.", 0, cassini.excentricity, 0);
        for (double φ = -PI/3; φ <= PI/3; φ += 0.01) {
            assertEquals("When excentricity=0, mlfn(φ, sinφ, cosφ) simplify to φ.",
                    φ, cassini.mlfn(φ, sin(φ), cos(φ)), 1E-9);
        }
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
     * Tests the {@link CassiniOrMercator#dmlfn_dφ} method.
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testDmlfn_dφ() throws TransformException {
        isInverseTransformSupported = false;
        derivativeDeltas = new double[] {2E-8};
        tolerance = 1E-7;
        boolean ellipse = false;
        do {
            final CassiniOrMercator cassini = CassiniSoldnerTest.create(ellipse);
            transform = new AbstractMathTransform1D() {
                @Override public double transform (final double φ) {
                    return cassini.mlfn(φ, sin(φ), cos(φ));
                }
                @Override public double derivative(final double φ) {
                    final double sinφ = sin(φ);
                    final double cosφ = cos(φ);
                    return cassini.dmlfn_dφ(sinφ*sinφ, cosφ*cosφ);
                }
            };
            verifyInDomain(new double[] {-PI/3}, new double[] {PI/3}, new int[] {100}, null);
        } while ((ellipse = !ellipse) == true);
    }
}
