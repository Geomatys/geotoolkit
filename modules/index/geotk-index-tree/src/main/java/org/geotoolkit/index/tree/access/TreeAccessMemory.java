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

import org.geotoolkit.index.tree.access.TreeAccess;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.index.tree.FileNode;
import static org.geotoolkit.index.tree.DefaultTreeUtils.intersects;

/**
 *
 * @author rmarechal
 */
public class TreeAccessMemory extends TreeAccess {

    private int tabNodeLength;
    private FileNode[] tabNode;
    private List<Integer> recycleID;

    public TreeAccessMemory() {
        tabNodeLength = 100;
        tabNode = new FileNode[tabNodeLength];
        recycleID = new ArrayList<Integer>();
    }
    
    
    
    @Override
    protected void internalSearch(int nodeID) throws IOException{ //algorithm a ameliorer
        final FileNode candidate = readNode(nodeID);
        if (intersects(regionSearch, candidate.getBoundary(), true)) {
            if (candidate.isData()) {
                if (currentPosition == currentLength) {
                    currentLength = currentLength << 1;
                    final int[] tabTemp = tabSearch;
                    tabSearch = new int[currentLength];
                    System.arraycopy(tabTemp, 0, tabSearch, 0, currentPosition);
                }
                tabSearch[currentPosition++] = -candidate.getChildId();// childID is value in data
            } else {
                int sibl = candidate.getChildId();
                while (sibl != 0) {
                    internalSearch(sibl);
                    final FileNode currentChild = readNode(sibl);
                    sibl = currentChild.getSiblingId();
                }
            }
        }
    }
    
    @Override
    public FileNode readNode(int indexNode) throws IOException {
        return tabNode[indexNode-1];
    }

    @Override
    public void writeNode(FileNode candidate) throws IOException {
        final int candidateID = candidate.getNodeId();
        if (candidateID > tabNodeLength) {
            final FileNode[] tfn = tabNode;
            tabNode = new FileNode[candidateID << 1];
            System.arraycopy(tfn, 0, tabNode, 0, tabNodeLength);
            tabNodeLength = candidateID << 1;
        }
        tabNode[candidateID-1] = candidate;
    }

    @Override
    public void deleteNode(FileNode candidate) throws IOException {
//        final int candidateNodeId = candidate.getNodeId();
//        for (int i = 0; i < listNode.size(); i++) {
//            final FileNode currentFN = listNode.get(i);
//            if (candidateNodeId == currentFN.getNodeId()) {
//                listNode.remove(i);
//                return;
//            }
//        }
        final int candID = candidate.getNodeId();
        recycleID.add(candID);
        tabNode[candID-1] = null;
    }

    @Override
    public void rewind() throws IOException {
        super.rewind();
        tabNodeLength = 100;
        tabNode = new FileNode[tabNodeLength];
        recycleID.clear();
    }

    @Override
    public void close() throws IOException {
        // nothing
    }
    
    /**
     * 
     * @param boundary
     * @param parentId
     * @param siblingId
     * @param childId
     * @return 
     */
    @Override
    public FileNode createNode(double[] boundary, int parentId, int siblingId, int childId){
        final int currentID = (recycleID.isEmpty()) ? nodeId++ : recycleID.remove(0);
        return new FileNode(this, currentID, boundary, parentId, siblingId, childId);
    }
}
