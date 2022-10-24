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
 * Header Message Name: Fill Value
 * <p>
 * Header Message Type: 0x0005
 * <p>
 * Length: Varies
 * <p>
 * Status: Required for dataset objects; may not be repeated.
 * <p>
 * Description: The fill value message stores a single data value which
 * is returned to the application when an uninitialized data element is read from
 * a dataset. The fill value is interpreted with the same datatype as the dataset
 * .
 * @see IV.A.2.f. The Data Storage - Fill Value Message
 * @author Johann Sorel (Geomatys)
 */
public final class FillValueMessage extends Message {

    /**
     * When the storage space for the dataset’s raw data will be allocated. The
     * allowed values are:
     * 0 : Not used.
     * 1 : Early allocation. Storage space for the entire dataset should be
     * allocated in the file when the dataset is created.
     * 2 : Late allocation. Storage space for the entire dataset should not be
     * allocated until the dataset is written to.
     * 3 : Incremental allocation. Storage space for the dataset should not be
     * allocated until the portion of the dataset is written to. This is
     * currently used in conjunction with chunked data storage for datasets.
     */
    private int spaceAllocationTime;
    /**
     * At the time that storage space for the dataset’s raw data is allocated,
     * this value indicates whether the fill value should be written to the raw
     * data storage elements. The allowed values are:
     * 0 : On allocation. The fill value is always written to the raw data
     * storage when the storage space is allocated.
     * 1 : Never. The fill value should never be written to the raw data storage.
     * 2 : Fill value written if set by user. The fill value will be written to
     * the raw data storage when the storage space is allocated only if the user
     * explicitly set the fill value. If the fill value is the library default
     * or is undefined, it will not be written to the raw data storage.
     */
    private int fillValueWriteTime;
    /**
     * This value indicates if a fill value is defined for this dataset. If this
     * value is 0, the fill value is undefined. If this value is 1, a fill value is defined for this dataset. For version 2 or later of the fill value message, this value controls the presence of the Size and Fill Value fields.
     */
    private int fillValueDefined;
    /**
     * This is the size of the Fill Value field in bytes. This field is not
     * present if the Version field is greater than 1, and the Fill Value
     * Defined field is set to 0.
     */
    private int size;
    /**
     * The fill value. The bytes of the fill value are interpreted using the
     * same datatype as for the dataset. This field is not present if the Version
     * field is greater than 1, and the Fill Value Defined field is set to 0.
     */
    private byte[] fillValue;

    /**
     * The fill value. The bytes of the fill value are interpreted using the
     * same datatype as for the dataset. This field is not present if the Version
     * field is greater than 1, and the Fill Value Defined field is set to 0.
     */
    public byte[] getFillValue() {
        return fillValue;
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        /*
        The version number information is used for changes in the format of the
        fill value message and is described here:
        0 : Never used
        1 : Initial version of this message.
        2 : In this version, the Size and Fill Value fields are only present if
        the Fill Value Defined field is set to 1.
        3 : This version packs the other fields in the message more efficiently
        than version 2.
         */
        final int version = channel.readUnsignedByte();

        if (version == 1) {
            spaceAllocationTime = channel.readUnsignedByte();
            fillValueWriteTime = channel.readUnsignedByte();
            fillValueDefined = channel.readUnsignedByte();
            size = channel.readInt();
            fillValue = channel.readNBytes(size);
        } else if (version == 2) {
            spaceAllocationTime = channel.readUnsignedByte();
            fillValueWriteTime = channel.readUnsignedByte();
            fillValueDefined = channel.readUnsignedByte();
            if (fillValueDefined != 0) {
                size = channel.readInt();
                fillValue = channel.readNBytes(size);
            }
        } else if (version == 3) {
            /*
            When the storage space for the dataset’s raw data will be allocated.
            The allowed values are:
            0-1 : Space Allocation Time, with the same values as versions 1 and 2 of
            the message.
            2-3 : Fill Value Write Time, with the same values as versions 1 and 2 of
            the message.
            4 : Fill Value Undefined, indicating that the fill value has been marked
            as “undefined” for this dataset. Bits 4 and 5 cannot both be set.
            5 : Fill Value Defined, with the same values as versions 1 and 2 of the
            message. Bits 4 and 5 cannot both be set.
            6-7 : Reserved (zero).
            */
            final int flags = channel.readUnsignedByte();
            spaceAllocationTime = flags & 0b11;
            fillValueWriteTime = (flags & 0b1100) >> 2;
            fillValueDefined  = (flags & 0b100000) >> 5;
            if (fillValueDefined != 0) {
                size = channel.readInt();
                fillValue = channel.readNBytes(size);
            }
        }
    }
}
