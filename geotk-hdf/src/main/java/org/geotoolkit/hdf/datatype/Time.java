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
 * Format is obscur, specification don't say anything on the bits definition or
 * calendar.
 * <p>
 * In HDF-5 source code version 1.12.2 file : H5Odtype.c ligne 1275 we can see
 * the fields is two bytes long.
 * <p>
 * In HDF-5 source code version 1.12.2 file : H5Tpublic.h ligne 1791 there is a note : <br>
 * Unsupported datatype: The time datatype class, #H5T_TIME,
 * is not supported. If #H5T_TIME is used, the resulting data will
 * be readable and modifiable only on the originating computing
 * platform; it will not be portable to other platforms.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Time extends DataType {

    /**
     * Byte Order. If zero, byte order is little-endian; otherwise, byte
     * order is big endian.
     */
    public final ByteOrder byteOrder;
    /**
     * The number of bits of precision of the time value.
     */
    public final int bitPrecision;

    public Time(int byteSize, int classBitFields, HDF5DataInput channel) throws IOException {
        super(byteSize);
        byteOrder = (classBitFields & 0b1) == 0 ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        bitPrecision = channel.readUnsignedShort();
    }

    @Override
    public Class getValueClass() {
        return int.class;
    }

    @Override
    public Object readData(HDF5DataInput input, int ... compoundindexes) throws IOException {
        return input.readUnsignedShort();
    }
}
