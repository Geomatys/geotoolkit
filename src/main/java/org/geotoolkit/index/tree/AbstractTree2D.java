/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.awt.Shape;
import java.util.List;
import org.geotoolkit.util.converter.Classes;

/**
 *
 * @author rmarech
 */
public abstract class AbstractTree2D implements Tree<Shape>{
    
    private Node2D root;
    private final int maxElements;

    public AbstractTree2D(int maxElements) {
        this.maxElements = maxElements;
    }

    public int getMaxElements() {
        return maxElements;
    }
       
    public Node2D getRoot(){
        return root;
    }
    
    protected void setRoot(Node2D root){
        this.root = root;
    }
    
    /**Create a adapted Node for this tree.
     * 
     * @param tree pointer on tree.
     * @param parent Node parent.
     * @param children subNode.
     * @param entries 
     * @return Node2D
     */
    public static Node2D createNode(Tree tree, Node2D parent, List<Node2D> children, List<Shape> entries) {
        return new Node2D(tree, parent, children, entries);
    }
    
    //protected abstract Node2D createNode(Tree tree, Node2D parent, List<Node2D> children, List<Entry2D<Object>>entries);

    @Override
    public String toString() {
        return Classes.getShortClassName(this)+"\n"+getRoot();
    }
}
