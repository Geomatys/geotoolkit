/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.math;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link Vector} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 */
public final class VectorTest {
    /**
     * Tests {@link ArrayVector} backed by an array of primitive type.
     */
    @Test
    public void testPrimitiveTypeArray() {
        final float[] array = new float[400];
        for (int i=0; i<array.length; i++) {
            array[i] = (i + 100) * 10;
        }
        Vector vector = Vector.create(array);
        assertTrue(vector instanceof ArrayVector);
        assertSame(vector, Vector.create(vector));
        assertEquals(array.length, vector.size());
        assertEquals(Float.class, vector.getElementType());
        /*
         * Tests element values.
         */
        for (int i=0; i<array.length; i++) {
            assertEquals(array[i], vector.floatValue (i), 0);
            assertEquals(array[i], vector.doubleValue(i), 0);
        }
        /*
         * Tests exception.
         */
        try {
            vector.floatValue(array.length);
            fail("Expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // This is the expected exception.
        }
        try {
            vector.longValue(0);
            fail("Expected a ClassCastException");
        } catch (ClassCastException e) {
            // This is the expected exception.
        }
        /*
         * Tests subvector in the range [100:2:298].
         */
        vector = vector.subList(100, 2, 300);
        assertEquals(100, vector.size());
        for (int i=0; i<100; i++) {
            assertEquals(array[i*2 + 100], vector.floatValue (i), 0);
        }
        try {
            vector.floatValue(100);
            fail("Expected an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // This is the expected exception.
        }
        /*
         * Tests subvector at specific indexes.
         */
        vector = vector.subvector(10, 20, 25);
        assertEquals(3, vector.size());
        assertEquals(array[120], vector.floatValue(0), 0);
        assertEquals(array[140], vector.floatValue(1), 0);
        assertEquals(array[150], vector.floatValue(2), 0);
    }
}
