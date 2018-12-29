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

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Little endian Data input stream.
 *
 * @author Johann Sorel (Geomatys)
 */
public class LEDataInputStream extends InputStream implements DataInput {

    private final DataInputStream ds;
    private final InputStream in;
    private final byte buffer[] = new byte[8];
    private long position = 0;

    public LEDataInputStream(final InputStream in) {
        this.in = in;
        this.ds = new DataInputStream(in);
    }

    /**
     * Get current stream position from the first written byte.
     * @return stream position
     */
    public long getPosition() {
        return position;
    }

    @Override
    public int available() throws IOException {
        return ds.available();
    }

    @Override
    public final short readShort() throws IOException {
        position+=2;
        ds.readFully(buffer, 0, 2);
        return (short) ((buffer[1] & 0xff) << 8
                | (buffer[0] & 0xff));
    }

    /**
     * Read multiple values in one call.
     *
     * @param nbValues number of valeus to read
     * @return array of values
     * @throws java.io.IOException
     */
    public short[] readShorts(int nbValues) throws IOException {
        final short[] array = new short[nbValues];
        for (int i=0; i<nbValues; i++) {
            array[i] = readShort();
        }
        return array;
    }

    @Override
    public final int readUnsignedShort() throws IOException {
        position+=2;
        ds.readFully(buffer, 0, 2);
        return ((buffer[1] & 0xff) << 8 | (buffer[0] & 0xff));
    }

    /**
     * Read multiple values in one call.
     *
     * @param nbValues number of valeus to read
     * @return array of values
     * @throws java.io.IOException
     */
    public int[] readUnsignedShorts(int nbValues) throws IOException {
        final int[] array = new int[nbValues];
        for (int i=0; i<nbValues; i++) {
            array[i] = readUnsignedShort();
        }
        return array;
    }

    @Override
    public final char readChar() throws IOException {
        position+=2;
        ds.readFully(buffer, 0, 2);
        return (char) ((buffer[1] & 0xff) << 8
                | (buffer[0] & 0xff));
    }

    @Override
    public final int readInt() throws IOException {
        position+=4;
        ds.readFully(buffer, 0, 4);
        return (buffer[3]) << 24
                | (buffer[2] & 0xff) << 16
                | (buffer[1] & 0xff) << 8
                | (buffer[0] & 0xff);
    }

    /**
     * Read multiple values in one call.
     *
     * @param nbValues number of valeus to read
     * @return array of values
     * @throws java.io.IOException
     */
    public int[] readInts(int nbValues) throws IOException {
        final int[] array = new int[nbValues];
        for (int i=0; i<nbValues; i++) {
            array[i] = readInt();
        }
        return array;
    }

    @Override
    public final long readLong() throws IOException {
        position+=8;
        ds.readFully(buffer, 0, 8);
        return (long) (buffer[7]) << 56
                | (long) (buffer[6] & 0xff) << 48
                | (long) (buffer[5] & 0xff) << 40
                | (long) (buffer[4] & 0xff) << 32
                | (long) (buffer[3] & 0xff) << 24
                | (long) (buffer[2] & 0xff) << 16
                | (long) (buffer[1] & 0xff) << 8
                | (long) (buffer[0] & 0xff);
    }

    /**
     * Read multiple values in one call.
     *
     * @param nbValues number of valeus to read
     * @return array of values
     * @throws java.io.IOException
     */
    public long[] readLongs(int nbValues) throws IOException {
        final long[] array = new long[nbValues];
        for (int i=0; i<nbValues; i++) {
            array[i] = readLong();
        }
        return array;
    }

    @Override
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    /**
     * Read multiple values in one call.
     *
     * @param nbValues number of valeus to read
     * @return array of values
     * @throws java.io.IOException
     */
    public float[] readFloats(int nbValues) throws IOException {
        final float[] array = new float[nbValues];
        for (int i=0; i<nbValues; i++) {
            array[i] = readFloat();
        }
        return array;
    }

    @Override
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    /**
     * Read multiple values in one call.
     *
     * @param nbValues number of valeus to read
     * @return array of values
     * @throws java.io.IOException
     */
    public double[] readDoubles(int nbValues) throws IOException {
        final double[] array = new double[nbValues];
        for (int i=0; i<nbValues; i++) {
            array[i] = readDouble();
        }
        return array;
    }

    @Override
    public final int read(byte b[], int off, int len) throws IOException {
        int nb = in.read(b, off, len);
        position+=nb;
        return nb;
    }

    @Override
    public final void readFully(byte b[]) throws IOException {
        ds.readFully(b, 0, b.length);
        position+=b.length;
    }

    @Override
    public final void readFully(byte b[], int off, int len) throws IOException {
        ds.readFully(b, off, len);
        position+=len;
    }

    @Override
    public final int skipBytes(int n) throws IOException {
        int nb = ds.skipBytes(n);
        position+=nb;
        return nb;
    }

    public final void skipFully(int n) throws IOException {
        while (n > 0) {
            n -= skipBytes(n);
        }
    }

    @Override
    public final boolean readBoolean() throws IOException {
        position++;
        return ds.readBoolean();
    }

    @Override
    public final byte readByte() throws IOException {
        position++;
        return ds.readByte();
    }

    @Override
    public int read() throws IOException {
        position++;
        return in.read();
    }

    @Override
    public final int readUnsignedByte() throws IOException {
        position++;
        return ds.readUnsignedByte();
    }

    @Override
    public final String readLine() throws IOException {
        return ds.readLine();
    }

    @Override
    public final String readUTF() throws IOException {
        return ds.readUTF();
    }

    /**
     * Ralign stream position, skipping any remaining byte to match given block size.
     * Older formats or javascript buffer requiere to be aligned on 2, 4 or 8 bytes
     * (short,int,float,double) to read datas.
     *
     * @param blockSize
     * @return number of bytes skipped
     */
    public int realign(int blockSize) throws IOException {
        final long position = getPosition();
        final long res = position % blockSize;
        if (res == 0) return 0;
        try {
            skipFully((int) (blockSize-res));
        } catch (EOFException ex) {
            return -1;
        }
        return (int) res;
    }

    @Override
    public final void close() throws IOException {
        ds.close();
    }


    public static short readUnsignedByte(final byte[] buffer, final int offset){
        return (short) (buffer[offset] & 0xff);
    }

    public static short readShort(final byte[] buffer, final int offset){
        return (short) ((buffer[offset+1] & 0xff) << 8
                | (buffer[offset+0] & 0xff));
    }

    public static int readUnsignedShort(final byte[] buffer, final int offset){
        return ((buffer[offset+1] & 0xff) << 8 | (buffer[offset+0] & 0xff));
    }

    public static char readChar(final byte[] buffer, final int offset){
        return (char) ((buffer[offset+1] & 0xff) << 8
                | (buffer[offset+0] & 0xff));
    }

    public static int readInt(final byte[] buffer, final int offset){
        return    (buffer[offset+3]) << 24
                | (buffer[offset+2] & 0xff) << 16
                | (buffer[offset+1] & 0xff) << 8
                | (buffer[offset+0] & 0xff);
    }

    public static long readUnsignedInt(final byte[] buffer, final int offset){
        return    (long) (buffer[offset+3] & 0xff) << 24
                | (long) (buffer[offset+2] & 0xff) << 16
                | (long) (buffer[offset+1] & 0xff) << 8
                | (long) (buffer[offset+0] & 0xff);
    }

    public static long readLong(final byte[] buffer, final int offset){
        return    (long) (buffer[offset+7]) << 56
                | (long) (buffer[offset+6] & 0xff) << 48
                | (long) (buffer[offset+5] & 0xff) << 40
                | (long) (buffer[offset+4] & 0xff) << 32
                | (long) (buffer[offset+3] & 0xff) << 24
                | (long) (buffer[offset+2] & 0xff) << 16
                | (long) (buffer[offset+1] & 0xff) << 8
                | (long) (buffer[offset+0] & 0xff);
    }

    public static float readFloat(final byte[] buffer, final int offset){
        return Float.intBitsToFloat(readInt(buffer,offset));
    }

    public static double readDouble(final byte[] buffer, final int offset){
        return Double.longBitsToDouble(readLong(buffer,offset));
    }

}
