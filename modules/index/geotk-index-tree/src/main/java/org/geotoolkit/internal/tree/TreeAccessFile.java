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
package org.geotoolkit.internal.tree;

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
import org.geotoolkit.index.tree.Node;
import static org.geotoolkit.index.tree.TreeUtilities.intersects;
import org.geotoolkit.index.tree.basic.BasicRTree;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.hilbert.HilbertTreeAccessFile;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * {@link TreeAccess} implementation.<br/>
 * Store all {@link Node} architecture use by {@link Tree} on disk drive.
 * 
 * @author Rémi Maréchal (Geomatys).
 */
public class TreeAccessFile extends TreeAccess {
    
    /**
     * Number of Integer per Node.<br/><br/>
     * parent ID<br/>
     * sibling ID<br/>
     * child ID<br/>
     * children number.
     * 
     * @see HilbertTreeAccessFile#INT_NUMBER
     */
    private static int INT_NUMBER = 4;
    
    /**
     * boundary table value length of each Node.
     */
    protected final int boundLength;
    
    /**
     * Length in Byte unit of a Node in file on hard disk. 
     */
    private final int nodeSize;
    
    /**
     * {@link FileChannel} position just after write or read file head.<br/>
     * Its also file position of first Node red or written.
     */
    private final int beginPosition;
    
    /**
     * Stream to read and write Tree information and Node.
     */
    private final RandomAccessFile inOutStream;
    
    /**
     * Channel to read and write Tree information and Node.
     */
    private final FileChannel inOutChannel;
    
    /**
     * {@link ByteBuffer} to read and write Node from file on hard disk.
     */
    protected final ByteBuffer byteBuffer;
    
    /**
     * ByteBuffer Length.
     */
    private final int bufferLength;
    
    /**
     * ByteBuffer attributs use to read and write.
     */
    protected int writeBufferLimit;
    private long currentBufferPosition;
    private int rwIndex;
    
    /**
     * Build a {@link Tree} from a already filled {@link File}.<br/>
     * The default length value of ByteBuffer which read and write on hard disk, is 4096 Bytes.
     * 
     * @param input {@code File} which already contains {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    public TreeAccessFile(final File input, final int magicNumber, final double versionNumber, final int byteBufferLength)  throws IOException, ClassNotFoundException {
        this(input, magicNumber, versionNumber, byteBufferLength, INT_NUMBER);
    }
    
    /**
     * Build a {@link Tree} from a already filled {@link File}.
     * 
     * @param input {@code File} which already contains {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    public TreeAccessFile( final File input, final int magicNumber, final double versionNumber) throws IOException, ClassNotFoundException{
        this(input, magicNumber, versionNumber, DEFAULT_BUFFER_LENGTH, INT_NUMBER);
    }
    
    /**
     * Build a {@link Tree} from a already filled {@link File}.
     * 
     * @param input {@code File} which already contains {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @param integerNumberPerNode integer number per Node which will be red/written during Node reading/writing process. 
     * @throws IOException if problem during read or write Node.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    protected TreeAccessFile( final File input, final int magicNumber, final double versionNumber , 
            final int byteBufferLength, final int integerNumberPerNode) throws IOException, ClassNotFoundException {
        super();
        
        // stream
        inOutStream  = new RandomAccessFile(input, "rw");
        inOutChannel = inOutStream.getChannel();
        
        /*****************************  read head ******************************/
        // read magicNumber
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
        // read hilbert Order
        hilbertOrder = inOutStream.readInt();
        // read SplitCase
        final byte sm = inOutStream.readByte();
        splitMade = ((sm & ((byte)1)) != 0) ? SplitCase.QUADRATIC : SplitCase.LINEAR;
        // read nodeID
        nodeId = inOutStream.readInt();
        if (nodeId == 0)
            throw new IllegalStateException("User shouldn't invoked tree.close() methode after insertion. You should build again RTree.");
        treeIdentifier = inOutStream.readInt();
        // read element number within tree
        eltNumber = inOutStream.readInt();
        // read CRS
        final int byteTabLength   = inOutStream.readInt();
        final byte[] crsByteArray = new byte[byteTabLength];
        inOutStream.read(crsByteArray, 0, byteTabLength);
        final ObjectInputStream crsInputS = new ObjectInputStream(new ByteArrayInputStream(crsByteArray));
        crs = (CoordinateReferenceSystem) crsInputS.readObject();
        crsInputS.close();
        /*****************************  end head ******************************/
        
        this.boundLength = crs.getCoordinateSystem().getDimension() << 1;
        
        //nanbound
        nanBound = new double[boundLength];
        Arrays.fill(nanBound, Double.NaN);
        
        /**
         * Node size : boundary weigth + Byte properties + n Integers.<br/><br/>
         * 
         * see INT_NUMBER attribut.
         */
        nodeSize = (boundLength * Double.SIZE + Integer.SIZE * integerNumberPerNode) / 8 + 1;
        
        // buffer attributs
        final int div = byteBufferLength / nodeSize;
        this.bufferLength = div * nodeSize;
        byteBuffer = ByteBuffer.allocateDirect(bufferLength);
        byteBuffer.order(bO);
        
        beginPosition = (int) inOutChannel.position();
        currentBufferPosition = beginPosition;
        writeBufferLimit = 0;
        
        // root 
        inOutChannel.read(byteBuffer, currentBufferPosition);
        root = this.readNode(1);
    }
    
    /**
     * Build and insert {@link Node} architecture in a {@link File}.<br/>
     * If file is not empty, data within it will be overwrite.<br/>
     * If file does not exist a file will be create.<br/><br/>
     * 
     * Constructor only use by {@link BasicRTree} implementation.
     * 
     * @param outPut {@code File} where {@link Node} architecture which will be write.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param maxElements element number per cell.
     * @param crs 
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessFile(final File outPut, final int magicNumber, final double versionNumber, final int maxElements, 
            final SplitCase splitMade, final CoordinateReferenceSystem crs, final int byteBufferLength) throws IOException {
        this(outPut, magicNumber, versionNumber, maxElements, splitMade, crs, byteBufferLength, INT_NUMBER);
    }
    
    /**
     * Build and insert {@link Node} architecture in a {@link File}.<br/>
     * If file is not empty, data within it will be overwrite.<br/>
     * If file does not exist a file will be create.<br/><br/>
     * 
     * Constructor only use by {@link BasicRTree} implementation.
     * 
     * @param outPut {@code File} where {@link Node} architecture which will be write.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param maxElements element number per cell.
     * @param crs 
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessFile(final File outPut, final int magicNumber, final double versionNumber, final int maxElements, 
             final SplitCase splitMade, final CoordinateReferenceSystem crs) throws IOException {
        this(outPut, magicNumber, versionNumber, maxElements, splitMade, crs, DEFAULT_BUFFER_LENGTH, INT_NUMBER);
    }
    
    /**
     * Build and insert {@link Node} architecture in a {@link File}.<br/>
     * If file is not empty, data within it will be overwrite.<br/>
     * If file does not exist a file will be create.
     * 
     * @param outPut {@code File} where {@link Node} architecture which will be write.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param maxElements element number per cell.
     * @param crs 
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessFile(final File outPut, final int magicNumber, final double versionNumber, 
            final int maxElements, final CoordinateReferenceSystem crs, final int byteBufferLength) throws IOException {
        this(outPut, magicNumber, versionNumber, maxElements, null, crs, byteBufferLength, INT_NUMBER);
    }
    
    /**
     * Build and insert {@link Node} architecture in a {@link File}.<br/>
     * If file is not empty, data within it will be overwrite.<br/>
     * If file does not exist a file will be create.
     * 
     * @param outPut {@code File} where {@link Node} architecture which will be write.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param maxElements element number per cell.
     * @param crs 
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessFile(final File outPut, final int magicNumber, final double versionNumber, 
            final int maxElements, final CoordinateReferenceSystem crs) throws IOException {
        this(outPut, magicNumber, versionNumber, maxElements, null, crs, DEFAULT_BUFFER_LENGTH, INT_NUMBER);
    }
    
    /**
     * Build and insert {@link Node} architecture in a {@link File}.<br/>
     * If file is not empty, data within it will be overwrite.<br/>
     * If file does not exist a file will be create.
     * 
     * @param outPut {@code File} where {@link Node} architecture which will be write.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param maxElements element number per cell.
     * @param crs 
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @param integerNumberPerNode integer number per Node which will be red/written during Node reading/writing process. 
     * @throws IOException if problem during read or write Node.
     */
    protected TreeAccessFile(final File outPut, final int magicNumber, final double versionNumber, final int maxElements,
            final SplitCase splitMade, final CoordinateReferenceSystem crs, final int byteBufferLength, final int integerNumberPerNode) throws IOException {
        super();
        
        this.crs         = crs;
        this.maxElement  = maxElements;
        this.boundLength = crs.getCoordinateSystem().getDimension() << 1;
        this.splitMade   = splitMade;
        
        //nanbound
        nanBound = new double[boundLength];
        Arrays.fill(nanBound, Double.NaN);
        
        /**
         * Node size : boundary weigth + Byte properties + n Integers.<br/><br/>
         * 
         * see this.INT_NUMBER attribut.
         */
        nodeSize = (boundLength * Double.SIZE + Integer.SIZE * integerNumberPerNode) / 8 + 1;
        
        final int div = byteBufferLength / nodeSize; // 4096
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
        // write hilbert order
        inOutStream.writeInt(hilbertOrder);
        // write splitCase
        inOutStream.writeByte((byte) ((splitMade == null || splitMade == SplitCase.LINEAR) ? 0 : 1));
        // write nodeID
        inOutStream.writeInt(0);
        // write treeIdentifier
        inOutStream.writeInt(0);
        // write element number within tree
        inOutStream.writeInt(0);
        // write CRS
        objOutput.writeObject(crs);
        objOutput.flush();
        final byte[] crsByteArray = temp.toByteArray();
        inOutStream.writeInt(crsByteArray.length);
        inOutStream.write(crsByteArray);
        objOutput.close();
        /*****************************  end head ******************************/
        
        beginPosition         = (int) inOutChannel.position();
        currentBufferPosition = beginPosition;
        writeBufferLimit      = 0;
        
        // root 
        root = null;
    }
        
    /**
     * {@inheritDoc }.
     */
    @Override
    public void internalSearch(int nodeID) throws IOException {
        adjustBuffer(nodeID);
        final double[] boundary = new double[boundLength];
        for (int i = 0; i < boundLength; i++) {
            boundary[i] = byteBuffer.getDouble();
        }
        byteBuffer.position(byteBuffer.position() + 5);// step properties (1 byte) and step parent ID (int  : 4 byte)
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
     * {@inheritDoc }.
     */
    @Override
    public Node readNode(final int indexNode) throws IOException {
        adjustBuffer(indexNode);
        final double[] boundary = new double[boundLength];
        for (int i = 0; i < boundLength; i++) {
            boundary[i] = byteBuffer.getDouble();
        }
        final byte properties  = byteBuffer.get();
        final int parentId     = byteBuffer.getInt();
        final int siblingId    = byteBuffer.getInt();
        final int childId      = byteBuffer.getInt();
        final int childCount   = byteBuffer.getInt();
        final Node redNode = new Node(this, indexNode, boundary, properties, parentId, siblingId, childId);
        redNode.setChildCount(childCount);
        return redNode;
    }
        
    /**
     * {@inheritDoc }.
     */
    @Override
    public void writeNode(final Node candidate) throws IOException {
        final int indexNode    = candidate.getNodeId();
        adjustBuffer(indexNode);
        writeBufferLimit = Math.max(writeBufferLimit, byteBuffer.limit());
        double[] candidateBound = candidate.getBoundary();
        if (candidateBound == null) candidateBound = nanBound;
        for (int i = 0; i < boundLength; i++) {
            byteBuffer.putDouble(candidateBound[i]);
        }
        byteBuffer.put(candidate.getProperties());
        byteBuffer.putInt(candidate.getParentId());
        byteBuffer.putInt(candidate.getSiblingId());
        byteBuffer.putInt(candidate.getChildId());
        byteBuffer.putInt(candidate.getChildCount());
    }
    
    /**
     * Adjust buffer position relative to filechanel which contain data, 
     * and prepare bytebuffer position and limit for reading or writing action.
     * 
     * @param treeIdentifier 
     * @throws IOException 
     */
    protected void adjustBuffer(final int nodeID) throws IOException {
        rwIndex = beginPosition + (nodeID - 1) * nodeSize;
        if (rwIndex < currentBufferPosition || rwIndex >= currentBufferPosition + bufferLength) {
            // write current data within bytebuffer in channel.
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
        byteBuffer.limit(rwIndex + nodeSize);
        byteBuffer.position(rwIndex);
    }
        
    /**
     * {@inheritDoc }.
     */
    @Override
    public void removeNode(final Node candidate) {
        recycleID.add(candidate.getNodeId());
    }
        
    /**
     * {@inheritDoc }.
     */
    @Override
    public void rewind() throws IOException {
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
    }
        
    /**
     * {@inheritDoc }.
     */
    @Override
     public void close() throws IOException {
        byteBuffer.position(0);
        byteBuffer.limit(writeBufferLimit);
        int writtenByte = 0;
        while (writtenByte != writeBufferLimit) {
            writtenByte = inOutChannel.write(byteBuffer, currentBufferPosition);
        }
        // write nodeID
        inOutChannel.position(22); 
        inOutStream.writeInt(nodeId);
        inOutStream.writeInt(treeIdentifier);
        inOutStream.writeInt(eltNumber);
        //close
        inOutChannel.close();
     }
}
