/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Little Endian data input and output stream tests.
 *
 * @author Johann Sorel (Geomatys)
 */
public class LEStreamTest {

    private static final float DELTA = 0.0000001f;
    private static final byte[] DATA = new byte[]{
        (byte)0x00, //boolean false
        (byte)0x01, //boolean true
        (byte)0xF2, //ubyte 242
        (byte)0xFA, //byte -6
        (byte)0x01,(byte)0x02,(byte)0x03,(byte)0x04,(byte)0x05, //byte array 1,2,3,4,5
        (byte)0x70,(byte)0xE0, //ushort 57456
        (byte)0x05,(byte)0xC1, //short -16123
        (byte)0x79,(byte)0x00, //char 'y'
        (byte)0xFB,(byte)0x24,(byte)0x0E,(byte)0x2F, //int 789456123
        (byte)0x15,(byte)0xCD,(byte)0x5B,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00, //long 123456789
        (byte)0xB6,(byte)0xD3,(byte)0xA0,(byte)0x43, //float 321.654
        (byte)0x21,(byte)0xB0,(byte)0x72,(byte)0x68,(byte)0x91,(byte)0xDA,(byte)0x8E,(byte)0x40 //double 987.321
    };

    @Test
    public void readTest() throws IOException{

        final LEDataInputStream ds = new LEDataInputStream(new ByteArrayInputStream(DATA));
        assertEquals(0, ds.getPosition());

        //byte types
        assertEquals(false, ds.readBoolean());
        assertEquals(1,     ds.getPosition());
        assertEquals(true,  ds.readBoolean());
        assertEquals(2,     ds.getPosition());
        assertEquals(242,   ds.readUnsignedByte());
        assertEquals(3,     ds.getPosition());
        assertEquals(-6,    ds.readByte());
        assertEquals(4,     ds.getPosition());
        final byte[] array = new byte[5];
        ds.readFully(array);
        assertArrayEquals(new byte[]{1,2,3,4,5}, array);
        assertEquals(9, ds.getPosition());
        //primitive types
        assertEquals(57456, ds.readUnsignedShort());
        assertEquals(11, ds.getPosition());
        assertEquals(-16123, ds.readShort());
        assertEquals(13, ds.getPosition());
        assertEquals('y', ds.readChar());
        assertEquals(15, ds.getPosition());
        assertEquals(789456123, ds.readInt());
        assertEquals(19, ds.getPosition());
        assertEquals(123456789, ds.readLong());
        assertEquals(27, ds.getPosition());
        assertEquals(321.654f, ds.readFloat(),DELTA);
        assertEquals(31, ds.getPosition());
        assertEquals(987.321, ds.readDouble(), DELTA);
        assertEquals(39, ds.getPosition());

    }

    @Test
    public void writeTest() throws IOException{

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final LEDataOutputStream ds = new LEDataOutputStream(out);
        assertEquals(0, ds.getPosition());

        //byte types
        ds.writeBoolean(false);
        assertEquals(1,     ds.getPosition());
        ds.writeBoolean(true);
        assertEquals(2,     ds.getPosition());
        ds.writeByte(242);
        assertEquals(3,     ds.getPosition());
        ds.writeByte(-6);
        assertEquals(4,     ds.getPosition());
        ds.write(new byte[]{1,2,3,4,5});
        assertEquals(9, ds.getPosition());
        //primitive types
        ds.writeShort(57456);
        assertEquals(11, ds.getPosition());
        ds.writeShort(-16123);
        assertEquals(13, ds.getPosition());
        ds.writeChar('y');
        assertEquals(15, ds.getPosition());
        ds.writeInt(789456123);
        assertEquals(19, ds.getPosition());
        ds.writeLong(123456789);
        assertEquals(27, ds.getPosition());
        ds.writeFloat(321.654f);
        assertEquals(31, ds.getPosition());
        ds.writeDouble(987.321);
        assertEquals(39, ds.getPosition());
        ds.flush();

        final byte[] res = out.toByteArray();
        assertArrayEquals(DATA, res);

    }


}
