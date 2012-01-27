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
public class StarRTreeTest extends TreeTest {

    public StarRTreeTest() {
        super(new StarRTree(4));
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
