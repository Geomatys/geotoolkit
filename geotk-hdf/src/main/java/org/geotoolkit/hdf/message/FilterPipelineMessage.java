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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Header Message Name: Data Storage - Filter Pipeline
 * <p>
 * Header Message Type: 0x000B
 * <p>
 * Length: Varies
 * <p>
 * Status: Optional; may not be repeated.
 * <p>
 * Description:
 * This message describes the filter pipeline which should be applied to the
 * data stream by providing filter identification numbers, flags, a name, and
 * client data.
 * <p>
 * This message may be present in the object headers of both dataset and group
 * objects. For datasets, it specifies the filters to apply to raw data. For
 * groups, it specifies the filters to apply to the group’s fractal heap.
 * Currently, only datasets using chunked data storage use the filter pipeline
 * on their raw data.
 *
 * @see IV.A.2.l. The Data Storage - Filter Pipeline Message
 * @author Johann Sorel (Geomatys)
 */
public final class FilterPipelineMessage extends Message {

    /** deflate : GZIP deflate compression */
    public static final int DEFLATE = 1;
    /** shuffle : Data element shuffling */
    public static final int SHUFFLE = 2;
    /** fletcher32 : Fletcher32 checksum */
    public static final int FLETCHER32 = 3;
    /** szip : SZIP compression */
    public static final int SZIP = 4;
    /** nbit : N-bit packing */
    public static final int NBITPACKING = 5;
    /** scaleoffset : Scale and offset encoded values */
    public static final int SCALEOFFSET = 6;

    /**
     * A description of each filter.
     */
    private List<FilterDescription> filters;

    public List<FilterDescription> getFilters() {
        return filters;
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        The version number for this message.
        */
        final int version = channel.ensureVersion(1,2);
        /*
        The total number of filters described in this message. The maximum
        possible number of filters in a message is 32.
        */
        final int numberOfFilters = channel.readUnsignedByte();
        filters = new ArrayList<>(numberOfFilters);

        if (version == 1) {
            channel.skipFully(6);
            for (int i = 0; i < numberOfFilters; i++) {
                final FilterDescription desc = new FilterDescription();
                desc.filteridentificationValue = channel.readUnsignedShort();
                desc.nameLength = channel.readUnsignedShort();
                desc.flags = channel.readUnsignedShort();
                desc.numberOfClientDataValues = channel.readUnsignedShort();
                if (desc.nameLength > 0) {
                    desc.name = channel.readNullTerminatedString(8, StandardCharsets.US_ASCII);
                }
                desc.clientData = channel.readInts(desc.numberOfClientDataValues);
                /*
                Four bytes of zeroes are added to the message at this point if the
                Client Data Number of Values field contains an odd number.
                */
                if (desc.numberOfClientDataValues % 2 != 0) {
                    channel.skipFully(4);
                }
                filters.add(desc);
            }
        } else if (version == 2) {
            for (int i = 0; i < numberOfFilters; i++) {
                final FilterDescription desc = new FilterDescription();
                desc.filteridentificationValue = channel.readUnsignedShort();
                if (desc.filteridentificationValue >= 256) {
                    desc.nameLength = channel.readUnsignedShort();
                }
                desc.flags = channel.readUnsignedShort();
                desc.numberOfClientDataValues = channel.readUnsignedShort();
                if (desc.filteridentificationValue >= 256 && desc.nameLength > 0) {
                    desc.name = channel.readNullTerminatedString(0, StandardCharsets.US_ASCII);
                }
                desc.clientData = channel.readInts(desc.numberOfClientDataValues);
                filters.add(desc);
            }
        }

    }

    public static class FilterDescription {

        /**
         *  This value, often referred to as a filter identifier, is designed to
         * be a unique identifier for the filter. Values from zero through
         * 32,767 are reserved for filters supported by The HDF Group in the
         * HDF5 Library and for filters requested and supported by third parties.
         * Filters supported by The HDF Group are documented immediately below.
         * Information on 3rd-party filters can be found at The HDF Group’s
         * Contributions page.
         * <p>
         * To request a filter identifier, please contact The HDF Group’s Help
         * Desk at The HDF Group Help Desk. You will be asked to provide the
         * following information:
         * Contact information for the developer requesting the new identifier
         * A short description of the new filter
         * Links to any relevant information, including licensing information
         * <p>
         * Values from 32768 to 65535 are reserved for non-distributed uses
         * (for example, internal company usage) or for application usage when
         * testing a feature. The HDF Group does not track or document the use
         * of the filters with identifiers from this range.
         * <p>
         * The filters currently in library version 1.8.0 are listed below:
         * 0 : N/A : Reserved
         * 1 : deflate : GZIP deflate compression
         * 2 : shuffle : Data element shuffling
         * 3 : fletcher32 : Fletcher32 checksum
         * 4 : szip : SZIP compression
         * 5 : nbit : N-bit packing
         * 6 : scaleoffset : Scale and offset encoded values
         */
        public int filteridentificationValue;
        /**
         * Each filter has an optional null-terminated ASCII name and this field
         * holds the length of the name including the null termination padded
         * with nulls to be a multiple of eight. If the filter has no name then
         * a value of zero is stored in this field.
         */
        private int nameLength;
        /**
         * The flags indicate certain properties for a filter. The bit values
         * defined so far are:
         * 0 : If set then the filter is an optional filter. During output,
         * if an optional filter fails it will be silently skipped in the
         * pipeline.
         * 1-15 : Reserved (zero)
         */
        private int flags;
        /**
         * Each filter can store integer values to control how the filter
         * operates. The number of entries in the Client Data array is stored
         * in this field.
         */
        private int numberOfClientDataValues;
        /**
         * If the Name Length field is non-zero then it will contain the size
         * of this field, padded to a multiple of eight. This field contains
         * a null-terminated, ASCII character string to serve as a comment/name
         * for the filter.
         */
        private String name;
        /**
         * This is an array of four-byte integers which will be passed to the
         * filter function. The Client Data Number of Values determines the
         * number of elements in the array.
         */
        public int[] clientData;

    }
}
