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
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.hdf.heap.GlobalHeapId;
import org.geotoolkit.hdf.heap.GlobalHeapObject;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class VariableLength extends DataType {

    private static final int PADDING_NULL_TERMINATED = 0;
    private static final int PADDING_NULL_PAD = 1;
    private static final int PADDING_SPACE_PAD = 2;

    /**
     * Type. This four-bit value contains the type of variable-length datatype described. The values defined are:
     * Value Description
     * 0 : Sequence: A variable-length sequence of any datatype.
     * Variable-length sequences do not have padding or character set
     * information.
     * 1 : String: A variable-length sequence of characters. Variable-length
     * strings have padding and character set information.
     * 2-15 : Reserved
     */
    public final int type;
    /**
     * Padding type. (variable-length string only) This four-bit value
     * determines the type of padding used for variable-length strings.
     * The values are the same as for the string padding type, as follows:
     * Value  Description
     * 0 : Null terminate: A zero byte marks the end of a string and is
     * guaranteed to be present after converting a long string to a short
     * string. When converting a short string to a long string, the value is
     * padded with additional null characters as necessary.
     * 1 : Null pad: Null characters are added to the end of the value during
     * conversion from a short string to a longer string. Conversion from a
     * long string to a shorter string simply truncates the value.
     * 2 : Space pad: Space characters are added to the end of the value
     * during conversion from a short string to a longer string. Conversion
     * from a long string to a shorter string simply truncates the value.
     * This is the Fortran representation of the string.
     * 3-15 : Reserved
     *
     * This value is set to zero for variable-length sequences.
     */
    public final int paddingType;
    /**
     * Character Set. (variable-length string only) This four-bit value
     * specifies the character set to be used for encoding the string:
     * Value Description
     * 0 : ASCII character set encoding
     * 1 : UTF-8 character set encoding
     * 2-15 : Reserved
     *
     * This value is set to zero for variable-length sequences.
     */
    public final int characterSet;
    /**
     * Each variable-length type is based on some parent type. The information
     * for that parent type is described recursively by this field.
     */
    public final DataType baseType;

    public VariableLength(int byteSize, int classBitFields, HDF5DataInput channel) throws IOException, DataStoreException {
        super(byteSize);
        type = classBitFields & 0b1111;
        paddingType = (classBitFields & 0b11110000) >> 4;
        characterSet = (classBitFields & 0b111100000000) >> 8;
        baseType =  DataType.readMessageType(channel);
    }

    @Override
    public Class getValueClass() {
        if (type == 0) {
            return Object.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object readData(HDF5DataInput channel, int ... compoundindexes) throws IOException, DataStoreException {

        /*
        Data is not here, it is in a global heap
        this is noted in the global heap section : III.E. Disk Format: Level 1E - Global Heap
        The HDF5 Library creates global heap collections as needed, so there
        may be multiple collections throughout the file. The set of all of
        them is abstractly called the “global heap”, although they do not
        actually link to each other, and there is no global place in the file
        where you can discover all of the collections. The collections are
        found simply by finding a reference to one through another object in
        the file. For example, data of variable-length datatype elements is
        stored in the global heap and is accessed via a global heap ID. The
        format for global heap IDs is described at the end of this section.
        */

        //TODO is is not defined anywhere, but 4bytes are here with an unkown information
        channel.skipFully(4);

        final GlobalHeapId globalheapId = new GlobalHeapId();
        globalheapId.read(channel);
        if (globalheapId.collectionAddress <= 0) {
            //no data or corrupted ? specification do no say.
            return null;
        }

        final GlobalHeapObject globalHeapObject = channel.getGlobalHeapObject(globalheapId);
        if (globalHeapObject == null) {
            channel.getGlobalHeapObject(globalheapId);
            //throw new IOException("global heap object not found " + globalheapId);
            return null;
        }

        if (type == 0) {
            //undefined type
            try {
                channel.mark();
                channel.seek(globalHeapObject.streamDataPosition);

                switch (paddingType) {
                    case PADDING_NULL_TERMINATED:
                        return baseType.readData(channel);
                        //return channel.readNBytes((int) globalHeapObject.objectSize);
                    case PADDING_NULL_PAD:
                    case PADDING_SPACE_PAD:
                    default:
                        throw new IOException("Unsupported padding " + paddingType);
                }
            } finally {
                channel.reset();
            }

        } else if (type == 1) {

            try {
                channel.mark();
                channel.seek(globalHeapObject.streamDataPosition);

                switch (paddingType) {
                    case PADDING_NULL_TERMINATED:
                        return channel.readNullTerminatedString(0, (int) globalHeapObject.objectSize, characterSet == 0 ? StandardCharsets.US_ASCII : StandardCharsets.UTF_8);
                    case PADDING_NULL_PAD:
                    case PADDING_SPACE_PAD:
                    default:
                        throw new IOException("Unsupported padding " + paddingType);
                }
            } finally {
                channel.reset();
            }

        } else {
            throw new IOException("Unexpected type " + type);
        }
    }
}
