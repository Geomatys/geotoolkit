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
import org.geotoolkit.index.tree.FileTreeElementMapperTest;
import org.geotoolkit.index.tree.StoreIndexException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Create a generic BasicRTree Test suite where Tree is store on hard drive.
 *
 * @author Remi Marechal (Geomatys).
 */
abstract class WritableBasicRTreeTest extends AbstractTreeTest {

    /**
     * Create a generic BasicRTree Test suite, stored on File,  with {@link CoordinateReferenceSystem} define by user.
     * 
     * @param crs
     * @throws StoreIndexException during Tree creation.
     * @throws IOException if problem during TreeElementMapper or Tree head writing.
     */
    protected WritableBasicRTreeTest(final CoordinateReferenceSystem crs) throws StoreIndexException, IOException {
        super(crs);
        final File inOutFile = File.createTempFile("tree", "test", tempDir);
        final File treeMapperFile = File.createTempFile("mapper", "test", tempDir);
        tEM  = new FileTreeElementMapperTest(crs, treeMapperFile);
        tree = new FileBasicRTree(inOutFile, 4, crs, SplitCase.LINEAR, tEM);
    }


}
