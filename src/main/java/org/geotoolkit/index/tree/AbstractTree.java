/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import org.geotoolkit.util.converter.Classes;

/**
 *
 * @author rmarech
 */
public abstract class AbstractTree<B,V> implements Tree<B,V>{
    
    private Node<B,V> root;
    
    public Node<B,V> getRoot(){
        return root;
    }
    
    protected void setRoot(Node<B,V> root){
        this.root = root;
    }

    @Override
    public String toString() {
        return Classes.getShortClassName(this)+"\n"+getRoot();
    }
}
