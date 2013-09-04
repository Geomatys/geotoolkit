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

import java.io.IOException;
import org.geotoolkit.index.tree.Node;
import static org.geotoolkit.index.tree.TreeUtilities.intersects;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Store all {@link Node} architecture use by {@link Tree} in memory computer.
 *
 * @author Remi Marechal (Geomatys).
 */
public class TreeAccessMemory extends TreeAccess {

    private int tabNodeLength;
    private Node[] tabNode;

    public TreeAccessMemory(final int maxElements, final SplitCase splitMade, final CoordinateReferenceSystem crs) {
        super();
        this.crs        = crs;
        this.maxElement = maxElements;
        tabNodeLength   = 100;
        tabNode         = new Node[tabNodeLength];
        root            = null;
        this.splitMade  = splitMade;
    }
    
    public TreeAccessMemory(final int maxElements, final CoordinateReferenceSystem crs) {
        this(maxElements, null, crs);
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    protected void internalSearch(int nodeID) throws IOException {
        final Node candidate = readNode(nodeID);
        if (!candidate.isEmpty() && intersects(regionSearch, candidate.getBoundary(), true)) {
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
                    final Node currentChild = readNode(sibl);
                    sibl = currentChild.getSiblingId();
                }
            }
        }
    }
    
    /**
     * {@inheritDoc }.
     */
    @Override
    public synchronized Node readNode(int indexNode) throws IOException {
        return tabNode[indexNode-1];
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void writeNode(Node candidate) throws IOException {
        final int candidateID = candidate.getNodeId();
        if (candidateID > tabNodeLength) {
            final Node[] tfn = tabNode;
            tabNode = new Node[candidateID << 1];
            System.arraycopy(tfn, 0, tabNode, 0, tabNodeLength);
            tabNodeLength = candidateID << 1;
        }
        tabNode[candidateID-1] = candidate;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void removeNode(Node candidate) {
        final int candID = candidate.getNodeId();
        recycleID.add(candID);
        tabNode[candID-1] = null;
    }

    /**
     * {@inheritDoc }
     * 
     * @throws IOException no exception in this implementation.
     */
    @Override
    public synchronized void rewind() throws IOException {
        super.rewind();
        tabNodeLength = 100;
        tabNode = new Node[tabNodeLength];
    }

    /**
     * Do Nothing.
     * @throws IOException 
     */
    @Override
    public void close() throws IOException {
        // nothing
    }
}
