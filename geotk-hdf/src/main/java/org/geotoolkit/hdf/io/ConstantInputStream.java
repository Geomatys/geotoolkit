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
import java.io.InputStream;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ConstantInputStream extends InputStream {

    private final long size;
    private final byte[] pattern;
    private long position = 0;

    public ConstantInputStream(long size, byte[] pattern) {
        this.size = size;
        this.pattern = pattern;
    }

    @Override
    public int read() throws IOException {
        if (position >= size) return -1;
        final byte b = pattern[(int) (position % pattern.length)];
        position++;
        return b;
    }

    @Override
    public int available() throws IOException {
        return Math.toIntExact(size - position);
    }

}
