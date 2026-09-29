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
package org.geotoolkit.hdf.datatype;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.hdf.io.HDF5ChannelDataInput;
import org.geotoolkit.hdf.io.HDF5DataInput;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests of the fixed point datatype, and of the agreement between the class a
 * datatype announces and the class of the values it decodes.
 *
 * @author Johann Sorel (Geomatys)
 */
public class FixedPointTest {

    /**
     * Build the bytes of a fixed point Datatype message, as an object header
     * would hold them.
     *
     * @param signed whether the values are signed
     * @param byteSize size of an element in bytes
     * @param bitPrecision number of significant bits
     */
    private static byte[] datatypeMessage(boolean signed, int byteSize, int bitPrecision) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        //class 0 (fixed point) and version 1
        out.write((1 << 4) | 0);
        //class bit fields on 3 bytes : bit 0 byte order (0 little endian), bit 3 signed
        out.write(signed ? 0b1000 : 0);
        out.write(0);
        out.write(0);
        //element size on 4 bytes
        out.write(byteSize);
        out.write(0);
        out.write(0);
        out.write(0);
        //bit offset on 2 bytes
        out.write(0);
        out.write(0);
        //bit precision on 2 bytes
        out.write(bitPrecision & 0xFF);
        out.write((bitPrecision >>> 8) & 0xFF);
        return out.toByteArray();
    }

    private static byte[] concat(byte[] a, byte[] b) {
        final byte[] all = new byte[a.length + b.length];
        System.arraycopy(a, 0, all, 0, a.length);
        System.arraycopy(b, 0, all, a.length, b.length);
        return all;
    }

    /**
     * A signed 16 bits array must decode to a short[].
     * <p>
     * This used to fail with "argument type mismatch" because the datatype
     * announced short.class while decoding values through a method returning an
     * int, which boxed them to Integer.
     */
    @Test
    public void testSignedShortArray() throws IOException, DataStoreException {
        //values -2, -1, 0, 1, 32767 as little endian 16 bits
        final byte[] values = new byte[]{
            (byte) 0xFE, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF,
            (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x00,
            (byte) 0xFF, (byte) 0x7F};
        try (HDF5DataInput channel = new HDF5ChannelDataInput(
                concat(datatypeMessage(true, 2, 16), values), null)) {
            final DataType type = DataType.readMessageType(channel);
            assertEquals(short.class, type.getValueClass());
            final Object decoded = type.readData(channel, new int[]{5}, null);
            assertEquals(short[].class, decoded.getClass());
            assertArrayEquals(new short[]{-2, -1, 0, 1, 32767}, (short[]) decoded);
        }
    }

    /**
     * The scalar form must agree with the announced class too.
     */
    @Test
    public void testSignedShortScalar() throws IOException, DataStoreException {
        try (HDF5DataInput channel = new HDF5ChannelDataInput(
                concat(datatypeMessage(true, 2, 16), new byte[]{(byte) 0xFE, (byte) 0xFF}), null)) {
            final DataType type = DataType.readMessageType(channel);
            final Object value = type.readData(channel);
            assertEquals(Short.class, value.getClass());
            assertEquals((short) -2, value);
        }
    }

    @Test
    public void testSignedByteArray() throws IOException, DataStoreException {
        try (HDF5DataInput channel = new HDF5ChannelDataInput(
                concat(datatypeMessage(true, 1, 8), new byte[]{(byte) 0xFF, 0x00, 0x7F}), null)) {
            final DataType type = DataType.readMessageType(channel);
            assertEquals(byte.class, type.getValueClass());
            final Object decoded = type.readData(channel, new int[]{3}, null);
            assertArrayEquals(new byte[]{-1, 0, 127}, (byte[]) decoded);
        }
    }

    @Test
    public void testSignedIntArray() throws IOException, DataStoreException {
        final byte[] values = new byte[]{
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0x2A, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        try (HDF5DataInput channel = new HDF5ChannelDataInput(
                concat(datatypeMessage(true, 4, 32), values), null)) {
            final DataType type = DataType.readMessageType(channel);
            assertEquals(int.class, type.getValueClass());
            assertArrayEquals(new int[]{-1, 42}, (int[]) type.readData(channel, new int[]{2}, null));
        }
    }

    /**
     * An unsigned 16 bits value does not fit in a short, so it widens to int.
     */
    @Test
    public void testUnsignedShortArray() throws IOException, DataStoreException {
        try (HDF5DataInput channel = new HDF5ChannelDataInput(
                concat(datatypeMessage(false, 2, 16), new byte[]{(byte) 0xFF, (byte) 0xFF}), null)) {
            final DataType type = DataType.readMessageType(channel);
            assertEquals(int.class, type.getValueClass());
            assertArrayEquals(new int[]{65535}, (int[]) type.readData(channel, new int[]{1}, null));
        }
    }

    /**
     * Arrays of rank three and above used to come back filled with nulls because
     * the reader only handled ranks one and two.
     */
    @Test
    public void testThreeDimensionalArray() throws IOException, DataStoreException {
        final byte[] values = new byte[2 * 2 * 2];
        for (int i = 0; i < values.length; i++) {
            values[i] = (byte) (i + 1);
        }
        try (HDF5DataInput channel = new HDF5ChannelDataInput(
                concat(datatypeMessage(true, 1, 8), values), null)) {
            final DataType type = DataType.readMessageType(channel);
            final byte[][][] decoded = (byte[][][]) type.readData(channel, new int[]{2, 2, 2}, null);
            //the last dimension changes fastest
            assertArrayEquals(new byte[]{1, 2}, decoded[0][0]);
            assertArrayEquals(new byte[]{3, 4}, decoded[0][1]);
            assertArrayEquals(new byte[]{5, 6}, decoded[1][0]);
            assertArrayEquals(new byte[]{7, 8}, decoded[1][1]);
        }
    }
}
