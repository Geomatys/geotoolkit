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
 * Header Message Name: Object Reference Count
 * <p>
 * Header Message Type: 0x0016
 * <p>
 * Length: Fixed
 * <p>
 * Status: Optional; may not be repeated.
 * <p>
 * Description:
 * This message stores the number of hard links (in groups or objects) pointing
 * to an object: in other words, its reference count.
 *
 * @see IV.A.2.w. The Object Reference Count Message
 * @author Johann Sorel (Geomatys)
 */
public final class ObjectReferenceCount extends Message {

    /**
     * The unsigned 32-bit integer is the reference count for the object. This
     * message is only present in “version 2” (or later) object headers, and if
     * not present those object header versions, the reference count for the
     * object is assumed to be 1.
     */
    private int referenceCount;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        /*
        The version number for this message. This document describes version 0.
        */
        channel.ensureVersion(0);
        referenceCount = channel.readInt();
    }
}
