/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed out the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.internal.image.io;

import org.geotoolkit.lang.Decorator;

import javax.imageio.stream.IIOByteBuffer;
import javax.imageio.stream.ImageOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;


/**
 * An image output stream which delegate every method calls to an other stream.
 * This is a base class for subclasses wanting to wrap an existing stream with
 * additional functionalities.
 *
 * @author Quentin Boileau (Geomatys)
 */
@Decorator(ImageOutputStream.class)
public abstract class ImageOutputStreamProxy implements ImageOutputStream {
    /**
     * The wrapped image input stream.
     */
    protected final ImageOutputStream out;

    /**
     * Creates a new proxy wrapping the given stream.
     *
     * @param out The image input stream to wrap.
     */
    protected ImageOutputStreamProxy(final ImageOutputStream out) {
        this.out = out;
    }

    @Override public void write(int b)                                  throws IOException {out.write(b);}
    @Override public void write(byte[] b)                               throws IOException {out.write(b);}
    @Override public void write(byte[] b, int off, int len)             throws IOException {out.write(b);}
    @Override public void writeBoolean(boolean v)                       throws IOException {out.writeBoolean(v);}
    @Override public void writeByte(int v)                              throws IOException {out.writeByte(v);}
    @Override public void writeShort(int v)                             throws IOException {out.writeShort(v);}
    @Override public void writeChar(int v)                              throws IOException {out.writeChar(v);}
    @Override public void writeInt(int v)                               throws IOException {out.writeInt(v);}
    @Override public void writeLong(long v)                             throws IOException {out.writeLong(v);}
    @Override public void writeFloat(float v)                           throws IOException {out.writeFloat(v);}
    @Override public void writeDouble(double v)                         throws IOException {out.writeDouble(v);}
    @Override public void writeBytes(String s)                          throws IOException {out.writeBytes(s);}
    @Override public void writeChars(String s)                          throws IOException {out.writeChars(s);}
    @Override public void writeUTF(String s)                            throws IOException {out.writeUTF(s);}
    @Override public void writeShorts(short[] s, int off, int len)      throws IOException {out.writeShorts(s, off, len);}
    @Override public void writeChars(char[] c, int off, int len)        throws IOException {out.writeChars(c, off, len);}
    @Override public void writeInts(int[] i, int off, int len)          throws IOException {out.writeInts(i, off, len);}
    @Override public void writeLongs(long[] l, int off, int len)        throws IOException {out.writeLongs(l, off, len);}
    @Override public void writeFloats(float[] f, int off, int len)      throws IOException {out.writeFloats(f, off, len);}
    @Override public void writeDoubles(double[] d, int off, int len)    throws IOException {out.writeDoubles(d, off, len);}
    @Override public void writeBit(int bit)                             throws IOException {out.writeBit(bit);}
    @Override public void writeBits(long bits, int numBits)             throws IOException {out.writeBits(bits, numBits);}

    /*
        ImageInputStream methods
     */
    @Override public void      setByteOrder(ByteOrder byteOrder)                      {       out.setByteOrder(byteOrder);}
    @Override public ByteOrder getByteOrder()                                         {return out.getByteOrder();}
    @Override public int       read()                              throws IOException {return out.read();}
    @Override public int       read(byte[] b)                      throws IOException {return out.read(b);}
    @Override public int       read(byte[] b, int i, int n)        throws IOException {return out.read(b, i, n);}
    @Override public void      readBytes(IIOByteBuffer buf, int n) throws IOException {       out.readBytes(buf, n);}
    @Override public boolean   readBoolean()                       throws IOException {return out.readBoolean();}
    @Override public byte      readByte()                          throws IOException {return out.readByte();}
    @Override public int       readUnsignedByte()                  throws IOException {return out.readUnsignedByte();}
    @Override public short     readShort()                         throws IOException {return out.readShort();}
    @Override public int       readUnsignedShort()                 throws IOException {return out.readUnsignedShort();}
    @Override public char      readChar()                          throws IOException {return out.readChar();}
    @Override public int       readInt()                           throws IOException {return out.readInt();}
    @Override public long      readUnsignedInt()                   throws IOException {return out.readUnsignedInt();}
    @Override public long      readLong()                          throws IOException {return out.readLong();}
    @Override public float     readFloat()                         throws IOException {return out.readFloat();}
    @Override public double    readDouble()                        throws IOException {return out.readDouble();}
    @Override public String    readLine()                          throws IOException {return out.readLine();}
    @Override public String    readUTF()                           throws IOException {return out.readUTF();}
    @Override public void      readFully(byte  [] b, int i, int n) throws IOException {       out.readFully(b, i, n);}
    @Override public void      readFully(byte  [] b)               throws IOException {       out.readFully(b);}
    @Override public void      readFully(short [] b, int i, int n) throws IOException {       out.readFully(b, i, n);}
    @Override public void      readFully(char  [] b, int i, int n) throws IOException {       out.readFully(b, i, n);}
    @Override public void      readFully(int   [] b, int i, int n) throws IOException {       out.readFully(b, i, n);}
    @Override public void      readFully(long  [] b, int i, int n) throws IOException {       out.readFully(b, i, n);}
    @Override public void      readFully(float [] b, int i, int n) throws IOException {       out.readFully(b, i, n);}
    @Override public void      readFully(double[] b, int i, int n) throws IOException {       out.readFully(b, i, n);}
    @Override public long      getStreamPosition()                 throws IOException {return out.getStreamPosition();}
    @Override public int       getBitOffset()                      throws IOException {return out.getBitOffset();}
    @Override public void      setBitOffset(int bitOffset)         throws IOException {       out.setBitOffset(bitOffset);}
    @Override public int       readBit()                           throws IOException {return out.readBit();}
    @Override public long      readBits(int numBits)               throws IOException {return out.readBits(numBits);}
    @Override public long      length()                            throws IOException {return out.length();}
    @Override public int       skipBytes(int n)                    throws IOException {return out.skipBytes(n);}
    @Override public long      skipBytes(long n)                   throws IOException {return out.skipBytes(n);}
    @Override public void      seek(long pos)                      throws IOException {       out.seek(pos);}
    @Override public void      mark()                                                 {       out.mark();}
    @Override public void      reset()                             throws IOException {       out.reset();}
    @Override public void      flushBefore(long pos)               throws IOException {       out.flushBefore(pos);}
    @Override public void      flush()                             throws IOException {       out.flush();}
    @Override public long      getFlushedPosition()                                   {return out.getFlushedPosition();}
    @Override public boolean   isCached()                                             {return out.isCached();}
    @Override public boolean   isCachedMemory()                                       {return out.isCachedMemory();}
    @Override public boolean   isCachedFile()                                         {return out.isCachedFile();}
    @Override public void      close()                             throws IOException {       out.close();}
}
