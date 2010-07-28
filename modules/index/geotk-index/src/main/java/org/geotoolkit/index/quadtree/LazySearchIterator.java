/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.index.quadtree;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Iterator that search the quad tree depth first. And return each node that match
 * the given bounding box.
 * 
 * @author Jesse
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class LazySearchIterator implements Iterator<AbstractNode> {

    private final Envelope bounds;
    private boolean closed;

    //the current path where we are, Integer is the current visited child node index.
    private final List<Entry<AbstractNode,Integer>> path = new ArrayList<Entry<AbstractNode,Integer>>(10);
    private final Envelope buffer = new Envelope();

    //curent visited node
    private AbstractNode current = null;

    public LazySearchIterator(AbstractNode node, Envelope bounds) {
        this.bounds = bounds;
        this.closed = false;

        if(node.getBounds(buffer).intersects(bounds)){
            path.add(new SimpleEntry<AbstractNode, Integer>(node, -1));
        }
        
    }

    @Override
    public boolean hasNext() {
        findNext();
        return current != null;
    }

    @Override
    public AbstractNode next() {
        findNext();
        if (current == null){
            throw new NoSuchElementException("No more elements available");
        }
        
        final AbstractNode temp = current;
        current = null;
        return temp;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void close() throws StoreException {
        this.closed = true;
    }

    private void findNext(){
        if (closed){
            throw new IllegalStateException("Iterator has been closed!");
        }

        if(current != null){
            //we already have the next one
            return;
        }

        try {
            //search the next node that has shpIds
            findNextNode();
        } catch (StoreException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void findNextNode() throws StoreException {
       
        nodeLoop:
        do{
            //nothing left
            if(path.isEmpty()){
                current = null;
                return;
            }

            final int lastIndex = path.size()-1;
            final Entry<AbstractNode,Integer> segment = path.get(lastIndex);
            final AbstractNode candidate = segment.getKey();
            int idNode = segment.getValue();

            if(idNode == -1){
                //prepare for next node search
                idNode = 0;
                segment.setValue(idNode);

                //we have not yet explore this node ids
                //we use the node shpIds if he has some
                final int nb = candidate.getNumShapeIds();
                if(nb>0){
                    //we have some ids in this node
                    current = candidate;
                    return;
                }
            }

            final int nbNodes = candidate.getNumSubNodes();
            childLoop:
            while(idNode < nbNodes){
                final AbstractNode child = candidate.getSubNode(idNode);
                if(bounds != null && !child.getBounds(buffer).intersects(bounds)){
                    //not in the area we requested
                    idNode++;
                    continue childLoop;
                }

                path.add(new SimpleEntry<AbstractNode, Integer>(candidate.getSubNode(idNode),-1));
                
                //prepare next node sarch
                idNode++;
                segment.setValue(idNode);

                //explore the sub node
                continue nodeLoop;
            }
            
            //we have nothing left to explore in this node, go back to the parent
            //to explore next branch
            path.remove(lastIndex);
        }while(current == null);

    }

}
