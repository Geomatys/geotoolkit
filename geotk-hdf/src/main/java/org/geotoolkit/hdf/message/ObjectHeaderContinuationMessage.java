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
 * Header Message Name: Object Header Continuation
 * <p>
 * Header Message Type: 0x0010
 * <p>
 * Length: Fixed
 * <p>
 * Status: Optional; may be repeated.
 * <p>
 * Description:
 * The object header continuation is the location in the file of a block
 * containing more header messages for the current data object. This can be
 * used when header blocks become too large or are likely to change over time.
 *
 * @see IV.A.2.q. The Object Header Continuation Message
 * @author Johann Sorel (Geomatys)
 */
public final class ObjectHeaderContinuationMessage extends Message {

    /**
     * This value is the address in the file where the header continuation
     * block is located.
     * <p>
     * The format of the header continuation block that this message points to
     * depends on the version of the object header that the message is contained
     * within.
     * <p>
     * Continuation blocks for version 1 object headers have no special
     * formatting information; they are merely a list of object header message
     * info sequences (type, size, flags, reserved bytes and data for each
     * message sequence). See the description of Version 1 Data Object Header
     * Prefix.
     * <p>
     * Continuation blocks for version 2 object headers do have special formatting
     * information as described here (see also the description of Version 2 Data
     * Object Header Prefix.):
     */
    public long offset;
    /**
     * This value is the length in bytes of the header continuation block in
     * the file.
     */
    public long length;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        offset = channel.readOffset();
        length = channel.readLength();
    }
}
