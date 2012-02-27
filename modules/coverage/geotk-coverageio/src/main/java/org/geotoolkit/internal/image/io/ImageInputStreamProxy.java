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
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.internal.image.io;

import java.io.IOException;
import java.nio.ByteOrder;
import javax.imageio.stream.IIOByteBuffer;
import javax.imageio.stream.ImageInputStream;

import org.geotoolkit.lang.Decorator;


/**
 * An image input stream which delegate every method calls to an other stream.
 * This is a base class for subclasses wanting to wrap an existing stream with
 * additional functionalities.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.14
 * @module
 */
@Decorator(ImageInputStream.class)
public abstract class ImageInputStreamProxy implements ImageInputStream {
    /**
     * The wrapped image input stream.
     */
    protected final ImageInputStream in;

    /**
     * Creates a new proxy wrapping the given stream.
     *
     * @param in The image input stream to wrap.
     */
    protected ImageInputStreamProxy(final ImageInputStream in) {
        this.in = in;
    }

    @Override public void      setByteOrder(ByteOrder byteOrder)                      {       in.setByteOrder(byteOrder);}
    @Override public ByteOrder getByteOrder()                                         {return in.getByteOrder();}
    @Override public int       read()                              throws IOException {return in.read();}
    @Override public int       read(byte[] b)                      throws IOException {return in.read(b);}
    @Override public int       read(byte[] b, int i, int n)        throws IOException {return in.read(b, i, n);}
    @Override public void      readBytes(IIOByteBuffer buf, int n) throws IOException {       in.readBytes(buf, n);}
    @Override public boolean   readBoolean()                       throws IOException {return in.readBoolean();}
    @Override public byte      readByte()                          throws IOException {return in.readByte();}
    @Override public int       readUnsignedByte()                  throws IOException {return in.readUnsignedByte();}
    @Override public short     readShort()                         throws IOException {return in.readShort();}
    @Override public int       readUnsignedShort()                 throws IOException {return in.readUnsignedShort();}
    @Override public char      readChar()                          throws IOException {return in.readChar();}
    @Override public int       readInt()                           throws IOException {return in.readInt();}
    @Override public long      readUnsignedInt()                   throws IOException {return in.readUnsignedInt();}
    @Override public long      readLong()                          throws IOException {return in.readLong();}
    @Override public float     readFloat()                         throws IOException {return in.readFloat();}
    @Override public double    readDouble()                        throws IOException {return in.readDouble();}
    @Override public String    readLine()                          throws IOException {return in.readLine();}
    @Override public String    readUTF()                           throws IOException {return in.readUTF();}
    @Override public void      readFully(byte  [] b, int i, int n) throws IOException {       in.readFully(b, i, n);}
    @Override public void      readFully(byte  [] b)               throws IOException {       in.readFully(b);}
    @Override public void      readFully(short [] b, int i, int n) throws IOException {       in.readFully(b, i, n);}
    @Override public void      readFully(char  [] b, int i, int n) throws IOException {       in.readFully(b, i, n);}
    @Override public void      readFully(int   [] b, int i, int n) throws IOException {       in.readFully(b, i, n);}
    @Override public void      readFully(long  [] b, int i, int n) throws IOException {       in.readFully(b, i, n);}
    @Override public void      readFully(float [] b, int i, int n) throws IOException {       in.readFully(b, i, n);}
    @Override public void      readFully(double[] b, int i, int n) throws IOException {       in.readFully(b, i, n);}
    @Override public long      getStreamPosition()                 throws IOException {return in.getStreamPosition();}
    @Override public int       getBitOffset()                      throws IOException {return in.getBitOffset();}
    @Override public void      setBitOffset(int bitOffset)         throws IOException {       in.setBitOffset(bitOffset);}
    @Override public int       readBit()                           throws IOException {return in.readBit();}
    @Override public long      readBits(int numBits)               throws IOException {return in.readBits(numBits);}
    @Override public long      length()                            throws IOException {return in.length();}
    @Override public int       skipBytes(int n)                    throws IOException {return in.skipBytes(n);}
    @Override public long      skipBytes(long n)                   throws IOException {return in.skipBytes(n);}
    @Override public void      seek(long pos)                      throws IOException {       in.seek(pos);}
    @Override public void      mark()                                                 {       in.mark();}
    @Override public void      reset()                             throws IOException {       in.reset();}
    @Override public void      flushBefore(long pos)               throws IOException {       in.flushBefore(pos);}
    @Override public void      flush()                             throws IOException {       in.flush();}
    @Override public long      getFlushedPosition()                                   {return in.getFlushedPosition();}
    @Override public boolean   isCached()                                             {return in.isCached();}
    @Override public boolean   isCachedMemory()                                       {return in.isCachedMemory();}
    @Override public boolean   isCachedFile()                                         {return in.isCachedFile();}
    @Override public void      close()                             throws IOException {       in.close();}
}
