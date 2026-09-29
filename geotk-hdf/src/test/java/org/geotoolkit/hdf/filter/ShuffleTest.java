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
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ShuffleTest {

    @Test
    public void testEncode() throws IOException {
        final byte[] in = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        final Shuffle filter = new Shuffle(3);
        final byte[] out = filter.encode(in);
        Assert.assertArrayEquals(out, new byte[]{1, 4, 7, 2, 5, 8, 3, 6, 9});
    }

    @Test
    public void testDecode() throws IOException {
        final byte[] in = new byte[]{1, 4, 7, 2, 5, 8, 3, 6, 9};
        final Shuffle filter = new Shuffle(3);
        final byte[] out = filter.decode(in);
         Assert.assertArrayEquals(out, new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
    }

    /**
     * Shuffling and un-shuffling are the same permutation only when there are as
     * many elements as there are bytes per element, which is the case of the two
     * tests above. This one is not square, so it tells the two apart.
     */
    @Test
    public void testEncodeIsNotDecode() throws IOException {
        final byte[] in = new byte[]{1, 2, 3, 4, 5, 6};
        final Shuffle filter = new Shuffle(3);
        Assert.assertArrayEquals(new byte[]{1, 4, 2, 5, 3, 6}, filter.encode(in));
        Assert.assertArrayEquals(in, filter.decode(filter.encode(in)));
    }

    /**
     * Element sizes of 2, 4 and 8 bytes have their own decoding path.
     */
    @Test
    public void testSpecializedElementSizes() throws IOException {
        Assert.assertArrayEquals(
                new byte[]{1, 3, 5, 7, 2, 4, 6, 8},
                new Shuffle(2).encode(new byte[]{1, 2, 3, 4, 5, 6, 7, 8}));
        Assert.assertArrayEquals(
                new byte[]{1, 2, 3, 4, 5, 6, 7, 8},
                new Shuffle(2).decode(new byte[]{1, 3, 5, 7, 2, 4, 6, 8}));

        Assert.assertArrayEquals(
                new byte[]{1, 5, 9, 2, 6, 10, 3, 7, 11, 4, 8, 12},
                new Shuffle(4).encode(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}));
        Assert.assertArrayEquals(
                new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12},
                new Shuffle(4).decode(new byte[]{1, 5, 9, 2, 6, 10, 3, 7, 11, 4, 8, 12}));

        final byte[] eight = new byte[24];
        for (int i = 0; i < eight.length; i++) eight[i] = (byte) (i + 1);
        Assert.assertArrayEquals(
                new byte[]{1, 9, 17, 2, 10, 18, 3, 11, 19, 4, 12, 20,
                           5, 13, 21, 6, 14, 22, 7, 15, 23, 8, 16, 24},
                new Shuffle(8).encode(eight));
        Assert.assertArrayEquals(eight, new Shuffle(8).decode(new Shuffle(8).encode(eight)));
    }

    /**
     * Bytes that do not fill a whole element take no part in the reordering.
     */
    @Test
    public void testTrailingBytes() throws IOException {
        final byte[] in = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
        final Shuffle filter = new Shuffle(4);
        final byte[] shuffled = filter.encode(in);
        Assert.assertArrayEquals(
                new byte[]{1, 5, 9, 2, 6, 10, 3, 7, 11, 4, 8, 12, 13, 14}, shuffled);
        Assert.assertArrayEquals(in, filter.decode(shuffled));
    }

    /**
     * An element size below two, or fewer bytes than one element, leaves the
     * data untouched.
     */
    @Test
    public void testNoReordering() throws IOException {
        final byte[] in = new byte[]{1, 2, 3};
        Assert.assertArrayEquals(in, new Shuffle(1).decode(in));
        Assert.assertArrayEquals(in, new Shuffle(0).decode(in));
        Assert.assertArrayEquals(in, new Shuffle(8).decode(in));
        Assert.assertArrayEquals(new byte[0], new Shuffle(4).decode(new byte[0]));
    }

    /**
     * Round trip over the specialized and the generic paths, with lengths that
     * are not multiples of the element size.
     */
    @Test
    public void testRoundTrip() throws IOException {
        final Random random = new Random(42);
        for (int size : new int[]{2, 3, 4, 5, 8, 12, 16}) {
            for (int length : new int[]{0, 1, size, size * 7, size * 13 + 5, 1000}) {
                final byte[] in = new byte[length];
                random.nextBytes(in);
                final Shuffle filter = new Shuffle(size);
                Assert.assertArrayEquals("size=" + size + " length=" + length,
                        in, filter.decode(filter.encode(in)));
            }
        }
    }
}
