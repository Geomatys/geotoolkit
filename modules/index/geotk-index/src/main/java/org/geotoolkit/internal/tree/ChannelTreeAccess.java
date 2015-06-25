/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ByteChannel;
import java.nio.channels.Channel;
import java.nio.channels.SeekableByteChannel;
import java.util.Arrays;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.index.tree.basic.SplitCase;
import static org.geotoolkit.internal.tree.TreeUtilities.intersects;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * {@link TreeAccess} implementation.<br/>
 * Store all {@link Node} architecture use by {@link Tree} into a {@link SeekableByteChannel}.
 *
 * @author Remi Marechal (Geomatys).
 */
public abstract strictfp class ChannelTreeAccess extends TreeAccess {

    /**
     * Position in the tree file where CRS description should begin.
     */
    private static final int CRS_POSITION = 34;
    
    /**
     * boundary table value length of each Node.
     */
    protected final int boundLength;
    
    /**
     * Length in Byte unit of a Node in file on hard disk. 
     */
    protected final int nodeSize;
    
    /**
     * {@link FileChannel} position just after write or read file head.<br/>
     * Its also file position of first Node red or written.
     */
    protected final int beginPosition;
    
    /**
     * ByteBuffer attributs use to read and write.
     */
    protected int writeBufferLimit;
    protected long currentBufferPosition;
    protected int rwIndex;
    
    /**
     * {@link ByteBuffer} to read and write Node from file on hard disk.
     */
    protected final ByteBuffer byteBuffer;
    
    /**
     * ByteBuffer Length.
     */
    protected final int bufferLength;
    
    /**
     * {@link Channel} where {@link Node} architecture will be written.
     */
    protected SeekableByteChannel inOutChannel;

    //------------------------- Reading mode -----------------------------------
    /**
     * Build a {@link Tree} from a already filled {@link Channel}, in other words, open in reading mode.<br/><br/>
     * 
     * @param byteChannel {@link SeekableByteChannel} to read already filled object.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @param integerNumberPerNode integer number per Node which will be red/written during Node reading/writing process. 
     * @throws IOException if problem during channel read / write action.
     */
    protected ChannelTreeAccess(final SeekableByteChannel byteChannel, 
            final int magicNumber, final double versionNumber, 
            final int byteBufferLength, final int integerNumberPerNode) 
            throws IOException, ClassNotFoundException {
        
        inOutChannel = byteChannel;
        final ByteBuffer magicOrderBuffer = ByteBuffer.allocate(5);//-- a stipuler en bigendian
//        magicOrderBuffer.order(ByteOrder.BIG_ENDIAN);//-- stand by byte order comportement
        
        inOutChannel.read(magicOrderBuffer);
        magicOrderBuffer.clear();
        
        assert inOutChannel.position() == 5;
        
        /*****************************  read head ******************************/
        //-- read magicNumber
        final int mgNumber = magicOrderBuffer.getInt();
        if (magicNumber != mgNumber) {
            final String createTreeType;
            final String redTreeType;
            switch (magicNumber) {
                case TreeUtilities.BASIC_NUMBER   : createTreeType = " BasicRTree ";break;
                case TreeUtilities.STAR_NUMBER    : createTreeType = " StarRTree ";break;
                case TreeUtilities.HILBERT_NUMBER : createTreeType = " hilbertRTree ";break;
                default : throw new IllegalArgumentException("Unknown tree type for magic number : "+magicNumber);
            };
            switch (mgNumber) {
                case TreeUtilities.BASIC_NUMBER   : redTreeType = " BasicRTree ";break;
                case TreeUtilities.STAR_NUMBER    : redTreeType = " StarRTree ";break;
                case TreeUtilities.HILBERT_NUMBER : redTreeType = " hilbertRTree ";break;
                default : throw new IllegalArgumentException("Unknown tree type for magic number : "+mgNumber);
            };
            final String messageError = "You try to create a "+createTreeType
                    +"RTree from a file which has been filled by a "+redTreeType
                    +"RTree implementation.";
            throw new IllegalArgumentException(messageError);
    }
    
        
        // read ByteOrder
        final boolean fbool = magicOrderBuffer.get() == 1;
        final ByteOrder bO  = (fbool) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;//-- stand by byte order comportement
        
        //-- fin de l'entete dans le bon byteOrder
        final ByteBuffer headBuffer = ByteBuffer.allocate(33);
//        headBuffer.order(bO);//-- stand by byte order comportement
        inOutChannel.read(headBuffer);
        headBuffer.clear();
        assert inOutChannel.position() == 38;
        
        // read version number
        final double vN = headBuffer.getDouble();
        if (vN != versionNumber)
            throw new IllegalArgumentException("Wrong version number. Expected : "+versionNumber+". Version found in tree file : "+vN);
        // read maxElement
        maxElement = headBuffer.getInt();
        // read hilbert Order
        hilbertOrder = headBuffer.getInt();
        // read SplitCase
        final byte sm = headBuffer.get();
        splitMade = ((sm & ((byte)1)) != 0) ? SplitCase.QUADRATIC : SplitCase.LINEAR;
        // read nodeID
        nodeId = headBuffer.getInt();
        if (nodeId == 0)
            throw new IllegalStateException("User has not been invoked tree.close() method after insertions. You should build again RTree.");
        treeIdentifier = headBuffer.getInt();
        // read element number within tree
        eltNumber = headBuffer.getInt();
        // read CRS
        final int byteTabLength   = headBuffer.getInt();
        final byte[] crsByteArray = new byte[byteTabLength];
        
        final ByteBuffer crsBuffer = ByteBuffer.wrap(crsByteArray);
//        crsBuffer.order(bO);//-- stand by byte order comportement
        
        inOutChannel.read(crsBuffer);
        
        assert inOutChannel.position() == 38 + crsByteArray.length;
        try (final ObjectInputStream crsInputS =
                     new ObjectInputStream(new ByteArrayInputStream(crsByteArray))) {
            crs = (CoordinateReferenceSystem) crsInputS.readObject();
        }
        /*****************************  end head ******************************/
        
        this.boundLength = crs.getCoordinateSystem().getDimension() << 1;
        
        //-- nanbound
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
        byteBuffer = ByteBuffer.allocate(bufferLength);
//        byteBuffer.order(bO);//-- stand by byte order comportement
        
        beginPosition = (int) inOutChannel.position();
        currentBufferPosition = beginPosition;
        writeBufferLimit = 0;
        
        // root 
        inOutChannel.position(currentBufferPosition);
        inOutChannel.read(byteBuffer);
        inOutChannel.position(beginPosition);
        root = this.readNode(1);
        if (root.isEmpty()) root = null;
    }
    
    
    //----------------------- writing constructors -----------------------------
    /**
     * Build an empty {@link TreeAccess} and store {@link Node} architecture,
     * in other words, open in writing mode.
     * 
     * @param byteChannel {@link SeekableByteChannel} to read already filled object.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @param maxElements element number per cell.
     * @param hilbertOrder
     * @param splitMade define tree node split made.
     * @param crs
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @param integerNumberPerNode integer number per Node which will be red/written during Node reading/writing process. 
     * @throws IOException 
     */
    protected ChannelTreeAccess(final SeekableByteChannel byteChannel, 
            final int magicNumber,  final double versionNumber, final int maxElements, 
            final int hilbertOrder, final SplitCase splitMade,  final CoordinateReferenceSystem crs, 
            final int byteBufferLength, final int integerNumberPerNode) 
            throws IOException {
        
        this.crs          = crs;
        this.maxElement   = maxElements;
        this.boundLength  = crs.getCoordinateSystem().getDimension() << 1;
        this.hilbertOrder = hilbertOrder;
        this.splitMade    = splitMade;
        
        //nanbound
        nanBound = new double[boundLength];
        Arrays.fill(nanBound, Double.NaN);
        
        /**
         * Node size : boundary weight + Byte properties + n Integers.<br/><br/>
         * 
         * see this.INT_NUMBER attribute.
         */
        nodeSize = (boundLength * Double.SIZE + Integer.SIZE * integerNumberPerNode) / 8 + 1;
        
        final int div = byteBufferLength / nodeSize; // 4096
        this.bufferLength = div * nodeSize;
        
        inOutChannel = byteChannel;
        
        //-- current writing order
        final ByteOrder bO = ByteOrder.nativeOrder();
        
        //-- magic number and byte order
        final ByteBuffer magicOrderBuffer = ByteBuffer.allocate(5);
//        magicOrderBuffer.order(ByteOrder.BIG_ENDIAN);//-- stand by byte order comportement
        // write magicNumber
        magicOrderBuffer.putInt(magicNumber);
        
        // write bytebuffer order
        magicOrderBuffer.put((byte)(bO == ByteOrder.LITTLE_ENDIAN ? 1 : 0));
        magicOrderBuffer.flip();
        inOutChannel.write(magicOrderBuffer);
        assert inOutChannel.position() == 5;
        //-------------------------------------------------------
        
        //--------------------------- head ending ------------------------------
        final ByteBuffer headBuffer = ByteBuffer.allocate(33);
//        headBuffer.order(bO);//-- stand by byte order comportement
        
        /***************************  write head ******************************/
        final ByteArrayOutputStream temp = new ByteArrayOutputStream();
        try (final ObjectOutputStream objOutput = new ObjectOutputStream(temp)) {
            // write version number
            headBuffer.putDouble(versionNumber);
            // write element number per Node
            headBuffer.putInt(maxElements);
            // write hilbert order
            headBuffer.putInt(hilbertOrder);
            // write splitCase
            headBuffer.put((byte) ((splitMade == null || splitMade == SplitCase.LINEAR) ? 0 : 1));
            // write nodeID
            headBuffer.putInt(0);
            // write treeIdentifier
            headBuffer.putInt(0);
            // write element number within tree
            headBuffer.putInt(0);
            // write CRS
            objOutput.writeObject(crs);
            objOutput.flush();
            final byte[] crsByteArray = temp.toByteArray();
            headBuffer.putInt(crsByteArray.length);
            
            //-- write head
            headBuffer.flip();
            inOutChannel.write(headBuffer);
            assert inOutChannel.position() == 38;
            
            final ByteBuffer crsbuff = ByteBuffer.allocate(crsByteArray.length);
//            crsbuff.order(bO);//-- stand by byte order comportement
            crsbuff.put(crsByteArray);
            crsbuff.flip();
            inOutChannel.write(crsbuff);//-- write all the crs array
            
            assert inOutChannel.position() == (crsByteArray.length + 38);
            
    }
        /*****************************  end head ******************************/
        
        
        // ByteBuffer
        byteBuffer = ByteBuffer.allocate((int)bufferLength);
//        byteBuffer.order(bO);//-- stand by byte order comportement
    
        beginPosition         = (int) inOutChannel.position();
        currentBufferPosition = beginPosition;
        writeBufferLimit      = 0;
        
        // root 
        root = null;
    }
    
    
//    /**
//     * Adjust buffer position relative to filechanel which contain data, 
//     * and prepare bytebuffer position and limit for reading or writing action.
//     * 
//     * @param treeIdentifier 
//     * @throws IOException 
//     */
//    protected void adjustBuffer(final int nodeID) throws IOException {
//        rwIndex = beginPosition + (nodeID - 1) * nodeSize;
//        if (rwIndex < currentBufferPosition || ((rwIndex + nodeSize) > (currentBufferPosition + bufferLength))) {
//            // write current data within bytebuffer in channel.
//            byteBuffer.position(0);
//            byteBuffer.limit(writeBufferLimit);
//            inOutChannel.position(currentBufferPosition);
//            int writtenByte = 0;
//            while (writtenByte < writeBufferLimit) {
//                writtenByte += inOutChannel.write(byteBuffer);
//            }
//            writeBufferLimit = 0;
//            byteBuffer.clear();
//            final int div = (rwIndex - beginPosition) / bufferLength;
//            currentBufferPosition = div * bufferLength + beginPosition;
//            inOutChannel.position(currentBufferPosition);
//            inOutChannel.read(byteBuffer);
//        }
//        rwIndex -= currentBufferPosition;
//        byteBuffer.limit(rwIndex + nodeSize);
//        byteBuffer.position(rwIndex);
//    }
    
    /**
     * Adjust buffer position relative to filechanel which contain data, 
     * and prepare bytebuffer position and limit for reading or writing action.
     * 
     * @param treeIdentifier 
     * @throws IOException 
     */
    protected void adjustBuffer(final int nodeID) throws IOException {
        assert inOutChannel.position() == currentBufferPosition;
        rwIndex = beginPosition + (nodeID - 1) * nodeSize;
        if (rwIndex < currentBufferPosition || (rwIndex + nodeSize) > currentBufferPosition + bufferLength) { //-- pense ici
            // write current data within bytebuffer in channel.
            byteBuffer.position(0);
            byteBuffer.limit(writeBufferLimit);
            int writtenByte = 0;
            while (writtenByte < writeBufferLimit) {
                writtenByte += inOutChannel.write(byteBuffer);
            }
            
            //-- define new appropriate window position
            final int div = (rwIndex - beginPosition) / bufferLength;
            currentBufferPosition = div * bufferLength + beginPosition;
            writeBufferLimit = 0;
            
            //-- read current data
            byteBuffer.clear();
            inOutChannel.position(currentBufferPosition);
            inOutChannel.read(byteBuffer);
            byteBuffer.flip();
            
            //-- get back to appropriate position after reading 
            inOutChannel.position(currentBufferPosition);
        }
        rwIndex -= currentBufferPosition;
        byteBuffer.limit(rwIndex + nodeSize);
        byteBuffer.position(rwIndex);
    }
    
    /**
     * 
     * @param nodeID
     * @throws IOException 
     */
    @Override
    protected void internalSearch(int nodeID) throws IOException {
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
     * 
     * @param indexNode
     * @return
     * @throws IOException 
     */
    @Override
    public Node readNode(int indexNode) throws IOException {
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
     * 
     * @param candidate
     * @throws IOException 
     */
    @Override
    public void writeNode(Node candidate) throws IOException {
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
     * {@inheritDoc }.
     */
    @Override
    public synchronized void removeNode(final Node candidate) {
        recycleID.add(candidate.getNodeId());
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public synchronized void rewind() throws IOException {
        super.rewind();
        byteBuffer.position(0);
        byteBuffer.limit(writeBufferLimit);
        inOutChannel.position(currentBufferPosition);
        int writtenByte = 0;
        while (writtenByte < writeBufferLimit) {
            writtenByte += inOutChannel.write(byteBuffer);
        }
        inOutChannel.position(beginPosition);
        currentBufferPosition = beginPosition;
        
        //-- fill buffer
        byteBuffer.clear();
        inOutChannel.read(byteBuffer);
        byteBuffer.flip();
        inOutChannel.position(beginPosition);
        //-- 
        writeBufferLimit = 0;
    }
    
    /**
     * 
     * @throws IOException 
     */
    @Override
    public void close() throws IOException {
        byteBuffer.position(0);
        byteBuffer.limit(writeBufferLimit);
        inOutChannel.position(currentBufferPosition);
        int writtenByte = 0;
        while (writtenByte < writeBufferLimit) {
            writtenByte += inOutChannel.write(byteBuffer);
        }
        
        
        byteBuffer.clear();
        // write nodeID
        inOutChannel.position(22); 
        byteBuffer.putInt(nodeId);
        byteBuffer.putInt(treeIdentifier);
        byteBuffer.putInt(eltNumber);
        byteBuffer.flip();
        inOutChannel.write(byteBuffer);
        
        //close
        inOutChannel.close();
    }

    /**
     * 
     * @throws IOException 
     */
    @Override
    public void flush() throws IOException {
        
        byteBuffer.position(0);
        byteBuffer.limit(writeBufferLimit);
        inOutChannel.position(currentBufferPosition);
        int writtenByte = 0;
        while (writtenByte < writeBufferLimit) {
            writtenByte += inOutChannel.write(byteBuffer);
        }
        
        // write nodeID
        byteBuffer.clear();
        inOutChannel.position(22); 
        byteBuffer.putInt(nodeId);
        byteBuffer.putInt(treeIdentifier);
        byteBuffer.putInt(eltNumber);
        byteBuffer.flip();
        inOutChannel.write(byteBuffer);
        
        //-- fill buffer
        inOutChannel.position(currentBufferPosition);
        byteBuffer.clear();
        inOutChannel.read(byteBuffer);
        byteBuffer.flip();
        inOutChannel.position(currentBufferPosition);
        writeBufferLimit = 0;
        //-- 
        
        adjustBuffer(nodeId);
    }

    /**
     * 
     * @return 
     */
    @Override
    public boolean isClose() {
        return !inOutChannel.isOpen();
    }
}
