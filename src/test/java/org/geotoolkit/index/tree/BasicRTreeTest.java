/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import org.geotoolkit.index.tree.basic.BasicRTree;
import org.geotoolkit.index.tree.basic.SplitCase;

/**
 * Create (Basic) R-Tree test suite.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class BasicRTreeTest extends TreeTest{

    public BasicRTreeTest() {
        super(new BasicRTree(4, SplitCase.QUADRATIC));
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
