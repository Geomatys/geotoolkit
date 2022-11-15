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
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.hdf.io.HDF5DataInput;
import org.geotoolkit.util.StringUtilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Enumerated extends DataType {

    /**
     * Each enumeration type is based on some parent type, usually an integer.
     * The information for that parent type is described recursively by this field.
     */
    private final DataType baseType;
    /**
     * The name for each name/value pair. Each name is stored as a null
     * terminated ASCII string in a multiple of eight bytes. The names are in no
     * particular order.
     */
    private final String[] names;
    /**
     * The list of values in the same order as the names. The values are packed
     * (no inter-value padding) and the size of each value is determined by the
     * parent type.
     */
    private final Object[] values;

    public Enumerated(int byteSize, int version, int classBitFields, HDF5DataInput channel) throws IOException {
        super(byteSize);
        final int numberOfMembers = classBitFields & 0b1111111111111111;

        names = new String[numberOfMembers];
        values = new Object[numberOfMembers];
        if (version == 1 || version == 2) {
            baseType = DataType.readMessageType(channel);
            for (int i = 0; i < numberOfMembers; i++) {
                names[i] = channel.readNullTerminatedString(8, StandardCharsets.US_ASCII);
            }
            for (int i = 0; i < numberOfMembers; i++) {
                values[i] = baseType.readData(channel);
            }
        } else if (version == 3) {
            baseType = DataType.readMessageType(channel);
            for (int i = 0; i < numberOfMembers; i++) {
                names[i] = channel.readNullTerminatedString(0, StandardCharsets.US_ASCII);
            }
            for (int i = 0; i < numberOfMembers; i++) {
                values[i] = baseType.readData(channel);
            }
        } else {
            throw new IOException("Unexpected enumerated version " + version);
        }
    }

    public DataType getBaseType() {
        return baseType;
    }

    public String[] getNames() {
        return names;
    }

    public Object[] getValues() {
        return values;
    }

    @Override
    public Class getValueClass() {
        return baseType.getValueClass();
    }

    @Override
    public Object readData(HDF5DataInput input, int ... compoundindexes) throws IOException {
        return baseType.readData(input);
    }

    @Override
    public Object readData(HDF5DataInput input, int size, int ... compoundindexes) throws IOException {
        return baseType.readData(input, size, compoundindexes);
    }

    @Override
    public Object readData(HDF5DataInput input, int[] dimensions, int ... compoundindexes) throws IOException {
        return baseType.readData(input, dimensions);
    }

    @Override
    public String toString() {
        final List<String> lst = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            lst.add(values[i] + " : " + names[i]);
        }
        return StringUtilities.toStringTree(this.getClass().getSimpleName(), lst);
    }
}
