/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.awt.geom.Rectangle2D;

/**
 *
 * @author rmarech
 */
public class CoupleNode2D implements Couple<Node2D>{

    private final Node2D node1;
    private final Node2D node2;

    public CoupleNode2D(Node2D node1, Node2D node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    public Node2D getObject1() {
        return node1;
    }

    public Node2D getObject2() {
        return node2;
    }

    public double getPerimeter() {
        final Rectangle2D rectO1 = getObject1().getBoundary().getBounds2D();
        final Rectangle2D rectO2 = getObject2().getBoundary().getBounds2D();
        
        return 2*(rectO1.getWidth() + rectO1.getHeight() + rectO2.getWidth() + rectO2.getHeight());
    }

    public boolean intersect() {
        return getObject1().getBoundary().intersects(getObject2().getBoundary().getBounds2D());
    }

    public double getDistance() {
        return TreeUtils.getDistanceBetweenTwoBound2D(getObject1().getBoundary().getBounds2D(), getObject2().getBoundary().getBounds2D());
    }
    
    public double getOverlaps(){
        Rectangle2D over = getObject1().getBoundary().getBounds2D().createIntersection(getObject2().getBoundary().getBounds2D());
        return over.getWidth()*over.getHeight();
    }
}
