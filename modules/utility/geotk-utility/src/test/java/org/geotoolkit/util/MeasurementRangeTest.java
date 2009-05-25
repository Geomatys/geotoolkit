/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2006-2009, Open Source Geospatial Foundation (OSGeo)
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

import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.measure.quantity.Length;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.*;
import org.geotoolkit.test.Depend;


/**
 * Tests the {@link MeasurementRange}.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.4
 */
@Depend(RangeTest.class)
public final class MeasurementRangeTest {
    /**
     * Tests unit conversions.
     */
    @Test
    public void testConversion() {
        final MeasurementRange<Float> range = MeasurementRange.create(1000f, 2000f, SI.METRE);
        assertSame(range, range.convertTo(SI.METRE));
        final Unit<Length> KILOMETRE = SI.KILO(SI.METRE);
        assertEquals(MeasurementRange.create(1f, 2f, KILOMETRE), range.convertTo(KILOMETRE));
    }

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
        assertEquals(Integer.class, r1.getElementClass());
        assertEquals(Integer.class, r2.getElementClass());
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
        assertEquals(Double.class, r1.getElementClass());
        assertEquals(Double.class, r2.getElementClass());
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
        assertEquals(Integer.class, r1.getElementClass());
        assertEquals(Double .class, r2.getElementClass());
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
        assertEquals(Double .class, r1.getElementClass());
        assertEquals(Integer.class, r2.getElementClass());
        assertEquals(NumberRange.create(10.0, 30.0), r1.union(r2));
        assertEquals(NumberRange.create(15, 20), r1.intersect(r2));

        r1 = NumberRange.create(10.0, 20.5);
        assertEquals(NumberRange.create(15.0f, 20.5f), r1.intersect(r2));
    }

    /**
     * Tests union and intersection involving a unit conversion.
     */
    @Test
    public void testIntersectWithConversion() {
        final Unit<Length> KILOMETRE = SI.KILO(SI.METRE);
        NumberRange<Float> r1 = MeasurementRange.create(1000f, 2000f, SI.METRE);
        NumberRange<Float> r2 = MeasurementRange.create(1.5f, 3f, KILOMETRE);
        assertEquals(Float.class, r1.getElementClass());
        assertEquals(Float.class, r2.getElementClass());
        assertEquals(MeasurementRange.create(1000f, 3000f, SI.METRE ), r1.union    (r2));
        assertEquals(MeasurementRange.create(1f,    3f,    KILOMETRE), r2.union    (r1));
        assertEquals(MeasurementRange.create(1500f, 2000f, SI.METRE ), r1.intersect(r2));
        assertEquals(MeasurementRange.create(1.5f,  2f,    KILOMETRE), r2.intersect(r1));
    }

    /**
     * Tests {@link MeasurementRange#toString()} method.
     */
    @Test
    public void testToString() {
        final MeasurementRange<Float> range = MeasurementRange.create(10f, 20f, SI.KILO(SI.METRE));
        assertEquals("[10.0, 20.0] km", range.toString());
    }

    /**
     * Tests serialization.
     */
    @Test
    public void testSerialization() {
        final Unit<Length> KILOMETRE = SI.KILO(SI.METRE);
        NumberRange<Float> r1 = MeasurementRange.create(1000f, 2000f, SI.METRE);
        NumberRange<Float> r2 = MeasurementRange.create(1.5f, 3f, KILOMETRE);
        assertNotSame(r1, serialize(r1));
        assertNotSame(r2, serialize(r2));
    }
}
