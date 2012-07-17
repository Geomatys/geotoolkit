/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.util;

import org.junit.*;
import static org.junit.Assert.*;
import org.geotoolkit.test.Depend;


/**
 * Tests the {@link NumberRange}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.06
 *
 * @since 3.06 (derived from 2.4)
 */
@Depend(RangeTest.class)
public final strictfp class NumberRangeTest {
    /**
     * Tests the bounds values of a range of integers.
     */
    @Test
    public void testIntegerBounds() {
        final NumberRange<Integer> range = NumberRange.create(10, 20);
        assertEquals(10, range.getMinimum(     ), 0);
        assertEquals(10, range.getMinimum(true ), 0);
        assertEquals( 9, range.getMinimum(false), 0);
        assertEquals(20, range.getMaximum(     ), 0);
        assertEquals(20, range.getMaximum(true ), 0);
        assertEquals(21, range.getMaximum(false), 0);
    }

    /**
     * Tests union and intersection without units and type change.
     */
    @Test
    public void testIntegerIntersect() {
        NumberRange<Integer> r1 = NumberRange.create(10, 20);
        NumberRange<Integer> r2 = NumberRange.create(15, 30);
        assertTrue (r1.equals(r1));
        assertTrue (r2.equals(r2));
        assertFalse(r1.equals(r2));
        assertEquals(Integer.class, r1.getElementType());
        assertEquals(Integer.class, r2.getElementType());
        assertEquals(NumberRange.create(10, 30), r1.union(r2));
        assertEquals(NumberRange.create(15, 20), r1.intersect(r2));
    }

    /**
     * Tests union and intersection with type change.
     */
    @Test
    public void testDoubleIntersect() {
        NumberRange<Double> r1 = NumberRange.create(10.0, 20.0);
        NumberRange<Double> r2 = NumberRange.create(15.0, 30.0);
        assertEquals(Double.class, r1.getElementType());
        assertEquals(Double.class, r2.getElementType());
        assertEquals(NumberRange.create(10.0, 30.0), r1.union(r2));
        assertEquals(NumberRange.create(15.0, 20.0), r1.intersect(r2));
    }

    /**
     * Tests union and intersection with type change.
     */
    @Test
    public void testIntegerDoubleIntersect() {
        NumberRange<Integer> r1 = NumberRange.create(10, 20);
        NumberRange<Double>  r2 = NumberRange.create(15.0, 30.0);
        assertEquals(Integer.class, r1.getElementType());
        assertEquals(Double .class, r2.getElementType());
        assertEquals(NumberRange.create(10.0, 30.0), r1.union(r2));
        assertEquals(NumberRange.create(15, 20), r1.intersect(r2));

        r2 = NumberRange.create(15.5, 30.0);
        assertEquals(NumberRange.create(15.5f, 20.0f), r1.intersect(r2));
    }

    /**
     * Tests union and intersection with type change.
     */
    @Test
    public void testDoubleIntegerIntersect() {
        NumberRange<Double>  r1 = NumberRange.create(10.0, 20.0);
        NumberRange<Integer> r2 = NumberRange.create(15, 30);
        assertEquals(Double .class, r1.getElementType());
        assertEquals(Integer.class, r2.getElementType());
        assertEquals(NumberRange.create(10.0, 30.0), r1.union(r2));
        assertEquals(NumberRange.create(15, 20), r1.intersect(r2));

        r1 = NumberRange.create(10.0, 20.5);
        assertEquals(NumberRange.create(15.0f, 20.5f), r1.intersect(r2));
    }

    /**
     * Tests the {@link NumberRange#createBestFit} method.
     */
    @Test
    public void testCreateBestFit() {
        assertEquals(NumberRange.create((short) 2, (short) 200),
                NumberRange.createBestFit(2, true, 200.0, true));
    }
}
