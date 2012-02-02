/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.awt.Shape;
import java.util.List;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.converter.Classes;

/**Create a "generic" 2D Tree.
 *
 * @author Rémi Marechal (Geomatys).
 * @author Johann Sorel  (Geomatys).
 */
public abstract class AbstractTree2D implements Tree<Shape> {

    private Node2D root;
    private final int maxElements;

    /**Create 2 Dimension Tree
     * 
     * @param maxElements max value permit for each tree cells.
     * @throws IllegalArgumentException if maxElements is out of required limits.
     */
    public AbstractTree2D(int maxElements) {
        ArgumentChecks.ensureStrictlyPositive("Create Tree : maxElements", maxElements);
        this.maxElements = maxElements;
    }

    /**
     * @return max elements permitted by tree cells. 
     */
    public int getMaxElements() {
        return maxElements;
    }

    /**
     * {@inheritDoc}
     */
    public Node2D getRoot() {
        return root;
    }

    /**Affect a new tree trunk.
     * 
     * @param root 
     */
    public void setRoot(Node2D root) {
        this.root = root;
    }

//    /**Create a adapted {@code Node2D} in 2 dimension for this tree.
//     * 
//     * @param tree pointer on tree.
//     * @param parent pointer on {@code Node2D} parent.
//     * @param children subNode.
//     * @param entries 
//     * @return Node2D
//     */
//    public static Node2D createNode(final Tree tree, final Node2D parent, final List<Node2D> children, final List<Shape> entries) {
//        return new Node2D(tree, parent, children, entries);
//    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public String toString() {
        return Classes.getShortClassName(this) + "\n" + getRoot();
    }
}
