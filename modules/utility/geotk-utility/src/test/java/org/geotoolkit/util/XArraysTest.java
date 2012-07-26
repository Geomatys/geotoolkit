/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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


/**
 * Tests the {@link XArrays} utility methods.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.04
 */
public final strictfp class XArraysTest {
    /**
     * Tests {@link XArrays#removeDuplicated(Object[])}.
     *
     * @since 3.20
     */
    @Test
    public void testRemoveDuplicated() {
        final Integer[] array = new Integer[] {2, 8, 4, 8, 1, 2, 8};
        assertArrayEquals(new Integer[] {2, 8, 4, 1},
                XArrays.resize(array, XArrays.removeDuplicated(array)));
    }

    /**
     * Tests {@link XArrays#reverse(int[])}.
     * The test uses an array of even length, then an array of odd length.
     *
     * @since 3.20
     */
    @Test
    public void testReverse() {
        int[] array = new int[] {2, 4, 8, 10};
        XArrays.reverse(array);
        assertArrayEquals(new int[] {10, 8, 4, 2}, array);

        array = new int[] {2, 4, 8, 10, 11};
        XArrays.reverse(array);
        assertArrayEquals(new int[] {11, 10, 8, 4, 2}, array);
    }

    /**
     * Tests {@link XArrays#unionSorted(int[], int[])}.
     */
    @Test
    public void testUnionSorted() {
        final int[] array1 = new int[] {2, 4, 6, 9, 12};
        final int[] array2 = new int[] {1, 2, 3, 12, 13, 18, 22};
        final int[] union = XArrays.unionSorted(array1, array2);
        assertArrayEquals(new int[] {1, 2, 3, 4, 6, 9, 12, 13, 18, 22}, union);
    }
}
