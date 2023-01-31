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
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.hdf.datatype.DataType;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Header Message Name: Datatype
 * <p>
 * Header Message Type: 0x0003
 * <p>
 * Length: Variable
 * <p>
 * Status: Required for dataset or committed datatype (formerly named datatype)
 * <p>
 * objects; may not be repeated.
 * <p>
 * Description: The datatype message defines the datatype for each element of a
 * dataset or a common datatype for sharing between multiple datasets. A datatype
 * can describe an atomic type like a fixed- or floating-point type or more
 * complex types like a C struct (compound datatype), array (array datatype),
 * or C++ vector (variable-length datatype).
 * <p>
 * Datatype messages that are part of a dataset object do not describe how
 * elements are related to one another; the dataspace message is used for that
 * purpose. Datatype messages that are part of a committed datatype (formerly
 * named datatype) message describe a common datatype that can be shared by
 * multiple datasets in the file.
 *
 * @see IV.A.2.d. The Datatype Message
 * @author Johann Sorel (Geomatys)
 */
public final class DatatypeMessage extends Message {

    private DataType dataType;

    public DataType getDataType() {
        return dataType;
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException, DataStoreException {
        dataType = DataType.readMessageType(channel);
    }

}
