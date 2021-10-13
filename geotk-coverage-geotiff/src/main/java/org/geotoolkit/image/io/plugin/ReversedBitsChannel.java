/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2016, Geomatys
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import org.apache.sis.util.ArgumentChecks;

/**
 * A {@link ReadableByteChannel} with a {@code read} method that return all available bytes with theirs bits reversed.<br/>
 * This class is used for read Tiff or GeoTiff file with a tag tiff fill order at value 2 which means all read byte have their bits reversed.
 *
 * @see #read(java.nio.ByteBuffer)
 * @author Remi Marechal (Geomatys).
 */
final class ReversedBitsChannel implements ReadableByteChannel {

    /**
     * Array which contain reversed value.
     */
    private static final byte[] REVERSE = new byte[256];

    static {
        for (int p = 0; p < 256; p++) {
            REVERSE[p] = (byte) (Integer.reverse(p) >>> 24);
        }
    }

    /**
     * ReadableByteChannel where to read originals Bytes.
     */
    private final ReadableByteChannel input;

    /**
     * Creates a new {@code ReversedBitsChannel} wrapping the given {@code ReadableByteChannel}.
     *
     * @param input given {@code ReadableByteChannel}.
     */
    public ReversedBitsChannel(final ReadableByteChannel input) {
        ArgumentChecks.ensureNonNull("input", input);
        this.input = input;
    }

    /**
     * Reads a sequence of bytes from this channel into the given buffer.<br/>
     * All <code>bits</code> from each read <code>byte</code> are reverse which means <br/>
     * lower order bit becomme high order bit and vice - versa.<br/><br/>
     *
     * For example : <br/>
     * read <code>byte</code> : 11010100<br/>
     * becomme : 00101011<br/>
     * Same operation affected on all read <code>bytes</code> into given buffer.<br/>
     * Moreother buffer properties like position, limit and others are same like a normal read use.
     *
     * @param  dst
     *         The buffer into which bytes are to be transferred
     *
     * @return  The number of bytes read, possibly zero, or <tt>-1</tt> if the
     *          channel has reached end-of-stream
     *
     * @throws  NonReadableChannelException
     *          If this channel was not opened for reading
     *
     * @throws  ClosedChannelException
     *          If this channel is closed
     *
     * @throws  AsynchronousCloseException
     *          If another thread closes this channel
     *          while the read operation is in progress
     *
     * @throws  ClosedByInterruptException
     *          If another thread interrupts the current thread
     *          while the read operation is in progress, thereby
     *          closing the channel and setting the current thread's
     *          interrupt status
     *
     * @throws  IOException
     *          If some other I/O error occurs
     */
    @Override
    public int read(ByteBuffer dst) throws IOException {
        final int dstPos = dst.position();
        final int n = input.read(dst);
        if (n > 0) {
            reverseBytes(dst, dstPos, dstPos + n);
        }
        return n;
    }

    /**
     * Reverse <code>bits</code> sens of all byte from buffer between inclusive positionMin and exclusive positionMax.
     *
     * @param buffer buffer where byte will be reverse.
     * @param position first reverse byte position in buffer.
     * @param limit last exclusive reverse byte position in buffer.
     */
    private static void reverseBytes(final ByteBuffer buffer, int position, final int limit) {
        while (position < limit) {
            final byte b = buffer.get(position);
            buffer.put(position++, REVERSE[b & 0xFF]);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isOpen() {
        return input.isOpen();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void close() throws IOException {
        input.close();
    }
}
