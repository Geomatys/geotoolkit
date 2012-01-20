/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.hilbert;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.index.tree.Node2D;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeUtils;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.converter.Classes;

/**
 *
 * @author rmarech
 */
public class HilbertNode2D extends Node2D{

    private int hilbertOrder;
    int[][] tabHV;
    boolean isleaf;

    public HilbertNode2D(Tree tree, Node2D parent, int hilbertOrder, List<Node2D> children, List<Shape> entries) {
        super(tree, parent, children, null);
        ArgumentChecks.ensurePositive("hilbertOrder", hilbertOrder);
        isleaf = false;
        if(children==null){
            isleaf=true;
            setUserProperty("centroids", new ArrayList<Point2D>());
            setUserProperty("cells", new ArrayList<Node2D>());
            Rectangle2D rect = TreeUtils.getEnveloppeMin(entries).getBounds2D();
            createBasicHB(hilbertOrder, rect);
            for(Shape sh : entries){
                HilbertRTree.insertNode(this, sh);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        List<Node2D> lC = (List<Node2D>)getUserProperty("cells");
        boolean empty = true;
        for(Node2D hc : lC){
            if(!hc.isEmpty()){
                empty = false;
                break;
            }
        }
        return getChildren().isEmpty() && empty;
    }

    @Override
    protected void calculateBounds() {
        if(isleaf){
            for(Node2D nod : ((List<Node2D>)getUserProperty("cells"))){
                addBound(nod.getBoundary());
            }
            List<Shape> lS = new ArrayList<Shape>();
            HilbertRTree.searchHilbertNode(this, getBound(), lS);
            createBasicHB(hilbertOrder, getBound());
            for(Shape sh : lS){
                HilbertRTree.chooseSubtree(this, sh).getEntries().add(sh);
            }
            
        }else{
            for(Node2D nod : getChildren()){
                addBound(nod.getBoundary());
            }
        }
    }
    
    public Rectangle2D getBound(){
        return super.boundary.getBounds2D();
    }
    
    public void setBound(Rectangle2D bound){
        this.boundary = bound;
    }

    @Override
    public boolean isLeaf() {
        return isleaf;
    }
    public void setLeaf(boolean leaf){
        this.isleaf = leaf;
    }
    
    @Override
    public boolean isFull() {
        if(!isLeaf()){
            return getChildren().size()>=getTree().getMaxElements();
        }
        List<Node2D> ln = (List<Node2D>)getUserProperty("cells");
        for(Node2D n2d : ln){
            if(!n2d.isFull()){
                return false;
            }
        }
        return true;
    }
    
    private static Node2D createCell(Tree tree, HilbertNode2D parent, Point2D centroid, int hilbertValue, List<Shape>entries){
        Node2D nod2d = new Node2D(tree, parent, null, entries);
        nod2d.setUserProperty("hilbertValue", hilbertValue);
        nod2d.setUserProperty("centroid", centroid);
        return nod2d;
    }
    
    /**Create a conform Hilbert leaf and define Hilbert curve in fonction of {@code indice}.
     * @throws IllegalArgumentException if indice < 0.
     * @param indice Hilber order ask by user. 
     */
    public void createBasicHB(int indice, Rectangle2D bound) {
        ArgumentChecks.ensurePositive("impossible to create Hilbert Curve with negative indice", indice);

        List<Point2D> listOfCentroidChild = (List<Point2D>)getUserProperty("centroids");
        listOfCentroidChild.clear();
        this.hilbertOrder = indice;
        setBound(bound);
        List<Node2D> listN   = (List<Node2D>)getUserProperty("cells");
        listN.clear();
//        isleaf = true;
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
                    createHB(this);
                }
            }

            int dim = (int) Math.pow(2, hilbertOrder);
            tabHV   = new int[dim][dim];
            
            for (int i = 0, s = listOfCentroidChild.size(); i < s; i++) {
                Point2D ptCTemp = listOfCentroidChild.get(i);
                int[] tabTemp = getHilbCoord(ptCTemp, bound, indice);
                tabHV[tabTemp[0]][tabTemp[1]] = i;
                listN.add(createCell(getTree(), this, ptCTemp,i , null));
            }
        } else {
            listOfCentroidChild.add(new Point2D.Double(bound.getCenterX(), bound.getCenterY()));
            this.hilbertOrder = 0;
            listN.add(createCell(getTree(), this, listOfCentroidChild.get(0), 0, null));
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
    private void createHB(final HilbertNode2D hl) {

        ArgumentChecks.ensureNonNull("impossible to increase hilbert order", hl);
        if (hl.getHilbertOrder() > ((HilbertRTree) hl.getTree()).getHilbertOrder()) {
            throw new IllegalArgumentException("hilbert order is larger than hilbertRTree hilbert order");
        }

        final List<Point2D> listOfCentroidChild = (List<Point2D>)hl.getUserProperty("centroids");
        final List<Point2D> lPTemp  = new ArrayList<Point2D>(listOfCentroidChild);
        List<Point2D> lPTemp2       = new ArrayList<Point2D>(lPTemp);
        final Rectangle2D bound     = hl.getBound();
        final Point2D centroid      = new Point2D.Double(bound.getCenterX(), bound.getCenterY());
        final double centreX        = centroid.getX();
        final double centreY        = centroid.getY();
        final double quartWidth     = bound.getWidth() / 4;
        final double quartHeight    = bound.getHeight() / 4;

        listOfCentroidChild.clear();
        final AffineTransform mt1   = new AffineTransform(1, 0, 0, 1, -centreX, -centreY);
        final AffineTransform rot1  = new AffineTransform();
        final AffineTransform mt21  = new AffineTransform(1 / quartWidth, 0, 0, 1 / quartHeight, 0, 0);
        final AffineTransform mt22  = new AffineTransform(quartWidth, 0, 0, quartHeight, 0, 0);
        final AffineTransform mt2   = new AffineTransform();
        final AffineTransform mt3   = new AffineTransform(1 / 2.0, 0, 0, 1 / 2.0, 0, 0);

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
                listOfCentroidChild.add(mt2.transform(pt, null));
            }
        }
    }
    
    public int getHilbertOrder(){
        return this.hilbertOrder;
    }
    
    public void setHilbertOrder(int hilbertOrder){
        this.hilbertOrder = hilbertOrder;
    }
    
    /**Find {@code Point2D} Hilbert coordinate from this Node.
     * 
     * @param pt {@code Point2D} 
     * @throws IllegalArgumentException if param "pt" is out of this node boundary.
     * @throws IllegalArgumentException if param pt is null.
     * @return int[] table of lenght 2 which contains two coordinate.
     * @see #getHVOfEntry(org.geotoolkit.utilsRTree.Entry)
     */
    private static int[] getHilbCoord(final Point2D pt, final Rectangle2D rect, final int hilbertOrder) {
        ArgumentChecks.ensureNonNull("impossible to define Hilbert coordinate with null point", pt);
        if (!rect.contains(pt)) {
            throw new IllegalArgumentException("Point is out of this node boundary");
        }
        final double divX = rect.getWidth()  / (Math.pow(2, hilbertOrder));
        final double divY = rect.getHeight() / (Math.pow(2, hilbertOrder));
        final int hx = (int) (Math.abs(pt.getX() - rect.getMinX()) / divX);
        final int hy = (int) (Math.abs(pt.getY() - rect.getMinY()) / divY);
        return new int[]{hx, hy};
    }

    /**Find Hilbert order of an entry from this HilbertLeaf.
     * 
     * @param entry where we looking for her Hilbert order.
     * @throws IllegalArgumentException if param "entry" is out of this node boundary.
     * @throws IllegalArgumentException if entry is null.
     * @return int the entry Hilbert order.
     */
    public int getHVOfEntry(final Shape entry) {
        ArgumentChecks.ensureNonNull("impossible to define Hilbert coordinate with null entry", entry);
        Rectangle2D recEnt = entry.getBounds2D();
        Point2D ptCE = new Point2D.Double(recEnt.getCenterX(), recEnt.getCenterY());
        
        if (!getBound().contains(ptCE)) {
            throw new IllegalArgumentException("entry is out of this node boundary");
        }
        int[] hCoord = getHilbCoord(ptCE, getBound(), getHilbertOrder());
        return tabHV[hCoord[0]][hCoord[1]];
    }
    
    @Override
    public String toString() {
        final Collection col = new ArrayList((List<Node2D>)getUserProperty("cells"));
        col.addAll(getChildren());
        String strparent =  (getParent() == null)?"null":String.valueOf(getParent().hashCode()); 
        return Trees.toString(Classes.getShortClassName(this)+" : "+this.hashCode()+" parent : "+strparent+" isleaf : "+isleaf, col);
    }
    
    
}
