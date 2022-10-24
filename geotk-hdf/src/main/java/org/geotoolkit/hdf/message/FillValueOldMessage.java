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
 * Header Message Name: Fill Value (old)
 * <p>
 * Header Message Type: 0x0004
 * <p>
 * Length: Varies
 * <p>
 * Status: Optional; may not be repeated.
 * <p>
 * Description:
 * The fill value message stores a single data value which is returned to the
 * application when an uninitialized data element is read from a dataset. The
 * fill value is interpreted with the same datatype as the dataset. If no fill
 * value message is present then a fill value of all zero bytes is assumed.
 * <p>
 * This fill value message is deprecated in favor of the “new” fill value message
 * (Message Type 0x0005) and is only written to the file for forward compatibility
 * with versions of the HDF5 Library before the 1.6.0 version. Additionally, it
 * only appears for datasets with a user-defined fill value (as opposed to the
 * library default fill value or an explicitly set “undefined” fill value).
 *
 * @see IV.A.2.e. The Data Storage - Fill Value (Old) Message
 * @author Johann Sorel (Geomatys)
 */
public final class FillValueOldMessage extends Message {

    /**
     * This is the size of the Fill Value field in bytes.
     */
    private int size;
    /**
     * The fill value. The bytes of the fill value are interpreted using the same
     * datatype as for the dataset.
     */
    private byte[] fillValue;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        size = channel.readInt();
        fillValue = channel.readNBytes(size);
    }
}
