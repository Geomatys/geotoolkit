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
public final class Opaque extends DataType {

    public Opaque(int byteSize, int classBitFields, HDF5DataInput channel) throws IOException {
        super(byteSize);
        throw new IOException("TODO");
    }

    @Override
    public Class getValueClass() {
        return byte[].class;
    }

    @Override
    public Object readData(HDF5DataInput input, int ... compoundindexes) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
