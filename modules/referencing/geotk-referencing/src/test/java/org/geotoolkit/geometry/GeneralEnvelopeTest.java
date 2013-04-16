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

import org.opengis.geometry.Envelope;
import org.opengis.geometry.DirectPosition;

import org.apache.sis.math.MathFunctions;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.junit.*;

import static java.lang.Double.NaN;
import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.cs.AxisRangeType.*;
import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.WGS84;


/**
 * Tests the {@link GeneralEnvelope} class. The {@link Envelope2D} class will also be tested as a
 * side effect, because it is used for comparison purpose. Note that {@link AbstractEnvelopeTest}
 * already tested {@code contains} and {@code intersects} methods, so this test file will focus on
 * other methods.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 3.21
 *
 * @since 2.4
 */
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
     * The {@code xmin} and {@code ymin} argument are actually the <var>x</var> ordinate values
     * for the lower and upper corner. The actual {@code xmin} and {@code ymin} values will be
     * inferred from those corners.
     * <p>
     * This method assumes that only the <var>x</var> axis may be a wraparound axis.
     */
    private static void assertEnvelopeEquals(final Envelope e,
            final double lower, final double ymin, final double upper, final double ymax)
    {
        final double xmin, xmax;
        if (MathFunctions.isNegative(upper - lower)) { // Check for anti-meridian spanning.
            xmin = -180;
            xmax = +180;
        } else {
            xmin = lower;
            xmax = upper;
        }
        final DirectPosition l = e.getLowerCorner();
        final DirectPosition u = e.getUpperCorner();
        assertEquals("lower", lower, l.getOrdinate(0), STRICT);
        assertEquals("upper", upper, u.getOrdinate(0), STRICT);
        assertEquals("xmin",  xmin,  e.getMinimum (0), STRICT);
        assertEquals("xmax",  xmax,  e.getMaximum (0), STRICT);
        assertEquals("ymin",  ymin,  e.getMinimum (1), STRICT);
        assertEquals("ymax",  ymax,  e.getMaximum (1), STRICT);
        assertEquals("ymin",  ymin,  l.getOrdinate(1), STRICT);
        assertEquals("ymax",  ymax,  u.getOrdinate(1), STRICT);
        if (e instanceof Envelope2D) {
            final Envelope2D ri = (Envelope2D) e;
            assertEquals("xmin", xmin, ri.getMinX(), STRICT);
            assertEquals("xmax", xmax, ri.getMaxX(), STRICT);
            assertEquals("ymin", ymin, ri.getMinY(), STRICT);
            assertEquals("ymax", ymax, ri.getMaxY(), STRICT);
        }
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
        assertEnvelopeEquals(r, xmin, ymin, xmax, ymax);

        // Compares with GeneralEnvelope.
        final GeneralEnvelope ec = new GeneralEnvelope(e);
        ec.add(p);
        assertEnvelopeEquals(ec, xmin, ymin, xmax, ymax);
        assertTrue("Using GeneralEnvelope.", ec.equals(r, STRICT, false));
    }

    /**
     * Tests the {@link GeneralEnvelope#reduceToDomain(boolean)} method.
     *
     * @since 3.20
     */
    private void testReduceToDomain(final boolean useDomainOfCRS) {
        GeneralEnvelope e = create(-100, -100, +100, +100);
        assertTrue(e.reduceToDomain(useDomainOfCRS));
        assertEnvelopeEquals(e, -100, -90, +100, +90);

        e = create(185, 10, 190, 20);
        assertTrue(e.reduceToDomain(useDomainOfCRS));
        assertEnvelopeEquals(e, -175, 10, -170, 20);

        e = create(175, 10, 185, 20);
        assertTrue(e.reduceToDomain(useDomainOfCRS));
        assertEnvelopeEquals(e, 175, 10, -175, 20);

        e = create(0, 10, 360, 20);
        assertTrue(e.reduceToDomain(useDomainOfCRS));
        assertEquals("Expect positive zero", Double.doubleToLongBits(+0.0), Double.doubleToLongBits(e.getLower(0)));
        assertEquals("Expect negative zero", Double.doubleToLongBits(-0.0), Double.doubleToLongBits(e.getUpper(0)));
    }

    /**
     * Tests the {@link GeneralEnvelope#reduceToDomain(boolean)} method
     * with an envelope having more then 360° of longitude.
     *
     * @since 3.21
     */
    @Test
    public void testReduceWorldRoundToDomain() {
        final GeneralEnvelope env = new GeneralEnvelope(WGS84);
        env.setRange(0, -195, 170); // -195° is equivalent to 165°
        env.setRange(1, -90, 90);

        assertTrue(env.reduceToDomain(false));
        assertEnvelopeEquals(env, -180, -90, +180, +90);
    }

    /**
     * Tests the {@link GeneralEnvelope#reduceToDomain(boolean)} method,
     * reducing to the Coordinate System domain.
     *
     * @since 3.20
     */
    @Test
    public void testReduceToDomainOfCS() {
        testReduceToDomain(false);
    }

    /**
     * Tests the {@link GeneralEnvelope#reduceToDomain(boolean)} method,
     * reducing to the Coordinate Reference System domain.
     *
     * @since 3.20
     */
    @Test
    public void testReduceToDomainOfCRS() {
        testReduceToDomain(true);
    }

    /**
     * Tests the {@link GeneralEnvelope#reorderCorners()}.
     *
     * @since 3.20
     */
    @Test
    public void testReorderCorners() {
        // Normal envelope: no change expected.
        GeneralEnvelope e = create(-100, -10, +100, +10);
        assertFalse(e.reorderCorners());
        assertEnvelopeEquals(e, -100, -10, +100, +10);

        // Anti-meridian spanning: should substitute [-180 … 180]°
        e = create(30, -10, -60, 10);
        assertTrue(e.reorderCorners());
        assertEnvelopeEquals(e, -180, -10, 180, 10);

        // Anti-meridian spanning using positive and negative zero.
        e = create(0.0, -10, -0.0, 10);
        assertTrue(e.reorderCorners());
        assertEnvelopeEquals(e, -180, -10, 180, 10);
    }

    /**
     * Tests shifting from the [-180 … 180]° to the [0 … 360]° longitude range.
     * The anti-meridian spanning is located at 360°.
     *
     * @since 3.20
     */
    @Test
    public void testShiftLongitudeRange() {
        GeneralEnvelope e = create(-100, -10, +100, +10);
        e.setCoordinateReferenceSystem(WGS84.shiftAxisRange(POSITIVE_LONGITUDE));
        assertTrue(e.reduceToDomain(false));
        assertEquals("Expected anti-meridian spanning", 260, e.getLower(0), STRICT);
        assertEquals("Expected anti-meridian spanning", 100, e.getUpper(0), STRICT);
        assertTrue(e.reorderCorners());
        assertEnvelopeEquals(e, 0, -10, 360, +10);
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
        assertEquals(-180, envelope.getLower(0), 0);
        assertEquals( 180, envelope.getUpper(0), 0);
        assertEquals( -90, envelope.getLower(1), 0);
        assertEquals(  90, envelope.getUpper(1), 0);
        assertEquals(  20, envelope.getLower(2), 0);
        assertEquals(  40, envelope.getUpper(2), 0);
    }

    /**
     * Tests the {@link AbstractEnvelope#toString(Envelope)} method.
     *
     * @since 3.09
     */
    @Test
    public void testWktFormatting() {
        Envelope2D envelope2D = new Envelope2D(null, -180, -90, 360, 180);
        assertEquals("BOX(-180 -90, 180 90)", envelope2D.toString());
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
        assertEquals(-180, envelope.getLower(0), STRICT);
        assertEquals( 180, envelope.getUpper(0), STRICT);
        assertEquals( -90, envelope.getLower(1), STRICT);
        assertEquals(  90, envelope.getUpper(1), STRICT);

        envelope = new GeneralEnvelope("BOX3D(-180 -90 10, 180 90 30)");
        assertEquals(3, envelope.getDimension());
        assertEquals(-180, envelope.getLower(0), STRICT);
        assertEquals( 180, envelope.getUpper(0), STRICT);
        assertEquals( -90, envelope.getLower(1), STRICT);
        assertEquals(  90, envelope.getUpper(1), STRICT);
        assertEquals(  10, envelope.getLower(2), STRICT);
        assertEquals(  30, envelope.getUpper(2), STRICT);

        envelope = new GeneralEnvelope("POLYGON((-80 -30,-100 40,80 40,100 -40,-80 -30))");
        assertEquals(-100, envelope.getLower(0), STRICT);
        assertEquals( 100, envelope.getUpper(0), STRICT);
        assertEquals( -40, envelope.getLower(1), STRICT);
        assertEquals(  40, envelope.getUpper(1), STRICT);

        assertEquals("BOX(6 10, 6 10)",     new GeneralEnvelope("POINT(6 10)").toString());
        assertEquals("BOX3D(6 10 3, 6 10 3)", new GeneralEnvelope("POINT M [ 6 10 3 ] ").toString());
        assertEquals("BOX(3 4, 20 50)",     new GeneralEnvelope("LINESTRING(3 4,10 50,20 25)").toString());
        assertEquals("BOX(1 1, 6 5)",       new GeneralEnvelope(
                "MULTIPOLYGON(((1 1,5 1,5 5,1 5,1 1),(2 2, 3 2, 3 3, 2 3,2 2)),((3 3,6 2,6 4,3 3)))").toString());
        assertEquals("BOX(3 6, 7 10)", new GeneralEnvelope("GEOMETRYCOLLECTION(POINT(4 6),LINESTRING(3 8,7 10))").toString());
        assertEquals(0, new GeneralEnvelope("BOX()").getDimension());

        try {
            new GeneralEnvelope("BOX(3 4");
            fail("Parsing should fails because of missing parenthesis.");
        } catch (IllegalArgumentException e) {
            // This is the expected exception.
            assertTrue(e.getMessage().contains("BOX"));
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
        assertFalse (e1.isAllNaN());
        assertEquals(e1.getLowerCorner(), e1.getUpperCorner());
        /*
         * Initializes with arbitrary coordinate values.
         * Should not be empty anymore.
         */
        for (int i=e1.getDimension(); --i>=0;) {
            e1.setRange(i, i*5 + 2, i*6 + 5);
        }
        assertFalse(e1.isAllNaN ());
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
        e2.setRange(2, e2.getLower(2) + 3E-5, e2.getUpper(2) - 3E-5);
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
        e2.setRange(1, e2.getLower(1) + 1.5, e2.getUpper(1) - 1.5);
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
