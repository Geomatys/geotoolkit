/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.access;

import java.io.IOException;
import java.util.Arrays;
import org.geotoolkit.index.tree.FileNode;

/**
 *
 * @author rmarechal
 */
public abstract class TreeAccess {
   
    protected int nodeId = 1;
    //attribut define for research
    protected int[] tabSearch;
    protected int currentLength;
    protected int currentPosition;
    protected double[] regionSearch;

    public TreeAccess() {
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
