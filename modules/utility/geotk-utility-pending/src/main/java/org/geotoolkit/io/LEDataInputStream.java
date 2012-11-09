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
        
    public LEDataInputStream(final InputStream in) {
        this.in = in;
        this.ds = new DataInputStream(in);
    }

    @Override
    public int available() throws IOException {
        return ds.available();
    }

    @Override
    public final short readShort() throws IOException {
        ds.readFully(buffer, 0, 2);
        return (short) ((buffer[1] & 0xff) << 8
                | (buffer[0] & 0xff));
    }

    @Override
    public final int readUnsignedShort() throws IOException {
        ds.readFully(buffer, 0, 2);
        return ((buffer[1] & 0xff) << 8 | (buffer[0] & 0xff));
    }

    @Override
    public final char readChar() throws IOException {
        ds.readFully(buffer, 0, 2);
        return (char) ((buffer[1] & 0xff) << 8
                | (buffer[0] & 0xff));
    }

    @Override
    public final int readInt() throws IOException {
        ds.readFully(buffer, 0, 4);
        return (buffer[3]) << 24
                | (buffer[2] & 0xff) << 16
                | (buffer[1] & 0xff) << 8
                | (buffer[0] & 0xff);
    }

    @Override
    public final long readLong() throws IOException {
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

    @Override
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public final int read(byte b[], int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    @Override
    public final void readFully(byte b[]) throws IOException {
        ds.readFully(b, 0, b.length);
    }

    @Override
    public final void readFully(byte b[], int off, int len) throws IOException {
        ds.readFully(b, off, len);
    }

    @Override
    public final int skipBytes(int n) throws IOException {
        return ds.skipBytes(n);
    }

    @Override
    public final boolean readBoolean() throws IOException {
        return ds.readBoolean();
    }

    @Override
    public final byte readByte() throws IOException {
        return ds.readByte();
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public final int readUnsignedByte() throws IOException {
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

    @Override
    public final void close() throws IOException {
        ds.close();
    }
    
}