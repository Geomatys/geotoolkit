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
public final class FixedPoint extends DataType {

    /**
     * Byte Order. If zero, byte order is little-endian; otherwise,
     * byte order is big endian.
     */
    public final ByteOrder byteOrder;
    /**
     * Padding type. Bit 1 is the lo_pad bit and bit 2 is the hi_pad bit.
     * If a datum has unused bits at either end, then the lo_pad or hi_pad
     * bit is copied to those locations.
     */
    public final int paddingType;
    /**
     * Signed. If this bit is set then the fixed-point number is in 2’s
     * complement form.
     */
    public final boolean signed;
    /**
     * The bit offset of the first significant bit of the fixed-point value
     * within the datatype. The bit offset specifies the number of bits
     * “to the right of” the value (which are set to the lo_pad bit value).
     */
    public final int byteOffset;
    /**
     * The number of bits of precision of the fixed-point value within the
     * datatype. This value, combined with the datatype element’s size and
     * the Bit Offset field specifies the number of bits “to the left of”
     * the value (which are set to the hi_pad bit value).
     */
    public final int bytePrecision;

    //detect if the fixed point type is a standard one.
    private static enum KnownType {
        EMPTY,
        INT8,
        INT16,
        INT32,
        INT64,
        UINT8,
        UINT16,
        UINT32,
        OTHER;
    }
    private KnownType knownType = KnownType.OTHER;

    public FixedPoint(int byteSize, int classBitFields, HDF5DataInput channel) throws IOException {
        super(byteSize);
        byteOrder = (classBitFields & 0b1) == 0 ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        paddingType = (classBitFields & 0b110) >> 1;
        signed = ((classBitFields & 0b1000) >> 3) != 0;
        byteOffset = channel.readUnsignedShort();
        bytePrecision = channel.readUnsignedShort();

        if (byteOffset == 0) {
            switch (bytePrecision) {
                case 8: knownType = signed ? KnownType.INT8 : KnownType.UINT8; break;
                case 16: knownType = signed ? KnownType.INT16 : KnownType.UINT16; break;
                case 32: knownType = signed ? KnownType.INT32 : KnownType.UINT32; break;
                case 64: knownType = KnownType.INT64; break;
                default:
                    throw new IOException("Unsupported fixed point structure");
            }
        }
    }

    @Override
    public Class getValueClass() {
        return switch(knownType) {
            case INT8 -> byte.class;
            case INT16 -> short.class;
            case INT32 -> int.class;
            case INT64 -> long.class;
            case UINT8 -> int.class;
            case UINT16 -> int.class;
            case UINT32 -> long.class;
            default -> long.class;
        };
    }

    @Override
    public Object readData(HDF5DataInput input) throws IOException {
        final ByteOrder previous = input.order();
        input.order(byteOrder);
        try {
            switch (knownType) {
                case INT8 :
                    return input.readByte();
                case INT16 :
                    return input.readShort();
                case INT32 :
                    return input.readInt();
                case INT64 :
                    return input.readLong();
                case UINT8 :
                    return input.readUnsignedByte();
                case UINT16 :
                    return input.readUnsignedShort();
                case UINT32 :
                    return input.readUnsignedInt();
                default:
                    throw new IOException("Unsupported type " + knownType);
            }
        } finally {
            input.order(previous);
        }
    }
}
