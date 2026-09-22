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
import org.geotoolkit.hdf.heap.GlobalHeapId;
import org.geotoolkit.hdf.heap.GlobalHeapObject;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Reference datatype : a reference to another object of the file, or to a
 * region of a dataset. Values are decoded as {@link Reference.Object} or
 * {@link Reference.Region} instances.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsubsec_fmt3_dataobject_hdr_msg_dtmessage">IV.A.2.d. The Datatype Message</a>
 * @author Johann Sorel (Geomatys)
 */
public final class Reference extends DataType {

    /** A reference to another object of this file. */
    public static final int TYPE_OBJECT = 0;
    /** A reference to a region within a dataset of this file. */
    public static final int TYPE_DATASET_REGION = 1;

    /**
     * This four-bit value contains the revised reference types. The values defined are:
     * <ul>
     *   <li>0 : Object Reference (H5R_OBJECT1): A reference to another object in this HDF5 file. </li>
     *   <li>1 : Dataset Region Reference (H5R_DATASET_REGION1): A reference to a region within a dataset in this HDF5 file. </li>
     *   <li>2 : Object Reference (H5R_OBJECT2): A reference to another object in this file or an external file. </li>
     *   <li>3 : Dataset Region Reference (H5R_DATASET_REGION2): A reference to a region within a dataset in this file or an external file. </li>
     *   <li>4 : Attribute Reference (H5R_ATTR): A reference to an attribute attached to an object in this file or an external file. </li>
     *   <li>5-15 : Reserved </li>
     * </ul>
     */
    public final int type;
    /**
     * This four-bit value contains the version for encoding the revised reference types. The values defined are:
     * <ul>
     *   <li>0 : Unused</li>
     *   <li>1 : The version for encoding the revised reference types: Object Reference (2), Dataset Region Reference (3) and Attribute Reference (4). </li>
     *   <li>2-15 : Reserved</li>
     * </ul>
     */
    public final int version;

    /**
     * Reads the datatype properties.
     *
     * @param byteSize element size in bytes.
     * @param version datatype message version, 4 for the revised reference types.
     * @param classBitFields class bit fields (reference type, encoding version since version 4).
     * @param channel input positioned on the datatype properties, nothing is read.
     * @throws IOException if the datatype message version is unexpected.
     */
    public Reference(int byteSize, int version, int classBitFields, HDF5DataInput channel) throws IOException {
        super(byteSize);
        if (version < 4) {
            this.type = classBitFields & 0b1111;
            this.version = 0;
        } else if (version == 4) {
            this.type = classBitFields & 0b1111;
            this.version = classBitFields & 0b11110000;
        } else {
            throw new IOException("Unexpected reference version " + version);
        }
    }

    @Override
    public Class getValueClass() {
        /*
        The decoded value is a reference holder, not a plain address : returning
        long.class here would make Dataset allocate a long[] that readData can
        not store into. Note this is Reference.Object, which shadows
        java.lang.Object inside this class.
        */
        return Reference.Object.class;
    }

    @Override
    public java.lang.Object readData(HDF5DataInput input, int ... compoundindexes) throws IOException {
        if (type == TYPE_DATASET_REGION) {
            /*
            The element is a global heap identifier; the heap object holds the
            address of the dataset followed by a serialized dataspace selection.
            */
            final GlobalHeapId heapId = new GlobalHeapId();
            heapId.read(input);
            final GlobalHeapObject heapObject = input.getGlobalHeapObject(heapId);
            if (heapObject == null) {
                return new Region(-1, null);
            }
            //the heap object position is a position in the file, not in this buffer
            final HDF5DataInput fileChannel = input.getFileChannel();
            fileChannel.mark();
            try {
                fileChannel.seek(heapObject.streamDataPosition);
                final long address = fileChannel.readOffset();
                final int remaining = Math.toIntExact(heapObject.objectSize - fileChannel.getOffsetSize());
                return new Region(address, remaining > 0 ? fileChannel.readNBytes(remaining) : null);
            } finally {
                fileChannel.reset();
            }
        }
        if (type == TYPE_OBJECT) {
            return new Object(input.readOffset());
        }
        /*
        Revised reference types, whose payload is a length prefixed blob this
        reader does not interpret.
        */
        return new Region(-1, input.readNBytes(getByteSize()));
    }

    /** A reference to an object, identified by the address of its object header. */
    public static class Object {
        /** Address of the referenced object header, or -1 when unknown. */
        public final long address;

        /**
         * Creates an object reference.
         *
         * @param address address of the referenced object header.
         */
        public Object(long address) {
            this.address = address;
        }

        @Override
        public String toString() {
            return "Reference to " + address;
        }
    }

    /**
     * A reference to a region of a dataset. The selection itself is kept as the
     * raw bytes of a serialized dataspace selection, which this reader does not
     * decode yet.
     */
    public static final class Region extends Object {
        /** Serialized dataspace selection, or {@code null} when it could not be located. */
        public final byte[] selection;

        /**
         * Creates a dataset region reference.
         *
         * @param address address of the dataset object header, or -1 when unknown.
         * @param selection serialized dataspace selection, or {@code null}.
         */
        public Region(long address, byte[] selection) {
            super(address);
            this.selection = selection;
        }

        @Override
        public String toString() {
            return "Reference to a region of " + address;
        }
    }
}
