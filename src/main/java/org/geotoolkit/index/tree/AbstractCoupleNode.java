/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

/**
 *
 * @author Rémi Marechal (Geomatys)
 */
public abstract class AbstractCoupleNode<B> implements Couple<Node2D> {
    
    private final Node2D node1;
    private final Node2D node2;

    public AbstractCoupleNode(Node2D node1, Node2D node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    public Node2D getObject1() {
        return node1;
    }

    public Node2D getObject2() {
        return node2;
    }

    protected abstract B getOverlaps();
}
