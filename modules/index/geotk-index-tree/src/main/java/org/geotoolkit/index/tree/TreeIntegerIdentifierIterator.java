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
package org.geotoolkit.index.tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.ArraysExt;
import org.geotoolkit.internal.tree.TreeAccess;
import static org.geotoolkit.index.tree.TreeUtilities.*;

/**
 * An Iterator which travel all search result one by one, from Tree architecture stored in a {@link TreeAccess } object.
 *
 * @author Remi Marechal (Geomatys).
 */
class TreeIntegerIdentifierIterator implements TreeIdentifierIterator {

    /**
     * {@link TreeAccess} which contain all saved {@link Tree} {@link Node}.
     */
    private final TreeAccess tAF;
    
    /**
     * Area of search.
     */
    private final double[] regionSearch;
    
    /**
     * List which contain all Node search stack path.
     */
    private final List<Node> path;
    
    /**
     * Current data Node identifier.
     */
    private int dataID;
    
    /**
     * Current tree identifier.
     */
    private int dataValue;

    /**
     * Create an Iterator to travel all treeIdentifier from search results one by one.
     * 
     * @param tAF TreeAccess which contain all saved Node.
     * @param regionSearch area of search
     * @throws StoreIndexException if problem during iterator initialize.(TreeAccess should not read tree root Node).
     */
    TreeIntegerIdentifierIterator(final TreeAccess tAF, final double[] regionSearch) throws StoreIndexException {
        ArgumentChecks.ensureNonNull("TreeAccess tAF", tAF);
        ArgumentChecks.ensureNonNull("regionSearch", regionSearch);
        if (ArraysExt.hasNaN(regionSearch))
            throw new StoreIndexException("regionSearch parameter should not contain NaN value. region search : "+Arrays.toString(regionSearch));
        this.tAF          = tAF;
        this.regionSearch = regionSearch;
        this.path         = new ArrayList<Node>();
        try {
            final Node root = tAF.readNode(1);
            if (root != null && !root.isEmpty()) {
                this.path.add(root);
                // initialization
                getNextData(0);
            } else {
                dataValue = 0;
            }
        } catch (IOException ex) {
            throw new StoreIndexException("problem during first data search file reading.", ex);
        }
    }  

    /**
     * Remove all Nodes from i index position to end of list.
     * 
     * @param i index position of first Node which will be delete.   
     */
    private void removeNodes(final int i) {
        for (int idL = path.size()-1; idL >= i; idL--) {
            path.remove(idL);
        }
    }
    
    /**
     * Find recursively, next data in tree.
     * 
     * @param pathID Node index position in path list.
     * @throws IOException if problem during Node reading from TreeAccess.
     */
    private void getNextData(final int pathID) throws IOException {
       final Node currentNode = path.get(pathID);
        if ((!currentNode.isEmpty()) && intersects(currentNode.getBoundary(), regionSearch, true)) {
            if (currentNode.isData()) {
                if (dataID == currentNode.getNodeId()) {
                    /**
                     * Go to next sibling.
                     */
                    if (currentNode.getSiblingId() != 0) {
                        path.remove(pathID);
                        path.add(tAF.readNode(currentNode.getSiblingId()));
                        getNextData(pathID);
                    } else {
                        /**
                         * end of current Node chained list.
                         */
                        getNextData(pathID-1);
                    }
                } else {
                    dataID    = currentNode.getNodeId();
                    dataValue = -currentNode.getChildId();
                }
            } else {
                // if path + 1 == path.size there isn't child in list. add first child.
                if (pathID + 1 == path.size()) {//pas d'enfant
                    path.add(tAF.readNode(currentNode.getChildId()));
                    getNextData(pathID+1);
                } else if (currentNode.getSiblingId() != 0) {// there is a child in list which mean we are in travel up recurency 
                    /**
                     * There is a child in list which mean we are in travel up recurency and the current Node has already be red.<br/>
                     * We follow its sibling Node.
                     */
                    removeNodes(pathID);
                    path.add(tAF.readNode(currentNode.getSiblingId()));
                    getNextData(pathID);
                } else if (currentNode.getParentId() != 0) {
                    /**
                     * We are at the end of chained Node List we travel up to current Node parent.
                     */
                    getNextData(pathID-1);
                } else {
                    dataValue = 0;
                }
            }
        } else {
            if (currentNode.getSiblingId() != 0) {
                /**
                 * There is a child in list which mean we are in travel up recurency and the current Node has already be red.<br/>
                 * We follow its sibling Node.
                 */
                // remove currentNode.
                removeNodes(pathID);
                // add sibling current Node.
                path.add(tAF.readNode(currentNode.getSiblingId()));
                getNextData(pathID);
            } else if (currentNode.getParentId() != 0) {
                /**
                 * We are at the end of chained Node List we travel up to current Node parent.
                 */
                getNextData(pathID-1);
            } else {
                // finish
                dataValue = 0;
            }
        }
            
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    public int nextInt() throws IOException {
        final int nextInt = dataValue;
        getNextData(path.size() - 1);
        return nextInt;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean hasNext() {
        return dataValue != 0;
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public Integer next() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * 
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
