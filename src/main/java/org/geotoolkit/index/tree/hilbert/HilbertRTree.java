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

import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.geotoolkit.index.tree.AbstractTree2D;
import org.geotoolkit.index.tree.CoupleNode2D;
import org.geotoolkit.index.tree.Node2D;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeUtils;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.geotoolkit.util.converter.Classes;

/**
 * Create Hilbert RTree.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class HilbertRTree extends AbstractTree2D {

    int hilbertOrder;

    /**
     * Create Hilbert RTree.
     * 
     * @param maxElements max elements number authorized
     * @param hilbertOrder max order value.
     * @throws IllegalArgumentException if maxElements <= 0.
     * @throws IllegalArgumentException if hilbertOrder <= 0. 
     */
    public HilbertRTree(final int maxElements, final int hilbertOrder) {
        super(maxElements);
        ArgumentChecks.ensureStrictlyPositive("impossible to create Hilbert Rtree with order <= 0", hilbertOrder);
        this.hilbertOrder = hilbertOrder;
        setRoot(null);
    }

    /**
     * @return Max Hilbert order value. 
     */
    public int getHilbertOrder() {
        return hilbertOrder;
    }

    /**
     * {@inheritDoc}. 
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + "\n" + getRoot();
    }

    /**
     * {@inheritDoc}
     */
    public void search(final Shape regionSearch, final List<Shape> result) {
        final Node2D root = getRoot();
        if (!root.isEmpty() && root != null) {
            searchHilbertNode(root, regionSearch, result);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final Shape entry) {
        final Node2D root = getRoot();
        if (root == null || root.isEmpty()) {
            setRoot(createNode(this, null, null, UnmodifiableArrayList.wrap(entry)));
        } else {
            insertNode(root, entry);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Shape entry) {
        deleteHilbertNode(getRoot(), entry);
    }

    /**
     * Find all {@code Shape} which intersect regionSearch parameter. 
     * 
     * @param regionSearch area of search.
     * @param result {@code List} where is add search resulting.
     */
    public static void searchHilbertNode(final Node2D candidate, final Shape regionSearch, final List<Shape> result) {
        if (regionSearch.intersects(candidate.getBoundary().getBounds2D()) && candidate instanceof HilbertNode2D) {
            if ((Boolean) candidate.getUserProperty("isleaf")) {
                HilbertNode2D hN2D = (HilbertNode2D) candidate;
                List<Node2D> lN = (List<Node2D>) hN2D.getUserProperty("cells");
                for (Node2D n2d : lN.toArray(new Node2D[lN.size()])) {
                    if (!n2d.isEmpty()) {
                        if (n2d.getBoundary().getBounds2D().intersects(regionSearch.getBounds2D())) {
                            for (Shape sh : n2d.getEntries().toArray(new Shape[n2d.getEntries().size()])) {
                                if (sh.getBounds2D().intersects(regionSearch.getBounds2D())) {
                                    result.add(sh);
                                }
                            }
                        }
                    }
                }
            } else {
                for (Node2D nod : candidate.getChildren()) {
                    searchHilbertNode(nod, regionSearch, result);
                }
            }
        }
    }

    /**Insert entry in {@code Node2D} in accordance with R-Tree properties.
     * 
     * @param candidate {@code Node2D} where user want insert data.
     * @param entry to insert.
     * @throws IllegalArgumentException if candidate or entry are null.
     */
    public static void insertNode(final Node2D candidate, final Shape entry) {

        ArgumentChecks.ensureNonNull("impossible to insert a null entry", entry);

        if (candidate.isFull()) {
            List<Node2D> lSp = splitNode2D((HilbertNode2D) candidate);
            if (lSp != null) {
                final Node2D lsp0 = lSp.get(0);
                final Node2D lsp1 = lSp.get(1);
                ((List<Point2D>) candidate.getUserProperty("centroids")).clear();
                ((List<Node2D>) candidate.getUserProperty("cells")).clear();
                candidate.getChildren().clear();
                candidate.setUserProperty("isleaf", false);
                candidate.setUserProperty("hilbertOrder", 0);
                lsp0.setParent(candidate);
                lsp1.setParent(candidate);
                if (lsp0.isLeaf() && lsp1.isLeaf() && lsp0.getBoundary().intersects(lsp1.getBoundary().getBounds2D())) {
                    branchGrafting(lsp1, lsp1);
                }
                candidate.getChildren().add(lSp.get(0));
                candidate.getChildren().add(lSp.get(1));
            }
        }
        if ((Boolean) candidate.getUserProperty("isleaf")) {
            if ((!candidate.getBoundary().getBounds2D().contains(entry.getBounds2D()))) {//risk bound vs boundary
                List<Shape> lS = new ArrayList<Shape>();
                searchHilbertNode(candidate, candidate.getBoundary(), lS);
                lS.add(entry);
                Rectangle2D enveloppe = TreeUtils.getEnveloppeMin(lS).getBounds2D();
                createBasicHB(candidate, (Integer) candidate.getUserProperty("hilbertOrder"), enveloppe);
                for (Shape sh : lS) {
                    chooseSubtree(candidate, entry).getEntries().add(sh);
                }
            } else {
                chooseSubtree(candidate, entry).getEntries().add(entry);

            }
        } else {
            insertNode(chooseSubtree(candidate, entry), entry);
            ((HilbertNode2D) candidate).setBound(null);
        }
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if this {@code Node} contains lesser two subnode.
     * @throws IllegalArgumentException if this {@code Node} doesn't contains {@code Entry}.
     */
    public static List<Node2D> splitNode2D(final HilbertNode2D candidate) {

        boolean cleaf = (Boolean) candidate.getUserProperty("isleaf");
        int cHO = (Integer) candidate.getUserProperty("hilbertOrder");
        if (candidate.getChildren().size() < 2 && !cleaf) {
            throw new IllegalStateException("impossible to split node with lesser two subnode");
        }
        if (cleaf && (cHO < ((HilbertRTree) candidate.getTree()).getHilbertOrder())) {

            final List<Shape> lS = new ArrayList<Shape>();
            searchHilbertNode(candidate, candidate.getBoundary().getBounds2D(), lS);
            if (lS.isEmpty()) {
                throw new IllegalStateException("impossible to increase Hilbert order of a empty Node");
            }
            createBasicHB(candidate, cHO + 1, TreeUtils.getEnveloppeMin(lS).getBounds2D());
            for (Shape sh : lS) {
                insertNode(candidate, sh);
            }
            return null;
        } else {
            final List<Node2D> lS = splitAxis(candidate);
            return lS;
        }
    }

    /**Compute and define which axis to split {@code this Node}.
     * 
     * @return 1 to split in x axis and 2 to split in y axis.
     */
    public static int defineSplitAxis(final Node2D candidate) {

        final Tree tree = candidate.getTree();
        final int val = tree.getMaxElements();
        boolean isleaf = (Boolean) candidate.getUserProperty("isleaf");
        double perimX = 0;
        double perimY = 0;
        List splitList1 = new ArrayList();
        List splitList2 = new ArrayList();
        List listElmnts;
        CoupleNode2D couplelements;

        if (isleaf) {
            listElmnts = new ArrayList();
            searchHilbertNode(candidate, candidate.getBoundary(), listElmnts);
        } else {
            listElmnts = candidate.getChildren();
        }

        for (int index = 1; index <= 2; index++) {
            if (isleaf) {
                TreeUtils.organize_List2DElements_From(index, null, listElmnts);
            } else {
                TreeUtils.organize_List2DElements_From(index, listElmnts, null);
            }

            for (int i = val; i <= listElmnts.size() - val; i++) {
                for (int j = 0; j < i; j++) {
                    splitList1.add(listElmnts.get(j));
                }
                for (int k = i; k < listElmnts.size(); k++) {
                    splitList2.add(listElmnts.get(k));
                }

                if (isleaf) {
                    couplelements = new CoupleNode2D(tree.createNode(tree, null, null, (List<Shape>) splitList1),
                            tree.createNode(tree, null, null, (List<Shape>) splitList2));
                } else {
                    couplelements = new CoupleNode2D(tree.createNode(tree, null, (List<Node2D>) splitList1, null),
                            tree.createNode(tree, null, (List<Node2D>) splitList2, null));
                }

                switch (index) {
                    case 1: {
                        perimX += couplelements.getPerimeter();
                    }
                    break;

                    case 2: {
                        perimY += couplelements.getPerimeter();
                    }
                    break;
                }

                splitList1.clear();
                splitList2.clear();
            }
        }

        if (perimX <= perimY) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * To choose axis to split :
     *      - case 1 : to choose x axis split.
     *      - case 2 : to choose y axis split.
     * 
     * @param index choose one or 2
     * @return List of two Node which is split of Node passed in parameter.
     * @throws Exception if try to split leaf with only one element.
     */
    public static List<Node2D> splitAxis(final Node2D candidate) {

        final Tree tree = candidate.getTree();
        final boolean leaf = (Boolean) candidate.getUserProperty("isleaf");
        int spaxis = defineSplitAxis(candidate);
        final List splitList1 = new ArrayList();
        final List splitList2 = new ArrayList();
        List listElements;

        if (leaf) {
            listElements = new ArrayList();
            searchHilbertNode(candidate, candidate.getBoundary(), listElements);
            TreeUtils.organize_List2DElements_From(spaxis, null, listElements);
        } else {
            listElements = candidate.getChildren();
            TreeUtils.organize_List2DElements_From(spaxis, listElements, null);
        }

        if (listElements.size() <= 1) {
            throw new IllegalArgumentException("you can't split Leaf with only one elements or lesser");
        }
        
        if (listElements.size() == 2) {
            if (leaf) {
                return UnmodifiableArrayList.wrap(tree.createNode(tree, null, null, UnmodifiableArrayList.wrap((Shape) listElements.get(0))),
                        tree.createNode(tree, null, null, UnmodifiableArrayList.wrap((Shape) listElements.get(1))));
            } else {
                return UnmodifiableArrayList.wrap(tree.createNode(tree, null, UnmodifiableArrayList.wrap((Node2D) listElements.get(0)), null),
                        tree.createNode(tree, null, UnmodifiableArrayList.wrap((Node2D) listElements.get(1)), null));
            }
        }

        CoupleNode2D couNN;

        final List<CoupleNode2D> lSAO = new ArrayList<CoupleNode2D>();
        final List<CoupleNode2D> lSSo = new ArrayList<CoupleNode2D>();
        final int val2 = listElements.size() / 2;
        for (int i = val2; i <= listElements.size() - val2; i++) {
            for (int j = 0; j < i; j++) {
                splitList1.add(listElements.get(j));
            }
            for (int k = i; k < listElements.size(); k++) {
                splitList2.add(listElements.get(k));
            }
            if (leaf) {
                couNN = new CoupleNode2D(tree.createNode(tree, null, null, splitList1), tree.createNode(tree, null, null, splitList2));
            } else {
                couNN = new CoupleNode2D(tree.createNode(tree, null, splitList1, null), tree.createNode(tree, null, splitList2, null));
            }

            if (couNN.intersect()) {
                lSAO.add(couNN);
            } else {
                lSSo.add(couNN);
            }
            splitList1.clear();
            splitList2.clear();
        }
        return lSSo.isEmpty() ? TreeUtils.getMinOverlapsOrPerimeter(lSAO, 0) : TreeUtils.getMinOverlapsOrPerimeter(lSSo, 1);
    }

    /**Find appropriate subnode to insert new entry.
     * Appropriate subnode is chosen to answer HilbertRtree criterion.
     * 
     * @param entry to insert.
     * @throws IllegalArgumentException if this subnodes list is empty.
     * @throws IllegalArgumentException if entry is null.
     * @return subnode chosen.
     */
    public static Node2D chooseSubtree(final Node2D candidate, final Shape entry) {
        ArgumentChecks.ensureNonNull("impossible to choose subtree with entry null", entry);

        if (candidate.isLeaf() && candidate.isFull()) {
            throw new IllegalStateException("impossible to choose subtree in overflow node");
        }

        if ((Boolean) candidate.getUserProperty("isleaf")) {
            if ((Integer) candidate.getUserProperty("hilbertOrder") < 1) {
                return ((List<Node2D>) candidate.getUserProperty("cells")).get(0);
            }
            int index;
            index = getHVOfEntry(candidate, entry);
            for (Node2D nod : (List<Node2D>) candidate.getUserProperty("cells")) {
                if (index <= ((Integer) (nod.getUserProperty("hilbertValue"))) && !nod.isFull()) {
                    return nod;
                }
            }
            final Rectangle2D rect = entry.getBounds2D();
            return ((List<Node2D>) candidate.getUserProperty("cells")).get(findAnotherCell(index, candidate, new Point2D.Double(rect.getCenterX(), rect.getCenterY())));
        } else {
            for (Node2D no : candidate.getChildren()) {
                if (no.getBoundary().getBounds2D().contains(entry.getBounds2D())) {
                    return no;
                }
            }

            int index = 0;
            final Rectangle2D rtotal = candidate.getBoundary().getBounds2D();
            double overlaps = rtotal.getWidth() * rtotal.getHeight();
            List<Node2D> children = candidate.getChildren();
            int childSize = children.size();
            final List<Shape> lS = new ArrayList<Shape>();
            final List<Shape> li = new ArrayList<Shape>();
            final List<Shape> lindex = new ArrayList<Shape>();
            for (int i = 0; i < childSize; i++) {
                double overlapsTemp = 0;
                for (int j = 0; j < childSize; j++) {
                    if (i != j) {
                        lS.clear();
                        final Node2D ni = children.get(i);
                        final Node2D nj = children.get(j);
                        searchHilbertNode(ni, ni.getBoundary(), lS);
                        lS.add(entry);
                        final Rectangle2D lBound = TreeUtils.getEnveloppeMin(lS).getBounds2D();
                        final Rectangle2D njBound = nj.getBoundary().getBounds2D();
                        final Rectangle2D inter = lBound.createIntersection(njBound);
                        overlapsTemp += inter.getWidth() * inter.getHeight();
                    }
                }
                if (overlapsTemp < overlaps) {
                    index = i;
                    overlaps = overlapsTemp;
                } else if (overlapsTemp == overlaps) {
                    li.clear();
                    lindex.clear();
                    final Node2D ni = children.get(i);
                    final Node2D nindex = children.get(index);
                    searchHilbertNode(ni, ni.getBoundary().getBounds2D(), li);
                    searchHilbertNode(nindex, nindex.getBoundary().getBounds2D(), lindex);
                    int si = li.size();
                    int sindex = lindex.size();
                    if (si < sindex) {
                        index = i;
                        overlaps = overlapsTemp;
                    }
                }
            }
            return children.get(index);
        }
    }

    /**To answer Hilbert criterion and to avoid call split method,  in some case 
     * we constrain tree leaf to choose another cell to insert Entry. 
     * 
     * @param index of subnode which is normally chosen.
     * @param ptEntryCentroid subnode chosen centroid.
     * @throws IllegalArgumentException if method call by none leaf {@code Node}.
     * @throws IllegalArgumentException if index is out of required limit.
     * @throws IllegalStateException if no another cell is find.
     * @return index of another subnode.
     */
    private static int findAnotherCell(int index, final Node2D candidate, final Point2D ptEntryCentroid) {
        ArgumentChecks.ensureNonNull("impossible to find another leaf with ptCentroid null", ptEntryCentroid);
        if (!(Boolean) candidate.getUserProperty("isleaf")) {
            throw new IllegalArgumentException("impossible to find another leaf in Node which isn't LEAF tree");
        }
        ArgumentChecks.ensureBetween("index to find another leaf is out of required limit",
                0, (int) Math.pow(2, ((Integer) ((HilbertNode2D) candidate).getUserProperty("hilbertOrder") * 2)), index);
        List<Node2D> listCells = (List<Node2D>) candidate.getUserProperty("cells");
        int siz = listCells.size();
        boolean oneTime = false;
        int indexTemp1 = index;
        for (int i = index; i < siz; i++) {
            if (!listCells.get(i).isFull()) {
                indexTemp1 = i;
                break;
            }
            if (i == siz - 1) {
                if (oneTime) {
                    throw new IllegalStateException("will be able to split");
                }
                oneTime = true;
                i = -1;
            }
        }
        return indexTemp1;
    }

    /**Travel {@code Tree}, find {@code Entry} if it exist and delete it.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: Moreover {@code Tree} is condensate after a deletion to stay conform about R-Tree properties.</strong> 
     * </font></blockquote>
     * 
     * @param candidate {@code Node2D}  where to delete.
     * @param entry {@code Shape} to delete.
     * @throws IllegalArgumentException if candidate or entry is null.
     * @return true if entry is find and deleted else false.
     */
    private static void deleteHilbertNode(final Node2D candidate, final Shape entry) {
        ArgumentChecks.ensureNonNull("deleteHilbertNode Node2D candidate : ", candidate);
        ArgumentChecks.ensureNonNull("deleteHilbertNode Shape entry : ", entry);
        if (candidate.getBoundary().intersects(entry.getBounds2D())) {
            if ((Boolean) candidate.getUserProperty("isleaf")) {
                boolean removed = false;
                final List<Node2D> lN = (List<Node2D>) candidate.getUserProperty("cells");
                for (Node2D nod : lN) {
                    if (nod.getEntries().remove(entry)) {
                        removed = true;
                    }
                }
                if (removed) {
                    ((HilbertNode2D) candidate).setBound(null);
                    trim(candidate);
                }
            } else {
                for (Node2D nod : candidate.getChildren().toArray(new Node2D[candidate.getChildren().size()])) {
                    deleteHilbertNode(nod, entry);
                }
            }
        }
    }

    /**Method which permit to condense R-Tree.
     * Condense made begin by leaf and travel up to tree trunk.
     * 
     * @param candidate {@code Node2D} to begin condense.
     */
    public static void trim(final Node2D candidate) {

        if (!(Boolean) candidate.getUserProperty("isleaf")) {
            final List<Node2D> children = candidate.getChildren();
            for (int i = children.size() - 1; i >= 0; i--) {
                final Node2D child = children.get(i);
                if (child.isEmpty()) {
                    children.remove(i);
                } else if (child.getChildren().size() == 1 && !child.isLeaf()) {
                    children.remove(i);
                    for (Node2D n2d : child.getChildren()) {
                        n2d.setParent(candidate);
                    }
                    children.addAll(child.getChildren());
                }
            }

            final HilbertRTree tree = (HilbertRTree) candidate.getTree();
            final List<Shape> lS = new ArrayList<Shape>();
            searchHilbertNode(candidate, candidate.getBoundary(), lS);
            if (lS.size() <= tree.getMaxElements() * Math.pow(2, tree.getHilbertOrder() * 2) && !lS.isEmpty()) {
                createBasicHB(candidate, tree.getHilbertOrder(), TreeUtils.getEnveloppeMin(lS).getBounds2D());
                ((HilbertNode2D) candidate).setUserProperty("isleaf", true);
                for (Shape sh : lS) {
                    Node2D n = chooseSubtree(candidate, sh);
                    n.getEntries().add(sh);
                }
            }
        }

        if (candidate.getParent() != null) {
            trim(candidate.getParent());
        }
    }

    /**Exchange some entry(ies) between two nodes in aim to find best form with lesser overlaps.
     * Also branchGrafting will be able to avoid splitting node.
     * 
     * @param n1 Node2D
     * @param n2 Node2D
     * @throws IllegalArgumentException if n1 or n2 are null.
     * @throws IllegalArgumentException if n1 and n2 have different "parent".
     * @throws IllegalArgumentException if n1 or n2 are not tree leaf.
     * @throws IllegalArgumentException if n1 or n2, and their subnodes, don't contains some {@code Entry}.
     */
    public static void branchGrafting(final Node2D n1, final Node2D n2) {

        ArgumentChecks.ensureNonNull("Node n1 null", n1);
        ArgumentChecks.ensureNonNull("Node n2 null", n2);

        if (!(n1.isLeaf() && n2.isLeaf())) {
            throw new IllegalArgumentException("you can not exchange data with not leaf nodes.");
        }

        final Node2D theFather = n1.getParent();

        final int hp1 = theFather.hashCode();
        final int hp2 = n2.getParent().hashCode();

        if (hp1 != hp2) {
            throw new IllegalArgumentException("you can not exchange data with nodes which don't own same parent");
        }

        final Rectangle2D boundN1 = n1.getBoundary().getBounds2D();
        final Rectangle2D boundN2 = n2.getBoundary().getBounds2D();

        if (boundN1.intersects(boundN2)) {

            final List<Shape> lEOverlaps = new ArrayList<Shape>();
            searchHilbertNode(theFather, boundN1.createIntersection(boundN2), lEOverlaps);
            if (!lEOverlaps.isEmpty()) {
                for (Shape sh : lEOverlaps) {
                    deleteHilbertNode(n1, sh);
                    deleteHilbertNode(n2, sh);
                }
                int indice;
                List<Shape> n1Entries = new ArrayList<Shape>();
                List<Shape> n2Entries = new ArrayList<Shape>();
                for (Node2D n2d1 : (List<Node2D>) n1.getUserProperty("cells")) {
                    for (Shape sh : n2d1.getEntries()) {
                        n1Entries.add(sh);
                    }
                }
                for (Node2D n2d2 : (List<Node2D>) n2.getUserProperty("cells")) {
                    for (Shape sh : n2d2.getEntries()) {
                        n2Entries.add(sh);
                    }
                }
                if (n1Entries.isEmpty() || n2Entries.isEmpty()) {
                    indice = -1;
                } else {
                    indice = TreeUtils.findAppropriateSplit(n1Entries, n2Entries, lEOverlaps);
                }
                if (!boundN1.contains(boundN2) && !boundN2.contains(boundN1) && indice != -1) {

                    for (int i = 0; i < indice; i++) {
                        insertNode(n1, lEOverlaps.get(i));
                    }

                    for (int i = indice, s = lEOverlaps.size(); i < s; i++) {
                        insertNode(n2, lEOverlaps.get(i));
                    }
                } else {

                    final List<Shape> lS = new ArrayList<Shape>(n1Entries);
                    lS.addAll(n2Entries);
                    lS.addAll(lEOverlaps);

                    final int s = lS.size();
                    final int smin = s / 3;
                    final Rectangle2D rectGlob = TreeUtils.getEnveloppeMin(lS).getBounds2D();
                    final double rGW = rectGlob.getWidth();
                    final double rGH = rectGlob.getHeight();
                    final int index = (rGW < rGH) ? 2 : 1;
                    TreeUtils.organize_List2DElements_From(index, null, n2Entries);
                    int indexing = s / 2;
                    double overlapsTemp = rGW * rGH;
                    final Rectangle2D rTemp1 = new Rectangle2D.Double();
                    final Rectangle2D rTemp2 = new Rectangle2D.Double();
                    final List<Shape> lTn1 = new ArrayList<Shape>();
                    final List<Shape> lTn2 = new ArrayList<Shape>();

                    for (int i = smin; i < s - smin; i++) {
                        final List<Shape> testn1 = new ArrayList<Shape>();
                        final List<Shape> testn2 = new ArrayList<Shape>();
                        for (int j = 0; j < i; j++) {
                            testn1.add(lS.get(j));
                        }
                        for (int k = i; k < s; k++) {
                            testn2.add(lS.get(k));
                        }
                        final Rectangle2D r1 = TreeUtils.getEnveloppeMin(testn1).getBounds2D();
                        final Rectangle2D r2 = TreeUtils.getEnveloppeMin(testn2).getBounds2D();
                        final Rectangle2D overlaps = r1.createIntersection(r2);
                        final double over = overlaps.getWidth() * overlaps.getHeight();
                        int indexingTemp = Math.abs(s / 2 - i);
                        if (over <= overlapsTemp && indexingTemp <= indexing) {
                            lTn1.clear();
                            lTn2.clear();
                            indexing = indexingTemp;
                            overlapsTemp = over;
                            rTemp1.setRect(r1);
                            rTemp2.setRect(r2);
                            lTn1.addAll(testn1);
                            lTn2.addAll(testn2);
                        }
                    }
                    for (Node2D n2d1 : (List<Node2D>) n1.getUserProperty("cells")) {
                        n2d1.getEntries().clear();
                    }
                    for (Node2D n2d2 : (List<Node2D>) n2.getUserProperty("cells")) {
                        n2d2.getEntries().clear();
                    }
                    for (Shape ent : lTn1) {
                        insertNode(n1, ent);
                    }
                    for (Shape ent : lTn2) {
                        insertNode(n2, ent);
                    }
                }
            }
        }
    }

    /**Create a conform Hilbert leaf and define Hilbert curve in function of {@code indice}.
     * @throws IllegalArgumentException if indice < 0.
     * @param indice Hilber order ask by user. 
     */
    public static void createBasicHB(final Node2D candidate, final int indice, final Rectangle2D bound) {
        ArgumentChecks.ensurePositive("impossible to create Hilbert Curve with negative indice", indice);
        candidate.getChildren().clear();

        List<Point2D> listOfCentroidChild = (List<Point2D>) candidate.getUserProperty("centroids");
        listOfCentroidChild.clear();
        candidate.setUserProperty("hilbertOrder", indice);
        ((HilbertNode2D) candidate).setBound(bound);
        List<Node2D> listN = (List<Node2D>) candidate.getUserProperty("cells");
        listN.clear();
        if (indice > 0) {
            final double w = bound.getWidth() / 4;
            final double h = bound.getHeight() / 4;

            final double minx = bound.getMinX() + w;
            final double maxx = bound.getMaxX() - w;
            final double miny = bound.getMinY() + h;
            final double maxy = bound.getMaxY() - h;
            listOfCentroidChild.add(new Point2D.Double(minx, miny));
            listOfCentroidChild.add(new Point2D.Double(minx, maxy));
            listOfCentroidChild.add(new Point2D.Double(maxx, maxy));
            listOfCentroidChild.add(new Point2D.Double(maxx, miny));
            if (indice > 1) {
                for (int i = 1; i < indice; i++) {
                    createHB(candidate);
                }
            }

            int dim = (int) Math.pow(2, (Integer) candidate.getUserProperty("hilbertOrder"));
            int[][] tabHV = new int[dim][dim];

            for (int i = 0, s = listOfCentroidChild.size(); i < s; i++) {
                Point2D ptCTemp = listOfCentroidChild.get(i);
                int[] tabTemp = getHilbCoord(ptCTemp, bound, indice);
                tabHV[tabTemp[0]][tabTemp[1]] = i;
                listN.add(createCell(candidate.getTree(), candidate, ptCTemp, i, null));
            }
            candidate.setUserProperty("tabHV", tabHV);
        } else {
            listOfCentroidChild.add(new Point2D.Double(bound.getCenterX(), bound.getCenterY()));
            listN.add(createCell(candidate.getTree(), candidate, listOfCentroidChild.get(0), 0, null));
        }

    }

    /**Create subnode(s) centroid(s).
     * These centroids define Hilbert curve.
     * Increase the Hilbert order of {@code HilbertLeaf} passed in parameter by one unity.
     * 
     * @param hl HilbertLeaf to increase Hilbert order.
     * @throws IllegalArgumentException if parameter "hl" is null.
     * @throws IllegalArgumentException if parameter hl Hilbert order is larger than them Hilbert RTree.
     */
    private static void createHB(final Node2D hl) {

        ArgumentChecks.ensureNonNull("impossible to increase hilbert order", hl);
        if ((Integer) hl.getUserProperty("hilbertOrder") > ((HilbertRTree) hl.getTree()).getHilbertOrder()) {
            throw new IllegalArgumentException("hilbert order is larger than hilbertRTree hilbert order");
        }

        final List<Point2D> listOfCentroidChild = (List<Point2D>) hl.getUserProperty("centroids");
        List<Point2D> lPTemp2 = new ArrayList<Point2D>(listOfCentroidChild);
        final Rectangle2D bound = ((HilbertNode2D) hl).getBound();
        final Point2D centroid = new Point2D.Double(bound.getCenterX(), bound.getCenterY());
        final double centreX = centroid.getX();
        final double centreY = centroid.getY();
        final double quartWidth = bound.getWidth() / 4;
        final double quartHeight = bound.getHeight() / 4;

        listOfCentroidChild.clear();
        final AffineTransform mt1 = new AffineTransform(1, 0, 0, 1, -centreX, -centreY);
        final AffineTransform rot1 = new AffineTransform();
        final AffineTransform mt21 = new AffineTransform(1 / quartWidth, 0, 0, 1 / quartHeight, 0, 0);
        final AffineTransform mt22 = new AffineTransform(quartWidth, 0, 0, quartHeight, 0, 0);
        final AffineTransform mt2 = new AffineTransform();
        final AffineTransform mt3 = new AffineTransform(1 / 2.0, 0, 0, 1 / 2.0, 0, 0);

        for (int i = 0; i < 4; i++) {

            if (i == 0) {
                rot1.setToRotation(-Math.PI / 2);
                mt2.setTransform(1, 0, 0, 1, centreX - quartWidth, centreY - quartHeight);
                mt2.concatenate(mt3);
                mt2.concatenate(mt22);
                mt2.concatenate(rot1);
                mt2.concatenate(mt21);
                mt2.concatenate(mt1);
                Collections.reverse(lPTemp2);
            } else if (i == 1) {

                mt2.setTransform(1, 0, 0, 1, centreX - quartWidth, centreY + quartHeight);
                mt2.concatenate(mt3);
                mt2.concatenate(mt1);
                Collections.reverse(lPTemp2);

            } else if (i == 2) {
                mt2.setTransform(1, 0, 0, 1, centreX + quartWidth, centreY + quartHeight);
                mt2.concatenate(mt3);
                mt2.concatenate(mt1);
            } else if (i == 3) {
                Collections.reverse(lPTemp2);
                rot1.setToRotation(Math.PI / 2);
                mt2.setTransform(1, 0, 0, 1, centreX + quartWidth, centreY - quartHeight);
                mt2.concatenate(mt3);
                mt2.concatenate(mt22);
                mt2.concatenate(rot1);
                mt2.concatenate(mt21);
                mt2.concatenate(mt1);
            }

            for (Point2D pt : lPTemp2) {
                listOfCentroidChild.add(mt2.transform(pt, null));
            }
        }
    }

    /**Find {@code Point2D} Hilbert coordinate from this Node.
     * 
     * @param pt {@code Point2D} 
     * @throws IllegalArgumentException if parameter "pt" is out of this node boundary.
     * @throws IllegalArgumentException if parameter pt is null.
     * @return int[] table of length 2 which contains two coordinate.
     * @see #getHVOfEntry(org.geotoolkit.utilsRTree.Entry)
     */
    private static int[] getHilbCoord(final Point2D pt, final Rectangle2D rect, final int hilbertOrder) {
        ArgumentChecks.ensureNonNull("point2D pt : ", pt);
        if (!rect.contains(pt)) {
            throw new IllegalArgumentException("Point is out of this node boundary");
        }
        final double divX = rect.getWidth() / (Math.pow(2, hilbertOrder));
        final double divY = rect.getHeight() / (Math.pow(2, hilbertOrder));
        final int hx = (int) (Math.abs(pt.getX() - rect.getMinX()) / divX);
        final int hy = (int) (Math.abs(pt.getY() - rect.getMinY()) / divY);
        return new int[]{hx, hy};
    }

    /**Find Hilbert order of an entry from this HilbertLeaf.
     * 
     * @param entry where we looking for her Hilbert order.
     * @throws IllegalArgumentException if parameter "entry" is out of this node boundary.
     * @throws IllegalArgumentException if entry is null.
     * @return integer the entry Hilbert order.
     */
    public static int getHVOfEntry(final Node2D candidate, final Shape entry) {
        ArgumentChecks.ensureNonNull("impossible to define Hilbert coordinate with null entry", entry);
        final Rectangle2D recEnt = entry.getBounds2D();
        final Point2D ptCE = new Point2D.Double(recEnt.getCenterX(), recEnt.getCenterY());
        if (!((HilbertNode2D) candidate).getBound().contains(ptCE)) {
            throw new IllegalArgumentException("entry is out of this node boundary");
        }
        int[] hCoord = getHilbCoord(ptCE, ((HilbertNode2D) candidate).getBound(), (Integer) candidate.getUserProperty("hilbertOrder"));
        return ((int[][]) candidate.getUserProperty("tabHV"))[hCoord[0]][hCoord[1]];
    }

    private static Node2D createCell(final Tree tree, final Node2D parent, final Point2D centroid, final int hilbertValue, final List<Shape> entries) {
        final Node2D nod2d = new Node2D(tree, parent, null, entries);
        nod2d.setUserProperty("hilbertValue", hilbertValue);
        nod2d.setUserProperty("centroid", centroid);
        return nod2d;
    }

    /**
     * {@inheritDoc}
     */
    public Node2D createNode(final Tree tree, final Node2D parent, final List<Node2D> listChildren, final List<Shape> listEntries) {
        return new HilbertNode2D(tree, parent, 0, listChildren, listEntries);
    }
}
