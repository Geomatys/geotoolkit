/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing;

import java.awt.geom.AffineTransform;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.operation.Matrix;
import org.opengis.referencing.operation.TransformException;
import org.geotoolkit.test.referencing.WKT;
import org.geotoolkit.test.referencing.ReferencingTestBase;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.referencing.crs.DefaultCompoundCRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.operation.transform.AbstractMathTransform;
import org.junit.*;

import static org.geotoolkit.referencing.Assert.*;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import static java.util.Collections.singletonMap;


/**
 * Tests the {@link CRS} class. This is actually an indirect way to test many referencing
 * service (WKT parsing, object comparisons, <i>etc.</i>).
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Andrea Aime (OpenGeo)
 * @version 3.19
 *
 * @since 3.00
 */
public final strictfp class CRS_Test extends ReferencingTestBase {
    /**
     * Tests the extraction of components from a {@link CompoundCRS}.
     *
     * @throws FactoryException Should never happen.
     *
     * @since 3.16
     */
    @Test
    public void testComponentCRS() throws FactoryException {
        final VerticalCRS ELLIPSOIDAL_HEIGHT = CommonCRS.Vertical.ELLIPSOIDAL.crs();
        final TemporalCRS MODIFIED_JULIAN = CommonCRS.Temporal.MODIFIED_JULIAN.crs();

        final SingleCRS          crs2D = (SingleCRS) org.apache.sis.referencing.CRS.fromWKT(WKT.PROJCS_LAMBERT_CONIC_NTF);
        final DefaultCompoundCRS crs3D = new DefaultCompoundCRS(singletonMap(NAME_KEY, "NTF 3D"), crs2D, ELLIPSOIDAL_HEIGHT);
        final DefaultCompoundCRS crs4D = new DefaultCompoundCRS(singletonMap(NAME_KEY, "NTF 4D"), crs3D, MODIFIED_JULIAN);
        assertTrue (org.apache.sis.referencing.CRS.isHorizontalCRS(crs2D));
        assertFalse(org.apache.sis.referencing.CRS.isHorizontalCRS(crs3D));
        assertFalse(org.apache.sis.referencing.CRS.isHorizontalCRS(crs4D));
        assertSame(crs2D, org.apache.sis.referencing.CRS.getHorizontalComponent(crs2D));
        assertSame(crs2D, org.apache.sis.referencing.CRS.getHorizontalComponent(crs3D));
        assertSame(crs2D, org.apache.sis.referencing.CRS.getHorizontalComponent(crs4D));
        assertNull("No vertical component expected.",     org.apache.sis.referencing.CRS.getVerticalComponent(crs2D, true));
        assertSame(ELLIPSOIDAL_HEIGHT, org.apache.sis.referencing.CRS.getVerticalComponent(crs3D, true));
        assertSame(ELLIPSOIDAL_HEIGHT, org.apache.sis.referencing.CRS.getVerticalComponent(crs4D, true));
        assertNull("No temporal component expected.",     org.apache.sis.referencing.CRS.getTemporalComponent(crs2D));
        assertNull("No temporal component expected.",     org.apache.sis.referencing.CRS.getTemporalComponent(crs3D));
        assertSame(MODIFIED_JULIAN,    org.apache.sis.referencing.CRS.getTemporalComponent(crs4D));
        assertSame(crs3D, CRS.getCompoundCRS(crs3D, crs2D, ELLIPSOIDAL_HEIGHT));
        assertSame(crs3D, CRS.getCompoundCRS(crs4D, crs2D, ELLIPSOIDAL_HEIGHT));
        assertNull(       CRS.getCompoundCRS(crs3D, crs2D, MODIFIED_JULIAN));
        assertNull(       CRS.getCompoundCRS(crs4D, crs2D, MODIFIED_JULIAN));
        assertNull(       CRS.getCompoundCRS(crs3D, crs2D, ELLIPSOIDAL_HEIGHT, MODIFIED_JULIAN));
        assertSame(crs4D, CRS.getCompoundCRS(crs4D, crs2D, ELLIPSOIDAL_HEIGHT, MODIFIED_JULIAN));
        assertSame(crs4D, CRS.getCompoundCRS(crs4D, ELLIPSOIDAL_HEIGHT, MODIFIED_JULIAN, crs2D));
    }

    /**
     * Tests {@link CRS#deltaTransform}
     *
     * @throws TransformException Should never happen.
     */
    @Test
    public void testDeltaTransform() throws TransformException {
        /*
         * Computes the point to be used as a reference.
         */
        final AffineTransform at = new AffineTransform();
        at.translate(-200, 300);
        at.scale(4, 6);
        at.rotate(1.5);
        final double[] vector = new double[] {4, 7};
        final double[] expected = new double[2];
        at.deltaTransform(vector, 0, expected, 0, 1);
        /*
         * Computes the same delta using the CRS.deltaTransform(...) method. We need a custom
         * class that doesn't extends AffineTransform, otherwise CRS.deltaTransform would select
         * its optimized path which doesn't really test the code we want to test.
         */
        final class TestTransform extends AbstractMathTransform {
            @Override public int getSourceDimensions() {return 2;}
            @Override public int getTargetDimensions() {return 2;}
            @Override public Matrix transform(
                    final double[] srcPts, final int srcOff,
                    final double[] dstPts, final int dstOff,
                    final boolean derivate)
            {
                at.transform(srcPts, srcOff, dstPts, dstOff, 1);
                return null;
            }
        }
        final TestTransform tr = new TestTransform();
        final DirectPosition2D origin = new DirectPosition2D(80, -20);
        final double[] result = CRS.deltaTransform(tr, origin, vector);
        assertEquals(expected.length, result.length);
        for (int i=0; i<expected.length; i++) {
            assertEquals(expected[i], result[i], 1E-10);
        }
    }

    /**
     * Tests a few CRS from the IGNF authority.
     *
     * @throws FactoryException Should never happen.
     *
     * @since 3.14
     */
    @Test
    public void testIGNF() throws FactoryException {
        final CoordinateReferenceSystem crs = CRS.decode("IGNF:MILLER");
        assertTrue(crs instanceof ProjectedCRS);
    }
}
