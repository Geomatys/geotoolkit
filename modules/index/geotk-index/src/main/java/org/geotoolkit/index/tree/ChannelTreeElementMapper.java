/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.index.tree;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import org.apache.sis.util.ArgumentChecks;

/**
 * Define a default abstract {@link TreeElementMapper} class, to store tree Identifiers on disk.
 *
 * @author Remi Marechal (Geomatys).
 */
public abstract class ChannelTreeElementMapper<E> implements TreeElementMapper<E> {

    /**
     * FileTreeElementMapper identifier.
     */
    private final static int TREE_ELT_MAP_NUMBER = 44034146;

    /**
     * Default byte buffer length.
     */
    private final static int DEFAULT_BUFFER_LENGTH = 4096;

    /**
     * Channel to read and write data informations and tree identifiers.
     */
    private final SeekableByteChannel inOutChannel;

    /**
     * Size in {@code Byte} of object which will be mapped.
     */
    private final int objSize;

    /**
     * {@link FileChannel} position just after write or read file head.<br/>
     * Its also file position of first Node red or written.
     */
    private final int beginPosition;

    /**
     * Last mapped object {@link FileChannel} position.
     */
    private int maxPosition;

    /**
     * {@link ByteBuffer} to read and write data informations and tree identifiers from file on hard disk.
     */
    protected final ByteBuffer byteBuffer;

    /**
     * ByteBuffer Length.
     */
    private final int bufferLength;

    /**
     * Store element on disk at file path emplacement.<br/>
     * Consist to store identifier use during {@link Tree} insertion.<br/>
     * A default buffer length of {@link #DEFAULT_BUFFER_LENGTH} is choose.
     *
     * @param channel channel where to read/write datas
     * @param objectSize size in {@code Byte} unit of elements which will be store.
     * @see AbstractTree#insert(java.lang.Object)
     * @throws IOException if problem during {@link RandomAccessFile} creation.
     */
    protected ChannelTreeElementMapper(final SeekableByteChannel channel, final int objectSize) throws IOException {
        this(channel, DEFAULT_BUFFER_LENGTH, objectSize);
    }

    /**
     * Store element on disk at file path emplacement.<br/>
     * Consist to store identifier use during {@link Tree} insertion.
     *
     * @param channel channel where to read/write datas
     * @param bufferLength length in Byte unit of the buffer which read and write on hard drive.
     * @param objectSize size in {@code Byte} unit of elements which will be store.
     * @see AbstractTree#insert(java.lang.Object)
     * @throws IOException if problem during {@link RandomAccessFile} creation.
     */
    protected ChannelTreeElementMapper(final SeekableByteChannel channel, final int bufferLength, final int objectSize) throws IOException {
        ArgumentChecks.ensureNonNull("SeekableByteChannel", channel);
        ArgumentChecks.ensureStrictlyPositive("buffer length", bufferLength);
        ArgumentChecks.ensureStrictlyPositive("object size", objectSize);
        inOutChannel = channel;
        // Ensure buffer capacity is a multiple of object length.
        final int div         = bufferLength / objectSize;
        this.bufferLength     = div * objectSize;
        byteBuffer            = ByteBuffer.allocateDirect(this.bufferLength);

        channel.position(0);

        //-- head length in byte
        final int headLength = 13;

        final ByteOrder bO;
        boolean isRead = channel.size() >= headLength;

        byteBuffer.position(0);
        byteBuffer.limit(13);

        if (isRead) {
            inOutChannel.read(byteBuffer);
            byteBuffer.flip();

            assert byteBuffer.position() == 0;
            assert byteBuffer.limit() == headLength;

            // read mapper identifier
            final int identifier  = byteBuffer.getInt();
            if (identifier != TREE_ELT_MAP_NUMBER)
                throw new IllegalArgumentException("input file don't contain TreeElementMapper information.");

            // read ByteOrder
            final boolean fbool   = byteBuffer.get() == 1;
            bO = (fbool) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
            byteBuffer.order(bO);

            // read object size
            objSize               = byteBuffer.getInt();
            // read maxposition
            maxPosition           = byteBuffer.getInt();

            assert byteBuffer.position() == headLength;

            if (objSize != objectSize)
                throw new IOException("Input data does not match given parameter \"object length\" : "+objSize+" vs "+objectSize);
        } else {

            objSize = objectSize;
            bO = ByteOrder.nativeOrder();

            /****************************** write head *****************************/
            //-- default jdk byteOrder to write treeIdentifier and ByteOrder
            byteBuffer.putInt(TREE_ELT_MAP_NUMBER);
            byteBuffer.put((byte)((bO == ByteOrder.LITTLE_ENDIAN) ? 1 : 0));

            byteBuffer.order(bO);
            byteBuffer.putInt(objSize);
            byteBuffer.putInt(0);
            byteBuffer.flip();

            assert byteBuffer.position() == 0;
            assert byteBuffer.limit() == headLength;

            inOutChannel.write(byteBuffer);
            /******************************* end head *****************************/
        }

        //-- init buffer
        byteBuffer.position(0);
        byteBuffer.flip();

        beginPosition         = (int) inOutChannel.position();
        assert beginPosition == headLength;

        if (isRead) pullBuffer();
        else maxPosition = beginPosition;

    }

    /**
     * Write object in output stream already positioned.
     *
     * @param Object which will be write.
     */
    protected abstract void writeObject(E Object) throws IOException ;

    /**
     * Read object from inpout stream already positioned.
     *
     * @return red object.
     */
    protected abstract E readObject() throws IOException ;

    /**
     * Compare and return true if the two object are equals else false.
     *
     * @param objectA
     * @param objectB
     * @return return true if the two object are equals else false.
     */
    protected abstract boolean areEquals(E objectA, E objectB);

    /**
     * {@inheritDoc }.
     */
    @Override
    public synchronized int getTreeIdentifier(E object) throws IOException {
        int treeIdentifier = 1;
        for (int currentPos = beginPosition; currentPos < maxPosition; currentPos += objSize) {
            adjustBuffer(treeIdentifier);
            final E currentObject = readObject();
            if (areEquals(currentObject, object)) return treeIdentifier;
            treeIdentifier++;
        }
        if (maxPosition == beginPosition) throw new IllegalStateException(this.getClass().getName()+".getTreeIdentifier() : you must set object before.");
        throw new IllegalStateException(this.getClass().getName()+".getTreeIdentifier() : impossible to find treeIdentifier for object : "+object.toString());
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public synchronized void setTreeIdentifier(E object, int treeIdentifier) throws IOException {
        adjustBuffer(treeIdentifier);
        writeObject(object);
        maxPosition += objSize;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public synchronized E getObjectFromTreeIdentifier(int treeIdentifier) throws IOException {
        adjustBuffer(treeIdentifier);
        return readObject();
    }

    /**
     * Put all attributes like just after constructor.
     */
    @Override
    public synchronized void clear() throws IOException {
        pushBuffer();
        inOutChannel.position(beginPosition);
        pullBuffer();
        maxPosition = beginPosition;
    }

    /**
     * Close stream and FileChannel.
     *
     * @throws IOException
     */
    @Override
    public synchronized void close() throws IOException {

        if (inOutChannel.isOpen()) {
            pushBuffer();
            writeMaxPosition();
            inOutChannel.close();
        }
    }

    /**
     * Flush {@link #byteBuffer} into {@link #inOutChannel}.
     *
     * @throws IOException
     */
    @Override
    public synchronized void flush() throws IOException {

        final long channelPos = inOutChannel.position();
        pushBuffer();
        writeMaxPosition();
        inOutChannel.position(channelPos);
        pullBuffer();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized boolean isClosed() {
        return !inOutChannel.isOpen();
    }

    /**
     * Adjust buffer position relative to filechanel which contain data,
     * and prepare bytebuffer position and limit for reading or writing action.
     *
     * @param treeIdentifier
     * @throws IOException
     */
    private void adjustBuffer(final int treeIdentifier) throws IOException {
        long rwIndex    = beginPosition + (treeIdentifier - 1) * objSize;
        long channelPos = inOutChannel.position();

        if (rwIndex < channelPos || (rwIndex + objSize) > channelPos + bufferLength) {
            pushBuffer();
            final long div = ((rwIndex - beginPosition) / bufferLength);
            channelPos = div * bufferLength + beginPosition;
            inOutChannel.position(channelPos);
            pullBuffer();
        }

        rwIndex -= channelPos;
        //-- only write that is necessarily
        byteBuffer.limit(Math.max(byteBuffer.limit(), (int) rwIndex + objSize));
        byteBuffer.position((int) rwIndex);
    }

    /**
     * Write the {@link #byteBuffer} into {@link #inOutChannel}, but without any {@link SeekableByteChannel#position() } changements.<br>
     * After buffer writing the channel position is rewind at its position when this method is called.<br>
     * Moreover, its {@link ByteBuffer#position() } and its {@link ByteBuffer#limit() } are setted to zero.
     */
    private void pushBuffer() throws IOException {
        final long channelPosition = inOutChannel.position();
        byteBuffer.flip();
        int expectedWB = byteBuffer.remaining();

        int writtenByte = 0;
        while (writtenByte < expectedWB) {
            writtenByte += inOutChannel.write(byteBuffer);
        }
        inOutChannel.position(channelPosition);
        byteBuffer.position(0);
        byteBuffer.flip();
    }

    /**
     * Fill the {@link #byteBuffer} from {@link #inOutChannel} datas, but without any {@link SeekableByteChannel#position() } changements.<br>
     * After buffer reading the channel position is rewind at its position when this method is called.<br>
     * Moreover, its {@link ByteBuffer#position() } and its {@link ByteBuffer#limit() } are setted to zero.
     */
    private void pullBuffer() throws IOException {
        final long channelPosition = inOutChannel.position();
        byteBuffer.clear();
        inOutChannel.read(byteBuffer);
        inOutChannel.position(channelPosition);
        byteBuffer.position(0);
        byteBuffer.flip();
    }

    /**
     * Write {@link #maxPosition} into this file header.
     */
    private void writeMaxPosition() throws IOException {
        //step mapper Identifier, byteOrder and object size.
        inOutChannel.position(9);

        //-- write maxPos
        byteBuffer.clear();
        byteBuffer.putInt(maxPosition);
        byteBuffer.flip();
        inOutChannel.write(byteBuffer);
    }
}
