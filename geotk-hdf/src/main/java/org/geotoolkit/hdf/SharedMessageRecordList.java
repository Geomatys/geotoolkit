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
package org.geotoolkit.hdf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 *
 * @see III.I. Disk Format: Level 1I - Shared Object Header Message Table
 * @author Johann Sorel (Geomatys)
 */
public final class SharedMessageRecordList extends IOStructure {

    /**
     * The ASCII character string “SMLI” is used to indicate the beginning of a
     * list of index nodes. This gives file consistency checking utilities a
     * better chance of reconstructing a damaged file.
     */
    public static final byte[] SIGNATURE = "SMLI".getBytes(StandardCharsets.US_ASCII);

    /**
     * The record for locating the shared message, either in the fractal heap
     * for the index, or an object header (see format for index nodes below).
     */
    public List<Object> sharedMessages;

    public void read(HDF5DataInput channel) throws IOException {

        channel.ensureSignature(SIGNATURE);
        /*
        list
        checksum
        */
        throw new IOException("TODO");
    }
}
