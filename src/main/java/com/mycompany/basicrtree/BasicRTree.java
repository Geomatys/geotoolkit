/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.basicrtree;

import com.mycompany.utilsRTree.Entry;
import com.mycompany.utilsRTree.Rtree;
import com.mycompany.utilsRTree.SplitCase;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**Create R-Tree (Basic)
 *
 * @author Rémi Marechal (Geomatys)
 */
public class BasicRTree extends Rtree{

    private SplitCase choice;
    
    /**Create R-Tree.
     * 
     * @param maxElements max value of elements per tree cell.
     * @param choice Split made "linear" or "quadratic".
     */
    public BasicRTree(int maxElements, SplitCase choice) {
        this.choice = choice;
        this.maxElements = maxElements;
        this.treeTrunk = new BasicBranch(this);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void insert(Entry entry) {
        if(((BasicBranch)treeTrunk).getNodes().isEmpty()){
            BasicLeaf bL = new BasicLeaf(this, entry);
            treeTrunk = new BasicBranch(this, bL);
            bL.setParent(treeTrunk);
        }else{
            treeTrunk.insert(entry);
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void delete(Entry entry) {
        treeTrunk.delete(entry);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void search(Rectangle2D regionSearch, List<Entry> result) {
        treeTrunk.search(regionSearch, result);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public int getMaxElements() {
        return this.maxElements;
    }

    /**
     * @return splitcase choosen to split. 
     */
    public SplitCase getSplitCase() {
        return this.choice;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void paint(Graphics2D g) {
        treeTrunk.paint(g);
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public String toString() {
        return treeTrunk.toString();
    }
}
