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

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.index.tree.Node2D;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeUtils;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.converter.Classes;

/**
 * Create an appropriate {@code Node2D} to {@code HilbertNode2D}.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class HilbertNode2D extends Node2D {

    /**
     * Create HilbertNode2D.
     * 
     * @param tree pointer on Tree.
     * @param parent pointer on parent Node2D.
     * @param hilbertOrder currently Node2D Hilbert order.
     * @param children sub {@code Node2D}.
     * @param entries {@code List<Shape>} to add in this node.
     * @throws IllegalArgumentException if hilbertOrder < 0.
     */
    public HilbertNode2D(final Tree tree, final Node2D parent, final int hilbertOrder, final List<Node2D> children, final List<Shape> entries) {
        super(tree, parent, children, null);
        ArgumentChecks.ensurePositive("hilbertOrder", hilbertOrder);
        setUserProperty("isleaf", false);
        if (entries != null && !entries.isEmpty()) {
            setUserProperty("isleaf", true);
            setUserProperty("centroids", new ArrayList<Point2D>());
            setUserProperty("cells", new ArrayList<Node2D>());
            Rectangle2D rect = TreeUtils.getEnveloppeMin(entries).getBounds2D();
            HilbertRTree.createBasicHB(this, hilbertOrder, rect);
            for (Shape sh : entries) {
                HilbertRTree.insertNode(this, sh);
            }
        }
    }

    /**
     * {@inheritDoc}. 
     */
    @Override
    public boolean isEmpty() {
        List<Node2D> lC = (List<Node2D>) getUserProperty("cells");
        boolean empty = true;
        if (lC != null && !lC.isEmpty()) {
            for (Node2D hc : lC.toArray(new Node2D[lC.size()])) {
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
    public Rectangle2D getBound() {
        return this.boundary.getBounds2D();
    }

    
    
    /**
     * Set new boundary.
     * 
     * @param rect future boundary.
     */
    public void setBound(final Rectangle2D rect) {
        this.boundary = rect;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void calculateBounds() {
        if ((Boolean) getUserProperty("isleaf")) {

            final List<Shape> lS = new ArrayList<Shape>();
            final List<Node2D> listCells = new ArrayList<Node2D>((List<Node2D>) getUserProperty("cells"));
            for (Node2D nod : listCells) {
                lS.addAll(nod.getEntries());
            }

            if (lS.isEmpty() && this.getParent() == null) {
                return;
            }

            int hO = (Integer) getUserProperty("hilbertOrder");
            if (hO > 0 && lS.size() < getTree().getMaxElements() * Math.pow(4, hO - 1)) {
                hO--;
            }

            HilbertRTree.createBasicHB(this, hO, TreeUtils.getEnveloppeMin(lS).getBounds2D());
            for (Shape sh : lS) {
                HilbertRTree.chooseSubtree(this, sh).getEntries().add(sh);
            }
        } else {
            for (Node2D nod : getChildren()) {
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
            for (Node2D n2d : (List<Node2D>) getUserProperty("cells")) {
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
        final List<Node2D> cup = (List<Node2D>) getUserProperty("cells");
        final Collection col = (cup != null) ? new ArrayList(cup) : new ArrayList();
        col.addAll(getChildren());
        String strparent = (getParent() == null) ? "null" : String.valueOf(getParent().hashCode());
        return Trees.toString(Classes.getShortClassName(this) + " : " + this.hashCode() + " parent : " + strparent + " isleaf : " + ((Boolean) getUserProperty("isleaf")) + "listentries : " + getEntries(), col);
    }
}
