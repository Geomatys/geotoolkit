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
import org.geotoolkit.hdf.io.HDF5DataInput;
import org.geotoolkit.hdf.message.Message;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface ObjectHeader {

    /**
     * Get all header messages.
     * @return messages in this header
     */
    List<Message> getMessages();

    void read(HDF5DataInput channel) throws IOException;

    public static ObjectHeader forVersion(int version) {
        return switch (version) {
            case 1 -> new ObjectHeaderV1();
            case 79 -> new ObjectHeaderV2();
            default -> throw new IllegalArgumentException("Unknowned header version " + version);
        };
    }
}
