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
import org.apache.sis.io.stream.ChannelDataInput;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.hdf.SuperBlock;
import static org.geotoolkit.hdf.io.HDF5DataInput.UNDEFINED_1;
import static org.geotoolkit.hdf.io.HDF5DataInput.UNDEFINED_2;
import static org.geotoolkit.hdf.io.HDF5DataInput.UNDEFINED_4;
import static org.geotoolkit.hdf.io.HDF5DataInput.UNDEFINED_8;

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


    /**
     * @see V. Appendix A: Definitions
     * @param offset tested address
     * @return true if offset in undefined
     */
    public boolean isDefinedOffset(long offset) {
        return !isUndefinedOffset(offset);
    }

    /**
     * @see V. Appendix A: Definitions
     * @param offset tested address
     * @return true if offset in undefined
     */
    public boolean isUndefinedOffset(long offset) {
        return isUndefinedOffset(offset, superblock.getSizeOfOffsets());
    }

    /**
     * @see V. Appendix A: Definitions
     * @param length tested length
     * @return true if offset in undefined
     */
    public boolean isDefinedLength(long length) {
        return !isUndefinedLength(length);
    }

    /**
     * @see V. Appendix A: Definitions
     * @param length tested length
     * @return true if offset in undefined
     */
    public boolean isUndefinedLength(long length) {
        return isUndefinedLength(length, superblock.getSizeOfLengths());
    }

    /**
     * @see V. Appendix A: Definitions
     * @param offset tested address
     * @return true if offset in undefined
     */
    public static boolean isDefinedOffset(long offset, int offsetSize) {
        return !isUndefinedOffset(offset, offsetSize);
    }

    /**
     * @see V. Appendix A: Definitions
     * @param offset tested address
     * @return true if offset in undefined
     */
    public static boolean isUndefinedOffset(long offset, int offsetSize) {
        return switch (offsetSize) {
            case 1 -> (offset == UNDEFINED_1);
            case 2 -> (offset == UNDEFINED_2);
            case 4 -> (offset == UNDEFINED_4);
            case 8 -> (offset == UNDEFINED_8);
            default -> throw new IllegalArgumentException("Unsupported size " + offsetSize);
        };
    }

    /**
     * @see V. Appendix A: Definitions
     * @param length tested length
     * @return true if offset in undefined
     */
    public static boolean isDefinedLength(long length, int lengthSize) {
        return !isUndefinedLength(length, lengthSize);
    }

    /**
     * @see V. Appendix A: Definitions
     * @param length tested length
     * @return true if offset in undefined
     */
    public static boolean isUndefinedLength(long length, int lengthSize) {
        return switch (lengthSize) {
            case 1 -> (length == UNDEFINED_1);
            case 2 -> (length == UNDEFINED_2);
            case 4 -> (length == UNDEFINED_4);
            case 8 -> (length == UNDEFINED_8);
            default -> throw new IllegalArgumentException("Unsupported size " + lengthSize);
        };
    }
}
