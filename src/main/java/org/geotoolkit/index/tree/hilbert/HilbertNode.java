/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hilbeRTree;

import com.mycompany.utilsRTree.CoupleNode;
import com.mycompany.utilsRTree.Entry;
import com.mycompany.utilsRTree.Node;
import com.mycompany.utilsRTree.Rtree;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

/**
 * TODO : find good mathematical formula to define exhaustive "Hilbert value".
 * @author Marechal Remi (Geomatys)
 */
public class HilbertNode extends Node /*implements Bound*/ {

    List<Node> listN;//contient des cells
    List<Point2D> listOfCentroidChild;
    boolean leaf = false;
    int hilbertOrder;
    int[][] tabHV;
    
    /**To compare two {@code Node} from them boundary box x axis coordinate. 
     * @see StarNode#organizeFrom(int) 
     */
    private static final Comparator<Bound> BOUND_COMPARATOR_X = new Comparator<Bound>() {

        public int compare(Bound e1, Bound e2) {
            ArgumentChecks.ensureNonNull("first bound is null", e1);
            ArgumentChecks.ensureNonNull("second bound is null", e2);
            java.lang.Double x1 = new java.lang.Double(e1.getBoundary().getBounds2D().getMinX());
            java.lang.Double x2 = new java.lang.Double(e2.getBoundary().getBounds2D().getMinX());
            return x1.compareTo(x2);
        }
    };
    
    /**To compare two {@code Node} from them boundary box y axis coordinate. 
     * @see StarNode#organizeFrom(int) 
     */
    private static final Comparator<Bound> BOUND_COMPARATOR_Y = new Comparator<Bound>() {

        public int compare(Bound e1, Bound e2) {
            ArgumentChecks.ensureNonNull("first bound is null", e1);
            ArgumentChecks.ensureNonNull("second bound is null", e2);
            java.lang.Double y1 = new java.lang.Double(e1.getBoundary().getBounds2D().getMinY());
            java.lang.Double y2 = new java.lang.Double(e2.getBoundary().getBounds2D().getMinY());
            return y1.compareTo(y2);
        }
    };

    /**Create a Hilbert leaf or branch.
     * 
     * @param tree : pointer on RTree.
     * @param hilbertOrder : subdivision leaf order.
     * @param rect : boundary of leaf.
     * @param leaf : true if caller want to create a leaf else false (to create a tree branch).
     * @throws IllegalArgumentException if tree is null.
     * @throws IllegalArgumentException if rect is null.
     * @throws IllegalArgumentException if hilbertOrder < 0.
     */
    public HilbertNode(Rtree tree, int hilbertOrder, Rectangle2D rect, boolean leaf) {
        ArgumentChecks.ensureNonNull("tree is null", tree);
        ArgumentChecks.ensureNonNull("rect is null", rect);
        ArgumentChecks.ensurePositive("hilbert Order can't be negative", hilbertOrder);
        this.tree = tree;
        this.leaf = leaf;
        this.hilbertOrder = hilbertOrder;
        this.setRect(rect);
        this.listOfCentroidChild = new ArrayList<Point2D>(Arrays.asList(getCentroid(rect)));
        this.listN = new ArrayList<Node>();
        createBasicHB(hilbertOrder);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void insert(Entry entry) {

        ArgumentChecks.ensureNonNull("impossible to insert a null entry", entry);

        if (isFull()) {

            List<Node> lSp = split();
            if (lSp != null) {
                listN.clear();
                listOfCentroidChild.clear();
                listN.addAll(lSp);
                this.leaf = false;
                this.hilbertOrder = 0;
            }
        }
        
        if (leaf && (!this.contains(entry.getBoundary()))) {
            List<Bound> lE = getAllEntry();
            lE.add(entry);
            this.setRect(getEnveloppeMin(lE));
            if (hilbertOrder > 0) {
                organizeFrom(0, lE);
            }
            createBasicHB(this.hilbertOrder);
            for (Bound boun : lE) {
                Entry ent = (Entry) boun;
                chooseSubtree(ent).insert(ent);
            }
        } else {
            chooseSubtree(entry).insert(entry);
        }
        reSize();
    }

    /**Exchange some entry(ies) between two nodes in aim to find best form with lesser overlaps.
     * Also branchGrafting will be able to avoid splitting node.
     * 
     * @param n1 Node
     * @param n2 Node
     * @throws IllegalArgumentException if n1 or n2 are null.
     * @throws IllegalArgumentException if n1 and n2 have different "parent".
     * @throws IllegalArgumentException if n1 or n2 are not tree leaf.
     * @throws IllegalArgumentException if n1 or n2, and their subnodes, don't contains some {@code Entry}.
     */
    private void branchGrafting(Node n1, Node n2) {
        
        ArgumentChecks.ensureNonNull("Node n1 null", n1);
        ArgumentChecks.ensureNonNull("Node n2 null", n2);

        final HilbertNode theFather = (HilbertNode) n1.getParent();

        final int hp1 = theFather.hashCode();
        final int hp2 = n2.getParent().hashCode();

        if (hp1 != hp2) {
            throw new IllegalArgumentException("you won't be exchange data with nodes which don't own same parent");
        }

        if (!(n1 instanceof HilbertNode && n2 instanceof HilbertNode)) {
            throw new IllegalArgumentException("two node aren't HilbertLeaf type");
        }

        final HilbertNode np1 = (HilbertNode) n1;
        final HilbertNode np2 = (HilbertNode) n2;

        final Rectangle2D boundN1 = n1.getBounds2D();
        final Rectangle2D boundN2 = n2.getBounds2D();

        if (boundN1.intersects(boundN2)) {

            final List<Bound> lnp1 = np1.getAllEntry();
            final List<Bound> lnp2 = np2.getAllEntry();

            if (lnp1.isEmpty() || lnp2.isEmpty()) {
                throw new IllegalArgumentException("impossible to exchange data with empty node");
            }

            final List<Entry> lEOverlaps = new ArrayList<Entry>();
            search(boundN1.createIntersection(boundN2), lEOverlaps);
            if (!lEOverlaps.isEmpty()) {
                List<Bound> lBE = new ArrayList<Bound>(lEOverlaps);
                int indice = findAppropriateSplit(lnp1, lnp2, lBE);

                if (!boundN1.contains(boundN2) && !boundN2.contains(boundN1) && indice != -1) {

                    if (!lEOverlaps.isEmpty()) {
                        for (Entry ent : lEOverlaps) {
                            theFather.delete(ent);
                        }
                        n1.reSize();
                        n2.reSize();

                        for (int i = 0; i < indice; i++) {
                            n1.insert((Entry) lBE.get(i));
                        }

                        for (int i = indice, s = lBE.size(); i < s; i++) {
                            n2.insert((Entry) lBE.get(i));
                        }
                    }
                } else {

                    List<Bound> lB = new ArrayList<Bound>(lnp1);
                    lB.addAll(lnp2);

                    for (int i = 0; i < lB.size() - 1; i++) {
                        for (int j = i + 1; j < lB.size(); j++) {
                            if (lB.get(i).hashCode() == lB.get(j).hashCode()) {
                                lB.remove(j);
                            }
                        }
                    }

                    final int s = lB.size();
                    final int smin = s / 3;
                    final Rectangle2D rectGlob = getEnveloppeMin(lB);
                    final double rGW = rectGlob.getWidth();
                    final double rGH = rectGlob.getHeight();
                    final int index = (rGW < rGH) ? 2 : 1;
                    organizeFrom(index, lB);

                    int indexing = s / 2;
                    double overlapsTemp = rGW * rGH;
                    final Rectangle2D rTemp1 = new Rectangle2D.Double();
                    final Rectangle2D rTemp2 = new Rectangle2D.Double();
                    final List<Bound> lTn1 = new ArrayList<Bound>();
                    final List<Bound> lTn2 = new ArrayList<Bound>();

                    for (int i = smin; i < s - smin; i++) {
                        final List<Bound> testn1 = new ArrayList<Bound>();
                        final List<Bound> testn2 = new ArrayList<Bound>();
                        for (int j = 0; j < i; j++) {
                            testn1.add(lB.get(j));
                        }
                        for (int k = i; k < s; k++) {
                            testn2.add(lB.get(k));
                        }
                        final Rectangle2D r1 = getEnveloppeMin(testn1);
                        final Rectangle2D r2 = getEnveloppeMin(testn2);
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

                    np1.setLeaf(true);
                    np1.setRect(rTemp1);
                    np1.setTree(tree);
                    np1.createBasicHB(0);
                    np1.setParent(null);

                    np2.setLeaf(true);
                    np2.setRect(rTemp1);
                    np2.setTree(tree);
                    np2.createBasicHB(0);
                    np2.setParent(null);

                    for (Bound ent : lTn1) {
                        np1.insert((Entry) ent);
                    }
                    for (Bound ent : lTn2) {
                        np2.insert((Entry) ent);
                    }
                    np1.setParent(theFather);
                    np2.setParent(theFather);
                }
            }
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void search(Rectangle2D regionSearch, List<Entry> result) {
        if (this.getBounds2D().intersects(regionSearch)) {
            for (Node nod : listN) {
                nod.search(regionSearch, result);
            }
        }
    }

    /**
     * @return listN Node list. 
     */
    public List<Node> getNode() {
        return listN;
    }

    /**Affect {@code RTree} pointer.
     * 
     * @param tree 
     */
    public void setTree(Rtree tree) {
        this.tree = tree;
    }

    /**
     * {@inheritDoc}. 
     */
    @Override
    public boolean delete(Entry entry) {
        if (!this.getBounds2D().intersects(entry.getBoundary())) {
            return false;
        }
        boolean success = false;
        for (Node nod : listN) {
            success = nod.delete(entry);
            if (success) {
                trim();
//                reSize();
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean isFull() {

        if (!leaf) {
            return listN.size() >= tree.getMaxElements();
        } else {
            for (Node nod : listN) {
                if (!nod.isFull()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**Define if it is a leaf or a branch tree.
     * 
     * @param leaf 
     */
    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trim() {
        
        if (leaf) {
            if (this.hilbertOrder > 0) {
                List<Bound> lE = new ArrayList<Bound>(getAllEntry());
                if (lE.size() <= ((HilbertRTree) tree).getMaxElements() * Math.pow(2, (this.hilbertOrder - 1) * 2)) {
                    hilbertOrder--;
                    this.setRect(getEnveloppeMin(lE));
                    createBasicHB(this.hilbertOrder);
                    for (Bound boun : lE) {
                        insert((Entry) boun);
                    }
                }
            } 
        } else {
            List<Bound> lE = new ArrayList<Bound>(getAllEntry());
            HilbertRTree t2 = (HilbertRTree) tree;
            if (lE.size() <= t2.getMaxElements() * Math.pow(2, (t2.getHilbertOrder()) * 2)) {
                this.setRect(getEnveloppeMin(lE));
                createBasicHB(t2.getHilbertOrder());
                for (Bound bo : lE) {
                    insert((Entry) bo);
                }
            } else {
                if(!listN.isEmpty()){
                    for (int i = listN.size() - 1; i >= 0; i--) {
                        HilbertNode pr = (HilbertNode) listN.get(i);
                        if (pr.getAllEntry().isEmpty()) {
                            listN.remove(i);
                        }
                    }
                    int s = listN.size();
                    if (s == 1) {
                        HilbertNode n = (HilbertNode) listN.remove(0);
                        if (!n.getInstanceLeaf()) {
                            List<Node> lNod = n.getNode();
                            for (Node noo : lNod) {
                                noo.setParent(this);
                            }
                            listN.addAll(lNod);
                        }else{
                            this.setRect(getEnveloppeMin(lE));
                            this.hilbertOrder = n.getHilberOrder();
                            createBasicHB(n.getHilberOrder());
                            for (Bound bo : lE) {
                                insert((Entry) bo);
                            }
                        }
                    }
                } 
            }
        }
        for(Node n : listN){
            n.reSize();
        }
        reSize();
        if (parent != null) {
            parent.trim();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Graphics2D g) {
        if (leaf) {
            g.setColor(Color.BLUE);
            if (!listOfCentroidChild.isEmpty()) {
                for (int i = 0; i < listOfCentroidChild.size() - 1; i++) {
                    g.draw(new Line2D.Double(listOfCentroidChild.get(i), listOfCentroidChild.get(i + 1)));
                }
            }

        }
        for (Node nod : listN) {
            nod.paint(g);
        }

        if (leaf) {
//            g.setColor(Color.blue);
//            g.draw(new Line2D.Double(getMinX(), getMinY(), getMaxX(), getMaxY()));
            g.setColor(Color.red);
            g.draw(this.getBounds2D());
        } else {
            g.setColor(Color.blue);
            g.draw(this.getBounds2D());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNbElements() {
        if (leaf) {
            int nbElement = 0;
            for (Node hc : listN) {
                nbElement += hc.getNbElements();
            }
            return nbElement;
        } else {
            return listN.size();
        }
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if this {@code Node} contains lesser two subnode.
     * @throws IllegalArgumentException if this {@code Node} doesn't contains {@code Entry}.
     */
    @Override
    public List<Node> split() {

        if (listN.size() < 2 && !leaf) {
            throw new IllegalStateException("impossible to split node with lesser two subnode");
        }

        if (leaf && (hilbertOrder < ((HilbertRTree) tree).getHilbertOrder())) {
            hilbertOrder++;
            List<Bound> lE = getAllEntry();
            if (lE.isEmpty()) {
                throw new IllegalStateException("impossible to increase Hilbert order of a empty Node");
            }
            this.setRect(getEnveloppeMin(lE));
            createBasicHB(this.hilbertOrder);
            organizeFrom(0, lE);
            for (Bound boun : lE) {
                Entry ent = (Entry) boun;
                chooseSubtree(ent).insert(ent);
            }
            return null;
        } else {
            final List<Node> lS = splitAxis(defineSplitAxis());
            final HilbertNode ls1 = (HilbertNode) lS.get(0);
            final HilbertNode ls2 = (HilbertNode) lS.get(1);
            ls1.setParent(this);
            ls2.setParent(this);
            if (ls1.intersects(ls2)&&this.leaf) {
                branchGrafting(ls1, ls2);
            }
            return lS;
        }
    }

    /**We can choose axis to split.
     *      - case 1 : to choose x axis split.
     *      - case 2 : to choose y axis split.
     * 
     * @param index choose one or 2.
     * @throws IllegalArgumentException if index is out of required limits.
     * @throws IllegalStateException if try to split leaf with only one or lesser element.
     * @return List of two Node which is split of Node passed in parameter.
     */
    private List<Node> splitAxis(int index) {

        ArgumentChecks.ensureBetween("index out of permit limits", 1, 2, index);

        if (listN.size() <= 1) {
            throw new IllegalStateException("you can't split Leaf with only one elements or lesser");
        }

        CoupleNode couNN;

        final List<CoupleNode> lSAO = new ArrayList<CoupleNode>();
        final List<CoupleNode> lSSo = new ArrayList<CoupleNode>();
        List<Bound> lE = new ArrayList<Bound>();
        if (leaf) {
            lE = getAllEntry();
        } else {
            for (Node nod : listN) {
                lE.add((HilbertNode) nod);
            }
        }

        final int s = lE.size();
        organizeFrom(index, lE);
        final int val = s / 2;

        final List<Bound> splitList1 = new ArrayList<Bound>();
        final List<Bound> splitList2 = new ArrayList<Bound>();

        for (int i = val; i <= s - val; i++) {
            for (int j = 0; j < i; j++) {
                splitList1.add(lE.get(j));
            }
            for (int k = i; k < s; k++) {
                splitList2.add(lE.get(k));
            }

            final Rectangle2D r1 = getEnveloppeMin(splitList1);
            final Rectangle2D r2 = getEnveloppeMin(splitList2);

            final HilbertNode sPH1 = new HilbertNode(tree, hilbertOrder, r1, false);
            final HilbertNode sPH2 = new HilbertNode(tree, hilbertOrder, r2, false);
            if (leaf) {
                for (Bound ent : splitList1) {
                    sPH1.insert((Entry) ent);
                }
                for (Bound ent : splitList2) {
                    sPH2.insert((Entry) ent);
                }
            } else {
                for (Bound ent : splitList1) {
                    sPH1.getNode().add((HilbertNode) ent);
                }
                for (Bound ent : splitList2) {
                    sPH2.getNode().add((HilbertNode) ent);
                }
                sPH1.reSize();
                sPH2.reSize();
            }

            couNN = new CoupleNode(sPH1, sPH2);

            if (couNN.intersect()) {
                lSAO.add(couNN);
            } else {
                lSSo.add(couNN);
            }
            splitList1.clear();
            splitList2.clear();
        }
        return lSSo.isEmpty() ? getMinOverlapsOrPerimeter(lSAO, 0) : getMinOverlapsOrPerimeter(lSSo, 1);
    }

    

    /**Compute and define which axis to split {@code this Node}.
     * 
     * @throws IllegalStateException if this {@code Node} and his subnodes, don't contains {@code Entry}.
     * @return 1 to split in x axis and 2 to split in y axis.
     */
    private int defineSplitAxis() {

        List<Bound> lE = new ArrayList<Bound>();
        if (leaf) {
            lE = getAllEntry();
        } else {
            for (Node nod : listN) {
                lE.add((HilbertNode) nod);
            }
        }

        if (lE.isEmpty()) {
            throw new IllegalStateException("impossible to define split axis with empty Bound list");
        }

        final int s = lE.size();
        final int val = s / 2;//tree.getMaxElements() / 3;

        double perimX = 0;
        double perimY = 0;

        final List<Bound> splitList1 = new ArrayList<Bound>();
        final List<Bound> splitList2 = new ArrayList<Bound>();

        CoupleNode coupNN;
        for (int index = 1; index <= 2; index++) {

            organizeFrom(index, lE);

            for (int i = val; i <= s - val; i++) {
                for (int j = 0; j < i; j++) {
                    splitList1.add(lE.get(j));
                }
                for (int k = i; k < s; k++) {
                    splitList2.add(lE.get(k));
                }
                final Rectangle2D r1 = getEnveloppeMin(splitList1);
                final Rectangle2D r2 = getEnveloppeMin(splitList2);

                final HilbertNode sPH1 = new HilbertNode(tree, hilbertOrder, r1, false);
                final HilbertNode sPH2 = new HilbertNode(tree, hilbertOrder, r2, false);
                coupNN = new CoupleNode(sPH1, sPH2);

                switch (index) {
                    case 1: {
                        perimX += coupNN.getPerimeter();
                    }
                    break;

                    case 2: {
                        perimY += coupNN.getPerimeter();
                    }
                    break;
                }
                splitList1.clear();
                splitList2.clear();
            }
        }
        return (perimX <= perimY) ? 1 : 2;
    }

    /**
     * Organize all {@code Node} by differents criterion.
     * 
     * @param index : - 0 : organize all Node by nearest to furthest between them centroid and this Node centroid.
     *                - 1 : organize all Node by smallest x value to tallest.
     *                - 2 : organize all Node by smallest y value to tallest.
     * @throws IllegalArgumentException if index is out of required limit.
     * @throws IllegalArgumentException if lE is null.
     * @throws IllegalArgumentException if lE is empty.
     * @throws IllegalArgumentException if lE don't contains some {@code Entry}.
     */
    private void organizeFrom(int index, List<Bound> lE) {

        ArgumentChecks.ensureBetween("index is out of limit required", 0, 2, index);
        ArgumentChecks.ensureNonNull("impossible to organize a empty list", lE);

        if (lE.isEmpty()) {
            throw new IllegalArgumentException("impossible to organize a empty list");
        }

        switch (index) {
            case 0:
                if (!(lE.get(0) instanceof Entry)) {
                    throw new IllegalArgumentException("impossible to organize from HilberValue none Entry liste");
                }
                int s = lE.size();
                for (int i = 0; i < s; i++) {
                    for (int j = 0; j < s - 1; j++) {
                        int hve1 = getHVOfEntry((Entry) lE.get(j));
                        int hve2 = getHVOfEntry((Entry) lE.get(j + 1));
                        if (hve2 < hve1) {
                            Entry e = (Entry) lE.remove(j);
                            lE.add(j + 1, e);
                        }
                    }
                }
                break;

            case 1:
                Collections.sort(lE, BOUND_COMPARATOR_X);
                break;

            case 2:
                Collections.sort(lE, BOUND_COMPARATOR_Y);
                break;
        }
    }

    /**
     * @return true if it's a Hilbert leaf {@code Node} else false (branch). 
     */
    public boolean getInstanceLeaf() {
        return leaf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reSize() {
        
        if (leaf && hilbertOrder > 0) {
            List<Bound> lB = getAllEntry();
            createBasicHB(hilbertOrder);
            this.setRect(getEnveloppeMin(lB));
            for (Bound boun : lB) {
                Entry ent = (Entry) boun;
                chooseSubtree(ent).insert(ent);
            }
        }else{
            this.setRect(getEnveloppeMin(new ArrayList<Bound>(listN)));
        }
    }

    @Override
    public double getDeadSpace() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**Create a conform Hilbert leaf and define Hilbert curve in fonction of {@code indice}.
     * @throws IllegalArgumentException if indice < 0.
     * @param indice Hilber order ask by user. 
     */
    private void createBasicHB(int indice) {
        ArgumentChecks.ensurePositive("impossible to create Hilbert Curve with negative indice", indice);

        listOfCentroidChild.clear();
        this.leaf = true;
        this.hilbertOrder = indice;
        if (indice > 0) {
            final double w = this.getWidth() / 4;
            final double h = this.getHeight() / 4;

            final double minx = this.getMinX() + w;
            final double maxx = this.getMaxX() - w;
            final double miny = this.getMinY() + h;
            final double maxy = this.getMaxY() - h;
            listOfCentroidChild.add(new Point2D.Double(minx, miny));
            listOfCentroidChild.add(new Point2D.Double(minx, maxy));
            listOfCentroidChild.add(new Point2D.Double(maxx, maxy));
            listOfCentroidChild.add(new Point2D.Double(maxx, miny));
            if (indice > 1) {
                for (int i = 1; i < indice; i++) {
                    createHB(this);
                }
            }

            listN   = new ArrayList<Node>();
            int dim = (int) Math.pow(2, hilbertOrder);
            tabHV   = new int[dim][dim];
            
            for (int i = 0, s = listOfCentroidChild.size(); i < s; i++) {
                Point2D ptCTemp = listOfCentroidChild.get(i);
                int[] tabTemp = getHilbCoord(ptCTemp);
                tabHV[tabTemp[0]][tabTemp[1]] = i;
                HilbertCell hl = new HilbertCell(tree, ptCTemp, i);
                hl.setParent(this);
                listN.add(hl);
            }
        } else {
            listOfCentroidChild.add(this.getCentroid());
            listN.clear();
            this.hilbertOrder = 0;
            listN.add(new HilbertCell(tree, this.getCentroid(), 0));
        }


    }

    /**Create subnode(s) centroid(s).
     * These centroids define Hilbert curve.
     * Increase the Hilbert order of {@code HilbertLeaf} passed in parameter by one unity.
     * 
     * @param hl HilbertLeaf to increase Hilbert order.
     * @throws IllegalArgumentException if param "hl" is null.
     * @throws IllegalArgumentException if param hl Hilbert order is larger than them Hilbert RTree.
     */
    private void createHB(HilbertNode hl) {

        ArgumentChecks.ensureNonNull("impossible to increase hilbert order", hl);
        if (hl.getHilberOrder() > ((HilbertRTree) tree).getHilbertOrder()) {
            throw new IllegalArgumentException("hilbert order is larger than hilbertRTree hilbert order");
        }

        final List<Point2D> lPTemp = new ArrayList<Point2D>(hl.getListOfSubCenter());
        List<Point2D> lPTemp2      = new ArrayList<Point2D>(lPTemp);
        final Point2D centroid     = hl.getCentroid();
        final double centreX       = centroid.getX();
        final double centreY       = centroid.getY();
        final double quartWidth    = hl.getWidth() / 4;
        final double quartHeight   = hl.getHeight() / 4;

        hl.getListOfSubCenter().clear();
        final AffineTransform mt1  = new AffineTransform(1, 0, 0, 1, -centreX, -centreY);
        final AffineTransform rot1 = new AffineTransform();
        final AffineTransform mt21 = new AffineTransform(1 / quartWidth, 0, 0, 1 / quartHeight, 0, 0);
        final AffineTransform mt22 = new AffineTransform(quartWidth, 0, 0, quartHeight, 0, 0);
        final AffineTransform mt2  = new AffineTransform();
        final AffineTransform mt3  = new AffineTransform(1 / 2.0, 0, 0, 1 / 2.0, 0, 0);

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
                lPTemp2 = lPTemp;

            } else if (i == 2) {
                mt2.setTransform(1, 0, 0, 1, centreX + quartWidth, centreY + quartHeight);
                mt2.concatenate(mt3);
                mt2.concatenate(mt1);
                lPTemp2 = lPTemp;
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
                hl.getListOfSubCenter().add(mt2.transform(pt, null));
            }
        }
    }

    /**
     * @return Hilbert curve order. 
     */
    public int getHilberOrder() {
        return this.hilbertOrder;
    }

    /**Find Hilbert order of an entry from this HilbertLeaf.
     * 
     * @param entry where we looking for her Hilbert order.
     * @throws IllegalArgumentException if param "entry" is out of this node boundary.
     * @throws IllegalArgumentException if entry is null.
     * @return int the entry Hilbert order.
     */
    private int getHVOfEntry(Entry entry) {
        ArgumentChecks.ensureNonNull("impossible to define Hilbert coordinate with null entry", entry);
        if (!this.contains(entry.getBoundary())) {
            throw new IllegalArgumentException("entry is out of this node boundary");
        }
        Rectangle2D recEnt = entry.getBoundary();
        Point2D ptCE = getCentroid(recEnt);
        int[] hCoord = getHilbCoord(ptCE);
        return tabHV[hCoord[0]][hCoord[1]];
    }

    /**
     * @return all entries contains in this Node and in his subnode.
     */
    public List<Bound> getAllEntry() {

        List<Entry> le = new ArrayList<Entry>();
        if (leaf) {
            for (Node nod : listN) {
                HilbertCell hC = (HilbertCell) nod;
                le.addAll(hC.getElements());
            }
        } else {
            search(this.getBounds2D(), le);
        }
        List<Bound> lB = new ArrayList<Bound>();
        for (Entry ent : le) {
            lB.add(ent);
        }
        return lB;
    }

    /**Find {@code Point2D} Hilbert coordinate from this Node.
     * 
     * @param pt {@code Point2D} 
     * @throws IllegalArgumentException if param "pt" is out of this node boundary.
     * @throws IllegalArgumentException if param pt is null.
     * @return int[] table of lenght 2 which contains two coordinate.
     * @see #getHVOfEntry(org.geotoolkit.utilsRTree.Entry)
     */
    private int[] getHilbCoord(Point2D pt) {
        ArgumentChecks.ensureNonNull("impossible to define Hilbert coordinate with null point", pt);
        if (!this.contains(pt)) {
            throw new IllegalArgumentException("Point is out of this node boundary");
        }
        double divX = this.getWidth() / (Math.pow(2, this.hilbertOrder));
        double divY = this.getHeight() / (Math.pow(2, this.hilbertOrder));
        int hx = (int) (Math.abs(pt.getX() - this.getMinX()) / divX);
        int hy = (int) (Math.abs(pt.getY() - this.getMinY()) / divY);
        return new int[]{hx, hy};
    }

    /**Find appropriate subnode to insert new entry.
     * Appropriate subnode is choosed to answer HilbertRtree criterions.
     * 
     * @param entry to insert.
     * @throws IllegalArgumentException if this subnodes list is empty.
     * @throws IllegalArgumentException if entry is null.
     * @return subnode choosen.
     */
    private Node chooseSubtree(Entry entry) {
        ArgumentChecks.ensureNonNull("impossible to choose subtree with entry null", entry);
        if (listN.isEmpty()) {
            throw new IllegalArgumentException("impossible to find appropriate subtree (list of child node is empty)");
        }
        if (leaf) {
            if (hilbertOrder < 1) {
                return listN.get(0);
            }
            int index;
            index = getHVOfEntry(entry);
            for (Node nod : listN) {
                HilbertCell phl = (HilbertCell) nod;
                if (index <= phl.getHilberValue() && !phl.isFull()) {
                    return nod;
                }
            }
            return listN.get(findAnotherCell(index, getCentroid(entry.getBoundary())));
        } else {
            for (Node no : listN) {
                if (no.getBounds2D().contains(entry.getBoundary())) {
                    return no;
                }
            }

            int index = 0;
            Rectangle2D rtotal = this.getBounds2D();
            double overlaps = rtotal.getWidth() * rtotal.getHeight();
            
            for (int i = 0; i < listN.size(); i++) {
                double overlapsTemp = 0;
                for (int j = 0; j < listN.size(); j++) {
                    if (i != j) {
                        HilbertNode ni = (HilbertNode) listN.get(i);
                        HilbertNode nj = (HilbertNode) listN.get(j);
                        List<Bound> lB = ni.getAllEntry();
                        lB.add((Bound) entry);
                        Rectangle2D lBound = getEnveloppeMin(lB);
                        Rectangle2D njBound = nj.getBounds2D();
                        Rectangle2D inter = lBound.createIntersection(njBound);
                        overlapsTemp += inter.getWidth() * inter.getHeight();
                    }
                }
                if (overlapsTemp < overlaps) {
                    index = i;
                    overlaps = overlapsTemp;
                } else if (overlapsTemp == overlaps) {
                    int si = ((HilbertNode) listN.get(i)).getAllEntry().size();
                    int sindex = ((HilbertNode) listN.get(index)).getAllEntry().size();
                    if (si < sindex) {
                        index = i;
                        overlaps = overlapsTemp;
                    }
                }
            }
            return listN.get(index);
        }
    }

    /**To answer Hilbert criterions and to avoid call split method,  in some case 
     * we constrain tree leaf to choose another cell to insert Entry. 
     * 
     * @param index of subnode which is normaly choosen.
     * @param ptCentroid subnode choosen centroid.
     * @throws IllegalArgumentException if method call by none leaf {@code Node}.
     * @throws IllegalArgumentException if index is out of required limit.
     * @throws IllegalStateException if no another cell is find.
     * @return int index of another subnode.
     */
    private int findAnotherCell(int index, Point2D ptCentroid) {
        ArgumentChecks.ensureNonNull("impossible to find another leaf with ptCentroid null", ptCentroid);
        if (!leaf) {
            throw new IllegalArgumentException("impossible to find another leaf in Node which isn't LEAF tree");
        }
        ArgumentChecks.ensureBetween("index to find another leaf is out of required limit",
                0, (int) Math.pow(2, (hilbertOrder * 2)), index);
        int siz = listN.size();
        int indexTemp1 = index;
        int indexTemp2 = index;
        for (int i = index; i < siz; i++) {
            if (! listN.get(i).isFull()) {
                indexTemp1 = i;
                break;
            }
            if (i == siz - 1) {
                i = -1;
            }
        }

        for (int j = index; j >= 0; j--) {
            if (! listN.get(j).isFull()) {
                indexTemp2 = j;
                break;
            }
        }

        if (indexTemp1 != index && indexTemp2 != index) {
            return (distanceBetweenTwoPoint(listN.get(indexTemp1).getCentroid(), ptCentroid) < distanceBetweenTwoPoint(listN.get(indexTemp2).getCentroid(), ptCentroid))
                    ? indexTemp1 : indexTemp2;
        }
        if (indexTemp1 != index) {
            return indexTemp1;
        }
        if (indexTemp2 != index) {
            return indexTemp2;
        }

        throw new IllegalStateException("will be able to split");
    }

    /**
     * @return list of centroids subnodes.
     */
    private List<Point2D> getListOfSubCenter() {
        return listOfCentroidChild;
    }

    /**
     * {@inheritDoc}. 
     */
    @Override
    public String toString() {
        if (leaf) {
            return Trees.toString("HLeaf", listN);
        } else {
            return Trees.toString("HBranch leaf = " + leaf, listN);
        }

    }

}
