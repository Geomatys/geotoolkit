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
import java.util.List;

import org.geotoolkit.index.tree.AbstractTreeTest;
import org.geotoolkit.index.tree.FileTreeElementMapperTest;
import org.geotoolkit.index.tree.StoreIndexException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import static org.junit.Assert.assertTrue;

/**
 * Create a generic BasicRTree Test suite where Tree is store on hard drive.<br/>
 * Test is effectuate with file already filled by tree architecture.
 *
 * @author Remi Marechal (Geomatys).
 */
abstract class ReadeableBasicRTreeTest extends AbstractTreeTest {

    /**
     * Create a generic BasicRTree Test suite with file already filled by tree architecture
     * and a {@link CoordinateReferenceSystem} define by user.
     *
     * @param crs
     * @throws IOException if problem during head reading from already filled file.
     * @throws StoreIndexException if file isn't already filled by {@link BasicRTree} implementation.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    protected ReadeableBasicRTreeTest(final CoordinateReferenceSystem crs) throws StoreIndexException, IOException, ClassNotFoundException {
        super(crs);
        final File inOutFile = File.createTempFile("test", "tree", tempDir);
        final File treeMapperFile = File.createTempFile("test", "mapper", tempDir);
        tEM  = new FileTreeElementMapperTest(crs, treeMapperFile);
        tree = new FileBasicRTree(inOutFile.toPath(), 3, crs, SplitCase.LINEAR, tEM);

        insert();
        tree.close();
        tEM.close();
        assertTrue(tree.isClosed());
        assertTrue(tEM.isClosed());
        tEM  = new FileTreeElementMapperTest(crs, treeMapperFile);
        tree = new FileBasicRTree(inOutFile.toPath(), tEM);
    }
}
