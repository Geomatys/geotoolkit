/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

/**
 *
 * @author rmarech
 */
public interface Couple<B> {
    
    B getObject1();
    
    B getObject2();
    
    double getPerimeter();
    
    boolean intersect();
    
    double getDistance();
}
