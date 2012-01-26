/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import org.geotoolkit.index.tree.star.StarRTree;

/**
 *
 * @author rmarech
 */
public class StarRTreeTest extends TreeTest{
    public StarRTreeTest() {
        super(new StarRTree(4));
    }
    
    public void testInsert(){
        super.insertTest();
    } 
    
    public void testQueryInside(){
        super.queryInsideTest();
    }
    
    public void testQueryOutside(){
        super.queryOutsideTest();
    }
    
    public void testQueryOnBorder(){
        super.queryOnBorderTest();
    }
    
    public void testQueryAll(){
        super.queryAllTest();
    }
    
    public void testInsertDelete(){
        super.insertDelete();
    }
}
