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
package org.geotoolkit.index.tree.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import org.opengis.geometry.Envelope;

/**
 * Define a default abstract TreeElementMapper class to store tree Identifiers on disk.
 *
 * @author Remi Marechal (Geomatys).
 */
public abstract class FileTreeElementMapper<E> implements TreeElementMapper<E> {

    protected final RandomAccessFile inOutStream;
    private final FileChannel inOutChannel;
    private final int objSize;
    private int maxPosition;

    protected FileTreeElementMapper(File inOutPut, int size) throws IOException {
        inOutStream  = new RandomAccessFile(inOutPut, "rw");
        inOutChannel = inOutStream.getChannel();
        objSize      = size;
        maxPosition  = 0;
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
    
    protected abstract boolean areaEquals(E objectA, E objectB);
    
    @Override
    public int getTreeIdentifier(E object) throws IOException {
        int treeIdentifier = 1;
        for (int currentPos = 0; currentPos < maxPosition; currentPos += objSize) {
            inOutChannel.position(currentPos);
            final E currentObject = readObject();
            if (areaEquals(currentObject, object)) return treeIdentifier;
            treeIdentifier++;
        }
        if (maxPosition == 0) throw new IllegalStateException(this.getClass().getName()+".getTreeIdentifier() : you must set object before.");
        throw new IllegalStateException(this.getClass().getName()+".getTreeIdentifier() : impossible to find treeIdentifier for object : "+object.toString());
    }

    @Override
    public Envelope getEnvelope(E object)  throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTreeIdentifier(E object, int treeIdentifier) throws IOException {
        inOutChannel.position((treeIdentifier-1) * objSize);
        writeObject(object);
        maxPosition += objSize;
    }

    @Override
    public E getObjectFromTreeIdentifier(int treeIdentifier) throws IOException {
        inOutChannel.position((treeIdentifier-1) * objSize);
        return readObject();
    }
    
    /**
     * Put all attributs like just after constructor.
     */
    @Override
    public void clear() {
        maxPosition = 0;
    }
    
    public void close() throws IOException {
        inOutChannel.close();
        inOutStream.close();
    }
}
