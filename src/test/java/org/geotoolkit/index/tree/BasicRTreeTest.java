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
public class BasicRTreeTest extends TreeTest {

    public BasicRTreeTest() {
        super(new BasicRTree(4, SplitCase.QUADRATIC));
    }

    /**
     * Some elements inserted in Hilbert R-Tree.
     */
    public void testInsert() {
        super.insertTest();
    }

    public void testCheckBoundary(){
        super.checkBoundaryTest();
    }
    
    /**
     * Test search query inside tree.
     */
    public void testQueryInside() {
        super.queryInsideTest();
    }

    /**
     * Test query outside of tree area.
     */
    public void testQueryOutside() {
        super.queryOutsideTest();
    }

    /**
     * Test query on tree boundary border. 
     */
    public void testQueryOnBorder() {
        super.queryOnBorderTest();
    }

    /**
     * Test query with search area contain all tree boundary. 
     */
    public void testQueryAll() {
        super.queryAllTest();
    }

    /**
     * Test insertion and deletion in tree.
     */
    public void testInsertDelete() {
//        for (int i = 0; i < 20; i++) {
            super.insertDelete();
//        }
    }
}
