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
 * Create a generic BasicRTree Test suite where Tree is store into byte array.<br/>
 * Test is effectuate with byte array already filled by tree architecture.
 *
 * @author Remi Marechal (Geomatys).
 * @see TreeAccessByteArray
 */
abstract class ReadChannelAccessBasicTest extends AbstractTreeTest {

    /**
     * Create a generic BasicRTree Test suite with {@link TreeAccess} already filled by tree architecture
     * and a {@link CoordinateReferenceSystem} define by user.
     * 
     * @param crs
     * @param insert {@code true} to insert data into tree during test constructor else no insertion.
     * @throws IOException if problem during head reading from already filled file.
     * @throws StoreIndexException should never append.
     * @throws ClassNotFoundException if there is a problem during {@link CoordinateReferenceSystem} invert serialization.
     */
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
