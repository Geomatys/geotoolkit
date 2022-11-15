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
 * Header Message Name: Object Comment
 * <p>
 * Header Message Type: 0x000D
 * <p>
 * Length: Varies
 * <p>
 * Status: Optional; may not be repeated.
 * <p>
 * Description:
 * The object comment is designed to be a short description of an object. An
 * object comment is a sequence of non-zero (\0) ASCII characters with no
 * other formatting included by the library.
 *
 * @see IV.A.2.n. The Object Comment Message
 * @author Johann Sorel (Geomatys)
 */
public final class ObjectCommentMessage extends Message {

    /**
     * A null terminated ASCII character string.
     */
    private byte[] comment;

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        throw new IOException("TODO");
    }
}
