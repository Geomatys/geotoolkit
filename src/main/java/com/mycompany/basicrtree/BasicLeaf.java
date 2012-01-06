/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.basicrtree;

import com.mycompany.utilsRTree.Entry;
import com.mycompany.utilsRTree.Node;
import com.mycompany.utilsRTree.Rtree;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.util.collection.UnmodifiableArrayList;

/**Create a Basic R-Tree leaf.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class BasicLeaf extends Node{

    private List<Entry> lEntry;
    
    /**Create a virgin tree leaf.
     * 
     * @param tree pointer on tree (BasicRTree). 
     */
    public BasicLeaf(Rtree tree){
        lEntry = new ArrayList<Entry>();
        this.tree = tree;
        reSize();
    }
    
    /**Rtree leaf copy constructor.
     * 
     * @param tree pointer on tree (BasicRTree).
     * @param lif 
     */
    public BasicLeaf(Rtree tree, BasicLeaf lif){
        this.tree = tree;
        lEntry = new ArrayList<Entry>(lif.getElements());
        reSize();
    }
    
    /**Create basic leaf.
     * 
     * @param tree pointer on tree (BasicRTree).
     * @param entry entries table.
     */
    public BasicLeaf(Rtree tree, Entry ...entry){
        this.tree = tree;
        lEntry = new ArrayList<Entry>(Arrays.asList(entry));
        reSize();
    }
    
    /**Create basic leaf.
     * 
     * @param tree pointer on tree (BasicRTree).
     * @param lEntry entries list.
     */
    public BasicLeaf(Rtree tree, List<Entry> lEntry) {
        this.lEntry = new ArrayList<Entry>(lEntry);
        this.tree = tree;
        reSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Graphics2D g){
        g.setColor(Color.BLACK);
        for(Entry ent : lEntry){
            ent.paint(g);
        }
        g.setColor(Color.GREEN);
        g.draw(this);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void insert(Entry entry){
        lEntry.add(entry);
        reSize();
    }
    
    /**Find {@code Shape} which intersect regionSearch.
     * 
     * @param regionSearch Area of search.
     * @param result List of Shape which intersect regionSearch.
     */
    @Override
    public void search(Rectangle2D regionSearch, List<Entry> result) {
        for(Entry ent : lEntry){
            if(ent.getBoundary().intersects(regionSearch)){
                result.add(ent);
            }
        }
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean delete(Entry entry) {
        if(!this.getBounds2D().intersects(entry.getBoundary())){
            return false;
        }
        final boolean removed = lEntry.remove(entry);
        if(removed){
            reSize();
            trim();
            return true;
        }
        return true;
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public double getDeadSpace(){
        double areaElmt = 0;
        for(Entry ent : lEntry){
            Rectangle2D recTemp = ent.getBoundary();
            areaElmt += (recTemp.getWidth()*recTemp.getHeight());
        }
        return this.getArea()-areaElmt;
    }
   
    /**
     * {@inheritDoc} 
     */
    @Override
    public int getNbElements(){
        return lEntry.size();
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public double getArea() {
        return this.getWidth()*this.getHeight();
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public List<Node> split(){
        if(getNbElements()<2){
            throw new IllegalArgumentException("not enought elements within "+this+" to split.");
        }
        List<Entry> ls = getElements();
        Entry s1 = ls.get(0), s2  = ls.get(1);
        BasicLeaf lfTemp;
        double refValue = 0;
        double tempValue = 0;
        int index1 = 0;
        int index2 = 0;
        
        switch(((BasicRTree)tree).getSplitCase()){
            case LINEAR : {
                for(int i=0;i<ls.size()-1;i++){
                    for(int j = i+1;j<ls.size();j++){
                        tempValue = getDistanceBetweenTwoShape(ls.get(i).getBoundary(), ls.get(j).getBoundary());
                        if(tempValue>refValue){
                            s1 = ls.get(i);
                            s2 = ls.get(j);
                            index1 = i;
                            index2 = j;
                            refValue = tempValue;
                        }
                    }
                }
            }break;
                
            case QUADRATIC : {
                for(int i=0;i<ls.size()-1;i++){
                    for(int j = i+1;j<ls.size();j++){
                        lfTemp = new BasicLeaf(this.tree, ls.get(i),ls.get(j));
                        tempValue = lfTemp.getDeadSpace();
                        if(tempValue>refValue){
                            s1 = ls.get(i);
                            s2 = ls.get(j);
                            index1 = i;
                            index2 = j;
                            refValue = tempValue;
                        }
                    }
                }
                
            }break;
        }
        
        ls.remove(Math.max(index1, index2));
        ls.remove(Math.min(index1, index2));
        
        BasicLeaf l1Temp, l2Temp;
        BasicLeaf result1 = new BasicLeaf(this.tree, s1);
        BasicLeaf result2 = new BasicLeaf(this.tree, s2);
        
        for(Entry ent : ls){
            l1Temp = new BasicLeaf(this.tree, s1,ent);
            l2Temp = new BasicLeaf(this.tree, s2, ent);
            
            if(l1Temp.getArea()<l2Temp.getArea()){
                if(result1.getNbElements()<=tree.getMaxElements()/2&&result2.getNbElements()>tree.getMaxElements()/2){
                    result1.insert(ent);
                }else if(result2.getNbElements()<=tree.getMaxElements()/2&&result1.getNbElements()>tree.getMaxElements()/2){
                    result2.insert(ent);
                }else{
                    result1.insert(ent);
                }
            }else if(l1Temp.getArea() == l2Temp.getArea()){
                if(l1Temp.getNbElements()<l2Temp.getNbElements()){
                    result1.insert(ent);
                }else{
                    result2.insert(ent);
                }
            }else{
                if(result1.getNbElements()<=tree.getMaxElements()/2&&result2.getNbElements()>tree.getMaxElements()/2){
                    result1.insert(ent);
                }else if(result2.getNbElements()<=tree.getMaxElements()/2&&result1.getNbElements()>tree.getMaxElements()/2){
                    result2.insert(ent);
                }else{
                    result2.insert(ent);
                }
            }
        }
        return UnmodifiableArrayList.wrap((Node)result1, (Node)result2);
    }
    
    
    /**Distance returned is compute between boundary gravity center of each {@code Shape}.
     * 
     * @param n1 first Shape.
     * @param n2 second Shape.
     * @return Euclidean distance between two Shape.
     */
    public double getDistanceBetweenTwoShape(final Shape sh1, final Shape sh2){
        if(sh1.equals(sh2)){
            return 0;
        }
        
        final Rectangle2D rn1 = sh1.getBounds2D();
        final Rectangle2D rn2 = sh2.getBounds2D();
        final double x1 = (rn1.getMinX()+rn1.getMaxX())/2;
        final double y1 = (rn1.getMinY()+rn1.getMaxY())/2;
        final double x2 = (rn2.getMinX()+rn2.getMaxX())/2;
        final double y2 = (rn2.getMinY()+rn2.getMaxY())/2;
        return Math.hypot(Math.abs(x1-x2), Math.abs(y1-y2));
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public double getPerimeter() {
        return 2*(this.getWidth()+this.getHeight());
    }

    public List<Entry> getElements() {
        return new ArrayList<Entry>(lEntry);
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public void reSize() {
        
        if(lEntry.isEmpty()){
            this.setRect(0, 0, 0, 0);
        }else{
            Rectangle2D rectTemp = lEntry.get(0).getBoundary();
            if(lEntry.size() == 1){
                this.setRect(rectTemp);
            }else{

                double x1 = rectTemp.getMinX();
                double y1 = rectTemp.getMinY();
                double x2 = x1;
                double y2 = y1;

                for(Entry ent : lEntry){
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void trim() {
        List<Entry> l = new ArrayList<Entry>();
        if(lEntry.size()<tree.getMaxElements()){
            l.addAll(lEntry);
            lEntry.clear();
        }
        
        if(lEntry.isEmpty() && parent != null){
            parent.trim();
        }
        
        for(Entry ent : l){
            tree.insert(ent);
        }
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public String toString() {
        return Trees.toString("Leaf ", lEntry);
    }
    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean isFull() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
