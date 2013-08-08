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
package org.geotoolkit.index.tree.basic;

import java.io.File;
import java.io.IOException;
import org.geotoolkit.index.tree.AbstractTreeTest;
import org.geotoolkit.index.tree.FileTreeElementMapper;
import org.geotoolkit.index.tree.FileTreeElementMapperTest;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.index.tree.TreeElementMapperTest;
import org.geotoolkit.referencing.crs.DefaultEngineeringCRS;

/**
 *
 * @author rmarechal
 */
public class ReadeableFileBasicTree2DTest extends AbstractTreeTest {
    
    /**
     * 
     * @throws StoreIndexException
     * @throws IOException
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    public ReadeableFileBasicTree2DTest() throws StoreIndexException, IOException, ClassNotFoundException {
        super(DefaultEngineeringCRS.CARTESIAN_2D);
        final File inOutFile = File.createTempFile("test", "tree");
        final File treeMapperFile = File.createTempFile("test", "mapper");
        tEM = new FileTreeElementMapperTest(crs, treeMapperFile);
        tree = new FileBasicRTree(inOutFile, 3, crs, SplitCase.LINEAR, tEM);
        tAF  = ((BasicRTree)tree).getTreeAccess();
        
        insert();
        tree.close();
        ((FileTreeElementMapper)tEM).close();
        tEM = new FileTreeElementMapperTest(treeMapperFile, crs);
        tree = new FileBasicRTree(inOutFile, tEM);
        tAF  = ((BasicRTree)tree).getTreeAccess();
    }
}
