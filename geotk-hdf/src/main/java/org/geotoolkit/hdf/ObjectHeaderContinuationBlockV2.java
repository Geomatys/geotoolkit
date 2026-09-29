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
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.hdf.io.HDF5DataInput;

/**
 * Continuation block of a version 2 object header.
 * <p>
 * Unlike version 1 continuation blocks, which are a bare list of messages, this
 * block has a signature and a trailing checksum. Its messages are formatted like
 * those of the owning header, which means they carry a creation order field when
 * that header tracks creation order. It can therefore not be decoded without its
 * owner, which is why it is not reachable through
 * {@link IOStructure#loadIdentifiedObject(HDF5DataInput)}.
 *
 * @see <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html#subsubsec_fmt3_dataobject_hdr_msg_continuation">IV.A.2.q. The Object Header Continuation Message</a>
 * @author Johann Sorel (Geomatys)
 */
public final class ObjectHeaderContinuationBlockV2 extends IOStructure {

    /**
     * The ASCII character string “OCHK” is used to indicate the beginning of an
     * object header continuation block. This gives file consistency checking
     * utilities a better chance of reconstructing a damaged file.
     */
    public static final byte[] SIGNATURE = "OCHK".getBytes(StandardCharsets.US_ASCII);

    /** Creates an empty block, filled by {@link #read(HDF5DataInput, ObjectHeaderV2, long)}. */
    public ObjectHeaderContinuationBlockV2() {
    }

    /**
     * A continuation block can not be read without the header owning it.
     *
     * @throws UnsupportedOperationException always
     * @see #read(HDF5DataInput, ObjectHeaderV2, long)
     */
    @Override
    public void read(HDF5DataInput channel) throws IOException {
        throw new UnsupportedOperationException("An object header continuation block "
                + "requires the owning header and the block length to be decoded, "
                + "use read(channel, owner, length).");
    }

    /**
     * Read this continuation block, appending its messages to the owning header.
     *
     * @param channel channel positioned on the block signature
     * @param owner header this block is a continuation of, not {@code null}
     * @param blockLength block length in bytes, from the Object Header Continuation message,
     *        covering the signature, the messages, the gap and the checksum.
     * @throws IOException if an I/O error occurred.
     * @throws DataStoreException if the signature or a message is invalid or unsupported.
     */
    public void read(HDF5DataInput channel, ObjectHeaderV2 owner, long blockLength) throws IOException, DataStoreException {
        final long start = channel.getStreamPosition();
        channel.ensureSignature(SIGNATURE);

        /*
        Messages occupy everything between the signature and the trailing
        checksum, minus a possible gap at the end.
        */
        final long end = start + blockLength - 4;
        owner.readMessages(channel, end);

        channel.seek(end);
        final int checksum = channel.readInt();
    }
}
