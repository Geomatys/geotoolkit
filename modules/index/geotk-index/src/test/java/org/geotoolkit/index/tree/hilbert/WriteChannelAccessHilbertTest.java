/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.index.tree.hilbert;

import java.io.File;
import java.io.IOException;
import org.geotoolkit.index.tree.AbstractTreeTest;
import org.geotoolkit.index.tree.FileTreeElementMapperTest;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.internal.tree.TreeUtilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author rmarechal
 */
abstract class WriteChannelAccessHilbertTest extends HilbertTest {
    protected WriteChannelAccessHilbertTest(final CoordinateReferenceSystem crs) throws StoreIndexException, IOException {
        super(crs);
//        inOutFile      = File.createTempFile("starRTree", "tree", tempDir);
        final File treeMapperFile = File.createTempFile("mapper", "test", tempDir);
        tEM = new FileTreeElementMapperTest(crs, treeMapperFile);
        tree = new HilbertRTree<double[]>(new HilbertAccessByteArray(TreeUtilities.HILBERT_NUMBER, TreeUtilities.VERSION_NUMBER, 4, 2, crs), tEM);
    }
}
