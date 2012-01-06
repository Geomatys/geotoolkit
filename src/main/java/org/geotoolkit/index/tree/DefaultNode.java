/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.util.ArrayList;
import java.util.Collection;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.converter.Classes;

/**
 *
 * @author rmarech
 */
public class DefaultNode<B,V> implements Node<B,V> {

    private Node<B,V> parent;
    private final Tree<B,V> tree;
    private Collection<Node<B,V>> children;
    private Collection<Entry<B,V>> entries;

    public DefaultNode(Tree<B, V> tree) {
        this(tree, null, null, null);
    }
    
    public DefaultNode(Tree<B, V> tree, Node<B, V> parent, Collection<Node<B, V>> children, Collection<Entry<B, V>> entries) {
        ArgumentChecks.ensureNonNull("tree", tree);
        this.parent = parent;
        this.tree = tree;
        this.children = (children!=null) ? children : new ArrayList<Node<B, V>>();
        this.entries  = (entries!=null)  ? entries  : new ArrayList<Entry<B, V>>();
    }
    
    public Collection<Node<B, V>> getChildren() {
        return children;
    }

    public Collection<Entry<B, V>> getEntries() {
        return entries;
    }

    public Node<B, V> getParent() {
        return parent;
    }

    public Tree<B, V> getTree() {
        return tree;
    }
    
    @Override
    public String toString() {
        final Collection col = new ArrayList(entries);
        col.addAll(children);
        return Trees.toString(Classes.getShortClassName(this), col);
    }
    
}
