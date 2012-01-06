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
public class CoupleNode {
    private Node nodeA;
    private Node nodeB;

    public CoupleNode(Node nodeA, Node nodeB) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
    }

    public Node getNodeA(){
        return nodeA;
    }

    public Node getNodeB(){
        return nodeB;
    }
    
    /**
     * @return perimeter of some of each {@code Shape} from {@code Shape} couple.
     */
    public double getPerimeter(){
        Rectangle2D rectA = nodeA.getBounds2D();
        Rectangle2D rectB = nodeB.getBounds2D();
        return 2*((rectA.getWidth()+rectA.getHeight())+(rectB.getWidth()+rectB.getHeight()));
    }
    
    /**
     * @return area of overlaps between two {@code Shape} from couple.
     */
    public double getOverlapsArea(){
        Rectangle2D rectA = nodeA.getBounds2D();
        Rectangle2D rectB = nodeB.getBounds2D();
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
        Rectangle2D rectA = nodeA.getBounds2D();
        Rectangle2D rectB = nodeB.getBounds2D();
        return rectA.intersects(rectB);
    }
}
