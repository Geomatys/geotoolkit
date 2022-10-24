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
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Header Message Name: Datatype
 * <p>
 * Header Message Type: 0x0003
 * <p>
 * Length: Variable
 * <p>
 * Status: Required for dataset or committed datatype (formerly named datatype)
 * <p>
 * objects; may not be repeated.
 * <p>
 * Description: The datatype message defines the datatype for each element of a
 * dataset or a common datatype for sharing between multiple datasets. A datatype
 * can describe an atomic type like a fixed- or floating-point type or more
 * complex types like a C struct (compound datatype), array (array datatype),
 * or C++ vector (variable-length datatype).
 * <p>
 * Datatype messages that are part of a dataset object do not describe how
 * elements are related to one another; the dataspace message is used for that
 * purpose. Datatype messages that are part of a committed datatype (formerly
 * named datatype) message describe a common datatype that can be shared by
 * multiple datasets in the file.
 *
 * @see IV.A.2.d. The Datatype Message
 * @author Johann Sorel (Geomatys)
 */
public abstract class DataType {

    public static final int CLASSE_FIXED_POINT = 0;
    public static final int CLASSE_FLOATING_POINT = 1;
    public static final int CLASSE_TIME = 2;
    public static final int CLASSE_STRING = 3;
    public static final int CLASSE_BIT_FIELD = 4;
    public static final int CLASSE_OPAQUE = 5;
    public static final int CLASSE_COMPOUND = 6;
    public static final int CLASSE_REFERENCE = 7;
    public static final int CLASSE_ENUMERATED = 8;
    public static final int CLASSE_VARIABLE_LENGTH = 9;
    public static final int CLASSE_ARRAY = 10;

    protected final int byteSize;

    public DataType(int byteSize) {
        this.byteSize = byteSize;
    }

    /**
     *
     * @return java type class matching this datatype
     */
    public abstract Class getValueClass();

    /**
     * The size of a datatype element in bytes.
     *
     * @return data value size in bytes.
     */
    public int getByteSize() {
        return byteSize;
    }

    /**
     * Read a single datatype value.
     * @param input to read from, not null
     * @param dimensions null for a scalar value, variable size of an array.
     */
    public Object readData(HDF5DataInput input, int[] dimensions) throws IOException {
        if (dimensions == null || dimensions.length == 0) {
            return readData(input);
        } else {
            Object array = java.lang.reflect.Array.newInstance(getValueClass(), dimensions);
            if (dimensions.length == 1) {
                for (int x = 0; x < dimensions[0]; x++) {
                    Object value = readData(input);
                    java.lang.reflect.Array.set(array, x, value);
                }
            } else if (dimensions.length == 2) {
                for (int y = 0; y < dimensions[0]; y++) {
                    Object arr = java.lang.reflect.Array.get(array, y);
                    for (int x = 0; x < dimensions[1]; x++) {
                        Object value = readData(input);
                        java.lang.reflect.Array.set(arr, x, value);
                    }
                }
            }
            return array;
        }
    }

    /**
     * Read a strip of datatype values.
     * @param input to read from, not null
     */
    public Object readData(HDF5DataInput input, int size) throws IOException {
        Object array = java.lang.reflect.Array.newInstance(getValueClass(), size);
        for (int x = 0; x < size; x++) {
            java.lang.reflect.Array.set(array, x, readData(input));
        }
        return array;
    }

    public abstract Object readData(HDF5DataInput input) throws IOException;

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public static DataType readMessageType(HDF5DataInput channel) throws IOException {

        /*
        The version of the datatype message and the datatype’s class information
        are packed together in this field. The version number is packed in the
        top 4 bits of the field and the class is contained in the bottom 4 bits.

        The version number information is used for changes in the format of the
        datatype message and is described here:
        <ul>
        <li>0 : Never used</li>
        <li>1 : Used by early versions of the library to encode compound
        datatypes with explicit array fields. See the compound datatype
        description below for further details.</li>
        <li>2 : Used when an array datatype needs to be encoded.</li>
        <li>3 : Used when a VAX byte-ordered type needs to be encoded. Packs
        various other datatype classes more efficiently also.</li>
        <li>4 : Used to encode the revised reference datatype. </li>
        </ul>

        The class of the datatype determines the format for the class bit field
        and properties portion of the datatype message, which are described below.
        The following classes are currently defined:
        <ul>
        <li>0 : Fixed-Point</li>
        <li>1 : Floating-Point</li>
        <li>2 : Time</li>
        <li>3 : String</li>
        <li>4 : Bit field</li>
        <li>5 : Opaque</li>
        <li>6 : Compound</li>
        <li>7 : Reference</li>
        <li>8 : Enumerated</li>
        <li>9 : Variable-Length</li>
        <li>10 : Array</li>
        </ul>
        */
        final int classAndVersion = channel.readUnsignedByte();
        final int classe = classAndVersion & 0b1111;
        final int version = (classAndVersion & 0b11110000) >> 4;
        /*
        The information in these bit fields is specific to each datatype class
        and is described below. All bits not defined for a datatype class are
        set to zero.
        */
        final int classBitFields = channel.readUnsignedInt24();
        /*
        The size of a datatype element in bytes.
        */
        final int byteSize = channel.readInt();

        final DataType dataType = switch (classe) {
            case CLASSE_FIXED_POINT     -> new FixedPoint(byteSize, classBitFields, channel);
            case CLASSE_FLOATING_POINT  -> new FloatingPoint(byteSize, classBitFields, channel);
            case CLASSE_TIME            -> new Time(byteSize, classBitFields, channel);
            case CLASSE_STRING          -> new Chars(byteSize, classBitFields, channel);
            case CLASSE_BIT_FIELD       -> new BitField(byteSize, classBitFields, channel);
            case CLASSE_OPAQUE          -> new Opaque(byteSize, classBitFields, channel);
            case CLASSE_COMPOUND        -> new Compound(byteSize, version, classBitFields, channel);
            case CLASSE_REFERENCE       -> new Reference(byteSize, classBitFields, channel);
            case CLASSE_ENUMERATED      -> new Enumerated(byteSize, version, classBitFields, channel);
            case CLASSE_VARIABLE_LENGTH -> new VariableLength(byteSize, classBitFields, channel);
            case CLASSE_ARRAY           -> new Array(byteSize, classBitFields, channel);
            default                     -> throw new IOException("Unexpected classe " + classe);
        };
        return dataType;
    }

}
