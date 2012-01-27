/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import org.geotoolkit.index.tree.hilbert.HilbertRTree;

/**
 * Create Hilbert R-Tree test suite.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class HilbertRTreeTest extends TreeTest{
    
    public HilbertRTreeTest() {
        super(new HilbertRTree(4, 2));
    }
    
    /**
     * {@inheritDoc}
     */
    public void testInsert(){
        super.insertTest();
    } 
    
    /**
     * {@inheritDoc}
     */
    public void testQueryInside(){
        super.queryInsideTest();
    }
    
    /**
     * {@inheritDoc}
     */
    public void testQueryOutside(){
        super.queryOutsideTest();
    }
    
    /**
     * {@inheritDoc}
     */
    public void testQueryOnBorder(){
        super.queryOnBorderTest();
    }
    
    /**
     * {@inheritDoc}
     */
    public void testQueryAll(){
        super.queryAllTest();
    }
    
    /**
     * {@inheritDoc}
     */
    public void testInsertDelete(){
        for(int i = 0; i<20; i++){
            super.insertDelete();
        }
        
    }
}
