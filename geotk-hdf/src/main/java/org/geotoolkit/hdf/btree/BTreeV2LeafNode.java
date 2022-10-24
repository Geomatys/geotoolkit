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
package org.geotoolkit.hdf.btree;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.geotoolkit.hdf.IOStructure;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 *
 * @see III.A.2. Disk Format: Level 1A2 - Version 2 B-trees
 * @author Johann Sorel (Geomatys)
 */
public final class BTreeV2LeafNode extends IOStructure {

    /**
     * The ASCII character string “BTLF“ is used to indicate the leaf node of a version 2 (v2) B-tree.
     */
    public static final byte[] SIGNATURE = "BTLF".getBytes(StandardCharsets.US_ASCII);

    /**
     * This field is the type of the B-tree node. It should always be the
     * same as the B-tree type in the header.
     */
    private int type;

    public void read(HDF5DataInput channel) throws IOException {

        channel.ensureSignature(SIGNATURE);
        /*
        The version number for this B-tree leaf node.
        This document describes version 0.
         */
        channel.ensureVersion(0);
        type = channel.readUnsignedByte();

        /*
        Records

        The size of this field is determined by the number of records for this
        node and the record size (from the header).
        The format of records depends on the type of B-tree.
        */

        int checksum = channel.readInt();

        throw new IOException("TODO");
    }
}
