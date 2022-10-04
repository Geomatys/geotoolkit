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
package org.geotoolkit.hdf.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.geotoolkit.hdf.heap.GlobalHeap;
import org.geotoolkit.hdf.heap.GlobalHeapId;
import org.geotoolkit.hdf.heap.GlobalHeapObject;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public interface HDF5DataInput extends AutoCloseable {

    public static final long UNDEFINED_1 = 0xFFl;
    public static final long UNDEFINED_2 = 0xFFFFl;
    public static final long UNDEFINED_4 = 0xFFFFFFFFl;
    public static final long UNDEFINED_8 = 0xFFFFFFFFFFFFFFFFl;

    void skipFully(long nb) throws IOException;

    int readUnsignedByte() throws IOException;

    int readUnsignedShort() throws IOException;

    byte readByte() throws IOException;

    int readShort() throws IOException;

    int readInt() throws IOException;

    long readUnsignedInt() throws IOException;

    double readDouble() throws IOException;

    float readFloat() throws IOException;

    byte[] readNBytes(int nb) throws IOException;

    int[] readInts(int nb) throws IOException;

    long readLong() throws IOException;

    void mark() throws IOException;

    void reset() throws IOException;

    long getStreamPosition() throws IOException;

    void seek(long position) throws IOException;

    ByteOrder order() throws IOException;

    void order(ByteOrder order) throws IOException;

    long readBits(int nb) throws IOException;

    void skipRemainingBits() throws IOException;

    /**
     * Read an unsigned int of variable byte length.
     */
    int readUnsignedInt(int nbBytes) throws IOException;

    void close() throws IOException;

    ////////////////////////////////////////////////////////////////////////////
    // specific to HDF format //////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Align stream on given padding.
     * @param padding padding size
     */
    default void realign(int padding) throws IOException {
        if (padding > 1) {
            final long position = getStreamPosition();
            final long r = position % padding;
            if (r == 0) return;
            skipFully((int) (padding - r));
        }
    }

    /**
     * Align stream on given padding from given offset.
     * @param start position from where to start padding.
     * @param padding padding size
     */
    default void realign(long start, int padding) throws IOException {
        if (padding > 1) {
            final long position = getStreamPosition() - start;
            final long r = position % padding;
            if (r == 0) return;
            skipFully((int) (padding - r));
        }
    }

    /**
     * Check and skip the upcoming bytes contain the given signature
     */
    default void ensureSignature(byte[] signature) throws IOException {
        final byte[] array = readNBytes(signature.length);
        if (!Arrays.equals(array, signature)) {
            throw new IOException("Incorrect block signature, expected " + new String(signature) +" but was " + new String(array));
        }
    }

    /**
     * Check and skip the upcoming unsigned byte version matchs.
     * @return version
     */
    default int ensureVersion(int ... expectedVersions) throws IOException {
        final int version = readUnsignedByte();
        for (int expectedVersion : expectedVersions) {
            if (version == expectedVersion) return version;
        }
        throw new IOException("Incorrect version, expected " + Arrays.toString(expectedVersions) +" but was " + version);
    }

    GlobalHeap getGlobalHeap(long address) throws IOException;

    GlobalHeapObject getGlobalHeapObject(GlobalHeapId id) throws IOException;

    default int readUnsignedInt24() throws IOException {
        final int b0 = readUnsignedByte();
        final int b1 = readUnsignedByte();
        final int b2 = readUnsignedByte();
        return b0 | (b1<<8) | (b2<<16);
    }

    /**
     * Read a null terminated string.
     * @param padding string may be padded with multiple null, indicate padding here.
     * @return result string
     */
    default String readNullTerminatedString(int padding, Charset charset) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (;;) {
            byte b = readByte();
            if (b == 0) break;
            out.write(b);
        }
        if (padding > 1) {
            final int r = (out.size()+1) % padding; //+1 for the end null
            if (r > 0) {
                final int remaining = padding - r;
                skipFully(remaining);
            }
        }
        return new String(out.toByteArray(), charset);
    }

    void setOffsetSize(int offsetSize);

    int getOffsetSize();

    void setLengthSize(int lengthSize);

    int getLengthSize();

    long readOffset() throws IOException;

    long readLength() throws IOException;

    /**
     * @see V. Appendix A: Definitions
     * @param offset tested address
     * @return true if offset in undefined
     */
    default boolean isDefinedOffset(long offset) {
        return !isUndefinedOffset(offset);
    }

    /**
     * @see V. Appendix A: Definitions
     * @param offset tested address
     * @return true if offset in undefined
     */
    default boolean isUndefinedOffset(long offset) {
        final int offsetSize = getOffsetSize();
        return switch (offsetSize) {
            case 1 -> (offset == UNDEFINED_1);
            case 2 -> (offset == UNDEFINED_2);
            case 4 -> (offset == UNDEFINED_4);
            case 8 -> (offset == UNDEFINED_8);
            default -> throw new IllegalArgumentException("Unsupported size " + offsetSize);
        };
    }

    /**
     * @see V. Appendix A: Definitions
     * @param length tested length
     * @return true if offset in undefined
     */
    default boolean isDefinedLength(long length) {
        return !isUndefinedLength(length);
    }

    /**
     * @see V. Appendix A: Definitions
     * @param length tested length
     * @return true if offset in undefined
     */
    default boolean isUndefinedLength(long length) {
        final int lengthSize = getLengthSize();
        return switch (lengthSize) {
            case 1 -> (length == UNDEFINED_1);
            case 2 -> (length == UNDEFINED_2);
            case 4 -> (length == UNDEFINED_4);
            case 8 -> (length == UNDEFINED_8);
            default -> throw new IllegalArgumentException("Unsupported size " + lengthSize);
        };
    }

}
