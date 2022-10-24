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
 * The driver information block is an optional region of the file which contains
 * information needed by the file driver to reopen a file.
 *
 * @see II.B. Disk Format: Level 0B - File Driver Info
 * @author Johann Sorel (Geomatys)
 */
public final class DriverInformationBlock extends IOStructure {

    /**
     * The size in bytes of the Driver Information field.
     */
    private int driverInformationSize;
    /**
     * This is an eight-byte ASCII string without null termination which identifies
     * the driver and/or version number of the Driver Information Block.
     * The predefined driver encoded in this field by the HDF5 Library is identified
     * by the letters NCSA followed by the first four characters of the driver name.
     * If the Driver Information block is not the original version then the last
     * letter(s) of the identification will be replaced by a version number in ASCII,
     * starting with 0.
     * <p>
     * Identification for user-defined drivers is also eight-byte long.
     * It can be arbitrary but should be unique to avoid the four character prefix “NCSA”.
     */
    private int driverIdentification;
    /**
     * Driver information is stored in a format defined by the file driver.
     */
    private byte[] driverInformation;

    /**
     */
    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        The version number of the Driver Information Block.
        This document describes version 0.
         */
        channel.ensureVersion(0);
        channel.skipFully(3);
        driverInformationSize = channel.readInt();
        driverIdentification = channel.readInt();
        driverInformation = channel.readNBytes(driverInformationSize);
        //TODO MultiDriver or FamilyDriver
    }
}
