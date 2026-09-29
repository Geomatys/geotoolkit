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
import java.util.List;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.hdf.io.HDF5DataInput;
import org.geotoolkit.hdf.message.Message;

/**
 * An object header contains the information needed to interpret an object (group, dataset, named datatype),
 * stored as a list of header messages. Two formats exist: version 1 ({@link ObjectHeaderV1})
 * and version 2 ({@link ObjectHeaderV2}).
 *
 * @author Johann Sorel (Geomatys)
 */
public interface ObjectHeader {

    /**
     * Returns all messages in this header.
     *
     * @return all messages in this header
     */
    List<Message> getMessages();

    /**
     * Reads this object header from the current channel position.
     *
     * @param channel channel positioned on the object header.
     * @throws IOException if an I/O error occurred.
     * @throws DataStoreException if the content is invalid or unsupported.
     */
    void read(HDF5DataInput channel) throws IOException, DataStoreException;

    /**
     * Creates an empty object header, to be filled by {@link #read(HDF5DataInput)}.
     *
     * @param version first byte of the object header: 1 for version 1, or 79
     *        (ASCII {@code 'O'} of the {@code "OHDR"} signature) for version 2.
     * @return the new object header.
     * @throws IllegalArgumentException if the version is not supported.
     */
    static ObjectHeader forVersion(int version) {
        return switch (version) {
            case 1 -> new ObjectHeaderV1();
            case 79 -> new ObjectHeaderV2();
            default -> throw new IllegalArgumentException("Unknowned header version " + version);
        };
    }
}
