/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.hdf.io;

import java.nio.charset.StandardCharsets;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests of the Jenkins lookup3 checksum used by every version 2 HDF5 structure.
 *
 * @author Johann Sorel (Geomatys)
 */
public class ChecksumsTest {

    /**
     * The length is mixed into the initial state, so an empty input still has a
     * non zero checksum. This is the degenerate case the switch must not fall into.
     */
    @Test
    public void testEmpty() {
        assertEquals(0xdeadbeef, Checksums.checksum(new byte[0]));
    }

    /**
     * Inputs shorter than one 12 bytes block, exercising the fall through switch.
     */
    @Test
    public void testShortInputs() {
        assertEquals(238646833, Checksums.checksum("abc".getBytes(StandardCharsets.US_ASCII)));
        assertEquals(868048138, Checksums.checksum("OHDR".getBytes(StandardCharsets.US_ASCII)));
    }

    /**
     * Exactly one block, then one byte more : the boundary between the loop and
     * the trailing switch, where an off by one in the loop condition shows up.
     */
    @Test
    public void testBlockBoundary() {
        assertEquals(1581950355, Checksums.checksum(sequence(12)));
        assertEquals(-1130534890, Checksums.checksum(sequence(13)));
    }

    /**
     * Several blocks plus a remainder.
     */
    @Test
    public void testLongInput() {
        assertEquals(982000196, Checksums.checksum(sequence(25)));
        assertEquals(393676113, Checksums.checksum(
                "Four score and seven years ago".getBytes(StandardCharsets.US_ASCII)));
    }

    /**
     * Hashing a range must give the same result as hashing a copy of that range.
     */
    @Test
    public void testOffsetAndLength() {
        final byte[] datas = new byte[40];
        for (int i = 0; i < datas.length; i++) {
            datas[i] = (byte) (i + 7);
        }
        final byte[] copy = new byte[25];
        System.arraycopy(datas, 5, copy, 0, 25);
        assertEquals(Checksums.checksum(copy), Checksums.hashLittle(datas, 5, 25, 0));
    }

    private static byte[] sequence(int length) {
        final byte[] datas = new byte[length];
        for (int i = 0; i < length; i++) {
            datas[i] = (byte) i;
        }
        return datas;
    }
}
