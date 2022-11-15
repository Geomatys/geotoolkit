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
import java.nio.charset.StandardCharsets;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 *
 * @see IV.A.2.q. The Object Header Continuation Message
 * @author Johann Sorel (Geomatys)
 */
public final class ObjectHeaderContinuationBlockV2 extends IOStructure {

    /**
     * The ASCII character string “OCHK” is used to indicate the beginning of an
     * object header continuation block. This gives file consistency checking
     * utilities a better chance of reconstructing a damaged file.
     */
    public static final byte[] SIGNATURE = "OCHK".getBytes(StandardCharsets.US_ASCII);


    @Override
    public void read(HDF5DataInput channel) throws IOException {
        channel.ensureSignature(SIGNATURE);
        throw new IOException("TODO");
    }


    private static class HeaderMessage {
        /**
         * Same format as version 1 of the object header, described above.
         */
        private int headerMessageType;
        /**
         * Same format as version 1 of the object header, described above.
         */
        private int sizeOfHeaderMessageData;
        /**
         * Same format as version 1 of the object header, described above.
         */
        private int headerMessageFlags;
        /**
         * This field stores the order that a message of a given type was
         * created in.
         * This field is present if bit 2 of flags is set.
         */
        private int headerMessageCreationOrder;
        /**
         * Same format as version 1 of the object header, described above.
         */
        private byte[] headerMessageData;

        public void read(HDF5DataInput channel) throws IOException {
            headerMessageType = channel.readUnsignedByte();
            sizeOfHeaderMessageData = channel.readUnsignedShort();
            headerMessageFlags = channel.readUnsignedByte();
            throw new IOException("TODO");
        }
    }
}
