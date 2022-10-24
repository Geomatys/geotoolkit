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
import org.geotoolkit.util.StringUtilities;

/**
 * The Properties field of a compound datatype is a list of the member definitions
 * of the compound datatype. The member definitions appear one after another
 * with no intervening bytes. The member types are described with a (recursively)
 * encoded datatype message.
 *
 * Note that the property descriptions are different for different versions
 * of the datatype version. Additionally note that the version 0 datatype
 * encoding is deprecated and has been replaced with later encodings in versions
 * of the HDF5 Library from the 1.4 release onward.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Compound extends DataType {

    public final Member[] members;

    private final Class componentClass;
    private final Class valueClass;

    public Compound(int byteSize, int version, int classBitFields, HDF5DataInput channel) throws IOException {
        super(byteSize);
        /*
        Number of Members. This field contains the number of members defined
        for the compound datatype. The member definitions are listed in the
        Properties field of the data type message.
         */
        final int numberOfMembers = classBitFields & 0b1111111111111111;
        members = new Member[numberOfMembers];
        for (int i = 0; i < numberOfMembers; i++) {
            final Member member = new Member();
            member.read(version, byteSize, channel);
            members[i] = member;
        }

        //compute value class
        Class c = members[0].memberType.getValueClass();
        for (int i = 0; i < members.length; i++) {
            Class mc = members[i].memberType.getValueClass();
            if (mc != c) {
                c = Object.class;
                break;
            }
        }
        componentClass = c;
        valueClass = java.lang.reflect.Array.newInstance(c, 1).getClass();
    }

    @Override
    public Class getValueClass() {
        return valueClass;
    }

    @Override
    public Object readData(HDF5DataInput channel) throws IOException {
        final long position = channel.getStreamPosition();
        final Object array = java.lang.reflect.Array.newInstance(componentClass, members.length);
        for (int i = 0; i < members.length; i++) {
            channel.seek(position + members[i].byteOffsetOfMember);
            java.lang.reflect.Array.set(array, i, members[i].memberType.readData(channel));
        }
        //move to end of data
        channel.seek(position + byteSize);
        return array;
    }

    @Override
    public String toString() {
        return StringUtilities.toStringTree(this.getClass().getSimpleName(), Arrays.asList(members));
    }

    public static class Member {
       /**
        * This NUL-terminated string provides a description for the opaque type.
        */
       public String name;
       /**
        * This is the byte offset of the member within the datatype.
        */
       public int byteOffsetOfMember;
       /**
        * This field is the size of a dimension of the array field as stored in
        * the file. The first dimension stored in the list of dimensions is the
        * slowest changing dimension and the last dimension stored is the fastest
        * changing dimension.
        *
        * Defined int version 1 only.
        */
       public int[] dimensionSizes;
       /**
        * This field is a datatype message describing the datatype of the member.
        */
       public DataType memberType;

       private void read(int version, int size, HDF5DataInput channel) throws IOException {
           if (version == 1) {
                name = channel.readNullTerminatedString(8, StandardCharsets.US_ASCII);
                /*
                This is the byte offset of the member within the datatype.
                 */
                byteOffsetOfMember = channel.readInt();
                /*
                If set to zero, this field indicates a scalar member. If set to
                a value greater than zero, this field indicates that the member
                is an array of values. For array members, the size of the array
                is indicated by the ‘Size of Dimension n’ field in this message.
                 */
                final int dimensionality = channel.readUnsignedByte();
                channel.skipFully(3);
                /*
                This field was intended to allow an array field to have its
                dimensions permuted, but this was never implemented. This field
                should always be set to zero.
                 */
                final int dimensionPermutation = channel.readInt();
                channel.skipFully(4);
                dimensionSizes = new int[dimensionality];
                for (int i = 0; i < 4; i++) {
                    final int s = channel.readInt();
                    if (i < dimensionSizes.length) dimensionSizes[i] = s;
                }
                memberType = DataType.readMessageType(channel);
            } else if (version == 2) {
                name = channel.readNullTerminatedString(8, StandardCharsets.US_ASCII);
                byteOffsetOfMember = channel.readInt();
                memberType = DataType.readMessageType(channel);
            } else if (version == 3) {
                name = channel.readNullTerminatedString(0, StandardCharsets.US_ASCII);
                if (size < 256) {
                    byteOffsetOfMember = channel.readUnsignedByte();
                } else if (size < 65536) {
                    byteOffsetOfMember = channel.readUnsignedShort();
                } else if (size < 16777216) {
                    byteOffsetOfMember = channel.readUnsignedInt24();
                } else {
                    byteOffsetOfMember = channel.readInt();
                }
                memberType = DataType.readMessageType(channel);
            } else {
                throw new IOException("Unexpected Compound datatype version " + version);
            }
        }

       @Override
       public String toString() {
           final StringBuilder sb = new StringBuilder(name);
           sb.append(" offset:").append(byteOffsetOfMember);
           if (dimensionSizes != null && dimensionSizes.length > 0) {
               sb.append(" size:").append(Arrays.toString(dimensionSizes));
           }
           sb.append(" datatype:").append(memberType);
           return sb.toString();
       }
    }
}
