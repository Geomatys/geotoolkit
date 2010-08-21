/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.index.CloseableCollection;
import org.geotoolkit.index.Data;
import org.geotoolkit.util.logging.Logging;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Java porting of mapserver quadtree implementation.<br>
 * <br>
 * Note that this implementation is <b>not thread safe</b>, so don't share the
 * same instance across two or more threads.
 * 
 * TODO: example of typical use...
 * 
 * @author Tommaso Nolli
 * @module pending
 */
public class QuadTree<T extends Data> {

    private static final double SPLITRATIO = 0.55d;

    public static final Logger LOGGER = Logging.getLogger("org.geotoolkit.index.quadtree");

    private final DataReader<T> dataReader;
    //open iterators
    private final Set iterators = new HashSet();

    private AbstractNode root;
    private int numShapes;
    private int maxDepth;

    /**
     * Constructor. The maxDepth will be calculated.
     * 
     * @param numShapes
     *                The total number of shapes to index
     * @param maxBounds
     *                The bounds of all geometries to be indexed
     */
    public QuadTree(int numShapes, Envelope maxBounds, DataReader<T> file) {
        this(numShapes, 0, maxBounds, file);
    }

    /**
     * Constructor.
     * 
     * @param numShapes
     *                The total number of shapes to index
     * @param maxDepth
     *                The max depth of the index, must be <= 65535
     * @param maxBounds
     *                The bounds of all geometries to be indexed
     */
    public QuadTree(int numShapes, int maxDepth, Envelope maxBounds, DataReader reader) {
        if (maxDepth > 65535) {
            throw new IllegalArgumentException("maxDepth must be <= 65535 value is " + maxDepth);
        }

        this.numShapes = numShapes;
        this.maxDepth = maxDepth;

        if (maxBounds != null){
            this.root = new Node(maxBounds.getMinX(), maxBounds.getMinY(),
                                 maxBounds.getMaxX(), maxBounds.getMaxY());
        }

        if (maxDepth < 1){
            /*
             * No max depth was defined, try to select a reasonable one that
             * implies approximately 8 shapes per node.
             */
            int numNodes = 1;
            this.maxDepth = 0;

            while (numNodes * 4 < numShapes) {
                this.maxDepth += 1;
                numNodes = numNodes * 2;
            }
        }
        this.dataReader = reader;
    }

    /**
     * Constructor. WARNING: using this constructor, you have to manually set
     * the root
     * 
     * @param numShapes
     *                The total number of shapes to index
     * @param maxDepth
     *                The max depth of the index, must be <= 65535
     */
    public QuadTree(int numShapes, int maxDepth, DataReader file) {
        this(numShapes, maxDepth, null, file);
    }

    /**
     * Inserts a shape record id in the quadtree
     * 
     * @param recno
     *                The record number
     * @param bounds
     *                The bounding box
     */
    public void insert(int recno, Envelope bounds) throws StoreException {
        this.insert(this.root, recno, bounds, this.maxDepth);
    }

    /**
     * Inserts a shape record id in the quadtree
     * 
     * @param node
     * @param recno
     * @param bounds
     * @param md
     * @throws StoreException
     */
    private void insert(AbstractNode node, int recno, Envelope bounds, int md)
            throws StoreException {

        final Envelope buffer = new Envelope();

        if (md > 1 && node.getNumSubNodes() > 0) {
            /*
             * If there are subnodes, then consider whether this object will fit
             * in them.
             */
            AbstractNode subNode = null;
            for (int i = 0; i < node.getNumSubNodes(); i++) {
                subNode = node.getSubNode(i);
                if (subNode.getBounds(buffer).contains(bounds)) {
                    this.insert(subNode, recno, bounds, md - 1);
                    return;
                }
            }
        } else if (md > 1 && node.getNumSubNodes() == 0) {
            /*
             * Otherwise, consider creating four subnodes if could fit into
             * them, and adding to the appropriate subnode.
             */
            final Envelope[] quads = this.splitBounds(node.getBounds(buffer));

            if (quads[0].contains(bounds) || quads[1].contains(bounds)
             || quads[2].contains(bounds) || quads[3].contains(bounds)) {
                node.setSubNodes(new Node[]{
                    new Node(quads[0]),
                    new Node(quads[1]),
                    new Node(quads[2]),
                    new Node(quads[3])
                    });

                // recurse back on this node now that it has subnodes
                this.insert(node, recno, bounds, md);
                return;
            }
        }

        // If none of that worked, just add it to this nodes list.
        node.addShapeId(recno);
    }

    /**
     * 
     * @param bounds
     * @return A List of Integer
     */
    public CloseableCollection<T> search(Envelope bounds) throws StoreException {
        return search(bounds,null);
    }

    /**
     *
     * @param bounds
     * @param minRes : nodes with a small envelope then the given resolution will be ignored.
     * @return A List of Integer
     */
    public CloseableCollection<T> search(Envelope bounds, double[] minRes) throws StoreException {
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "Querying {0}", bounds);
        }

        LazySearchCollection lazySearchCollection;
        try {
            lazySearchCollection = new LazySearchCollection(this, bounds, minRes);
        } catch (RuntimeException e) {
            LOGGER.warning("IOException occurred while reading root");
            return null;
        }
        return lazySearchCollection;
    }


    /**
     * Closes this QuadTree after use...
     * 
     * @throws StoreException
     */
    public void close(Iterator iter) throws StoreException {
        iterators.remove(iter);
        if (iter instanceof SearchIterator){
            ((SearchIterator) iter).close();
        }
    }

    /**
     * 
     */
    public boolean trim() throws StoreException {
        LOGGER.fine("Trimming the tree...");
        return this.trim(this.root);
    }

    /**
     * Trim subtrees, and free subnodes that come back empty.
     * 
     * @param node The node to trim
     * @return true if this node has been trimmed
     */
    private boolean trim(AbstractNode node) throws StoreException {
        final int nbSub = node.getNumSubNodes();

        if(nbSub>0){
            final List<AbstractNode> dummy = new ArrayList<AbstractNode>(nbSub);
            for (int i=0; i<nbSub; i++) {
                final AbstractNode n = node.getSubNode(i);
                if(!this.trim(n)) {
                    dummy.add(n);
                }
            }
            node.setSubNodes(dummy.toArray(new Node[dummy.size()]));
        }

        /*
         * If I have only 1 subnode and no shape records, promote that subnode
         * to my position.
         */
        if (node.getNumSubNodes() == 1 && node.getNumShapeIds() == 0) {
            final AbstractNode subNode = node.getSubNode(0);

            final int nbssn = subNode.getNumSubNodes();
            final AbstractNode[] ssn = new AbstractNode[nbssn];
            for(int i=0;i<nbssn;i++){
                ssn[i] = subNode.getSubNode(i);
            }
            node.setSubNodes(ssn);
            node.setShapesId(subNode.getShapesId());
            node.setEnvelope(subNode.getEnvelope());
        }

        return (node.getNumSubNodes() == 0 && node.getNumShapeIds() == 0);
    }

    /**
     * Splits the specified Envelope
     * 
     * @param in an Envelope to split
     * @return an array of 4 Envelopes
     * +---+---+
     * | 0 | 1 |
     * +---+---+
     * | 2 | 3 |
     * +---+---+
     */
    private Envelope[] splitBounds(Envelope in) {
        final Envelope[] ret = new Envelope[4];
        final double minx = in.getMinX();
        final double miny = in.getMinY();
        final double maxx = in.getMaxX();
        final double maxy = in.getMaxY();
        final double middlex = minx + (in.getWidth()/2);
        final double middley = miny + (in.getHeight()/2);
        ret[0] = new Envelope(minx,     middlex,    middley,    maxy);
        ret[1] = new Envelope(middlex,  maxx,       middley,    maxy);
        ret[2] = new Envelope(minx,     middlex,    miny,       middley);
        ret[3] = new Envelope(middlex,  maxx,       miny,       middley);
        return ret;
    }

    /**
     * @return Returns the maxDepth.
     */
    public int getMaxDepth() {
        return this.maxDepth;
    }

    /**
     * @param maxDepth
     *                The maxDepth to set.
     */
    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * @return Returns the numShapes.
     */
    public int getNumShapes() {
        return this.numShapes;
    }

    /**
     * @param numShapesAbstractNode
     *                The numShapes to set.
     */
    public void setNumShapes(int numShapes) {
        this.numShapes = numShapes;
    }

    /**
     * @return Returns the root.
     */
    public AbstractNode getRoot() {
        return this.root;
    }

    /**
     * @param root
     *                The root to set.
     */
    public void setRoot(AbstractNode root) {
        this.root = root;
    }

    public void close() throws StoreException {
        try {
            dataReader.close();
        } catch (IOException e) {
            throw new StoreException("error closing indexfile", e.getCause());
        }
        if (!iterators.isEmpty()) {
            throw new StoreException("There are still open iterators!!");
        }
    }

    public void registerIterator(Iterator object) {
        iterators.add(object);
    }

    public DataReader getDataReader() {
        return dataReader;
    }
}
