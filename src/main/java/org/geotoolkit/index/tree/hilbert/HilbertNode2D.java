/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.hilbert;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.geotoolkit.gui.swing.tree.Trees;
import org.geotoolkit.index.tree.Node2D;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.TreeUtils;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.converter.Classes;

/**
 *
 * @author rmarech
 */
public class HilbertNode2D extends Node2D{

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

    @Override
    public boolean isLeaf() {
        return (Boolean)getUserProperty("isleaf");
    }
    
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
    
    @Override
    public String toString() {
        final Collection col = new ArrayList((List<Node2D>)getUserProperty("cells"));
        col.addAll(getChildren());
        String strparent =  (getParent() == null)?"null":String.valueOf(getParent().hashCode()); 
        return Trees.toString(Classes.getShortClassName(this)+" : "+this.hashCode()+" parent : "+strparent+" isleaf : "+((Boolean)getUserProperty("isleaf")), col);
    }
    
    
}
