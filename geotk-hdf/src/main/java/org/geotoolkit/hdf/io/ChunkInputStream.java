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

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.geotoolkit.hdf.filter.Filter;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ChunkInputStream extends InputStream {

    private final HDF5DataInput channel;
    private final long address;
    private final long compressedSize;
    private long uncompressedSize = -1;
    private final Filter[] filters;
    private InputStream back;
    private boolean finished = false;

    /**
     * @param filters applied in reversed order on the chunk datas.
     */
    public ChunkInputStream(HDF5DataInput channel, long address, long compressedSize, Filter... filters) {
        this.channel = channel;
        this.address = address;
        this.compressedSize = compressedSize;
        this.filters = filters;
    }

    public long getUncompressedSize() throws IOException {
        if (uncompressedSize == -1) {
            if (filters.length == 0) {
                uncompressedSize = compressedSize;
            } else {
                //no other choice then to decompress data
                getBack();
            }
        }
        return uncompressedSize;
    }

    private InputStream getBack() throws IOException {
        if (back == null) {
            channel.seek(address);
            byte[] array = channel.readNBytes(Math.toIntExact(compressedSize));
            //apply filters
            for (int i = filters.length - 1; i >= 0; i--) {
                array = filters[i].decode(array);
            }
            uncompressedSize = array.length;
            back = new ByteArrayInputStream(array);
        }
        return back;
    }

    @Override
    public int read() throws IOException {
        if (finished) {
            return -1;
        }
        return getBack().read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (finished) {
            return -1;
        }
        return getBack().read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (finished) {
            return -1;
        }
        return getBack().read(b, off, len);
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        if (finished) {
            return new byte[0];
        }
        return getBack().readAllBytes();
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        if (finished) {
            return new byte[0];
        }
        return getBack().readNBytes(len);
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        if (finished) {
            return 0;
        }
        return getBack().readNBytes(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        if (back == null & n >= getUncompressedSize()) {
            finished = true;
        }
        return getBack().skip(n);
    }

    @Override
    public void skipNBytes(long n) throws IOException {
        if (finished) {
            throw new EOFException();
        }
        getBack().skipNBytes(n);
    }

    @Override
    public int available() throws IOException {
        if (finished) {
            return 0;
        }
        return getBack().available();
    }

    @Override
    public void close() throws IOException {
        finished = true;
        if (back != null) {
            getBack().close();
        }
    }
}
