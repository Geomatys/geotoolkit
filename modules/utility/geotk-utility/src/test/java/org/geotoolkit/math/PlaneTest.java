/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 1998-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.math;

import java.util.Random;
import javax.vecmath.Point3d;

import org.junit.*;
import static org.apache.sis.test.Assert.*;
import static java.lang.StrictMath.*;


/**
 * Tests the {@link Plane} class.
 *
 * @author Martin Desruisseaux (MPO, IRD)
 * @version 3.00
 *
 * @since 2.0
 */
public final strictfp class PlaneTest {
    /**
     * Tolerance factor for comparisons.
     */
    private static final double EPS = 1E-8;

    /**
     * Tests {@link Plane#fit} methods.
     */
    @Test
    public void testFit() {
        final Random  rd = new Random(457821698762354L);
        final Plane plan = new Plane();
        final Point3d P1 = new Point3d(100*rd.nextDouble()+25, 100*rd.nextDouble()+25, rint(100*rd.nextDouble()+40));
        final Point3d P2 = new Point3d(100*rd.nextDouble()+25, 100*rd.nextDouble()+25, rint(100*rd.nextDouble()+40));
        final Point3d P3 = new Point3d(100*rd.nextDouble()+25, 100*rd.nextDouble()+25, rint(100*rd.nextDouble()+40));
        plan.setPlane(P1, P2, P3);
        assertEquals("P1", P1.z, plan.z(P1.x,P1.y), EPS);
        assertEquals("P2", P2.z, plan.z(P2.x,P2.y), EPS);
        assertEquals("P3", P3.z, plan.z(P3.x,P3.y), EPS);

        final double[] x = new double[4000];
        final double[] y = new double[4000];
        final double[] z = new double[4000];
        for (int i=0; i<z.length; i++) {
            x[i] = 40 + 100*rd.nextDouble();
            y[i] = 40 + 100*rd.nextDouble();
            z[i] = plan.z(x[i], y[i]) + 10*rd.nextDouble()-5;
        }
        final Plane copy = plan.clone();
        final double eps = 1E-2; // We do expect some difference, but not much more than that.
        assertEquals("c",  copy.c,  plan.c,  eps);
        assertEquals("cx", copy.cx, plan.cx, eps);
        assertEquals("cy", copy.cy, plan.cy, eps);
    }

    /**
     * Tests serialization.
     */
    @Test
    public void testSerialization() {
        final Plane local = new Plane();
        local.c  =  3.7;
        local.cx =  9.3;
        local.cy = -1.8;
        assertNotSame(local, assertSerializedEquals(local));
    }
}
