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
package org.geotoolkit.hdf.io;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Path;
import org.apache.sis.internal.storage.io.ChannelDataInput;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.hdf.SuperBlock;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Connector implements AutoCloseable {

    private final SuperBlock superblock;
    private final Path path;

    public Path getPath() {
        return path;
    }

    public SuperBlock getSuperblock() {
        return superblock;
    }

    public Connector(Path path) throws IllegalArgumentException, DataStoreException, IOException {
        this.path = path;

        final StorageConnector cnx = new StorageConnector(path);
        final ChannelDataInput c = cnx.getStorageAs(ChannelDataInput.class);
        cnx.closeAllExcept(c);
        c.buffer.order(ByteOrder.LITTLE_ENDIAN);
        try (final HDF5DataInput channel = new HDF5ChannelDataInput(c)) {
            superblock = new SuperBlock();
            superblock.read(channel);
        }
    }

    public HDF5DataInput createChannel() throws IllegalArgumentException, DataStoreException {
        final StorageConnector cnx = new StorageConnector(path);
        final ChannelDataInput c = cnx.getStorageAs(ChannelDataInput.class);
        cnx.closeAllExcept(c);
        c.buffer.order(ByteOrder.LITTLE_ENDIAN);
        final HDF5DataInput channel = new HDF5ChannelDataInput(c);
        channel.setOffsetSize(superblock.getSizeOfOffsets());
        channel.setLengthSize(superblock.getSizeOfLengths());
        return channel;
    }

    @Override
    public void close() {
    }
}
