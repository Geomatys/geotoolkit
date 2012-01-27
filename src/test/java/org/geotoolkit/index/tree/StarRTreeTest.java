/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import org.geotoolkit.index.tree.star.StarRTree;

/**
 * Create R*Tree test suite.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class StarRTreeTest extends TreeTest{
    public StarRTreeTest() {
        super(new StarRTree(4));
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
        super.insertDelete();
    }
}
