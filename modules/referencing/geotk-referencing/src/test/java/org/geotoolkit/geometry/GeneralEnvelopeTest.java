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

import org.opengis.geometry.Envelope;
import org.opengis.geometry.DirectPosition;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.junit.*;
import org.geotoolkit.test.Depend;

import static java.lang.Double.NaN;
import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.WGS84;


/**
 * Tests the {@link GeneralEnvelope} class. The {@link Envelope2D} class will also be tested as a
 * side effect, because it is used for comparison purpose. Note that {@link AbstractEnvelopeTest}
 * already tested {@code contains} and {@code intersects} methods, so this test file will focus on
 * other methods.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.4
 */
@Depend(AbstractEnvelopeTest.class)
public final strictfp class GeneralEnvelopeTest {
    /**
     * The comparison threshold for strict comparisons.
     */
    private static final double STRICT = 0;

    /**
     * Tolerance threshold for floating point comparisons.
     */
    private static final double EPS = 1E-4;

    /**
     * Creates a new geographic envelope for the given ordinate values.
     */
    private static GeneralEnvelope create(final double xmin, final double ymin, final double xmax, final double ymax) {
        final GeneralEnvelope envelope = new GeneralEnvelope(WGS84);
        envelope.setEnvelope(xmin, ymin, xmax, ymax);
        return envelope;
    }

    /**
     * Asserts that the the given two-dimensional envelope is equals to the given rectangle.
     */
    private static void assertEnvelopeEquals(final Envelope e,
            final double xmin, final double ymin, final double xmax, final double ymax)
    {
        assertEquals("xmin", xmin, e.getMinimum(0), STRICT);
        assertEquals("xmax", xmax, e.getMaximum(0), STRICT);
        assertEquals("ymin", ymin, e.getMinimum(1), STRICT);
        assertEquals("ymax", ymax, e.getMaximum(1), STRICT);
    }

    /**
     * Asserts that the intersection of the two following envelopes is equals to the given rectangle.
     * First, this method tests using the {@link Envelope2D} implementation. Then, it tests using the
     * {@link GeneralEnvelope} implementation.
     */
    private static void assertIntersectEquals(final GeneralEnvelope e1, final GeneralEnvelope e2,
            final double xmin, final double ymin, final double xmax, final double ymax)
    {
        final boolean isEmpty = !(((xmax - xmin) * (ymax - ymin)) != 0); // Use ! for catching NaN.
        final Envelope2D r1 = new Envelope2D(e1);
        final Envelope2D r2 = new Envelope2D(e2);
        final Envelope2D ri = r1.createIntersection(r2);
        assertEquals("isEmpty", isEmpty, r1.isEmpty());
        assertEquals("xmin", xmin, ri.getMinX(), STRICT);
        assertEquals("xmax", xmax, ri.getMaxX(), STRICT);
        assertEquals("ymin", ymin, ri.getMinY(), STRICT);
        assertEquals("ymax", ymax, ri.getMaxY(), STRICT);
        assertEnvelopeEquals(ri, xmin, ymin, xmax, ymax);
        assertEquals("Interchanged arguments.", ri, r2.createIntersection(r1));

        // Compares with GeneralEnvelope.
        final GeneralEnvelope ei = new GeneralEnvelope(e1);
        ei.intersect(e2);
        assertEquals("isEmpty", isEmpty, e1.isEmpty());
        assertEnvelopeEquals(ei, xmin, ymin, xmax, ymax);
        assertTrue("Using GeneralEnvelope.", ei.equals(ri, STRICT, false));

        // Interchanges arguments.
        ei.setEnvelope(e2);
        ei.intersect(e1);
        assertEquals("isEmpty", isEmpty, e1.isEmpty());
        assertEnvelopeEquals(ei, xmin, ymin, xmax, ymax);
        assertTrue("Using GeneralEnvelope.", ei.equals(ri, STRICT, false));
    }

    /**
     * Asserts that the union of the two following envelopes is equals to the given rectangle.
     * First, this method tests using the {@link Envelope2D} implementation. Then, it tests
     * using the {@link GeneralEnvelope} implementation.
     *
     * @param inf {@code true} if the range after union is infinite. The handling of such case
     *        is different for {@link GeneralEnvelope} than for {@link Envelope2D} because we
     *        can not store infinite values in a reliable way in a {@link Rectangle2D} object,
     *        so we use NaN instead.
     * @param exactlyOneAntiMeridianSpan {@code true} if one envelope spans the anti-meridian
     *        and the other does not.
     */
    private static void assertUnionEquals(final GeneralEnvelope e1, final GeneralEnvelope e2,
            final double xmin, final double ymin, final double xmax, final double ymax,
            final boolean inf, final boolean exactlyOneAntiMeridianSpan)
    {
        final Envelope2D r1 = new Envelope2D(e1);
        final Envelope2D r2 = new Envelope2D(e2);
        final Envelope2D ri = r1.createUnion(r2);
        assertEquals("xmin", inf ? NaN : xmin, ri.getMinX(), STRICT);
        assertEquals("xmax", inf ? NaN : xmax, ri.getMaxX(), STRICT);
        assertEquals("ymin",             ymin, ri.getMinY(), STRICT);
        assertEquals("ymax",             ymax, ri.getMaxY(), STRICT);
        assertEnvelopeEquals(ri, inf ? NaN : xmin, ymin, inf ? NaN : xmax, ymax);
        assertEquals("Interchanged arguments.", ri, r2.createUnion(r1));

        // Compares with GeneralEnvelope.
        final GeneralEnvelope ei = new GeneralEnvelope(e1);
        ei.add(e2);
        assertEnvelopeEquals(ei, xmin, ymin, xmax, ymax);
        if (!inf) {
            assertTrue("Using GeneralEnvelope.", ei.equals(ri, STRICT, false));
        }

        // Interchanges arguments.
        ei.setEnvelope(e2);
        ei.add(e1);
        if (inf && exactlyOneAntiMeridianSpan) {
            assertEnvelopeEquals(ei, Double.NEGATIVE_INFINITY, ymin, Double.POSITIVE_INFINITY, ymax);
        } else {
            assertEnvelopeEquals(ei, xmin, ymin, xmax, ymax);
        }
        if (!inf) {
            assertTrue("Using GeneralEnvelope.", ei.equals(ri, STRICT, false));
        }
    }

    /**
     * Asserts that adding the given point to the given envelope produces the given result.
     * First, this method tests using the {@link Envelope2D} implementation. Then, it tests
     * using the {@link GeneralEnvelope} implementation.
     */
    private static void assertAddEquals(final GeneralEnvelope e, final DirectPosition2D p,
            final double xmin, final double ymin, final double xmax, final double ymax)
    {
        final Envelope2D r = new Envelope2D(e);
        r.add(p);
        assertEquals("xmin", xmin, r.getMinX(), STRICT);
        assertEquals("xmax", xmax, r.getMaxX(), STRICT);
        assertEquals("ymin", ymin, r.getMinY(), STRICT);
        assertEquals("ymax", ymax, r.getMaxY(), STRICT);
        assertEnvelopeEquals(r, xmin, ymin, xmax, ymax);

        // Compares with GeneralEnvelope.
        final GeneralEnvelope ec = new GeneralEnvelope(e);
        ec.add(p);
        assertEnvelopeEquals(ec, xmin, ymin, xmax, ymax);
        assertTrue("Using GeneralEnvelope.", ec.equals(r, STRICT, false));
    }

    /**
     * Tests the {@link GeneralEnvelope#intersect(Envelope)} and
     * {@link Envelope2D#createIntersection(Rectangle2D)} methods.
     *
     * @since 3.20
     */
    @Test
    public void testIntersection() {
        //  ┌─────────────┐
        //  │  ┌───────┐  │
        //  │  └───────┘  │
        //  └─────────────┘
        final GeneralEnvelope e1 = create(20, -20, 80, 10);
        final GeneralEnvelope e2 = create(40, -10, 62,  8);
        assertIntersectEquals(e1, e2, 40, -10, 62, 8);
        //  ┌──────────┐
        //  │  ┌───────┼──┐
        //  │  └───────┼──┘
        //  └──────────┘
        e1.setEnvelope(20, -20,  80, 12);
        e2.setEnvelope(40, -10, 100, 30);
        final double ymin=-10, ymax=12; // Will not change anymore
        assertIntersectEquals(e1, e2, 40, ymin, 80, ymax);
        //  ────┐  ┌────
        //  ──┐ │  │ ┌──
        //  ──┘ │  │ └──
        //  ────┘  └────
        e1.setRange(0,  80, 20);
        e2.setRange(0, 100, 18);
        assertIntersectEquals(e1, e2, 100, ymin, 18, ymax);
        //  ────┐  ┌────
        //  ────┼──┼─┐┌─
        //  ────┼──┼─┘└─
        //  ────┘  └────
        e2.setRange(0, 100, 90);
        assertIntersectEquals(e1, e2, 100, ymin, 20, ymax);
        //  ─────┐      ┌─────
        //     ┌─┼────┐ │
        //     └─┼────┘ │
        //  ─────┘      └─────
        e2.setRange(0, 10, 30);
        assertIntersectEquals(e1, e2, 10, ymin, 20, ymax);
        //  ──────────┐  ┌─────
        //    ┌────┐  │  │
        //    └────┘  │  │
        //  ──────────┘  └─────
        e2.setRange(0, 10, 16);
        assertIntersectEquals(e1, e2, 10, ymin, 16, ymax);
        //  ─────┐     ┌─────
        //       │ ┌─┐ │
        //       │ └─┘ │
        //  ─────┘     └─────
        e2.setRange(0, 40, 60);
        assertIntersectEquals(e1, e2, NaN, ymin, NaN, ymax);
        //  ─────┐     ┌─────
        //     ┌─┼─────┼─┐
        //     └─┼─────┼─┘
        //  ─────┘     └─────
        e2.setRange(0, 10, 90);
        assertIntersectEquals(e1, e2, NaN, ymin, NaN, ymax);
    }

    /**
     * Tests the {@link GeneralEnvelope#add(Envelope)} and
     * {@link Envelope2D#createUnion(Rectangle2D)} methods.
     *
     * @since 3.20
     */
    @Test
    public void testUnion() {
        //  ┌─────────────┐
        //  │  ┌───────┐  │
        //  │  └───────┘  │
        //  └─────────────┘
        final GeneralEnvelope e1 = create(20, -20, 80, 10);
        final GeneralEnvelope e2 = create(40, -10, 62,  8);
        assertUnionEquals(e1, e2, 20, -20, 80, 10, false, false);
        //  ┌──────────┐
        //  │  ┌───────┼──┐
        //  │  └───────┼──┘
        //  └──────────┘
        e1.setEnvelope(20, -20,  80, 12);
        e2.setEnvelope(40, -10, 100, 30);
        final double ymin=-20, ymax=30; // Will not change anymore
        assertUnionEquals(e1, e2, 20, ymin, 100, ymax, false, false);
        //  ────┐  ┌────
        //  ──┐ │  │ ┌──
        //  ──┘ │  │ └──
        //  ────┘  └────
        e1.setRange(0,  80, 20);
        e2.setRange(0, 100, 18);
        assertUnionEquals(e1, e2, 80, ymin, 20, ymax, false, false);
        //  ────┐  ┌────
        //  ────┼──┼─┐┌─
        //  ────┼──┼─┘└─
        //  ────┘  └────
        e2.setRange(0, 100, 90);
        assertUnionEquals(e1, e2, +0.0, ymin, -0.0, ymax, true, false);
        //  ─────┐      ┌─────
        //     ┌─┼────┐ │
        //     └─┼────┘ │
        //  ─────┘      └─────
        e2.setRange(0, 10, 30);
        assertUnionEquals(e1, e2, 80, ymin, 30, ymax, false, true);
        //  ──────────┐  ┌─────
        //    ┌────┐  │  │
        //    └────┘  │  │
        //  ──────────┘  └─────
        e2.setRange(0, 10, 16);
        assertUnionEquals(e1, e2, 80, ymin, 20, ymax, false, true);
        //  ─────┐     ┌─────
        //       │ ┌─┐ │
        //       │ └─┘ │
        //  ─────┘     └─────
        e2.setRange(0, 41, 60);
        assertUnionEquals(e1, e2, 41, ymin, 20, ymax, false, true);
        //  ─────┐     ┌─────
        //     ┌─┼─────┼─┐
        //     └─┼─────┼─┘
        //  ─────┘     └─────
        e2.setRange(0, 10, 90);
        assertUnionEquals(e1, e2, +0.0, ymin, -0.0, ymax, true, true);
    }

    /**
     * Tests the {@link GeneralEnvelope#add(DirectPosition)} and
     * {@link Envelope2D#add(Point2D)} methods.
     *
     * @since 3.20
     */
    @Test
    public void testAddPoint() {
        final double ymin=-20, ymax=30; // Will not change anymore
        final GeneralEnvelope  e = create(20, ymin,  80, ymax);
        final DirectPosition2D p = new DirectPosition2D(40, 15);
        assertAddEquals(e, p, 20, ymin, 80, ymax);

        p.x = 100; // Add on the right side.
        assertAddEquals(e, p, 20, ymin, 100, ymax);

        p.x = -10; // Add on the left side.
        assertAddEquals(e, p, -10, ymin, 80, ymax);

        e.setRange(0,  80, 20);
        p.x = 100; // No change expected.
        assertAddEquals(e, p, 80, ymin, 20, ymax);

        p.x = 70; // Add on the right side.
        assertAddEquals(e, p, 70, ymin, 20, ymax);

        p.x = 30; // Add on the left side.
        assertAddEquals(e, p, 80, ymin, 30, ymax);
    }

    /**
     * Tests the {@link GeneralEnvelope#setSubEnvelope(Envelope, int)} method.
     *
     * @since 3.16
     */
    @Test
    public void testSetSubEnvelope() {
        final GeneralEnvelope horizontal = create(-180, -90, 180, 90);
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
        assertEquals("POLYGON((-180 -90, -180 90, 180 90, 180 -90, -180 -90))", Envelopes.toPolygonWKT(envelope2D));

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
        assertEquals(-180, envelope.getMinimum(0), STRICT);
        assertEquals( 180, envelope.getMaximum(0), STRICT);
        assertEquals( -90, envelope.getMinimum(1), STRICT);
        assertEquals(  90, envelope.getMaximum(1), STRICT);

        envelope = new GeneralEnvelope("BOX3D(-180 -90 10, 180 90 30)");
        assertEquals(3, envelope.getDimension());
        assertEquals(-180, envelope.getMinimum(0), STRICT);
        assertEquals( 180, envelope.getMaximum(0), STRICT);
        assertEquals( -90, envelope.getMinimum(1), STRICT);
        assertEquals(  90, envelope.getMaximum(1), STRICT);
        assertEquals(  10, envelope.getMinimum(2), STRICT);
        assertEquals(  30, envelope.getMaximum(2), STRICT);

        envelope = new GeneralEnvelope("POLYGON((-80 -30,-100 40,80 40,100 -40,-80 -30))");
        assertEquals(-100, envelope.getMinimum(0), STRICT);
        assertEquals( 100, envelope.getMaximum(0), STRICT);
        assertEquals( -40, envelope.getMinimum(1), STRICT);
        assertEquals(  40, envelope.getMaximum(1), STRICT);

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
        assertTrue   (e1.equals(e2, EPS, true ));
        assertTrue   (e1.equals(e2, EPS, false));
        assertEquals (e1.hashCode(), e2.hashCode());
        /*
         * Offset slightly some coordinate value. Should not be equals anymore,
         * except when comparing with a tolerance value.
         */
        e2.setRange(2, e2.getMinimum(2) + 3E-5, e2.getMaximum(2) - 3E-5);
        assertTrue (e1.contains(e2, true ));
        assertFalse(e1.contains(e2, false));
        assertFalse(e1.equals  (e2));
        assertTrue (e1.equals  (e2, EPS, true ));
        assertTrue (e1.equals  (e2, EPS, false));
        assertFalse(e1.hashCode() == e2.hashCode());
        /*
         * Applies a greater offset. Should not be equal,
         * even when comparing with a tolerance value.
         */
        e2.setRange(1, e2.getMinimum(1) + 1.5, e2.getMaximum(1) - 1.5);
        assertTrue (e1.contains(e2, true ));
        assertFalse(e1.contains(e2, false));
        assertFalse(e1.equals  (e2));
        assertFalse(e1.equals  (e2, EPS, true ));
        assertFalse(e1.equals  (e2, EPS, false));
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
