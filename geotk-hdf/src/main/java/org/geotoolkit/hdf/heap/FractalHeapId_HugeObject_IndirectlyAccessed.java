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
package org.geotoolkit.hdf.heap;

import java.io.IOException;
import org.geotoolkit.hdf.IOStructure;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Fractal heap ID of an indirectly accessed huge object.
 * The ID holds a key into the version 2 B-tree of huge objects of the heap.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsec_fmt3_infra_fractalheap">III.G. Disk Format: Level 1G - Fractal Heap</a>
 * @author Johann Sorel (Geomatys)
 */
public final class FractalHeapId_HugeObject_IndirectlyAccessed extends IOStructure {

    /**
     * This is a bit field with the following definition:
     * <p>
     * Bits :
     * <ul>
     * <li>6-7 : The current version of ID format. This document describes version 0. </li>
     * <li>4-5 : The ID type. Huge objects have a value of 1. </li>
     * <li>0-3 : Reserved. </li>
     * </ul>
     */
    private int versionType;
    /**
     * This field is the key for retrieving the information about the object
     * from the v2 B-tree for huge objects.
     */
    private int v2BtreeKey;

    /** Creates an empty heap ID, filled by {@link #read(HDF5DataInput)}. */
    public FractalHeapId_HugeObject_IndirectlyAccessed() {
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        versionType = channel.readUnsignedByte();
        throw new IOException("TODO");
    }
}
