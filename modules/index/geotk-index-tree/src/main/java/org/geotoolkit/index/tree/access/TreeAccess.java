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
import java.util.Arrays;
import org.geotoolkit.index.tree.Node;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author rmarechal
 */
public abstract class TreeAccess {
   
    protected CoordinateReferenceSystem crs;
    protected int maxElement;
    protected Node root;
    
    protected int nodeId = 1;
    //attribut define for research
    protected int[] tabSearch;
    protected int currentLength;
    protected int currentPosition;
    protected double[] regionSearch;
    
    protected int treeIdentifier;
    protected int eltNumber;
    
    // a voir par la suite comment y degager
    protected int hilbertOrder;
    

    protected TreeAccess() {
    }
    
    protected TreeAccess(final int maxElement, final CoordinateReferenceSystem crs){
        this.maxElement = maxElement;
        this.crs        = crs;
        treeIdentifier  = 1;
    }
    
    public int[] search(int nodeID, double[] regionSearch) throws IOException {
        currentLength     = 100;
        tabSearch         = new int[currentLength];
        currentPosition   = 0;
        this.regionSearch = regionSearch;
        internalSearch(nodeID);
        return Arrays.copyOf(tabSearch, currentPosition);
    }
    
    /**
     * 
     * @param nodeID
     * @throws IOException 
     */
    protected abstract void internalSearch(int nodeID) throws IOException;
    
    /**
     * 
     * @param indexNode
     * @return
     * @throws IOException 
     */
    public abstract Node readNode(final int indexNode) throws IOException;
    
    /**
     * 
     * @param candidate
     * @throws IOException 
     */
    public abstract void writeNode(final Node candidate) throws IOException;
    
    /**
     * 
     * @param candidate
     * @throws IOException 
     */
    public abstract void deleteNode(final Node candidate) throws IOException;
    
    public CoordinateReferenceSystem getCRS(){
        return crs;
    }
    
    public int getMaxElementPerCells(){
        return maxElement;
    }
    
    public Node getRoot(){
        return root;
    }
    
    public void setTreeIdentifier(final int treeIdentifier){
        this.treeIdentifier = treeIdentifier;
    }

    public int getTreeIdentifier() {
        return treeIdentifier;
    }

    public void setEltNumber(int eltNumber) {
        this.eltNumber = eltNumber;
    }

    public int getEltNumber() {
        return eltNumber;
    }

    /**
     * only use in hilbert RTree;
     * @return 
     */
    public int getHilbertOrder() {
        return hilbertOrder;
    }
    
    /**
     * 
     * @throws IOException 
     */
    public void rewind() throws IOException {
        nodeId = 1;
    }
    
    /**
     * 
     * @throws IOException 
     */
    public abstract void close() throws IOException;
    
    /**
     * 
     * @param boundary
     * @param parentId
     * @param siblingId
     * @param childId
     * @return 
     */
    public Node createNode(double[] boundary, byte properties, int parentId, int siblingId, int childId) {
        return new Node(this, nodeId++, boundary, properties, parentId, siblingId, childId);
    }
}
