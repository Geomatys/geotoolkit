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
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ConstantInputStreamTest {

    @Test
    public void testReadSingle() throws IOException {
        final InputStream is = ConstantInputStream.create(100, new byte[]{5});

        final byte[] array = new byte[200];
        int nb = is.read(array, 100, 26);
        Assert.assertEquals(26, nb);
        for(int i=  0;i<100;i++) Assert.assertEquals(0, array[i]);
        for(int i=100;i<126;i++) Assert.assertEquals(5, array[i]);
        for(int i=126;i<200;i++) Assert.assertEquals(0, array[i]);
    }

    @Test
    public void testReadPattern() throws IOException {

        { //test read at offset
            final InputStream is = ConstantInputStream.create(5, new byte[]{5,6,7});
            final byte[] array = new byte[200];
            int nb = is.read(array, 100, 26);
            Assert.assertEquals(5, nb);
            for (int i =  0; i < 100; i++) Assert.assertEquals(0, array[i]);
            Assert.assertEquals(5, array[100]);
            Assert.assertEquals(6, array[101]);
            Assert.assertEquals(7, array[102]);
            Assert.assertEquals(5, array[103]);
            Assert.assertEquals(6, array[104]);
            for (int i = 105; i < 200; i++) Assert.assertEquals(0, array[i]);
        }

        { //test read at offset+1
            final InputStream is = ConstantInputStream.create(5, new byte[]{5,6,7});
            final byte[] array = new byte[200];
            is.read();// displace it
            int nb = is.read(array, 100, 26);
            Assert.assertEquals(4, nb);
            for (int i =  0; i < 100; i++) Assert.assertEquals(0, array[i]);
            Assert.assertEquals(6, array[100]);
            Assert.assertEquals(7, array[101]);
            Assert.assertEquals(5, array[102]);
            Assert.assertEquals(6, array[103]);
            for (int i = 104; i < 200; i++) Assert.assertEquals(0, array[i]);
        }
    }
}
