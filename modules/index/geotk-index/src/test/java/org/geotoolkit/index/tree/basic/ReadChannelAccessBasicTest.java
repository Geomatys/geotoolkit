/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.index.tree.basic;

import java.io.File;
import java.io.IOException;
import org.geotoolkit.index.tree.AbstractTreeTest;
import org.geotoolkit.index.tree.FileTreeElementMapperTest;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.internal.tree.TreeAccessByteArray;
import org.geotoolkit.internal.tree.TreeUtilities;
import static org.junit.Assert.assertTrue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author rmarechal
 */
abstract class ReadChannelAccessBasicTest extends AbstractTreeTest {

    ReadChannelAccessBasicTest(final CoordinateReferenceSystem crs, final boolean insert) throws IOException, StoreIndexException, ClassNotFoundException {
        super(crs);
        
        final File treeMapperFile = File.createTempFile("test", "mapper", tempDir);
        
        // data insertion
        tEM  = new FileTreeElementMapperTest(crs, treeMapperFile);
        final TreeAccessByteArray ta = new TreeAccessByteArray(TreeUtilities.BASIC_NUMBER, TreeUtilities.VERSION_NUMBER, 4, SplitCase.QUADRATIC, crs);
        tree = new BasicRTree<double[]>(ta, tEM);
        
        // close 
        if (insert) insert();
        tree.close();
        tEM.close();
        assertTrue(tree.isClosed());
        assertTrue(tEM.isClosed());
        
        final byte[] data = ta.getData();
        
        // open Tree from already filled files.
        tEM  = new FileTreeElementMapperTest(treeMapperFile, crs);
        tree = new BasicRTree<>(new TreeAccessByteArray(data, TreeUtilities.BASIC_NUMBER, TreeUtilities.VERSION_NUMBER), tEM);
    }
    
}
