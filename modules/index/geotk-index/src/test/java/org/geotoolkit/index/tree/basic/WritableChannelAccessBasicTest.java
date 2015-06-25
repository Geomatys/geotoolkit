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
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author rmarechal
 */
abstract class WritableChannelAccessBasicTest extends AbstractTreeTest {
    WritableChannelAccessBasicTest(final CoordinateReferenceSystem crs) throws IOException, StoreIndexException{
         super(crs);
        final File treeMapperFile = File.createTempFile("mapper", "test", tempDir);
        tEM  = new FileTreeElementMapperTest(crs, treeMapperFile);
        tree = new BasicRTree<double[]>(new TreeAccessByteArray(TreeUtilities.STAR_NUMBER, TreeUtilities.VERSION_NUMBER, 4,SplitCase.LINEAR, crs), tEM);
    }
}
