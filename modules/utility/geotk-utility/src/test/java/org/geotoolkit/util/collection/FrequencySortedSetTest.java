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

import java.util.Collections;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link FrequencySortedSet} implementation.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 */
public final strictfp class FrequencySortedSetTest {
    /**
     * A simple case with only two elements, the first one being omitted.
     */
    @Test
    public void testSimple() {
        boolean reverse = false;
        do {
            final FrequencySortedSet<Integer> set = new FrequencySortedSet<>(reverse);
            assertFalse(set.add(12, 0));
            assertTrue (set.add(18, 11));
            assertEquals(Collections.singleton(18), set);
            assertArrayEquals(new int[] {11}, set.frequencies());
        } while ((reverse = !reverse) == true);
    }

    /**
     * Simple test with 2 elements.
     */
    @Test
    public void testTwoElements() {
        final FrequencySortedSet<Integer> set = new FrequencySortedSet<>(true);
        for (int i=0; i<10; i++) {
            if ((i % 3) == 0) {
                set.add(11);
            }
            set.add(9);
        }
        assertEquals(2, set.size());
        assertEquals(Integer.valueOf(9), set.first());
        assertEquals(Integer.valueOf(11), set.last());
        assertArrayEquals(new int[] {10, 4}, set.frequencies());
    }
}
