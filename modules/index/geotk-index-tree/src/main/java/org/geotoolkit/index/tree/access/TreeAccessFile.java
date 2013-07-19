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
package org.geotoolkit.index.tree.access;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.geotoolkit.index.tree.FileNode;
import static org.geotoolkit.index.tree.DefaultTreeUtils.intersects;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author rmarechal
 */
public class TreeAccessFile extends TreeAccess {
    
    private final int boundLength;
    private final int nodeSize;
    private final long beginPosition;
    private final RandomAccessFile inOutStream;
    private final FileChannel inOutChannel;
    
    private final ByteBuffer byteBuffer;
    private long currentBufferPosition;
    private int writeBufferLimit;
    private final int bufferLength;
    private List<Integer> recycleID = new LinkedList<Integer>();
    
    public TreeAccessFile( final File input, final int magicNumber, final double versionNumber ) throws IOException, ClassNotFoundException {
        super();
        
        // stream
        inOutStream  = new RandomAccessFile(input, "rw");
        inOutChannel = inOutStream.getChannel();
        
        /***************************  read head ******************************/
        // write magicNumber
        final int mgNumber = inOutStream.readInt();
        if (magicNumber != mgNumber)
            throw new IllegalArgumentException("tree type identifier should match. expected identifier : "+magicNumber+". Found : "+mgNumber);
        
        // read ByteOrder
        final boolean fbool = inOutStream.readBoolean();
        final ByteOrder bO  = (fbool) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        // read version number
        final double vN = inOutStream.readDouble();
        if (vN != versionNumber)
            throw new IllegalArgumentException("Wrong version number. Expected : "+versionNumber+". Version found in tree file : "+vN);
        // read maxElement
        maxElement = inOutStream.readInt();
        // read nodeID
        nodeId = inOutStream.readInt();
        if (nodeId == 0)
            throw new IllegalStateException("User shouldn't invoked tree.close() methode after insertion. You should build again RTree.");
        treeIdentifier = inOutStream.readInt();
        // read CRS
        final int byteTabLength   = inOutStream.readInt();
        final byte[] crsByteArray = new byte[byteTabLength];
        inOutStream.read(crsByteArray, 0, byteTabLength);
        final ObjectInputStream crsInputS = new ObjectInputStream(new ByteArrayInputStream(crsByteArray));
        crs = (CoordinateReferenceSystem) crsInputS.readObject();
        crsInputS.close();
        /*****************************  end head ******************************/
        
        final int dimension = crs.getCoordinateSystem().getDimension();
        this.boundLength = dimension << 1;
        
        // Node size : boundary weigth + parent ID + 1st sibling Integer + 1st child Integer + child number.
        nodeSize = (dimension * Double.SIZE + ((Integer.SIZE) << 1)) >> 2;
        
        // buffer attributs
        final int div = 8192 / nodeSize;// 4096
        this.bufferLength = div * nodeSize;
        byteBuffer = ByteBuffer.allocateDirect(bufferLength);
        byteBuffer.order(bO);
        
        beginPosition = inOutChannel.position();
        currentBufferPosition = beginPosition;
        writeBufferLimit = 0;
        
        // root 
        inOutChannel.read(byteBuffer, currentBufferPosition);
        root = this.readNode(1);
    }
    
    public TreeAccessFile(final File outPut, final int magicNumber, final double versionNumber, int maxElements, CoordinateReferenceSystem crs) throws IOException {
        super(maxElements, crs);
        
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
        final ByteArrayOutputStream temp   = new ByteArrayOutputStream();
        final ObjectOutputStream objOutput = new ObjectOutputStream(temp);
        // write magicNumber
        inOutStream.writeInt(magicNumber);
        // write bytebuffer order
        inOutStream.writeBoolean(bO == ByteOrder.LITTLE_ENDIAN);
        // write version number
        inOutStream.writeDouble(versionNumber);
        // write element number per Node
        inOutStream.writeInt(maxElements);
        // write nodeID
        inOutStream.writeInt(0);
        // write treeIdentifier
        inOutStream.writeInt(0);
        // write CRS
        objOutput.writeObject(crs);
        objOutput.flush();
        final byte[] crsByteArray = temp.toByteArray();
        inOutStream.writeInt(crsByteArray.length);
        inOutStream.write(crsByteArray);
        objOutput.close();
        /*****************************  end head ******************************/
        
        beginPosition = inOutChannel.position();
        currentBufferPosition = beginPosition;
        writeBufferLimit = 0;
        
        // root 
        root = null;
    }
    
    @Override
    public int[] search(int nodeID, double[] regionSearch) throws IOException {
        currentLength     = 100;
        tabSearch         = new int[currentLength];
        currentPosition   = 0;
        this.regionSearch = regionSearch;
        internalSearch(nodeID);
        return Arrays.copyOf(tabSearch, currentPosition);
    }
    
    @Override
    public void internalSearch(int nodeID) throws IOException {
        adjustBuffer(nodeID);
        final int searchIndex = (int) ((beginPosition + (nodeID - 1) * nodeSize) - currentBufferPosition);
        byteBuffer.limit(searchIndex + nodeSize);
        byteBuffer.position(searchIndex);
        final double[] boundary = new double[boundLength];
        for (int i = 0; i < boundLength; i++) {
            boundary[i] = byteBuffer.getDouble();
        }
        byteBuffer.position(byteBuffer.position() + 4);// step parent ID
        final int sibling = byteBuffer.getInt();
        final int child   = byteBuffer.getInt();
        byteBuffer.position(byteBuffer.position() + 4);// step child count
        if (sibling != 0) {
            internalSearch(sibling);
        }
        if (intersects(boundary, regionSearch, true)) {
            if (child > 0) {
                internalSearch(child);
            } else {
                if (child == 0)
                    throw new IllegalStateException("child index should never be 0.");
                if (currentPosition == currentLength) {
                    currentLength = currentLength << 1;
                    final int[] tabTemp = tabSearch;
                    tabSearch = new int[currentLength];
                    System.arraycopy(tabTemp, 0, tabSearch, 0, currentPosition);
                }
                tabSearch[currentPosition++] = -child;
            }
        } 
    }
    
    /**
     * 
     * @param indexNode
     * @return
     * @throws IOException 
     */
    @Override
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
    @Override
    public void writeNode(final FileNode candidate) throws IOException {
        final int indexNode    = candidate.getNodeId();
        adjustBuffer(indexNode);
        final int writeIndex = (int) ((beginPosition + (indexNode - 1) * nodeSize) - currentBufferPosition);
        final int currentLimit = writeIndex + nodeSize;
        writeBufferLimit = Math.max(writeBufferLimit, currentLimit);
        byteBuffer.limit(currentLimit);
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
    @Override
    public void deleteNode(final FileNode candidate) throws IOException {
        recycleID.add(candidate.getNodeId());
    }
    
    @Override
    public void rewind() throws IOException{
        super.rewind();
        byteBuffer.position(0);
        byteBuffer.limit(writeBufferLimit);
        int writtenByte = 0;
        while (writtenByte != writeBufferLimit) {
            writtenByte = inOutChannel.write(byteBuffer, currentBufferPosition);
        }
        inOutChannel.position(beginPosition);
        currentBufferPosition = beginPosition;
        writeBufferLimit = 0;
        recycleID.clear();
    }
    
    @Override
     public void close() throws IOException{
        byteBuffer.position(0);
        byteBuffer.limit(writeBufferLimit);
        int writtenByte = 0;
        while (writtenByte != writeBufferLimit) {
            writtenByte = inOutChannel.write(byteBuffer, currentBufferPosition);
        }
        // write nodeID
        inOutChannel.position(17); // a checker
        inOutStream.writeInt(nodeId);
        inOutStream.writeInt(treeIdentifier);
        //close
        inOutChannel.close();
     }
//     
    @Override
     public FileNode createNode(double[] boundary, int parentId, int siblingId, int childId) {
         final int currentID = (!recycleID.isEmpty()) ? recycleID.remove(0) : nodeId++;
         return new FileNode(this, currentID, boundary, parentId, siblingId, childId);
     }
}
