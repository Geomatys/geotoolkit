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
public final class FloatingPoint extends DataType {

    /**
     * Byte Order. These two non-contiguous bits specify the “endianness” of the bytes in the datatype element.
     * Bit 6 Bit 0 Description
     * 0 0 Byte order is little-endian
     * 0 1 Byte order is big-endian
     * 1 0 Reserved
     * 1 1 Byte order is VAX-endian
     */
    public int byteOrder;
    /**
     * Padding type. Bit 1 is the low bits pad type, bit 2 is the high bits
     * pad type, and bit 3 is the internal bits pad type. If a datum has
     * unused bits at either end or between the sign bit, exponent, or mantissa,
     * then the value of bit 1, 2, or 3 is copied to those locations.
     */
    public int paddingType;
    /**
     * Mantissa Normalization. This 2-bit bit field specifies how the most
     * significant bit of the mantissa is managed.
     * Value Description
     * 0 : No normalization
     * 1 : The most significant bit of the mantissa is always set (except for 0.0).
     * 2 : The most significant bit of the mantissa is not stored, but is implied to be set.
     * 3 : Reserved.
     */
    public int mantissaNormalisation;
    /**
     * Sign Location. This is the bit position of the sign bit. Bits are
     * numbered with the least significant bit zero.
     */
    public int signLocation;
    /**
     * The bit offset of the first significant bit of the floating-point
     * value within the datatype. The bit offset specifies the number of
     * bits “to the right of” the value.
     */
    public int bitOffset;
    /**
     * The number of bits of precision of the floating-point value within
     * the datatype.
     */
    public int bitPrecision;
    /**
     * The bit position of the exponent field. Bits are numbered with the
     * least significant bit number zero.
     */
    public int exponentLocation;
    /**
     * The size of the exponent field in bits.
     */
    public int exponentSize;
    /**
     * The bit position of the mantissa field. Bits are numbered with the
     * least significant bit number zero.
     */
    public int mantissaLocation;
    /**
     * The size of the mantissa field in bits.
     */
    public int mantissaSize;
    /**
     * The bias of the exponent field.
     */
    public int exponentBias;


    //detect if the floating point type is a standard one.
    private static enum KnownType {
        FLOAT32,
        FLOAT64,
        OTHER;
    }
    private KnownType knownType = KnownType.OTHER;

    public FloatingPoint(int byteSize, int classBitFields, HDF5DataInput channel) throws IOException {
        super(byteSize);
        byteOrder = (classBitFields & 0b1) | ((classBitFields & 0b1000000) >> 5);
        paddingType = (classBitFields & 0b1110) >> 1;
        mantissaNormalisation = (classBitFields & 0b110000) >> 4;
        signLocation = (classBitFields & 0b1111111100000000) >> 8;
        bitOffset = channel.readUnsignedShort();
        bitPrecision = channel.readUnsignedShort();
        exponentLocation = channel.readUnsignedByte();
        exponentSize = channel.readUnsignedByte();
        mantissaLocation = channel.readUnsignedByte();
        mantissaSize = channel.readUnsignedByte();
        exponentBias = channel.readInt();

        if (       signLocation     == 63
                && bitOffset        == 0
                && bitPrecision     == 64
                && exponentLocation == 52
                && exponentSize     == 11
                && mantissaLocation == 0
                && mantissaSize     == 52) {
            knownType = KnownType.FLOAT64;
        } else if (signLocation     == 31
                && bitOffset        == 0
                && bitPrecision     == 32
                && exponentLocation == 23
                && exponentSize     == 8
                && mantissaLocation == 0
                && mantissaSize     == 23) {
            knownType = KnownType.FLOAT32;
        }

    }

    @Override
    public Class getValueClass() {
        return switch(knownType) {
            case FLOAT32 -> float.class;
            case FLOAT64 -> double.class;
            default -> double.class;
        };
    }

    @Override
    public Object readData(HDF5DataInput input) throws IOException {
        final ByteOrder previous = input.order();
        switch (byteOrder) {
            case 0:
                //little endian
                input.order(ByteOrder.LITTLE_ENDIAN);
                break;
            case 1:
                //big endian
                input.order(ByteOrder.BIG_ENDIAN);
                break;
            default:
                throw new IOException("Unsupported endian " + byteOrder);
        }

        try {
            switch (knownType) {
                case FLOAT64 :
                    return input.readDouble();
                case FLOAT32 :
                    return input.readFloat();
                default:
                    throw new IOException("Unsupported type " + knownType);
            }
        } finally {
            input.order(previous);
        }
    }
}
