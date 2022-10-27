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

import java.io.IOException;
import java.nio.ByteOrder;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class BitField extends DataType {

    /**
     * Byte Order. If zero, byte order is little-endian; otherwise, byte order
     * is big endian.
     */
    private ByteOrder byteOrder;
    /**
     * Padding type. Bit 1 is the lo_pad type and bit 2 is the hi_pad type.
     * If a datum has unused bits at either end, then the lo_pad or hi_pad bit
     * is copied to those locations.
     */
    private int paddingType;
    /**
     * The bit offset of the first significant bit of the bit field within the
     * datatype. The bit offset specifies the number of bits “to the right of”
     * the value.
     */
    public final int bitOffset;
    /**
     * The number of bits of precision of the bit field within the datatype.
     */
    public final int bitPrecision;

    public BitField(int byteSize, int classBitFields, HDF5DataInput channel) throws IOException {
        super(byteSize);
        byteOrder = (classBitFields & 0b1) == 0 ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        paddingType = (classBitFields & 0b110) >> 1;

        bitOffset = channel.readUnsignedShort();
        bitPrecision = channel.readUnsignedShort();
    }

    @Override
    public Class getValueClass() {
        return Long.class;
    }

    @Override
    public Object readData(HDF5DataInput input) throws IOException {
        final ByteOrder previous = input.order();
        input.order(byteOrder);
        try {
            long position1 = input.getStreamPosition();
            input.readBits(bitOffset);
            long value = input.readBits(bitPrecision);
            input.skipRemainingBits();
            long position2 = input.getStreamPosition();
            long remain = getByteSize() - (position2 - position1);
            if (remain > 0) {
                input.skipFully((int) remain);
            } else if (remain < 0) {
                throw new IOException();
            }

            return value;
        } finally {
            input.order(previous);
        }


    }
}
