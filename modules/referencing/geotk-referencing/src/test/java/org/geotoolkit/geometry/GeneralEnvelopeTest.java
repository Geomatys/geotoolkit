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
import org.apache.sis.geometry.Envelope2D;

import org.junit.*;

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
}
