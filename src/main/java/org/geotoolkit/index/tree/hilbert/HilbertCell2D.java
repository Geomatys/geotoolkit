/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.hilbert;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.geotoolkit.index.tree.Node2D;
import org.geotoolkit.index.tree.Tree;

/**Create a HilbertCell.
 *
 * @author rmarech
 */
public class HilbertCell2D extends Node2D{

    int hilbertValue;
    
    public HilbertCell2D(Tree tree, Node2D parent, Point2D centroid, int hilbertValue, List<Shape> entries) {
        super(tree, parent, null, entries);
        setUserProperty("centroid", centroid);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected void calculateBounds() {
        if(getEntries().isEmpty()){
            final Point2D centroid = (Point2D)getUserProperty("centroid");
            super.boundary = new Rectangle2D.Double(centroid.getX(), centroid.getY(), 0, 0);
        }else{
            super.calculateBounds();
        }
    }
    
    /**
     * @return centroid from this cell.
     */
    public Point2D getCentroid(){
        return (Point2D)getUserProperty("centroid");
    }
    
    /**Update this cell centroid.
     * @param centroid 
     */
    public void setCentroid(final Point2D centroid){
        setUserProperty("centroid", centroid);
    }
}
