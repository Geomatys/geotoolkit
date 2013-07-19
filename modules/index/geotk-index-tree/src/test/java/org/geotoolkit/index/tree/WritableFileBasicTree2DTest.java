/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.index.tree;

import java.io.File;
import java.io.IOException;
import org.geotoolkit.index.tree.basic.AbstractBasicRTree;
import org.geotoolkit.index.tree.basic.FileBasicRTree;
import org.geotoolkit.index.tree.basic.SplitCase;
import org.geotoolkit.index.tree.io.AbstractTreeTest;
import org.geotoolkit.index.tree.io.FileTreeElementMapperTest;
import org.geotoolkit.index.tree.io.StoreIndexException;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;

/**
 *
 * @author rmarechal
 */
public class WritableFileBasicTree2DTest extends AbstractTreeTest {

    
    public WritableFileBasicTree2DTest() throws StoreIndexException, IOException {
        super(DefaultEngineeringCRS.CARTESIAN_2D);
        tEM = new FileTreeElementMapperTest(crs, File.createTempFile("test", "mapper"));
        tree = new FileBasicRTree(File.createTempFile("test", "tree"), 3, crs, SplitCase.LINEAR, tEM);
//        tree = new FileBasicRTree(File.createTempFile("test", "tree"), 3, crs, 
//                SplitCase.LINEAR, new FileTreeElementMapperTest(crs, File.createTempFile("test", "mapper")));
        tAF  = ((AbstractBasicRTree)tree).getTreeAccess();
    }
    
}
