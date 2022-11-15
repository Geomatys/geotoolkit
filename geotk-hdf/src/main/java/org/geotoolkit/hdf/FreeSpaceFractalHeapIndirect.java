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
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 *
 * @see III.H. Disk Format: Level 1H - Free-space Manager
 * @author Johann Sorel (Geomatys)
 */
public final class FreeSpaceFractalHeapIndirect extends IOStructure {

    /**
     * The offset of the indirect block in the fractal heap’s address space
     * containing the empty blocks.
     *
     * The number of bytes used to encode this field is the minimum number of
     * bytes needed to encode values for the Maximum Heap Size (in the fractal
     * heap’s header).
     */
    private int fractalHeapIndirectBlockOffset;
    /**
     * This is the row that the empty blocks start in.
     */
    private int blockStartRow;
    /**
     * This is the column that the empty blocks start in.
     */
    private int blockStartColumn;
    /**
     * This is the number of empty blocks covered by the section.
     */
    private int numberOfBlocks;

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        /*
        block offset
        */
        blockStartRow = channel.readUnsignedShort();
        blockStartRow = channel.readUnsignedShort();
        numberOfBlocks = channel.readUnsignedShort();
        throw new IOException("TODO");
    }
}
