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
package org.geotoolkit.index.tree.mapper;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.geotoolkit.index.tree.AbstractTree;
import org.geotoolkit.index.tree.Tree;
import org.opengis.geometry.Envelope;

/**
 * Define a default abstract {@link TreeElementMapper} class, to store tree Identifiers on disk.
 *
 * @author Remi Marechal (Geomatys).
 */
public abstract class FileTreeElementMapper<E> implements TreeElementMapper<E> {

    protected final RandomAccessFile inOutStream;
    private final FileChannel inOutChannel;
    private final int objSize;
    private int maxPosition;
    
    // byte buffer
    protected final ByteBuffer byteBuffer;
    private int currentBufferPosition;
    private int writeBufferLimit;
    private final int bufferLength;
    private int rwIndex;

    /**
     * Store element on disk at file path emplacement.<br/>
     * Consist to store identifier use during {@link Tree} insertion.
     * 
     * @param inOutPut to store element on disk.
     * @param size size in {@code Byte} unit of elements which will be store.
     * @see AbstractTree#insert(java.lang.Object) 
     * @throws IOException 
     */
    protected FileTreeElementMapper(final File inOutPut, final int size) throws IOException {
        inOutStream           = new RandomAccessFile(inOutPut, "rw");
        inOutChannel          = inOutStream.getChannel();
        objSize               = size;
        final int div         = 4096 / objSize;
        bufferLength          = div * objSize;
        byteBuffer            = ByteBuffer.allocateDirect(bufferLength);
        writeBufferLimit      = 0;
        currentBufferPosition = 0;
        maxPosition           = 0;
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
        rwIndex = (treeIdentifier - 1) * objSize;
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
            final int div = rwIndex / bufferLength;
            currentBufferPosition = div * bufferLength;
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
    public int getTreeIdentifier(E object) throws IOException {
        int treeIdentifier = 1;
        for (int currentPos = 0; currentPos < maxPosition; currentPos += objSize) {
            adjustBuffer(treeIdentifier);                           
            final E currentObject = readObject();
            if (areEquals(currentObject, object)) return treeIdentifier;
            treeIdentifier++;
        }
        if (maxPosition == 0) throw new IllegalStateException(this.getClass().getName()+".getTreeIdentifier() : you must set object before.");
        throw new IllegalStateException(this.getClass().getName()+".getTreeIdentifier() : impossible to find treeIdentifier for object : "+object.toString());
    }

    @Override
    public Envelope getEnvelope(E object)  throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setTreeIdentifier(E object, int treeIdentifier) throws IOException {
        adjustBuffer(treeIdentifier);
        writeObject(object);
        maxPosition += objSize;
        writeBufferLimit = Math.max(writeBufferLimit, byteBuffer.limit());
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public E getObjectFromTreeIdentifier(int treeIdentifier) throws IOException {
        adjustBuffer(treeIdentifier);
        return readObject();
    }
    
    /**
     * Put all attributs like just after constructor.
     */
    @Override
    public void clear() throws IOException {
        byteBuffer.position(0);
        byteBuffer.limit(writeBufferLimit);
        int writtenByte = 0;
        while (writtenByte != writeBufferLimit) {
            writtenByte = inOutChannel.write(byteBuffer, currentBufferPosition);
        }
        currentBufferPosition = 0;
        writeBufferLimit = 0;
        maxPosition = 0;
    }
    
    /**
     * Close stream and FileChannel.
     * 
     * @throws IOException 
     */
    public void close() throws IOException {
        this.clear();
        inOutChannel.close();
        inOutStream.close();
    }
}
