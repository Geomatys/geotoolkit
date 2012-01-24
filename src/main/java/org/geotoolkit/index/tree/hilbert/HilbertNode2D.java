/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.hilbert;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.index.tree.Node2D;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeUtils;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.converter.Classes;

/**Create an appropriate {@code Node2D} to {@code HilbertNode2D}.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class HilbertNode2D extends Node2D{

    /**Create HilbertNode2D.
     * 
     * @param tree pointer on Tree.
     * @param parent pointer on parent Node2D.
     * @param hilbertOrder currently Node2D Hilbert order.
     * @param children sub {@code Node2D}.
     * @param entries {@code List<Shape>} to add in this node. 
     */
    public HilbertNode2D(Tree tree, Node2D parent, int hilbertOrder, List<Node2D> children, List<Shape> entries) {
        super(tree, parent, children, null);
        ArgumentChecks.ensurePositive("hilbertOrder", hilbertOrder);
        setUserProperty("isleaf", false);
        if(children==null){
            setUserProperty("isleaf", true);
            setUserProperty("centroids", new ArrayList<Point2D>());
            setUserProperty("cells", new ArrayList<Node2D>());
            Rectangle2D rect = TreeUtils.getEnveloppeMin(entries).getBounds2D();
            HilbertRTree.createBasicHB(this, hilbertOrder, rect);
            for(Shape sh : entries){
                HilbertRTree.insertNode(this, sh);
            }
        }
    }

    /**
     * {@inheritDoc}. 
     */
    @Override
    public boolean isEmpty() {
        List<Node2D> lC = (List<Node2D>)getUserProperty("cells");
        boolean empty = true;
        for(Node2D hc : lC){
            if(!hc.isEmpty()){
                empty = false;
                break;
            }
        }
        return getChildren().isEmpty() && empty;
    }

    
    public Rectangle2D getBound(){
        return this.boundary.getBounds2D();
    }
    
    public void setBound(Rectangle2D rect){
        this.boundary = rect;
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    protected void calculateBounds() {
        if((Boolean)getUserProperty("isleaf")){
            List<Shape> lS = new ArrayList<Shape>();
            for(Node2D nod : (List<Node2D>)getUserProperty("cells")){
                addBound(nod.getBoundary());
            }
            HilbertRTree.searchHilbertNode(this, getBound(), lS);
            HilbertRTree.createBasicHB(this, (Integer)getUserProperty("hilbertOrder"), getBound());
            for(Shape sh : lS){
                HilbertRTree.chooseSubtree(this, sh).getEntries().add(sh);
            }
        }else{
            for(Node2D nod : getChildren()){
                addBound(nod.getBoundary());
            }
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean isLeaf() {
        return (Boolean)getUserProperty("isleaf");
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean isFull() {
        if((Boolean)getUserProperty("isleaf")){
            for(Node2D n2d : (List<Node2D>)getUserProperty("cells")){
                if(!n2d.isFull()){
                    return false;
                }
            }
            return true;
        }
        return getChildren().size()>=getTree().getMaxElements();
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public String toString() {
        final Collection col = new ArrayList((List<Node2D>)getUserProperty("cells"));
        col.addAll(getChildren());
        String strparent =  (getParent() == null)?"null":String.valueOf(getParent().hashCode()); 
        return Trees.toString(Classes.getShortClassName(this)+" : "+this.hashCode()+" parent : "+strparent+" isleaf : "+((Boolean)getUserProperty("isleaf")), col);
    }
}
