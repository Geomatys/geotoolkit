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
package org.geotoolkit.index.tree.star;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.index.tree.AbstractTree2D;
import org.geotoolkit.index.tree.Node2D;
import static org.geotoolkit.index.tree.TreeUtils.*;

/**Create R*Tree.
 *
 * @author Rémi Maréchal (Geomatys)
 * @author Johann Sorel  (Geomatys).
 * @version SNAPSHOT
 */
public class StarRTree extends AbstractTree2D {

    /**
     * In accordance with R*Tree properties.
     * To avoid unnecessary split permit to 
     * reinsert some elements just one time.
     */
    private boolean insertAgain = true;

    /**Create R*Tree.
     * 
     * @param maxElements max elements number permit by cells. 
     */
    public StarRTree(int maxElements) {
        super(maxElements);
        setRoot(new Node2D(this));
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void insert(final Shape entry) {
        final Node2D root = getRoot();
        if (root.isEmpty()) {
            root.getEntries().add(entry);
        } else {
            insertNode(root, entry);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final Shape entry) {
        final Node2D root = getRoot();
        if (root != null) {
            deleteNode(root, entry);
        }
    }

    /**
     *{@inheritDoc}.
     */
    @Override
    public void search(final Shape regionSearch, final List<Shape> result) {
        final Node2D root = getRoot();
        if (root != null) {
            searchNode(root, regionSearch, result);
        }
    }

    /**Find appropriate {@code Node2D} to insert {@code Shape}.
     * To define appropriate Node, R*Tree criterion are : 
     *      - require minimum area enlargement to cover shape.
     *      - or put into Node with lesser elements number in case area equals.
     * 
     * @param listSubnode List of {@code Shape} means you must pass a list of {@code Node}.
     * @param shap {@code Shape} to add.
     * @throws IllegalArgumentException if {@code List<Node2D>} listSubnode is null.
     * @throws IllegalArgumentException if {@code Shape} entry is null.
     * @throws IllegalArgumentException if {@code List<Node2D>} listSubnode is empty.
     * @return {@code Node} which will be appropriate to contain shape.
     */
    private static Node2D chooseSubtree(final List<Node2D> listSubnode, final Shape entry) {
        ArgumentChecks.ensureNonNull("chooseSubtree : List<Node2D> lN", listSubnode);
        ArgumentChecks.ensureNonNull("chooseSubtree : Shape entry", entry);
        if (listSubnode.isEmpty()) {
            throw new IllegalArgumentException("impossible to find subtree from empty list");
        }

        if (listSubnode.size() == 1) {
            return listSubnode.get(0);
        }

        for (Node2D no : listSubnode) {
            if (no.getBoundary().contains(entry.getBounds2D())) {
                return no;
            }
        }

        int index = 0;

        final Rectangle2D rtotal = listSubnode.get(0).getParent().getBoundary().getBounds2D();
        double overlaps = rtotal.getWidth() * rtotal.getHeight();

        for (int i = 0; i < listSubnode.size(); i++) {
            double overlapsTemp = 0;
            for (int j = 0; j < listSubnode.size(); j++) {
                if (i != j) {
                    final Node2D ni = listSubnode.get(i);
                    final Node2D nj = listSubnode.get(j);
                    final List<Shape> lB = new ArrayList<Shape>(Arrays.asList(ni.getBoundary(), entry));
                    final Rectangle2D inter = getEnveloppeMin(lB).getBounds2D().createIntersection(nj.getBoundary().getBounds2D());
                    overlapsTemp += inter.getWidth() * inter.getHeight();
                }
            }
            if (overlapsTemp < overlaps) {
                index = i;
                overlaps = overlapsTemp;
            } else if (overlapsTemp == overlaps) {
                final int si = countElements(listSubnode.get(i));
                final int sindex = countElements(listSubnode.get(index));
                if (si < sindex) {
                    index = i;
                    overlaps = overlapsTemp;
                }
            }
        }
        return listSubnode.get(index);
    }

    /**Insert new {@code Entry} in branch and organize branch if it's necessary.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: insertion is in accordance with R*Tree properties.</strong> 
     * </font></blockquote>
     * 
     * @param shape to add.
     * @throws IllegalArgumentException if {@code Node2D} candidate is null.
     * @throws IllegalArgumentException if {@code Shape} entry is null.
     */
    private static void insertNode(final Node2D candidate, final Shape entry) {
        ArgumentChecks.ensureNonNull("insertNode : candidate", candidate);
        ArgumentChecks.ensureNonNull("insertNode : entry", entry);

        if (candidate.isLeaf()) {
            candidate.getEntries().add(entry);
        } else {
            insertNode(chooseSubtree(candidate.getChildren(), entry), entry);
        }

        final StarRTree tree = (StarRTree) candidate.getTree();
        final int maxElmts = tree.getMaxElements();

        if (countElements(candidate) > maxElmts && tree.getIA()) {
            tree.setIA(false);
            final List<Shape> lsh30 = getElementAtMore33PerCent(candidate);
            for (Shape ent : lsh30) {
                deleteNode(candidate, ent);
            }
            for (Shape ent : lsh30) {
                tree.insert(ent);
            }
            tree.setIA(true);
        }

        if (!candidate.isLeaf()) {
            final List<Node2D> lN = candidate.getChildren();
            for (int i = lN.size() - 1; i >= 0; i--) {
                if (countElements(lN.get(i)) > candidate.getTree().getMaxElements()) {
                    final Node2D n = lN.remove(i);
                    List<Node2D> ls = splitNode(n);
                    final Node2D l0 = ls.get(0);
                    final Node2D l1 = ls.get(1);
                    l0.setParent(candidate);
                    l1.setParent(candidate);
                    lN.addAll(ls);
                    if (l0.isLeaf() && l1.isLeaf() && l0.getBoundary().intersects(l1.getBoundary().getBounds2D())) {
                        branchGrafting(l0, l1);
                    }
                }
            }
        }

        if (candidate.getParent() == null) {
            if (countElements(candidate) > candidate.getTree().getMaxElements()) {
                List<Node2D> l = splitNode(candidate);
                final Node2D l0 = l.get(0);
                final Node2D l1 = l.get(1);
                l0.setParent(candidate);
                l1.setParent(candidate);
                if (l0.isLeaf() && l1.isLeaf() && l0.getBoundary().intersects(l1.getBoundary().getBounds2D())) {
                    branchGrafting(l0, l1);
                }
                candidate.getEntries().clear();
                candidate.getChildren().clear();
                candidate.getChildren().addAll(l);
            }
        }
    }

    /**Split a overflow {@code Node2D} in accordance with R-Tree properties.
     * 
     * @param candidate {@code Node2D} to Split
     * @throws IllegalArgumentException if {@code Node2D} candidate is null.
     * @throws IllegalArgumentException if elements number within candidate is lesser 2.
     * @return List<Node2D> which contains two {@code Node2D} (split of candidate).
     */
    private static List<Node2D> splitNode(final Node2D candidate) {
        ArgumentChecks.ensureNonNull("splitNode : candidate", candidate);
        if (countElements(candidate) < 2) {
            throw new IllegalArgumentException("not enought elements within " + candidate + " to split.");
        }
        return new ArrayList<Node2D>(splitAxis(candidate));
    }

    /**Recover lesser 33% largest of {@code Node2D} candidate within it.
     * 
     * @throws IllegalArgumentException if {@code Node2D} candidate is null.
     * @return all Entry within subNodes at more 33% largest of {@code this Node}.
     */
    private static List<Shape> getElementAtMore33PerCent(final Node2D candidate) {
        ArgumentChecks.ensureNonNull("getElementAtMore33PerCent : candidate", candidate);
        final List<Shape> lsh = new ArrayList<Shape>();
        final Rectangle2D rect = candidate.getBoundary().getBounds2D();
        final double rw2 = rect.getWidth();
        final double rh2 = rect.getHeight();

        final double distPermit = Math.hypot(rw2, rh2) / 1.666666666;
        searchNode(candidate, rect, lsh);
        for (int i = lsh.size() - 1; i >= 0; i--) {
            if (getDistanceBetweenTwoBound2D(lsh.get(i).getBounds2D(), rect) < distPermit) {
                lsh.remove(i);
            }
        }
        return lsh;
    }

    /**Get statement from re-insert state.
     * 
     * @return true if it's permit to re-insert else false.
     */
    private boolean getIA() {
        return insertAgain;
    }

    /**Affect statement to permit or not, re-insertion.
     * @param insertAgain
     */
    private void setIA(boolean insertAgain) {
        this.insertAgain = insertAgain;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public Node2D createNode(final Tree tree, final Node2D parent, final List<Node2D> listChildren, final List<Shape> listEntries, double ...coordinates) {
        return new Node2D(tree, parent, listChildren, listEntries, coordinates);
    }
}