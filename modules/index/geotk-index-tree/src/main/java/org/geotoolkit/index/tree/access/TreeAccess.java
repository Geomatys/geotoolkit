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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.geotoolkit.index.tree.AbstractTree;
import org.geotoolkit.index.tree.Node;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Mechanic to store Tree architecture.<br/><br/>
 * It exist two differents implementation.<br/>
 * One which write each Tree {@link Node} in a file at place specified by user.<br/>
 * The other store in memory each Node.
 * 
 * @see TreeAccessFile
 * @see TreeAccessMemory
 * @author Remi Marechal (Geomatys).
 */
public abstract class TreeAccess {
   
    /**
     * Default buffer length value use to read and write on hard disk.
     * 
     * @see TreeAccessFile
     */
    protected static final int DEFAULT_BUFFER_LENGTH = 819200;// 4096 
    
    /**
     * CoordinateReferenceSystem attribut use by Tree.
     */
    protected CoordinateReferenceSystem crs;
    
    /**
     * Maximum element number accepted by each Tree leaf.
     */
    protected int maxElement;
    
    /**
     * The most higher Node in Tree.(also called Tree trunk).
     */
    protected Node root;
    
    /**
     * Identifier of each node which will be store in TreeAccess implementation.<br/>
     * Default value is 1, value reserved for root Node.
     */
    protected int nodeId = 1;
    
    /**
     * Table which contain all search result Node identifier.
     */
    protected int[] tabSearch;
    
    /**
     * Search table attributs.
     */
    protected int currentLength;
    protected int currentPosition;
    
    /**
     * boundary of search region.
     */
    protected double[] regionSearch;
    
    /**
     * Store treeIdentifier when user call close method from tree.
     * 
     * @see AbstractTree#close() 
     */
    protected int treeIdentifier;
    
    /**
     * Store eltNumber when user call close method from tree.<br/>
     * eltNumber represent number of data inserted in Tree.
     * 
     * @see AbstractTree#close() 
     */
    protected int eltNumber;
    
    /**
     * Double table fill with {@link Double#NaN} value.<br/>
     * Element use if boundary of Node as a null value like remove last element in tree.<br/>
     * Moreover in Hilbert RTree they exist Node which call cell and within each Hilbert leaf.<br/>
     * This cells may have no boundary its an expected comportement. 
     * 
     * @see TreeAccessFile#writeNode(org.geotoolkit.index.tree.Node) 
     */
    protected double[] nanBound;
    
    /**
     * Store maximum hilbert value reach by each {@link HilbertNode}.
     * 
     * @see HilbertNode.
     */
    protected int hilbertOrder;
    
    /**
     * Store identifier of all removed Node to re-use them. 
     */
    protected List<Integer> recycleID = new LinkedList<Integer>();
    
    /**
     * Create a TreeAccess.
     */
    protected TreeAccess() {
        treeIdentifier  = 1;
    }
    
    /**
     * Find all value stored in Tree which intersect region search.
     * 
     * @param nodeID Node identifier where search begin. Generaly begin at node identifier.
     * @param regionSearch boundary of search region.
     * @return integer table which contain all value stored in Tree which intersect region search.
     * @throws IOException if read or write Exception in {@link TreeAccessFile} implementation.
     */
    public int[] search(int nodeID, double[] regionSearch) throws IOException {
        currentLength     = 100;
        tabSearch         = new int[currentLength];
        currentPosition   = 0;
        this.regionSearch = regionSearch;
        internalSearch(nodeID);
        return Arrays.copyOf(tabSearch, currentPosition);
    }
    
    /**
     * Search method adapted for implementation. 
     * 
     * @param nodeID current Node identifier search
     * @throws IOException if read or write Exception in {@link TreeAccessFile} implementation.
     */
    protected abstract void internalSearch(int nodeID) throws IOException;
    
    /**
     * Read Node at specified Node identifier.
     * 
     * @param indexNode node identifier.
     * @return Node at specified Node identifier.
     * @throws IOException if read Exception in {@link TreeAccessFile} implementation.
     */
    public abstract Node readNode(final int indexNode) throws IOException;
    
    /**
     * Write Node.
     * 
     * @param candidate Node which will be written.
     * @throws IOException if write Exception in {@link TreeAccessFile} implementation.
     */
    public abstract void writeNode(final Node candidate) throws IOException;
    
    /**
     * Remove specified Node.
     * 
     * @param candidate Node which will be remove.
     */
    public abstract void removeNode(final Node candidate);
    
    /**
     * Return CoordinateReferenceSystem use by Tree.
     * 
     * @return CoordinateReferenceSystem use by Tree.
     */
    public CoordinateReferenceSystem getCRS() {
        return crs;
    }
    
    /**
     * Return authorized element number stored in each Node.
     * 
     * @return authorized element number stored in each Node. 
     */
    public int getMaxElementPerCells() {
        return maxElement;
    }
    
    /**
     * Return root Node.
     * 
     * @return root Node.
     */
    public Node getRoot(){
        return root;
    }
    
    /**
     * Affect a new tree Identifier value.
     * Generaly use when caller use close method.
     * 
     * @param treeIdentifier 
     * @see AbstractTree#close() 
     */
    public void setTreeIdentifier(final int treeIdentifier){
        this.treeIdentifier = treeIdentifier;
    }

    /**
     * Return current Tree Identifier value.
     * 
     * @return current Tree Identifier value.
     */
    public int getTreeIdentifier() {
        return treeIdentifier;
    }

    /**
     * Affect new element number value.
     * 
     * @param eltNumber 
     * @see AbstractTree#close() 
     */
    public void setEltNumber(int eltNumber) {
        this.eltNumber = eltNumber;
    }

    /**
     * Return number of element stored in Tree.
     * 
     * @return number of element stored in Tree.
     */
    public int getEltNumber() {
        return eltNumber;
    }

    /**
     * Only use in hilbert RTree.
     * 
     * @return Leaf Hilbert Order.
     * @see #hilbertOrder
     */
    public int getHilbertOrder() {
        return hilbertOrder;
    }
    
    /**
     * Put TreeAccess just like after creating.
     * 
     * @throws IOException if problem during write current {@link ByteBuffer} in {@link TreeAccessFile} implementation.
     * @see TreeAccessFile#rewind() 
     * @see TreeAccessMemory#rewind() 
     */
    public void rewind() throws IOException {
        nodeId = 1;
        recycleID.clear();
    }
    
    /**
     * Close all stream and write current {@link ByteBuffer} use by {@link TreeAccessFile} implementation.<br/>
     * In {@link TreeAccessMemory} implementation method is empty.
     * 
     * @throws IOException if problem during write current {@link ByteBuffer} or 
     * close stream use in {@link TreeAccessFile} implementation.
     */
    public abstract void close() throws IOException;
    
    /**
     * Create a {@link Node} adapted to {@link AbstractBasicRTree} and {@link AbstractStarRTree} Implementations.
     * 
     * @param boundary Node boundary.
     * @param properties Byte which contain Node properties if a Node is Leaf or not. 
     * @param parentId parent identifier Node of current Node.
     * @param siblingId sibling or neighbour Node.
     * @param childId child Node identifier or stored value if Node is a leaf. 
     * @return Node adapted from Tree implementation.
     */
    public Node createNode(double[] boundary, byte properties, int parentId, int siblingId, int childId) {
        final int currentID = (recycleID.isEmpty()) ? nodeId++ : recycleID.remove(0);
        return new Node(this, currentID, boundary, properties, parentId, siblingId, childId);
    }
}
