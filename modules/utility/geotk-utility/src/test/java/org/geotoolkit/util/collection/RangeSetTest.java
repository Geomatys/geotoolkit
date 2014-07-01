/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2003-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util.collection;

import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import org.apache.sis.measure.Range;
import org.geotoolkit.util.DateRange;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.test.Performance;

import org.junit.*;
import static org.apache.sis.test.Assert.*;


/**
 * Tests the {@link RangeSet} implementation.
 *
 * @author Andrea Aime (TOPP)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.2
 */
public final strictfp class RangeSetTest {
    /**
     * Tests {@link RangeSet#add} followed by {@link RangeSet#remove},
     * working on double values.
     */
    @Test
    public void testRangeRemoval() {
        final RangeSet<Double> ranges = new RangeSet<>(Double.class);
        assertEquals(NumberRange.class, ranges.getElementType());
        assertEquals(Double.TYPE, ranges.getArrayElementType());
        assertEquals(0, ranges.size());

        ranges.add(10.0, 22.0);
        assertEquals(1, ranges.size());

        ranges.remove(8.0, 12.0);
        assertEquals(1, ranges.size());

        final RangeSet<Double> expected = new RangeSet<>(Double.class);
        expected.add(12.0, 22.0);
        assertEquals("Lower removal", expected, ranges);

        ranges.remove(20.0, 30.0);
        assertEquals(1, ranges.size());

        expected.clear();
        expected.add(12.0, 20.0);
        assertEquals("Upper removal", expected, ranges);

        ranges.remove(8.0, 10.0);
        assertEquals(1, ranges.size());
        assertEquals("Inferior null removal", expected, ranges);
        ranges.remove(8.0, 12.0);
        assertEquals(1, ranges.size());
        assertEquals("Inferior touch removal", expected, ranges);

        ranges.remove(22.0, 40.0);
        assertEquals(1, ranges.size());
        assertEquals("Upper null removal", expected, ranges);
        ranges.remove(20.0, 40.0);
        assertEquals(1, ranges.size());
        assertEquals("Upper touch removal", expected, ranges);

        ranges.remove(14.0, 16.0);
        assertEquals(2, ranges.size());
        expected.clear();
        expected.add(12.0, 14.0);
        expected.add(16.0, 20.0);
        assertEquals("Central removal", expected, ranges);

        ranges.remove(15.0, 15.5);
        assertEquals(2, ranges.size());
        assertEquals("Central null removal", expected, ranges);

        ranges.remove(14.0, 16.0);
        assertEquals(2, ranges.size());
        assertEquals("Central touch null removal", expected, ranges);

        ranges.remove(15.0, 17.0);
        assertEquals(2, ranges.size());
        expected.clear();
        expected.add(12.0, 14.0);
        expected.add(17.0, 20.0);
        assertEquals("Central right removal", expected, ranges);

        ranges.remove(13.0, 15.0);
        assertEquals(2, ranges.size());
        expected.clear();
        expected.add(12.0, 13.0);
        expected.add(17.0, 20.0);
        assertEquals("Central left removal", expected, ranges);

        ranges.remove(12.5, 18.0);
        assertEquals(2, ranges.size());
        expected.clear();
        expected.add(12.0, 12.5);
        expected.add(18.0, 20.0);
        assertEquals("Central both removal", expected, ranges);

        ranges.remove(18.5, 19.0);
        assertEquals(3, ranges.size());
        expected.clear();
        expected.add(12.0, 12.5);
        expected.add(18.0, 18.5);
        expected.add(19.0, 20.0);
        assertEquals("Central removal 2", expected, ranges);

        ranges.remove(17.0, 19.0);
        assertEquals(2, ranges.size());
        expected.clear();
        expected.add(12.0, 12.5);
        expected.add(19.0, 20.0);
        assertEquals("Central wipeout", expected, ranges);

        ranges.remove(0.0, 25.0);
        assertEquals(0, ranges.size());
        expected.clear();
        assertEquals("Full wipeout", expected, ranges);
    }

    /**
     * Tests {@link RangeSet} using integer values.
     */
    @Test
    public void testIntegers() {
        final RangeSet<Integer> ranges = new RangeSet<>(Integer.class);
        assertEquals(NumberRange.class, ranges.getElementType());
        assertEquals(Integer.TYPE, ranges.getArrayElementType());
        assertTrue(ranges.isEmpty());

        ranges.add(10, 22);
        assertEquals(1, ranges.size());
        assertTrue (ranges.contains(NumberRange.create(10, true, 22, true)));
        assertFalse(ranges.contains(NumberRange.create(10, true, 20, true)));

        ranges.add(14, 25);
        assertEquals(1, ranges.size());
        assertFalse(ranges.contains(NumberRange.create(10, true, 22, true)));
        assertTrue (ranges.contains(NumberRange.create(10, true, 25, true)));

        ranges.add(-5, 5);
        assertEquals(2, ranges.size());
        assertTrue(ranges.contains(NumberRange.create(10, true, 25, true)));
        assertTrue(ranges.contains(NumberRange.create(-5, true,  5, true)));

        ranges.add(NumberRange.create(5, true, 10, true));
        assertEquals(1, ranges.size());
        assertFalse(ranges.contains(NumberRange.create(10, true, 25, true)));
        assertFalse(ranges.contains(NumberRange.create(-5, true,  5, true)));
        assertTrue (ranges.contains(NumberRange.create(-5, true, 25, true)));

        ranges.add(40, 50);
        ranges.add(30, 35);
        ranges.add(NumberRange.create(28, true, 32, true));
        ranges.add(-20, -10);
        ranges.add(60, 70);
        assertEquals(5, ranges.size());

        final Iterator<Range<Integer>> it = ranges.iterator();
        assertEquals(NumberRange.create(-20, true, -10, true), it.next());
        assertEquals(NumberRange.create( -5, true,  25, true), it.next());
        assertEquals(NumberRange.create( 28, true,  35, true), it.next());
        assertEquals(NumberRange.create( 40, true,  50, true), it.next());
        assertEquals(NumberRange.create( 60, true,  70, true), it.next());
        assertFalse(it.hasNext());
    }

    /**
     * Tests {@link RangeSet} using date values. This is an indirect way
     * to test {@link org.apache.sis.util.ObjectConverter}.
     */
    @Test
    @Ignore
    public void testDates() {
        final RangeSet<Date> ranges = new RangeSet<>(Date.class);
        assertEquals(DateRange.class, ranges.getElementType());
        assertEquals(Long.TYPE, ranges.getArrayElementType());
        assertTrue(ranges.isEmpty());

        final long day = 24*60*60*1000L;
        final Date now = new Date();
        final Date yesterday = new Date(now.getTime() - day);
        ranges.add(yesterday, now);
        assertEquals(1, ranges.size());
        assertTrue(ranges.contains(new DateRange(yesterday, now)));

        final Date lastWeek = new Date(now.getTime() - 7*day);
        final Date other = new Date(lastWeek.getTime() + 2*day);
        ranges.add(new DateRange(lastWeek, other));
        assertEquals(2, ranges.size());

        final Iterator<Range<Date>> it = ranges.iterator();
        assertEquals(new DateRange(lastWeek, other), it.next());
        assertEquals(new DateRange(yesterday, now), it.next());
        assertFalse(it.hasNext());
    }

    /**
     * Tests {@link RangeSet} using string values.
     */
    @Test
    @Ignore
    public void testStrings() {
        final RangeSet<String> ranges = new RangeSet<>(String.class);
        assertEquals(Range.class, ranges.getElementType());
        assertEquals(String.class, ranges.getArrayElementType());
        assertTrue(ranges.isEmpty());

        ranges.add("FAA", "FBB");
        assertEquals(1, ranges.size());
        assertTrue(ranges.contains(new Range<>(String.class, "FAA", true, "FBB", true)));

        ranges.add("FAZ", "FCC");
        assertEquals(1, ranges.size());
        assertTrue(ranges.contains(new Range<>(String.class, "FAA", true, "FCC", true)));

        ranges.add("GAA", "GBB");
        assertEquals(2, ranges.size());

        final Iterator<Range<String>> it = ranges.iterator();
        assertEquals(new Range<>(String.class, "FAA", true, "FCC", true), it.next());
        assertEquals(new Range<>(String.class, "GAA", true, "GBB", true), it.next());
        assertFalse(it.hasNext());
    }

    /**
     * Tests serialization.
     */
    @Test
    public void testSerialization() {
        final RangeSet<Double> ranges = new RangeSet<>(Double.class);
        ranges.add(12.0, 12.5);
        ranges.add(18.0, 18.5);
        ranges.add(19.0, 20.0);
        assertNotSame(ranges, assertSerializedEquals(ranges));
    }

    /**
     * Tests the performance of {@link RangeSet} implementation. This test is not executed
     * in normal Geotk build. We run this test only when the {@link RangeSet} implementation
     * changed, and we want to test the impact of that change on the performance.
     *
     * @throws InterruptedException If the test has been interrupted.
     */
    @Performance
    public void testPerformance() throws InterruptedException {
        final Random r = new Random(5638743);
        for (int p=0; p<10; p++) {
            final long start = System.nanoTime();
            final RangeSet<Integer> set = new RangeSet<>(Integer.class);
            for (int i=0; i<100000; i++) {
                final int lower = r.nextInt(1000000) - 500;
                final int upper = lower + r.nextInt(100) + 1;
                if (r.nextBoolean()) {
                    set.add(lower, upper);
                } else {
                    set.remove(lower, upper);
                }
            }
            final long end = System.nanoTime();
            System.out.println((end - start) / 1E9 + "  " + set.size());
            Thread.sleep(1000);
        }
    }
}
