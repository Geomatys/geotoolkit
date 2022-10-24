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

}
