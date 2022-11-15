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
 * Header Message Name: Object Modification Time
 * <p>
 * Header Message Type: 0x0012
 * <p>
 * Length: Fixed
 * <p>
 * Status: Optional; may not be repeated.
 * <p>
 * Description:
 * The object modification time is a timestamp which indicates the time of the
 * last modification of an object. The time is updated when any object header
 * message changes according to the system clock where the change was posted.
 *
 * @see IV.A.2.s. The Object Modification Time Message
 * @author Johann Sorel (Geomatys)
 */
public final class ObjectModificationTimeMessage extends Message {

    /**
     * A 32-bit unsigned integer value that stores the number of seconds since
     * 0 hours, 0 minutes, 0 seconds, January 1, 1970, Coordinated Universal
     * Time.
     */
    private int secondsAfterUnixEpoch;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        The version number is used for changes in the format of Object
        Modification Time and is described here:
        0 : Never used.
        1 : Used by Version 1.6.1 and after of the library to encode time. In
        this version, the time is the seconds after Epoch.
         */
        channel.ensureVersion(1);
        channel.skipFully(3);
        secondsAfterUnixEpoch = channel.readInt();
    }
}
