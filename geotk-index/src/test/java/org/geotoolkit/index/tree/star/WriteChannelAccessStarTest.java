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
package org.geotoolkit.index.tree.star;

import java.io.File;
import java.io.IOException;
import org.geotoolkit.index.tree.AbstractTreeTest;
import org.geotoolkit.index.tree.FileTreeElementMapperTest;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.internal.tree.TreeAccessByteArray;
import org.geotoolkit.internal.tree.TreeUtilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Create a generic StarRTree Test suite where Tree architecture is stored into byte array.<br/>
 *
 * @author Remi Marechal (Geomatys).
 * @see TreeAccessByteArray
 */
abstract class WriteChannelAccessStarTest extends AbstractTreeTest {

    /**
     * Create a generic StarRTree Test suite where Tree is stored into byte array.
     *
     * @author Remi Marechal (Geomatys).
     * @param crs
     * @throws StoreIndexException
     * @throws IOException
     */
    protected WriteChannelAccessStarTest(final CoordinateReferenceSystem crs)
            throws StoreIndexException, IOException {
        super(crs);
        final File treeMapperFile = File.createTempFile("mapper", "test", tempDir);
        tEM  = new FileTreeElementMapperTest(crs, treeMapperFile);
        tree = new StarRTree<double[]>(new TreeAccessByteArray(TreeUtilities.STAR_NUMBER, TreeUtilities.VERSION_NUMBER, 4, crs), tEM);
    }
}
