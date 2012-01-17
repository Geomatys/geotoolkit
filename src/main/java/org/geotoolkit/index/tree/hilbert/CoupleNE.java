/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.utilsRTree;

import java.awt.geom.Rectangle2D;

/**
 *
 * @author marechal
 */
public class CoupleNE {
    private Node node;
    private Entry entry;

    public CoupleNE(Node node, Entry entry) {
        this.node = node;
        this.entry = entry;
    }

    public Node getNode(){
        return node;
    }

    public Entry getEntry(){
        return entry;
    }
    
    /**
     * @return perimeter of some of each {@code Shape} from {@code Shape} couple.
     */
    public double getPerimeter(){
        Rectangle2D rectA = node.getBounds2D();
        Rectangle2D rectB = entry.getBoundary();
        return 2*((rectA.getWidth()+rectA.getHeight())+(rectB.getWidth()+rectB.getHeight()));
    }
    
    /**
     * @return area of overlaps between two {@code Shape} from couple.
     */
    public double getOverlapsArea(){
        Rectangle2D rectA = node.getBounds2D();
        Rectangle2D rectB = entry.getBoundary();
        if(!rectA.intersects(rectB)){
            return 0;
        }
        Rectangle2D rectO = rectA.createIntersection(rectB);
        return rectO.getWidth()*rectO.getHeight();
    }
    
    /**
     * @return {@code true} if boundary box from two shape intersect them. 
     */
    public boolean intersect(){
        Rectangle2D rectA = node.getBounds2D();
        Rectangle2D rectB = entry.getBoundary();
        return rectA.intersects(rectB);
    }
}
