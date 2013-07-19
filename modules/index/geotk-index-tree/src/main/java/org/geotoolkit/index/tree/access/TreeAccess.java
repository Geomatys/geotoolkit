/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.access;

import java.io.IOException;
import java.util.Arrays;
import org.geotoolkit.index.tree.FileNode;
import org.geotoolkit.index.tree.Node;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author rmarechal
 */
public abstract class TreeAccess {
   
    protected CoordinateReferenceSystem crs;
    protected int maxElement;
    protected FileNode root;
    
    protected int nodeId = 1;
    //attribut define for research
    protected int[] tabSearch;
    protected int currentLength;
    protected int currentPosition;
    protected double[] regionSearch;
    
    protected int treeIdentifier;

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
    public abstract FileNode readNode(final int indexNode) throws IOException;
    
    /**
     * 
     * @param candidate
     * @throws IOException 
     */
    public abstract void writeNode(final FileNode candidate) throws IOException;
    
    /**
     * 
     * @param candidate
     * @throws IOException 
     */
    public abstract void deleteNode(final FileNode candidate) throws IOException;
    
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
    public FileNode createNode(double[] boundary, int parentId, int siblingId, int childId){
        return new FileNode(this, nodeId++, boundary, parentId, siblingId, childId);
    }
}
