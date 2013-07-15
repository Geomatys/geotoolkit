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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import static org.geotoolkit.index.tree.DefaultTreeUtils.intersects;
import org.geotoolkit.index.tree.io.TreeVisitor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author rmarechal
 */
public class TreeAccessFile {
    
    private final int boundLength;
    private final int nodeSize;
    private final long beginPosition;
    private final RandomAccessFile inOutStream;
    private final FileChannel inOutChannel;
    
    private final ByteBuffer byteBuffer;
    private long currentBufferPosition;
    private int writeBufferLimit;
    private final long bufferLength;
    private int nodeId = 1;
    private List<Integer> recycleID = new LinkedList<>();

    public TreeAccessFile(final File outPut, final int magicNumber, final double versionNumber, int maxElements, CoordinateReferenceSystem crs) throws IOException{

        int dimension = crs.getCoordinateSystem().getDimension();
        
        this.boundLength = dimension << 1;
        
        // Node size : boundary weigth + parent ID + 1st sibling Integer + 1st child Integer + child number.
        nodeSize = (dimension * Double.SIZE + ((Integer.SIZE) << 1)) >> 2;
        
        final int div = 8192 / nodeSize;// 4096
        this.bufferLength = div * nodeSize;
        // ByteBuffer
        final ByteOrder bO = ByteOrder.nativeOrder();
        byteBuffer = ByteBuffer.allocateDirect((int)bufferLength);
        byteBuffer.order(bO);
        
        // stream
        inOutStream  = new RandomAccessFile(outPut, "rw");
        inOutChannel = inOutStream.getChannel();
        
        /***************************  write head ******************************/
        final ByteArrayOutputStream temp = new ByteArrayOutputStream();
        final ObjectOutputStream objOutput = new ObjectOutputStream(temp);
        // write magicNumber
        inOutStream.writeInt(magicNumber);
        // write bytebuffer order
        inOutStream.writeBoolean(bO == ByteOrder.LITTLE_ENDIAN);
        // write version number
        inOutStream.writeDouble(versionNumber);
        // write element number per Node
        inOutStream.writeInt(maxElements);
        // write dimension
        inOutStream.writeInt(dimension);
        // write CRS
        objOutput.writeObject(crs);
        objOutput.flush();
        final byte[] crsByteArray = temp.toByteArray();
        inOutStream.writeInt(crsByteArray.length);
        inOutStream.write(crsByteArray);
        objOutput.close();
        /*****************************  end head ******************************/
        
        beginPosition = inOutChannel.position();
        currentBufferPosition   = beginPosition;
        writeBufferLimit = 0;
    }
    
    
    
    public void search(int nodeID, double[] regionSearch, TreeVisitor visitor) throws IOException {
        adjustBuffer(nodeID);// faire des move buffposition
        final int searchIndex = (int) ((beginPosition + (nodeID - 1) * nodeSize) - currentBufferPosition);
        byteBuffer.limit(searchIndex + nodeSize);
        byteBuffer.position(searchIndex);
        final double[] boundary = new double[boundLength];
        for (int i = 0; i < boundLength; i++) {
            boundary[i] = byteBuffer.getDouble();
        }
        final int parent  = byteBuffer.getInt();
        final int sibling = byteBuffer.getInt();
        final int child   = byteBuffer.getInt();// appel avant de suivre voisin risk de perte de cursor
        final int chCount = byteBuffer.getInt();
        if (sibling != 0) {
            search(sibling, regionSearch, visitor);
        }
        if (intersects(boundary, regionSearch, true)) {
            if (child > 0) {
                search(child, regionSearch, visitor);
            } else {
                if (child == 0)
                    throw new IllegalStateException("child index should never be 0.");
                visitor.visit(-child);
            }
        } 
    }
    
    /**
     * 
     * @param indexNode
     * @return
     * @throws IOException 
     */
    public FileNode readNode(final int indexNode) throws IOException {
        adjustBuffer(indexNode);
        final int readIndex = (int) ((beginPosition + (indexNode - 1) * nodeSize) - currentBufferPosition);
        byteBuffer.limit(readIndex + nodeSize);
        byteBuffer.position(readIndex);
        final double[] boundary = new double[boundLength];
        for (int i = 0; i < boundLength; i++) {
            boundary[i] = byteBuffer.getDouble();
        }
        final int parentId   = byteBuffer.getInt();
        final int siblingId  = byteBuffer.getInt();
        final int childId    = byteBuffer.getInt();
        final int childCount = byteBuffer.getInt();
        final FileNode redNode = new FileNode(this, indexNode, boundary, parentId, siblingId, childId);
        redNode.setChildCount(childCount);
        return redNode;
    }
    
    /**
     * 
     * @param candidate
     * @throws IOException 
     */
    public void writeNode(final FileNode candidate) throws IOException {
        final int indexNode    = candidate.getNodeId();
        adjustBuffer(indexNode);
        final int writeIndex = (int) ((beginPosition + (indexNode - 1) * nodeSize) - currentBufferPosition);
        final int currentLimit = writeIndex + nodeSize;
        writeBufferLimit = Math.max(writeBufferLimit, currentLimit);
        byteBuffer.limit(writeIndex + nodeSize);
        byteBuffer.position(writeIndex);
        double[] candidateBound = candidate.getBoundary();
        for (int i = 0; i < boundLength; i++) {
            byteBuffer.putDouble(candidateBound[i]);
        }
        byteBuffer.putInt(candidate.getParentId());
        byteBuffer.putInt(candidate.getSiblingId());
        byteBuffer.putInt(candidate.getChildId());
        byteBuffer.putInt(candidate.getChildCount());
    }
    
    private void adjustBuffer(final int nodeID) throws IOException {
        final int readIndex = (int) (beginPosition + (nodeID - 1) * nodeSize);
        if (readIndex < currentBufferPosition || readIndex >= currentBufferPosition + bufferLength) {
            // on ecrit ce qui est dans le buffer et
            byteBuffer.position(0);
            byteBuffer.limit(writeBufferLimit);
            int writtenByte = 0;
            while (writtenByte != writeBufferLimit) {
                writtenByte = inOutChannel.write(byteBuffer, currentBufferPosition);
            }
            writeBufferLimit = 0;
            byteBuffer.clear();
            final int div = (int) (((readIndex - beginPosition)) / bufferLength);
            currentBufferPosition = div * bufferLength + beginPosition;
            inOutChannel.read(byteBuffer, currentBufferPosition);
        }
    }
    
    /**
     * 
     * @param candidate
     * @throws IOException 
     */
    public void deleteNode(final FileNode candidate) throws IOException {
        recycleID.add(candidate.getNodeId());
    }
    
    public void rewind() throws IOException{
        byteBuffer.position(0);
        byteBuffer.limit(writeBufferLimit);
        int writtenByte = 0;
        while (writtenByte != writeBufferLimit) {
            writtenByte = inOutChannel.write(byteBuffer, currentBufferPosition);
        }
        inOutChannel.position(beginPosition);
        currentBufferPosition = beginPosition;
        writeBufferLimit = 0;
        nodeId = 1;
        recycleID.clear();
    }
     public void close() throws IOException{
        byteBuffer.position(0);
        byteBuffer.limit(writeBufferLimit);
        int writtenByte = 0;
        while (writtenByte != writeBufferLimit) {
            writtenByte = inOutChannel.write(byteBuffer, currentBufferPosition);
        }
        inOutChannel.close();
     }
     
     public FileNode createNode(double[] boundary, int parentId, int siblingId, int childId) {
//         final int currentID = (!recycleID.isEmpty()) ? recycleID.remove(0) : nodeId++;
//         return new FileNode(this, currentID, boundary, parentId, siblingId, childId);
         return new FileNode(this, nodeId++, boundary, parentId, siblingId, childId);
     }
     
}
