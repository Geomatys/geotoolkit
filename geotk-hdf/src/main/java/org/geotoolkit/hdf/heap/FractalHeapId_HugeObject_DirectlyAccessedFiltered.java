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
 *
 * @see III.G. Disk Format: Level 1G - Fractal Heap
 * @author Johann Sorel (Geomatys)
 */
public final class FractalHeapId_HugeObject_DirectlyAccessedFiltered extends IOStructure {

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
     * This field is the address of the filtered object in the file.
     */
    private long address;
    /**
     * This field is the length of the filtered object in the file.
     */
    private long length;
    /**
     * This field is the I/O pipeline filter mask for the filtered object in the file.
     */
    private int filterMask;
    /**
     * This field is the size of the de-filtered object in the file.
     */
    private long defilteredSize;

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        versionType = channel.readUnsignedByte();
        address = channel.readOffset();
        length = channel.readLength();
        filterMask = channel.readInt();
        defilteredSize = channel.readLength();
    }
}
