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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;

import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.index.tree.Node;
import static org.geotoolkit.internal.tree.TreeUtilities.intersects;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * {@link TreeAccess} implementation.<br/>
 * Store all {@link Node} architecture use by {@link Tree} on disk drive.
 * 
 * @author Rémi Maréchal (Geomatys).
 */
public class TreeAccessFile extends ChannelTreeAccess {

    /**
     * Position in the tree file where CRS description should begin.
     */
    private static final int CRS_POSITION = 34;

    /**
     * Number of Integer per Node.<br/><br/>
     * parent ID<br/>
     * sibling ID<br/>
     * child ID<br/>
     * children number.
     * 
     * @see HilbertTreeAccessFile#INT_NUMBER
     */
    private static final int INT_NUMBER = 4;
    
//    /**
//     * boundary table value length of each Node.
//     */
//    protected final int boundLength;
//    
//    /**
//     * Length in Byte unit of a Node in file on hard disk. 
//     */
//    private final int nodeSize;
//    
//    /**
//     * {@link FileChannel} position just after write or read file head.<br/>
//     * Its also file position of first Node red or written.
//     */
//    private final int beginPosition;
//    
////    /**
////     * Stream to read and write Tree information and Node.
////     */
////    private final RandomAccessFile inOutStream;
//    
//    /**
//     * Channel to read and write Tree information and Node.
//     */
//    private final SeekableByteChannel inOutChannel;
//    
//    /**
//     * {@link ByteBuffer} to read and write Node from file on hard disk.
//     */
//    protected final ByteBuffer byteBuffer;
//    
//    /**
//     * ByteBuffer Length.
//     */
//    private final int bufferLength;
//    
//    /**
//     * ByteBuffer attributs use to read and write.
//     */
//    protected int writeBufferLimit;
//    private long currentBufferPosition;
//    private int rwIndex;
    
    /**
     * Build a {@link Tree} from a already filled {@link File}.<br/><br/>
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
     * Build a {@link Tree} from a already filled {@link File}.<br><br>
     * 
     * Note : The default length value of ByteBuffer which read and write on hard disk, is 4096 Bytes.
     * 
     * @param input {@code File} which already contains {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @throws IOException if problem during read or write Node.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    public TreeAccessFile( final File input, final int magicNumber, final double versionNumber) throws IOException, ClassNotFoundException{
        this(input, magicNumber, versionNumber, DEFAULT_BUFFER_LENGTH, INT_NUMBER);
    }
    
    /**
     * Build a {@link TreeAccess} from a already filled {@link File}.
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
        super(getChannel(input), magicNumber, versionNumber, byteBufferLength, integerNumberPerNode);
//        super();
        
        // stream
//        final RandomAccessFile inOutStream  = new RandomAccessFile(input, "rw");
//        inOutChannel = inOutStream.getChannel();
//        
//        final ByteBuffer magicOrderBuffer = ByteBuffer.allocate(5);//-- a stipuler en bigendian
////        magicOrderBuffer.order(ByteOrder.BIG_ENDIAN);//-- stand by byte order comportement
//        
//        inOutChannel.read(magicOrderBuffer);
//        magicOrderBuffer.clear();
//        
//        assert inOutChannel.position() == 5;
//        
//        /*****************************  read head ******************************/
//        //-- read magicNumber
//        final int mgNumber = magicOrderBuffer.getInt();
//        if (magicNumber != mgNumber) {
//            final String createTreeType;
//            final String redTreeType;
//            switch (magicNumber) {
//                case TreeUtilities.BASIC_NUMBER   : createTreeType = " BasicRTree ";break;
//                case TreeUtilities.STAR_NUMBER    : createTreeType = " StarRTree ";break;
//                case TreeUtilities.HILBERT_NUMBER : createTreeType = " hilbertRTree ";break;
//                default : throw new IllegalArgumentException("Unknown tree type for magic number : "+magicNumber);
//            };
//            switch (mgNumber) {
//                case TreeUtilities.BASIC_NUMBER   : redTreeType = " BasicRTree ";break;
//                case TreeUtilities.STAR_NUMBER    : redTreeType = " StarRTree ";break;
//                case TreeUtilities.HILBERT_NUMBER : redTreeType = " hilbertRTree ";break;
//                default : throw new IllegalArgumentException("Unknown tree type for magic number : "+mgNumber);
//            };
//            final String messageError = "You try to create a "+createTreeType
//                    +"RTree from a file which has been filled by a "+redTreeType
//                    +"RTree implementation.";
//            throw new IllegalArgumentException(messageError);
//    }
//    
//        
//        // read ByteOrder
//        final boolean fbool = magicOrderBuffer.get() == 1;
//        final ByteOrder bO  = (fbool) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;//-- stand by byte order comportement
//        
//        //-- fin de l'entete dans le bon byteOrder
//        final ByteBuffer headBuffer = ByteBuffer.allocate(33);
////        headBuffer.order(bO);//-- stand by byte order comportement
//        inOutChannel.read(headBuffer);
//        headBuffer.clear();
//        assert inOutChannel.position() == 38;
//        
//        // read version number
//        final double vN = headBuffer.getDouble();
//        if (vN != versionNumber)
//            throw new IllegalArgumentException("Wrong version number. Expected : "+versionNumber+". Version found in tree file : "+vN);
//        // read maxElement
//        maxElement = headBuffer.getInt();
//        // read hilbert Order
//        hilbertOrder = headBuffer.getInt();
//        // read SplitCase
//        final byte sm = headBuffer.get();
//        splitMade = ((sm & ((byte)1)) != 0) ? SplitCase.QUADRATIC : SplitCase.LINEAR;
//        // read nodeID
//        nodeId = headBuffer.getInt();
//        if (nodeId == 0)
//            throw new IllegalStateException("User has not been invoked tree.close() method after insertions. You should build again RTree.");
//        treeIdentifier = headBuffer.getInt();
//        // read element number within tree
//        eltNumber = headBuffer.getInt();
//        // read CRS
//        final int byteTabLength   = headBuffer.getInt();
//        final byte[] crsByteArray = new byte[byteTabLength];
//        
//        final ByteBuffer crsBuffer = ByteBuffer.wrap(crsByteArray);
////        crsBuffer.order(bO);//-- stand by byte order comportement
//        
//        inOutChannel.read(crsBuffer);
//        
//        assert inOutChannel.position() == 38 + crsByteArray.length;
//        try (final ObjectInputStream crsInputS =
//                     new ObjectInputStream(new ByteArrayInputStream(crsByteArray))) {
//            crs = (CoordinateReferenceSystem) crsInputS.readObject();
//        }
//        /*****************************  end head ******************************/
//        
//        this.boundLength = crs.getCoordinateSystem().getDimension() << 1;
//        
//        //-- nanbound
//        nanBound = new double[boundLength];
//        Arrays.fill(nanBound, Double.NaN);
//        
//        /**
//         * Node size : boundary weigth + Byte properties + n Integers.<br/><br/>
//         * 
//         * see INT_NUMBER attribut.
//         */
//        nodeSize = (boundLength * Double.SIZE + Integer.SIZE * integerNumberPerNode) / 8 + 1;
//        
//        // buffer attributs
//        final int div = byteBufferLength / nodeSize;
//        this.bufferLength = div * nodeSize;
//        byteBuffer = ByteBuffer.allocate(bufferLength);
////        byteBuffer.order(bO);//-- stand by byte order comportement
//        
//        beginPosition = (int) inOutChannel.position();
//        currentBufferPosition = beginPosition;
//        writeBufferLimit = 0;
//        
//        // root 
//        inOutChannel.position(currentBufferPosition);
//        inOutChannel.read(byteBuffer);
//        inOutChannel.position(beginPosition);
//        root = this.readNode(1);
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
     * @param splitMade define tree node split made.
     * @param crs 
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     */
    public TreeAccessFile(final File outPut, final int magicNumber, final double versionNumber, final int maxElements, 
            final SplitCase splitMade, final CoordinateReferenceSystem crs, final int byteBufferLength) throws IOException {
        this(outPut, magicNumber, versionNumber, maxElements, 0, splitMade, crs, byteBufferLength, INT_NUMBER);
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
        this(outPut, magicNumber, versionNumber, maxElements, 0, splitMade, crs, DEFAULT_BUFFER_LENGTH, INT_NUMBER);
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
        this(outPut, magicNumber, versionNumber, maxElements, 0, null, crs, byteBufferLength, INT_NUMBER);
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
        this(outPut, magicNumber, versionNumber, maxElements, 0, null, crs, DEFAULT_BUFFER_LENGTH, INT_NUMBER);
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
    protected TreeAccessFile(final File outPut, final int magicNumber, final double versionNumber, final int maxElements, final int hilbertOrder,
            final SplitCase splitMade, final CoordinateReferenceSystem crs, final int byteBufferLength, final int integerNumberPerNode) throws IOException {
        super(getChannel(outPut), magicNumber, versionNumber, maxElements, hilbertOrder, splitMade, crs, byteBufferLength, integerNumberPerNode);
//        super();
        
//        this.crs          = crs;
//        this.maxElement   = maxElements;
//        this.boundLength  = crs.getCoordinateSystem().getDimension() << 1;
//        this.hilbertOrder = hilbertOrder;
//        this.splitMade    = splitMade;
//        
//        //nanbound
//        nanBound = new double[boundLength];
//        Arrays.fill(nanBound, Double.NaN);
//        
//        /**
//         * Node size : boundary weight + Byte properties + n Integers.<br/><br/>
//         * 
//         * see this.INT_NUMBER attribute.
//         */
//        nodeSize = (boundLength * Double.SIZE + Integer.SIZE * integerNumberPerNode) / 8 + 1;
//        
//        final int div = byteBufferLength / nodeSize; // 4096
//        this.bufferLength = div * nodeSize;
//        
//        // stream
//        final RandomAccessFile inOutStream  = new RandomAccessFile(outPut, "rw");
//        inOutChannel = inOutStream.getChannel();
//        
//        //-- current writing order
//        final ByteOrder bO = ByteOrder.nativeOrder();
//        
//        //-- magic number and byte order
//        final ByteBuffer magicOrderBuffer = ByteBuffer.allocate(5);
////        magicOrderBuffer.order(ByteOrder.BIG_ENDIAN);//-- stand by byte order comportement
//        // write magicNumber
//        magicOrderBuffer.putInt(magicNumber);
//        
//        // write bytebuffer order
//        magicOrderBuffer.put((byte)(bO == ByteOrder.LITTLE_ENDIAN ? 1 : 0));
//        magicOrderBuffer.flip();
//        inOutChannel.write(magicOrderBuffer);
//        assert inOutChannel.position() == 5;
//        //-------------------------------------------------------
//        
//        //--------------------------- head ending ------------------------------
//        final ByteBuffer headBuffer = ByteBuffer.allocate(33);
////        headBuffer.order(bO);//-- stand by byte order comportement
//        
//        /***************************  write head ******************************/
//        final ByteArrayOutputStream temp = new ByteArrayOutputStream();
//        try (final ObjectOutputStream objOutput = new ObjectOutputStream(temp)) {
//            // write version number
//            headBuffer.putDouble(versionNumber);
//            // write element number per Node
//            headBuffer.putInt(maxElements);
//            // write hilbert order
//            headBuffer.putInt(hilbertOrder);
//            // write splitCase
//            headBuffer.put((byte) ((splitMade == null || splitMade == SplitCase.LINEAR) ? 0 : 1));
//            // write nodeID
//            headBuffer.putInt(0);
//            // write treeIdentifier
//            headBuffer.putInt(0);
//            // write element number within tree
//            headBuffer.putInt(0);
//            // write CRS
//            objOutput.writeObject(crs);
//            objOutput.flush();
//            final byte[] crsByteArray = temp.toByteArray();
//            headBuffer.putInt(crsByteArray.length);
//            
//            //-- write head
//            headBuffer.flip();
//            inOutChannel.write(headBuffer);
//            assert inOutChannel.position() == 38;
//            
//            final ByteBuffer crsbuff = ByteBuffer.allocate(crsByteArray.length);
////            crsbuff.order(bO);//-- stand by byte order comportement
//            crsbuff.put(crsByteArray);
//            crsbuff.flip();
//            inOutChannel.write(crsbuff);//-- write all the crs array
//            
//            assert inOutChannel.position() == (crsByteArray.length + 38);
//            
//    }
//        /*****************************  end head ******************************/
//        
//        
//        // ByteBuffer
//        byteBuffer = ByteBuffer.allocate((int)bufferLength);
////        byteBuffer.order(bO);//-- stand by byte order comportement
//    
//        beginPosition         = (int) inOutChannel.position();
//        currentBufferPosition = beginPosition;
//        writeBufferLimit      = 0;
//        
//        // root 
//        root = null;
    }
        
//    /**
//     * {@inheritDoc }.
//     */
//    @Override
//    public void internalSearch(int nodeID) throws IOException {
//        adjustBuffer(nodeID);
//        final double[] boundary = new double[boundLength];
//        for (int i = 0; i < boundLength; i++) {
//            boundary[i] = byteBuffer.getDouble();
//        }
//        byteBuffer.position(byteBuffer.position() + 5);// step properties (1 byte) and step parent ID (int  : 4 byte)
//        final int sibling = byteBuffer.getInt();
//        final int child   = byteBuffer.getInt();
//        byteBuffer.position(byteBuffer.position() + 4);// step child count
//        if (sibling != 0) {
//            internalSearch(sibling);
//        }
//        if (intersects(boundary, regionSearch, true)) {
//            if (child > 0) {
//                internalSearch(child);
//            } else {
//                if (child == 0)
//                    throw new IllegalStateException("child index should never be 0.");
//                if (currentPosition == currentLength) {
//                    currentLength = currentLength << 1;
//                    final int[] tabTemp = tabSearch;
//                    tabSearch = new int[currentLength];
//                    System.arraycopy(tabTemp, 0, tabSearch, 0, currentPosition);
//                }
//                tabSearch[currentPosition++] = -child;
//            }
//        } 
//    }
//
//    /**
//     * {@inheritDoc }.
//     */
//    @Override
//    public synchronized Node readNode(final int indexNode) throws IOException {
//        adjustBuffer(indexNode);
//        final double[] boundary = new double[boundLength];
//        for (int i = 0; i < boundLength; i++) {
//            boundary[i] = byteBuffer.getDouble();
//        }
//        final byte properties  = byteBuffer.get();
//        final int parentId     = byteBuffer.getInt();
//        final int siblingId    = byteBuffer.getInt();
//        final int childId      = byteBuffer.getInt();
//        final int childCount   = byteBuffer.getInt();
//        final Node redNode = new Node(this, indexNode, boundary, properties, parentId, siblingId, childId);
//        redNode.setChildCount(childCount);
//        return redNode;
//    }
//        
//    /**
//     * {@inheritDoc }.
//     */
//    @Override
//    public synchronized void writeNode(final Node candidate) throws IOException {
//        final int indexNode    = candidate.getNodeId();
//        adjustBuffer(indexNode);
//        writeBufferLimit = Math.max(writeBufferLimit, byteBuffer.limit());
//        double[] candidateBound = candidate.getBoundary();
//        if (candidateBound == null) candidateBound = nanBound;
//        for (int i = 0; i < boundLength; i++) {
//            byteBuffer.putDouble(candidateBound[i]);
//        }
//        byteBuffer.put(candidate.getProperties());
//        byteBuffer.putInt(candidate.getParentId());
//        byteBuffer.putInt(candidate.getSiblingId());
//        byteBuffer.putInt(candidate.getChildId());
//        byteBuffer.putInt(candidate.getChildCount());
//    }
//    
//    /**
//     * Adjust buffer position relative to filechanel which contain data, 
//     * and prepare bytebuffer position and limit for reading or writing action.
//     * 
//     * @param treeIdentifier 
//     * @throws IOException 
//     */
//    protected void adjustBuffer(final int nodeID) throws IOException {
//        rwIndex = beginPosition + (nodeID - 1) * nodeSize;
//        if (rwIndex < currentBufferPosition || (rwIndex + nodeId) > currentBufferPosition + bufferLength) { //-- pense ici
//            // write current data within bytebuffer in channel.
//            byteBuffer.position(0);
//            byteBuffer.limit(writeBufferLimit);
//            long tempPos = inOutChannel.position();
//            inOutChannel.position(currentBufferPosition);
//            int writtenByte = 0;
//            while (writtenByte < writeBufferLimit) {
//                writtenByte += inOutChannel.write(byteBuffer);
//            }
//            inOutChannel.position(tempPos);
//            writeBufferLimit = 0;
//            byteBuffer.clear();
//            final int div = (rwIndex - beginPosition) / bufferLength;
//            currentBufferPosition = div * bufferLength + beginPosition;
//            tempPos = inOutChannel.position();
//            inOutChannel.position(currentBufferPosition);
//            inOutChannel.read(byteBuffer);
//            inOutChannel.position(tempPos);
//        }
//        rwIndex -= currentBufferPosition;
//        byteBuffer.limit(rwIndex + nodeSize);
//        byteBuffer.position(rwIndex);
//    }
//        
//    /**
//     * {@inheritDoc }.
//     */
//    @Override
//    public synchronized void removeNode(final Node candidate) {
//        recycleID.add(candidate.getNodeId());
//    }
//    
//    /**
//     * {@inheritDoc }.
//     */
//    @Override
//    public synchronized void rewind() throws IOException {
//        super.rewind();
//        byteBuffer.position(0);
//        byteBuffer.limit(writeBufferLimit);
////        long temPos = inOutChannel.position();
//        inOutChannel.position(currentBufferPosition);
//        int writtenByte = 0;
//        while (writtenByte < writeBufferLimit) {
//            writtenByte += inOutChannel.write(byteBuffer);
//        }
//        inOutChannel.position(beginPosition);
//        currentBufferPosition = beginPosition;
//        writeBufferLimit = 0;
//    }
//
//    /**
//     * {@inheritDoc }.
//     */
//    @Override
//     public synchronized void flush() throws IOException {
//
//        byteBuffer.position(0);
//        byteBuffer.limit(writeBufferLimit);
//        int writtenByte = 0;
//        inOutChannel.position(currentBufferPosition);
//        while (writtenByte < writeBufferLimit) {
//            writtenByte += inOutChannel.write(byteBuffer);
//        }
//        // write nodeID into head
//        inOutChannel.position(22);
//        
//        final ByteBuffer headBuff = ByteBuffer.allocate(12);
//        headBuff.putInt(nodeId);
//        headBuff.putInt(treeIdentifier);
//        headBuff.putInt(eltNumber);
//        headBuff.flip();
//        inOutChannel.write(headBuff);
//        
////        inOutStream.writeInt(nodeId);
////        inOutStream.writeInt(treeIdentifier);
////        inOutStream.writeInt(eltNumber);
//        adjustBuffer(nodeId);
//     }
//
//
//    /**
//     * {@inheritDoc }.
//     */
//    @Override
//     public synchronized void close() throws IOException {
//        byteBuffer.position(0);
//        byteBuffer.limit(writeBufferLimit);
//        int writtenByte = 0;
//        inOutChannel.position(currentBufferPosition);
//        while (writtenByte < writeBufferLimit) {
//            writtenByte += inOutChannel.write(byteBuffer);
//        }
//        // write nodeID
//        inOutChannel.position(22); 
//        
//        final ByteBuffer headBuff = ByteBuffer.allocate(12);
//        headBuff.putInt(nodeId);
//        headBuff.putInt(treeIdentifier);
//        headBuff.putInt(eltNumber);
//        headBuff.flip();
//        inOutChannel.write(headBuff);
//        
////        inOutStream.writeInt(nodeId);
////        inOutStream.writeInt(treeIdentifier);
////        inOutStream.writeInt(eltNumber);
//        //close
//        inOutChannel.close();
//     }
//
//    /**
//     * {@inheritDoc }
//     * In this {@link TreeAccess} implementation, return {@link FileChannel} opening/close condition. 
//     */
//    @Override
//    public boolean isClose() {
//        return !inOutChannel.isOpen();
//    }

    /**
     * Retrieve the CRS of the input tree.
     * @param treeFile The file containing the tree.
     * @return The {@link org.opengis.referencing.crs.CoordinateReferenceSystem} in which teh tree is expressed.
     * @throws java.lang.IllegalArgumentException if input file is null.
     * @throws java.io.IOException if input file does not exists, or is a directory, or a problem happens at reading.
     * @throws java.lang.ClassNotFoundException If the read object is corrupted,
     */
    public static CoordinateReferenceSystem getTreeCRS(final File treeFile) throws IOException, ClassNotFoundException {
        ArgumentChecks.ensureNonNull("Input tree file", treeFile);
        if (!treeFile.isFile()) {
            throw new IOException("Input file is not a regular file : "+treeFile);
        }
        final RandomAccessFile raf = new RandomAccessFile(treeFile, "r");
        raf.seek(34);
        final int byteTabLength   = raf.readInt();
        final byte[] crsByteArray = new byte[byteTabLength];
        raf.read(crsByteArray, 0, byteTabLength);

        try (final ObjectInputStream crsInputS =
                     new ObjectInputStream(new ByteArrayInputStream(crsByteArray))) {

            return (CoordinateReferenceSystem) crsInputS.readObject();
        }
    }
    
    private static SeekableByteChannel getChannel(File inOutFile) throws FileNotFoundException {
        return new RandomAccessFile(inOutFile, "rw").getChannel();
    }
//    
//    /**
//     * Retrieve the CRS of the input tree.
//     * @param treeFile The file containing the tree.
//     * @return The {@link org.opengis.referencing.crs.CoordinateReferenceSystem} in which teh tree is expressed.
//     * @throws java.lang.IllegalArgumentException if input file is null.
//     * @throws java.io.IOException if input file does not exists, or is a directory, or a problem happens at reading.
//     * @throws java.lang.ClassNotFoundException If the read object is corrupted,
//     */
//    public static CoordinateReferenceSystem getTreeCRS(final File treeFile) throws IOException, ClassNotFoundException {
//        ArgumentChecks.ensureNonNull("Input tree file", treeFile);
//        if (!treeFile.isFile()) {
//            throw new IOException("Input file is not a regular file : "+treeFile);
//        }
//        final RandomAccessFile raf = new RandomAccessFile(treeFile, "r");
//        final FileChannel fChannel = raf.getChannel();
//        final ByteBuffer buff = ByteBuffer.allocate(38);
//        fChannel.read(buff);
//        assert fChannel.position() == 38;
//        buff.flip();
//        buff.position(4);
//        final ByteOrder bO = (buff.get() == 1) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
//        buff.order(bO);
//        buff.position(34);
//        final int byteTabLength   = buff.getInt();
//        final byte[] crsByteArray = new byte[byteTabLength];
//        raf.seek(38);
//        raf.read(crsByteArray, 0, byteTabLength);
//
//        try (final ObjectInputStream crsInputS =
//                     new ObjectInputStream(new ByteArrayInputStream(crsByteArray))) {
//
//            return (CoordinateReferenceSystem) crsInputS.readObject();
//        }
//    }
}
