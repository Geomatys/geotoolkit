/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.geotoolkit.index.Data;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Iterator that search the quad tree depth first. 32000 indices are cached at a
 * time and each time a node is visited the indices are removed from the node so
 * that the memory footprint is kept small. Note that if other iterators operate
 * on the same tree then they can interfere with each other.
 * 
 * @author Jesse
 * @module pending
 */
public class LazySearchIterator implements Iterator<Data> {

    private final DataReader dataReader;
    private final Envelope bounds;
    private boolean closed;

    //the current path where we are, Integer is the current visited child node index.
    private final List<Entry<AbstractNode,Integer>> path = new ArrayList<Entry<AbstractNode,Integer>>(10);
    private final Envelope buffer = new Envelope();

    //curent visited node
    private AbstractNode current = null;
    private int nbShp = 0;
    private int idShp = 0;
    private Data next = null;

    public LazySearchIterator(AbstractNode node, DataReader dataReader, Envelope bounds) {
        this.dataReader = dataReader;
        this.bounds = bounds;
        this.closed = false;
        this.next = null;

        if(node.getBounds(buffer).intersects(bounds)){
            path.add(new SimpleEntry<AbstractNode, Integer>(node, -1));
        }
        
    }

    @Override
    public boolean hasNext() {
        findNext();
        return next != null;
    }

    @Override
    public Data next() {
        findNext();
        if (next == null){
            throw new NoSuchElementException("No more elements available");
        }
        
        final Data temp = next;
        next = null;
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

        if(next != null){
            //we already have the next one
            return;
        }

        if(current == null){
            try {
                //search the next node that has shpIds
                findNextNode();
            } catch (StoreException ex) {
                throw new RuntimeException(ex);
            }

            if(current == null){
                //there are no more datas to read.
                return;
            }else{
                //reset index
                idShp = 0;
            }
        }
        try {
            next = dataReader.create(current.getShapeId(idShp));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        //prepare next id
        idShp++;
        if(idShp == nbShp){
            //no more ids in this node, we will search for a new node next time.
            current = null;
        }

    }

    private void findNextNode() throws StoreException {

        if(current != null){
            return;
        }
       
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
                    nbShp = nb;
                    idShp = 0;
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
