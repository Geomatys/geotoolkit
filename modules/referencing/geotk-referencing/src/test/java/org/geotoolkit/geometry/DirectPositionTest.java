/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry;

import java.util.Arrays;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link GeneralDirectPosition} and {@link DirectPosition2D} classes.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 2.4
 */
public final strictfp class DirectPositionTest {
    /**
     * Tests the {@link AbstractDirectPosition#toString(DirectPosition)} method.
     */
    @Test
    public void testWktFormatting() {
        assertEquals("POINT(6 10 2)", new GeneralDirectPosition(6, 10, 2).toString());
        assertEquals("POINT(6.5 10)", new DirectPosition2D(6.5, 10).toString());
    }

    /**
     * Tests the {@link GeneralDirectPosition#GeneralDirectPosition(String)} constructor.
     *
     * @since 3.09
     */
    @Test
    public void testWktParsing() {
        assertEquals("POINT(6 10 2)", new GeneralDirectPosition("POINT(6 10 2)").toString());
        assertEquals("POINT(3 14 2)", new GeneralDirectPosition("POINT M [ 3 14 2 ] ").toString());
        assertEquals("POINT(2 10 8)", new GeneralDirectPosition("POINT Z 2 10 8").toString());
        assertEquals("POINT()",       new GeneralDirectPosition("POINT()").toString());
        assertEquals("POINT()",       new GeneralDirectPosition("POINT ( ) ").toString());
        assertEquals("POINT()",       new GeneralDirectPosition("POINT").toString());
        assertEquals("POINT(6 10)",   new DirectPosition2D("POINT(6 10)").toString());
        assertEquals("POINT(8)",      new DirectPosition1D("POINT(8)").toString());

        try {
            new GeneralDirectPosition("POINT(6 10 2");
            fail("Parsing should fails because of missing parenthesis.");
        } catch (IllegalArgumentException e) {
            // This is the expected exception.
            assertTrue(e.getMessage().contains("POINT"));
        }
        try {
            new GeneralDirectPosition("POINT 6 10 2)");
            fail("Parsing should fails because of missing parenthesis.");
        } catch (NumberFormatException e) {
            // This is the expected exception.
        }
        try {
            new GeneralDirectPosition("POINT(6 10 2) x");
            fail("Parsing should fails because of extra characters.");
        } catch (IllegalArgumentException e) {
            // This is the expected exception.
            assertTrue(e.getMessage().contains("POINT"));
        }
    }

    /**
     * Tests {@link GeneralDirectPosition#equals} method between different implementations. The
     * purpose of this test is also to run the assertion in the direct position implementations.
     */
    @Test
    public void testEquals() {
        assertTrue(GeneralDirectPosition.class.desiredAssertionStatus());
        assertTrue(DirectPosition2D.class.desiredAssertionStatus());

        CoordinateReferenceSystem WGS84 = DefaultGeographicCRS.WGS84;
        DirectPosition p1 = new DirectPosition2D(WGS84, 48.543261561072285, -123.47009555832284);
        GeneralDirectPosition p2 = new GeneralDirectPosition(48.543261561072285, -123.47009555832284);
        assertFalse(p1.equals(p2));
        assertFalse(p2.equals(p1));

        p2.setCoordinateReferenceSystem(WGS84);
        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));
    }

    /**
     * Tests {@link GeneralDirectPosition#clone()}.
     *
     * @since 3.16
     */
    @Test
    public void testClone() {
        final GeneralDirectPosition p1 = new GeneralDirectPosition(10, 20, 30);
        p1.setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84_3D);
        final GeneralDirectPosition p2 = p1.clone();
        assertEquals ("Expected the same CRS and ordinates.", p1, p2);
        assertTrue   ("Expected the same ordinates.", Arrays.equals(p1.ordinates, p2.ordinates));
        assertNotSame("the ordinates array should have been cloned.", p1.ordinates, p2.ordinates);
    }
}
