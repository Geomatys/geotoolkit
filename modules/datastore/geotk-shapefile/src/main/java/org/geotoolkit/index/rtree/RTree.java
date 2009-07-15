/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.index.rtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.filter.visitor.ExtractBoundsFilterVisitor;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.index.Data;
import org.geotoolkit.index.DataDefinition;
import org.geotoolkit.index.Lock;
import org.geotoolkit.index.LockTimeoutException;
import org.geotoolkit.index.TreeException;
import org.geotoolkit.index.UnsupportedFilterException;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Relational index.
 * 
 * @author Tommaso Nolli
 * @source $URL:
 *         http://svn.geotools.org/geotools/trunk/gt/modules/plugin/shapefile/src/main/java/org/geotools/index/rtree/RTree.java $
 */
public class RTree {
    private Logger logger = org.geotoolkit.util.logging.Logging
            .getLogger("org.geotools.index.rtree");
    private PageStore store;

    public RTree(PageStore store) throws TreeException {
        this.store = store;
    }

    /**
     * Gets this index bounding box
     * 
     * @return An Envelope
     * 
     * @throws TreeException
     *                 DOCUMENT ME!
     */
    public Envelope getBounds() throws TreeException {
        this.checkOpen();

        Node root = this.store.getRoot();

        return (root == null) ? null : root.getBounds();
    }

    /**
     * Returns the maxiumal boudns for the provided filter.
     * <p>
     * This method will try and produce a filter for the provided bounds, see
     * ExtractBoundsFilterVisitor.BOUNDS_VISITOR for details of generation.
     * 
     * @param filter
     * @throws TreeException
     * @throws UnsupportedFilterException
     *                 For Filter.EXCLUDES
     */
    public Envelope getBounds(Filter filter) throws TreeException,
            UnsupportedFilterException {
        this.checkOpen();

        Envelope env;
        env = (Envelope) filter.accept(
                ExtractBoundsFilterVisitor.BOUNDS_VISITOR,
                new JTSEnvelope2D());

        if (env == null || env.isNull()) {
            throw new UnsupportedFilterException(
                    "Filter does not contains any Geometry");
        }

        Node root = this.store.getRoot();

        return env.contains(root.getBounds()) ? root.getBounds() : this
                .getBoundsInternal(env, root);
    }

    /*
     * public Envelope getBounds(Envelope env) {
     *  }
     */

    /**
     * DOCUMENT ME!
     * 
     * @param query
     * @param node
     * 
     * @return DOCUMENT ME!
     * 
     * @throws TreeException
     */
    private Envelope getBoundsInternal(final Envelope query, final Node node)
            throws TreeException {
        Envelope result = null;
        Entry entry = null;

        for (int i = 0; i < node.getEntriesCount(); i++) {
            entry = node.getEntry(i);

            if (entry.getBounds().intersects(query)) {
                if (node.isLeaf()) {
                    if (result == null) {
                        result = new Envelope(entry.getBounds());
                    } else {
                        result.expandToInclude(entry.getBounds());
                    }
                } else {
                    this.getBoundsInternal(query, this.store.getNode(entry,
                            node));
                }
            }
        }

        return result;
    }

    public DataDefinition getDataDefinition() {
        return this.store.getDataDefinition();
    }

    /**
     * Performs a search on this <code>RTree</code>
     * 
     * @param query
     *                the query <code>Envelope</code>
     * 
     * @return a <code>Collection</code> of <code>Data</code>
     * 
     * @throws TreeException
     *                 DOCUMENT ME!
     * @throws LockTimeoutException
     *                 DOCUMENT ME!
     */
    public List search(Envelope query) throws TreeException,
            LockTimeoutException {
        // Aquire a read lock
        Lock lock = this.store.getReadLock();
        List ret = null;

        try {
            ret = this.search(query, lock);
        } finally {
            // Release the lock
            this.store.releaseLock(lock);
        }

        return ret;
    }

    /**
     * Performs a search on this <code>RTree</code>
     * 
     * @param filter
     *                a <code>Filter</code>
     * 
     * @return a <code>Collection</code> of <code>Data</code>
     * 
     * @throws TreeException
     * @throws UnsupportedFilterException
     *                 DOCUMENT ME!
     * @throws LockTimeoutException
     *                 DOCUMENT ME!
     */
    public List search(Filter filter) throws TreeException,
            UnsupportedFilterException, LockTimeoutException {
        // Aquire a read lock
        Lock lock = this.store.getReadLock();
        List ret = null;

        try {
            Envelope env = (Envelope) filter.accept(
                    ExtractBoundsFilterVisitor.BOUNDS_VISITOR,
                    new JTSEnvelope2D());

            if (env == null || env.isNull()) {
                throw new UnsupportedFilterException("Not a valid filter");
            }
            ret = this.search(env, lock);
        } finally {
            // Release the lock
            this.store.releaseLock(lock);
        }

        return ret;
    }

    /**
     * Performs a search on the index
     * 
     * @param query
     *                The query <code>Envelope</code>
     * @param lock
     *                A <code>Lock</code> on the index
     * 
     * @return A <code>Collection</code> of <code>Data</code>
     * 
     * @throws TreeException
     * @throws LockTimeoutException
     */
    private List search(Envelope query, Lock lock) throws TreeException,
            LockTimeoutException {
        long start = System.currentTimeMillis();
        this.checkOpen();

        ArrayList matches = new ArrayList();

        Node root = this.store.getRoot();
        this.searchNode(query, root, matches);

        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, matches.size()
                    + " Data objects retrieved in "
                    + (System.currentTimeMillis() - start) + "ms.");
        }

        return matches;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param query
     * @param node
     * @param matches
     * 
     * @throws TreeException
     */
    private void searchNode(final Envelope query, final Node node,
            final ArrayList matches) throws TreeException {
        Entry entry = null;

        for (int i = 0; i < node.getEntriesCount(); i++) {
            entry = node.getEntry(i);

            if (entry.getBounds().intersects(query)) {
                if (node.isLeaf()) {
                    matches.add(entry.getData());
                } else {
                    searchNode(query, this.store.getNode(entry, node), matches);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param bounds
     * @param data
     * 
     * @throws TreeException
     * @throws LockTimeoutException
     */
    public void insert(Envelope bounds, Data data) throws TreeException,
            LockTimeoutException {
        if (!data.isValid()) {
            throw new TreeException("Invalid data supplied!");
        }

        // Aquire a write lock
        Lock lock = this.store.getWriteLock();

        try {
            this.insert(lock, new Entry(bounds, data));
        } finally {
            // Release the lock
            this.store.releaseLock(lock);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param lock
     * @param entry
     * 
     * @throws TreeException
     */
    private void insert(Lock lock, Entry entry) throws TreeException {
        this.checkOpen();

        // Get the right leaf
        Node leaf = this.chooseLeaf(this.store.getRoot(), entry);

        leaf.addEntry(entry);

        if (leaf.getEntriesCount() <= this.store.getMaxNodeEntries()) {
            // If leaf has room add to it
            leaf.save();
            this.adjustTree(leaf, null);
        } else {
            // Overflow...
            Node[] split = this.splitNode(leaf);
            this.adjustTree(split[0], split[1]);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param node
     * @param newEntry
     * 
     * 
     * @throws TreeException
     *                 DOCUMENT ME!
     */
    private Node chooseLeaf(Node node, Entry newEntry) throws TreeException {
        if (node.isLeaf()) {
            return node;
        }

        Collection entries = node.getEntries();

        // Find the best Entry
        Entry best = null;
        Envelope env = null;
        double lastArea = Double.POSITIVE_INFINITY;
        double currentArea = 0d;
        double w = 0;
        double h = 0;
        double nw = 0;
        double nh = 0;

        Entry element = null;

        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            element = (Entry) iter.next();

            currentArea = this.getAreaIncrease(element.getBounds(), newEntry
                    .getBounds());

            if (currentArea < lastArea) {
                lastArea = currentArea;
                best = element;
            } else if ((currentArea == lastArea)
                    && (this.getEntryArea(best) > this.getEntryArea(element))) {
                best = element;
            }
        }

        // Now best is the best Entry
        return this.chooseLeaf(this.store.getNode(best, node), newEntry);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param node
     * 
     * 
     * @throws TreeException
     */
    private Node[] splitNode(Node node) throws TreeException {
        Collection entriesTmp = node.getEntries();

        Entry[] e = (Entry[]) entriesTmp.toArray(new Entry[entriesTmp.size()]);
        Entry[] firsts = null;

        if (this.store.getSplitAlgorithm() == PageStore.SPLIT_QUADRATIC) {
            firsts = this.quadraticPickSeeds(e);
        } else {
            firsts = this.linearPickSeeds(e);
        }

        ArrayList entries = new ArrayList(e.length - 2);

        for (int i = 0; i < e.length; i++) {
            if (!e[i].equals(firsts[0]) && !e[i].equals(firsts[1])) {
                entries.add(e[i]);
            }
        }

        // Clear the node in order to reuse it
        node.clear();

        Node newNode = this.store.getEmptyNode(node.isLeaf());

        Node[] ret = new Node[] { node, newNode };
        ret[0].addEntry(firsts[0]);
        ret[1].addEntry(firsts[1]);

        Entry toAssign = null;
        double d1 = 0d;
        double d2 = 0d;
        int pointer = -1;

        while (true) {
            if (entries.size() == 0) {
                break;
            } else {
                /*
                 * If the remaining elements are not enough for reaching the
                 * minNodeElements of a group or are just right to reach it, add
                 * all entries to the group
                 */
                if ((ret[0].getEntriesCount() + entries.size()) <= this.store
                        .getMinNodeEntries()) {
                    for (int i = 0; i < entries.size(); i++) {
                        ret[0].addEntry((Entry) entries.get(i));
                    }

                    break;
                }

                if ((ret[1].getEntriesCount() + entries.size()) <= this.store
                        .getMinNodeEntries()) {
                    for (int i = 0; i < entries.size(); i++) {
                        ret[1].addEntry((Entry) entries.get(i));
                    }

                    break;
                }
            }

            toAssign = null;

            if (this.store.getSplitAlgorithm() == PageStore.SPLIT_QUADRATIC) {
                toAssign = this.quadraticPickNext(ret, entries);
            } else {
                toAssign = this.linearPickNext(ret, entries);
            }

            d1 = this.getAreaIncrease(ret[0].getBounds(), toAssign.getBounds());
            d2 = this.getAreaIncrease(ret[1].getBounds(), toAssign.getBounds());

            if (d1 < d2) {
                pointer = 0;
            } else if (d1 > d2) {
                pointer = 1;
            } else {
                // If areas increase are the same, smallest wins
                d1 = this.getEnvelopeArea(ret[0].getBounds());
                d2 = this.getEnvelopeArea(ret[1].getBounds());

                if (d1 < d2) {
                    pointer = 0;
                } else if (d1 > d2) {
                    pointer = 1;
                } else {
                    /*
                     * If areas are the same the one with less entries wins
                     */
                    if (ret[0].getEntriesCount() < ret[1].getEntriesCount()) {
                        pointer = 0;
                    } else {
                        pointer = 1;
                    }
                }
            }

            ret[pointer].addEntry(toAssign);

            entries.remove(toAssign);
        }

        ret[0].save();
        ret[1].save();

        return ret;
    }

    /**
     * Returns the 2 new <code>Node</code>s
     * 
     * @param entries
     * 
     */
    private Entry[] quadraticPickSeeds(Entry[] entries) {
        Entry[] ret = new Entry[2];
        Envelope env = null;
        double actualD = 0d;
        double choosedD = Double.NEGATIVE_INFINITY;

        // Foreach pair of entries...
        for (int i = 0; i < (entries.length - 1); i++) {
            env = new Envelope(entries[i].getBounds());

            for (int j = i + 1; j < entries.length; j++) {
                env.expandToInclude(entries[j].getBounds());

                // find the inefficency of groupping together
                actualD = this.getAreaDifference(env, entries[i], entries[j]);

                // Choose the pair with the largest area
                if (actualD > choosedD) {
                    choosedD = actualD;
                    ret[0] = entries[i];
                    ret[1] = entries[j];
                }
            }
        }

        return ret;
    }

    private Entry[] linearPickSeeds(Entry[] entries) {
        // TODO Implement
        return null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param nodes
     * @param entries
     * 
     */
    private Entry quadraticPickNext(Node[] nodes, ArrayList entries) {
        Entry ret = null;
        double[] d = new double[] { 0d, 0d };

        double diff = 0d;
        double maxDiff = Double.NEGATIVE_INFINITY;
        Envelope e = null;

        for (int i = 0; i < entries.size(); i++) {
            e = ((Entry) entries.get(i)).getBounds();
            d[0] = this.getAreaIncrease(nodes[0].getBounds(), e);
            d[1] = this.getAreaIncrease(nodes[1].getBounds(), e);

            diff = Math.abs(d[0] - d[1]);

            if (diff > maxDiff) {
                maxDiff = diff;
                ret = (Entry) entries.get(i);
            }
        }

        return ret;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param nodes
     * @param entries
     * 
     */
    private Entry linearPickNext(Node[] nodes, ArrayList entries) {
        // TODO implement
        return null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param node1
     * @param node2
     * 
     * @throws TreeException
     */
    private void adjustTree(Node node1, Node node2) throws TreeException {
        Node n = node1;
        Node nn = node2;

        Node p = null;
        Entry e = null;

        while (true) {
            if (n.equals(this.store.getRoot())) {
                if (nn != null) {
                    Node newRoot = this.store.getEmptyNode(false);

                    e = this.store.createEntryPointingNode(n);
                    newRoot.addEntry(e);

                    e = this.store.createEntryPointingNode(nn);
                    newRoot.addEntry(e);

                    newRoot.save();
                    this.store.setRoot(newRoot);

                    n.setParent(newRoot);
                    nn.setParent(newRoot);

                    n.save();
                    nn.save();
                } else {
                    // Force root save...
                    this.store.setRoot(n);
                }

                break;
            }

            p = n.getParent();
            e = p.getEntry(n);
            e.setBounds(new Envelope(n.getBounds()));

            if (nn != null) {
                Entry e2 = this.store.createEntryPointingNode(nn);

                p.addEntry(e2);

                if (p.getEntriesCount() > this.store.getMaxNodeEntries()) {
                    Node[] split = this.splitNode(p);
                    n = split[0];
                    nn = split[1];

                    // splitNodes saves the 2 nodes before returning
                } else {
                    // No more to check
                    p.save();

                    nn = null;
                    n = p;
                }
            } else {
                p.save();
                n = p;
            }
        }
    }

    /**
     * Deletes the entry with the specified <code>Envelope</code> as its
     * bounds.<br>
     * If more than one entry exists with the same bounds, then subsequent calls
     * to <code>delete</code> are needed to remove all this elements.
     * 
     * @param env
     *                The <code>Envelope</code>
     * 
     * @throws TreeException
     * @throws LockTimeoutException
     *                 DOCUMENT ME!
     */
    public void delete(Envelope env) throws TreeException, LockTimeoutException {
        this.checkOpen();

        // Aquire a write lock
        Lock lock = this.store.getWriteLock();

        try {
            Node node = this.findLeaf(this.store.getRoot(), env);

            if (node == null) {
                throw new TreeException(
                        "No node found with the supplied envelope: " + env);
            }

            Entry e = null;

            for (int i = 0; i < node.getEntriesCount(); i++) {
                e = node.getEntry(i);

                if (e.getBounds().equals(env)) {
                    this.doDelete(lock, node, e);

                    break;
                }
            }
        } finally {
            // Release the lock
            this.store.releaseLock(lock);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param lock
     * @param node
     * @param entry
     * 
     * @throws TreeException
     */
    private void doDelete(Lock lock, Node node, Entry entry)
            throws TreeException {
        node.removeEntry(entry);
        node.save();

        Collection toRemove = this.condenseTree(node);

        Node root = this.store.getRoot();

        if ((root.getEntriesCount() == 1) && !root.isLeaf()) {
            root = this.store.getNode(root.getEntry(0), root);
            this.store.setRoot(root);
        }

        Collection entries = new ArrayList();
        Iterator iter = toRemove.iterator();

        while (iter.hasNext()) {
            this.free((Node) iter.next(), entries);
        }

        // Reinsert orphaned entries
        Entry e = null;

        for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
            e = (Entry) iterator.next();
            this.insert(lock, e);
        }
    }

    /**
     * Frees a non leaf Node
     * 
     * @param node
     *                The Node to free
     * @param entries
     *                A <code>Collection</code> used to store Node Entry
     * 
     * @throws TreeException
     *                 DOCUMENT ME!
     */
    private void free(final Node node, final Collection entries)
            throws TreeException {
        if (node.isLeaf()) {
            entries.addAll(node.getEntries());
        } else {
            for (int i = 0; i < node.getEntriesCount(); i++) {
                this.free(this.store.getNode(node.getEntry(i), node), entries);
            }
        }

        this.store.free(node);
    }

    /**
     * Closes this index and the associated <code>PageStore</code>
     * 
     * @throws TreeException
     */
    public void close() throws TreeException {
        this.store.close();
        this.store = null;
    }

    /**
     * Checks to see if the index is open
     * 
     * @throws TreeException
     *                 If the index is closed
     */
    private void checkOpen() throws TreeException {
        if (this.store == null) {
            throw new TreeException("The index is closed!");
        }
    }

    /**
     * Finds the first leaf node that contains the element with the supplied
     * envelope
     * 
     * @param node
     *                Starting node
     * @param envelope
     *                <code>Envelope</code> to search
     * 
     * @return The <code>Node</code> that contains the element
     * 
     * @throws TreeException
     */
    private Node findLeaf(Node node, Envelope envelope) throws TreeException {
        Node ret = null;
        Entry entry = null;

        for (int i = 0; i < node.getEntriesCount(); i++) {
            entry = node.getEntry(i);

            if (node.isLeaf()) {
                if (entry.getBounds().equals(envelope)) {
                    ret = node;
                }
            } else {
                if (entry.getBounds().contains(envelope)) {
                    ret = this.findLeaf(this.store.getNode(entry, node),
                            envelope);
                }
            }

            if ((ret != null) && ret.isLeaf()) {
                break;
            }
        }

        return ret;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param node
     * 
     * 
     * @throws TreeException
     */
    private Collection condenseTree(Node node) throws TreeException {
        ArrayList removed = new ArrayList();

        if (node.equals(this.store.getRoot())) {
            return removed;
        }

        Node parentNode = node.getParent();
        Entry parentEntry = parentNode.getEntry(node);

        if (node.getEntriesCount() < this.store.getMinNodeEntries()) {
            removed.add(node);
            parentNode.removeEntry(parentEntry);
        } else {
            parentEntry.setBounds(node.getBounds());
        }

        parentNode.save();

        if (this.store.getRoot().equals(parentNode)) {
            this.store.setRoot(parentNode);
        }

        removed.addAll(this.condenseTree(parentNode));

        return removed;
    }

    /**
     * Gets the area of an <code>Entry</code>
     * 
     * @param e
     *                The <code>Entry</code>
     * 
     * @return the area
     */
    private double getEntryArea(Entry e) {
        return this.getEnvelopeArea(e.getBounds());
    }

    private double getEnvelopeArea(Envelope env) {
        return env.getWidth() * env.getHeight();
    }

    private double getAreaIncrease(Envelope orig, Envelope add) {
        double ret = 0d;

        // The old values
        Envelope env = new Envelope(orig);
        double w = env.getWidth();
        double h = env.getHeight();

        // Expand the envelope
        env.expandToInclude(add);

        // Check area delta
        double nw = env.getWidth();
        double nh = env.getHeight();

        ret += ((nw - w) * nh); // new height
        ret += ((nh - h) * w); // old width

        return ret;
    }

    private double getAreaDifference(Envelope env, Entry e1, Entry e2) {
        return this.getEnvelopeArea(env) - this.getEntryArea(e1)
                - this.getEntryArea(e2);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        Node root = this.store.getRoot();

        String ret = null;

        try {
            ret = this.dump(root, 0);
        } catch (TreeException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private String dump(Node node, int indent) throws TreeException {
        StringBuffer spc = new StringBuffer();

        for (int i = 0; i < indent; i++) {
            spc.append("  ");
        }

        StringBuffer ret = new StringBuffer();
        ret.append(spc);
        ret.append("Node: ").append(node.getBounds());
        ret.append(System.getProperty("line.separator"));

        spc.append("  ");

        for (int i = 0; i < node.getEntriesCount(); i++) {
            ret.append(spc).append(node.getEntry(i)).append(
                    System.getProperty("line.separator"));

            if (!node.isLeaf()) {
                ret
                        .append(this.dump(this.store.getNode(node.getEntry(i),
                                node), indent + 1));
            }
        }

        return ret.toString();
    }
}
