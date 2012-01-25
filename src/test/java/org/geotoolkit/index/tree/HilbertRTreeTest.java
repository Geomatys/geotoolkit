/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import org.geotoolkit.index.tree.hilbert.HilbertRTree;

/**
 *
 * @author rmarech
 */
public class HilbertRTreeTest extends TreeTest{
    public HilbertRTreeTest() {
        super(new HilbertRTree(4, 2));
    }
    
    public void testInsert(){
        super.insertTest();
    } 
}
