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
package org.geotoolkit.hdf.message;

import java.io.IOException;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Version 3 of this message re-structured the format into specific properties
 * that are required for each layout class.
 * <p>
 * Version 4 of this message is similar to version 3 but has additional
 * information for the virtual layout class as well as indexing information for
 * the chunked layout class.
 *
 * @see IV.A.2.i. The Data Layout Message
 * @author Johann Sorel (Geomatys)
 */
public final class DataLayoutMessage extends Message {

    public Layout layout;

    public Layout getLayout() {
        return layout;
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        final int version = channel.ensureVersion(1, 2, 3, 4);

        /*
        An array has a fixed dimensionality. This field specifies the number of
        dimension size fields later in the message. The value stored for chunked
        storage is 1 greater than the number of dimensions in the dataset’s
        dataspace. For example, 2 is stored for a 1 dimensional dataset.
        */
        final int dimensionality;
        if (version == 1 || version == 2) {
            dimensionality = channel.readUnsignedByte();
        } else {
            dimensionality = 0;
        }

        /**
         * The layout class specifies the type of storage for the data and how the
         * other fields of the layout message are to be interpreted.
         * 0 : Compact Storage
         * 1 : Contiguous Storage
         * 2 : Chunked Storage
         * 3 : Virtual Storage
         */
        final int layoutClass = channel.readUnsignedByte();

        if (version == 1 || version == 2) {
            channel.skipFully(5);
        }

        if (layoutClass == 0) {
            final Compact layout = new Compact();
            this.layout = layout;

            if (version == 1 || version == 2) {
                layout.dimensionSizes = channel.readInts(dimensionality);
                layout.size = channel.readInt();
                //do not read the datas now
                layout.rawDataAddress = channel.getStreamPosition();
                channel.skipFully(layout.size);
            } else {
                layout.size = channel.readUnsignedShort();
                //do not read the datas now
                layout.rawDataAddress = channel.getStreamPosition();
                channel.skipFully(layout.size);
            }
        } else if (layoutClass == 1) {
            final Contiguous layout = new Contiguous();
            this.layout = layout;

            if (version == 1 || version == 2) {
                layout.address = channel.readOffset();
                layout.dimensionSizes = channel.readInts(dimensionality);
            } else {
                layout.address = channel.readOffset();
                layout.size = channel.readLength();
            }
        } else if (layoutClass == 2) {
            final Chunked layout = new Chunked();
            this.layout = layout;
            if (version == 1 || version == 2) {
                layout.dimensionality = dimensionality;
                layout.address = channel.readOffset();
                layout.dimensionSizes = channel.readInts(dimensionality);
                layout.datasetElementSize = channel.readInt();
            } else if (version == 3) {
                //TODO : chunk dimentionality is +1 in this version.
                layout.dimensionality = channel.readUnsignedByte() - 1;
                layout.address = channel.readOffset();
                layout.dimensionSizes = channel.readInts(layout.dimensionality);
                layout.datasetElementSize = channel.readInt();
            } else if (version == 4) {
                /*
                This is the chunked layout feature flag:
                DONT_FILTER_PARTIAL_BOUND_CHUNKS (bit 0)
                Do not apply filter to a partial edge chunk.
                SINGLE_INDEX_WITH_FILTER (bit 1)
                A filtered chunk for Single Chunk indexing.
                */
                int flags = channel.readUnsignedByte();
                /*
                A chunk has fixed dimension. This field specifies the number of
                Dimension Size fields later in the message.
                */
                layout.dimensionality = channel.readUnsignedByte();
                /*
                This is the size in bytes used to encode Dimension Size.
                */
                int dimensionSizeEncodedLength = channel.readUnsignedByte();
                /*
                These values define the dimension size of a single chunk, in units
                of array elements (not bytes). The first dimension stored in the
                list of dimensions is the slowest changing dimension and the last
                dimension stored is the fastest changing dimension.
                */
                layout.dimensionSizes = new int[dimensionality];
                for (int i = 0; i < dimensionality; i++) {
                    layout.dimensionSizes[i] = channel.readUnsignedInt(dimensionSizeEncodedLength);
                }
                /*
                There are five indexing types used to look up addresses of the
                chunks. For more information on each type, see “Appendix C: Types
                of Indexes for Dataset Chunks.”
                1 : Single Chunk indexing type.
                2 : Implicit indexing type.
                3 : Fixed Array indexing type.
                4 : Extensible Array indexing type.
                5 : Version 2 B-tree indexing type.
                */
                int chunkIndexingType = channel.readUnsignedByte();
                /*
                This variable-sized field encodes information specific to an
                indexing type. More information on what is encoded with each type
                can be found below this table.
                */
                throw new IOException("TODO");
                //int indexingTypeInformation = channel.readInt();
                //int address = channel.readOffset();
            }
        } else if (layoutClass == 3) {
            final Virtual layout = new Virtual();
            layout.address = channel.readOffset();
            layout.index = channel.readInt();
            this.layout = layout;
        } else {
            throw new IOException("Unexpected layout class " + layoutClass);
        }

    }

    public static abstract class Layout {
    }

    /**
     * Class-specific information for compact storage (layout class 0):
     * (Note: The dimensionality information is in the Dataspace message)
     */
    public static class Compact extends Layout {
        /**
         * For contiguous and compact storage the dimensions define the entire size
         * of the array while for chunked storage they define the size of a single
         * chunk. In all cases, they are in units of array elements (not bytes).
         * The first dimension stored in the list of dimensions is the slowest
         * changing dimension and the last dimension stored is the fastest changing
         * dimension.
         * <p>
         * Only deined in version 1 and 2 of DataLyout.
         */
        public int[] dimensionSizes;
        /**
         * This field contains the size of the raw data for the dataset array,
         * in bytes.
         */
        public int size;
        /**
         * This field contains the adress of the raw data for the dataset array.
         */
        public long rawDataAddress;
    }

    /**
     * Class-specific information for contiguous storage (layout class 1):
     * (Note: The dimensionality information is in the Dataspace message)
     */
    public static class Contiguous extends Layout {
        /**
         * For contiguous and compact storage the dimensions define the entire size
         * of the array while for chunked storage they define the size of a single
         * chunk. In all cases, they are in units of array elements (not bytes).
         * The first dimension stored in the list of dimensions is the slowest
         * changing dimension and the last dimension stored is the fastest changing
         * dimension.
         * <p>
         * Only deined in version 1 and 2 of DataLyout.
         */
        public int[] dimensionSizes;
        /**
         * This is the address of the raw data in the file. The address may
         * have the “undefined address” value, to indicate that storage has
         * not yet been allocated for this array.
         */
        public long address;
        /**
         * This field contains the size allocated to store the raw data, in
         * bytes.
         */
        public long size;
    }

    /**
     * Class-specific information for chunked storage (layout class 2).
     */
    public static class Chunked extends Layout {
        /**
         * For version 1 and 2 of DataLyout.
         * For chunked storage they define the size of a single
         * chunk. In all cases, they are in units of array elements (not bytes).
         * The first dimension stored in the list of dimensions is the slowest
         * changing dimension and the last dimension stored is the fastest changing
         * dimension.
         * <p>
         * For version 3 and 4 of DataLayout.
         * These values define the dimension size of a single chunk, in units
         * of array elements (not bytes). The first dimension stored in the
         * list of dimensions is the slowest changing dimension and the last
         * dimension stored is the fastest changing dimension.
         *
         */
        public int[] dimensionSizes;
        /**
         * A chunk has a fixed dimensionality. This field specifies the number
         * of dimension size fields later in the message.
         */
        public int dimensionality;
        /**
         * This is the address of the v1 B-tree that is used to look up the
         * addresses of the chunks that actually store portions of the array
         * data. The address may have the “undefined address” value, to
         * indicate that storage has not yet been allocated for this array.
         */
        public long address;
        /**
         * The size of a dataset element, in bytes.
         */
        public int datasetElementSize;
    }

    /**
     *
     */
    public static class Virtual extends Layout {
        /**
         * This is the address of the global heap collection where the VDS
         * mapping entries are stored. See “Disk Format: Level 1F - Global
         * Heap Block for Virtual Datasets.”
         */
        public long address;
        /**
         * This is the index of the data object within the global heap
         * collection.
         */
        public int index;
    }
}
