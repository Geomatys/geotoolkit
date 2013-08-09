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
package org.geotoolkit.index.tree.hilbert;

import java.io.File;
import java.io.IOException;
import org.geotoolkit.index.tree.AbstractTreeTest;
import org.geotoolkit.index.tree.FileTreeElementMapperTest;
import org.geotoolkit.index.tree.StoreIndexException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Create a generic HilbertRTree Test suite where Tree is store on hard drive.<br/>
 * Test is effectuate with file already filled by tree architecture.
 *
 * @author Remi Marechal (Geomatys).
 */
abstract class ReadeableHilbertRTreeTest extends AbstractTreeTest {

    /**
     * Create a generic HilbertRTree Test suite with file already filled by tree architecture
     * and a {@link CoordinateReferenceSystem} define by user.
     * 
     * @param crs
     * @throws IOException if problem during head reading from already filled file.
     * @throws StoreIndexException if file isn't already filled by {@link HilbertRTree} implementation.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
    protected ReadeableHilbertRTreeTest(final CoordinateReferenceSystem crs) throws StoreIndexException, IOException, ClassNotFoundException {
        super(crs);
        final File inOutFile = File.createTempFile("test", "tree");
        final File treeMapperFile = File.createTempFile("test", "mapper");
        tEM = new FileTreeElementMapperTest(crs, treeMapperFile);
        tree = new FileHilbertRTree(inOutFile, 4, 2, crs, tEM);
        
        insert();
        tree.close();
        tEM.close();
        
        tEM = new FileTreeElementMapperTest(treeMapperFile, crs);
        tree = new FileHilbertRTree(inOutFile, tEM);
    }
}
