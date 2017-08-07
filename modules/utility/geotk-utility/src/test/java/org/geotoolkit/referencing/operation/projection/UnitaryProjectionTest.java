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
import org.apache.sis.referencing.operation.transform.AbstractMathTransform1D;
import org.junit.*;

import static java.lang.StrictMath.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link UnitaryProjection} class. This class uses {@link Mercator}
 * for testing purpose, because it is the simplest non-trivial projection.
 *
 * @author Martin Desruisseaux (Geomatys)
 *
 * @since 3.00
 */
public final strictfp class UnitaryProjectionTest extends ProjectionTestBase {
    /**
     * Tolerance level for comparing floating point numbers.
     */
    private static final double TOLERANCE = 1E-12;

    /**
     * Natural logarithm of the pseudo-infinity as returned by Mercator formulas in the spherical
     * case, truncated to nearest integer. This is not a real infinity because there is no exact
     * representation of π/2 in base 2, so tan(π/2) is not positive infinity.
     */
    static final int LN_INFINITY = 37;

    /**
     * Creates a default test suite.
     */
    public UnitaryProjectionTest() {
        super(UnitaryProjection.class, null);
    }

    /**
     * Computes {@link UnitaryProjection#sinphi}.
     *
     * @param sinphi Sinus of the latitude <var>q</var> is calculated for.
     * @return <var>q</var> from Snyder equation (3-12).
     */
    private double qsfn(final double sinphi) {
        return ((UnitaryProjection) transform).qsfn(sinphi);
    }

    /**
     * Tests the {@link UnitaryProjection#qsfn} method.
     */
    @Test
    @Ignore
    public void testQsfn() {
        boolean ellipse = true;
        do {
//            transform = MercatorTest.create(ellipse);
            tolerance = TOLERANCE;
            for (int i=-100; i<=100; i++) {
                final double sinφ = i/100.0;
                final double q = qsfn(sinφ);
                assertEquals("Expected qsfn(-sinphi) == -qsfn(sinphi)", q, -qsfn(-sinφ), tolerance);
                assertEquals("Expected sinphi and qsfn(sinphi) to have same sign.", signum(sinφ), signum(q), 0);
            }
        } while ((ellipse = !ellipse) == false);
    }

    /**
     * Tests the {@link UnitaryProjection#dmsfn_dφ} method.
     *
     * @throws TransformException Should never happen.
     *
     * @since 3.19
     */
    @Test
    @Ignore
    public void testDmsfn() throws TransformException {
        boolean ellipse = false;
        do {
            final UnitaryProjection mercator = null;//MercatorTest.create(ellipse);
            transform = new AbstractMathTransform1D() {
                @Override public double transform(final double φ) {
                    return mercator.msfn(sin(φ), cos(φ));
                }
                @Override public double derivative(final double φ) {
                    final double sinφ = sin(φ);
                    final double cosφ = cos(φ);
                    final double msfn = mercator.msfn(sinφ, cosφ);
                    return mercator.dmsfn_dφ(sinφ, cosφ, msfn) * msfn;
                }
            };
            verifyInDomain(-PI/3, PI/3);
        } while ((ellipse = !ellipse) == true);
    }

    /**
     * Tests the {@link UnitaryProjection#dssfn_dφ} method.
     *
     * @throws TransformException Should never happen.
     *
     * @since 3.18
     */
    @Test
    @Ignore
    public void testDssfn() throws TransformException {
        boolean ellipse = false;
        do {
            final UnitaryProjection mercator = null;//MercatorTest.create(ellipse);
            transform = new AbstractMathTransform1D() {
                @Override public double transform(final double φ) {
                    return mercator.ssfn(φ, sin(φ));
                }
                @Override public double derivative(final double φ) {
                    final double sinφ = sin(φ);
                    return mercator.dssfn_dφ(φ, sinφ, cos(φ)) * mercator.ssfn(φ, sinφ);
                }
            };
            verifyInDomain(-PI/3, PI/3);
        } while ((ellipse = !ellipse) == true);
    }

    /**
     * Tests the {@link UnitaryProjection#dqsfn_dφ} method.
     *
     * @throws TransformException Should never happen.
     *
     * @since 3.18
     */
    @Test
    @Ignore
    public void testDqsfn() throws TransformException {
        boolean ellipse = false;
        do {
            final UnitaryProjection mercator = null;//MercatorTest.create(ellipse);
            transform = new AbstractMathTransform1D() {
                @Override public double transform (final double φ) {
                    return mercator.qsfn(sin(φ));
                }
                @Override public double derivative(final double φ) {
                    return mercator.dqsfn_dφ(sin(φ), cos(φ));
                }
            };
            verifyInDomain(-PI/3, PI/3);
        } while ((ellipse = !ellipse) == true);
    }

    /**
     * Convenience method invoking {@link TransformTestCase#verifyInDomain} for an 1D transform.
     *
     * @since 3.18
     */
    private void verifyInDomain(final double min, final double max) throws TransformException {
        isInverseTransformSupported = false;
        derivativeDeltas = new double[] {2E-8};
        tolerance = 1E-6;
        verifyInDomain(new double[] {min}, new double[] {max}, new int[] {100}, null);
    }
}
