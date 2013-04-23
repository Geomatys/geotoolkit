/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.internal.io;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;


/**
 * Utility methods related to NIO buffers.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 * @module
 */
public final class Buffers extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private Buffers() {
    }

    /**
     * Returns the size in bits of one data element stored in the given buffer.
     * If the given buffer is {@code null}, then this method returns 0. If the
     * type is not recognized, then an exception is thrown.
     *
     * @param  buffer The buffer for which we want the size of one data element.
     * @return The size of one data element, or 0 if the given buffer was null.
     * @throws IllegalArgumentException If the type of the buffer is not known.
     */
    public static int getDataSize(final Buffer buffer) throws IllegalArgumentException {
        if (buffer == null)                 return 0;
        if (buffer instanceof ByteBuffer)   return Byte.SIZE;
        if (buffer instanceof CharBuffer)   return Character.SIZE;
        if (buffer instanceof ShortBuffer)  return Short.SIZE;
        if (buffer instanceof IntBuffer)    return Integer.SIZE;
        if (buffer instanceof LongBuffer)   return Long.SIZE;
        if (buffer instanceof FloatBuffer)  return Float.SIZE;
        if (buffer instanceof DoubleBuffer) return Double.SIZE;
        throw new IllegalArgumentException(Errors.format(Errors.Keys.UNKNOWN_TYPE_1, buffer.getClass()));
    }

    /**
     * Wraps fully the given buffer. This is different from the standard method which
     * wraps only the part starting at the current position up to the remaining bytes.
     *
     * @param  buffer The buffer to wrap.
     * @return The wrapped buffer.
     */
    public static CharBuffer asCharBuffer(final ByteBuffer buffer) {
        final int pos = buffer.position();
        final int lim = buffer.limit();
        buffer.position(0).limit(buffer.capacity());
        final CharBuffer nb = buffer.asCharBuffer();
        buffer.position(pos).limit(lim);
        return nb;
    }

    /**
     * Wraps fully the given buffer. This is different from the standard method which
     * wraps only the part starting at the current position up to the remaining bytes.
     *
     * @param  buffer The buffer to wrap.
     * @return The wrapped buffer.
     */
    public static ShortBuffer asShortBuffer(final ByteBuffer buffer) {
        final int pos = buffer.position();
        final int lim = buffer.limit();
        buffer.position(0).limit(buffer.capacity());
        final ShortBuffer nb = buffer.asShortBuffer();
        buffer.position(pos).limit(lim);
        return nb;
    }

    /**
     * Wraps fully the given buffer. This is different from the standard method which
     * wraps only the part starting at the current position up to the remaining bytes.
     *
     * @param  buffer The buffer to wrap.
     * @return The wrapped buffer.
     */
    public static IntBuffer asIntBuffer(final ByteBuffer buffer) {
        final int pos = buffer.position();
        final int lim = buffer.limit();
        buffer.position(0).limit(buffer.capacity());
        final IntBuffer nb = buffer.asIntBuffer();
        buffer.position(pos).limit(lim);
        return nb;
    }

    /**
     * Wraps fully the given buffer. This is different from the standard method which
     * wraps only the part starting at the current position up to the remaining bytes.
     *
     * @param  buffer The buffer to wrap.
     * @return The wrapped buffer.
     */
    public static LongBuffer asLongBuffer(final ByteBuffer buffer) {
        final int pos = buffer.position();
        final int lim = buffer.limit();
        buffer.position(0).limit(buffer.capacity());
        final LongBuffer nb = buffer.asLongBuffer();
        buffer.position(pos).limit(lim);
        return nb;
    }

    /**
     * Wraps fully the given buffer. This is different from the standard method which
     * wraps only the part starting at the current position up to the remaining bytes.
     *
     * @param  buffer The buffer to wrap.
     * @return The wrapped buffer.
     */
    public static FloatBuffer asFloatBuffer(final ByteBuffer buffer) {
        final int pos = buffer.position();
        final int lim = buffer.limit();
        buffer.position(0).limit(buffer.capacity());
        final FloatBuffer nb = buffer.asFloatBuffer();
        buffer.position(pos).limit(lim);
        return nb;
    }

    /**
     * Wraps fully the given buffer. This is different from the standard method which
     * wraps only the part starting at the current position up to the remaining bytes.
     *
     * @param  buffer The buffer to wrap.
     * @return The wrapped buffer.
     */
    public static DoubleBuffer asDoubleBuffer(final ByteBuffer buffer) {
        final int pos = buffer.position();
        final int lim = buffer.limit();
        buffer.position(0).limit(buffer.capacity());
        final DoubleBuffer nb = buffer.asDoubleBuffer();
        buffer.position(pos).limit(lim);
        return nb;
    }
}
