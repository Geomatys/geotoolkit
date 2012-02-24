/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.index.tree.hilbert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.index.tree.DefaultNode;
import org.geotoolkit.index.tree.DefaultTreeUtils;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.util.converter.Classes;
import org.opengis.geometry.DirectPosition;

/**
 * Create an appropriate Hilbert R-Tree {@code DefaultNode}, which named {@code HilbertNode}.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class HilbertNode extends DefaultNode {

    /**
     * Create HilbertNode.
     *
     * @param tree pointer on Tree.
     * @param parent pointer on parent {@code DefaultNode}.
     * @param hilbertOrder currently {@code DefaultNode} Hilbert order.
     * @param children sub {@code DefaultNode}.
     * @param entries {@code GeneralEnvelope} List to add in this node.
     * @throws IllegalArgumentException if hilbertOrder < 0.
     */
    public HilbertNode(final Tree tree, final DefaultNode parent, final DirectPosition lowerCorner, final DirectPosition upperCorner, final List<DefaultNode> children, final List<GeneralEnvelope> entries) {
        super(tree, parent, lowerCorner, upperCorner, children, null);
        setUserProperty("isleaf", false);
        if (entries != null && !entries.isEmpty()) {
            setUserProperty("isleaf", true);
            setUserProperty("centroids", new ArrayList<DirectPosition>());
            setUserProperty("cells", new ArrayList<DefaultNode>());
            final GeneralEnvelope bound = DefaultTreeUtils.getEnveloppeMin(entries);
            tree.getCalculator().createBasicHL(this, 0, bound);
            for (GeneralEnvelope ent : entries) {
                HilbertRTree.insertNode(this, ent);
            }
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isEmpty() {
        final List<DefaultNode> lC = (List<DefaultNode>) getUserProperty("cells");
        boolean empty = true;
        if (lC != null && !lC.isEmpty()) {
            for (DefaultNode hc : lC.toArray(new DefaultNode[lC.size()])) {
                if (!hc.isEmpty()) {
                    empty = false;
                    break;
                }
            }
        }
        return getChildren().isEmpty() && empty;
    }

    /**
     * @return boundary without re-compute of sub node boundary.
     */
    public GeneralEnvelope getBound() {
        return this.boundary;
    }

    /**
     * Set new boundary.
     *
     * @param bound future boundary.
     */
    public void setBound(final GeneralEnvelope bound) {
        this.boundary = bound;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void calculateBounds() {
        if ((Boolean) getUserProperty("isleaf")) {
            final List<GeneralEnvelope> lS = new ArrayList<GeneralEnvelope>();
            final List<DefaultNode> listCells = new ArrayList<DefaultNode>((List<DefaultNode>) getUserProperty("cells"));
            for (DefaultNode nod : listCells) {
                lS.addAll(nod.getEntries());
            }

            if (lS.isEmpty() && this.getParent() == null) {
                return;
            }

            int hO = (Integer) getUserProperty("hilbertOrder");
            if (hO > 0 && lS.size() < getTree().getMaxElements() * Math.pow(4, hO - 1)) {
                hO--;
            }

            getTree().getCalculator().createBasicHL(this, hO, DefaultTreeUtils.getEnveloppeMin(lS));
            for (GeneralEnvelope sh : lS) {
                HilbertRTree.chooseSubtree(this, sh).getEntries().add(sh);
            }
        } else {
            for (DefaultNode nod : getChildren()) {
                addBound(nod.getBoundary());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLeaf() {
        return (Boolean) getUserProperty("isleaf");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFull() {
        if ((Boolean) getUserProperty("isleaf")) {
            for (DefaultNode n2d : (List<DefaultNode>) getUserProperty("cells")) {
                if (!n2d.isFull()) {
                    return false;
                }
            }
            return true;
        }
        return getChildren().size() >= getTree().getMaxElements();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final List<DefaultNode> cup = (List<DefaultNode>) getUserProperty("cells");
        final Collection col = (cup != null) ? new ArrayList(cup) : new ArrayList();
        col.addAll(getChildren());
        String strparent = (getParent() == null) ? "null" : String.valueOf(getParent().hashCode());
        return Trees.toString(Classes.getShortClassName(this) + " : " + this.hashCode() + " parent : " + strparent + " isleaf : " + ((Boolean) getUserProperty("isleaf")), col);
    }
}
