/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import org.opengis.geometry.DirectPosition;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.junit.*;
import static org.junit.Assert.*;
import org.geotoolkit.test.Depend;


/**
 * Tests the {@link GeneralEnvelope} class.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.16
 *
 * @since 2.4
 */
@Depend(DirectPositionTest.class)
public final class GeneralEnvelopeTest {
    /**
     * Tests the {@link GeneralEnvelope#setSubEnvelope(Envelope, int)} method.
     *
     * @since 3.16
     */
    @Test
    public void testSetSubEnvelope() {
        final GeneralEnvelope horizontal = new GeneralEnvelope(new double[] {-180, -90}, new double[] {180, 90});
        final GeneralEnvelope vertical   = new GeneralEnvelope(20, 40);
        final GeneralEnvelope envelope   = new GeneralEnvelope(3);
        assertTrue (envelope.isEmpty());
        assertFalse(vertical.isEmpty());
        assertFalse(horizontal.isEmpty());
        envelope.setSubEnvelope(horizontal, 0);
        envelope.setSubEnvelope(vertical,   2);
        assertFalse(envelope.isEmpty());
        assertEquals(-180, envelope.getMinimum(0), 0);
        assertEquals( 180, envelope.getMaximum(0), 0);
        assertEquals( -90, envelope.getMinimum(1), 0);
        assertEquals(  90, envelope.getMaximum(1), 0);
        assertEquals(  20, envelope.getMinimum(2), 0);
        assertEquals(  40, envelope.getMaximum(2), 0);
    }

    /**
     * Tests the {@link AbstractEnvelope#toString(Envelope)} method.
     *
     * @since 3.09
     */
    @Test
    public void testWktFormatting() {
        Envelope2D envelope2D = new Envelope2D(null, -180, -90, 360, 180);
        assertEquals("BOX2D(-180 -90, 180 90)", envelope2D.toString());
        assertEquals("POLYGON((-180 -90, -180 90, 180 90, 180 -90, -180 -90))",
                AbstractEnvelope.toPolygonString(envelope2D));

        GeneralEnvelope envelope = new GeneralEnvelope(3);
        envelope.setRange(0, -180, +180);
        envelope.setRange(1,  -90,  +90);
        envelope.setRange(2,   10,   30);
        assertEquals("BOX3D(-180 -90 10, 180 90 30)", envelope.toString());
    }

    /**
     * Tests the {@link GeneralEnvelope#GeneralEnvelope(String)} constructor.
     *
     * @since 3.09
     */
    @Test
    public void testWktParsing() {
        GeneralEnvelope envelope = new GeneralEnvelope("BOX(-180 -90,180 90)");
        assertEquals(2, envelope.getDimension());
        assertEquals(-180, envelope.getMinimum(0), 0);
        assertEquals( 180, envelope.getMaximum(0), 0);
        assertEquals( -90, envelope.getMinimum(1), 0);
        assertEquals(  90, envelope.getMaximum(1), 0);

        envelope = new GeneralEnvelope("BOX3D(-180 -90 10, 180 90 30)");
        assertEquals(3, envelope.getDimension());
        assertEquals(-180, envelope.getMinimum(0), 0);
        assertEquals( 180, envelope.getMaximum(0), 0);
        assertEquals( -90, envelope.getMinimum(1), 0);
        assertEquals(  90, envelope.getMaximum(1), 0);
        assertEquals(  10, envelope.getMinimum(2), 0);
        assertEquals(  30, envelope.getMaximum(2), 0);

        envelope = new GeneralEnvelope("POLYGON((-80 -30,-100 40,80 40,100 -40,-80 -30))");
        assertEquals(-100, envelope.getMinimum(0), 0);
        assertEquals( 100, envelope.getMaximum(0), 0);
        assertEquals( -40, envelope.getMinimum(1), 0);
        assertEquals(  40, envelope.getMaximum(1), 0);

        assertEquals("BOX2D(6 10, 6 10)",     new GeneralEnvelope("POINT(6 10)").toString());
        assertEquals("BOX3D(6 10 3, 6 10 3)", new GeneralEnvelope("POINT M [ 6 10 3 ] ").toString());
        assertEquals("BOX2D(3 4, 20 50)",     new GeneralEnvelope("LINESTRING(3 4,10 50,20 25)").toString());
        assertEquals("BOX2D(1 1, 6 5)",       new GeneralEnvelope(
                "MULTIPOLYGON(((1 1,5 1,5 5,1 5,1 1),(2 2, 3 2, 3 3, 2 3,2 2)),((3 3,6 2,6 4,3 3)))").toString());
        assertEquals("BOX2D(3 6, 7 10)", new GeneralEnvelope("GEOMETRYCOLLECTION(POINT(4 6),LINESTRING(3 8,7 10))").toString());
        assertEquals(0, new GeneralEnvelope("BOX()").getDimension());

        try {
            new GeneralEnvelope("BOX2D(3 4");
            fail("Parsing should fails because of missing parenthesis.");
        } catch (IllegalArgumentException e) {
            // This is the expected exception.
            assertTrue(e.getMessage().contains("BOX2D"));
        }
        try {
            new GeneralEnvelope("LINESTRING(3 4,10 50),20 25)");
            fail("Parsing should fails because of missing parenthesis.");
        } catch (IllegalArgumentException e) {
            // This is the expected exception.
            assertTrue(e.getMessage().contains("LINESTRING"));
        }
    }

    /**
     * Tests the {@link GeneralEnvelope#equals} method.
     */
    @Test
    public void testEquals() {
        /*
         * Initializes an empty envelope. The new envelope is empty
         * but not null because initialized to 0, not NaN.
         */
        final GeneralEnvelope e1 = new GeneralEnvelope(4);
        assertTrue  (e1.isEmpty());
        assertFalse (e1.isNull());
        assertEquals(e1.getLowerCorner(), e1.getUpperCorner());
        /*
         * Initializes with arbitrary coordinate values.
         * Should not be empty anymore.
         */
        for (int i=e1.getDimension(); --i>=0;) {
            e1.setRange(i, i*5 + 2, i*6 + 5);
        }
        assertFalse(e1.isNull ());
        assertFalse(e1.isEmpty());
        assertFalse(e1.getLowerCorner().equals(e1.getUpperCorner()));
        /*
         * Creates a new envelope initialized with the same
         * coordinate values. The two envelope should be equals.
         */
        final GeneralEnvelope e2 = new GeneralEnvelope(e1);
        assertPositionEquals(e1.getLowerCorner(), e2.getLowerCorner());
        assertPositionEquals(e1.getUpperCorner(), e2.getUpperCorner());
        assertTrue   (e1.contains(e2, true ));
        assertFalse  (e1.contains(e2, false));
        assertNotSame(e1, e2);
        assertEquals (e1, e2);
        assertTrue   (e1.equals(e2, 1E-4, true ));
        assertTrue   (e1.equals(e2, 1E-4, false));
        assertEquals (e1.hashCode(), e2.hashCode());
        /*
         * Offset slightly one coordinate value. Should not be equals anymore,
         * except when comparing with a tolerance value.
         */
        e2.setRange(2, e2.getMinimum(2) + 3E-5, e2.getMaximum(2) - 3E-5);
        assertTrue (e1.contains(e2, true ));
        assertFalse(e1.contains(e2, false));
        assertFalse(e1.equals  (e2));
        assertTrue (e1.equals  (e2, 1E-4, true ));
        assertTrue (e1.equals  (e2, 1E-4, false));
        assertFalse(e1.hashCode() == e2.hashCode());
        /*
         * Applies a greater offset. Should not be equal,
         * even when comparing with a tolerance value.
         */
        e2.setRange(1, e2.getMinimum(1) + 3, e2.getMaximum(1) - 3);
        assertTrue (e1.contains(e2, true ));
        assertFalse(e1.contains(e2, false));
        assertFalse(e1.equals  (e2));
        assertFalse(e1.equals  (e2, 1E-4, true ));
        assertFalse(e1.equals  (e2, 1E-4, false));
        assertFalse(e1.hashCode() == e2.hashCode());
    }

    /**
     * Compares the specified corners.
     */
    private static void assertPositionEquals(final DirectPosition p1, final DirectPosition p2) {
        assertNotSame(p1, p2);
        assertEquals (p1, p2);
        assertEquals (p1.hashCode(), p2.hashCode());
    }

    /**
     * Tests the {@link GeneralEnvelope#clone()} method.
     *
     * @since 3.16
     */
    @Test
    public void testClone() {
        final GeneralEnvelope e1 = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        e1.setRange(0, -40, +60);
        e1.setRange(1, -20, +30);
        final GeneralEnvelope e2 = e1.clone();
        assertNotSame("Expected a new instance.",           e1, e2);
        assertEquals ("The two instances should be equal.", e1, e2);
        e1.setRange(0, -40, +61);
        assertFalse("Ordinates array should have been cloned.", e1.equals(e2));
        e2.setRange(0, -40, +61);
        assertEquals(e1, e2);
        assertSame(DefaultGeographicCRS.WGS84, e2.getCoordinateReferenceSystem());
    }
}
