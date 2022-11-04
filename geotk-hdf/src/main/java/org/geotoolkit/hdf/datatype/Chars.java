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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Chars extends DataType {

    /**
     * Padding type. This four-bit value determines the type of padding to
     * use for the string. The values are:
     * Value Description
     * 0 : Null Terminate: A zero byte marks the end of the string and is
     * guaranteed to be present after converting a long string to a short
     * string. When converting a short string to a long string the value
     * is padded with additional null characters as necessary.
     * 1 : Null Pad: Null characters are added to the end of the value during
     * conversions from short values to long values but conversion in the
     * opposite direction simply truncates the value.
     * 2 : Space Pad: Space characters are added to the end of the value
     * during conversions from short values to long values but conversion
     * in the opposite direction simply truncates the value. This is the
     * Fortran representation of the string.
     * 3-15 : Reserved
     */
    public final int paddingType;
    /**
     * Character Set. The character set used to encode the string.
     * Value Description
     * 0 : ASCII character set encoding
     * 1 : UTF-8 character set encoding
     * 2-15 : Reserved
     */
    public final int charset;

    public Chars(int byteSize, int classBitFields, HDF5DataInput channel) throws IOException {
        super(byteSize);
        paddingType = classBitFields & 0b1111;
        charset = (classBitFields & 0b11110000) >> 4;
    }

    @Override
    public Class getValueClass() {
        return String.class;
    }

    @Override
    public Object readData(HDF5DataInput input, int ... compoundindexes) throws IOException {
        byte[] raw = input.readNBytes(getByteSize());
        int end = raw.length;
        endsearch:
        for (int i = raw.length-1; i > 0; i--, end--) {
            switch (paddingType) {
                case 0:
                case 1:
                    if (raw[i] != 0) break endsearch;
                    break;
                case 2:
                    if (raw[i] != ' ') break endsearch;
                    break;
                default:
                    throw new AssertionError();
            }
        }
        raw = Arrays.copyOf(raw, end);
        return new String(raw, (charset == 0) ? StandardCharsets.US_ASCII : StandardCharsets.UTF_8);
    }
}
