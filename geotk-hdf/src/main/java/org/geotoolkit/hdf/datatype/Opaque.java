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
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Opaque datatype : raw bytes the format does not interpret, carrying an ASCII
 * tag that says what the application means by them.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsubsec_fmt3_dataobject_hdr_msg_dtmessage">IV.A.2.d. The Datatype Message</a>
 * @author Johann Sorel (Geomatys)
 */
public final class Opaque extends DataType {

    /** Application provided description of the data, may be empty. */
    public final String tag;

    /**
     * Reads the datatype properties, which are the ASCII tag.
     *
     * @param byteSize element size in bytes.
     * @param classBitFields class bit fields (tag length).
     * @param channel input positioned on the datatype properties.
     * @throws IOException if an I/O error occurred.
     */
    public Opaque(int byteSize, int classBitFields, HDF5DataInput channel) throws IOException {
        super(byteSize);
        /*
        The low byte of the class bit field is the length of the tag, already
        padded to a multiple of eight bytes.
        */
        final int tagLength = classBitFields & 0xFF;
        final byte[] datas = channel.readNBytes(tagLength);
        int end = 0;
        while (end < datas.length && datas[end] != 0) end++;
        tag = new String(datas, 0, end, StandardCharsets.US_ASCII);
    }

    @Override
    public Class getValueClass() {
        return byte[].class;
    }

    @Override
    public Object readData(HDF5DataInput input, int ... compoundindexes) throws IOException {
        return input.readNBytes(getByteSize());
    }
}
