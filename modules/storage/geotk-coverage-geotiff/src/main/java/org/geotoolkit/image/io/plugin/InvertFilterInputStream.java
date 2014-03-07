/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.image.io.plugin;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <code>FilterInputStream</code> implementation itself simply overrides 
 * {@linkplain #read() } and {@linkplain #read(byte[], int, int)} methods.<br/><br/>
 * The {@linkplain #read() } method reverse all (8 - bits) of returned <code>byte</code>.<br/>
 * The {@linkplain #read(byte[], int, int)} method fill <code>byte[]</code> array with expected reversed <code>byte</code>.
 * 
 * @author Martin Desruisseaux (Geomatys)
 * @author Remi Marechal       (Geomatys)
 */
class InvertFilterInputStream extends FilterInputStream {
    
    InvertFilterInputStream(InputStream in){
        super(in);
    }

    /**
     * {@inheritDoc }
     * 
     * <br/><br/>Moreover the returned <code>byte</code> is reversed which means all their <code>bits</code> are reversed.
     */
    @Override
    public int read() throws IOException {
        final int superInt = super.read(); 
        return (superInt < 0) ? superInt : reverseByte(superInt);
    }

    /**
     * Read <code>len</code> <code>byte</code> reverse theirs <code>bits</code> (8 - bits) sens and put them into <code>b</code>
     * <code>byte array</code> at position <code>off</code>.
     * 
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset in the destination array <code>b</code>
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  NullPointerException If <code>b</code> is <code>null</code>.
     * @exception  IndexOutOfBoundsException If <code>off</code> is negative,
     * <code>len</code> is negative, or <code>len</code> is greater than
     * <code>b.length - off</code>
     * @exception  IOException  if an I/O error occurs.
     */
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int superInt = super.read(b, off, len);
        if (superInt < 0) return superInt;
        for (int p = off, maxPos = off + superInt; p < maxPos; p++) {
            b[p] = (byte) reverseByte(b[p] & 0xFF);
        }
        return superInt;
    }
    
    /**
     * Reverse <code>bits</code> sens from 8 first Integer <code>bits</code>.
     * 
     * @param b <code>byte</code> which will be invert.
     * @return inverted <code>byte</code>.
     */
    private static int reverseByte(final int b) {
        final int r = (int) (((b * 0x80200802L) & 0x0884422110L) * 0x0101010101L >>> 32);
        assert r == ((Integer.reverse(b) >>> 24) & 0xFF) : b;
        return r;
    }
}
