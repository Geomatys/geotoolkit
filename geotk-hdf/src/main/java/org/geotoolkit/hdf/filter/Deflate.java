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
package org.geotoolkit.hdf.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Deflate implements Filter {

    @Override
    public byte[] decode(byte[] chunkdata) throws IOException {
        if (chunkdata.length == 0) {
            return chunkdata;
        }

        try (InputStream is = new InflaterInputStream(new ByteArrayInputStream(chunkdata, 0, chunkdata.length), new Inflater(false));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[4096];
            for(;;) {
                final int nb = is.read(buffer, 0, buffer.length);
                if (nb == -1) break;
                out.write(buffer, 0, nb);
            }
            return out.toByteArray();
        }
    }

    @Override
    public byte[] encode(byte[] chunkdata) throws IOException {
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream();
             final DeflaterOutputStream os = new DeflaterOutputStream(out)) {
            os.write(chunkdata);
            os.finish();
            return out.toByteArray();
        }
    }

    @Override
    public long uncompressedSize(byte[] chunkdata) throws IOException {
        int b4 = chunkdata[chunkdata.length - 4] & 0xFF;
        int b3 = chunkdata[chunkdata.length - 3] & 0xFF;
        int b2 = chunkdata[chunkdata.length - 2] & 0xFF;
        int b1 = chunkdata[chunkdata.length - 1] & 0xFF;
        return (b1 << 24) | (b2 << 16) + (b3 << 8) + b4;
    }

}
