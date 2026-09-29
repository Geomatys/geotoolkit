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
package org.geotoolkit.hdf.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests of the Fletcher32 checksum filter.
 * <p>
 * The expected values come from an implementation of the reference algorithm
 * validated against the checksums the HDF5 library writes in a filtered file.
 *
 * @author Johann Sorel (Geomatys)
 */
public class Fletcher32Test {

    @Test
    public void testChecksum() {
        assertEquals(0, Fletcher32.checksum(new byte[0], 0));
        assertEquals(640271558, Fletcher32.checksum(
                "abcd".getBytes(StandardCharsets.US_ASCII), 4));
        assertEquals(-858070013, Fletcher32.checksum(sequence(64), 64));
    }

    /**
     * A trailing odd byte counts as the high half of a word.
     */
    @Test
    public void testOddLength() {
        assertEquals(134809092, Fletcher32.checksum(sequence(5), 5));
    }

    /**
     * More than 360 words, which is where the accumulators are reduced.
     */
    @Test
    public void testBeyondReductionBlock() {
        assertEquals(224956803, Fletcher32.checksum(sequence(1000), 1000));
    }

    /**
     * Decoding strips the four checksum bytes and returns the data untouched.
     */
    @Test
    public void testDecodeRoundTrip() throws IOException {
        final Fletcher32 filter = new Fletcher32();
        final byte[] datas = sequence(64);
        final byte[] encoded = filter.encode(datas);
        assertEquals(datas.length + 4, encoded.length);
        assertArrayEquals(datas, filter.decode(encoded));
    }

    private static byte[] sequence(int length) {
        final byte[] datas = new byte[length];
        for (int i = 0; i < length; i++) {
            datas[i] = (byte) i;
        }
        return datas;
    }
}
