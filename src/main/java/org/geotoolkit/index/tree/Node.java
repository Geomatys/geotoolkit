/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.util.Collection;

/**
 *
 * @author rmarech
 */
public interface Node<B,V> {
    
    Tree<B,V> getTree();
    
    Collection<Node<B,V>>  getChildren();
    
    Collection<Entry<B,V>> getEntries();
    
    Node<B,V> getParent();
    
}
