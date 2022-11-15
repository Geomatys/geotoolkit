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
 * Header Message Name: Object Modification Time (Old)
 * <p>
 * Header Message Type: 0x000E
 * <p>
 * Length: Fixed
 * <p>
 * Status: Optional; may not be repeated.
 * <p>
 * Description:
 * The object modification date and time is a timestamp which indicates (using
 * ISO-8601 date and time format) the last modification of an object. The time
 * is updated when any object header message changes according to the system
 * clock where the change was posted. All fields of this message should be
 * interpreted as coordinated universal time (UTC).
 * <p>
 * This modification time message is deprecated in favor of the “new” Object
 * Modification Time message and is no longer written to the file in versions
 * of the HDF5 Library after the 1.6.0 version.
 *
 * @see IV.A.2.o. The Object Modification Time (Old) Message
 * @author Johann Sorel (Geomatys)
 */
public final class ModificationTimeMessageOld extends Message {

    /**
     * The four-digit year as an ASCII string. For example, 1998.
     */
    private int year;
    /**
     * The month number as a two digit ASCII string where January is 01 and
     * December is 12.
     */
    private int month;
    /**
     * The day number within the month as a two digit ASCII string. The first
     * day of the month is 01.
     */
    private int dayOfMonth;
    /**
     * The hour of the day as a two digit ASCII string where midnight is 00 and
     * 11:00pm is 23.
     */
    private int hour;
    /**
     * The minute of the hour as a two digit ASCII string where the first minute
     * of the hour is 00 and the last is 59.
     */
    private int minute;
    /**
     * The second of the minute as a two digit ASCII string where the first
     * second of the minute is 00 and the last is 59.
     */
    private int second;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        year = channel.readInt();
        month = channel.readUnsignedShort();
        dayOfMonth = channel.readUnsignedShort();
        hour = channel.readUnsignedShort();
        minute = channel.readUnsignedShort();
        second = channel.readUnsignedShort();
        channel.skipFully(2);
    }
}
