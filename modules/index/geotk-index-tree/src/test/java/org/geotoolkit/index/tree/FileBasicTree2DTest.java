/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree;

import java.io.File;
import java.io.IOException;
import org.geotoolkit.index.tree.basic.AbstractBasicRTree;
import org.geotoolkit.index.tree.basic.FileBasicRTree;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.io.AbstractTreeTest;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;

/**
 *
 * @author rmarechal
 */
public class FileBasicTree2DTest extends AbstractTreeTest {

    public FileBasicTree2DTest() throws StoreIndexException, IOException {
        super(DefaultEngineeringCRS.CARTESIAN_2D);
//        tree = new FileBasicRTree(File.createTempFile("test", "tree"), 3, crs, SplitCase.LINEAR, tEM);
        tree = new FileBasicRTree(new File("/home/rmarechal/Documents/tree.test"), 3, crs, SplitCase.LINEAR, tEM);
        tAF  = ((AbstractBasicRTree)tree).getTreeAccess();
    }
    
}
