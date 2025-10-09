/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.dggs.h3.internal.shared;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class H3IndexTest {

    /**
     * Values taken from :
     * https://observablehq.com/@nrabinowitz/h3-index-inspector?collection=@nrabinowitz/h3
     */
    @Test
    public void test() {

        { //Standard hexagon cell
            final long cellId = 0x85283473fffffffl;
            assertEquals(1, H3Index.getMode(cellId));
            assertEquals(0, H3Index.getModeDependent(cellId));
            assertEquals(5, H3Index.getResolution(cellId));
            assertEquals(20, H3Index.getBaseCellNumber(cellId));
            assertArrayEquals(new int[]{0,6,4,3,4,7,7,7,7,7,7,7,7,7,7}, H3Index.getIndexDigits(cellId));
            assertFalse(H3Index.isPentagon(cellId));
        }

        { //Hexagon cell with an icosahedron edge crossing one vertex and one edge
            final long cellId = 0x85080013fffffffl;
            assertEquals(1, H3Index.getMode(cellId));
            assertEquals(0, H3Index.getModeDependent(cellId));
            assertEquals(5, H3Index.getResolution(cellId));
            assertEquals(4, H3Index.getBaseCellNumber(cellId));
            assertArrayEquals(new int[]{0,0,0,0,4,7,7,7,7,7,7,7,7,7,7}, H3Index.getIndexDigits(cellId));
            assertFalse(H3Index.isPentagon(cellId));
        }

        { //Hexagon cell with an icosahedron edge crossing two edges
            final long cellId = 0x850802a3fffffffl;
            assertEquals(1, H3Index.getMode(cellId));
            assertEquals(0, H3Index.getModeDependent(cellId));
            assertEquals(5, H3Index.getResolution(cellId));
            assertEquals(4, H3Index.getBaseCellNumber(cellId));
            assertArrayEquals(new int[]{0,0,2,5,0,7,7,7,7,7,7,7,7,7,7}, H3Index.getIndexDigits(cellId));
            assertFalse(H3Index.isPentagon(cellId));
        }

        { //Even-resolution pentagon cell
            final long cellId = 0x860800007ffffffl;
            assertEquals(1, H3Index.getMode(cellId));
            assertEquals(0, H3Index.getModeDependent(cellId));
            assertEquals(6, H3Index.getResolution(cellId));
            assertEquals(4, H3Index.getBaseCellNumber(cellId));
            assertArrayEquals(new int[]{0,0,0,0,0,0,7,7,7,7,7,7,7,7,7}, H3Index.getIndexDigits(cellId));
            assertTrue(H3Index.isPentagon(cellId));
        }

        { //Odd-resolution pentagon cell
            final long cellId = 0x85080003fffffffl;
            assertEquals(1, H3Index.getMode(cellId));
            assertEquals(0, H3Index.getModeDependent(cellId));
            assertEquals(5, H3Index.getResolution(cellId));
            assertEquals(4, H3Index.getBaseCellNumber(cellId));
            assertArrayEquals(new int[]{0,0,0,0,0,7,7,7,7,7,7,7,7,7,7}, H3Index.getIndexDigits(cellId));
            assertTrue(H3Index.isPentagon(cellId));
        }
    }

}
