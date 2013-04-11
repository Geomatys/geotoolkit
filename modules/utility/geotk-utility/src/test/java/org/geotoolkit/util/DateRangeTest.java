/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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
package org.geotoolkit.util;

import java.util.Date;
import org.apache.sis.measure.Range;
import org.geotoolkit.test.TestBase;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests {@link DateRange}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class DateRangeTest extends TestBase {
    /**
     * Tests {@link DateRange#union(Range)}.
     */
    @Test
    public void testUnion() {
        final Date min = date("1998-04-02 13:00:00");
        final Date in1 = date("1998-05-12 11:00:00");
        final Date in2 = date("1998-06-08 14:00:00");
        final Date max = date("1998-07-01 19:00:00");
        final DateRange r1 = new DateRange(min, in2);
        final DateRange r2 = new DateRange(in1, max);
        final DateRange rt = r1.union(r2);
        assertEquals(min, rt.getMinValue());
        assertEquals(max, rt.getMaxValue());
        assertEquals(rt, r2.union(r1));
        /*
         * Test a range fully included in the other range.
         */
        final DateRange outer = new DateRange(min, max);
        final DateRange inner = new DateRange(in1, in2);
        assertSame(outer, outer.union(inner));
        assertSame(outer, inner.union(outer));
        /*
         * Same test than above, but with a cast from Range to DateRange.
         */
        final Range<Date> outerAsRange = new Range<>(Date.class, min, true, max, true);
        assertSame(outerAsRange, outerAsRange.union(inner));
        assertEquals(outer, inner.union(outerAsRange));
    }

    /**
     * Tests {@link DateRange#intersect(Range)}.
     */
    @Test
    public void testIntersect() {
        final Date min = date("1998-04-02 13:00:00");
        final Date in1 = date("1998-05-12 11:00:00");
        final Date in2 = date("1998-06-08 14:00:00");
        final Date max = date("1998-07-01 19:00:00");
        final DateRange r1 = new DateRange(min, in2);
        final DateRange r2 = new DateRange(in1, max);
        final DateRange rt = r1.intersect(r2);
        assertEquals(in1, rt.getMinValue());
        assertEquals(in2, rt.getMaxValue());
        assertEquals(rt, r2.intersect(r1));
        /*
         * Test a range fully included in the other range.
         */
        final DateRange outer = new DateRange(min, max);
        final DateRange inner = new DateRange(in1, in2);
        assertSame(inner, outer.intersect(inner));
        assertSame(inner, inner.intersect(outer));
        /*
         * Same test than above, but with a cast from Range to DateRange.
         */
        final Range<Date> innerAsRange = new Range<>(Date.class, in1, true, in2, true);
        assertSame(innerAsRange, innerAsRange.intersect(outer));
        assertEquals(inner, outer.intersect(innerAsRange));
    }

    /**
     * Tests {@link DateRange#subtract(Range)}.
     */
    @Test
    public void testSubtract() {
        final Date min = date("1998-04-02 13:00:00");
        final Date in1 = date("1998-05-12 11:00:00");
        final Date in2 = date("1998-06-08 14:00:00");
        final Date max = date("1998-07-01 19:00:00");
        final DateRange outer = new DateRange(min, max);
        final DateRange inner = new DateRange(in1, in2);
        final DateRange[] rt = outer.subtract(inner);
        assertEquals(2, rt.length);
        assertEquals(min, rt[0].getMinValue());
        assertEquals(in1, rt[0].getMaxValue());
        assertEquals(in2, rt[1].getMinValue());
        assertEquals(max, rt[1].getMaxValue());
    }
}
