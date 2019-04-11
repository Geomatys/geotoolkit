/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.geometry;

import org.geotoolkit.geometry.HyperCubeIterator.HyperCube;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class HyperCubeIteratorTest extends org.geotoolkit.test.TestBase {

    @Test
    public void iterate2DTest(){

        final long[] lower = {0,0};
        final long[] upper = {11,3};

        final HyperCubeIterator ite = new HyperCubeIterator(lower, upper, new int[]{5,2});

        //first row
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{ 0,0}, new long[]{ 5,2}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{ 5,0}, new long[]{10,2}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{10,0}, new long[]{11,2}), ite.next());

        //second row
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{ 0,2}, new long[]{ 5,3}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{ 5,2}, new long[]{10,3}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{10,2}, new long[]{11,3}), ite.next());

        assertFalse(ite.hasNext());

    }

    @Test
    public void iterate2DOffsetTest(){

        final long[] lower = {4,1};
        final long[] upper = {11,3};

        final HyperCubeIterator ite = new HyperCubeIterator(lower, upper, new int[]{5,2});

        //first row
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{ 4,1}, new long[]{ 9,3}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{ 9,1}, new long[]{11,3}), ite.next());

        assertFalse(ite.hasNext());

    }

    @Test
    public void iterate3DTest(){

        final long[] lower = {0,0,0};
        final long[] upper = {10,2,3};

        final HyperCubeIterator ite = new HyperCubeIterator(lower, upper, new int[]{5,1,2});

        //first slice
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{ 0,0,0}, new long[]{ 5,1,2}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{ 5,0,0}, new long[]{10,1,2}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{ 0,1,0}, new long[]{ 5,2,2}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{ 5,1,0}, new long[]{10,2,2}), ite.next());

        //second slice
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{ 0,0,2}, new long[]{ 5,1,3}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{ 5,0,2}, new long[]{10,1,3}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{ 0,1,2}, new long[]{ 5,2,3}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new long[]{ 5,1,2}, new long[]{10,2,3}), ite.next());

        assertFalse(ite.hasNext());

    }


}
