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
 * Header Message Name: Bogus
 * <p>
 * Header Message Type: 0x0009
 * <p>
 * Length: 4 bytes
 * <p>
 * Status: For testing only; should never be stored in a valid file.
 * <p>
 * Description:
 * This message is used for testing the HDF5 Library’s response to an “unknown”
 * message type and should never be encountered in a valid HDF5 file.
 *
 * @see IV.A.2.j. The Bogus Message
 * @author Johann Sorel (Geomatys)
 */
public final class BogusMessage extends Message {

    /**
     * This value should always be: 0xdeadbeef.
     */
    private int bogusValue;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        bogusValue = channel.readInt();
    }
}
