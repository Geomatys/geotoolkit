/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.stream;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import javax.imageio.stream.IIOByteBuffer;
import javax.imageio.stream.ImageInputStream;

import org.geotoolkit.internal.io.Buffers;
import org.geotoolkit.resources.Errors;

import static org.apache.sis.util.ArgumentChecks.ensureBetween;


/**
 * An {@linkplain ImageInputStream Image Input Stream} using a {@linkplain ReadableByteChannel
 * Readable Byte Channel} as the data source. Using this class is similar to using the code below:
 *
 * {@preformat java
 *     import javax.imageio.ImageIO;
 *     import java.nio.channels.Channels;
 *
 *     // Omitting class and method declaration...
 *
 *     ReadableByteChannel channel = ...;
 *     ImageInputStream stream = ImageIO.createImageInputStream(Channels.newInputStream(channel));
 * }
 *
 * except that:
 * <p>
 * <ul>
 *   <li>This class is both an {@link InputStream} and an {@link ImageInputStream}.</li>
 *   <li>It delegates most of the work to the underlying {@link ByteBuffer} (by contrast,
 *       the standard {@link javax.imageio.stream.ImageInputStreamImpl} performs many work
 *       itself, including the {@linkplain ByteOrder byte order} handling).</li>
 *   <li>This class can uses an existing {@link ByteBuffer} supplied by the caller.</li>
 * </ul>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @see FileImageInputStream
 * @see javax.imageio.stream.ImageInputStreamImpl
 * @see javax.imageio.ImageIO#createImageInputStream(Object)
 * @see java.nio.channels.Channels#newInputStream(ReadableByteChannel)
 *
 * @since 3.07
 * @module
 */
public class ChannelImageInputStream extends InputStream implements ImageInputStream {
    /**
     * The channel from which to read bytes.
     * This is the channel given at construction time.
     */
    protected final ReadableByteChannel channel;

    /**
     * The byte buffer to use for reading bytes from the channel.
     * This is the buffer given at construction time.
     */
    protected final ByteBuffer buffer;

    /**
     * A duplicated {@linkplain #buffer}. Will be created only if needed.
     */
    private transient ByteBuffer dupBuffer;

    /**
     * A view of {@link #buffer}, created only if needed.
     */
    private transient CharBuffer charBuffer;

    /**
     * A view of {@link #buffer}, created only if needed.
     */
    private transient ShortBuffer shortBuffer;

    /**
     * A view of {@link #buffer}, created only if needed.
     */
    private transient IntBuffer intBuffer;

    /**
     * A view of {@link #buffer}, created only if needed.
     */
    private transient LongBuffer longBuffer;

    /**
     * A view of {@link #buffer}, created only if needed.
     */
    private transient FloatBuffer floatBuffer;

    /**
     * A view of {@link #buffer}, created only if needed.
     */
    private transient DoubleBuffer doubleBuffer;

    /**
     * A string buffer, created only if needed.
     */
    private transient StringBuilder stringBuffer;

    /**
     * The position within the stream of the first byte in the buffer.
     * The stream position will be this value plus {@code buffer.position()}.
     */
    private long bufferPosition;

    /**
     * The current bit offset within the stream.
     */
    private int bitOffset;

    /**
     * The most recent mark, or {@code null} if none.
     */
    private Mark mark;

    /**
     * A mark pushed by the {@link ChannelImageInputStream#mark()} method
     * and pooled by the {@link ChannelImageInputStream#reset()} method.
     */
    private static final class Mark {
        final long position;
        final int  bitOffset;
        final Mark next;

        Mark(long position, int bitOffset, Mark next) {
            this.position  = position;
            this.bitOffset = bitOffset;
            this.next      = next;
        }
    }

    /**
     * Creates a new image input stream using the given channel.
     * A default buffer of the given size is created.
     *
     * @param channel The channel to use.
     * @param size The buffer size.
     */
    public ChannelImageInputStream(final ReadableByteChannel channel, final int size) {
        if (size < 2*(Double.SIZE / Byte.SIZE)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_2, "size", size));
        }
        this.channel = channel;
        buffer = ByteBuffer.allocate(size);
        buffer.limit(0);
    }

    /**
     * Creates a new image input stream using the given channel and buffer.
     * <strong>The buffer is assumed to have valid initial content</strong>.
     * If not, then the following should be invoked before to give the buffer
     * to this constructor:
     *
     * {@preformat java
     *     buffer.clear().limit(0);
     * }
     *
     * @param channel The channel to use.
     * @param buffer  The buffer to use.
     */
    public ChannelImageInputStream(final ReadableByteChannel channel, final ByteBuffer buffer) {
        this.channel = channel;
        this.buffer  = buffer;
        if (buffer.capacity() < 2*(Double.SIZE / Byte.SIZE)) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_ARGUMENT_1, "buffer"));
        }
    }

    /**
     * Sets the desired byte order for future reads of data values from this stream.
     * The default value is {@link ByteOrder#BIG_ENDIAN BIG_ENDIAN}.
     */
    @Override
    public void setByteOrder(final ByteOrder byteOrder) {
        if (!byteOrder.equals(buffer.order())) {
            clearViews();
            buffer.order(byteOrder);
        }
    }

    /**
     * Returns the byte order with which data values will be read from this stream.
     */
    @Override
    public ByteOrder getByteOrder() {
        return buffer.order();
    }

    /**
     * Returns the duplicated buffer, creating it if needed.
     */
    private ByteBuffer getDuplicatedBuffer() {
        ByteBuffer dupBuffer = this.dupBuffer;
        if (dupBuffer == null) {
            this.dupBuffer = dupBuffer = buffer.duplicate();
        }
        return dupBuffer;
    }
    /**
     * Updates the {@linkplain #bufferPosition} and fills the {@linkplain #buffer}.
     * This method and the {@link #ensureRemaining(int)} method below should be the
     * only ones invoked for all read operations on the channel.
     */
    private int fillBuffer() throws IOException {
        bufferPosition += buffer.position();
        buffer.clear();
        final int n = channel.read(buffer);
        buffer.flip();
        return n;
    }

    /**
     * Ensures that there is at least one byte remaining in the buffer.
     *
     * @throws IOException If an error occurred while reading (including EOF).
     */
    private void ensureRemaining() throws IOException {
        if (!buffer.hasRemaining() && fillBuffer() <= 0) {
            throw new EOFException();
        }
    }

    /**
     * Ensures that there is at least <var>n</var> remaining bytes in the buffer,
     * reading new bytes if necessary. The number of requested bytes shall not be
     * greater than the buffer capacity.
     *
     * {@section Intentional side effects}
     * The {@link #bitOffset} is set to zero by this method.
     *
     * @param  n The requested amount of bytes.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    private void ensureRemaining(final int n) throws IOException {
        bitOffset = 0;
        final int remaining = buffer.remaining();
        if (remaining < n) {
            final int p = buffer.position();
            if (p != 0) {
                // Move the remaining bytes at the beginning of the buffer,
                // if they are not already there.
                bufferPosition += p;
                for (int i=0; i<remaining; i++) {
                    buffer.put(i, buffer.get());
                }
            }
            buffer.position(remaining).limit(buffer.capacity());
            do if (channel.read(buffer) <= 0) {
                throw new EOFException();
            } while (buffer.position() < n);
            buffer.flip();
        }
    }

    /**
     * Returns the length of the stream (in bytes), or -1 if unknown.
     *
     * @throws IOException If an error occurred while fetching the stream length.
     */
    @Override
    public long length() throws IOException {
        if (channel instanceof FileChannel) {
            return ((FileChannel) channel).size();
        }
        return -1;
    }

    /**
     * Returns the earliest position in the stream to which {@linkplain #seek(long) seeking}
     * may be performed.
     *
     * @return the earliest legal position for seeking.
     */
    @Override
    public long getFlushedPosition() {
        return bufferPosition;
    }

    /**
     * Returns the current byte position of the stream.
     *
     * @return The position of the stream.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public long getStreamPosition() throws IOException {
        return bufferPosition + buffer.position();
    }

    /**
     * Returns the current bit offset, as an integer between 0 and 7 inclusive. Note that the
     * bit offset is reset to 0 by every call to a {@code read} methods except {@link #readBit()}
     * and {@link #readBits(int)}.
     *
     * @return The bit offset of the stream.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int getBitOffset() throws IOException {
        return bitOffset;
    }

    /**
     * Sets the bit offset to the given value. Note that the bit offset is implicitly reset to 0
     * by every call to a {@code read} methods except {@link #readBit()} and {@link #readBits(int)}.
     *
     * @param bitOffset The new bit offset of the stream.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void setBitOffset(final int bitOffset) throws IOException {
        ensureBetween("bitOffset", 0, Byte.SIZE-1, bitOffset);
        this.bitOffset = bitOffset;
    }

    /**
     * Push back the last processed byte. This is used when a call to {@link #readBit()}
     * did not used every bits in a byte, or when {@link #readLine()} checked for the
     * Windows-style of EOL.
     */
    private void pushBack() {
        buffer.position(buffer.position() - 1);
    }

    /**
     * Reads a single byte from the stream and returns it as an int between 0 and 255.
     * If EOF is reached, -1 is returned.
     *
     * @return The value of the next byte in the stream, or {@code -1} on EOF.
     * @throws IOException If an error occurred while reading.
     */
    @Override
    public int read() throws IOException {
        bitOffset = 0;
        if (!buffer.hasRemaining()) {
            if (fillBuffer() <= 0) {
                return -1;
            }
        }
        return buffer.get() & 0xFF;
    }

    /**
     * Reads a single bit from the stream. The bit to be read depends on the
     * {@linkplain #getBitOffset() current bit offset}. Note that the bit offset is reset
     * to 0 by every {@code read} methods except {@code readBit()} and {@link #readBits(int)}.
     *
     * @return The value of the next bit from the stream.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public int readBit() throws IOException {
        ensureRemaining();
        int value = buffer.get() & 0xFF;
        final int toShift = (Byte.SIZE - ++bitOffset);
        if (toShift == 0) {
            bitOffset = 0;
        } else {
            pushBack();
            value >>= toShift;
        }
        return value & 1;
    }

    /**
     * Reads many bits from the stream. The first bit to be read depends on the
     * {@linkplain #getBitOffset() current bit offset}. Note that the bit offset is reset
     * to 0 by every {@code read} methods except {@code readBit()} and {@link #readBits(int)}.
     *
     * @param  numBits The number of bits to read.
     * @return The value of the next bits from the stream.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public long readBits(int numBits) throws IOException {
        ensureBetween("numBits", 0, Long.SIZE, numBits);
        if (numBits == 0) {
            return 0;
        }
        /*
         * Reads the bits available in the next bytes (all of them if bitOffset == 0)
         * and compute the number of bits that still need to be read. That number may
         * be negative if we have read too many bits.
         */
        ensureRemaining();
        long value = buffer.get() & (0xFF >>> bitOffset);
        numBits -= (Byte.SIZE - bitOffset);
        while (numBits > 0) {
            ensureRemaining();
            value = (value << Byte.SIZE) | (buffer.get() & 0xFF);
            numBits -= Byte.SIZE;
        }
        if (numBits != 0) {
            value >>>= (-numBits); // Discard the unwanted bits.
            bitOffset = Byte.SIZE + numBits;
            pushBack();
        } else {
            bitOffset = 0;
        }
        return value;
    }

    /**
     * Reads a byte from the stream and returns a {@code true} if it is nonzero,
     * {@code false} otherwise. The default implementation is as below:
     *
     * {@preformat java
     *     return readByte() != 0;
     * }
     *
     * @return The value of the next boolean from the stream.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public boolean readBoolean() throws IOException {
        return readByte() != 0;
    }

    /**
     * Reads a byte from the stream. The default implementation ensures that there is at
     * least 1 byte remaining in the buffer (reading new bytes from the channel if necessary),
     * then delegates to {@link ByteBuffer#get()}.
     *
     * @return The value of the next byte from the stream.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public byte readByte() throws IOException {
        bitOffset = 0;
        ensureRemaining();
        return buffer.get();
    }

    /**
     * Reads a unsigned byte from the stream. The default implementation is as below:
     *
     * {@preformat java
     *     return readByte() & 0xFF;
     * }
     *
     * @return The value of the next byte from the stream.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public int readUnsignedByte() throws IOException {
        return readByte() & 0xFF;
    }

    /**
     * Reads a short from the stream. The default implementation ensures that there is at
     * least 2 bytes remaining in the buffer (reading new bytes from the channel if necessary),
     * then delegates to {@link ByteBuffer#getShort()}.
     *
     * @return The value of the next short from the stream.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public short readShort() throws IOException {
        ensureRemaining(Short.SIZE / Byte.SIZE);
        return buffer.getShort();
    }

    /**
     * Reads a unsigned short from the stream. The default implementation is as below:
     *
     * {@preformat java
     *     return readShort() & 0xFFFF;
     * }
     *
     * @return The value of the next short from the stream.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public int readUnsignedShort() throws IOException {
        return readShort() & 0xFFFF;
    }

    /**
     * Reads a character from the stream. The default implementation ensures that there is at
     * least 2 bytes remaining in the buffer (reading new bytes from the channel if necessary),
     * then delegates to {@link ByteBuffer#getChar()}.
     *
     * @return The value of the next character from the stream.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public char readChar() throws IOException {
        ensureRemaining(Character.SIZE / Byte.SIZE);
        return buffer.getChar();
    }

    /**
     * Reads an integer from the stream. The default implementation ensures that there is at
     * least 4 bytes remaining in the buffer (reading new bytes from the channel if necessary),
     * then delegates to {@link ByteBuffer#getInt()}.
     *
     * @return The value of the next integer from the stream.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public int readInt() throws IOException {
        ensureRemaining(Integer.SIZE / Byte.SIZE);
        return buffer.getInt();
    }

    /**
     * Reads a unsigned integer from the stream. The default implementation is as below:
     *
     * {@preformat java
     *     return readInt() & 0xFFFFFFFFL;
     * }
     *
     * @return The value of the next integer from the stream.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public long readUnsignedInt() throws IOException {
        return readInt() & 0xFFFFFFFFL;
    }

    /**
     * Reads a long from the stream. The default implementation ensures that there is at
     * least 8 bytes remaining in the buffer (reading new bytes from the channel if necessary),
     * then delegates to {@link ByteBuffer#getLong()}.
     *
     * @return The value of the next long from the stream.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public long readLong() throws IOException {
        ensureRemaining(Long.SIZE / Byte.SIZE);
        return buffer.getLong();
    }

    /**
     * Reads a float from the stream. The default implementation ensures that there is at
     * least 4 bytes remaining in the buffer (reading new bytes from the channel if necessary),
     * then delegates to {@link ByteBuffer#getFloat()}.
     *
     * @return The value of the next float from the stream.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public float readFloat() throws IOException {
        ensureRemaining(Float.SIZE / Byte.SIZE);
        return buffer.getFloat();
    }

    /**
     * Reads a double from the stream. The default implementation ensures that there is at
     * least 8 bytes remaining in the buffer (reading new bytes from the channel if necessary),
     * then delegates to {@link ByteBuffer#getDouble()}.
     *
     * @return The value of the next double from the stream.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public double readDouble() throws IOException {
        ensureRemaining(Double.SIZE / Byte.SIZE);
        return buffer.getDouble();
    }

    /**
     * Reads the new bytes until the next EOL.
     *
     * @return The next line, or {@code null} if the EOF has been reached.
     * @throws IOException If an error occurred while reading.
     */
    @Override
    public String readLine() throws IOException {
        int c = read();
        if (c < 0) {
            return null;
        }
        StringBuilder line = stringBuffer;
        if (line == null) {
            stringBuffer = line = new StringBuilder();
        }
        line.append((char) c);
loop:   while ((c = read()) >= 0) {
            switch (c) {
                case '\n': break loop;
                case '\r': {
                    c = read();
                    if (c >= 0 && c != '\n') {
                        pushBack();
                    }
                    break loop;
                }
            }
            line.append((char) c);
        }
        return line.toString();
    }

    /**
     * Reads in a string that has been encoded using a UTF-8 string.
     *
     * @return The string reads from the stream.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public String readUTF() throws IOException {
        final ByteOrder oldOrder = buffer.order();
        buffer.order(ByteOrder.BIG_ENDIAN);
        try {
            return DataInputStream.readUTF(this);
        } finally {
            buffer.order(oldOrder);
        }
    }

    /**
     * Checks the validity of user-provided argument.
     *
     * @param  maxLength The length of the array given by the user.
     * @param  offset    The {@code offset} argument provided by the user.
     * @param  length    The {@code length} argument provided by the user.
     * @throws IllegalArgumentException If one of the user-provided arguments is invalid.
     */
    private static void checkRange(int maxLength, final int offset, final int length) {
        if (length < 0 || (maxLength -= length) < 0) {
            throw new IllegalArgumentException(Errors.format(
                    Errors.Keys.ILLEGAL_ARGUMENT_2, "length", length));
        }
        ensureBetween("offset", 0, maxLength, offset);
    }

    /**
     * Reads up to {@code length} bytes from the stream, and modifies the supplied
     * {@code IIOByteBuffer} to indicate the byte array, offset, and length where
     * the data may be found.
     *
     * @param  dest The buffer to be written to.
     * @param  length The maximum number of bytes to read.
     * @throws IOException If an error occurred while reading.
     */
    @Override
    public void readBytes(final IIOByteBuffer dest, int length) throws IOException {
        final byte[] data = new byte[length];
        length = read(data);
        dest.setData(data);
        dest.setOffset(0);
        dest.setLength(length);
    }

    /**
     * Reads up to {@code dest.length} bytes from the stream, and stores them into
     * {@code dest} starting at index 0. The default implementation is as below:
     *
     * {@preformat java
     *     return read(dest, 0, dest.length);
     * }
     *
     * @param  dest An array of bytes to be written to.
     * @return The number of bytes actually read, or -1 on EOF.
     * @throws IOException If an error occurred while reading.
     */
    @Override
    public int read(final byte[] dest) throws IOException {
        return read(dest, 0, dest.length);
    }

    /**
     * Reads up to {@code length} bytes from the stream, and stores them into {@code dest}
     * starting at index {@code offset}. If no bytes can be read because the end of the stream
     * has been reached, -1 is returned.
     *
     * @param  dest   An array of bytes to be written to.
     * @param  offset The starting position withing {@code dest} to write.
     * @param  length The maximum number of bytes to read.
     * @return The number of bytes actually read, or -1 on EOF.
     * @throws IOException If an error occurred while reading.
     */
    @Override
    public int read(final byte[] dest, int offset, int length) throws IOException {
        checkRange(dest.length, offset, length);
        bitOffset = 0;
        final int requested = length;
        while (length != 0) {
            if (!buffer.hasRemaining()) {
                if (fillBuffer() <= 0) {
                    if (requested == length) {
                        return -1; // No byte read.
                    }
                    break;
                }
            }
            final int n = Math.min(buffer.remaining(), length);
            buffer.get(dest, offset, n);
            offset += n;
            length -= n;
        }
        return requested - length;
    }

    /**
     * Reads {@code dest.length} bytes from the stream, and stores them into
     * {@code dest} starting at index 0. The default implementation is as below:
     *
     * {@preformat java
     *     return readFully(dest, 0, dest.length);
     * }
     *
     * @param  dest An array of bytes to be written to.
     * @throws IOException If an error occurred while reading.
     */
    @Override
    public void readFully(final byte[] dest) throws IOException {
        readFully(dest, 0, dest.length);
    }

    /**
     * Reads {@code length} bytes from the stream, and stores them into
     * {@code dest} starting at index {@code offset}.
     *
     * @param  dest   An array of bytes to be written to.
     * @param  offset The starting position withing {@code dest} to write.
     * @param  length The number of bytes to read.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public void readFully(final byte[] dest, int offset, int length) throws IOException {
        checkRange(dest.length, offset, length);
        bitOffset = 0; // For all other readFully methods, this field is reset by syncView.
        while (length != 0) {
            ensureRemaining();
            final int n = Math.min(buffer.remaining(), length);
            buffer.get(dest, offset, n);
            offset += n;
            length -= n;
        }
    }

    /**
     * Reads {@code length} characters from the stream, and stores them into
     * {@code dest} starting at index {@code offset}.
     *
     * @param  dest   An array of characters to be written to.
     * @param  offset The starting position withing {@code dest} to write.
     * @param  length The number of characters to read.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public void readFully(final char[] dest, int offset, int length) throws IOException {
        checkRange(dest.length, offset, length);
        CharBuffer view = charBuffer;
        if (view == null) {
            charBuffer = view = Buffers.asCharBuffer(buffer);
        }
        while (length != 0) {
            syncView(view, Character.SIZE / Byte.SIZE);
            final int n = Math.min(view.remaining(), length);
            view.get(dest, offset, n);
            syncBuffer(view, Character.SIZE / Byte.SIZE);
            offset += n;
            length -= n;
        }
    }

    /**
     * Reads {@code length} shorts from the stream, and stores them into
     * {@code dest} starting at index {@code offset}.
     *
     * @param  dest   An array of shorts to be written to.
     * @param  offset The starting position withing {@code dest} to write.
     * @param  length The number of shorts to read.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public void readFully(final short[] dest, int offset, int length) throws IOException {
        checkRange(dest.length, offset, length);
        ShortBuffer view = shortBuffer;
        if (view == null) {
            shortBuffer = view = Buffers.asShortBuffer(buffer);
        }
        while (length != 0) {
            syncView(view, Short.SIZE / Byte.SIZE);
            final int n = Math.min(view.remaining(), length);
            view.get(dest, offset, n);
            syncBuffer(view, Short.SIZE / Byte.SIZE);
            offset += n;
            length -= n;
        }
    }

    /**
     * Reads {@code length} ints from the stream, and stores them into
     * {@code dest} starting at index {@code offset}.
     *
     * @param  dest   An array of ints to be written to.
     * @param  offset The starting position withing {@code dest} to write.
     * @param  length The number of ints to read.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public void readFully(final int[] dest, int offset, int length) throws IOException {
        checkRange(dest.length, offset, length);
        IntBuffer view = intBuffer;
        if (view == null) {
            intBuffer = view = Buffers.asIntBuffer(buffer);
        }
        while (length != 0) {
            syncView(view, Integer.SIZE / Byte.SIZE);
            final int n = Math.min(view.remaining(), length);
            view.get(dest, offset, n);
            syncBuffer(view, Integer.SIZE / Byte.SIZE);
            offset += n;
            length -= n;
        }
    }

    /**
     * Reads {@code length} longs from the stream, and stores them into
     * {@code dest} starting at index {@code offset}.
     *
     * @param  dest   An array of longs to be written to.
     * @param  offset The starting position withing {@code dest} to write.
     * @param  length The number of longs to read.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public void readFully(final long[] dest, int offset, int length) throws IOException {
        checkRange(dest.length, offset, length);
        LongBuffer view = longBuffer;
        if (view == null) {
            longBuffer = view = Buffers.asLongBuffer(buffer);
        }
        while (length != 0) {
            syncView(view, Long.SIZE / Byte.SIZE);
            final int n = Math.min(view.remaining(), length);
            view.get(dest, offset, n);
            syncBuffer(view, Long.SIZE / Byte.SIZE);
            offset += n;
            length -= n;
        }
    }

    /**
     * Reads {@code length} floats from the stream, and stores them into
     * {@code dest} starting at index {@code offset}.
     *
     * @param  dest   An array of floats to be written to.
     * @param  offset The starting position withing {@code dest} to write.
     * @param  length The number of floats to read.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public void readFully(final float[] dest, int offset, int length) throws IOException {
        checkRange(dest.length, offset, length);
        FloatBuffer view = floatBuffer;
        if (view == null) {
            floatBuffer = view = Buffers.asFloatBuffer(buffer);
        }
        while (length != 0) {
            syncView(view, Float.SIZE / Byte.SIZE);
            final int n = Math.min(view.remaining(), length);
            view.get(dest, offset, n);
            syncBuffer(view, Float.SIZE / Byte.SIZE);
            offset += n;
            length -= n;
        }
    }

    /**
     * Reads {@code length} doubles from the stream, and stores them into
     * {@code dest} starting at index {@code offset}.
     *
     * @param  dest   An array of doubles to be written to.
     * @param  offset The starting position withing {@code dest} to write.
     * @param  length The number of shorts to read.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    @Override
    public void readFully(final double[] dest, int offset, int length) throws IOException {
        checkRange(dest.length, offset, length);
        DoubleBuffer view = doubleBuffer;
        if (view == null) {
            doubleBuffer = view = Buffers.asDoubleBuffer(buffer);
        }
        while (length != 0) {
            syncView(view, Double.SIZE / Byte.SIZE);
            final int n = Math.min(view.remaining(), length);
            view.get(dest, offset, n);
            syncBuffer(view, Double.SIZE / Byte.SIZE);
            offset += n;
            length -= n;
        }
    }

    /**
     * Synchronizes the position and limit of the given target buffer with the current state of
     * the {@linkplain #buffer}. If the current position of the buffer is not divisible by the
     * given data size, then the remaining data in the buffer are moved to index 0.
     * <p>
     * This method is invoked before to read an array of short, int, long, float or double data.
     *
     * {@section Intentional side effects}
     * The {@link #bitOffset} is indirectly set to zero (through the call to
     * {@link #ensureRemaining(int)}), and the {@linkplain #buffer} is guaranteed
     * to have at least {@code dataSize} bytes.
     *
     * @param  target   The buffer to synchronize with the {@link #buffer}.
     * @param  dataSize The size of each data element in the {@code target} buffer, in bytes.
     * @throws IOException If an error occurred while reading (including EOF).
     */
    private void syncView(final Buffer target, final int dataSize) throws IOException {
        int p = buffer.position();
        int r = p % dataSize;
        if (r != 0) {
            final ByteBuffer dupBuffer = getDuplicatedBuffer();
            dupBuffer.limit(buffer.limit()).position(buffer.position());
            buffer.clear();
            buffer.put(dupBuffer).flip();
            bufferPosition += p;
        }
        ensureRemaining(dataSize);
        p = buffer.position();
        assert p % dataSize == 0 : p;
        target.limit(buffer.limit() / dataSize).position(p / dataSize);
    }

    /**
     * Synchronizes the position of the {@link #buffer} to the current state of the source buffer.
     * This method is invoked after having read an array of short, int, long, float or double data.
     *
     * @param source   The buffer from which to fetch the position.
     * @param dataSize The size of each data element in the {@code source} buffer, in bytes.
     */
    private void syncBuffer(final Buffer source, final int dataSize) {
        buffer.position(source.position() * dataSize);
    }

    /**
     * Advances the current stream position by the given amount of bytes.
     * The default implementation delegates to:
     *
     * {@preformat java
     *     return (int) skipBytes((long) n);
     * }
     *
     * @param  n The number of bytes to seek forward.
     * @return The number of bytes skipped.
     * @throws IOException If an error occurred while skiping.
     */
    @Override
    public int skipBytes(final int n) throws IOException {
        return (int) skipBytes((long) n);
    }

    /**
     * Advances the current stream position by the given amount of bytes.
     * The bit offset is reset to 0 by this method.
     *
     * @param  n The number of bytes to seek forward.
     * @return The number of bytes skipped.
     * @throws IOException If an error occurred while skiping.
     */
    @Override
    public long skipBytes(long n) throws IOException {
        bitOffset = 0;
        long numToSkip = n; // Number of bytes that still need to be skipped.
        while (numToSkip > 0) {
            final int numBuffered = buffer.remaining();
            if (numToSkip <= numBuffered) {
                buffer.position(buffer.position() + (int) numToSkip);
                break; // We have been able to skip everything.
            }
            buffer.position(buffer.limit());
            numToSkip -= numBuffered;
            if (channel instanceof FileChannel) {
                final FileChannel fc  = (FileChannel) channel;
                final long position   = fc.position();
                final long numCanSkip = fc.size() - position;
                // If the user wants to skip past EOF, clamp to EOF.
                if (numToSkip > numCanSkip) {
                    n -= (numToSkip - numCanSkip);
                    numToSkip = numCanSkip;
                }
                fc.position(position + numToSkip);
                bufferPosition += numToSkip;
                break;
            }
            if (fillBuffer() <= 0) {
                n -= numToSkip;
                break;
            }
        }
        return n;
    }

    /**
     * Sets the current stream position to the desired location. The next read will occur at this
     * location. The bit offset is set to 0.
     *
     * @param  pos The new stream position.
     * @throws IOException If an I/O error occurred.
     * @throws IllegalArgumentException If the given position is before the
     *         {@linkplain #getFlushedPosition flushed position}.
     */
    @Override
    public void seek(final long pos) throws IOException, IllegalArgumentException {
        bitOffset = 0;
        final long relativePosition = pos - bufferPosition;
        if (relativePosition < 0) {
            final long size = length();
            throw new IndexOutOfBoundsException(Errors.format(Errors.Keys.VALUE_OUT_OF_BOUNDS_3,
                    pos, bufferPosition, (size >= 0) ? size : "\u221E"));
        }
        if (relativePosition < buffer.limit()) {
            buffer.position((int) relativePosition);
        } else {
            skipBytes(relativePosition - buffer.position());
        }
    }

    /**
     * Pushes the current stream position onto a stack of marked positions.
     */
    @Override
    public void mark() {
        mark = new Mark(bufferPosition + buffer.position(), bitOffset, mark);
    }

    /**
     * Resets the current stream byte and bit positions from the stack of marked positions.
     * An {@code IOException} will be thrown if the previous marked position lies in the
     * discarded portion of the stream.
     *
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void reset() throws IOException {
        final Mark mark = this.mark;
        if (mark == null) {
            throw new IOException("No marked position."); // TODO: localize
        }
        seek(mark.position);
        bitOffset = mark.bitOffset;
        this.mark = mark.next;
    }

    /**
     * Discards the initial position of the stream prior to the current
     * stream position. The default implementation is as below:
     *
     * {@preformat java
     *     flushBefore(getStreamPosition());
     * }
     *
     * @throws IOException If an I/O error occurred.
     */
    @Override
    public void flush() throws IOException {
        flushBefore(getStreamPosition());
    }

    /**
     * Discards the initial portion of the stream prior to the indicated postion.
     * Attempting to {@linkplain #seek(long) seek} to an offset within the flushed
     * portion of the stream will result in an {@link IndexOutOfBoundsException}.
     *
     * @param  pos The length of the stream prefix that may be flushed.
     * @throws IOException If an I/O error occurred.
     */
    @Override
    public void flushBefore(final long pos) throws IOException {
        final long relativePosition = pos - bufferPosition;
        final int  currentPosition  = buffer.position();
        if (relativePosition < 0 || relativePosition > currentPosition) {
            throw new IndexOutOfBoundsException(Errors.format(Errors.Keys.VALUE_OUT_OF_BOUNDS_3,
                    pos, bufferPosition, bufferPosition + currentPosition));
        }
        // At this point we know that 'relativePosition' fit in an
        // int type since it is not greater then 'currentPosition'.
        if (relativePosition != 0) {
            final ByteBuffer dupBuffer = getDuplicatedBuffer();
            dupBuffer.limit(buffer.limit()).position((int) relativePosition);
            buffer.clear();
            buffer.put(dupBuffer).flip().position(currentPosition - (int) relativePosition);
            bufferPosition += relativePosition;
        }
    }

    /**
     * Returns {@code true} if this {@code ImageInputStream} caches data itself in order to
     * allow {@linkplain #seek(long) seeking} backwards. The default implementation returns
     * {@code false}.
     *
     * @return {@code true} If this {@code ImageInputStream} caches data.
     *
     * @see #isCachedMemory
     * @see #isCachedFile
     */
    @Override
    public boolean isCached() {
        return false;
    }

    /**
     * Returns {@code true} if this {@code ImageInputStream} caches data itself in order to
     * allow {@linkplain #seek(long) seeking} backwards, and the cache is kept in main memory.
     * The default implementation returns {@code false}.
     *
     * @return {@code true} if this {@code ImageInputStream} caches data in main memory.
     */
    @Override
    public boolean isCachedMemory() {
        return false;
    }

    /**
     * Returns {@code true} if this {@code ImageInputStream} caches data itself in order to
     * allow {@linkplain #seek(long) seeking} backwards, and the cache is kept in a temporary
     * file. The default implementation returns {@code false}.
     *
     * @return {@code true} if this {@code ImageInputStream} caches data in a temporary file.
     */
    @Override
    public boolean isCachedFile() {
        return false;
    }

    /**
     * Closes the {@linkplain #channel}.
     *
     * @throws IOException If an error occurred while closing the channel.
     */
    @Override
    public void close() throws IOException {
        channel.close();
        clearViews();
        bufferPosition = 0;
        bitOffset      = 0;
        mark           = null;
        stringBuffer   = null;
    }

    /**
     * Clears every views. This is necessary when the byte order is modified.
     */
    private void clearViews() {
        dupBuffer    = null;
        charBuffer   = null;
        shortBuffer  = null;
        intBuffer    = null;
        longBuffer   = null;
        floatBuffer  = null;
        doubleBuffer = null;
    }
}
