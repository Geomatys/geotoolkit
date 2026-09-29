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

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import org.geotoolkit.hdf.HDF5Provider;

/**
 * Fletcher32 checksum filter : the chunk carries a four bytes checksum after its
 * data, which decoding strips and verifies.
 * <p>
 * The HDF5 library writes the checksum in the byte order of the machine that
 * created the file, so both readings are accepted before reporting a mismatch.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class Fletcher32 implements Filter {

    /** Creates a new Fletcher32 checksum filter. */
    public Fletcher32() {
    }

    @Override
    public byte[] decode(byte[] chunkdata) throws IOException {
        if (chunkdata.length < 4) {
            throw new IOException("Chunk of " + chunkdata.length
                    + " bytes is too short to carry a Fletcher32 checksum");
        }
        final int length = chunkdata.length - 4;
        final byte[] datas = Arrays.copyOf(chunkdata, length);

        final int computed = checksum(datas, length);
        final int littleEndian = (chunkdata[length] & 0xFF)
                | ((chunkdata[length + 1] & 0xFF) << 8)
                | ((chunkdata[length + 2] & 0xFF) << 16)
                | ((chunkdata[length + 3] & 0xFF) << 24);
        final int bigEndian = Integer.reverseBytes(littleEndian);
        if (computed != littleEndian && computed != bigEndian) {
            HDF5Provider.LOGGER.log(Level.WARNING, () -> "Fletcher32 checksum mismatch,"
                    + " computed 0x" + Integer.toHexString(computed)
                    + " but found 0x" + Integer.toHexString(littleEndian));
        }
        return datas;
    }

    @Override
    public byte[] encode(byte[] chunkdata) throws IOException {
        final byte[] out = Arrays.copyOf(chunkdata, chunkdata.length + 4);
        final int value = checksum(chunkdata, chunkdata.length);
        out[chunkdata.length]     = (byte) (value);
        out[chunkdata.length + 1] = (byte) (value >>> 8);
        out[chunkdata.length + 2] = (byte) (value >>> 16);
        out[chunkdata.length + 3] = (byte) (value >>> 24);
        return out;
    }

    /**
     * Returns the Fletcher32 checksum over 16 bits words, as {@code H5_checksum_fletcher32} computes it.
     *
     * @param datas bytes to checksum
     * @param length number of bytes to use, starting at index 0
     * @return the Fletcher32 checksum over 16 bits words, as {@code H5_checksum_fletcher32} computes it
     */
    static int checksum(byte[] datas, int length) {
        int sum1 = 0;
        int sum2 = 0;
        int index = 0;
        int remaining = length / 2;
        while (remaining > 0) {
            /*
            Reduce at most every 360 words, which is the largest count that can
            not overflow the accumulators.
            */
            int block = Math.min(remaining, 360);
            remaining -= block;
            while (block-- > 0) {
                final int word = ((datas[index] & 0xFF) << 8) | (datas[index + 1] & 0xFF);
                index += 2;
                sum1 += word;
                sum2 += sum1;
            }
            sum1 = (sum1 & 0xFFFF) + (sum1 >>> 16);
            sum2 = (sum2 & 0xFFFF) + (sum2 >>> 16);
        }
        if ((length % 2) != 0) {
            //a trailing odd byte is the high half of a word
            sum1 += (datas[index] & 0xFF) << 8;
            sum2 += sum1;
            sum1 = (sum1 & 0xFFFF) + (sum1 >>> 16);
            sum2 = (sum2 & 0xFFFF) + (sum2 >>> 16);
        }
        sum1 = (sum1 & 0xFFFF) + (sum1 >>> 16);
        sum2 = (sum2 & 0xFFFF) + (sum2 >>> 16);
        return (sum2 << 16) | sum1;
    }
}
