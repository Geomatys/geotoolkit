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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import org.apache.sis.util.ArgumentChecks;

/**
 * Define a default abstract {@link TreeElementMapper} class, to store tree Identifiers on disk.
 *
 * @author Remi Marechal (Geomatys).
 */
public abstract class FileTreeElementMapper<E> implements TreeElementMapper<E> {

    /**
     * FileTreeElementMapper identifier.
     */
    private final static int TREE_ELT_MAP_NUMBER = 44034146;
    
    /**
     * Default byte buffer length.
     */
    private final static int DEFAULT_BUFFER_LENGTH = 4096;
    
    /**
     * Stream to read and write data informations and tree identifiers.
     */
    private final RandomAccessFile inOutStream;
    
    /**
     * Channel to read and write data informations and tree identifiers.
     */
    private final FileChannel inOutChannel;
    
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
     * ByteBuffer attributs use to read and write.
     */
    private int rwIndex;
    private int currentBufferPosition;
    private int writeBufferLimit;

    /**
     * Store element on disk at file path emplacement.<br/>
     * Consist to store identifier use during {@link Tree} insertion.<br/><br/>
     * 
     * Note : The default length value of ByteBuffer which read and write on hard disk, is 4096 Bytes.
     * 
     * @param outPut to store element on disk.
     * @param objectSize size in {@code Byte} unit of elements which will be store.
     * @see AbstractTree#insert(java.lang.Object) 
     * @throws IOException if problem during {@link RandomAccessFile} creation.
     */
    protected FileTreeElementMapper(final File outPut, final int objectSize) throws IOException {
        this(outPut, DEFAULT_BUFFER_LENGTH, objectSize);
    }
    
    /**
     * Store element on disk at file path emplacement.<br/>
     * Consist to store identifier use during {@link Tree} insertion.
     * 
     * @param output to store element on disk.
     * @param bufferLength length in Byte unit of the buffer which read and write on hard drive.
     * @param objectSize size in {@code Byte} unit of elements which will be store.
     * @see AbstractTree#insert(java.lang.Object) 
     * @throws IOException if problem during {@link RandomAccessFile} creation.
     */
    protected FileTreeElementMapper(final File output, final int bufferLength, final int objectSize) throws IOException {
        ArgumentChecks.ensureNonNull("output file", output);
        ArgumentChecks.ensureStrictlyPositive("buffer length", bufferLength);
        ArgumentChecks.ensureStrictlyPositive("object size", objectSize);
        inOutStream           = new RandomAccessFile(output, "rw");
        inOutChannel          = inOutStream.getChannel();
        // Ensure buffer capacity is a multiple of object length.
        final int div         = bufferLength / objectSize;
        this.bufferLength     = div * objectSize;
        byteBuffer            = ByteBuffer.allocateDirect(bufferLength);

        final ByteOrder bO;
        boolean isRead = output.isFile() && output.length() >= 13;
        if (isRead) {
            // read mapper identifier
            final int identifier  = inOutStream.readInt();
            if (identifier != TREE_ELT_MAP_NUMBER)
                throw new IllegalArgumentException("input file don't contain TreeElementMapper information.");
            // read ByteOrder
            final boolean fbool   = inOutStream.readBoolean();
            bO = (fbool) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
            byteBuffer.order(bO);
            // read object size
            objSize               = inOutStream.readInt();
            // read maxposition
            maxPosition           = inOutStream.readInt();

            if (objSize != objectSize) {
                throw new IOException("Input data does not match given parameter \"object length\" : "+objSize+" vs "+objectSize);
            }
        } else {
            objSize = objectSize;
            bO = ByteOrder.nativeOrder();
            byteBuffer.order(bO);
            /****************************** write head *****************************/
            // write identifier
            inOutStream.writeInt(TREE_ELT_MAP_NUMBER);
            // write buffer order
            inOutStream.writeBoolean(bO == ByteOrder.LITTLE_ENDIAN);
            // write objSize
            inOutStream.writeInt(objSize);
            // write maxPosition
            inOutStream.writeInt(0);
            /******************************* end head *****************************/
        }

        beginPosition         = (int) inOutChannel.position();
        writeBufferLimit      = 0;
        currentBufferPosition = beginPosition;
        if (!isRead) {
            maxPosition = beginPosition;
        }

        if (isRead) {
            // fill buffer
            inOutChannel.read(byteBuffer, currentBufferPosition);
        }
    }
    
    /**
     * Store element on disk at file path emplacement.<br/>
     * Consist to store identifier use during {@link Tree} insertion.<br/><br/>
     * 
     * Note : The default length value of ByteBuffer which read and write on hard disk, is 4096 Bytes.
     * 
     * @param input to store element on disk.
     * @see AbstractTree#insert(java.lang.Object) 
     * @throws IOException if problem during {@link RandomAccessFile} creation.
     */
    protected FileTreeElementMapper(final File input) throws IOException {
        this(DEFAULT_BUFFER_LENGTH, input);
    }
    
    /**
     * Store element on disk at file path emplacement.<br/>
     * Consist to store identifier use during {@link Tree} insertion.
     * 
     * @param inPut to store element on disk.
     * @param bufferLength length in Byte unit of the buffer which read and write on hard drive.
     * @see AbstractTree#insert(java.lang.Object) 
     * @throws IOException if problem during {@link RandomAccessFile} creation.
     */
    protected FileTreeElementMapper(final int bufferLength, final File inPut) throws IOException {
        ArgumentChecks.ensureNonNull("inPut file", inPut);
        ArgumentChecks.ensureStrictlyPositive("buffer length", bufferLength);
        inOutStream           = new RandomAccessFile(inPut, "rw");
        inOutChannel          = inOutStream.getChannel();
        
        /*************************** read head ********************************/
        // read mapper identifier
        final int identifier  = inOutStream.readInt();
        if (identifier != TREE_ELT_MAP_NUMBER)
            throw new IllegalArgumentException("input file don't contain TreeElementMapper information.");
        // read ByteOrder
        final boolean fbool   = inOutStream.readBoolean();
        final ByteOrder bO    = (fbool) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        // read object size
        objSize               = inOutStream.readInt();
        // read maxposition
        maxPosition           = inOutStream.readInt();
        /**************************** end head reading ************************/

        // To ensure given capacity is a multiple of object size storing.
        final int div         = bufferLength / objSize;
        this.bufferLength     = div * objSize;
        byteBuffer            = ByteBuffer.allocateDirect(bufferLength);
        byteBuffer.order(bO);
        beginPosition         = (int) inOutChannel.position();
        writeBufferLimit      = 0;
        currentBufferPosition = beginPosition;
        
        // load buffer
        inOutChannel.read(byteBuffer, currentBufferPosition);
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
     * Adjust buffer position relative to filechanel which contain data, 
     * and prepare bytebuffer position and limit for reading or writing action.
     * 
     * @param treeIdentifier 
     * @throws IOException 
     */
    private void adjustBuffer(final int treeIdentifier) throws IOException {
        rwIndex = beginPosition + (treeIdentifier - 1) * objSize;
        if (rwIndex < currentBufferPosition || rwIndex >= currentBufferPosition + bufferLength) {
            // write in chanel
            byteBuffer.position(0);
            byteBuffer.limit(writeBufferLimit);
            int writtenByte = 0;
            while (writtenByte != writeBufferLimit) {
                writtenByte = inOutChannel.write(byteBuffer, currentBufferPosition);
            }
            writeBufferLimit = 0;
            byteBuffer.clear();
            final int div = (rwIndex - beginPosition) / bufferLength;
            currentBufferPosition = div * bufferLength + beginPosition;
            inOutChannel.read(byteBuffer, currentBufferPosition);
        }
        rwIndex -= currentBufferPosition;
        byteBuffer.limit(rwIndex + objSize);
        byteBuffer.position(rwIndex);
    }
    
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
        writeBufferLimit = Math.max(writeBufferLimit, byteBuffer.limit());
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
        byteBuffer.position(0);
        byteBuffer.limit(writeBufferLimit);
        int writtenByte = 0;
        while (writtenByte != writeBufferLimit) {
            writtenByte = inOutChannel.write(byteBuffer, currentBufferPosition);
        }
        currentBufferPosition = beginPosition;
        writeBufferLimit = 0;
        maxPosition = beginPosition;
    }
    
    /**
     * Close stream and FileChannel.
     * 
     * @throws IOException 
     */
    @Override
    public synchronized void close() throws IOException {
        byteBuffer.position(0);
        byteBuffer.limit(writeBufferLimit);
        int writtenByte = 0;
        while (writtenByte != writeBufferLimit) {
            writtenByte = inOutChannel.write(byteBuffer, currentBufferPosition);
        }
        
        //step mapper Identifier, byteOrder and object size.
        inOutChannel.position(9);
        
        inOutStream.writeInt(maxPosition);
        inOutChannel.close();
        inOutStream.close();
    }

    /**
     * Close stream and FileChannel.
     *
     * @throws IOException
     */
    @Override
    public synchronized void flush() throws IOException {
        final long channelPos = inOutChannel.position();

        byteBuffer.position(0);
        byteBuffer.limit(writeBufferLimit);
        int writtenByte = 0;
        while (writtenByte != writeBufferLimit) {
            writtenByte = inOutChannel.write(byteBuffer, currentBufferPosition);
        }

        //step mapper Identifier, byteOrder and object size.
        inOutChannel.position(9);

        inOutStream.writeInt(maxPosition);
        inOutChannel.position(channelPos);
        inOutChannel.read(byteBuffer, currentBufferPosition);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized boolean isClosed() {
        return !inOutChannel.isOpen();
    }
}
