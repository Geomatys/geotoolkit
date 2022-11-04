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

import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ConstantChannelDataInputTest {

    @Test
    public void testReadSingle() throws IOException {
        final ConstantSeekableByteChannel is = new ConstantSeekableByteChannel(100, new byte[]{5});

        final byte[] array = new byte[200];
        final ByteBuffer bb = ByteBuffer.wrap(array);
        int nb = is.read(bb);
        Assert.assertEquals(100, nb);
        for(int i=  0;i<100;i++) Assert.assertEquals(5, array[i]);
        for(int i=100;i<200;i++) Assert.assertEquals(0, array[i]);
    }

    @Test
    public void testReadPattern() throws IOException {

        { //test read pattern
            final ConstantSeekableByteChannel is = new ConstantSeekableByteChannel(5, new byte[]{5,6,7});
            final byte[] array = new byte[200];
            final ByteBuffer bb = ByteBuffer.wrap(array);
            int nb = is.read(bb);
            Assert.assertEquals(5, nb);
            Assert.assertEquals(5, array[0]);
            Assert.assertEquals(6, array[1]);
            Assert.assertEquals(7, array[2]);
            Assert.assertEquals(5, array[3]);
            Assert.assertEquals(6, array[4]);
            for (int i = 5; i < 200; i++) Assert.assertEquals(0, array[i]);
        }

        { //test read pattern small buffer with offset
            final ConstantSeekableByteChannel is = new ConstantSeekableByteChannel(20, new byte[]{5,6,7,8,9});
            final byte[] array = new byte[5];
            final ByteBuffer bb = ByteBuffer.wrap(array);
            is.position(2);
            int nb = is.read(bb);
            Assert.assertEquals(5, nb);
            Assert.assertEquals(7, array[0]);
            Assert.assertEquals(8, array[1]);
            Assert.assertEquals(9, array[2]);
            Assert.assertEquals(5, array[3]);
            Assert.assertEquals(6, array[4]);
        }
    }
}
