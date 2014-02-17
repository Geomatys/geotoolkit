/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.index.quadtree.fs;

import java.io.IOException;

import org.geotoolkit.index.quadtree.AbstractNode;
import org.geotoolkit.index.quadtree.StoreException;

import static org.geotoolkit.index.quadtree.fs.FileSystemIndexStore.*;

/**
 * DOCUMENT ME!
 * 
 * @author Tommaso Nolli
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FileSystemNode extends AbstractNode {

    private FileSystemNode[] nodes = null;

    private final ScrollingBuffer buffer;
    private final int subNodeStartByte;
    private final int subNodesLength;
    private byte numSubNodes;

    FileSystemNode(final double minx, final double miny, final double maxx, final double maxy,
            final ScrollingBuffer buffer, final int startByte, final int subNodesLength) {
        super(minx,miny,maxx,maxy);
        this.buffer = buffer;
        this.subNodeStartByte = startByte;
        this.subNodesLength = subNodesLength;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int getNumSubNodes() {
        return this.numSubNodes;
    }

    /**
     * DOCUMENT ME!
     * @param numSubNodes The numSubNodes to set.
     */
    public void setNumSubNodes(final int numSubNodes) {
        this.numSubNodes = (byte) numSubNodes;
    }

    /**
     * DOCUMENT ME!
     * @return Returns the subNodeStartByte.
     */
    public int getSubNodeStartByte() {
        return this.subNodeStartByte;
    }

    /**
     * DOCUMENT ME!
     * @return Returns the subNodesLength.
     */
    public int getSubNodesLength() {
        return this.subNodesLength;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public AbstractNode getSubNode(final int index) throws StoreException {

        if (nodes == null) {
            //read the subnodes
            try {
                nodes = new FileSystemNode[numSubNodes];
                for(int i = 0;i<nodes.length; i++){

                    final int offset;
                    if(i>0){
                        //skip the previous nodes
                        final FileSystemNode previousNode = (FileSystemNode) nodes[i-1];
                        offset = previousNode.getSubNodeStartByte()+ previousNode.getSubNodesLength();
                    }else{
                        offset = subNodeStartByte;
                    }
                    buffer.goTo(offset);
                    nodes[i] = readNode(buffer);
                }

            } catch (IOException e) {
                throw new StoreException(e);
            }
        }

        return nodes[index];
    }

    @Override
    public void setSubNodes(final AbstractNode ... nodes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
