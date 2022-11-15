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
 * Header Message Name: Driver Info
 * <p>
 * Header Message Type: 0x0014
 * <p>
 * Length: Varies
 * <p>
 * Status: Optional; may not be repeated.
 * <p>
 * Description:
 * This message contains information needed by the file driver to reopen a file.
 * This message is only found in the superblock extension: see the “Disk Format:
 * Level 0C - Superblock Extension” section for more information. For more
 * information on the fields in the driver info message, see the “Disk Format:
 * Level 0B - File Driver Info” section; those who use the multi and family
 * file drivers will find this section particularly helpful.
 *
 * @see IV.A.2.u. The Driver Info Message
 * @author Johann Sorel (Geomatys)
 */
public final class DriverInfoMessage extends Message {

    /**
     * This is an eight-byte ASCII string without null termination which
     * identifies the driver.
     */
    private int driverIdentification;
    /**
     * The size in bytes of the Driver Information field of this message.
     */
    private int driverInformationSize;
    /**
     * Driver information is stored in a format defined by the file driver.
     */
    private int driverInformation;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        The version number for this message. This document describes version 0.
        */
        channel.ensureVersion(0);
        throw new IOException("TODO");
    }
}
