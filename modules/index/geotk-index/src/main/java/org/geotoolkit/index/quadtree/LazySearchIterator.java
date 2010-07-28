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

    private static class Segment{
        private final AbstractNode node;
        int childIndex;

        private Segment(AbstractNode node, int childIndex){
            this.node = node;
            this.childIndex = childIndex;
        }

    }

    private final Envelope bounds;
    private boolean closed;

    //the current path where we are, Integer is the current visited child node index.
    private final List<Segment> path = new ArrayList<Segment>(10);

    //curent visited node
    private AbstractNode current = null;

    public LazySearchIterator(AbstractNode node, Envelope bounds) {
        this.bounds = bounds;
        this.closed = false;

        if(node.intersects(bounds)){
            path.add(new Segment(node, -1));
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
            final Segment segment = path.get(lastIndex);

            if(segment.childIndex == -1){
                //prepare for next node search
                segment.childIndex = 0;

                //we have not yet explore this node ids
                //we use the node shpIds if he has some
                final int nb = segment.node.getNumShapeIds();
                if(nb>0){
                    //we have some ids in this node
                    current = segment.node;
                    return;
                }
            }

            final int nbNodes = segment.node.getNumSubNodes();
            childLoop:
            while(segment.childIndex < nbNodes){
                final AbstractNode child = segment.node.getSubNode(segment.childIndex);

                if(!child.intersects(bounds)){
                    //not in the area we requested
                    segment.childIndex++;
                    continue childLoop;
                }

                path.add(new Segment(segment.node.getSubNode(segment.childIndex), -1));
                
                //prepare next node sarch
                segment.childIndex++;

                //explore the sub node
                continue nodeLoop;
            }
            
            //we have nothing left to explore in this node, go back to the parent
            //to explore next branch
            path.remove(lastIndex);
        }while(current == null);

    }

}
