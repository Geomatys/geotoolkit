/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.awt.Shape;
import java.util.List;

/**
 *
 * @author rmarech
 */
public interface Node {
    
    List<? extends Node> getChildren();
    
    /**A leaf is a {@code Node2D} at extremity of {@code Tree} which contains only entries.
     * 
     * @return true if it is  a leaf else false (branch).
     */
    boolean isLeaf();
    
    /**
     * @return true if {@code Node2D} contains nothing else false.
     */
    boolean isEmpty();
    
    /**
     * @return true if node elements number equals or overflow max elements
     *         number autorized by {@code Tree} else false. 
     */
    boolean isFull();
    
    /**
     * @return entries.
     */
    List<Shape> getEntries();

    /**
     * @return {@code Node2D} parent pointer.
     */
    Node getParent();

    /**
     * @return {@code Tree} pointer.
     */
    Tree getTree();
}
