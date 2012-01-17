/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.hilbert;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

/**Create RTree Node. (Branch or leaf)
 *
 * @author Rémi Maréchal (Geomatys).
 */
public abstract class Node extends Rectangle2D.Double implements Bound{
    
    protected Rtree tree;
    protected Node parent;

    /**
     * @return pointer on this node parent. 
     */
    public Node getParent() {
        return parent;
    }

    /**Affect a parent Node at this Node.
     * 
     * @param parent node which will be affect.
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }
    
    /**
     * Concaten and condense RTree.
     * For example after a deletion.
     * @see #delete(org.geotoolkit.utilsRTree.Entry) 
     */
    public abstract void trim();
    
    /**Find all {@code Shape} which intersect regionSearch param. 
     * 
     * @param regionSearch area of search.
     * @param result {@code List} where is put search result.
     */
    public abstract void search(Rectangle2D regionSearch, List<Entry> result);
    
    /**Draw Node.
     * 
     * @param g {@code Graphics2D}
     */
    public abstract void paint(Graphics2D g);
    
    /**
     * @return {@code double} number of elements within this Node.
     */
    public abstract int getNbElements();
    
    /**Split this Node in two others distincts Node with split rules in accordance from R-Tree.
     * 
     * @return List of two node which is split of {@code this Node}.
     * @throw IllegalStateException : if split with one or less elements
     */
    public abstract List<Node> split();
    
    /**Delete {@code Shape} (if it is find).
     * 
     * @param shape to delete.
     */
    public abstract boolean delete(Entry entry);
    
    /**Insert new {@code Shape} in branch and organize branch if it's necessary.
     * 
     * @param shape to add.
     */
    public abstract void insert(Entry entry);
    
    /**Ajuste size of {@code this Node} from interiors {@code Node} datas.
     * 
     * @param nod to resize;
     */
    public abstract void reSize();
    
    /**Compute empty area of {@code this Node}.
     * 
     * @return this area subtract some of area elements.
     */
    public abstract double getDeadSpace();
    
    /**A {@code Node} is considered overflow when it own more elements than RTree max value. 
     * @return true if Node is full else false.
     */
    public abstract boolean isFull();
    
    /**
     * @return this Node centroid.
     */
    public Point2D getCentroid(){
        return new Point2D.Double(this.getMinX()+this.getWidth()/2, this.getMinY()+this.getHeight()/2);
    }
    
    /**
     * @return {@code this Node} perimeter.
     */
    public double getPerimeter(){
        return 2*(this.getWidth()+this.getHeight());
    }
    
    /**Compute intersect of two {@code Rectangle2D} and return this area.
     * 
     * @param rect1
     * @param rect2
     * @return rect1 and rect2 intersection area.
     */
    public double getOverlapsArea(final Rectangle2D rect1, final Rectangle2D rect2){
        final Rectangle2D rect = rect1.createIntersection(rect2);
        return rect.getWidth()*rect.getHeight();
    }
    
    /**Return the smallest distance between an {@code Entry} and a {@code Node}.
     * 
     * @param entry
     * @param node
     * @throws IllegalArgumentException if node null.
     * @throws IllegalArgumentException if entry null.
     * @return double distance.
     */
    private double getMinDist(Entry entry, Node node) {
        ArgumentChecks.ensureNonNull("(getMinDist) entry null", entry);
        ArgumentChecks.ensureNonNull("(getMinDist) node null", node);
        Point2D ptEnt = getCentroid(entry.getBoundary());
        final double entX = ptEnt.getX();
        final double entY = ptEnt.getY();
        final double nMinX = node.getMinX();
        final double nMinY = node.getMinY();
        final double nMaxX = node.getMaxX();
        final double nMaxY = node.getMaxY();
        if (entX < nMaxX && entX > nMinX && entY < nMaxY && entY > nMinX) {
            return 0;
        } else if (entX < nMaxX && entX > nMinX) {
            return Math.min(Math.abs(entY - nMinY), Math.abs(entY - nMaxY));
        } else if (entY < nMaxY && entY > nMinX) {
            return Math.min(Math.abs(entX - nMinX), Math.abs(entX - nMaxX));
        } else {
            final double dist00 = distanceBetweenTwoPoint(ptEnt, new Point2D.Double(nMinX, nMinY));
            final double dist10 = distanceBetweenTwoPoint(ptEnt, new Point2D.Double(nMaxX, nMinY));
            final double dist01 = distanceBetweenTwoPoint(ptEnt, new Point2D.Double(nMinX, nMaxY));
            final double dist11 = distanceBetweenTwoPoint(ptEnt, new Point2D.Double(nMaxX, nMaxY));
            return Math.min(Math.min(dist00, dist01), Math.min(dist10, dist11));
        }
    }
    
    /**Find in lN, couple of {@code Node} with smallest overlapping or perimeter.
     * Choose indice : - case 0 : find couple with smallest overlapping.
     *                 - case 1 : find couple with smallest perimeter.
     * 
     * @param lN list of CoupleShape.
     * @see CoupleShape.
     * @param indice to select criterion.
     * @throws IllegalArgumentException if lCN is empty or null.
     * @throws IllegalArgumentException if indice is out of required limits.
     * @return {@code List<Node>} with size = 2, which is selectionned couple of {@code Node}.
     */
    protected List<Node> getMinOverlapsOrPerimeter(List<CoupleNode> lCN, int indice) {

        ArgumentChecks.ensureBetween("indice out of permit limits", 0, 1, indice);
        ArgumentChecks.ensureNonNull("CoupleNode null", lCN);
        if (lCN.isEmpty()) {
            throw new IllegalArgumentException("CoupleNode list is empty");
        }
        if (lCN.size() == 1) {
            return UnmodifiableArrayList.wrap((Node) lCN.get(0).getNodeA(), (Node) lCN.get(0).getNodeB());
        }

        double valueRef;
        int index = 0;

        switch (indice) {
            case 0: {
                valueRef = lCN.get(0).getOverlapsArea();
                for (int i = 1; i < lCN.size(); i++) {
                    double valueTemp = lCN.get(i).getOverlapsArea();
                    if (valueTemp < valueRef) {
                        valueRef = valueTemp;
                        index = i;
                    }
                }
            }
            break;
            case 1: {
                valueRef = lCN.get(0).getPerimeter();
                for (int i = 1, n = lCN.size(); i < n; i++) {
                    double valueTemp = lCN.get(i).getPerimeter();
                    if (valueTemp < valueRef) {
                        valueRef = valueTemp;
                        index = i;
                    }
                }
            }
            break;
        }
        return UnmodifiableArrayList.wrap((Node) lCN.get(index).getNodeA(), (Node) lCN.get(index).getNodeB());
    }
    
    /**Add progressively each {@code listOverlaps} element(s) in {@code listnode1} or {@code listnode2}. 
     * Each list symbolize a surface representing by them elements.
     * The aim is to distribute one by one in order each data of {@code listOverlaps} in {@code listNode1} and 
     * {@code listNode2} to avoid (if it's possible) overlaps between {@code listNode1} surface and {@code listNode2} surface.
     * 
     * @see HilbertLeaf#exchangeData(com.mycompany.utilsRTree.Node, com.mycompany.utilsRTree.Node) 
     * @param listNode1 list of Node elements.
     * @param listNode2 list of Node elements.
     * @param listOverlaps list of elements on the overlaps surface.
     * @throws IllegalArgumentException if listNode1 or listNode2 or listOverlaps are null.
     * @throws IllegalArgumentException if one of these list is empty.
     * @return index of overlaps list if a solution is find, else -1
     */
    protected int findAppropriateSplit(List<Bound> listNode1, List<Bound> listNode2, List<Bound> listOverlaps) {
        ArgumentChecks.ensureNonNull("(findAppropriateSplit) listNode1 is null", listNode1);
        ArgumentChecks.ensureNonNull("(findAppropriateSplit) listNode2 is null", listNode2);
        ArgumentChecks.ensureNonNull("(findAppropriateSplit) listOverlaps is null", listOverlaps);
        
        if (listOverlaps.isEmpty()||listNode1.isEmpty()||listNode2.isEmpty()) {
            throw new IllegalArgumentException("impossible to find solution with  empty list of element");
        }

        for (int i = 0, s = listOverlaps.size(); i < s; i++) {
            final List<Bound> testn1 = new ArrayList<Bound>(listNode1);
            final List<Bound> testn2 = new ArrayList<Bound>(listNode2);
            for (int choix = 0; choix < 2; choix++) {
                for (int j = 0; j < i + choix; j++) {
                    testn1.add(listOverlaps.get(j));
                }
                for (int k = i + choix; k < s; k++) {
                    testn2.add(listOverlaps.get(k));
                }

                final Rectangle2D overlaps = getEnveloppeMin(testn1).createIntersection(getEnveloppeMin(testn2));

                if (overlaps.getWidth() * overlaps.getHeight() <= 0) {
                    return i + choix;
                }
            }
        }
        return -1;
    }
    
    /**Define globally enveloppe of some {@code Bound}.
     * 
     * @param listB {@code Bound} list.
     * @throws IllegalArgumentException if listB is empty.
     * @throws IllegalArgumentException if listB is null.
     * @return Rectangle2D globally enveloppe.
     */
    public Rectangle2D getEnveloppeMin(List<Bound> listB) {
        ArgumentChecks.ensureNonNull("impossible to find enveloppe min with null param list", listB);
        Rectangle2D rect = new Rectangle2D.Double();

        if (listB.isEmpty()) {
            throw new IllegalArgumentException("impossible to find enveloppe from empty list");
        } else {
            Rectangle2D rectTemp = listB.get(0).getBoundary().getBounds2D();

            double x1 = rectTemp.getMinX();
            double y1 = rectTemp.getMinY();
            double x2 = x1;
            double y2 = y1;

            for (Bound boun : listB) {
                rectTemp = boun.getBoundary().getBounds2D();
                double xTemp = rectTemp.getMinX();
                double yTemp = rectTemp.getMinY();
                double x1Temp = rectTemp.getMaxX();
                double y1Temp = rectTemp.getMaxY();

                x1 = (xTemp < x1) ? xTemp : x1;
                y1 = (yTemp < y1) ? yTemp : y1;
                x2 = (x1Temp > x2) ? x1Temp : x2;
                y2 = (y1Temp > y2) ? y1Temp : y2;
            }
            rect.setFrameFromDiagonal(x1, y1, x2, y2);
        }
        return rect;
    }
    
    /**Find furthest rectangle corner from another point. 
     * 
     * @param ptRef Point2D, reference to find furthest corner. 
     * @param rect Rectangle2D, reference to find furthest corner.
     * @return Point2D which is furthest {@code Rectangle2D} corner.
     */
    public Point2D getFurthestCorner(Point2D ptRef, Rectangle2D rect){
        ArgumentChecks.ensureNonNull("impossible to find furthest corner with rect null", rect);
        ArgumentChecks.ensureNonNull("(getFurthestCorner) reference point is null", ptRef);
        
        final double xMin = rect.getMinX();
        final double yMin = rect.getMinY();
        final double rWidth = rect.getWidth();
        final double rHeight = rect.getHeight();
        
        final Point2D cornerMin      = new Point2D.Double(xMin, yMin);
        final Point2D cornerMax      = new Point2D.Double(xMin+rWidth, yMin+rHeight);
        final Point2D cornerXMinYMax = new Point2D.Double(xMin, yMin+rHeight);
        final Point2D cornerXMaxYMin = new Point2D.Double(xMin+rWidth, yMin);
        final List<Point2D> lpt = new ArrayList<Point2D>(Arrays.asList(cornerMin, cornerMax, cornerXMaxYMin, cornerXMinYMax));
        
        double distRef = distanceBetweenTwoPoint(ptRef, cornerMin);
        int index = 0;
        for(int i = 1; i<lpt.size(); i++){
            double distTemp = distanceBetweenTwoPoint(ptRef, lpt.get(i));
            if(distTemp>distRef){
                index = i;
                distRef = distTemp;
            }
        }
        return lpt.get(index);
    }
    
    /**
     * @return {@code double} area of Node.
     */
    public double getArea(){
        return this.getWidth()*this.getHeight();
    }
    
    /**Compute euclidean distance between two {@code Point2D}.
     * 
     * @param pt1
     * @param pt2
     * @return distance between pt1, pt2.
     */
    public double distanceBetweenTwoPoint(Point2D pt1, Point2D pt2){
        return Math.hypot(Math.abs(pt2.getX()-pt1.getX()), Math.abs(pt2.getY()-pt1.getY()));
    }
    
    /**Find rectangle2D parameter centroid.
     * 
     * @param rect
     * @return {@code Point2D} which is rect centroid.
     */
    public Point2D getCentroid(Rectangle2D rect){
        return new Point2D.Double((rect.getMinX()+rect.getMaxX())/2, (rect.getMinY()+rect.getMaxY())/2);
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public Rectangle2D getBoundary(){
        return this.getBounds2D();
    }
}
