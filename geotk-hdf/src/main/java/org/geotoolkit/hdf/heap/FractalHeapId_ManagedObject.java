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
public final class FractalHeapId_ManagedObject extends IOStructure {

    /**
     * This is a bit field with the following definition:
     * <p>
     * Bits :
     * <ul>
     * <li>6-7 : The current version of ID format. This document describes version 0. </li>
     * <li>4-5 : The ID type. Managed objects have a value of 0. </li>
     * <li>0-3 : Reserved. </li>
     * </ul>
     */
    private int versionType;
    /**
     * This field is the offset of the object in the heap. This field’s size is
     * the minimum number of bytes necessary to encode the Maximum Heap Size
     * value (from the Fractal Heap Header). For example, if the value of the
     * Maximum Heap Size is less than 256 bytes, this field is 1 byte in length,
     * a Maximum Heap Size of 256-65535 bytes uses a 2 byte length, and so on.
     */
    private int offset;
    /**
     * This field is the length of the object in the heap. It is determined by
     * taking the minimum value of Maximum Direct Block Size and Maximum Size
     * of Managed Objects in the Fractal Heap Header. Again, the minimum number
     * of bytes needed to encode that value is used for the size of this field.
     */
    private int length;

    @Override
    public void read(HDF5DataInput channel) throws IOException {

        versionType = channel.readUnsignedByte();
        throw new IOException("TODO");
    }
}
