/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

/**Create type B Object couple.
 * 
 * @author Rémi Maréchal (Geomatys).
 */
public interface Couple<B> {
    /**
     * @return Object 1.
     */
    B getObject1();
    
    /**
     * @return Object2.
     */
    B getObject2();
    
    /**
     * @return two object perimeter.
     */
    double getPerimeter();
    
    /**
     * @return true if the two object intersect them.
     */
    boolean intersect();
    
    /**
     * @return distance between two objects centroids.
     */
    double getDistance();
}
