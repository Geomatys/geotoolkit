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
 * Header Message Name: B-tree ‘K’ Values
 * <p>
 * Header Message Type: 0x0013
 * <p>
 * Length: Fixed
 * <p>
 * Status: Optional; may not be repeated.
 * <p>
 * Description:
 * This message retrieves non-default ‘K’ values for internal and leaf nodes of
 * a group or indexed storage v1 B-trees. This message is only found in the
 * superblock extension.
 *
 * @see IV.A.2.t. The B-tree ‘K’ Values Message
 * @author Johann Sorel (Geomatys)
 */
public final class BtreeKValuesMessage extends Message {

    /**
     * This is the node ‘K’ value for each internal node of an indexed storage
     * v1 B-tree. See the description of this field in version 0 and 1 of the
     * superblock as well the section on v1 B-trees.
     */
    private int indexStorageInternalNodeK;
    /**
     * This is the node ‘K’ value for each internal node of a group v1 B-tree.
     * See the description of this field in version 0 and 1 of the superblock
     * as well as the section on v1 B-trees.
     */
    private int groupInternalNodeK;
    /**
     * This is the node ‘K’ value for each leaf node of a group v1 B-tree. See
     * the description of this field in version 0 and 1 of the superblock as
     * well as the section on v1 B-trees.
     */
    private int groupLeafNodeK;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        The version number for this message. This document describes version 0.
        */
        channel.ensureVersion(0);
        indexStorageInternalNodeK = channel.readUnsignedShort();
        groupInternalNodeK = channel.readUnsignedShort();
        groupLeafNodeK = channel.readUnsignedShort();
    }
}
