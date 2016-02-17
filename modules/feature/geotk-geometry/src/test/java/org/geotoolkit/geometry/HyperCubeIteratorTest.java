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

import static org.junit.Assert.*;
import static org.geotoolkit.geometry.HyperCubeIterator.*;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class HyperCubeIteratorTest extends org.geotoolkit.test.TestBase {

    @Test
    public void iterate2DTest(){

        final int[] lower = {0,0};
        final int[] upper = {11,3};

        final HyperCubeIterator ite = new HyperCubeIterator(lower, upper, new int[]{5,2});

        //first row
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{ 0,0}, new int[]{ 5,2}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{ 5,0}, new int[]{10,2}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{10,0}, new int[]{11,2}), ite.next());

        //second row
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{ 0,2}, new int[]{ 5,3}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{ 5,2}, new int[]{10,3}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{10,2}, new int[]{11,3}), ite.next());

        assertFalse(ite.hasNext());

    }

    @Test
    public void iterate2DOffsetTest(){

        final int[] lower = {4,1};
        final int[] upper = {11,3};

        final HyperCubeIterator ite = new HyperCubeIterator(lower, upper, new int[]{5,2});

        //first row
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{ 4,1}, new int[]{ 9,3}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{ 9,1}, new int[]{11,3}), ite.next());

        assertFalse(ite.hasNext());

    }

    @Test
    public void iterate3DTest(){

        final int[] lower = {0,0,0};
        final int[] upper = {10,2,3};

        final HyperCubeIterator ite = new HyperCubeIterator(lower, upper, new int[]{5,1,2});

        //first slice
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{ 0,0,0}, new int[]{ 5,1,2}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{ 5,0,0}, new int[]{10,1,2}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{ 0,1,0}, new int[]{ 5,2,2}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{ 5,1,0}, new int[]{10,2,2}), ite.next());

        //second slice
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{ 0,0,2}, new int[]{ 5,1,3}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{ 5,0,2}, new int[]{10,1,3}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{ 0,1,2}, new int[]{ 5,2,3}), ite.next());
        assertTrue(ite.hasNext());
        assertEquals(new HyperCube(new int[]{ 5,1,2}, new int[]{10,2,3}), ite.next());

        assertFalse(ite.hasNext());

    }


}
