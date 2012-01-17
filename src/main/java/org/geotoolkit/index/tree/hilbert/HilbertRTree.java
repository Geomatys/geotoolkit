/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hilbeRTree;

import com.mycompany.utilsRTree.Entry;
import com.mycompany.utilsRTree.Node;
import com.mycompany.utilsRTree.Rtree;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.util.List;
import org.geotoolkit.util.ArgumentChecks;

/**Create Hilbert RTree.
 *
 * @author Rémi Maréchal (Geomatys).
 */
public class HilbertRTree extends Rtree{

    int hilbertOrder;
    
    /**Create Hilbert RTree.
     * 
     * @param maxElements max elements number autorized
     * @param hilbertOrder max order value.
     * @throws IllegalArgumentException if maxElements <= 0.
     * @throws IllegalArgumentException if hilbertOrder <= 0. 
     */
    public HilbertRTree( int maxElements, int hilbertOrder) {
        ArgumentChecks.ensureStrictlyPositive("max elements parameter too small", maxElements);
        ArgumentChecks.ensureStrictlyPositive("impossible to create Hilbert Rtree with order <= 0", hilbertOrder);
        this.maxElements = maxElements;
        this.hilbertOrder = hilbertOrder;
        this.treeTrunk = null;
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public void search(Rectangle2D regionSearch, List<Entry> result) {
        treeTrunk.search(regionSearch, result);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void insert(Entry entry) {
        if(treeTrunk == null){
            treeTrunk = new HilbertNode(this, 0, entry.getBoundary(), true);
        }
            treeTrunk.insert(entry);
    }

    /**
     * {@inheritDoc}. 
     */
    @Override
    public void delete(Entry entry) {
        treeTrunk.delete(entry);
    }

    /**
     * {@inheritDoc}. 
     */
    @Override
    public int getMaxElements() {
        return maxElements;
    }

    /**
     * {@inheritDoc}. 
     */
    @Override
    public void paint(Graphics2D g) {
        treeTrunk.paint(g);
    }
    
    /**
     * @return Max Hilbert order value. 
     */
    public int getHilbertOrder(){
        return hilbertOrder;
    }

    /**
     * {@inheritDoc}. 
     */
    @Override
    public String toString() {
        return treeTrunk.toString();
    }
    public Node getTreeTrunk(){
        return treeTrunk;
    }
}
