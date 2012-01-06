/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.starRTree;

import com.mycompany.utilsRTree.CoupleNE;
import com.mycompany.utilsRTree.CoupleNode;
import com.mycompany.utilsRTree.Entry;
import com.mycompany.utilsRTree.Node;
import com.mycompany.utilsRTree.Rtree;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.util.collection.UnmodifiableArrayList;


/**Create tree leaf {@code Node}.
 *
 * @author Marechal Rémi (Geomatys).
 */
public class StarLeaf extends Node{

    private final ArrayList<Entry> listE = new ArrayList<Entry>();
    private boolean insertAgain = true;
    
    /**To compare two {@code Shape} from them boundary box x axis coordinate. 
     * @see StarNode#organizeFrom(int) 
     * @author Marechal Remi (Geomatys)
     */
    private static final Comparator<Entry> SHAPE_COMPARATOR_X = new Comparator<Entry>() {

        public int compare(Entry o1, Entry o2) {
            java.lang.Double x1 = new java.lang.Double(o1.getBoundary().getMinX());
            java.lang.Double x2 = new java.lang.Double(o2.getBoundary().getMinX());
            return x1.compareTo(x2);
        }
    };
    
    /**To compare two {@code Shape} from them boundary box y axis coordinate. 
     * @see StarNode#organizeFrom(int) 
     * @author Marechal Remi (Geomatys)
     */
    private static final Comparator<Entry> SHAPE_COMPARATOR_Y = new Comparator<Entry>() {

        public int compare(Entry o1, Entry o2) {
            java.lang.Double y1 = new java.lang.Double(o1.getBoundary().getMinY());
            java.lang.Double y2 = new java.lang.Double(o2.getBoundary().getMinY());
            return y1.compareTo(y2);
        }
    };

    /**To compare two distances between two distincts {@code Shape} and a same {@code Node}. 
     * Distance is Euclidean distance between joint {@code Shape} centroid and {@code Shape} centroid.
     * 
     * <blockquote><font size=-1>
     * <strong>NOTE: to use compare method create CoupleShape where the first param is the joint {@code Shape}.</strong> 
     * </font></blockquote>
     * 
     * @see StarNode#organizeFrom(int) 
     * @author Marechal Remi (Geomatys)
     */
    private static final Comparator<CoupleNE> SHAPE_COMPARATOR_CENTROID = new Comparator<CoupleNE>() {

        public int compare(CoupleNE cn1, CoupleNE cn2) {
            if (!cn1.getNode().getBounds2D().equals(cn2.getNode().getBounds2D())) {
                throw new IllegalArgumentException("you will be able to compare two shapes between the same Node");
            }
            Node ref = (Node) cn1.getNode();
            java.lang.Double c1 = new java.lang.Double(ref.distanceBetweenTwoPoint(ref.getCentroid(), ref.getCentroid(cn1.getEntry().getBoundary())));
            java.lang.Double c2 = new java.lang.Double(ref.distanceBetweenTwoPoint(ref.getCentroid(), ref.getCentroid(cn2.getEntry().getBoundary())));
            return c1.compareTo(c2);
        }
    };
    
    public StarLeaf(Rtree tree, Entry ...entry) {
        this.tree = tree;
        listE.addAll(Arrays.asList(entry));
        reSize();
    }
    
    public StarLeaf(Rtree tree, List<Entry> lE) {
        this.tree = tree;
        this.listE.addAll(lE);
        reSize();
    }
    
    /**Find all {@code Shape} within {@code this Node} which intersect {@code regionSearch} parameter.
     * 
     * @param regionSearch area of search.
     * @param result {@code List} to add result(s).
     */
    @Override
    public void search(Rectangle2D regionSearch, List<Entry> result) {
        for(Entry ent : listE){
            if(ent.getBoundary().intersects(regionSearch)){
                result.add(ent);
            }
        }
    }

   /**Add a {@code Shape} in {@code this Node} tree leaf.
    * 
    * <blockquote><font size=-1>
    * <strong>NOTE: if this leaf is overflowed, find furthest element and reinsert it in tree
    * to avoid unnecessary split.</strong> 
    * </font></blockquote>
    * 
    * @param shape to insert.
    */
    @Override
    public void insert(Entry entry) {
        listE.add(entry);
        if(listE.size()>tree.getMaxElements()&&insertAgain){
            insertAgain = false;
            for(Entry ent : getElementAtMore30PerCent()){
                  tree.insert(ent);
            }
            insertAgain = true;
        }
        reSize();
    }

    /**Delete {@code Shape} if it is find. 
     * 
     * @param shape to delete.
     */
    @Override
    public boolean delete(Entry entry) {
        final boolean success = listE.remove(entry);
        if(success){
            reSize();
            trim();
        }
        return success;
    }

    /**Delete shape at more 30% largest of {@code this Node}.
     * 
     * @return all Shape within subNodes at more 30% largest of {@code this Node}.
     */
    protected List<Entry> getElementAtMore30PerCent(){
        organizeFrom(0);
        List<Entry> lsh = new ArrayList<Entry>();
        for(int i = 0;i<listE.size();i++){
            if(distanceBetweenTwoPoint(getCentroid(), getCentroid(listE.get(i).getBoundary()))
               >Math.max(this.getWidth(), this.getHeight())*0.3){
                lsh.add(listE.get(i));
                listE.remove(i);
            }
        }
        reSize();
        return lsh;
    }
    
    /**
     * Organize all {@code Shape} by differents criterion.
     * 
     * @param index : - 0 : organize all shapes by nearest to furthest between them centroid and this Node centroid.
     *                - 1 : organize all shapes by smallest x value to tallest.
     *                - 2 : organize all shapes by smallest y value to tallest.
     */
    public void organizeFrom(int index){
       
        List<CoupleNE> lcouple = new ArrayList<CoupleNE>(listE.size());
        
        for(Entry ent : listE){
            lcouple.add(new CoupleNE(this, ent));
        }
        
        switch(index){
            case 0 : 
                Collections.sort(lcouple, SHAPE_COMPARATOR_CENTROID);
                listE.clear();
                for(CoupleNE cNE : lcouple){
                    listE.add(cNE.getEntry());
                }
                break;
            
            case 1 : 
                Collections.sort(listE, SHAPE_COMPARATOR_X);
                break;
                
            case 2 : 
                Collections.sort(listE, SHAPE_COMPARATOR_Y);
                break;
        }
    }
    
    @Override
    public void paint(Graphics2D g) {
        for(Entry ent : listE){
            ent.paint(g);
        }
        g.setColor(Color.GREEN);
        g.draw(this);
    }

    @Override
    public void trim() {
        if(getNbElements()<=tree.getMaxElements()/3){
            final List<Entry> lsh = new ArrayList<Entry>(listE);
            listE.clear();
            reSize();
        
            if(listE.isEmpty()&&parent!=null){
                parent.trim();
            }

            for(Entry ent : lsh){
                tree.insert(ent);
            }
            reSize();
        }
    }

    @Override
    public int getNbElements() {
        return listE.size();
    }
    
    @Override
    public List<Node> split() {
        final int size = listE.size();
        if(size<=1){
            throw new IllegalStateException("you can't split Leaf with only one elements");
        }
        
        if(size==2){
            return UnmodifiableArrayList.wrap(
                    (Node)new StarLeaf(tree, listE.get(0)),
                    (Node)new StarLeaf(tree, listE.get(1)));
        }
        return splitAxis(defineSplitAxis());
    }

    /**
     * We can choose axis to split.
     *      - case 1 : to choose x axis split.
     *      - case 2 : to choose y axis split.
     * 
     * @param index choose one or 2
     * @return List of two Node which is split of Node passed in parameter.
     * @throws Exception if try to split leaf with only one element.
     */
    private List<Node> splitAxis(int index) {
        
        /**
         * Its to be in accordance with R*Tree properties.
         * We can, during splitting action, create new Node which contains
         * 33% in minimum of data from origin Node.
         */
        //final int val = tree.getMaxElements()/3;
        final int val2 = tree.getMaxElements()/2;
        
        if(index!=1&&index!=2){
            throw new IllegalArgumentException(" index is not conform !!!");
        }
        
        if(listE.size()<=1){
            throw new IllegalArgumentException("you can't split Leaf with only one elements or lesser");
        }
        
        if(listE.size()==2){
            return UnmodifiableArrayList.wrap((Node)new StarLeaf(tree, listE.get(0)), (Node)new StarLeaf(tree, listE.get(1)));
        }   
        
        organizeFrom(index);
        List<Entry> splitList1 = new ArrayList<Entry>();
        List<Entry> splitList2 = new ArrayList<Entry>();
        
        CoupleNode couNN;
        
        final List<CoupleNode> lSAO = new ArrayList<CoupleNode>();
        final List<CoupleNode> lSSo = new ArrayList<CoupleNode>();
        
        for(int i = val2;i<=listE.size()-val2;i++){
            for(int j = 0;j<i;j++){
                splitList1.add(listE.get(j));
            }
            for(int k =  i;k<listE.size();k++){
                splitList2.add(listE.get(k));
            }
            
            couNN = new CoupleNode(new StarLeaf(tree, splitList1), new StarLeaf(tree, splitList2));
            
            if(couNN.intersect()){
                lSAO.add(couNN);
            }else{
                lSSo.add(couNN);
            }
            splitList1.clear();
            splitList2.clear();
        }
        
        return lSSo.isEmpty()?getMinOverlapsOrPerimeter(lSAO, 0):getMinOverlapsOrPerimeter(lSSo, 1);
    }
    
    
    /**Ajuste size of {@code this Node} from interiors {@code Node} datas.
     * 
     * @param nod to resize;
     */
    @Override
    public void reSize() {
        
        if(listE.isEmpty()){
            this.setRect(java.lang.Double.NaN, java.lang.Double.NaN, java.lang.Double.NaN, java.lang.Double.NaN);
        }else{
            Rectangle2D rectTemp = listE.get(0).getBoundary();
            if(listE.size() == 1){
                this.setRect(rectTemp);
            }else{

                double x1 = rectTemp.getMinX();
                double y1 = rectTemp.getMinY();
                double x2 = x1;
                double y2 = y1;

                for(Entry ent : listE){
                    rectTemp = ent.getBoundary();
                    double xTemp  = rectTemp.getMinX();
                    double yTemp  = rectTemp.getMinY();
                    double x1Temp = rectTemp.getMaxX();
                    double y1Temp = rectTemp.getMaxY();

                    x1  = (xTemp < x1)  ? xTemp  : x1;
                    y1  = (yTemp < y1)  ? yTemp  : y1;
                    x2  = (x1Temp > x2) ? x1Temp : x2;
                    y2  = (y1Temp > y2) ? y1Temp : y2;
                }
                this.setRect(new Rectangle2D.Double(x1, y1, Math.abs(x2-x1), Math.abs(y2-y1)));
            }   
        }
    }

    /**Compute and define which axis to split {@code this Node}.
     * 
     * @return 1 to split in x axis and 2 to split in y axis.
     */
    private int defineSplitAxis(){
        
        final int val = tree.getMaxElements()/3;
        
        double perimX = 0;
        double perimY = 0;
        
        final List<Entry> splitList1 = new ArrayList<Entry>();
        final List<Entry> splitList2 = new ArrayList<Entry>();
        CoupleNode cN;
        for(int index = 1; index<=2;index++){
            
            organizeFrom(index);
            
            for(int i = val;i<=listE.size()-val;i++){
                for(int j = 0;j<i;j++){
                    splitList1.add(listE.get(j));
                }
                for(int k =  i;k<listE.size();k++){
                    splitList2.add(listE.get(k));
                }

               cN = new CoupleNode(new StarLeaf(tree, splitList1), new StarLeaf(tree, splitList2));
                
                switch(index){
                    case 1 : {
                        perimX+=(cN.getPerimeter());
                    }break;
                    
                    case 2 : {
                        perimY+=(cN.getPerimeter());
                    }break;
                }
                
                splitList1.clear();
                splitList2.clear();
            }
        }
        
        if(perimX<=perimY){
            return 1;
        }else{
            return 2;
        }
    }
    
    @Override
    public double getDeadSpace() {
        double area = 0;
        for(Entry ent : listE){
            Rectangle2D rect = ent.getBoundary();
            area+=rect.getWidth()*rect.getHeight();
        }
        return this.getArea()-area;
    }
    
    @Override
    public String toString() {
        return Trees.toString("Leaf ", listE);
    }

    @Override
    public boolean isFull() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
