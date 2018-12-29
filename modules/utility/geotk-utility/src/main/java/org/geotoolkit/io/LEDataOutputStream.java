/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.io;

import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Little endian Data output stream.
 *
 * @author Johann Sorel (Geomatys)
 */
public class LEDataOutputStream extends FilterOutputStream implements DataOutput {

    private long position = 0;

    public LEDataOutputStream(OutputStream out) {
        super(out);
    }

    /**
     * Get current stream position from the first written byte.
     * @return stream position
     */
    public long getPosition() {
        return position;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        position++;
    }

    @Override
    public void write(byte[] data, int offset, int length) throws IOException {
        out.write(data, offset, length);
        position+=length;
    }

    @Override
    public void writeBoolean(boolean b) throws IOException {
        this.write( b ? 1 : 0 );
    }

    @Override
    public void writeByte(int b) throws IOException {
        out.write(b);
        position++;
    }

    @Override
    public void writeShort(int s) throws IOException {
        out.write((s      ) & 0xFF);
        out.write((s >>> 8) & 0xFF);
        position+=2;
    }

    /**
     * Write multiple values in one call.
     *
     * @param values values to write
     * @throws IOException
     */
    public final void writeShorts(short[] values) throws IOException {
        for (short value : values) {
            writeShort(value);
        }
    }

    public void writeUnsignedShort(int s) throws IOException {
        writeShort(s);
    }

    /**
     * Write multiple values in one call.
     *
     * @param values values to write
     * @throws IOException
     */
    public final void writeUnsignedShorts(int[] values) throws IOException {
        for (int value : values) {
            writeUnsignedShort(value);
        }
    }

    @Override
    public void writeChar(int c) throws IOException {
        out.write((c      ) & 0xFF);
        out.write((c >>> 8) & 0xFF);
        position+=2;
    }

    @Override
    public void writeInt(int i) throws IOException {
        out.write((i       ) & 0xFF);
        out.write((i >>>  8) & 0xFF);
        out.write((i >>> 16) & 0xFF);
        out.write((i >>> 24) & 0xFF);
        position+=4;
    }

    /**
     * Write multiple values in one call.
     *
     * @param values values to write
     * @throws IOException
     */
    public final void writeInts(int[] values) throws IOException {
        for (int value : values) {
            writeInt(value);
        }
    }

    @Override
    public void writeLong(long l) throws IOException {
        out.write((int) (l       ) & 0xFF);
        out.write((int) (l >>> 8 ) & 0xFF);
        out.write((int) (l >>> 16) & 0xFF);
        out.write((int) (l >>> 24) & 0xFF);
        out.write((int) (l >>> 32) & 0xFF);
        out.write((int) (l >>> 40) & 0xFF);
        out.write((int) (l >>> 48) & 0xFF);
        out.write((int) (l >>> 56) & 0xFF);
        position+=8;
    }

    /**
     * Write multiple values in one call.
     *
     * @param values values to write
     * @throws IOException
     */
    public final void writeLongs(long[] values) throws IOException {
        for (long value : values) {
            writeLong(value);
        }
    }

    @Override
    public final void writeFloat(float f) throws IOException {
        this.writeInt(Float.floatToIntBits(f));
    }

    /**
     * Write multiple values in one call.
     *
     * @param values values to write
     * @throws IOException
     */
    public final void writeFloats(float[] values) throws IOException {
        for (float value : values) {
            writeFloat(value);
        }
    }

    @Override
    public final void writeDouble(double d) throws IOException {
        this.writeLong(Double.doubleToLongBits(d));
    }

    /**
     * Write multiple values in one call.
     *
     * @param values values to write
     * @throws IOException
     */
    public final void writeDoubles(double[] values) throws IOException {
        for (double value : values) {
            writeDouble(value);
        }
    }

    @Override
    public void writeBytes(String s) throws IOException {
        for (int i=0,n=s.length(); i<n; i++) {
            out.write((byte) s.charAt(i));
        }
        position+=s.length();
    }

    @Override
    public void writeChars(String s) throws IOException {
        for (int i=0,n=s.length(); i<n; i++) {
            final char c = s.charAt(i);
            out.write((c      ) & 0xFF);
            out.write((c >>> 8) & 0xFF);
        }
        position+=s.length()*2;
    }

    @Override
    public void writeUTF(String s) throws IOException {
        throw new IOException("Not supported");
    }

    /**
     * Align byte stream to a padding value, 0 bytes will be added until padding
     * value is reached.
     * Padding values are usually very small, 2, 4 or 8 bytes to ensure primitive
     * types such as Short, Integer, Float are aligned in memory.
     *
     * @param padding value to realign.
     * @throws IOException
     */
    public void realign(int padding) throws IOException{
        while ((position%padding) != 0) {
            write(0);
        }
    }

}
