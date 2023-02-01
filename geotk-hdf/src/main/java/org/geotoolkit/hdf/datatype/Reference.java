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
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Reference extends DataType {

    /**
     * This four-bit value contains the revised reference types. The values defined are:
     * <ul>
     *   <li>0 : Object Reference (H5R_OBJECT1): A reference to another object in this HDF5 file. </li>
     *   <li>1 : Dataset Region Reference (H5R_DATASET_REGION1): A reference to a region within a dataset in this HDF5 file. </li>
     *   <li>2 : Object Reference (H5R_OBJECT2): A reference to another object in this file or an external file. </li>
     *   <li>3 : Dataset Region Reference (H5R_DATASET_REGION2): A reference to a region within a dataset in this file or an external file. </li>
     *   <li>4 : Attribute Reference (H5R_ATTR): A reference to an attribute attached to an object in this file or an external file. </li>
     *   <li>5-15 : Reserved </li>
     * </ul>
     */
    public final int type;
    /**
     * This four-bit value contains the version for encoding the revised reference types. The values defined are:
     * <ul>
     *   <li>0 : Unused</li>
     *   <li>1 : The version for encoding the revised reference types: Object Reference (2), Dataset Region Reference (3) and Attribute Reference (4). </li>
     *   <li>2-15 : Reserved</li>
     * </ul>
     */
    public final int version;

    public Reference(int byteSize, int version, int classBitFields, HDF5DataInput channel) throws IOException {
        super(byteSize);
        if (byteSize != 8) throw new IOException("Unexpected reference length "+ byteSize + ", was expecting 8");
        if (version < 4) {
            this.type = classBitFields & 0b1111;
            this.version = 0;
        } else if (version == 4) {
            this.type = classBitFields & 0b1111;
            this.version = classBitFields & 0b11110000;
        } else {
            throw new IOException("Unexpected reference version " + version);
        }
    }

    @Override
    public Class getValueClass() {
        return long.class;
    }

    @Override
    public Object readData(HDF5DataInput input, int ... compoundindexes) throws IOException {
        return new Object(input.readLong());
    }

    public static class Object {
        public final long address;

        public Object(long address) {
            this.address = address;
        }
    }
}
