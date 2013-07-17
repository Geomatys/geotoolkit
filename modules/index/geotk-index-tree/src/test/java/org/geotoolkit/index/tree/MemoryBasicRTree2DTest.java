/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.io.IOException;
import org.geotoolkit.index.tree.basic.AbstractBasicRTree;
import org.geotoolkit.index.tree.basic.MemoryBasicRTree;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.io.AbstractTreeTest;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;

/**
 *
 * @author rmarechal
 */
public class MemoryBasicRTree2DTest extends AbstractTreeTest {

    public MemoryBasicRTree2DTest() throws StoreIndexException, IOException {
        super(DefaultEngineeringCRS.CARTESIAN_2D);
        tree = new MemoryBasicRTree(3, crs, SplitCase.LINEAR, tEM);
        tAF  = ((AbstractBasicRTree)tree).getTreeAccess();
    }
}
