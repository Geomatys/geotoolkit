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
package org.geotoolkit.index.tree.hilbert;

import java.io.File;
import java.io.IOException;
import org.apache.sis.util.ArraysExt;
import static org.geotoolkit.index.tree.TreeUtilities.intersects;
import org.geotoolkit.index.tree.Node;
import org.geotoolkit.internal.tree.TreeAccessFile;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * {@link TreeAccess} implementation adapted for {@link HilbertRTree}.<br/>
 * Store all {@link Node} architecture use by {@link Tree} on disk drive.
 *
 * @author Remi Marechal (Geomatys).
 */
final class HilbertTreeAccessFile extends TreeAccessFile {

    /**
     * Hilbert Node attributs Number.<br/>
     * parent ID<br/>
     * sibling ID <br/>
     * child ID<br/>
     * current Hilbert Order<br/>
     * children number<br/>
     * data number.
     */
    private static final int INT_NUMBER = 6;
    
    /**
     * Build a {@link Tree} from a already filled {@link File}.
     * 
     * @param input {@code File} which already contains {@link Node} architecture.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber tree version.
     * @throws IOException if problem during read or write Node.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    HilbertTreeAccessFile( final File input, final int magicNumber, final double versionNumber ) throws IOException, ClassNotFoundException {
        super(input, magicNumber, versionNumber, DEFAULT_BUFFER_LENGTH, INT_NUMBER);
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
    HilbertTreeAccessFile( final File input, final int magicNumber, final double versionNumber, final int byteBufferLength) throws IOException, ClassNotFoundException {
        super(input, magicNumber, versionNumber, byteBufferLength, INT_NUMBER);
    }
    
    /**
     * Build and insert {@link Node} architecture in a {@link File}.<br/>
     * If file is not empty, data within it will be overwrite.<br/>
     * If file does not exist a file will be create.<br/>
     * The default length value of ByteBuffer which read and write on hard disk, is 4096 Bytes.
     * 
     * @param outPut {@code File} where {@link Node} architecture which will be write.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param hilbertOrder maximum hilbert order value permit for each tree leaf.
     * @param maxElements element number per cell.
     * @param crs 
     * @throws IOException if problem during read or write Node.
     */
    HilbertTreeAccessFile(final File outPut, final int magicNumber, final double versionNumber, 
            final int maxElements, final int hilbertOrder, final CoordinateReferenceSystem crs) throws IOException {
        super(outPut, magicNumber, versionNumber, maxElements, hilbertOrder, null, crs, DEFAULT_BUFFER_LENGTH, INT_NUMBER);
    }
    
    /**
     * Build and insert {@link Node} architecture in a {@link File}.<br/>
     * If file is not empty, data within it will be overwrite.<br/>
     * If file does not exist a file will be create.<br/>
     * The default length value of ByteBuffer which read and write on hard disk, is 4096 Bytes.
     * 
     * @param outPut {@code File} where {@link Node} architecture which will be write.
     * @param magicNumber {@code Integer} single {@link Tree} code.
     * @param versionNumber version number.
     * @param hilbertOrder maximum hilbert order value permit for each tree leaf.
     * @param crs 
     * @param byteBufferLength length in Byte unit of the buffer which read and write on hard disk.
     * @throws IOException if problem during read or write Node.
     */
    HilbertTreeAccessFile(final File outPut, final int magicNumber, final double versionNumber, 
            final int maxElements, final int hilbertOrder, final CoordinateReferenceSystem crs, final int byteBufferLength) throws IOException {
        super(outPut, magicNumber, versionNumber, maxElements, hilbertOrder, null, crs, byteBufferLength, INT_NUMBER);
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
        byteBuffer.position(byteBuffer.position() + 5);// step properties (1 byte) and step parent ID (int : 4 bytes)
        final int sibling = byteBuffer.getInt();
        final int child   = byteBuffer.getInt();
        byteBuffer.position(byteBuffer.position() + 12);// step hilbertOrder, step child count and step dataCount
        if (sibling != 0) {
            internalSearch(sibling);
        }
        // trouver a ameliorer avec les valeurs de hilbert qui aide en cas de feuille
        if (!ArraysExt.hasNaN(boundary) && intersects(boundary, regionSearch, true)) {
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
    public synchronized Node readNode(final int indexNode) throws IOException {
        adjustBuffer(indexNode);
        final double[] boundary = new double[boundLength];
        for (int i = 0; i < boundLength; i++) {
            boundary[i] = byteBuffer.getDouble();
        }
        final byte properties         = byteBuffer.get();
        final int parentId            = byteBuffer.getInt();
        final int siblingId           = byteBuffer.getInt();
        final int childId             = byteBuffer.getInt();
        final int currentHilbertOrder = byteBuffer.getInt();
        final int childCount          = byteBuffer.getInt();
        final int dataCount           = byteBuffer.getInt();
        final HilbertNode redNode = new HilbertNode(this, indexNode, boundary, properties, parentId, siblingId, childId);
        redNode.setCurrentHilbertOrder(currentHilbertOrder);
        redNode.setChildCount(childCount);
        redNode.setDataCount(dataCount);
        return redNode;
    }
        
    /**
     * {@inheritDoc }.
     */
    @Override
    public synchronized void writeNode(final Node candidate) throws IOException {
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
        byteBuffer.putInt(((HilbertNode)candidate).getCurrentHilbertOrder());
        byteBuffer.putInt(candidate.getChildCount());
        byteBuffer.putInt(((HilbertNode)candidate).getDataCount());
    }
       
    /**
     * {@inheritDoc }.
     */
    @Override
     public synchronized Node createNode(double[] boundary, byte properties, int parentId, int siblingId, int childId) {
         final int currentID = (!recycleID.isEmpty()) ? recycleID.remove(0) : nodeId++;
            return new HilbertNode(this, currentID, (boundary == null) ? nanBound : boundary, properties, parentId, siblingId, childId);
     }
}
