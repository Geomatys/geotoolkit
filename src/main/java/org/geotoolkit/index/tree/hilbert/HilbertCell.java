/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hilbeRTree;

import com.mycompany.utilsRTree.Entry;
import com.mycompany.utilsRTree.Node;
import com.mycompany.utilsRTree.Rtree;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.gui.swing.tree.Trees;

/**
 *
 * @author rmarech
 */
public class HilbertCell extends Node{
    
    List<Entry> listE;
    Point2D centroid;
    int hilbertValue;

    public HilbertCell(Rtree tree, Point2D centroid, int hilbertValue) {
        this.tree = tree;
        this.centroid = centroid;
        this.hilbertValue = hilbertValue;
        this.listE = new ArrayList<Entry>();
    }
    
    
    @Override
    public boolean isFull() {
        return listE.size()>=tree.getMaxElements();
    }


    @Override
    public void trim() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void search(Rectangle2D regionSearch, List<Entry> result) {
//        System.out.println("on arrive jusk la???!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        if(this.getBounds2D().intersects(regionSearch)){
            for(Entry ent : listE){
                if(regionSearch.intersects(ent.getBoundary())){
                    result.add(ent);
                }
            }
        }
    }

    public int getHilberValue(){
        return hilbertValue;
    }
    
    public void addWithReSize(Entry entry){
        listE.add(entry);
        reSize();
    }
    
    public List<Entry> getElements(){
        return listE;
    }
    
    @Override
    public void paint(Graphics2D g) {
        
        for(Entry ent : listE){
            ent.paint(g);
        }
        g.setColor(Color.green);
        g.draw(this);
    }

    @Override
    public int getNbElements() {
        return listE.size();
    }

    @Override
    public List<Node> split() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean delete(Entry entry) {
        final boolean success = listE.remove(entry);
        if(success){
            reSize();
        }
        return success;
    }

    @Override
    public void insert(Entry entry) {
        listE.add(entry);
        reSize();
    }

    @Override
    public void reSize() {
        if(listE.isEmpty()){
            this.setFrameFromCenter(centroid, centroid);
        }else{
            this.setRect(getEnveloppeMin(new ArrayList<Bound>(listE)));
        }
    }

    @Override
    public double getDeadSpace() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return Trees.toString("HCell", listE);
    }
    
    
}
