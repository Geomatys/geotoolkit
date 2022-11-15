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
 * Header Message Name: NIL
 * <p>
 * Header Message Type: 0x0000
 * <p>
 * Length: Varies
 * <p>
 * Status: Optional; may be repeated.
 * <p>
 * Description:
 * The NIL message is used to indicate a message which is to be ignored when
 * reading the header messages for a data object. [Possibly one which has
 * been deleted for some reason.]
 *
 * @see IV.A.2.a. The NIL Message
 * @author Johann Sorel (Geomatys)
 */
public final class NillMessage extends Message {

    @Override
    public void read(HDF5DataInput channel) throws IOException {
    }
}
