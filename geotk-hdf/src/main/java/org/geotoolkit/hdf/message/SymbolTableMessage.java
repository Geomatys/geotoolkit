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
 * Header Message Name: Symbol Table Message
 * <p>
 * Header Message Type: 0x0011
 * <p>
 * Length: Fixed
 * <p>
 * Status: Required for “old style” groups; may not be repeated.
 * <p>
 * Description:
 * Each “old style” group has a v1 B-tree and a local heap for storing symbol
 * table entries, which are located with this message.
 *
 * @see IV.A.2.r. The Symbol Table Message
 * @author Johann Sorel (Geomatys)
 */
public final class SymbolTableMessage extends Message {

    /**
     * This value is the address of the v1 B-tree containing the symbol table
     * entries for the group.
     */
    public long v1BtreeAddress;
    /**
     * This value is the address of the local heap containing the link names
     * for the symbol table entries for the group.
     */
    public long localHeapAddress;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        v1BtreeAddress = channel.readOffset();
        localHeapAddress = channel.readOffset();
    }
}
