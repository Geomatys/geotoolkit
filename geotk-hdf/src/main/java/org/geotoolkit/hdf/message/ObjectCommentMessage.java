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
import java.nio.charset.StandardCharsets;
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
 * object comment is a sequence of non-zero ({@code \0}) ASCII characters with no
 * other formatting included by the library.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsubsec_fmt3_dataobject_hdr_msg_comment">IV.A.2.n. The Object Comment Message</a>
 * @author Johann Sorel (Geomatys)
 */
public final class ObjectCommentMessage extends Message {

    /** A null terminated ASCII character string. */
    private String comment;

    /** Creates an empty message, filled by {@link #read(HDF5DataInput)}. */
    public ObjectCommentMessage() {
    }

    /**
     * Returns the object comment, a short description of the object.
     *
     * @return the object comment, a short description of the object
     */
    public String getComment() {
        return comment;
    }

    @Override
    public void read(HDF5DataInput channel) throws IOException {
        comment = channel.readNullTerminatedString(0, StandardCharsets.US_ASCII);
    }
}
