/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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

import java.awt.geom.Rectangle2D;
import org.opengis.geometry.Envelope;
import org.geotoolkit.test.Depend;

import org.junit.*;
import static java.lang.Double.NaN;
import static org.geotoolkit.referencing.Assert.*;
import static org.geotoolkit.referencing.crs.DefaultGeographicCRS.WGS84;


/**
 * Tests the {@link AbstractEnvelope} class. Various implementations are used for each test.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
@Depend(DirectPositionTest.class)
public final strictfp class AbstractEnvelopeTest {
    /**
     * Tolerance threshold for strict floating point comparisons.
     */
    private static final double STRICT = 0;

    /**
     * Enumeration of implementations to be tested.
     * The {@code LAST} constant is for stopping the loops.
     */
    private static final int GENERAL=0, IMMUTABLE=1, RECTANGLE=2, LAST=3;

    /**
     * Creates an envelope of the given type. The type shall be one of the
     * {@link #GENERAL}, {@link #IMMUTABLE} or {@link #RECTANGLE} constants.
     */
    private static Envelope create(final int type,
            final double xmin, final double xmax,
            final double ymin, final double ymax)
    {
        switch (type) {
            case GENERAL: {
                final GeneralEnvelope envelope = new GeneralEnvelope(WGS84);
                envelope.setRange(0, xmin, xmax);
                envelope.setRange(1, ymin, ymax);
                return envelope;
            }
            case IMMUTABLE: {
                return new ImmutableEnvelope(WGS84, xmin, xmax, ymin, ymax);
            }
            case RECTANGLE: {
                return new Envelope2D(WGS84, xmin, ymin, xmax - xmin, ymax - ymin);
            }
            default: throw new IllegalArgumentException(String.valueOf(type));
        }
    }

    /**
     * Returns the lower value at the given dimension.
     */
    private static double getLower(final Envelope envelope, final int dimension) {
        if (envelope instanceof Envelope2D) {
            return ((Envelope2D) envelope).getLowerCorner().getOrdinate(dimension);
        }
        return ((AbstractEnvelope) envelope).getLower(dimension);
    }

    /**
     * Returns the upper value at the given dimension.
     */
    private static double getUpper(final Envelope envelope, final int dimension) {
        if (envelope instanceof Envelope2D) {
            return ((Envelope2D) envelope).getUpperCorner().getOrdinate(dimension);
        }
        return ((AbstractEnvelope) envelope).getUpper(dimension);
    }

    /**
     * Tests the simple case (no anti-meridian crossing).
     *
     * {@preformat text
     *     ┌─────────────┐
     *     │  ┌───────┐  │
     *     │  └───────┘  │
     *     └─────────────┘
     * }
     */
    @Test
    public void testSimpleEnvelope() {
        final DirectPosition2D inside  = new DirectPosition2D( 3, 32);
        final DirectPosition2D outside = new DirectPosition2D(-5, 32);
        final Envelope2D contained = (Envelope2D) create(RECTANGLE, -2, 10, 35, 40);
        final Envelope2D intersect = (Envelope2D) create(RECTANGLE, -2, 16, 35, 40);
        final Envelope2D disjoint  = (Envelope2D) create(RECTANGLE, 14, 16, 35, 40);
        for (int type=0; type<LAST; type++) {
            final String label = "Type " + type;
            final Envelope envelope = create(type, -4, 12, 30, 50);
            assertEquals(label, 30, envelope.getMinimum(1), STRICT);
            assertEquals(label, 50, envelope.getMaximum(1), STRICT);
            assertEquals(label, 40, envelope.getMedian (1), STRICT);
            assertEquals(label, 20, envelope.getSpan   (1), STRICT);
            assertEquals(label, -4, envelope.getMinimum(0), STRICT);
            assertEquals(label, 12, envelope.getMaximum(0), STRICT);
            assertEquals(label,  4, envelope.getMedian (0), STRICT);
            assertEquals(label, 16, envelope.getSpan   (0), STRICT);
            if (envelope instanceof AbstractEnvelope) {
                final AbstractEnvelope ext = (AbstractEnvelope) envelope;
                assertTrue (label, ext.contains  (inside));
                assertFalse(label, ext.contains  (outside));
                assertFalse(label, ext.contains  (intersect, false));
                assertTrue (label, ext.intersects(intersect, false));
                assertDisjoint(ext, disjoint);
                assertContains(ext, contained);
            }
            if (envelope instanceof Rectangle2D) {
                final Rectangle2D ext = (Rectangle2D) envelope;
                assertTrue (label, ext.contains  (inside));
                assertFalse(label, ext.contains  (outside));
                assertFalse(label, ext.contains  (intersect));
                assertTrue (label, ext.intersects(intersect));
                assertDisjoint(ext, disjoint);
                assertContains(ext, contained);
            }
        }
    }

    /**
     * Tests a case crossing the anti-meridian.
     *
     * {@preformat text
     *      ─────┐  ┌─────────              ─────┐      ┌─────
     *           │  │  ┌────┐       or      ──┐  │      │  ┌──
     *           │  │  └────┘               ──┘  │      │  └──
     *      ─────┘  └─────────              ─────┘      └─────
     * }
     */
    @Test
    public void testCrossingAntiMeridian() {
        final DirectPosition2D inside  = new DirectPosition2D(18, 32);
        final DirectPosition2D outside = new DirectPosition2D( 3, 32);
        final Envelope2D contained  = (Envelope2D) create(RECTANGLE, 14, 16, 35, 40);
        final Envelope2D intersect  = (Envelope2D) create(RECTANGLE, -2, 16, 35, 40);
        final Envelope2D disjoint   = (Envelope2D) create(RECTANGLE, -2, 10, 35, 40);
        final Envelope2D spanning   = (Envelope2D) create(RECTANGLE, 16, -8, 35, 40);
        for (int type=0; type<LAST; type++) {
            final String label = "Type " + type;
            final Envelope envelope = create(type, 12, -4, 30, 50);
            assertEquals(label,   30, envelope.getMinimum(1), STRICT);
            assertEquals(label,   50, envelope.getMaximum(1), STRICT);
            assertEquals(label,   40, envelope.getMedian (1), STRICT);
            assertEquals(label,   20, envelope.getSpan   (1), STRICT);
            assertEquals(label,   12, getLower (envelope, 0), STRICT);
            assertEquals(label, -180, envelope.getMinimum(0), STRICT);
            assertEquals(label,   -4, getUpper (envelope, 0), STRICT);
            assertEquals(label, +180, envelope.getMaximum(0), STRICT);
            assertEquals(label, -176, envelope.getMedian (0), STRICT);
            assertEquals(label,  344, envelope.getSpan   (0), STRICT); // 360° - testSimpleEnvelope()
            if (envelope instanceof AbstractEnvelope) {
                final AbstractEnvelope ext = (AbstractEnvelope) envelope;
                assertTrue (label, ext.contains  (inside));
                assertFalse(label, ext.contains  (outside));
                assertFalse(label, ext.contains  (intersect, false));
                assertTrue (label, ext.intersects(intersect, false));
                assertDisjoint(ext, disjoint);
                assertContains(ext, contained);
                assertContains(ext, spanning);
            }
            if (envelope instanceof Rectangle2D) {
                final Rectangle2D ext = (Rectangle2D) envelope;
                assertTrue (label, ext.contains  (inside));
                assertFalse(label, ext.contains  (outside));
                assertFalse(label, ext.contains  (intersect));
                assertTrue (label, ext.intersects(intersect));
                assertDisjoint(ext, disjoint);
                assertContains(ext, contained);
                assertContains(ext, spanning);
            }
        }
    }

    /**
     * Tests a the anti-meridian case with a larger empty space
     * on the left side.
     *
     * {@preformat text
     *      ───┐    ┌─────────              ───┐      ┌─────
     *         │    │  ┌────┐       or      ───┼──┐   │  ┌──
     *         │    │  └────┘               ───┼──┘   │  └──
     *      ───┘    └─────────              ───┘      └─────
     * }
     */
    @Test
    public void testCrossingAntiMeridianTwice() {
        final DirectPosition2D inside  = new DirectPosition2D(18, 32);
        final DirectPosition2D outside = new DirectPosition2D( 3, 32);
        final Envelope2D contained  = (Envelope2D) create(RECTANGLE, 14, 16, 35, 40);
        final Envelope2D intersect  = (Envelope2D) create(RECTANGLE, -2, 16, 35, 40);
        final Envelope2D disjoint   = (Envelope2D) create(RECTANGLE, -2, 10, 35, 40);
        final Envelope2D spanning   = (Envelope2D) create(RECTANGLE, 16, -8, 35, 40);
        for (int type=0; type<LAST; type++) {
            final String label = "Type " + type;
            final Envelope envelope = create(type, 12, -364, 30, 50);
            assertEquals(label,   30, envelope.getMinimum(1), STRICT);
            assertEquals(label,   50, envelope.getMaximum(1), STRICT);
            assertEquals(label,   40, envelope.getMedian (1), STRICT);
            assertEquals(label,   20, envelope.getSpan   (1), STRICT);
            assertEquals(label,   12, getLower (envelope, 0), STRICT);
            assertEquals(label, -180, envelope.getMinimum(0), STRICT);
            assertEquals(label, -364, getUpper (envelope, 0), STRICT);
            assertEquals(label, +180, envelope.getMaximum(0), STRICT);
            assertEquals(label,    4, envelope.getMedian (0), STRICT); // Note the alternance with the previous test methods.
            assertEquals(label,  NaN, envelope.getSpan   (0), STRICT); // testCrossingAntiMeridian() + 360°.
            if (envelope instanceof AbstractEnvelope) {
                final AbstractEnvelope ext = (AbstractEnvelope) envelope;
                assertTrue (label, ext.contains  (inside));
                assertFalse(label, ext.contains  (outside));
                assertFalse(label, ext.contains  (intersect, false));
                assertTrue (label, ext.intersects(intersect, false));
                assertFalse(label, ext.contains  (spanning,  false));
                assertTrue (label, ext.intersects(spanning,  false));
                assertDisjoint(ext, disjoint);
                assertContains(ext, contained);
            }
            if (envelope instanceof Rectangle2D) {
                final Rectangle2D ext = (Rectangle2D) envelope;
                assertTrue (label, ext.contains  (inside));
                assertFalse(label, ext.contains  (outside));
                assertFalse(label, ext.contains  (intersect));
                assertTrue (label, ext.intersects(intersect));
                assertFalse(label, ext.contains  (spanning));
                assertTrue (label, ext.intersects(spanning));
                assertDisjoint(ext, disjoint);
                assertContains(ext, contained);
            }
        }
    }

    /**
     * Tests a the anti-meridian case with a larger empty space
     * on the left and right sides.
     */
    @Test
    public void testCrossingAntiMeridianThreeTimes() {
        final DirectPosition2D wasInside = new DirectPosition2D(18, 32);
        final DirectPosition2D outside   = new DirectPosition2D( 3, 32);
        final Envelope2D wasContained = (Envelope2D) create(RECTANGLE, 14, 16, 35, 40);
        final Envelope2D wasIntersect = (Envelope2D) create(RECTANGLE, -2, 16, 35, 40);
        final Envelope2D disjoint     = (Envelope2D) create(RECTANGLE, -2, 10, 35, 40);
        final Envelope2D spanning     = (Envelope2D) create(RECTANGLE, 16, -8, 35, 40);
        for (int type=0; type<LAST; type++) {
            final String label = "Type " + type;
            final Envelope envelope = create(type, 372, -364, 30, 50);
            assertEquals(label,   30, envelope.getMinimum(1), STRICT);
            assertEquals(label,   50, envelope.getMaximum(1), STRICT);
            assertEquals(label,   40, envelope.getMedian (1), STRICT);
            assertEquals(label,   20, envelope.getSpan   (1), STRICT);
            assertEquals(label,  372, getLower (envelope, 0), STRICT);
            assertEquals(label, -180, envelope.getMinimum(0), STRICT);
            assertEquals(label, -364, getUpper (envelope, 0), STRICT);
            assertEquals(label, +180, envelope.getMaximum(0), STRICT);
            assertEquals(label, -176, envelope.getMedian (0), STRICT); // Note the alternance with the previous test methods.
            assertEquals(label,  NaN, envelope.getSpan   (0), STRICT); // testCrossingAntiMeridianTwice() + 360°.
            if (envelope instanceof AbstractEnvelope) {
                final AbstractEnvelope ext = (AbstractEnvelope) envelope;
                assertFalse(label, ext.contains  (wasInside));
                assertFalse(label, ext.contains  (outside));
                assertFalse(label, ext.contains  (spanning,  false));
                assertTrue (label, ext.intersects(spanning,  false));
                assertDisjoint(ext, wasIntersect);
                assertDisjoint(ext, disjoint);
                assertDisjoint(ext, wasContained);
            }
            if (envelope instanceof Rectangle2D) {
                final Rectangle2D ext = (Rectangle2D) envelope;
                assertFalse(label, ext.contains  (wasInside));
                assertFalse(label, ext.contains  (outside));
                assertFalse(label, ext.contains  (spanning));
                assertTrue (label, ext.intersects(spanning));
                assertDisjoint(ext, wasIntersect);
                assertDisjoint(ext, disjoint);
                assertDisjoint(ext, wasContained);
            }
        }
    }

    /**
     * Tests an empty envelope from -0 to 0°
     */
    @Test
    public void testRange0() {
        final DirectPosition2D wasInside = new DirectPosition2D(18, 32);
        final DirectPosition2D outside   = new DirectPosition2D( 3, 32);
        final Envelope2D wasContained = (Envelope2D) create(RECTANGLE, 14, 16, 35, 40);
        final Envelope2D intersect    = (Envelope2D) create(RECTANGLE, -2, 16, 35, 40);
        final Envelope2D spanning     = (Envelope2D) create(RECTANGLE, 16, -8, 35, 40);
        for (int type=0; type<LAST; type++) {
            final String label = "Type " + type;
            final Envelope envelope = create(type, -0.0, 0.0, 30, 50);
            assertEquals(label,   30, envelope.getMinimum(1), STRICT);
            assertEquals(label,   50, envelope.getMaximum(1), STRICT);
            assertEquals(label,   40, envelope.getMedian (1), STRICT);
            assertEquals(label,   20, envelope.getSpan   (1), STRICT);
            assertEquals(label, -0.0, envelope.getMinimum(0), STRICT);
            assertEquals(label,  0.0, envelope.getMaximum(0), STRICT);
            assertEquals(label,    0, envelope.getMedian (0), STRICT);
            assertEquals(label,    0, envelope.getSpan   (0), STRICT);
            if (envelope instanceof AbstractEnvelope) {
                final AbstractEnvelope ext = (AbstractEnvelope) envelope;
                assertFalse(label, ext.contains  (wasInside));
                assertFalse(label, ext.contains  (outside));
                assertFalse(label, ext.contains  (intersect, false));
                assertTrue (label, ext.intersects(intersect, false));
                assertFalse(label, ext.contains  (spanning,  false));
                assertFalse(label, ext.intersects(spanning,  false));
                assertFalse(label, ext.intersects(spanning,  false));
                assertDisjoint(ext, wasContained);
            }
            if (envelope instanceof Rectangle2D) {
                final Rectangle2D ext = (Rectangle2D) envelope;
                assertFalse(label, ext.contains  (wasInside));
                assertFalse(label, ext.contains  (outside));
                assertFalse(label, ext.contains  (intersect));
                assertTrue (label, ext.intersects(intersect));
                assertFalse(label, ext.contains  (spanning));
                assertFalse(label, ext.intersects(spanning));
                assertFalse(label, ext.intersects(spanning));
                assertDisjoint(ext, wasContained);
            }
        }
    }

    /**
     * Tests a case crossing the anti-meridian crossing, from 0° to -0°.
     */
    @Test
    public void testRange360() {
        final DirectPosition2D inside     = new DirectPosition2D(18, 32);
        final DirectPosition2D wasOutside = new DirectPosition2D( 3, 32);
        final Envelope2D contained = (Envelope2D) create(RECTANGLE, 14, 16, 35, 40);
        final Envelope2D intersect = (Envelope2D) create(RECTANGLE, -2, 16, 35, 40);
        final Envelope2D spanning  = (Envelope2D) create(RECTANGLE, 16, -8, 35, 40);
        for (int type=0; type<LAST; type++) {
            final String label = "Type " + type;
            final Envelope envelope = create(type, 0.0, -0.0, 30, 50);
            assertEquals(label,   30, envelope.getMinimum(1), STRICT);
            assertEquals(label,   50, envelope.getMaximum(1), STRICT);
            assertEquals(label,   40, envelope.getMedian (1), STRICT);
            assertEquals(label,   20, envelope.getSpan   (1), STRICT);
            assertEquals(label,  0.0, getLower (envelope, 0), STRICT);
            assertEquals(label, -180, envelope.getMinimum(0), STRICT);
            assertEquals(label, -0.0, getUpper (envelope, 0), STRICT);
            assertEquals(label, +180, envelope.getMaximum(0), STRICT);
            assertEquals(label,  180, envelope.getMedian (0), STRICT);
            assertEquals(label,  360, envelope.getSpan   (0), STRICT);
            if (envelope instanceof AbstractEnvelope) {
                final AbstractEnvelope ext = (AbstractEnvelope) envelope;
                assertTrue(label, ext.contains(inside));
                assertTrue(label, ext.contains(wasOutside));
                assertTrue(label, ext.intersects(intersect, false));
                assertContains(ext, contained);
                assertContains(ext, spanning);
            }
            if (envelope instanceof Rectangle2D) {
                final Rectangle2D ext = (Rectangle2D) envelope;
                assertTrue(label, ext.contains(inside));
                assertTrue(label, ext.contains(wasOutside));
                assertTrue(label, ext.intersects(intersect));
                assertContains(ext, contained);
                assertContains(ext, spanning);
            }
        }
    }
}
