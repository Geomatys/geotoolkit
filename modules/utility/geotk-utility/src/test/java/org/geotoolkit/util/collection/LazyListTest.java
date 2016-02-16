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

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link LazyList} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 */
public final strictfp class LazyListTest extends org.geotoolkit.test.TestBase {
    /**
     * Tests with random integer values.
     */
    @Test
    public void testRandom() {
        /*
         * Fills a standard ArrayList with random values. This array
         * list will be used as a reference for comparison purpose.
         */
        final Random random = new Random(583912);
        final List<Integer> reference = new ArrayList<>();
        for (int i=0; i<400; i++) {
            reference.add(random.nextInt(1000));
        }
        /*
         * Creates a lazy list and compare some values at random index.
         * We tests only in the first half of the list in order to keep
         * some room for testing the iterator later.
         */
        final List<Integer> lazy = new LazyList<>(reference.iterator());
        final int size = reference.size();
        assertFalse(lazy.isEmpty());
        for (int i=0; i<200; i++) {
            final int index = random.nextInt(size / 2);
            assertEquals(reference.get(index), lazy.get(index));
        }
        /*
         * AbstractList.equals(Object) is implemented using iterator,
         * so following lines test indirectly the iterator.
         */
        assertTrue(lazy.equals(reference));
        assertTrue(reference.equals(lazy));
        assertEquals(size, lazy.size());
    }
}
