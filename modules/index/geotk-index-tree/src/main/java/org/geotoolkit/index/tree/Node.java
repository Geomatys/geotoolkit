/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.util.List;

/**Create a {@code Node}.
 *
 * <B> : Entries type stocked in {@code Node}.
 * 
 * @author Rémi Marechal (Geomatys).
 */
public interface Node<N extends Node<N,B>, B> {
    
    List<N> getChildren();
    
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
     * <blockquote><font size=-1>
     * <strong>NOTE: if boundary is null, method re-compute all subnode boundary.</strong> 
     * </font></blockquote>
     * @return boundary.
     */
    B getBoundary();
    
    /**
     * @return entries.
     */
    List<B> getEntries();

    /**
     * @return {@code Node2D} parent pointer.
     */
    N getParent();

    /**
     * @return {@code Tree} pointer.
     */
    Tree getTree();
}
