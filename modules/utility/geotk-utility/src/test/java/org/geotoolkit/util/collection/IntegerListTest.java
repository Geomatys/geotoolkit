/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.*;
import org.junit.*;
import static org.apache.sis.test.Assert.*;
import static java.lang.StrictMath.*;


/**
 * Tests {@link IntegerList} implementations.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.5
 */
public final strictfp class IntegerListTest {
    /**
     * The random number generator used for this test suite.
     */
    private final Random random = new Random(1241962316404811189L);

    /**
     * The list of integers.
     */
    private IntegerList list;

    /**
     * Writes values and read them again for making sure they are the expected ones.
     *
     * @param maximalValue The maximal value allowed.
     */
    private void testReadWrite(final int maximalValue) {
        final int length = 400;
        // Use half the lenght as initial capacity in order to test dynamic resizing.
        list = new IntegerList(length / 2, maximalValue);
        assertTrue(list.maximalValue() >= maximalValue);
        final List<Integer> copy = new ArrayList<>(length);
        for (int i=0; i<length; i++) {
            assertEquals(i, list.size());
            final Integer value = nextInt(maximalValue);
            assertTrue(copy.add(value));
            assertTrue(list.add(value));
        }
        assertEquals(copy, list);
        assertEquals(copy.hashCode(), list.hashCode());
        /*
         * Overwrite 1/10 of the values.
         */
        for (int i=0; i<length; i+=10) {
            final Integer value = nextInt(maximalValue);
            final Integer old = copy.set(i, value);
            assertNotNull(old);
            assertEquals(old, list.set(i, value));
        }
        for (int i=0; i<length; i++) {
            assertEquals(String.valueOf(i), copy.get(i), list.get(i));
        }
        assertEquals(copy, list);
        assertEquals(copy.hashCode(), list.hashCode());
        assertNotSame(list, assertSerializedEquals(list));
        /*
         * Tests cloning and removal of values.
         */
        final List<Integer> clone = list.clone();
        assertEquals(copy, clone);
        assertEquals(copy.remove(100), clone.remove(100));
        assertEquals(copy, clone);
        copy .subList(128, 256).clear();
        clone.subList(128, 256).clear();
        assertEquals(copy, clone);
    }

    /**
     * Returns the next number from the random number generator.
     */
    private int nextInt(final int maximalValue) {
        if (maximalValue == Integer.MAX_VALUE) {
            return abs(random.nextInt());
        } else {
            return random.nextInt(maximalValue + 1);
        }
    }

    /**
     * Tests the fill value using the existing list, which is assumed
     * already filled with random values prior this method call.
     */
    private void testFill(final int value) {
        assertEquals(400, list.size());
        final Set<Integer> set = new HashSet<>();
        list.fill(value);
        set.addAll(list);
        assertEquals(Collections.singleton(value), set);
        list.fill(0);
        set.clear();
        set.addAll(list);
        assertEquals(Collections.singleton(0), set);
    }

    /**
     * Tests with a maximal value of 1.
     */
    @Test
    public void test1() {
        testReadWrite(1);
        testFill(1);
    }

    /**
     * Tests with a maximal value of 2.
     */
    @Test
    public void test2() {
        testReadWrite(2);
        testFill(2);
    }

    /**
     * Tests with a maximal value of 3.
     */
    @Test
    public void test3() {
        testReadWrite(3);
        testFill(3);
    }

    /**
     * Tests with a maximal value of 10.
     */
    @Test
    public void test10() {
        testReadWrite(10);
        testFill(10);
    }

    /**
     * Tests with a maximal value of 100.
     */
    @Test
    public void test100() {
        testReadWrite(100);
        assertEquals(400, list.size());
        final int old100 = list.getInteger(100);
        final int old101 = list.getInteger(101);
        assertFalse(0 == old100);
        assertFalse(0 == old101);
        list.resize(101);
        assertEquals(old100, list.getInteger(100));
        list.resize(200);
        assertEquals(200, list.size());
        assertEquals(old100, list.getInteger(100));
        assertEquals(0, list.getInteger(101));
        for (int i=101; i<200; i++) {
            assertEquals(0, list.getInteger(i));
        }
        list.resize(400);
        testFill(100);
    }

    /**
     * Tests with a maximal value of 100000.
     */
    @Test
    public void test100000() {
        testReadWrite(100000);
        testFill(17);
    }

    /**
     * Tests with a maximal value of {@value Integer#MAX_VALUE}.
     */
    @Test
    public void testMax() {
        testReadWrite(Integer.MAX_VALUE);
        testFill(17);
    }
}
