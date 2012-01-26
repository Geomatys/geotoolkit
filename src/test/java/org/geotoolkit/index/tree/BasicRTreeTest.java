/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import junit.framework.*;
import org.geotoolkit.index.tree.basic.BasicRTree;
import org.geotoolkit.index.tree.basic.SplitCase;

/**
 *
 * @author rmarech
 */
public class BasicRTreeTest extends TreeTest{

    public BasicRTreeTest() {
        super(new BasicRTree(4, SplitCase.LINEAR));
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
