/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author rmarech
 */
public class CoupleShape implements Couple<Shape>{

    private final Shape shape1;
    private final Shape shape2;

    public CoupleShape(Shape shape1, Shape shape2) {
        this.shape1 = shape1;
        this.shape2 = shape2;
    }

    public Shape getObject1() {
        return shape1;
    }

    public Shape getObject2() {
        return shape2;
    }

    public double getPerimeter() {
        final Rectangle2D rectO1 = getObject1().getBounds2D();
        final Rectangle2D rectO2 = getObject2().getBounds2D();
        
        return 2*(rectO1.getWidth() + rectO1.getHeight() + rectO2.getWidth() + rectO2.getHeight());
    }

    public boolean intersect() {
        return getObject1().intersects(getObject2().getBounds2D());
    }

    public double getDistance() {
        return TreeUtils.getDistanceBetweenTwoBound2D(getObject1().getBounds2D(), getObject2().getBounds2D());
    }
    
    public double getOverlaps(){
        Rectangle2D over = getObject1().getBounds2D().createIntersection(getObject2().getBounds2D());
        return over.getWidth()*over.getHeight();
    }
}
