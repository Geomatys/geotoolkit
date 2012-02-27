/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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

import java.util.HashSet;
import java.util.Random;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link WeakHashSet}. A standard {@link HashSet} object is used for comparison purpose.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 */
public final strictfp class WeakHashSetTest {
    /**
     * Tests the {@link WeakHashSet} using strong references.
     * The tested {@link WeakHashSet} should behave like a standard {@link Set} object.
     */
    @Test
    public void testStrongReferences() {
        final Random random = new Random();
        for (int pass=0; pass<20; pass++) {
            final WeakHashSet<Integer> weakSet = WeakHashSet.newInstance(Integer.class);
            final HashSet<Integer> strongSet = new HashSet<Integer>();
            for (int i=0; i<1000; i++) {
                final Integer value = random.nextInt(500);
                if (random.nextBoolean()) {
                    /*
                     * Tests addition.
                     */
                    final boolean   weakModified = weakSet  .add(value);
                    final boolean strongModified = strongSet.add(value);
                    assertEquals("add:", strongModified, weakModified);
                    if (strongModified) {
                        assertSame("get:", value, weakSet.get(value));
                    } else {
                        assertEquals("get:",  value, weakSet.get(value));
                    }
                } else {
                    /*
                     * Tests remove
                     */
                    final boolean   weakModified = weakSet  .remove(value);
                    final boolean strongModified = strongSet.remove(value);
                    assertEquals("remove:", strongModified, weakModified);
                    assertNull("get:", weakSet.get(value));
                }
                assertEquals("contains:", strongSet.contains(value), weakSet.contains(value));
                assertEquals("equals:", strongSet, weakSet);
            }
        }
    }

    /**
     * Tests the {@link WeakHashSet} using weak references. In this test, we have to keep
     * in mind than some elements in {@code weakSet} may disappear at any time!
     *
     * @throws InterruptedException If the test has been interrupted.
     */
    @Test
    public void testWeakReferences() throws InterruptedException {
        final Random random = new Random();
        for (int pass=0; pass<2; pass++) {
            final WeakHashSet<Integer> weakSet = WeakHashSet.newInstance(Integer.class);
            final HashSet<Integer> strongSet = new HashSet<Integer>();
            for (int i=0; i<500; i++) {
                final Integer value = new Integer(random.nextInt(500)); // Really need new instances
                if (random.nextBoolean()) {
                    /*
                     * Tests addition.
                     */
                    final boolean   weakModified = weakSet  .add(value);
                    final boolean strongModified = strongSet.add(value);
                    if (weakModified) {
                        // If the element was not in the WeakHashSet (i.e. if the garbage
                        // collector has cleared it), then it must not been in HashSet neither
                        // (otherwise GC should not have cleared it).
                        assertTrue("add:", strongModified);
                    } else {
                        assertTrue(value != weakSet.get(value));
                        if (strongModified) {
                            // If the element was already in the WeakHashSet but not in the
                            // HashSet, this is because GC has not cleared it yet. Replace it
                            // by 'value', because if we don't it may be cleared later and the
                            // "contains" test below will fails.
                            //
                            // Note: we don't test if 'remove' below returns 'true', because GC
                            //       may have already done its work since the few previous lines!
                            weakSet.remove(value);
                            assertTrue(weakSet.add(value));
                            assertSame(value, weakSet.get(value));
                        }
                    }
                } else {
                    /*
                     * Test remove
                     */
                    final boolean c = weakSet.contains(value);
                    if (strongSet.remove(value)) {
                        assertTrue("contains:", c);
                    }
                }
                assertTrue("containsAll:", weakSet.containsAll(strongSet));
            }
            // Do our best to lets GC finish its work.
            for (int i=0; i<4; i++) {
                Thread.sleep(50);
                System.gc();
            }
            assertEquals("equals:", strongSet, weakSet);
        }
    }

    /**
     * Tests with array elements.
     */
    @Test
    public void testArray() {
        final WeakHashSet<int[]> weakSet = WeakHashSet.newInstance(int[].class);
        final int[] array = new int[] {2, 5, 3};
        assertTrue (weakSet.add(array));
        assertFalse(weakSet.add(array));
        assertFalse(weakSet.add(array.clone()));
        assertTrue (weakSet.add(new int[] {2, 5, 4}));
        assertSame (array, weakSet.unique(array.clone()));
    }
}
